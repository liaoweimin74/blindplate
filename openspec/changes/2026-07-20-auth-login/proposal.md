## Why

项目骨架已完成，前端登录页（Login.vue）和后端认证接口（/api/v1/auth/login）已搭建，但存在以下问题：

1. **Token 未持久化** — 登录后 token 仅存储在内存（Pinia store），刷新页面即丢失，用户需重复登录
2. **路由守卫未生效** — router/index.ts 的 beforeEach 守卫逻辑未正确检查 token，未登录用户可直接访问受保护页面
3. **登录页未联调** — Login.vue 的表单提交未对接后端 API，缺少错误提示、loading 状态
4. **Axios 拦截器不完整** — request.ts 的请求拦截器未正确附加 Authorization header，响应拦截器未处理 401 跳转

## What Changes

**从**: 登录页面存在但无法使用，token 不持久化，路由守卫形同虚设

**到**: 完整可用的登录流程 — 输入账号密码 → 调用后端 → token 持久化到 localStorage → 路由守卫拦截未登录用户 → 401 自动跳转登录页

**具体变更：**

1. **后端 LoginResponse 增强** — 返回用户基本信息（用户名、角色），前端可展示
2. **前端 Token 持久化** — 登录成功后将 token + userInfo 存入 localStorage，刷新后自动恢复
3. **路由守卫完善** — beforeEach 正确检查 token 存在性，白名单（/login）放行，其余重定向
4. **登录页联调** — Login.vue 对接 /api/v1/auth/login，处理错误（用户名错误、密码错误、网络异常）
5. **Axios 拦截器修复** — 请求拦截器从 localStorage 读取 token 并附加 Authorization header，响应拦截器处理 401 清除 token 并跳转登录

## Capabilities

### Modified Capabilities

- `user-auth`: 增强登录流程，支持 token 持久化和路由守卫

## Impact

**代码影响：**
- 后端：修改 `auth.dto.LoginResponse` 增加用户信息字段
- 前端：修改 `stores/auth.ts`、`router/index.ts`、`api/request.ts`、`views/Login.vue`

**依赖影响：**
- 无新依赖

**部署影响：**
- 无额外部署要求
