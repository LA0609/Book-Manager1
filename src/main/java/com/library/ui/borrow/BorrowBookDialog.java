package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import com.library.dao.BookDao;
import com.library.dao.ReaderDao;
import com.library.model.Book;
import com.library.model.Reader;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 【弹窗】办理借书
 * 功能：输入读者编号和图书ID，实时显示对应姓名/书名，设置借阅天数，完成借书
 */
public class BorrowBookDialog extends javax.swing.JDialog {

    private JLabel lblReaderName;
    private JLabel lblBookName;

    public BorrowBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // 设置借阅天数范围
        spinnerDays.setModel(new SpinnerNumberModel(30, 1, 365, 1));
        // 建立完整布局（含实时查询标签）
        setupLayout();
        // 监听输入变化
        addInputListeners();
        setSize(380, 330);
        setLocationRelativeTo(parent);
    }

    /**
     * 构建完整界面布局，在initComponents之后调用
     */
    private void setupLayout() {
        lblReaderName = new JLabel(" ");
        lblReaderName.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblReaderName.setForeground(new java.awt.Color(0, 102, 204));

        lblBookName = new JLabel(" ");
        lblBookName.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblBookName.setForeground(new java.awt.Color(0, 102, 204));

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirmBorrow, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jlabel)
                            .addComponent(jalabel1)
                            .addComponent(jLabel3))
                        .addGap(12)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtReaderId, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblReaderName)
                            .addComponent(txtBookId, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBookName)
                            .addComponent(spinnerDays, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))))
                .addGap(20)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                // 读者编号
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabel)
                    .addComponent(txtReaderId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblReaderName)
                .addGap(10)
                // 图书ID
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jalabel1)
                    .addComponent(txtBookId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblBookName)
                .addGap(10)
                // 借阅天数
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinnerDays, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                // 确认按钮
                .addComponent(btnConfirmBorrow, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );

        setContentPane(panel);
    }

    private void addInputListeners() {
        txtReaderId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupReader(); }
            public void removeUpdate(DocumentEvent e) { lookupReader(); }
            public void changedUpdate(DocumentEvent e) { lookupReader(); }
        });
        txtBookId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupBook(); }
            public void removeUpdate(DocumentEvent e) { lookupBook(); }
            public void changedUpdate(DocumentEvent e) { lookupBook(); }
        });
    }

    private void lookupReader() {
        String text = txtReaderId.getText().trim();
        if (text.isEmpty()) { lblReaderName.setText(" "); return; }
        try {
            int id = Integer.parseInt(text);
            Reader reader = new ReaderDao().findById(id);
            if (reader != null) {
                lblReaderName.setText("\u2192 " + reader.getName() + "\uff08" + reader.getStatus() + "\uff09");
                lblReaderName.setForeground("\u5df2\u6ce8\u9500".equals(reader.getStatus()) ? new java.awt.Color(255,51,51) : new java.awt.Color(0,153,0));
            } else {
                lblReaderName.setText("\u2192 \u672a\u627e\u5230\u8be5\u8bfb\u8005");
                lblReaderName.setForeground(new java.awt.Color(255,51,51));
            }
        } catch (NumberFormatException ex) { lblReaderName.setText(" "); }
    }

    private void lookupBook() {
        String text = txtBookId.getText().trim();
        if (text.isEmpty()) { lblBookName.setText(" "); return; }
        try {
            int id = Integer.parseInt(text);
            Book book = new BookDao().findById(id);
            if (book != null) {
                lblBookName.setText("\u2192 " + book.getName() + "\uff08\u5728\u9986" + book.getCurrentCount() + "\u672c\uff09");
                lblBookName.setForeground(book.getCurrentCount() <= 0 ? new java.awt.Color(255,51,51) : new java.awt.Color(0,153,0));
            } else {
                lblBookName.setText("\u2192 \u672a\u627e\u5230\u8be5\u56fe\u4e66");
                lblBookName.setForeground(new java.awt.Color(255,51,51));
            }
        } catch (NumberFormatException ex) { lblBookName.setText(" "); }
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
        setTitle("\u529e\u7406\u56fe\u4e66\u501f\u9605");
        jlabel.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jlabel.setText("\u8bfb\u8005\u7f16\u53f7\uff1a");
        jalabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jalabel1.setText("\u56fe\u4e66ID\uff1a");
        txtReaderId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        txtReaderId.addActionListener(evt -> txtBookId.requestFocus());
        jLabel3.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        jLabel3.setText("\u501f\u9605\u5929\u6570\uff1a");
        txtBookId.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14));
        txtBookId.addActionListener(evt -> btnConfirmBorrow.doClick());
        btnConfirmBorrow.setBackground(new java.awt.Color(51, 153, 255));
        btnConfirmBorrow.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 16));
        btnConfirmBorrow.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmBorrow.setText("\u786e \u8ba4 \u501f \u9605");
        btnConfirmBorrow.addActionListener(evt -> btnConfirmBorrowActionPerformed());
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmBorrowActionPerformed() {
        try {
            int readerId = Integer.parseInt(txtReaderId.getText().trim());
            if (readerId <= 0) { JOptionPane.showMessageDialog(this, "\u8bfb\u8005\u7f16\u53f7\u5fc5\u987b\u4e3a\u6b63\u6574\u6570\uff01"); return; }
            int bookId = Integer.parseInt(txtBookId.getText().trim());
            if (bookId <= 0) { JOptionPane.showMessageDialog(this, "\u56fe\u4e66ID\u5fc5\u987b\u4e3a\u6b63\u6574\u6570\uff01"); return; }
            int days = (int) spinnerDays.getValue();
            BorrowDao dao = new BorrowDao();
            if (dao.checkOverdue(readerId)) { JOptionPane.showMessageDialog(this, "\u501f\u9605\u5931\u8d25\uff1a\u8be5\u8bfb\u8005\u6709\u903e\u671f\u672a\u8fd8\u4e66\u7c4d\uff0c\u8bf7\u5148\u5904\u7406\uff01"); return; }
            if (!dao.checkLimit(readerId)) { JOptionPane.showMessageDialog(this, "\u501f\u9605\u5931\u8d25\uff1a\u8be5\u8bfb\u8005\u501f\u9605\u6570\u91cf\u5df2\u8fbe\u4e0a\u9650\uff085\u672c\uff09\uff01"); return; }
            String borrowDate = java.time.LocalDate.now().toString();
            String returnDate = java.time.LocalDate.now().plusDays(days).toString();
            boolean success = dao.borrowBook(bookId, readerId, borrowDate, returnDate);
            if (success) {
                JOptionPane.showMessageDialog(this, "\u501f\u9605\u6210\u529f\uff01\n\u5e94\u8fd8\u65e5\u671f\uff1a" + returnDate);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "\u501f\u9605\u5931\u8d25\uff1a\u8bfb\u8005\u4e0d\u5b58\u5728/\u5df2\u6ce8\u9500\uff0c\u6216\u56fe\u4e66\u5e93\u5b58\u4e0d\u8db3\uff01");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u6b63\u6574\u6570\uff01");
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