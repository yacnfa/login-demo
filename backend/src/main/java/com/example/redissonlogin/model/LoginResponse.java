package com.example.redissonlogin.model;

/** 登录成功响应：返回 token 和用户信息 */
public class LoginResponse {
    private String token;
    private UserInfo userInfo;

    public LoginResponse() {}

    public LoginResponse(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
}
