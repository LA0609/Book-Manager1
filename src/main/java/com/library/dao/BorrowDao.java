package com.library.dao;

import com.library.model.BorrowRecord;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【数据层】BorrowDao（借阅数据访问对象）
 *
 * 作用：负责 borrow_records 表的所有数据库操作，包括借书、还书、查询等。
 * 简单来说，这个类是"借还业务与数据库之间的桥梁"。
 *
 * 核心业务规则：
 * - 每位读者最多同时借阅 5 本图书
 * - 逾期超过 30 天的读者不允许再借
 * - 逾期罚款：每天 0.5 元，上限 50 元
 *
 * 技术要点：
 * - 借书/还书操作使用事务（Transaction）保证数据一致性
 * - 查询使用 LEFT JOIN 关联 books 和 readers 表，一次拿到书名和读者姓名
 *
 * @author LA
 */
public class BorrowDao {

    /**
     * 查询所有借阅记录（重载方法，无状态筛选）
     *
     * @param keyword 搜索关键词（可为空，为空时查询全部）
     * @return 借阅记录列表
     */
    public List<BorrowRecord> findAll(String keyword) {
        return findAll(keyword, null);
    }

    /**
     * 查询借阅记录（支持关键词搜索 + 状态筛选）
     * 简单来说，这是借阅列表的"万能查询方法"，既能搜索又能按状态过滤。
     *
     * 实现原理：
     * 1. 使用 LEFT JOIN 关联三张表（borrow_records、books、readers）
     * 2. 通过 WHERE 1=1 占位，后续条件用 AND 拼接（便于动态拼接SQL）
     * 3. 关键词模糊匹配书名、读者姓名，或精确匹配记录ID
     * 4. 状态筛选将中文"借出中/已归还"映射为数据库英文"borrowing/returned"
     *
     * @param keyword      搜索关键词（书名/读者名/记录ID），可为空
     * @param statusFilter 状态筛选（"全部"/"借出中"/"已归还"），可为空
     * @return 符合条件的借阅记录列表
     */
    public List<BorrowRecord> findAll(String keyword, String statusFilter) {
        List<BorrowRecord> list = new ArrayList<>();
        // 联表查询：借阅记录 LEFT JOIN 图书表 LEFT JOIN 读者表
        String sql = "SELECT br.id, bk.name AS book_name, rd.name AS reader_name, "
                   + "br.borrow_date, br.return_date, br.status, br.fine, br.book_id, br.reader_id "
                   + "FROM borrow_records br "
                   + "LEFT JOIN books bk ON br.book_id = bk.id "
                   + "LEFT JOIN readers rd ON br.reader_id = rd.id "
                   + "WHERE 1=1";

        // 动态参数列表，根据是否有搜索条件决定是否添加
        List<Object> params = new ArrayList<>();

        // 拼接关键词搜索条件（模糊匹配书名/读者名，精确匹配记录ID）
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (bk.name LIKE ? OR rd.name LIKE ? OR br.id = ?)";
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            try {
                // 尝试将关键词转为整数，用于精确匹配记录ID
                params.add(Integer.parseInt(keyword));
            } catch (NumberFormatException e) {
                // 非数字时填入不可能的ID值，避免SQL参数类型错误
                params.add(-1);
            }
        }

