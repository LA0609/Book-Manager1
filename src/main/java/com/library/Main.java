package com.library;

// 引入必要的包
import javax.swing.*;
import com.library.ui.borrow.BorrowListPanel; // 你的借阅面板
// import com.library.ui.book.BookPanel;      // 成员 B 的面板 (待引入)
// import com.library.ui.reader.ReaderPanel;   // 成员 C 的面板 (待引入)

/**
 * 【主窗口】MainFrame
 * 作用：登录成功后显示的主界面，包含选项卡用于切换不同模块。
 */
public class Main extends javax.swing.JFrame {

    public Main() {
        // 1. 初始化界面组件
        initComponents();
        
        // 2. 手动组装选项卡
        setupTabs();
        
        // 3. 窗口设置
        setTitle("图书管理系统 - 主控制台");
        setSize(1000, 700); // 设置窗口大小
        setLocationRelativeTo(null); // 窗口居中
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出程序
    }

    /**
     * 组装选项卡的方法
     */
    private void setupTabs() {
        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // --- 添加借阅管理 Tab ---
        BorrowListPanel borrowPanel = new BorrowListPanel();
        tabbedPane.addTab("借阅管理", borrowPanel);
        
        // --- 添加图书管理 Tab (待成员 B 完善后取消注释) ---
        // BookPanel bookPanel = new BookPanel();
        // tabbedPane.addTab("图书管理", bookPanel);
        
        // --- 添加读者管理 Tab (待成员 C 完善后取消注释) ---
        // ReaderPanel readerPanel = new ReaderPanel();
        // tabbedPane.addTab("读者管理", readerPanel);
        
        // 把选项卡放到窗口中央
        this.setContentPane(tabbedPane);
    }

    /**
     * NetBeans 自动生成的初始化代码 (通常为空，因为我们用了手动 setupTabs)
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }

    /**
     * 启动主窗口的入口
     */
    public static void main(String[] args) {
        // 初始化数据库
        com.library.util.DBUtil.initDB();
        
        // 启动主窗口 (Swing 线程安全启动方式)
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}