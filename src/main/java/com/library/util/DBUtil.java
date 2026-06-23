package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; 
    private static final String PASSWORD = "3296901701aA@"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver load failed!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDB() {
        String serverUrl = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        
        try (Connection conn = DriverManager.getConnection(serverUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE DATABASE IF NOT EXISTS library_db");
            stmt.execute("USE library_db");
            
            // users
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "username VARCHAR(50) NOT NULL,"
                    + "password VARCHAR(50) NOT NULL,"
                    + "role VARCHAR(20) DEFAULT 'admin'"
                    + ")");
            
            // books (with is_delete for soft delete)
            stmt.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "isbn VARCHAR(20),"
                    + "name VARCHAR(100) NOT NULL,"
                    + "author VARCHAR(50),"
                    + "publisher VARCHAR(100),"
                    + "total_count INT DEFAULT 0,"
                    + "current_count INT DEFAULT 0,"
                    + "is_delete INT DEFAULT 0"
                    + ")");
            
            // readers (with gender)
            stmt.execute("CREATE TABLE IF NOT EXISTS readers ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "card_no VARCHAR(20),"
                    + "name VARCHAR(50) NOT NULL,"
                    + "gender VARCHAR(4),"
                    + "phone VARCHAR(20),"
                    + "status VARCHAR(20) DEFAULT '正常'"
                    + ")");
            
            // borrow_records (with fine)
            stmt.execute("CREATE TABLE IF NOT EXISTS borrow_records ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "book_id INT,"
                    + "reader_id INT,"
                    + "borrow_date VARCHAR(20),"
                    + "return_date VARCHAR(20),"
                    + "status VARCHAR(20) DEFAULT 'borrowing',"
                    + "fine DECIMAL(10,2) DEFAULT 0"
                    + ")");
            
            System.out.println("All tables ready!");
            
        } catch (SQLException e) {
            System.err.println("DB init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

