# redisson-login-demo

> Spring Boot + Redisson 登录示例：**登录 token 保存在 Redis 中，重启服务后登录态不丢失。**

这是一个最小可运行的演示项目，核心目标只有一个 —— 证明把登录 token 存进 Redis（用 Redisson 客户端）之后，
即使把后端 Spring Boot 服务**重启**，只要 Redis 中的 token 还没过期，用户依然处于登录状态。

## 为什么能“重启后不丢登录态”？

- 很多教程用 `HashMap` 或 Session 在内存里存 token，**服务一重启内存清空，全部掉登录**。
- 本项目用 **Redisson** 把 `token -> 用户信息(JSON)` 写进 **Redis**（`auth:token:<token>`，带 TTL）。
- Redis 是独立进程，与 Spring Boot 服务解耦。重启 Spring Boot 不会动 Redis 里的 key，
  因此 `validate(token)` 依旧能从 Redis 读到用户信息 —— **登录态保留**。
- 每次校验成功还会对 token 做**滑动续期**（重置 TTL），避免长时间操作被踢。

## 技术栈

- Java 21
- Spring Boot 3.3
- Redisson 4.8（Redis Java 客户端）
- 内存演示账号（真实项目应替换为数据库 + 加密密码）

## 目录结构

```
redisson-login-demo/
├── pom.xml
├── docker-compose.yml          # 一键起本地 Redis
├── README.md
└── src/main/
    ├── java/com/example/redissonlogin/
    │   ├── RedissonLoginDemoApplication.java
    │   ├── config/RedissonConfig.java       # RedissonClient 配置
    │   ├── config/WebConfig.java            # 注册拦截器
    │   ├── interceptor/TokenInterceptor.java # 校验 Authorization: Bearer <token>
    │   ├── model/                           # 请求/响应/用户实体
    │   ├── service/AuthService + Impl        # 登录、校验、退出、滑动续期
    │   ├── service/UserStore                 # 内存用户存储（登录校验 + 用户管理共用）
    │   └── controller/                      # AuthController / UserController / ProtectedController
    └── resources/
        ├── application.yml                  # Redis 连接配置
        └── static/index.html                # 浏览器演示页
```

## 快速开始

### 1. 准备 Redis

任选其一：

```bash
# 方式 A：用 Docker（本仓库已提供 compose）
docker compose up -d redis

# 方式 B：本机已装 Redis，直接启动
redis-server

# 方式 C：用云 Redis / 远程 Redis，改 application.yml 的 host/port/password
```

### 2. 启动项目

```bash
mvn spring-boot:run
# 或先打包再运行
mvn clean package -DskipTests
java -jar target/redisson-login-demo-1.0.0.jar
```

启动后访问：http://localhost:8080/ 即可看到演示页。

### 3. 演示账号（密码均为 `123456`）

| 账号 | 角色 | 说明 |
|---|---|---|
| admin | admin | 管理员 |
| manager | manager | 经理 |
| viewer | viewer | 访客 |

## 接口说明

| 方法 | 路径 | 需要登录 | 说明 |
|---|---|---|---|
| POST | `/api/login` | 否 | 登录，返回 `token` |
| POST | `/api/logout` | 是 | 退出（删除 Redis 中的 token） |
| GET | `/api/me` | 是 | 获取当前登录用户 |
| GET | `/api/hello` | 是 | 受保护接口示例 |
| GET | `/api/health` | 否 | 健康检查 |
| GET | `/api/users?keyword=` | 是 | 用户列表 |
| POST | `/api/users` | 是 | 新增用户 |
| PUT | `/api/users/{id}` | 是 | 编辑用户 |
| DELETE | `/api/users/{id}` | 是 | 删除用户 |
| POST | `/api/users/batch-delete` | 是 | 批量删除，body: `{"ids":[1,2]}` |

受保护接口在请求头携带：`Authorization: Bearer <token>`

## curl 示例

```bash
# 登录
curl -s -X POST http://localhost:8080/api/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'

# 假设拿到 token=abc123
TOKEN=abc123

# 访问受保护接口
curl -s http://localhost:8080/api/me -H "Authorization: Bearer $TOKEN"
curl -s http://localhost:8080/api/hello -H "Authorization: Bearer $TOKEN"

# 没有 token 访问受保护接口 -> 401
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/hello
```

## 验证“重启不丢登录态”

1. 登录拿到 token，调用 `/api/me` 正常返回。
2. **重启 Spring Boot 服务**（Redis 保持运行）。
3. 不重新登录，直接拿旧 token 再调 `/api/me` 或 `/api/hello` —— 依然 200。✅
4. 若也重启了 Redis，或等超过 TTL（默认 30 分钟）未续期，则 token 失效返回 401。

## 可调整参数

`application.yml`：

```yaml
spring.data.redis.host / port / password   # Redis 连接
auth.token.ttl-seconds                      # token 有效期（秒，默认 1800）
```

## 生产环境提示

- 本项目账号密码为演示硬编码，生产应接入数据库并校验 BCrypt 等加密密码。
- token 用 UUID 足够随机；若要更强可用 JWT + Redis 双写，或用 Redis 存 JWT 黑名单。
- 建议在 Redis 侧也配置持久化（AOF/RDB），避免 Redis 重启导致所有用户掉线。

## 前端联调（配合 work-admin）

本项目已与前端中台 `work-admin`（Vue 3 + Element Plus）打通：登录走本后端、用户管理 CRUD 也走本后端的 `/api/users`。

```bash
# 1) 启动 Redis（本仓库 docker-compose 或本机 redis-server）
# 2) 启动本后端（8080）
mvn spring-boot:run

# 3) 在 work-admin 目录启动前端（8081，/api 已代理到 8080）
cd work-admin
npm install
npm run serve
# 浏览器打开 http://localhost:8081
```

前端登录页内置 `admin / manager / viewer` 三个演示账号（密码均 `123456`），
登录后 token 存入浏览器 localStorage，并通过 `Authorization: Bearer <token>` 调用后端；
后端 token 存 Redis，因此刷新页面或重启后端（Redis 不重启）都不会掉登录。
