package com.library.dao;

import com.library.model.BorrowRecord;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDao {

    /**
     * Joint query + fuzzy search (now includes fine)
     */
    public List<BorrowRecord> findAll(String keyword) {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT br.id, bk.name AS book_name, rd.name AS reader_name, "
                   + "br.borrow_date, br.return_date, br.status, br.fine, br.book_id, br.reader_id "
                   + "FROM borrow_records br "
                   + "LEFT JOIN books bk ON br.book_id = bk.id "
                   + "LEFT JOIN readers rd ON br.reader_id = rd.id "
                   + "WHERE 1=1";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (bk.name LIKE ? OR rd.name LIKE ? OR br.id = ?)";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                try {
                    pstmt.setInt(3, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    pstmt.setInt(3, -1);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord r = new BorrowRecord();
                    r.setId(rs.getInt("id"));
                    r.setBookId(rs.getInt("book_id"));
                    r.setReaderId(rs.getInt("reader_id"));
                    r.setBookName(rs.getString("book_name"));
                    r.setReaderName(rs.getString("reader_name"));
                    r.setBorrowDate(rs.getString("borrow_date"));
                    r.setReturnDate(rs.getString("return_date"));
                    r.setStatus(rs.getString("status"));
                    r.setFine(rs.getDouble("fine"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Borrow limit check (max 5 books per reader)
     */
    public boolean checkLimit(int readerId) {
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE reader_id = ? AND status = 'borrowing'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) >= 5) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Overdue check (borrowed for more than 30 days without return)
     */
    public boolean checkOverdue(int readerId) {
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE reader_id = ? AND status = 'borrowing' "
                   + "AND (julianday('now') - julianday(borrow_date)) > 30";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Borrow book (transaction)
     */
    public boolean borrowBook(int bookId, int readerId, String borrowDate, String returnDate) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Check reader status first
            PreparedStatement readerStmt = conn.prepareStatement("SELECT status FROM readers WHERE id = ?");
            readerStmt.setInt(1, readerId);
            ResultSet readerRs = readerStmt.executeQuery();
            if (!readerRs.next() || !"正常".equals(readerRs.getString("status"))) {
                return false;
            }

            // Check book availability
            PreparedStatement checkStmt = conn.prepareStatement("SELECT current_count FROM books WHERE id = ?");
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("current_count") > 0) {
                String insertSql = "INSERT INTO borrow_records (book_id, reader_id, borrow_date, return_date, status, fine) "
                                 + "VALUES (?, ?, ?, ?, 'borrowing', 0)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, readerId);
                insertStmt.setString(3, borrowDate);
                insertStmt.setString(4, returnDate);
                insertStmt.executeUpdate();

                String updateSql = "UPDATE books SET current_count = current_count - 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    /**
     * Return book (transaction) - with overdue fine calculation
     * Rule: 0.5 yuan per overdue day, max fine capped at 50 yuan
     */
    public double returnBook(int recordId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement findStmt = conn.prepareStatement(
                "SELECT book_id, return_date FROM borrow_records WHERE id = ? AND status = 'borrowing'");
            findStmt.setInt(1, recordId);
            ResultSet rs = findStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");
                String returnDate = rs.getString("return_date");

                // Calculate overdue fine
                double fine = 0.0;
                if (returnDate != null) {
                    java.time.LocalDate expectedReturn = java.time.LocalDate.parse(returnDate);
                    java.time.LocalDate today = java.time.LocalDate.now();
                    if (today.isAfter(expectedReturn)) {
                        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(expectedReturn, today);
                        fine = overdueDays * 0.5;
                        if (fine > 50) fine = 50.0; // cap at 50 yuan
                    }
                }

                // Update record: set status to returned, save fine
                PreparedStatement updateRecord = conn.prepareStatement(
                    "UPDATE borrow_records SET status = 'returned', fine = ? WHERE id = ?");
                updateRecord.setDouble(1, fine);
                updateRecord.setInt(2, recordId);
                updateRecord.executeUpdate();

                // Restore book inventory
                PreparedStatement updateBook = conn.prepareStatement(
                    "UPDATE books SET current_count = current_count + 1 WHERE id = ?");
                updateBook.setInt(1, bookId);
                updateBook.executeUpdate();

                conn.commit();
                return fine;
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return -1;
    }
}

