package com.library.ui.borrow;

import com.library.dao.BorrowDao;
import com.library.dao.BookDao;
import com.library.dao.ReaderDao;
import com.library.model.Book;
import com.library.model.Reader;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【弹窗】办理借书
 * 功能：通过书名/读者姓名模糊搜索选择，不需要记编号
 * 重名读者通过ID区分
 */
public class BorrowBookDialog extends javax.swing.JDialog {

    /** 选中的图书ID（-1表示未选择） */
    private int selectedBookId = -1;
    /** 选中的读者ID（-1表示未选择） */
    private int selectedReaderId = -1;

    /** 防抖定时器，避免每次按键都查数据库 */
    private javax.swing.Timer bookSearchTimer;
    private javax.swing.Timer readerSearchTimer;

    public BorrowBookDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setupUI();
        setupSearchTimers();
        setSize(450, 380);
        setLocationRelativeTo(parent);
    }

    private JTextField txtBookName;
    private JLabel lblBookInfo;
    private JTextField txtReaderName;
    private JLabel lblReaderInfo;
    private JSpinner spinnerDays;
    private JButton btnConfirm;

    /**
     * 构建完整界面
     */
    private void setupUI() {
        setTitle("办理图书借阅");

        // ---- 组件创建 ----
        JLabel lblBook = new JLabel("书    名：");
        lblBook.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        txtBookName = new JTextField(20);
        txtBookName.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        lblBookInfo = new JLabel(" ");
        lblBookInfo.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        lblBookInfo.setForeground(new Color(0, 102, 204));

        JLabel lblReader = new JLabel("读者姓名：");
        lblReader.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        txtReaderName = new JTextField(20);
        txtReaderName.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        lblReaderInfo = new JLabel(" ");
        lblReaderInfo.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        lblReaderInfo.setForeground(new Color(0, 102, 204));

        JLabel lblDays = new JLabel("借阅天数：");
        lblDays.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        spinnerDays = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        spinnerDays.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

        btnConfirm = new JButton("确 认 借 阅");
        btnConfirm.setBackground(new Color(51, 153, 255));
        btnConfirm.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> doBorrow());

        // ---- 布局 ----
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirm, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblBook)
                            .addComponent(lblReader)
                            .addComponent(lblDays))
                        .addGap(12)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBookName, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBookInfo)
                            .addComponent(txtReaderName, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblReaderInfo)
                            .addComponent(spinnerDays, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))))
                .addGap(20)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                // 书名
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBook)
                    .addComponent(txtBookName, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblBookInfo)
                .addGap(8)
                // 读者姓名
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReader)
                    .addComponent(txtReaderName, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblReaderInfo)
                .addGap(8)
                // 借阅天数
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDays)
                    .addComponent(spinnerDays, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addComponent(btnConfirm, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );

        setContentPane(panel);
    }

    /**
     * 设置搜索防抖（300ms延迟，停止打字后才查询）
     */
    private void setupSearchTimers() {
        // 图书搜索防抖
        bookSearchTimer = new javax.swing.Timer(300, e -> searchBook());
        bookSearchTimer.setRepeats(false);
        txtBookName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { selectedBookId = -1; bookSearchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { selectedBookId = -1; bookSearchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { selectedBookId = -1; bookSearchTimer.restart(); }
        });

        // 读者搜索防抖
        readerSearchTimer = new javax.swing.Timer(300, e -> searchReader());
        readerSearchTimer.setRepeats(false);
        txtReaderName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { selectedReaderId = -1; readerSearchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { selectedReaderId = -1; readerSearchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { selectedReaderId = -1; readerSearchTimer.restart(); }
        });
    }

    /**
     * 模糊搜索图书，弹出候选列表供选择
     */
    private void searchBook() {
        String keyword = txtBookName.getText().trim();
        if (keyword.isEmpty()) { lblBookInfo.setText(" "); return; }

        List<Book> results = new BookDao().search("书名", keyword);
        if (results.isEmpty()) {
            lblBookInfo.setText("未找到匹配图书");
            lblBookInfo.setForeground(new Color(255, 51, 51));
            return;
        }

        // 单条结果直接选中
        if (results.size() == 1) {
            selectBook(results.get(0));
            return;
        }

        // 多条结果弹出选择框
        String[] options = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Book b = results.get(i);
            options[i] = b.getName() + " | " + b.getAuthor() + " | 在馆" + b.getCurrentCount() + "本";
        }
        String choice = (String) JOptionPane.showInputDialog(this, "请选择图书：", "搜索结果",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice != null) {
            int idx = java.util.Arrays.asList(options).indexOf(choice);
            if (idx >= 0) selectBook(results.get(idx));
        }
    }

    private void selectBook(Book book) {
        selectedBookId = book.getId();
        txtBookName.setText(book.getName());
        lblBookInfo.setText(book.getAuthor() + " | 在馆" + book.getCurrentCount() + "本 | ID:" + book.getId());
        if (book.getCurrentCount() <= 0) {
            lblBookInfo.setForeground(new Color(255, 51, 51));
            lblBookInfo.setText(lblBookInfo.getText() + " (库存不足)");
        } else {
            lblBookInfo.setForeground(new Color(0, 153, 0));
        }
    }

    /**
     * 模糊搜索读者，弹出候选列表供选择
     * 重名读者通过ID区分
     */
    private void searchReader() {
        String keyword = txtReaderName.getText().trim();
        if (keyword.isEmpty()) { lblReaderInfo.setText(" "); return; }

        List<Reader> results = new ReaderDao().searchReader(keyword);
        if (results.isEmpty()) {
            lblReaderInfo.setText("未找到匹配读者");
            lblReaderInfo.setForeground(new Color(255, 51, 51));
            return;
        }

        // 单条结果直接选中
        if (results.size() == 1) {
            selectReader(results.get(0));
            return;
        }

        // 多条结果弹出选择框（重名的通过ID区分）
        String[] options = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Reader r = results.get(i);
            options[i] = r.getName() + " (ID:" + r.getId() + ") | " + r.getGender() + " | " + r.getPhone() + " | " + r.getStatus();
        }
        String choice = (String) JOptionPane.showInputDialog(this, "请选择读者：", "搜索结果",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice != null) {
            int idx = java.util.Arrays.asList(options).indexOf(choice);
            if (idx >= 0) selectReader(results.get(idx));
        }
    }

    private void selectReader(Reader reader) {
        selectedReaderId = reader.getId();
        txtReaderName.setText(reader.getName());
        String info = "ID:" + reader.getId() + " | " + reader.getGender() + " | " + reader.getPhone() + " | " + reader.getStatus();
        lblReaderInfo.setText(info);
        if ("已注销".equals(reader.getStatus())) {
            lblReaderInfo.setForeground(new Color(255, 51, 51));
            lblReaderInfo.setText(info + " (无法借书)");
        } else {
            lblReaderInfo.setForeground(new Color(0, 153, 0));
        }
    }

    /**
     * 执行借书操作
     */
    private void doBorrow() {
        if (selectedBookId <= 0) {
            JOptionPane.showMessageDialog(this, "请先搜索并选择一本图书！");
            return;
        }
        if (selectedReaderId <= 0) {
            JOptionPane.showMessageDialog(this, "请先搜索并选择一位读者！");
            return;
        }

        int days = (int) spinnerDays.getValue();
        BorrowDao dao = new BorrowDao();

        if (dao.checkOverdue(selectedReaderId)) {
            JOptionPane.showMessageDialog(this, "借阅失败：该读者有逾期未还书籍，请先处理！");
            return;
        }
        if (!dao.checkLimit(selectedReaderId)) {
            JOptionPane.showMessageDialog(this, "借阅失败：该读者借阅数量已达上限（5本）！");
            return;
        }

        String borrowDate = java.time.LocalDate.now().toString();
        String returnDate = java.time.LocalDate.now().plusDays(days).toString();

        boolean success = dao.borrowBook(selectedBookId, selectedReaderId, borrowDate, returnDate);
        if (success) {
            JOptionPane.showMessageDialog(this, "借阅成功！\n应还日期：" + returnDate);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "借阅失败：读者已注销，或图书库存不足！");
        }
    }
}