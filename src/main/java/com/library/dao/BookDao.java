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
 * 【数据层】BookDao（图书数据访问对象）
 *
 * 作用：专门负责 books 表的增删改查（CRUD）。
 * 简单来说，UI 层想操作图书数据，就通过这个类来跟数据库"对话"。
 *
 * 设计说明：
 * - 使用 PreparedStatement 防止 SQL 注入
 * - 使用 try-with-resources 自动关闭连接，防止资源泄漏
 * - 软删除设计：delete 方法只标记 is_delete=1，不物理删除数据
 *
 * @author LA
 */
public class BookDao {

    /**
     * 查询所有未删除的图书
     * 应用场景：图书管理界面打开时加载列表数据。
     *
     * @return 包含所有未删除图书对象的列表，无数据时返回空列表（不会返回 null）
     */
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_delete = 0";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Book b = new Book();
                // 逐列读取数据库字段，封装进 Book 对象
                b.setId(rs.getInt("id"));
                b.setIsbn(rs.getString("isbn"));
                b.setName(rs.getString("name"));
                b.setAuthor(rs.getString("author"));
                b.setPublisher(rs.getString("publisher"));
                b.setTotalCount(rs.getInt("total_count"));
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
     * 添加图书（入库）
     * 简单来说，就是把一本新书的信息写入数据库。
     *
     * @param book 图书对象，包含ISBN、书名、作者、出版社、库存等信息
     */
    public void add(Book book) {
        // INSERT INTO 表名 (列1, 列2...) VALUES (?, ?...)
        String sql = "INSERT INTO books (isbn, name, author, publisher, total_count, current_count, is_delete) "
             + "VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 依次设置 SQL 参数（id 由数据库自增生成，无需手动传）
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getName());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getTotalCount());
            pstmt.setInt(6, book.getCurrentCount()); // 新书初始可借量 = 总量

            pstmt.executeUpdate(); // 执行 INSERT
            System.out.println("图书入库成功！");
        } catch (SQLException e) {
            System.err.println("入库失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 模糊搜索图书
     * 简单来说，根据用户选择的搜索条件（书名/作者/出版社/可借阅状态）查找图书。
     *
     * @param searchField 搜索字段类型，如"书名"、"作者"、"出版社"、"可借阅"、"已借阅"
     * @param keyword     搜索关键词（模糊匹配用），状态筛选时可传空
     * @return 符合条件的图书列表，无结果时返回空列表
     */
    public List<Book> search(String searchField, String keyword) {
        List<Book> list = new ArrayList<>();
        String sql;

        // 根据搜索字段类型动态构建不同的 SQL 语句
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
            // 状态筛选（可借阅/已借阅）不需要设置参数，模糊查询需要设置关键词
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
     * 根据主键查询单本图书
     * 简单来说，通过图书ID精确查找一本书，常用于修改窗口的数据回显。
     *
     * @param id 图书ID（数据库主键）
     * @return 对应的 Book 对象，找不到时返回 null
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
     * 简单来说，把用户编辑后的新数据覆盖写回数据库。
     *
     * @param book 封装了完整新数据的图书对象（id 不能为0，用于 WHERE 条件定位）
     */
    public void update(Book book) {
        // 注意：参数顺序需与 SQL 占位符顺序严格对应
        String sql = "UPDATE books SET isbn=?, name=?, author=?, publisher=?, total_count=?, current_count=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getName());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getTotalCount());
            pstmt.setInt(6, book.getCurrentCount());
            pstmt.setInt(7, book.getId()); // WHERE 条件：按ID定位
            pstmt.executeUpdate();
            System.out.println("图书修改成功！id=" + book.getId());
        } catch (SQLException e) {
            System.err.println("修改图书失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 逻辑删除图书（软删除）
     * 简单来说，不是真从数据库删掉，而是标记 is_delete=1，查询时自动过滤。
     * 为什么要软删除？→ 保留历史借阅记录的完整性，避免数据关联断裂。
     *
     * @param id 要删除的图书ID
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
