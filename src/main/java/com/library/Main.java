package com.library;

import javax.swing.*;
import com.library.ui.borrow.BorrowListPanel;
import com.library.ui.BookListFrame;
import com.library.ui.ReaderPanel;

public class Main extends JFrame {

    public Main() {
        setTitle("Library Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Microsoft YaHei UI", java.awt.Font.PLAIN, 14));

        // Tab 1: Book Management (from member B's BookListFrame)
        BookListFrame bookFrame = new BookListFrame();
        tabbedPane.addTab("Book Management", bookFrame.getContentPane());

        // Tab 2: Reader Management (from member C's ReaderPanel)
        ReaderPanel readerFrame = new ReaderPanel();
        tabbedPane.addTab("Reader Management", readerFrame.getContentPane());

        // Tab 3: Borrow Management (from member A's BorrowListPanel)
        tabbedPane.addTab("Borrow Management", new BorrowListPanel());

        setContentPane(tabbedPane);
    }

    public static void main(String[] args) {
        com.library.util.DBUtil.initDB();
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
