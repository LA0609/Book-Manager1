/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.model;

/**
 * 图书实体类。
 * 作用：充当“图书的数据容器”，在数据库和界面之间传递一本书的完整信息。
 * 对应表：`books`。
 * 关键字段：`currentCount` 代表当前可借数量，是系统判断能否借出的核心依据。
 */
public class Book {
    private int id;             // 图书唯一编号，用于数据库主键关联与界面定位
    private String isbn;        // 国际标准书号，用于精确标识图书版本
    private String name;        // 书名，界面展示与模糊查询的主要字段
    private String author;      // 作者，用于按作者筛选与展示
    private String publisher;   // 出版社，用于展示与条件检索
    private int totalCount;     // 总藏书量，代表该书在系统中的总库存数
    private int currentCount;   // 当前可借数量，借书时判断是否还有库存的关键字段

    public Book() {}

    // 全参构造，便于在测试或批量数据组装时一次性创建对象
    public Book(int id, String isbn, String name, String author, String publisher, int totalCount, int currentCount) {
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.totalCount = totalCount;
        this.currentCount = currentCount;
    }

    // Getter 和 Setter，用于在不破坏封装的前提下读写私有字段
    
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