package com.example.redissonlogin.interceptor;

import com.example.redissonlogin.model.UserInfo;
import com.example.redissonlogin.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * token 校验拦截器。
 * 从 Authorization: Bearer <token> 取出 token，交给 AuthService 校验。
 * 校验通过则把用户信息放入 request attribute，供后续 Controller 使用；
 * 校验失败返回 401。
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String USER_ATTR = "currentUser";
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String header = request.getHeader(AUTH_HEADER);
        String token = resolveToken(header);
        if (token == null) {
            writeUnauthorized(response, "缺少 Authorization 头");
            return false;
        }
        UserInfo userInfo = authService.validate(token);
        if (userInfo == null) {
            writeUnauthorized(response, "token 无效或已过期，请重新登录");
            return false;
        }
        // 校验通过，写入请求属性，供 Controller 读取
        request.setAttribute(USER_ATTR, userInfo);
        return true;
    }

    /** 从 "Bearer <token>" 中解析 token */
    private String resolveToken(String header) {
        if (header == null || !header.toLowerCase().startsWith("bearer ")) {
            return null;
        }
        return header.substring(7).trim();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        java.util.Map.of("code", 401, "msg", message)));
    }

    public static String currentUserAttrName() {
        return USER_ATTR;
    }
}
