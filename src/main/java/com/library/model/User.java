package com.library.model;

/**
 * 用户实体类。
 * 作用：表示系统中的登录账号信息，相当于“用户的电子档案”。
 * 对应数据库 `users` 表，登录验证、角色判断都依赖该实体承载查询结果。
 * 设计说明：字段与表列一一对应，并通过 Getter/Setter 保持封装性。
 */
public class User {
    // ---------------------------------------------------------
    // 1. 成员变量 (对应数据库字段)
    // ---------------------------------------------------------
    private int id;          // 用户唯一编号，数据库自增主键
    private String username; // 登录账号，登录时按该字段精确查询
    private String password; // 登录密码，本项目课设阶段为明文校验
    private String role;     // 角色，用于区分权限，如 `admin` 表示管理员

    // ---------------------------------------------------------
    // 2. 构造方法
    // ---------------------------------------------------------
    
    // 无参构造，框架反射和结果集映射场景需要
    public User() {}

    // 全参构造，便于在测试或批量初始化时一次性赋值
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ---------------------------------------------------------
    // 3. Getter 和 Setter 方法
    // ---------------------------------------------------------
    
    // 获取用户编号
    public int getId() {
        return id;
    }

    // 设置用户编号
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
    
    // 重写 toString，便于日志和调试时快速看到关键信息
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}