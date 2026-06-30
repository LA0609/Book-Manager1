package com.library;

import javax.swing.*;
import com.library.ui.borrow.BorrowListPanel;
import com.library.ui.BookListFrame;
import com.library.ui.ReaderPanel;

/**
 * 主窗口类（系统入口）
 *
 * 作用：图书管理系统的"总控中心"，登录成功后打开此窗口。
 * 简单来说，这就是系统主页，用标签页（TabbedPane）把三大功能模块整合在一起：
 *
 * 标签页结构：
 * 1. "图书管理" → BookListFrame（图书的增删改查）
 * 2. "读者管理" → ReaderPanel（读者的增删改查）
 * 3. "借阅管理" → BorrowListPanel（借书、还书、借阅记录查询）
 *
 * 启动流程：
 * main() → initDB() 初始化数据库 → 显示登录窗口 LoginFrame
 * → 用户登录成功 → 打开本窗口（Main）→ 用户使用各功能模块
 *
 * @see com.library.ui.login.LoginFrame 登录窗口
 * @see com.library.util.DBUtil#initDB() 数据库初始化
 */
public class Main extends JFrame {

    /**
     * 构造方法：初始化主窗口，创建三个标签页
     * 简单来说，就是把图书管理、读者管理、借阅管理三个页面组装到一个窗口里。
     */
    public Main() {
        setTitle("知书阁——图书管理系统");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Microsoft YaHei UI", java.awt.Font.PLAIN, 14));

        // 标签1：图书管理（成员B模块）
        BookListFrame bookFrame = new BookListFrame();
        tabbedPane.addTab("图书管理", bookFrame.getContentPane());

        // 标签2：读者管理（成员C模块）
        ReaderPanel readerFrame = new ReaderPanel();
        tabbedPane.addTab("读者管理", readerFrame.getContentPane());

        // 标签3：借阅管理（成员A模块）
        tabbedPane.addTab("借阅管理", new BorrowListPanel());

        setContentPane(tabbedPane);
    }

    /**
     * 程序入口
     * 启动顺序：① 初始化数据库 → ② 显示登录窗口 → ③ 登录成功后打开主窗口
     */
    public static void main(String[] args) {
        // 初始化数据库和表结构（首次运行时自动建库建表）
        com.library.util.DBUtil.initDB();
        java.awt.EventQueue.invokeLater(() -> {
            // 先显示登录窗口，登录成功后再打开主窗口
            new com.library.ui.login.LoginFrame().setVisible(true);
        });
    }
}