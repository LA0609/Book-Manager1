package com.library.ui.borrow;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import com.library.model.BorrowRecord;
import com.library.dao.BorrowDao;

public class BorrowListPanel extends javax.swing.JPanel {

    public BorrowListPanel() {
        initComponents();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnBorrow = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        ScrollPane = new javax.swing.JScrollPane();
        tableBorrow = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 500));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 14));
        jLabel1.setText("搜索关键字：");
        topPanel.add(jLabel1);

        txtSearch.setColumns(15);
        topPanel.add(txtSearch);

        btnSearch.setText("查 询");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        topPanel.add(btnSearch);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        topPanel.add(jSeparator1);

        btnBorrow.setBackground(new java.awt.Color(204, 221, 255));
        btnBorrow.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12));
        btnBorrow.setText("办理借书");
        btnBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrowActionPerformed(evt);
            }
        });
        topPanel.add(btnBorrow);

        btnReturn.setBackground(new java.awt.Color(204, 255, 204));
        btnReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12));
        btnReturn.setText("办理还书");
        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
            }
        });
        topPanel.add(btnReturn);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        tableBorrow.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "记录ID", "图书名称", "读者姓名", "借出日期", "应还日期", "状态", "罚款金额" }
        ));
        tableBorrow.setRowHeight(28);
        ScrollPane.setViewportView(tableBorrow);

        add(ScrollPane, java.awt.BorderLayout.CENTER);
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        loadData();
    }

    private void btnBorrowActionPerformed(java.awt.event.ActionEvent evt) {
        new BorrowBookDialog(null, true).setVisible(true);
        loadData();
    }

    private void btnReturnActionPerformed(java.awt.event.ActionEvent evt) {
        new ReturnBookDialog(null, true).setVisible(true);
        loadData();
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tableBorrow.getModel();
        model.setRowCount(0);
        String keyword = txtSearch.getText();
        List<BorrowRecord> list = new BorrowDao().findAll(keyword);
        for (BorrowRecord r : list) {
            String fineStr = r.getFine() > 0 ? String.format("%.2f", r.getFine()) : "-";
            model.addRow(new Object[]{
                r.getId(), r.getBookName(), r.getReaderName(),
                r.getBorrowDate(), r.getReturnDate(), r.getStatus(), fineStr
            });
        }
    }

    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JButton btnBorrow;
    private javax.swing.JButton btnReturn;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tableBorrow;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtSearch;
}
