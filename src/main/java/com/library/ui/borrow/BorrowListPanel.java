package com.library.ui.borrow;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import com.library.model.BorrowRecord;
import com.library.dao.BorrowDao;

/**
 * 借阅管理面板类
 *
 * 作用：作为主窗口"借阅管理"标签页的内容，展示所有借阅记录。
 * 简单来说，这就是图书馆的"借还记录本"，能看到谁借了什么书、什么时候还、有没有罚款。
 *
 * 功能清单：
 * - 展示所有借阅记录列表（联表查询，直接显示书名和读者姓名）
 * - 按状态筛选：全部 / 借出中 / 已归还
 * - 按读者姓名模糊搜索
 * - "办理借书"按钮：弹出 BorrowBookDialog 借书弹窗
 * - "办理还书"按钮：弹出 ReturnBookDialog 还书弹窗
 *
 * 数据来源：通过 BorrowDao.findAll() 联表查询 borrow_records、books、readers 三张表
 */
public class BorrowListPanel extends javax.swing.JPanel {

    /**
     * 构造方法：初始化界面组件并自动加载借阅数据
     */
    /**
     * 构造方法：初始化界面组件并自动加载借阅数据
     */
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
        txtReaderSearch = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnBorrow = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        ScrollPane = new javax.swing.JScrollPane();
        tableBorrow = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 500));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 14)); // NOI18N
        jLabel1.setText("筛选状态：");
        topPanel.add(jLabel1);

        comboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "全部", "借出中", "已归还", " " }));
        comboStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStatusActionPerformed(evt);
            }
        });
        topPanel.add(comboStatus);

        txtReaderSearch.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 14)); // NOI18N
        txtReaderSearch.setText("读者姓名");
        topPanel.add(txtReaderSearch);

        jTextField1.setColumns(10);
        jTextField1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 13)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        topPanel.add(jTextField1);

        btnSearch.setText("查 询");
        topPanel.add(btnSearch);

        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        topPanel.add(jSeparator1);

        btnBorrow.setBackground(new java.awt.Color(204, 221, 255));
        btnBorrow.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12)); // NOI18N
        btnBorrow.setText("办理借书");
        btnBorrow.setToolTipText("");
        topPanel.add(btnBorrow);

        btnBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrowActionPerformed(evt);
            }
        });

        btnReturn.setBackground(new java.awt.Color(204, 255, 204));
        btnReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 12)); // NOI18N
        btnReturn.setText("办理还书");
        topPanel.add(btnReturn);

        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
            }
        });

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        tableBorrow.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "记录ID", "图书名称", "读者姓名", "借出日期", "应还日期", "状态"
            }
        ));
        tableBorrow.setRowHeight(28);
        ScrollPane.setViewportView(tableBorrow);

        add(ScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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

    /**
     * 加载借阅数据到表格
     * 简单来说，从数据库查询借阅记录，然后填入界面表格中。
     * 这是一个被多处复用的方法：初始化、刷新、借还操作后都会调用。
     */
    /**
     * 加载借阅数据到表格
     * 简单来说，从数据库查询借阅记录，然后填入界面表格中。
     * 这是一个被多处复用的方法：初始化、刷新、借还操作后都会调用。
     */
    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tableBorrow.getModel();
        model.setRowCount(0);
        String statusFilter = (String) comboStatus.getSelectedItem();
        String readerKeyword = jTextField1.getText().trim();
        List<BorrowRecord> list = new BorrowDao().findAll("", statusFilter);
        for (BorrowRecord r : list) {
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tableBorrow;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel txtReaderSearch;
    // End of variables declaration//GEN-END:variables
}
