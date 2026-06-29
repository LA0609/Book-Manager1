package com.library.ui.borrow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.library.model.BorrowRecord;
import com.library.dao.BorrowDao;

/**
 * 【面板】借阅管理主面板
 * 功能：展示借阅记录列表，支持按状态筛选、按读者姓名模糊搜索、办理借书/还书
 */
public class BorrowListPanel extends javax.swing.JPanel {

    /** 读者姓名搜索框 */
    private JTextField txtReaderSearch;

    public BorrowListPanel() {
        initComponents();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());

        // ===== 顶部工具栏 =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));

        // 状态筛选
        JLabel lblStatus = new JLabel("筛选状态：");
        lblStatus.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
        topPanel.add(lblStatus);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"全部", "借出中", "已归还"});
        comboStatus.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        comboStatus.addActionListener(e -> loadData());
        topPanel.add(comboStatus);

        topPanel.add(Box.createHorizontalStrut(10));

        // 读者姓名搜索框
        JLabel lblSearch = new JLabel("读者姓名：");
        lblSearch.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
        topPanel.add(lblSearch);

        txtReaderSearch = new JTextField(12);
        txtReaderSearch.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        // 回车触发搜索
        txtReaderSearch.addActionListener(e -> loadData());
        topPanel.add(txtReaderSearch);

        JButton btnSearch = new JButton("查 询");
        btnSearch.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btnSearch.addActionListener(e -> loadData());
        topPanel.add(btnSearch);

        // 分隔线
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 25));
        topPanel.add(sep);

        // 借书按钮
        JButton btnBorrow = new JButton("办理借书");
        btnBorrow.setBackground(new Color(204, 221, 255));
        btnBorrow.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        btnBorrow.addActionListener(e -> {
            new BorrowBookDialog(null, true).setVisible(true);
            loadData();
        });
        topPanel.add(btnBorrow);

        // 还书按钮
        JButton btnReturn = new JButton("办理还书");
        btnReturn.setBackground(new Color(204, 255, 204));
        btnReturn.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
        btnReturn.addActionListener(e -> {
            new ReturnBookDialog(null, true).setVisible(true);
            loadData();
        });
        topPanel.add(btnReturn);

        add(topPanel, BorderLayout.PAGE_START);

        // ===== 中部表格 =====
        JTable tableBorrow = new JTable();
        tableBorrow.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"记录号", "书名", "读者姓名", "借出日期", "应还日期", "状态", "罚款(元)"}
        ));
        tableBorrow.setRowHeight(28);
        tableBorrow.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
        tableBorrow.getTableHeader().setFont(new Font("Microsoft YaHei UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tableBorrow);
        add(scrollPane, BorderLayout.CENTER);

        // 保存表格引用供loadData使用
        this.tableBorrow = tableBorrow;
        this.comboStatus = comboStatus;
    }

    /** 表格引用 */
    private JTable tableBorrow;
    /** 状态下拉框引用 */
    private JComboBox<String> comboStatus;

    /**
     * 加载借阅数据到表格
     * 支持按状态筛选 + 按读者姓名模糊搜索
     */
    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tableBorrow.getModel();
        model.setRowCount(0);

        String statusFilter = (String) comboStatus.getSelectedItem();
        String readerKeyword = txtReaderSearch != null ? txtReaderSearch.getText().trim() : "";

        // 查询所有记录（带状态筛选）
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
}