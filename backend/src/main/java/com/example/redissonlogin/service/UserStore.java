package com.example.redissonlogin.service;

import com.example.redissonlogin.model.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版用户存储（演示用，真实项目应替换为数据库）。
 * 同时承担两件事：
 *  1) 提供登录时校验账号密码的数据源（含 admin/manager/viewer 三个演示账号）；
 *  2) 提供 /api/users 用户管理 CRUD 的数据源。
 */
@Component
public class UserStore {

    private final Map<Long, AppUser> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        seed(new AppUser(idGen.getAndIncrement(), "admin",   "123456", "超级管理员", "13800000000", "admin",   "admin@demo.com",   "", 1, now));
        seed(new AppUser(idGen.getAndIncrement(), "manager", "123456", "部门经理",   "13800000001", "manager", "manager@demo.com", "", 1, now));
        seed(new AppUser(idGen.getAndIncrement(), "viewer",  "123456", "访客",       "13800000002", "viewer",  "viewer@demo.com",  "", 1, now));
        seed(new AppUser(idGen.getAndIncrement(), "zhangsan", null, "张三", "13800138000", "user", "zhangsan@demo.com", "", 1, now));
        seed(new AppUser(idGen.getAndIncrement(), "lisi",     null, "李四", "13900139000", "user", "lisi@demo.com",     "", 1, now));
        seed(new AppUser(idGen.getAndIncrement(), "wangwu",   null, "王五", "13700137000", "admin", "wangwu@demo.com",   "", 0, now));
    }

    private void seed(AppUser u) {
        users.put(u.getId(), u);
    }

    public AppUser findByUsername(String username) {
        if (username == null) return null;
        for (AppUser u : users.values()) {
            if (username.equals(u.getUsername())) return u;
        }
        return null;
    }

    public List<AppUser> list(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(users.values());
        }
        String k = keyword.trim().toLowerCase();
        List<AppUser> result = new ArrayList<>();
        for (AppUser u : users.values()) {
            if (u.getUsername() != null && u.getUsername().toLowerCase().contains(k)) {
                result.add(u);
            }
        }
        return result;
    }

    public AppUser getById(Long id) {
        return users.get(id);
    }

    public AppUser save(AppUser u) {
        if (u.getId() == null) u.setId(idGen.getAndIncrement());
        users.put(u.getId(), u);
        return u;
    }

    public boolean delete(Long id) {
        return users.remove(id) != null;
    }

    public int batchDelete(List<Long> ids) {
        int count = 0;
        if (ids == null) return 0;
        for (Long id : ids) {
            if (users.remove(id) != null) count++;
        }
        return count;
    }
}
