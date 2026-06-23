/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.model;

/**
 * 【实体类】Book (图书)
 * 对应数据库 books 表。
 * 注意：currentCount (在馆数量) 是借阅逻辑的关键！
 */
public class Book {
    private int id;             // 图书ID
    private String isbn;        // 国际标准书号
    private String name;        // 书名
    private String author;      // 作者
    private String publisher;   // 出版社
    private int totalCount;     // 总藏书量 (买了多少本)
    private int currentCount;   // 当前在馆量 (还剩多少本没借出)

    public Book() {}

    // 全参构造
    public Book(int id, String isbn, String name, String author, String publisher, int totalCount, int currentCount) {
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.totalCount = totalCount;
        this.currentCount = currentCount;
    }

    // Getter 和 Setter (请自行补全，这里列出关键的)
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    
    public int getCurrentCount() { return currentCount; }
    public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }
    
    @Override
    public String toString() {
        return "Book{name='" + name + "', current=" + currentCount + "/" + totalCount + "}";
    }
}