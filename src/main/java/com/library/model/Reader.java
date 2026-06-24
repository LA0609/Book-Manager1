package com.library.model;

/**
 * 读者实体类。
 * 作用：承载一位读者在系统中的完整信息，相当于“读者的电子档案”。
 * 对应数据库 `readers` 表，常用于读者列表展示、新增/修改表单回显、借阅校验等场景。
 */
public class Reader {
    private int id;         // 读者唯一编号，数据库自增主键
    private String cardNo;  // 证件号，通常为18位身份证，用于唯一标识读者
    private String name;    // 读者姓名，用于列表展示与模糊查询
    private String gender;  // 性别，取值如“男/女”，用于档案展示
    private String phone;   // 联系手机号，系统中常做唯一性校验
    private String status;  // 读者状态，如“正常”“已注销”，控制是否可继续借阅

    public Reader() {}

    // 全参构造，便于一次性填充所有字段，常用于测试或数据导入场景
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

    // 性别字段的访问方法，保持与其他字段一致的封装规范
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}