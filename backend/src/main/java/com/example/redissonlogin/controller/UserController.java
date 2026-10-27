package com.example.redissonlogin.controller;

import com.example.redissonlogin.model.AppUser;
import com.example.redissonlogin.service.UserStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理接口（需要已登录的 token，由 TokenInterceptor 统一校验）。
 * 前缀 /api/users，例如：
 *   GET    /api/users?keyword=     列表
 *   POST   /api/users              新增
 *   PUT    /api/users/{id}         编辑
 *   DELETE /api/users/{id}         删除
 *   POST   /api/users/batch-delete 批量删除  body: { "ids": [1,2,3] }
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserStore userStore;

    public UserController(UserStore userStore) {
        this.userStore = userStore;
    }

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword) {
        return Map.of("code", 200, "data", userStore.list(keyword));
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody AppUser user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Map.of("code", 400, "msg", "用户名不能为空");
        }
        user.setId(null);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userStore.save(user);
        return Map.of("code", 200, "msg", "新增成功");
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody AppUser user) {
        if (userStore.getById(id) == null) {
            return Map.of("code", 404, "msg", "用户不存在");
        }
        user.setId(id);
        userStore.save(user);
        return Map.of("code", 200, "msg", "编辑成功");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = userStore.delete(id);
        return ok ? Map.of("code", 200, "msg", "删除成功")
                  : Map.of("code", 404, "msg", "用户不存在");
    }

    @PostMapping("/batch-delete")
    public Map<String, Object> batchDelete(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        int n = (ids == null) ? 0 : userStore.batchDelete(ids);
        return Map.of("code", 200, "msg", "成功删除 " + n + " 个用户");
    }
}
