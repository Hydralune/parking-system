# 数据库说明文档

## 概览

本项目使用 **MySQL 8.0+** 作为主数据库，数据库名为 `parking_system`。
ORM 框架为 **MyBatis-Plus**，连接池使用 **HikariCP**。

---

## 数据库脚本

脚本位于项目根目录的 `database/` 文件夹下，共两个文件，**必须按顺序执行**：

| 顺序 | 文件 | 说明 |
|------|------|------|
| 1 | `database/create_tables.sql` | 创建数据库及所有表结构、索引、外键约束 |
| 2 | `database/init_data.sql` | 插入初始测试数据（用户、停车场、车位、计费规则、会员） |

---

## 数据库初始化步骤

### 前提条件

- MySQL 8.0+ 已安装并运行
- root 用户密码已知（默认配置为 `1234`，可在 `backend/src/main/resources/application.yml` 中修改）

### 方式一：命令行

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行建表脚本
source /path/to/parking-system/database/create_tables.sql

# 3. 执行初始数据脚本
source /path/to/parking-system/database/init_data.sql
```

或一次性执行：

```bash
mysql -u root -p < database/create_tables.sql
mysql -u root -p parking_system < database/init_data.sql
```

### 方式二：图形化工具（Navicat / MySQL Workbench）

1. 连接到本地 MySQL 实例
2. 新建查询，依次打开并执行 `create_tables.sql`
3. 再执行 `init_data.sql`

---

## 数据库连接配置

配置文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parking_system?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 1234   # 根据实际密码修改
```

---

## 数据表结构

### user（用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| user_type | TINYINT | 0-普通用户，1-管理员 |
| status | TINYINT | 0-禁用，1-启用 |

### parking_lot（停车场表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(100) | 停车场名称 |
| address | VARCHAR(255) | 地址 |
| longitude / latitude | DECIMAL(10,7) | 经纬度坐标 |
| total_spots | INT | 总车位数 |
| available_spots | INT | 剩余可用车位数 |
| hourly_rate | DECIMAL(10,2) | 每小时费用 |
| opening_time / closing_time | TIME | 营业时间 |
| status | TINYINT | 0-关闭，1-营业中 |

### parking_spot（车位表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| parking_lot_id | BIGINT | 所属停车场（外键） |
| spot_number | VARCHAR(20) | 车位编号 |
| spot_type | TINYINT | 1-小型车，2-大型车，3-残疾人专位 |
| status | TINYINT | 0-维护中，1-可用，2-已预订，3-已占用 |

### pricing_rule（计费规则表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| parking_lot_id | BIGINT | 所属停车场（外键） |
| pricing_type | TINYINT | 1-按时，2-按次，3-包月 |
| hourly_rate | DECIMAL(10,2) | 每小时费用 |
| daily_cap | DECIMAL(10,2) | 每日费用上限 |
| discount_rate | DECIMAL(5,2) | 优惠费率（如 0.8 表示八折） |

### reservation（预约订单表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户（外键） |
| parking_lot_id | BIGINT | 停车场（外键） |
| parking_spot_id | BIGINT | 车位（外键） |
| start_time / end_time | DATETIME | 预约时间段 |
| reservation_status | TINYINT | 1-待确认，2-已确认，3-已取消，4-已完成，5-已过期 |
| payment_status | TINYINT | 0-未支付，1-已支付，2-已退款 |
| total_fee | DECIMAL(10,2) | 总费用 |
| license_plate | VARCHAR(20) | 车牌号 |

### member（会员信息表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户（外键，唯一） |
| member_level | TINYINT | 1-普通，2-银卡，3-金卡，4-钻石 |
| points | INT | 积分 |
| discount_rate | DECIMAL(5,2) | 会员折扣 |
| valid_from / valid_to | DATE | 会员有效期 |

---

## 初始测试账号

> 所有账号初始密码均为 `123456`（BCrypt 加密存储）

| 用户名 | 类型 | 说明 |
|--------|------|------|
| admin | 管理员 | 超级管理员 |
| manager | 管理员 | 管理员 |
| user1 | 普通用户 | 银卡会员，1500积分 |
| user2 | 普通用户 | 金卡会员，3200积分 |

---

## 初始测试数据

- 停车场：3 个（北京CBD、上海陆家嘴、深圳科技园）
- 车位：共 35 个（分布于 3 个停车场）
- 计费规则：4 条（含夜间优惠规则）
- 会员：2 条（对应 user1、user2）
