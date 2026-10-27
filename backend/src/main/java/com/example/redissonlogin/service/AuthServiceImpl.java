package com.example.redissonlogin.service;

import com.example.redissonlogin.constant.RedisKeys;
import com.example.redissonlogin.model.AppUser;
import com.example.redissonlogin.model.LoginResponse;
import com.example.redissonlogin.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录实现。
 * 关键点：token 与其对应的用户信息都保存在 Redis 中（通过 Redisson），
 * 所以本服务重启后，只要 Redis 还保留这些 key，用户就仍然是登录状态。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final RedissonClient redissonClient;
    private final UserStore userStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${auth.token.ttl-seconds:1800}")
    private long ttlSeconds;

    /** 角色 -> 权限映射（前端据此做按钮级权限控制） */
    private static final Map<String, List<String>> ROLE_PERMISSIONS = Map.of(
            "admin",   List.of("user:add", "user:edit", "user:delete"),
            "manager", List.of("user:edit"),
            "viewer",  List.of()
    );

    public AuthServiceImpl(RedissonClient redissonClient, UserStore userStore) {
        this.redissonClient = redissonClient;
        this.userStore = userStore;
    }

    @Override
    public LoginResponse login(String username, String password) {
        AppUser user = userStore.findByUsername(username);
        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            return null;
        }
        // 生成随机 token
        String token = UUID.randomUUID().toString().replace("-", "");
        List<String> permissions = ROLE_PERMISSIONS.getOrDefault(user.getRole(), List.of());
        UserInfo userInfo = new UserInfo(user.getUsername(), user.getRole(), permissions);

        // 写入 Redis：key = auth:token:<token>，value = 用户信息 JSON，带过期时间
        RBucket<String> bucket = redissonClient.getBucket(RedisKeys.TOKEN_KEY_PREFIX + token);
        try {
            bucket.set(toJson(userInfo), ttlSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化用户信息失败", e);
        }
        return new LoginResponse(token, userInfo);
    }

    @Override
    public UserInfo validate(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        RBucket<String> bucket = redissonClient.getBucket(RedisKeys.TOKEN_KEY_PREFIX + token);
        String json = bucket.get();
        if (json == null) {
            return null; // token 不存在或已过期
        }
        // 滑动续期：每次校验成功都把过期时间重置为 ttlSeconds
        bucket.expire(ttlSeconds, TimeUnit.SECONDS);
        try {
            return toUserInfo(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        redissonClient.getBucket(RedisKeys.TOKEN_KEY_PREFIX + token).delete();
    }

    private String toJson(UserInfo info) throws JsonProcessingException {
        return objectMapper.writeValueAsString(info);
    }

    private UserInfo toUserInfo(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserInfo.class);
    }
}