        // 拼接状态筛选条件
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"全部".equals(statusFilter)) {
            // 将界面中文状态映射为数据库存储的英文状态
            String dbStatus = statusFilter;
            if ("借出中".equals(statusFilter)) {
                dbStatus = "borrowing";
            } else if ("已归还".equals(statusFilter)) {
                dbStatus = "returned";
            }
            sql += " AND br.status = ?";
            params.add(dbStatus);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 循环设置动态参数（区分 Integer 和 String 类型）
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else {
                    pstmt.setString(i + 1, (String) param);
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
     * 借阅数量限制检查
     * 简单来说，检查某位读者当前已借了几本书，是否还能继续借。
     * 规则：每人最多同时借阅 5 本。
     *
     * @param readerId 读者ID
     * @return true=未达上限，可以继续借；false=已达上限（5本），不能再借
     */
    public boolean checkLimit(int readerId) {
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE reader_id = ? AND status = 'borrowing'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) >= 5) {
                return false; // 已借满5本
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // 未达上限
    }

    /**
     * 逾期检查
     * 简单来说，检查某位读者是否有超过30天未归还的书。如果有，就不允许再借新书。
     *
     * 实现原理：使用 MySQL 的 DATEDIFF 函数计算当前日期与借出日期的天数差。
     *
     * @param readerId 读者ID
     * @return true=有逾期记录，不允许借书；false=无逾期，可以借书
     */
    public boolean checkOverdue(int readerId) {
        // 将 VARCHAR 类型的 borrow_date 转为 DATE 进行日期计算
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE reader_id = ? AND status = 'borrowing' "
                   + "AND DATEDIFF(NOW(), CAST(borrow_date AS DATE)) > 30";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            pstmt.setInt(1, readerId);
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // 存在逾期
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 无逾期
    }

    /**
     * 借书操作（事务）
     * 简单来说，完成一次借书需要同时做两件事：① 插入借阅记录 ② 减少图书可借量。
     * 这两件事必须"要么都成功，要么都失败"，所以用事务保证数据一致性。
     *
     * 事务流程：
     * 1. 关闭自动提交（开启事务）
     * 2. 校验图书是否存在且有库存
     * 3. 插入一条借阅记录
     * 4. 将图书可借量 -1
     * 5. 提交事务（如果任何步骤失败则回滚）
     *
     * @param bookId     图书ID
     * @param readerId   读者ID
     * @param borrowDate 借出日期（格式 yyyy-MM-dd）
     * @param returnDate 应还日期（格式 yyyy-MM-dd）
     * @return true=借阅成功；false=借阅失败（库存不足/读者异常等）
     */
    public boolean borrowBook(int bookId, int readerId, String borrowDate, String returnDate) {
        Connection conn = null;
        // 收集所有 Statement，确保 finally 中统一关闭
        java.util.List<Statement> stmts = new java.util.ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 关闭自动提交，开启事务

            // 1. 检查图书是否有库存
            String checkSql = "SELECT current_count FROM books WHERE id = ? AND is_delete = 0 FOR UPDATE";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            stmts.add(checkStmt);
            checkStmt.setInt(1, bookId);

            boolean canBorrow = false;
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("current_count") > 0) {
                    canBorrow = true;
                }
            }

            if (canBorrow) {
                // 2. 插入借阅记录
                String insertSql = "INSERT INTO borrow_records (book_id, reader_id, borrow_date, return_date, status, fine) "
                                 + "VALUES (?, ?, ?, ?, 'borrowing', 0)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                stmts.add(insertStmt);
                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, readerId);
                insertStmt.setString(3, borrowDate);
                insertStmt.setString(4, returnDate);
                insertStmt.executeUpdate();

                // 3. 图书可借量 -1
                String updateSql = "UPDATE books SET current_count = current_count - 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                stmts.add(updateStmt);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                conn.commit(); // 提交事务，两步操作同时生效
                return true;
            } else {
                conn.rollback(); // 库存不足，回滚事务
            }
        } catch (SQLException e) {
            // 发生异常时回滚，保证数据一致性
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            // 统一关闭所有 Statement（关闭 Statement 会级联关闭其 ResultSet）
            for (Statement st : stmts) {
                try { st.close(); } catch (SQLException ignored) {}
            }
            // 恢复自动提交模式
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    /**
     * 还书操作（事务 + 逾期罚款计算）
     * 简单来说，完成一次还书需要同时做三件事：① 更新借阅状态为"已归还" ② 计算逾期罚款 ③ 增加图书可借量。
     *
     * 罚款规则：
     * - 超过应还日期每天罚款 0.5 元
     * - 罚款金额上限为 50 元
     * - 未逾期则罚款为 0
     *
     * @param recordId 借阅记录ID
     * @return 实际罚款金额（0 表示无罚款）；-1 表示归还失败（记录不存在或已归还）
     */
    public double returnBook(int recordId) {
        Connection conn = null;
        java.util.List<Statement> stmts = new java.util.ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 查询借阅记录，确认处于"借出中"状态
            PreparedStatement findStmt = conn.prepareStatement(
                "SELECT book_id, return_date FROM borrow_records WHERE id = ? AND status = 'borrowing'");
            stmts.add(findStmt);
            findStmt.setInt(1, recordId);

            int bookId = -1;
            double fine = 0.0;

            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                    String returnDate = rs.getString("return_date");

                    // 2. 计算逾期罚款
                    if (returnDate != null) {
                        java.time.LocalDate expectedReturn = java.time.LocalDate.parse(returnDate);
                        java.time.LocalDate today = java.time.LocalDate.now();
                        if (today.isAfter(expectedReturn)) {
                            // 计算逾期天数：当前日期 - 应还日期
                            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(expectedReturn, today);
                            fine = overdueDays * 0.5; // 每天 0.5 元
                            if (fine > 50) fine = 50.0; // 上限 50 元
                        }
                    }
                } else {
                    // 记录不存在或已归还，回滚并返回 -1
                    conn.rollback();
                    return -1;
                }
            }

            // 3. 更新借阅记录：状态改为"已归还"，写入罚款金额，更新实际归还日期
            PreparedStatement updateRecord = conn.prepareStatement(
                "UPDATE borrow_records SET status = 'returned', fine = ?, return_date = ? WHERE id = ?");
            stmts.add(updateRecord);
            updateRecord.setDouble(1, fine);
            updateRecord.setString(2, java.time.LocalDate.now().toString()); // 实际归还日期 = 今天
            updateRecord.setInt(3, recordId);
            updateRecord.executeUpdate();

            // 4. 图书可借量 +1
            PreparedStatement updateBook = conn.prepareStatement(
                "UPDATE books SET current_count = current_count + 1 WHERE id = ?");
            stmts.add(updateBook);
            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            conn.commit(); // 提交事务
            return fine;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            for (Statement st : stmts) {
                try { st.close(); } catch (SQLException ignored) {}
            }
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return -1;
    }
}
