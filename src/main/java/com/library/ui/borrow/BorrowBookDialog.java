package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 * 【弹窗】办理借书
 * 功能：输入读者编号和图书ID，设置借阅天数，完成借书操作
 * 校验：读者编号/图书ID必须为正整数，借阅天数范围1~365天
 */
public class BorrowBookDialog extends javax.swing.JDialog {

    public BorrowBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // 设置借阅天数范围：最小1天，最大365天，默认30天
        spinnerDays.setModel(new SpinnerNumberModel(30, 1, 365, 1));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlabel = new javax.swing.JLabel();
        jalabel1 = new javax.swing.JLabel();
        txtReaderId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtBookId = new javax.swing.JTextField();
        btnConfirmBorrow = new javax.swing.JButton();
        spinnerDays = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("办理图书借阅");
        setSize(new java.awt.Dimension(420, 350));

        jlabel.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jlabel.setText("读者编号：");

        jalabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jalabel1.setText("图书ID：");

        txtReaderId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        txtReaderId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReaderIdActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jLabel3.setText("借阅天数：");

        txtBookId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        txtBookId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBookIdActionPerformed(evt);
            }
        });

        btnConfirmBorrow.setBackground(new java.awt.Color(51, 153, 255));
        btnConfirmBorrow.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16));
        btnConfirmBorrow.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmBorrow.setText("确 认 借 阅");
        btnConfirmBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmBorrowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirmBorrow, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlabel)
                            .addComponent(jalabel1)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtReaderId)
                            .addComponent(txtBookId)
                            .addComponent(spinnerDays, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabel)
                    .addComponent(txtReaderId, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jalabel1)
                    .addComponent(txtBookId, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinnerDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(btnConfirmBorrow, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtReaderIdActionPerformed(java.awt.event.ActionEvent evt) {
        txtBookId.requestFocus();
    }

    private void txtBookIdActionPerformed(java.awt.event.ActionEvent evt) {
        btnConfirmBorrow.doClick();
    }

    private void btnConfirmBorrowActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // 获取并校验读者编号（必须为正整数）
            int readerId = Integer.parseInt(txtReaderId.getText().trim());
            if (readerId <= 0) {
                JOptionPane.showMessageDialog(this, "读者编号必须为正整数！");
                return;
            }

            // 获取并校验图书ID（必须为正整数）
            int bookId = Integer.parseInt(txtBookId.getText().trim());
            if (bookId <= 0) {
                JOptionPane.showMessageDialog(this, "图书ID必须为正整数！");
                return;
            }

            // 获取借阅天数（SpinnerNumberModel已限制1~365）
            int days = (int) spinnerDays.getValue();

            BorrowDao dao = new BorrowDao();

            // 检查该读者是否有逾期未还书籍
            if (dao.checkOverdue(readerId)) {
                JOptionPane.showMessageDialog(this, "借阅失败：该读者有逾期未还书籍，请先处理！");
                return;
            }

            // 检查该读者借阅数量是否已达上限（5本）
            if (!dao.checkLimit(readerId)) {
                JOptionPane.showMessageDialog(this, "借阅失败：该读者借阅数量已达上限（5本）！");
                return;
            }

            // 计算借阅日期和应还日期
            String borrowDate = java.time.LocalDate.now().toString();
            String returnDate = java.time.LocalDate.now().plusDays(days).toString();

            // 执行借书操作
            boolean success = dao.borrowBook(bookId, readerId, borrowDate, returnDate);
            if (success) {
                JOptionPane.showMessageDialog(this, "借阅成功！\n应还日期：" + returnDate);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "借阅失败：读者不存在/已注销，或图书库存不足！");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的正整数！");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmBorrow;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jalabel1;
    private javax.swing.JLabel jlabel;
    private javax.swing.JSpinner spinnerDays;
    private javax.swing.JTextField txtBookId;
    private javax.swing.JTextField txtReaderId;
    // End of variables declaration//GEN-END:variables
}