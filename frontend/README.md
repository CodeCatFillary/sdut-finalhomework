# 政务服务管理系统前端页面

该前端基于 Vue3 Vite 和 vue-router 实现登录注册 路由守卫以及以下模块页面

- 0 账号注册与登录页面
- 路由守卫 未登录自动拦截业务页面
- 1 政务事项管理模块
- 2 办事申请管理模块
- 3 审批流程管理模块
- 4 统计分析模块
- 5 SpringAI 智能问答模块

## 页面内容


### 账号注册与登录

- 新增登录页面 `/login`
- 新增注册页面 `/register`
- 新增 vue-router 路由配置
- 新增全局路由守卫
- 未登录访问 `/matter` `/application` `/approval` `/statistics` `/spring-ai` 会自动跳转登录页
- 登录成功后按照 redirect 参数回到原访问页面
- 登录状态使用 localStorage 保存 便于前端演示
- 默认测试账号 admin / 123456 和 user / 123456

### 政务事项管理

- 事项录入管理
- 录入政务办理事项名称 办理流程 所需材料 办理时限 受理条件 服务对象和办理部门
- 事项分类管理
- 按户籍 社保 税务 市场准入 工程建设等业务类型筛选和维护事项
- 事项发布管理
- 支持事项上线 下线和统一发布到政务服务平台
- 用例与用例规约描述
- 展示事项录入管理 事项分类管理 事项发布管理的参与者 前置条件 基本流程 备选流程和后置条件

### 办事申请管理

- 在线申请提交
- 用户填写申请人 联系方式 证件号码 办理事项 申请说明并上传材料
- 申请状态跟踪
- 实时展示申请编号 当前节点 受理 审核 办结状态和办理时间线
- 材料补正管理
- 展示不合格材料 补正原因 支持标记补正后重新进入审核

### 审批流程管理

- 流程配置管理
- 不同政务事项流程切换
- 审批节点新增 删除 上移 下移
- 节点名称 层级 科室 角色 审批人 时限 流转条件配置
- 科室级和部门级多级审批展示
- 审批记录追踪表格
- 审批人员 审批时间 审批意见完整展示

### 统计分析

- 事项办理统计
- 申请数量 受理数量 办结数量 补正数量统计
- 办结率 受理率 平均办理时长统计
- 办事效率分析
- 按事项类型 部门 日期筛选
- 支持导出事项办理统计 Excel
- 支持导出办事效率分析 Excel

### SpringAI 智能问答

- 智能问答聊天窗口
- 五类问答场景切换
- 常见问题快捷提问
- SpringAI 后端接口预留
- 接口未完成时自动启用前端兜底回答

## 路由说明

```text
/#/login        登录页
/#/register     注册页
/#/matter       政务事项管理
/#/application  办事申请管理
/#/approval     审批流程管理
/#/statistics   统计分析
/#/spring-ai     SpringAI 智能问答
```

## 后端接口约定

政务事项接口预留

```text
GET  /api/matters
POST /api/matters
GET  /api/matters/categories
POST /api/matters/publish
```

办事申请接口预留

```text
GET  /api/applications
POST /api/applications
GET  /api/applications/status
POST /api/applications/supplements
```

审批流程接口预留

```text
GET  /api/approval/flows
POST /api/approval/flows
GET  /api/approval/records
```

统计分析接口预留

```text
GET /api/statistics/summary
GET /api/statistics/export
```

智能问答接口

```text
POST /api/spring-ai/chat
```

当前页面使用前端模拟数据 可以在后端接口完成后替换数据来源

账号接口后续可替换为

```text
POST /api/auth/login
POST /api/auth/register
GET  /api/auth/profile
POST /api/auth/logout
```

## 运行方式

```bash
cd frontend
npm install
npm run dev
```

## 构建方式

```bash
cd frontend
npm run build
```
