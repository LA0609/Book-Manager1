package com.library.model;

/**
 * 【实体类】User (用户)
 * 
 * 作用：对应数据库中的 users 表。
 * 设计：每个 private 属性对应表中的一个字段。
 * 
 * 为什么要有 Getter/Setter？
 * 为了封装性：外部不能直接访问属性，必须通过方法，这样可以保证数据安全。
 */
public class User {
    // ---------------------------------------------------------
    // 1. 成员变量 (对应数据库字段)
    // ---------------------------------------------------------
    private int id;          // 用户唯一ID (主键)
    private String username; // 登录账号
    private String password; // 登录密码 (实际项目中应加密存储，课设明文即可)
    private String role;     // 角色 (admin: 管理员, user: 普通用户)

    // ---------------------------------------------------------
    // 2. 构造方法
    // ---------------------------------------------------------
    
    // 无参构造：反射机制需要，必须保留
    public User() {}

    // 全参构造：方便一次性创建对象
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ---------------------------------------------------------
    // 3. Getter 和 Setter 方法
    // ---------------------------------------------------------
    
    // 获取 ID
    public int getId() {
        return id;
    }

    // 设置 ID
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    // 重写 toString 方法：打印对象时显示有用的信息，方便调试
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}