package com.library.ui.borrow;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import com.library.model.BorrowRecord;
import com.library.dao.BorrowDao;

/**
 * 【面板】借阅管理主面板
 * 功能：展示借阅记录列表，支持按状态筛选、按读者姓名模糊搜索、办理借书/还书
 * 注意：此文件由NetBeans窗体设计器管理，手改时请勿破坏GEN-BEGIN/GEN-END标记
 */
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
        jLabel2 = new javax.swing.JLabel();
        txtReaderSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnBorrow = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        ScrollPane = new javax.swing.JScrollPane();
        tableBorrow = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 500));
        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 14)); // NOI18N
        jLabel1.setText("筛选状态：");
        topPanel.add(jLabel1);

        comboStatus.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 13)); // NOI18N
        comboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全部", "借出中", "已归还" }));
        comboStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStatusActionPerformed(evt);
            }
        });
        topPanel.add(comboStatus);

        jLabel2.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14)); // NOI18N
        jLabel2.setText("读者姓名：");
        topPanel.add(jLabel2);

        txtReaderSearch.setColumns(10);
        txtReaderSearch.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 13)); // NOI18N
        txtReaderSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReaderSearchActionPerformed(evt);
            }
        });
        topPanel.add(txtReaderSearch);

        btnSearch.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        btnSearch.setText("查 询");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        topPanel.add(btnSearch);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, 25));
        topPanel.add(jSeparator1);

        btnBorrow.setBackground(new java.awt.Color(204, 221, 255));
        btnBorrow.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12)); // NOI18N
        btnBorrow.setText("办理借书");
        btnBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrowActionPerformed(evt);
            }
        });
        topPanel.add(btnBorrow);

        btnReturn.setBackground(new java.awt.Color(204, 255, 204));
        btnReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12)); // NOI18N
        btnReturn.setText("办理还书");
        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
            }
        });
        topPanel.add(btnReturn);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        tableBorrow.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "记录号", "书名", "读者姓名", "借出日期", "应还日期", "状态", "罚款(元)"
            }
        ));
        tableBorrow.setRowHeight(28);
        ScrollPane.setViewportView(tableBorrow);

        add(ScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /** 点击查询按钮，按读者姓名模糊搜索 */
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadData();
    }//GEN-LAST:event_btnSearchActionPerformed

    /** 读者姓名输入框回车触发搜索 */
    private void txtReaderSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReaderSearchActionPerformed
        loadData();
    }//GEN-LAST:event_txtReaderSearchActionPerformed

    /** 点击办理借书按钮，弹出借书对话框 */
    private void btnBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrowActionPerformed
        new BorrowBookDialog(null, true).setVisible(true);
        loadData();
    }//GEN-LAST:event_btnBorrowActionPerformed

    /** 点击办理还书按钮，弹出还书对话框 */
    private void btnReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnActionPerformed
        new ReturnBookDialog(null, true).setVisible(true);
        loadData();
    }//GEN-LAST:event_btnReturnActionPerformed

    /** 状态下拉框切换时自动刷新列表 */
    private void comboStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStatusActionPerformed
        loadData();
    }//GEN-LAST:event_comboStatusActionPerformed

    /**
     * 加载借阅数据到表格
     * 支持按状态筛选 + 按读者姓名模糊搜索
     */
    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tableBorrow.getModel();
        model.setRowCount(0);

        String statusFilter = (String) comboStatus.getSelectedItem();
        String readerKeyword = txtReaderSearch.getText().trim();

        List<BorrowRecord> list = new BorrowDao().findAll("", statusFilter);

        for (BorrowRecord r : list) {
            // 读者姓名模糊过滤
            if (!readerKeyword.isEmpty() && (r.getReaderName() == null || !r.getReaderName().contains(readerKeyword))) {
                continue;
            }
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tableBorrow;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtReaderSearch;
    // End of variables declaration//GEN-END:variables
}