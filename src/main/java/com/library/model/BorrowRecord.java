package com.library.model;

/**
 * 借阅记录实体类。
 * 简单来说，这个类是“一次借书行为的凭证”，记录谁在什么时候借了哪本书、何时归还、是否产生罚款。
 * 对应数据库 `borrow_records` 表，同时通过 `bookName` / `readerName` 承载关联查询结果，便于界面直接展示。
 */
public class BorrowRecord {
    private int id;               // 借阅记录唯一编号
    private int bookId;           // 被借图书ID，关联 `books` 表
    private int readerId;         // 借阅读者ID，关联 `readers` 表
    private String bookName;      // 被借图书名称，来源于关联查询，用于列表展示
    private String readerName;    // 借阅读者姓名，来源于关联查询，用于列表展示
    private String borrowDate;    // 借书日期，格式通常为 `yyyy-MM-dd`
    private String returnDate;    // 应还日期，系统据此判断是否逾期并计算罚款
    private String status;        // 状态，常见值：`borrowing`（借出中）、`returned`（已归还）
    private double fine;          // 逾期罚款金额，归还时由系统自动计算并回填

    public BorrowRecord() {}      // 无参构造，JDBC映射与框架反序列化需要

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getReaderId() { return readerId; }
    public void setReaderId(int readerId) { this.readerId = readerId; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getReaderName() { return readerName; }
    public void setReaderName(String readerName) { this.readerName = readerName; }
    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}
