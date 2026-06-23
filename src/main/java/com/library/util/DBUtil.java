package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 【MySQL版】数据库工具类
 * 负责连接 MySQL 服务器并初始化表结构。
 */
public class DBUtil {
    
    // ---------------------------------------------------------
    // 1. MySQL 连接配置
    // ---------------------------------------------------------
    
    // 连接 URL：jdbc:mysql://地址:端口/库名?参数
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    
    // 用户名 (默认是 root)
    private static final String USER = "root"; 
    
    // 密码
    private static final String PASSWORD = "3296901701aA@";

    // ---------------------------------------------------------
    // 2. 静态代码块 (加载驱动)
    // ---------------------------------------------------------
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(">>> [错误] MySQL 驱动加载失败！");
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // 3. 获取连接方法
    // ---------------------------------------------------------
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ---------------------------------------------------------
    // 4. 初始化数据库与表结构
    // ---------------------------------------------------------
    public static void initDB() {
        // 先连到服务器本身 (不带库名)，手动建库
        String serverUrl = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        
        try (Connection conn = DriverManager.getConnection(serverUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // 1. 创建数据库
            stmt.execute("CREATE DATABASE IF NOT EXISTS library_db");
            System.out.println(">>> [DBUtil] 数据库 library_db 已就绪！");
            
            // 2. 切换到该数据库
            stmt.execute("USE library_db");
            
            // 3. 创建表
            // 用户表
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "role VARCHAR(20) DEFAULT 'admin'" +
                    ")");
            
            // 图书表
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "isbn VARCHAR(20)," +
                    "name VARCHAR(100) NOT NULL," +
                    "author VARCHAR(50)," +
                    "publisher VARCHAR(100)," +
                    "total_count INT DEFAULT 0," +
                    "current_count INT DEFAULT 0" +
                    ")");
            
            // 读者表
            stmt.execute("CREATE TABLE IF NOT EXISTS readers (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "card_no VARCHAR(20)," +
                    "name VARCHAR(50) NOT NULL," +
                    "phone VARCHAR(20)," +
                    "status VARCHAR(20) DEFAULT '正常'" +
                    ")");
            
            // 借阅表
            stmt.execute("CREATE TABLE IF NOT EXISTS borrow_records (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "book_id INT," +
                    "reader_id INT," +
                    "borrow_date VARCHAR(20)," +
                    "return_date VARCHAR(20)," +
                    "status VARCHAR(20) DEFAULT '借出' ," + 
                    "fine DECIMAL(10,2) DEFAULT 0" +
                    ")");
            
            System.out.println(">>> [DBUtil] 所有数据表初始化成功！");
            
        } catch (SQLException e) {
            System.err.println(">>> [DBUtil错误] 初始化失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
