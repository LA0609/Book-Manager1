package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 【工具层】数据库连接工具类
 *
 * 作用：统一管理数据库的连接和初始化，是整个项目与 MySQL 交互的"桥梁"。
 * 简单来说，所有 DAO 类要操作数据库，都必须通过这个类获取连接。
 *
 * 核心职责：
 * 1. {@link #getConnection()} - 提供数据库连接（供 DAO 层调用）
 * 2. {@link #initDB()}  - 首次启动时自动建库、建表、插入默认管理员
 *
 * 技术要点：
 * - 使用 JDBC 驱动连接 MySQL 8.x
 * - 采用静态代码块加载驱动，确保类加载时驱动已就绪
 * - 密码等敏感信息硬编码仅适用于课设阶段，生产环境应改用配置文件或环境变量
 *
 * @author LA
 */
public class DBUtil {

    /** 数据库连接地址，含时区和SSL配置 */
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

    /** 数据库用户名 */
    private static final String USER = "root";

    /** 数据库密码（课设阶段硬编码，生产环境应使用配置文件或环境变量） */
    private static final String PASSWORD = "3296901701aA@";

    /**
     * 静态代码块：类加载时自动执行，用于注册 JDBC 驱动
     * 原理：Class.forName 会触发驱动类的静态初始化，将自身注册到 DriverManager
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败！");
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * 简单来说，调用这个方法就能拿到一条通往 MySQL 的"通道"。
     *
     * @return Connection 数据库连接对象
     * @throws SQLException 如果连接失败（如数据库未启动、密码错误等）
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 初始化数据库：创建库、表、默认数据
     *
     * 简单来说，程序第一次运行时调用此方法，会自动完成以下操作：
     * 1. 创建 library_db 数据库（如果不存在）
     * 2. 创建四张核心表：users、books、readers、borrow_records
     * 3. 插入默认管理员账号（admin/123456）
     *
     * 为什么需要这个方法？
     * → 让程序"开箱即用"，不需要用户手动执行 SQL 脚本建库建表。
     */
    public static void initDB() {
        // 连接 MySQL 服务器（不指定数据库，因为库可能还不存在）
        String serverUrl = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(serverUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 1. 创建数据库（IF NOT EXISTS 防止重复创建报错）
            stmt.execute("CREATE DATABASE IF NOT EXISTS library_db");
            stmt.execute("USE library_db");

            // 2. 创建用户表：存储登录账号信息（IF NOT EXISTS 保护已有数据）
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "username VARCHAR(50) NOT NULL,"
                    + "password VARCHAR(50) NOT NULL,"
                    + "role VARCHAR(20) DEFAULT 'admin'"
                    + ")");

            // 3. 创建图书表：is_delete 字段实现逻辑删除（0=正常，1=已删除）
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

            // 4. 创建读者表：status 字段控制读者是否可借阅
            stmt.execute("CREATE TABLE IF NOT EXISTS readers ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "card_no VARCHAR(20),"
                    + "name VARCHAR(50) NOT NULL,"
                    + "gender VARCHAR(4),"
                    + "phone VARCHAR(20),"
                    + "status VARCHAR(20) DEFAULT '正常'"
                    + ")");

            // 5. 创建借阅记录表：记录借还行为，fine 字段存储逾期罚款
            stmt.execute("CREATE TABLE IF NOT EXISTS borrow_records ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "book_id INT,"
                    + "reader_id INT,"
                    + "borrow_date VARCHAR(20),"
                    + "return_date VARCHAR(20),"
                    + "status VARCHAR(20) DEFAULT 'borrowing',"
                    + "fine DECIMAL(10,2) DEFAULT 0"
                    + ")");

            // 6. 插入默认管理员账号（仅在 users 表为空时插入，防止重复）
            stmt.execute("INSERT INTO users (username, password, role) "
                    + "SELECT 'admin', '123456', 'admin' FROM DUAL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM users LIMIT 1)");

            System.out.println("数据库初始化完成！所有表已就绪。");

        } catch (SQLException e) {
            System.err.println("数据库初始化失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
