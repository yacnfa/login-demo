package com.example.redissonlogin.controller;

import com.example.redissonlogin.interceptor.TokenInterceptor;
import com.example.redissonlogin.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 受保护接口示例：只有携带有效 token 才能访问。
 * 验证“重启服务后，Redis 中的 token 仍然有效，接口依旧可用”。
 */
@RestController
@RequestMapping("/api")
public class ProtectedController {

    @GetMapping("/hello")
    public Map<String, Object> hello(HttpServletRequest request) {
        UserInfo user = (UserInfo) request.getAttribute(TokenInterceptor.currentUserAttrName());
        return Map.of(
                "code", 200,
                "msg", "你好，" + user.getUsername() + "！你的角色是 " + user.getRole(),
                "role", user.getRole());
    }
}
