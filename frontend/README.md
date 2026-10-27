# work-admin

> Vue 3 + Element Plus 工作中台，**已对接真实后端 `redisson-login-demo`**。

课程练习项目，最终形态包含：登录、角色路由守卫、动态菜单、按钮级权限、用户管理（CRUD + 批量删除）、ECharts 仪表盘。
之前登录与用户数据走前端 Mock；现已改为调用后端真实接口（见下方说明）。

## 与后端联调

后端仓库：<https://github.com/yacnfa/redisson-login-demo>（Spring Boot 3 + Redisson，token 存 Redis）。

前端通过 `vue.config.js` 的 devServer 代理，把 `/api` 转发到后端 `http://localhost:8080`（前端本身跑在 `8081`，避免与后端 8080 冲突）。

### 启动

```bash
npm install
npm run serve          # 前端 http://localhost:8081

# 另开终端启动后端（需先起 Redis）
cd ../redisson-login-demo
mvn spring-boot:run    # 后端 http://localhost:8080
```

打开 http://localhost:8081 即可。

### 演示账号（密码均为 123456）

| 账号 | 角色 | 用户管理权限 |
|---|---|---|
| admin | 管理员 | 新增 / 编辑 / 删除（全权限） |
| manager | 经理 | 仅编辑 |
| viewer | 访客 | 无（连“用户管理”菜单都没有） |

登录成功后 token 保存在浏览器 localStorage，并以 `Authorization: Bearer <token>` 请求头调用后端；
后端把 token 存进 Redis，**刷新页面或重启后端（Redis 不重启）都不会掉登录**。

## 构建

```bash
npm run build          # 产物输出到 dist/
```
