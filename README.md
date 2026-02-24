# 停车场管理系统

基于 Spring Boot 3 + Vue 3 的全栈停车场预约管理系统，支持车位预约、会员管理、在线支付等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis 7.0+（可选） |

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone https://github.com/Hydralune/parking-system.git
cd parking-system
```

### 2. 初始化数据库

```bash
mysql -u root -p < database/create_tables.sql
mysql -u root -p parking_system < database/init_data.sql
```

### 3. 配置环境变量

后端通过环境变量注入敏感配置，**不要直接修改 `application.yml`**。

```bash
# Linux / macOS
export DB_HOST=localhost
export DB_PORT=3306
export DB_USERNAME=root
export DB_PASSWORD=你的MySQL密码
export JWT_SECRET=替换为至少32位的随机强密钥

# Windows PowerShell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
$env:JWT_SECRET="替换为至少32位的随机强密钥"
```

也可以在项目根目录创建 `.env` 文件（已加入 `.gitignore`，不会提交）：

```env
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码
JWT_SECRET=替换为至少32位的随机强密钥
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

服务启动后监听 `http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问 `http://localhost:3000`

## 默认测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| manager | 123456 | 停车场管理员 |
| user1 | 123456 | 普通用户 |

> 生产环境部署前请修改所有默认密码。

## 项目结构

```
parking-system/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── database/         # SQL 初始化脚本
└── docs/             # 详细文档
    ├── environment-setup.md    # 开发环境配置
    ├── backend-startup-guide.md # 后端启动指南
    └── database.md             # 数据库说明
```

## 详细文档

- [开发环境配置](docs/environment-setup.md)
- [后端启动指南](docs/backend-startup-guide.md)
- [数据库说明](docs/database.md)
