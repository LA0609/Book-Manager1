/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.dao;

import com.library.model.Book;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【数据层】BookDao (图书数据访问对象)
 * 负责 books 表的增删改查。
 */
public class BookDao {

    /**
     * 查询所有图书
     * 应用场景：图书管理界面展示列表。
     * @return 包含所有图书对象的列表
     */
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_delete = 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Book b = new Book();
                // 从数据库读取数据，封装进对象
                b.setId(rs.getInt("id"));
                b.setIsbn(rs.getString("isbn"));
                b.setName(rs.getString("name"));
                b.setAuthor(rs.getString("author"));
                b.setPublisher(rs.getString("publisher"));
                b.setTotalCount(rs.getInt("total_count")); // 注意下划线命名
                b.setCurrentCount(rs.getInt("current_count"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("查询图书失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 添加图书 (入库)
     * @param book 图书对象
     */
    public void add(Book book) {
        // INSERT INTO 表名 (列1, 列2...) VALUES (?, ?...)
        String sql = "INSERT INTO books (id,isbn, name, author, publisher, total_count, current_count, is_delete) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 依次设置参数
            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getIsbn());
            pstmt.setString(3, book.getName());
            pstmt.setString(4, book.getAuthor());
            pstmt.setString(5, book.getPublisher());
            pstmt.setInt(6, book.getTotalCount());
            pstmt.setInt(7, book.getCurrentCount()); // 初始在馆量通常等于总量
            
            pstmt.executeUpdate(); // 执行更新操作
            System.out.println("图书入库成功！");
        } catch (SQLException e) {
            System.err.println("入库失败：" + e.getMessage());
            e.printStackTrace();
        }
        
    }
/**
 * 模糊搜索图书
 * @param searchField
 * @param keyword
 * @return 
 */
    public List<Book> search(String searchField, String keyword) {
        List<Book> list = new ArrayList<>();
        String sql;

        // 根据查询字段类型构建不同SQL
        switch (searchField) {
        case "书名":
            sql = "SELECT * FROM books WHERE name LIKE ? AND is_delete = 0";
            break;
        case "作者":
            sql = "SELECT * FROM books WHERE author LIKE ? AND is_delete = 0";
            break;
        case "出版社":
            sql = "SELECT * FROM books WHERE publisher LIKE ? AND is_delete = 0";
            break;
        case "可借阅":
            sql = "SELECT * FROM books WHERE current_count > 0 AND is_delete = 0";
            break;
        case "已借阅":
            sql = "SELECT * FROM books WHERE current_count = 0 AND total_count > 0 AND is_delete = 0";
            break;
        default:
            sql = "SELECT * FROM books WHERE name LIKE ? AND is_delete = 0";
            break;
    }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 状态筛选不需要参数，模糊查询需要设置关键词
            if (!searchField.equals("可借阅") && !searchField.equals("已借阅")) {
                pstmt.setString(1, "%" + keyword + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setIsbn(rs.getString("isbn"));
                    b.setName(rs.getString("name"));
                    b.setAuthor(rs.getString("author"));
                    b.setPublisher(rs.getString("publisher"));
                    b.setTotalCount(rs.getInt("total_count"));
                    b.setCurrentCount(rs.getInt("current_count"));
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
/**
 * 根据主键单条查询
 * @param id
 * @return 
 */
    public Book findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setIsbn(rs.getString("isbn"));
                    b.setName(rs.getString("name"));
                    b.setAuthor(rs.getString("author"));
                    b.setPublisher(rs.getString("publisher"));
                    b.setTotalCount(rs.getInt("total_count"));
                    b.setCurrentCount(rs.getInt("current_count"));
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("查询图书失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
/**
 * 修改图书信息
 * @param book 封装了完整新数据的图书对象
 */
    public void update(Book book) {
        String sql = "UPDATE books SET name=?, author=?, publisher=?, total_count=?, current_count=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getPublisher());
            pstmt.setInt(4, book.getTotalCount());
            pstmt.setInt(5, book.getCurrentCount());
            pstmt.setInt(6, book.getId());
            pstmt.executeUpdate();
            System.out.println("图书修改成功！id=" + book.getId());
        } catch (SQLException e) {
            System.err.println("修改图书失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据ID删除图书
     * @param id 图书ID
     */
    public void delete(int id) {
        String sql = "UPDATE books SET is_delete = 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        System.out.println("图书列表移除成功！id=" + id);
    } catch (SQLException e) {
        System.err.println("图书移除失败：" + e.getMessage());
        e.printStackTrace();
    }
    }
}