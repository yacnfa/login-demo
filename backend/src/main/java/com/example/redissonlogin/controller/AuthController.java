package com.example.redissonlogin.controller;

import com.example.redissonlogin.interceptor.TokenInterceptor;
import com.example.redissonlogin.model.LoginRequest;
import com.example.redissonlogin.model.LoginResponse;
import com.example.redissonlogin.model.UserInfo;
import com.example.redissonlogin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录 / 退出登录 / 当前用户 接口
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录：POST /api/login */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        if (response == null) {
            return Map.of("code", 401, "msg", "账号或密码错误");
        }
        return Map.of(
                "code", 200,
                "msg", "登录成功",
                "token", response.getToken(),
                "userInfo", response.getUserInfo());
    }

    /** 退出登录：POST /api/logout（需要已登录） */
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token);
        return Map.of("code", 200, "msg", "已退出登录");
    }

    /** 获取当前登录用户：GET /api/me（需要已登录，用于验证 token 有效性） */
    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        UserInfo user = (UserInfo) request.getAttribute(TokenInterceptor.currentUserAttrName());
        return Map.of("code", 200, "userInfo", user);
    }

    /** 健康检查（无需登录） */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("code", 200, "status", "UP");
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}
