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

    /** 读者姓名提示标签 */
    private JLabel lblReaderName;
    /** 图书名称提示标签 */
    private JLabel lblBookName;

    public BorrowBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // 设置借阅天数范围：最小1天，最大365天，默认30天
        spinnerDays.setModel(new SpinnerNumberModel(30, 1, 365, 1));
        // 添加实时查询标签（在initComponents生成的布局之后追加）
        addLookupLabels();
        // 监听输入变化，实时查询并显示
        addInputListeners();
    }

    /**
     * 在输入框右侧添加实时查询结果标签
     */
    private void addLookupLabels() {
        lblReaderName = new JLabel(" ");
        lblReaderName.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblReaderName.setForeground(new java.awt.Color(0, 102, 204));

        lblBookName = new JLabel(" ");
        lblBookName.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12));
        lblBookName.setForeground(new java.awt.Color(0, 102, 204));

        // 插入到内容面板，紧挨输入框下方
        javax.swing.JPanel contentPanel = (javax.swing.JPanel) getContentPane();
        java.awt.Component[] comps = contentPanel.getComponents();

        // 用BoxLayout重新排列：在每个输入框下面插入提示标签
        // 简化做法：直接在面板末尾追加两个提示标签
        // 改用更简单的方式——修改布局，在输入框下方添加提示
        GroupLayout layout = (GroupLayout) contentPanel.getLayout();

        // 在读者编号输入框下方添加读者姓名提示
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(30)
                // 读者编号行
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabel)
                    .addComponent(txtReaderId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addGap(3)
                .addComponent(lblReaderName)
                .addGap(15)
                // 图书ID行
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jalabel1)
                    .addComponent(txtBookId, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addGap(3)
                .addComponent(lblBookName)
                .addGap(15)
                // 借阅天数行
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinnerDays, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(30)
                // 确认按钮
                .addComponent(btnConfirmBorrow, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(30)
        );

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(35)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirmBorrow, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jlabel)
                            .addComponent(jalabel1)
                            .addComponent(jLabel3))
                        .addGap(18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtReaderId)
                            .addComponent(lblReaderName)
                            .addComponent(txtBookId)
                            .addComponent(lblBookName)
                            .addComponent(spinnerDays, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
                .addContainerGap(30, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(380, 330));
    }

    /**
     * 监听输入框文本变化，输入ID后实时查询显示对应名称
     */
    private void addInputListeners() {
        // 读者编号输入框：文本变化时查询读者姓名
        txtReaderId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupReader(); }
            public void removeUpdate(DocumentEvent e) { lookupReader(); }
            public void changedUpdate(DocumentEvent e) { lookupReader(); }
        });

        // 图书ID输入框：文本变化时查询图书名称
        txtBookId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupBook(); }
            public void removeUpdate(DocumentEvent e) { lookupBook(); }
            public void changedUpdate(DocumentEvent e) { lookupBook(); }
        });
    }

    /**
     * 根据输入的读者编号查询并显示读者姓名
     */
    private void lookupReader() {
        String text = txtReaderId.getText().trim();
        if (text.isEmpty()) {
            lblReaderName.setText(" ");
            return;
        }
        try {
            int id = Integer.parseInt(text);
            Reader reader = new ReaderDao().findById(id);
            if (reader != null) {
                lblReaderName.setText("→ " + reader.getName() + "（" + reader.getStatus() + "）");
                if ("已注销".equals(reader.getStatus())) {
                    lblReaderName.setForeground(new java.awt.Color(255, 51, 51));
                } else {
                    lblReaderName.setForeground(new java.awt.Color(0, 153, 0));
                }
            } else {
                lblReaderName.setText("→ 未找到该读者");
                lblReaderName.setForeground(new java.awt.Color(255, 51, 51));
            }
        } catch (NumberFormatException ex) {
            lblReaderName.setText(" ");
        }
    }

    /**
     * 根据输入的图书ID查询并显示图书名称和库存
     */
    private void lookupBook() {
        String text = txtBookId.getText().trim();
        if (text.isEmpty()) {
            lblBookName.setText(" ");
            return;
        }
        try {
            int id = Integer.parseInt(text);
            Book book = new BookDao().findById(id);
            if (book != null) {
                lblBookName.setText("→ " + book.getName() + "（在馆" + book.getCurrentCount() + "本）");
                if (book.getCurrentCount() <= 0) {
                    lblBookName.setForeground(new java.awt.Color(255, 51, 51));
                } else {
                    lblBookName.setForeground(new java.awt.Color(0, 153, 0));
                }
            } else {
                lblBookName.setText("→ 未找到该图书");
                lblBookName.setForeground(new java.awt.Color(255, 51, 51));
            }
        } catch (NumberFormatException ex) {
            lblBookName.setText(" ");
        }
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
    }// </editor-fold>//GEN-END:initComponents

    private void txtReaderIdActionPerformed(java.awt.event.ActionEvent evt) {
        txtBookId.requestFocus();
    }

    private void txtBookIdActionPerformed(java.awt.event.ActionEvent evt) {
        btnConfirmBorrow.doClick();
    }

    private void btnConfirmBorrowActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int readerId = Integer.parseInt(txtReaderId.getText().trim());
            if (readerId <= 0) {
                JOptionPane.showMessageDialog(this, "读者编号必须为正整数！");
                return;
            }
            int bookId = Integer.parseInt(txtBookId.getText().trim());
            if (bookId <= 0) {
                JOptionPane.showMessageDialog(this, "图书ID必须为正整数！");
                return;
            }
            int days = (int) spinnerDays.getValue();

            BorrowDao dao = new BorrowDao();
            if (dao.checkOverdue(readerId)) {
                JOptionPane.showMessageDialog(this, "借阅失败：该读者有逾期未还书籍，请先处理！");
                return;
            }
            if (!dao.checkLimit(readerId)) {
                JOptionPane.showMessageDialog(this, "借阅失败：该读者借阅数量已达上限（5本）！");
                return;
            }

            String borrowDate = java.time.LocalDate.now().toString();
            String returnDate = java.time.LocalDate.now().plusDays(days).toString();

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