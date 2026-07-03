# 政务服务管理系统后端

技术栈

```text
Java 17
Spring Boot 3.2.12
Maven
MySQL 8
Redis 7
Flyway
JdbcTemplate
Apache POI
```

功能模块

```text
账号登录注册
Redis token 登录态
政务事项管理
办事申请管理
审批流程管理
统计分析
Excel 导出
SpringAI 智能问答接口
```

## 目录说明

```text
government-service-backend
├─ src/main/java/com/example/govservice
│  ├─ auth          登录 注册 Redis token
│  ├─ matter        政务事项管理
│  ├─ application   办事申请管理
│  ├─ approval      审批流程管理
│  ├─ statistics    统计分析和 Excel 导出
│  ├─ springai      SpringAI 智能问答接口
│  ├─ config        跨域 拦截器 密码加密
│  └─ common        统一返回 异常 ID工具
├─ src/main/resources/db/migration
│  ├─ V1__create_tables.sql
│  └─ V2__seed_data.sql
├─ docker-compose.yml
├─ API.md
└─ DATABASE.md
```

## 本地运行方式

### 1 安装运行环境

需要安装

```text
JDK 17 或更高
Maven 3.6.3 或更高
Docker Desktop 可选  用来启动 MySQL 和 Redis
```

检查命令

```powershell
java -version
mvn -version
docker -v
```

### 2 启动 MySQL 和 Redis

如果电脑装了 Docker Desktop  进入后端项目根目录执行

```powershell
docker compose up -d
```

这会自动启动

```text
MySQL  localhost:3306
数据库 gov_service
账号 root
密码 123456

Redis  localhost:6379
```

如果你不用 Docker  就手动启动自己的 MySQL 和 Redis  然后创建数据库

```sql
create database if not exists gov_service default charset utf8mb4 collate utf8mb4_unicode_ci;
```

再修改

```text
src/main/resources/application.yml
```

里面的 MySQL 和 Redis 地址账号密码

### 3 启动后端

进入后端项目根目录

```powershell
mvn spring-boot:run
```

第一次启动会自动执行 Flyway 建表和初始化演示数据

启动成功后访问

```text
http://localhost:8080/api/health
```

正常会看到

```json
{
  "status": "UP",
  "mysql": 1,
  "redis": "PONG"
}
```

### 4 测试登录

```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"123456"}'
```

返回里的 token 用在后续接口

```text
Authorization: Bearer 你的token
```

### 5 测试 SpringAI 问答

这个接口已经兼容当前前端  不强制登录

```powershell
curl -X POST http://localhost:8080/api/spring-ai/chat `
  -H "Content-Type: application/json" `
  -d '{"question":"办理营业执照变更需要哪些材料","scene":"所需材料"}'
```

### 6 前端联调

先启动后端

```powershell
mvn spring-boot:run
```

再启动前端

```powershell
cd ..\frontend
npm install
npm run dev
```

前端 vite 已经把 `/api` 代理到 `http://localhost:8080`

## 默认账号

```text
admin / 123456
user / 123456
```

## 常见问题

### 端口 3306 被占用

说明你的电脑已经启动了 MySQL

处理方式 1  直接用已有 MySQL  修改 application.yml 的账号密码

处理方式 2  改 docker-compose.yml 的端口

```yaml
ports:
  - "3307:3306"
```

然后 application.yml 改成

```yaml
url: jdbc:mysql://localhost:3307/gov_service
```

### Redis 连接失败

确认 Redis 已启动

```powershell
docker ps
```

能看到 gov-service-redis 才正常

### Flyway 报表已存在

如果你手动建过旧表  可以删除数据库后重新启动

```sql
drop database gov_service;
create database gov_service default charset utf8mb4 collate utf8mb4_unicode_ci;
```

### 前端 SpringAI 仍然走本地兜底回答

检查后端是否启动

```text
http://localhost:8080/api/health
```

再看浏览器 F12 Network 里 `/api/spring-ai/chat` 状态码是否为 200


## 数据库文件

数据库 SQL 文件已经放在项目根目录的 `sql` 文件夹

```text
database.sql                     根目录完整数据库文件  方便直接提交或导入
sql/government_service_mysql.sql  完整数据库文件  包含建库 建表 初始化数据
sql/schema.sql                    只建表
sql/seed.sql                      只插入初始化数据
```

正常运行项目时不需要手动导入 SQL
因为项目使用 Flyway 自动执行

```text
src/main/resources/db/migration/V1__create_tables.sql
src/main/resources/db/migration/V2__seed_data.sql
```

如果需要手动导入数据库
在 Windows PowerShell 进入项目根目录后执行

```powershell
mysql -u root -p < .\database.sql
```

项目自带 docker-compose 的 MySQL 默认密码是 `123456`
