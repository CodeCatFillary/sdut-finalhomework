# 后端接口说明

基础地址

```text
http://localhost:8080
```

除登录 注册和 SpringAI 问答外  业务接口默认需要请求头

```text
Authorization: Bearer 登录返回的token
```

统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

SpringAI 问答为了兼容当前前端  直接返回

```json
{
  "answer": "回答内容",
  "suggestions": ["继续追问"]
}
```

## 认证接口

### 登录

`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "123456"
}
```

### 注册

`POST /api/auth/register`

```json
{
  "username": "test001",
  "password": "123456",
  "name": "测试用户",
  "role": "普通用户",
  "department": "个人办事端",
  "phone": "13800000000"
}
```

### 退出登录

`POST /api/auth/logout`

## 政务事项管理

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | /api/matters | 事项列表  支持 category status keyword |
| GET | /api/matters/{id} | 事项详情 |
| POST | /api/matters | 新增事项 |
| PUT | /api/matters/{id} | 修改事项 |
| DELETE | /api/matters/{id} | 删除事项 |
| POST | /api/matters/{id}/publish | 上线或下线事项 |
| GET | /api/matters/categories | 分类列表 |
| POST | /api/matters/categories | 新增分类 |

新增事项示例

```json
{
  "name": "施工许可备案",
  "category": "市场准入",
  "department": "住建窗口",
  "limit": "5个工作日",
  "status": "草稿",
  "publishChannel": "待发布",
  "serviceObject": "企业法人",
  "conditions": "材料齐全且符合法定条件",
  "description": "施工许可备案事项",
  "materials": [
    {"name": "备案申请表", "required": true}
  ],
  "flow": [
    {"name": "申请提交"},
    {"name": "窗口受理"}
  ]
}
```

## 办事申请管理

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | /api/applications | 申请列表  支持 status keyword |
| GET | /api/applications/{id} | 申请详情 |
| GET | /api/applications/{id}/status | 状态跟踪 |
| POST | /api/applications | 在线提交申请 |
| POST | /api/applications/{id}/accept | 窗口受理 |
| POST | /api/applications/{id}/materials/reject | 材料退回补正 |
| POST | /api/applications/{id}/supplements | 重新提交补正材料 |

提交申请示例

```json
{
  "applicant": "张明",
  "phone": "13800000000",
  "matter": "营业执照变更登记",
  "idNo": "370100199901010011",
  "description": "企业经营范围和注册地址需要同步变更",
  "files": [
    {"name": "变更登记申请书.pdf", "size": "1.2MB"}
  ]
}
```

## 审批流程管理

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | /api/approval/flows | 流程列表 |
| GET | /api/approval/flows/{id} | 流程详情 |
| POST | /api/approval/flows | 新增流程 |
| PUT | /api/approval/flows/{id} | 修改流程 |
| DELETE | /api/approval/flows/{id} | 删除流程 |
| POST | /api/approval/flows/{flowId}/nodes | 新增节点 |
| PUT | /api/approval/flows/{flowId}/nodes/{nodeId} | 修改节点 |
| DELETE | /api/approval/flows/{flowId}/nodes/{nodeId} | 删除节点 |
| GET | /api/approval/records | 审批记录  支持 applicationId flowId |
| POST | /api/approval/records | 新增审批记录 |

审批记录示例

```json
{
  "applicationId": "ZW202607020001",
  "flowId": "flow-business-license",
  "nodeName": "部门复核",
  "levelType": "部门级",
  "approver": "陈主任",
  "department": "行政审批局",
  "status": "办结",
  "opinion": "审核通过 准予办结"
}
```

## 统计分析

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | /api/statistics/overview | 首页统计概览 |
| GET | /api/statistics/summary | 办理统计  支持 startDate endDate category department |
| GET | /api/statistics/efficiency | 效率分析  支持 startDate endDate category department |
| GET | /api/statistics/export | 导出 Excel  参数 type=summary 或 efficiency |

## SpringAI 智能问答

`POST /api/spring-ai/chat`

```json
{
  "question": "办理营业执照变更需要哪些材料",
  "scene": "所需材料"
}
```

返回

```json
{
  "answer": "材料办理建议...",
  "suggestions": ["这个事项需要哪些材料", "审批流程有几个节点"]
}
```
