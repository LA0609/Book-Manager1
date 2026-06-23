package com.library.model;

/**
 * 【实体类】Reader (读者)
 * 对应数据库 readers 表。
 */
public class Reader {
    private int id;         // 读者编号（对应表格第一列）
    private String cardNo;  // 证件号（表格第五列）
    private String name;    // 姓名（第二列）
    private String gender;  // 性别（第三列，新增）
    private String phone;   // 电话（第四列）
    private String status;  // 状态（第六列）

    public Reader() {}

    // 全参构造，包含gender性别字段
    public Reader(int id, String cardNo, String name, String gender, String phone, String status) {
        this.id = id;
        this.cardNo = cardNo;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.status = status;
    }

    // Getter 和 Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // 性别get/set
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}