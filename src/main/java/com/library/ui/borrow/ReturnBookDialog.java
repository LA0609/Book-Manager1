package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import javax.swing.JOptionPane;

public class ReturnBookDialog extends javax.swing.JDialog {

    public ReturnBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtRecordId = new javax.swing.JTextField();
        jLabelTip = new javax.swing.JLabel();
        btnConfirmReturn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("办理图书归还");

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jLabel1.setText("借阅记录ID：");

        txtRecordId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));

        jLabelTip.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 10));
        jLabelTip.setForeground(new java.awt.Color(99, 99, 99));
        jLabelTip.setText("(提示：输入借阅记录ID)");

        btnConfirmReturn.setBackground(new java.awt.Color(51, 204, 51));
        btnConfirmReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16));
        btnConfirmReturn.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmReturn.setText("确 认 归 还");
        btnConfirmReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmReturnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTip)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtRecordId, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(btnConfirmReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecordId, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTip)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(btnConfirmReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pack();
    }

    private void btnConfirmReturnActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int recordId = Integer.parseInt(txtRecordId.getText());
            double fine = new BorrowDao().returnBook(recordId);
            if (fine == -1) {
                JOptionPane.showMessageDialog(this, "归还失败：记录ID不存在或已归还！");
            } else if (fine > 0) {
                JOptionPane.showMessageDialog(this,
                    "归还成功！\n逾期罚款：¥" + String.format("%.2f", fine) + "\n（超过应还日期，每天0.5元，上限50元）");
            } else {
                JOptionPane.showMessageDialog(this, "归还成功！无逾期，无需罚款。");
            }
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的记录ID！");
        }
    }

    private javax.swing.JButton btnConfirmReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTip;
    private javax.swing.JTextField txtRecordId;
}
