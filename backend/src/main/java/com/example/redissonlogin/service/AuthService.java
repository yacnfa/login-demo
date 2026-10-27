package com.example.redissonlogin.service;

import com.example.redissonlogin.model.LoginResponse;
import com.example.redissonlogin.model.UserInfo;

/** 登录与 token 校验逻辑 */
public interface AuthService {

    /** 校验账号密码，成功返回带 token 的响应，失败返回 null */
    LoginResponse login(String username, String password);

    /** 校验 token 是否有效；有效则刷新其过期时间（滑动窗口），并返回用户信息；无效返回 null */
    UserInfo validate(String token);

    /** 退出登录：删除 Redis 中的 token */
    void logout(String token);
}
