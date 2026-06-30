package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import com.library.model.BorrowRecord;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

/**
 * 【弹窗】办理还书
 *
 * 作用：图书管理员通过此弹窗为读者办理还书手续。
 * 简单来说，输入借阅记录号，系统自动显示借阅详情（借的什么书、谁借的、什么时候借的），
 * 确认无误后点击"确认归还"完成还书。
 *
 * 核心设计：
 * - 输入记录号后实时查询并显示借阅详情（通过 DocumentListener 实现）
 * - 归还时自动计算逾期罚款（超过应还日期每天0.5元，上限50元）
 * - 归还操作由 BorrowDao.returnBook() 以事务方式完成，保证数据一致性
 *
 * @see BorrowDao#returnBook(int) 实际的还书事务逻辑
 */
public class ReturnBookDialog extends javax.swing.JDialog {

    private JLabel lblDetail;

    public ReturnBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setupLayout();
        addInputListener();
        setSize(400, 280);
        setLocationRelativeTo(parent);
    }

    private void setupLayout() {
        lblDetail = new JLabel(" ");
        lblDetail.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblDetail.setForeground(new java.awt.Color(0, 102, 204));

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirmReturn, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDetail)
                    .addComponent(jLabelTip)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(12)
                        .addComponent(txtRecordId, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
                .addGap(20)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecordId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addGap(2)
                .addComponent(jLabelTip)
                .addGap(8)
                .addComponent(lblDetail)
                .addGap(20)
                .addComponent(btnConfirmReturn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );

        setContentPane(panel);
    }

    private void addInputListener() {
        txtRecordId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupRecord(); }
            public void removeUpdate(DocumentEvent e) { lookupRecord(); }
            public void changedUpdate(DocumentEvent e) { lookupRecord(); }
        });
    }

    /**
     * 根据输入的记录号实时查询借阅详情
     * 简单来说，用户每输入一个字符都会触发此方法，
     * 自动去数据库查对应的借阅记录并显示在界面上。
     */
    private void lookupRecord() {
        String text = txtRecordId.getText().trim();
        if (text.isEmpty()) { lblDetail.setText(" "); return; }
        try {
            int recordId = Integer.parseInt(text);
            List<BorrowRecord> all = new BorrowDao().findAll(null, null);
            BorrowRecord target = null;
            for (BorrowRecord r : all) {
                if (r.getId() == recordId) { target = r; break; }
            }
            if (target != null) {
                String statusText = "borrowing".equals(target.getStatus()) ? "\u501f\u51fa\u4e2d" : "\u5df2\u5f52\u8fd8";
                String info = "\u2192 " + target.getBookName() + " | \u8bfb\u8005\uff1a" + target.getReaderName()
                    + " | " + target.getBorrowDate() + " \u2192 " + target.getReturnDate() + " | " + statusText;
                lblDetail.setText(info);
                lblDetail.setForeground("borrowing".equals(target.getStatus()) ? new java.awt.Color(0,153,0) : new java.awt.Color(153,153,153));
            } else {
                lblDetail.setText("\u2192 \u672a\u627e\u5230\u8be5\u8bb0\u5f55");
                lblDetail.setForeground(new java.awt.Color(255,51,51));
            }
        } catch (NumberFormatException ex) { lblDetail.setText(" "); }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        txtRecordId = new javax.swing.JTextField();
        jLabelTip = new javax.swing.JLabel();
        btnConfirmReturn = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("\u529e\u7406\u56fe\u4e66\u5f52\u8fd8");
        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jLabel1.setText("\u501f\u9605\u8bb0\u5f55\u53f7\uff1a");
        txtRecordId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jLabelTip.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 10));
        jLabelTip.setForeground(new java.awt.Color(99, 99, 99));
        jLabelTip.setText("(\u63d0\u793a\uff1a\u8f93\u5165\u8bb0\u5f55\u53f7\u540e\u81ea\u52a8\u663e\u793a\u501f\u9605\u8be6\u60c5)");
        btnConfirmReturn.setBackground(new java.awt.Color(51, 204, 51));
        btnConfirmReturn.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16));
        btnConfirmReturn.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmReturn.setText("\u786e \u8ba4 \u5f52 \u8fd8");
        btnConfirmReturn.addActionListener(evt -> btnConfirmReturnActionPerformed());
    }

    private void btnConfirmReturnActionPerformed() {
        try {
            int recordId = Integer.parseInt(txtRecordId.getText().trim());
            if (recordId <= 0) { JOptionPane.showMessageDialog(this, "\u501f\u9605\u8bb0\u5f55\u53f7\u5fc5\u987b\u4e3a\u6b63\u6574\u6570\uff01"); return; }
            double fine = new BorrowDao().returnBook(recordId);
            if (fine == -1) {
                JOptionPane.showMessageDialog(this, "\u5f52\u8fd8\u5931\u8d25\uff1a\u8bb0\u5f55\u53f7\u4e0d\u5b58\u5728\u6216\u5df2\u5f52\u8fd8\uff01");
            } else if (fine > 0) {
                JOptionPane.showMessageDialog(this, "\u5f52\u8fd8\u6210\u529f\uff01\n\u903e\u671f\u7f5a\u6b3e\uff1a\uffe5" + String.format("%.2f", fine) + "\n\uff08\u8d85\u8fc7\u5e94\u8fd8\u65e5\u671f\uff0c\u6bcf\u59290.5\u5143\uff0c\u4e0a\u965050\u5143\uff09");
            } else {
                JOptionPane.showMessageDialog(this, "\u5f52\u8fd8\u6210\u529f\uff01\u65e0\u903e\u671f\uff0c\u65e0\u9700\u7f5a\u6b3e\u3002");
            }
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u8bb0\u5f55\u53f7\uff01");
        }
    }

    private javax.swing.JButton btnConfirmReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTip;
    private javax.swing.JTextField txtRecordId;
}