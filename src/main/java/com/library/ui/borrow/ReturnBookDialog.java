package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import com.library.model.BorrowRecord;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

/**
 * 【弹窗】办理还书
 * 功能：输入借阅记录号，实时显示借阅详情（书名、读者、日期），确认归还
 */
public class ReturnBookDialog extends javax.swing.JDialog {

    /** 借阅详情提示标签 */
    private JLabel lblDetail;

    public ReturnBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // 添加实时查询标签和输入监听
        addLookupLabel();
        addInputListener();
    }

    /**
     * 在输入框下方添加实时查询结果标签
     */
    private void addLookupLabel() {
        lblDetail = new JLabel(" ");
        lblDetail.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblDetail.setForeground(new java.awt.Color(0, 102, 204));

        GroupLayout layout = (GroupLayout) getContentPane().getLayout();

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(40)
                // 记录号输入行
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecordId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addGap(2)
                .addComponent(jLabelTip)
                .addGap(8)
                // 详情展示
                .addComponent(lblDetail)
                .addGap(25)
                // 确认按钮
                .addComponent(btnConfirmReturn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(30)
        );

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblDetail)
                    .addComponent(jLabelTip)
                    .addComponent(btnConfirmReturn, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18)
                        .addComponent(txtRecordId, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(380, 280));
    }

    /**
     * 监听记录号输入，实时查询借阅详情
     */
    private void addInputListener() {
        txtRecordId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupRecord(); }
            public void removeUpdate(DocumentEvent e) { lookupRecord(); }
            public void changedUpdate(DocumentEvent e) { lookupRecord(); }
        });
    }

    /**
     * 根据记录号查询并显示借阅详情
     */
    private void lookupRecord() {
        String text = txtRecordId.getText().trim();
        if (text.isEmpty()) {
            lblDetail.setText(" ");
            return;
        }
        try {
            int recordId = Integer.parseInt(text);
            // 查询所有记录（不限状态），找到匹配的
            List<BorrowRecord> all = new BorrowDao().findAll(null, null);
            BorrowRecord target = null;
            for (BorrowRecord r : all) {
                if (r.getId() == recordId) {
                    target = r;
                    break;
                }
            }
            if (target != null) {
                String statusText = "borrowing".equals(target.getStatus()) ? "借出中" : "已归还";
                String info = String.format("→ %s | 读者：%s | %s → %s | %s",
                    target.getBookName(), target.getReaderName(),
                    target.getBorrowDate(), target.getReturnDate(), statusText);
                lblDetail.setText(info);
                if ("borrowing".equals(target.getStatus())) {
                    lblDetail.setForeground(new java.awt.Color(0, 153, 0));
                } else {
                    lblDetail.setForeground(new java.awt.Color(153, 153, 153));
                }
            } else {
                lblDetail.setText("→ 未找到该记录");
                lblDetail.setForeground(new java.awt.Color(255, 51, 51));
            }
        } catch (NumberFormatException ex) {
            lblDetail.setText(" ");
        }
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
        jLabel1.setText("借阅记录号：");

        txtRecordId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));

        jLabelTip.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 10));
        jLabelTip.setForeground(new java.awt.Color(99, 99, 99));
        jLabelTip.setText("(提示：输入记录号后自动显示借阅详情)");

        btnConfirmReturn.setBackground(new java.awt.Color(51, 204, 51));
        btnConfirmReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16));
        btnConfirmReturn.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmReturn.setText("确 认 归 还");
        btnConfirmReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmReturnActionPerformed(evt);
            }
        });
    }

    private void btnConfirmReturnActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int recordId = Integer.parseInt(txtRecordId.getText().trim());
            if (recordId <= 0) {
                JOptionPane.showMessageDialog(this, "借阅记录号必须为正整数！");
                return;
            }
            double fine = new BorrowDao().returnBook(recordId);
            if (fine == -1) {
                JOptionPane.showMessageDialog(this, "归还失败：记录号不存在或已归还！");
            } else if (fine > 0) {
                JOptionPane.showMessageDialog(this,
                    "归还成功！\n逾期罚款：¥" + String.format("%.2f", fine) + "\n（超过应还日期，每天0.5元，上限50元）");
            } else {
                JOptionPane.showMessageDialog(this, "归还成功！无逾期，无需罚款。");
            }
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的记录号！");
        }
    }

    private javax.swing.JButton btnConfirmReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTip;
    private javax.swing.JTextField txtRecordId;
}