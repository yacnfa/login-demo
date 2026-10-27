package com.example.redissonlogin.model;

/** 内部演示用户（真实项目应来自数据库） */
public class DemoUser {
    private final String username;
    private final String password;
    private final String role;

    public DemoUser(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
