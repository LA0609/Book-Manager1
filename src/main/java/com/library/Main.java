package com.library;

import javax.swing.*;
import com.library.ui.borrow.BorrowListPanel;
import com.library.ui.BookListFrame;
import com.library.ui.ReaderPanel;

/**
 * 主窗口类：包含三个功能模块的标签页
 * 启动流程：先显示登录窗口，登录成功后才打开本窗口
 */
public class Main extends JFrame {

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

    public static void main(String[] args) {
        // 初始化数据库和表结构
        com.library.util.DBUtil.initDB();
        java.awt.EventQueue.invokeLater(() -> {
            // 先显示登录窗口，登录成功后再打开主窗口
            new com.library.ui.login.LoginFrame().setVisible(true);
        });
    }
}