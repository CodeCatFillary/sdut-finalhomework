# MySQL 数据库表设计

## 数据库文件位置

完整数据库文件已经补到项目根目录

```text
database.sql
sql/government_service_mysql.sql
sql/schema.sql
sql/seed.sql
```

后端启动时也会通过 Flyway 自动执行以下文件

```text
src/main/resources/db/migration/V1__create_tables.sql
src/main/resources/db/migration/V2__seed_data.sql
```


本项目使用 MySQL 作为业务主库  Redis 负责登录态和统计缓存

## 账号与认证

| 表名 | 说明 |
|---|---|
| sys_user | 系统用户表  包含账号 密码密文 姓名 角色 部门 手机 状态 |

## 政务事项管理

| 表名 | 说明 |
|---|---|
| matter_category | 事项分类字典  如户籍 社保 税务 市场准入 |
| gov_matter | 政务事项主表  记录事项名称 分类 部门 办理时限 发布状态 服务对象 受理条件 说明 |
| gov_matter_material | 事项材料清单 |
| gov_matter_flow_step | 事项办理流程步骤 |

## 办事申请管理

| 表名 | 说明 |
|---|---|
| service_application | 办事申请主表  记录申请人 事项 状态 当前节点 提交时间 办结时间 |
| application_file | 申请材料文件表  记录材料核验状态和意见 |
| application_timeline | 申请状态轨迹表  支撑状态跟踪 |

## 审批流程管理

| 表名 | 说明 |
|---|---|
| approval_flow | 审批流程配置主表 |
| approval_node | 审批流程节点表  支持科室级 部门级 多级审批 |
| approval_record | 审批记录表  记录审批人员 审批时间 审批意见 |

## 统计分析

统计分析不单独建宽表  直接基于 service_application 和 gov_matter 聚合

接口返回内容

| 接口 | 数据来源 |
|---|---|
| /api/statistics/summary | service_application 聚合申请数量 受理数量 办结数量 补正数量 |
| /api/statistics/efficiency | service_application 的 submit_time 和 completed_time 计算办理时长 |
| /api/statistics/export | Apache POI 生成 xlsx |

## Redis Key 设计

| Key | 说明 | 过期时间 |
|---|---|---|
| gov:auth:token:{token} | 登录 token 对应用户信息 | 默认 7200 秒 |
| gov:statistics:summary:* | 办理统计缓存 | 默认 60 秒 |
| gov:statistics:efficiency:* | 效率统计缓存 | 默认 60 秒 |
| gov:spring-ai:chat-history | 智能问答历史记录 最近 100 条 | 7 天 |
