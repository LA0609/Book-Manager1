package com.library.dao;

import com.library.model.User;
import com.library.util.DBUtil;
import java.sql.*;

/**
 * 【数据层】UserDao (用户数据访问对象)
 * 
 * 作用：专门负责 users 表的增删改查 (CRUD)。
 * 技术：使用 JDBC (PreparedStatement) 操作 MySQL。
 */
public class UserDao {

    /**
     * 根据用户名查找用户
     * 
     * 应用场景：登录验证。用户输入账号，去数据库里找有没有这个人。
     * 
     * @param username 传入的用户名
     * @return User对象 (如果找到)，null (如果没找到)
     */
    public User findByUsername(String username) {
        // SQL 语句：? 是占位符，稍后会被替换
        String sql = "SELECT * FROM users WHERE username = ?";
        
        // try-with-resources 语法：
        // 自动关闭 Connection 和 PreparedStatement，防止内存泄漏。
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 设置参数：第一个 ? 替换为 username
            pstmt.setString(1, username);
            
            // 执行查询，结果集保存在 rs 中
            try (ResultSet rs = pstmt.executeQuery()) {
                // rs.next() 判断是否有下一行数据
                if (rs.next()) {
                    // 如果找到了，把数据从数据库“搬运”到 Java 对象中
                    User user = new User();
                    user.setId(rs.getInt("id"));             // 读取 id 列
                    user.setUsername(rs.getString("username")); // 读取 username 列
                    user.setPassword(rs.getString("password")); // 读取 password 列
                    user.setRole(rs.getString("role"));       // 读取 role 列
                    return user;
                }
            }
        } catch (SQLException e) {
            // 异常处理：打印错误堆栈，方便排查
            System.err.println("查询用户失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null; // 没找到返回 null
    }
}