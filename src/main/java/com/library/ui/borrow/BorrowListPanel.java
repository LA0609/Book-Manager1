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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comboStatus = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnBorrow = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        ScrollPane = new javax.swing.JScrollPane();
        tableBorrow = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 500));
        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 14));
        jLabel1.setText("筛选状态：");
        topPanel.add(jLabel1);

        comboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全部", "借出中", "已归还" }));
        topPanel.add(comboStatus);

        comboStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStatusActionPerformed(evt);
            }
        });

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
            new String [] { "记录号", "书名", "读者姓名", "借出日期", "归还日期", "状态", "罚款(元)" }
        ));
        tableBorrow.setRowHeight(28);
        ScrollPane.setViewportView(tableBorrow);

        add(ScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

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

    private void comboStatusActionPerformed(java.awt.event.ActionEvent evt) {
        loadData();
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tableBorrow.getModel();
        model.setRowCount(0);
        String statusFilter = (String) comboStatus.getSelectedItem();
        List<BorrowRecord> list = new BorrowDao().findAll("", statusFilter);
        for (BorrowRecord r : list) {
            String statusZh = r.getStatus();
            if ("borrowing".equals(statusZh)) statusZh = "借出中";
            else if ("returned".equals(statusZh)) statusZh = "已归还";
            model.addRow(new Object[]{
                r.getId(), r.getBookName(), r.getReaderName(),
                r.getBorrowDate(), r.getReturnDate(), statusZh,
                String.format("%.2f", r.getFine())
            });
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JButton btnBorrow;
    private javax.swing.JButton btnReturn;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> comboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tableBorrow;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
