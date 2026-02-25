# 智停停车场管理系统

基于 Spring Boot 3 + Vue 3 的全栈停车场预约管理系统，支持车位可视化选择、在线预约、模拟支付、会员折扣、管理后台等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + ECharts |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT |
| 数据库 | MySQL 8.0+ |

## 功能模块

### 用户端
- 停车场列表查询（实时空位数）
- 车位可视化选择（按区域筛选，只显示可用车位）
- 在线预约（选择时间段、车牌号）
- 模拟支付（支付宝/微信支付选择）
- 我的预约（查看状态、取消、支付）
- 会员信息展示（等级、折扣、积分、有效期）

### 管理后台
- 控制台概览（真实统计数据、ECharts 图表）
- 停车场管理（增删改查）
- 预约管理（查看所有预约、修改状态、支付状态）
- 会员管理（按用户名/手机号搜索用户，设置会员等级）

## 快速开始

### 前置要求

- JDK 21
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone https://gitee.com/altuss/parking-system.git
cd parking-system
```

### 2. 初始化数据库

```bash
# 建表
mysql -u root -p < database/create_tables.sql

# 导入停车场、车位种子数据（3个停车场，620个车位）
mysql -u root -p parking_system < database/seed_data.sql

# 导入用户、预约、会员种子数据
mysql -u root -p parking_system < database/seed_reservations.sql
```

### 3. 配置环境变量

后端通过环境变量注入敏感配置，**不要直接修改 `application.yml`**。

在 IntelliJ IDEA 中：Run → Edit Configurations → Environment variables 填入：

```
DB_USERNAME=你的MySQL用户名
DB_PASSWORD=你的MySQL密码
```

或 Windows PowerShell：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
```

### 4. 初始化测试用户密码

启动后端后，在浏览器控制台执行（只需执行一次）：

```javascript
fetch('http://localhost:8080/api/auth/init-users', { method: 'POST' })
  .then(r => r.json()).then(console.log)
```

### 5. 启动后端

```bash
cd backend
mvn spring-boot:run
```

服务启动后监听 `http://localhost:8080`

### 6. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问 `http://localhost:3000`

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| kkk | 123456 | 普通用户 |
| zhang_wei | 123456 | 普通用户（金卡会员）|
| li_fang | 123456 | 普通用户（银卡会员）|
| wang_jun | 123456 | 普通用户（钻石会员）|

## 项目结构

```
parking-system/
├── backend/                    # Spring Boot 后端
│   └── src/main/java/com/parking/
│       ├── config/             # Security、JWT、Jackson 配置
│       ├── controller/         # REST 接口
│       ├── entity/             # 数据库实体
│       ├── mapper/             # MyBatis-Plus Mapper
│       └── service/            # 业务逻辑
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── api/                # 接口封装
│       ├── router/             # 路由配置
│       ├── stores/             # Pinia 状态管理
│       └── views/              # 页面组件
└── database/
    ├── create_tables.sql       # 建表语句
    ├── seed_data.sql           # 停车场、车位数据
    └── seed_reservations.sql   # 用户、预约、会员数据
```

## 数据库说明

| 表名 | 说明 |
|------|------|
| user | 用户表（普通用户/管理员）|
| parking_lot | 停车场表（3个停车场）|
| parking_spot | 车位表（620个车位，A-L区）|
| reservation | 预约订单表 |
| member | 会员信息表（4个等级）|
| pricing_rule | 计费规则表 |
