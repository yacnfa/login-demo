# login-demo（全栈登录演示）

> Vue 3 前端 + Spring Boot / Redisson 后端：**登录 token 存进 Redis，重启服务后登录态不丢。**

本仓库把前端与后端放在同一个仓库里，方便一起看、一起跑：

```
login-demo/
├── frontend/   # Vue 3 + Element Plus 工作中台（work-admin）
└── backend/    # Spring Boot 3 + Redisson 登录后端（redisson-login-demo）
```

- 前端：`frontend/` —— 登录、角色路由守卫、动态菜单、按钮级权限、用户管理 CRUD、ECharts 仪表盘。
- 后端：`backend/` —— 登录生成 token 并写入 Redis（`auth:token:<token>`，带 TTL + 滑动续期），拦截器校验 `Authorization: Bearer <token>`；并提供用户管理接口。

## 为什么“重启后不丢登录态”？

token 与用户信息存在 **Redis**（独立进程），与 Spring Boot 解耦。重启 Spring Boot 不会清掉 Redis 里的 key，
因此旧 token 在 TTL 内依旧有效 —— 登录态保留。每次校验还会滑动续期（重置 TTL）。

## 快速开始

### 1. 准备 Redis

```bash
# 方式 A：Docker（backend 已提供 compose）
cd backend && docker compose up -d redis

# 方式 B：本机 Redis
redis-server
```

### 2. 启动后端（8080）

```bash
cd backend
mvn spring-boot:run        # 或 mvn clean package && java -jar target/redisson-login-demo-1.0.0.jar
```

### 3. 启动前端（8081，/api 代理到后端 8080）

```bash
cd frontend
npm install
npm run serve              # 打开 http://localhost:8081
```

### 演示账号（密码均为 123456）

| 账号 | 角色 | 用户管理权限 |
|---|---|---|
| admin | 管理员 | 新增 / 编辑 / 删除 |
| manager | 经理 | 仅编辑 |
| viewer | 访客 | 无（连“用户管理”菜单都没有） |

登录后 token 存浏览器 localStorage，并以 `Authorization: Bearer <token>` 调后端；
后端把 token 存 Redis，**刷新页面或重启后端（Redis 不重启）都不会掉登录**。

## 验证“重启不丢登录态”

1. 登录拿到 token，访问 `/api/me` 正常返回。
2. **重启后端 Spring Boot**（Redis 保持运行）。
3. 不重新登录，直接拿旧 token 再访问 `/api/me` —— 依然 200 ✅

## 说明

- 账号密码为演示硬编码，生产应接入数据库并校验加密密码。
- 前端 `frontend/` 与后端 `backend/` 原本是两个独立仓库，已合并到本仓库统一管理。
