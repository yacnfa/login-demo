package com.example.redissonlogin.model;

/** 业务用户实体（真实项目应来自数据库，这里用内存存储做演示） */
public class AppUser {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String role;
    private String email;
    private String avatar;
    private Integer status;     // 1=正常 0=禁用
    private String createTime;

    public AppUser() {}

    public AppUser(Long id, String username, String password, String name,
                   String phone, String role, String email, String avatar,
                   Integer status, String createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.createTime = createTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
