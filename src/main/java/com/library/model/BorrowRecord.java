package com.library.model;

public class BorrowRecord {
    private int id;
    private int bookId;
    private int readerId;
    private String bookName;
    private String readerName;
    private String borrowDate;
    private String returnDate;
    private String status;
    private double fine;

    public BorrowRecord() {}

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
