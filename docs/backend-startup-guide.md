# 后端启动指南（IntelliJ IDEA）

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 运行环境 |
| Spring Boot | 3.2.0 | 后端框架 |
| Maven | 3.6+ | 依赖管理与构建工具 |
| MyBatis-Plus | 3.5.5 | ORM 框架，负责数据库操作 |
| MySQL | 8.0+ | 主数据库 |
| JWT | 0.11.5 | 用户登录认证 |
| HikariCP | 内置 | 数据库连接池 |

---

## 第一步：确认 JDK 版本

项目要求 **JDK 17 或以上**。

在 IntelliJ 中检查：
1. 顶部菜单 `File` → `Project Structure`（快捷键 `Ctrl+Alt+Shift+S`）
2. 左侧选 `Project`
3. `SDK` 一栏确认选择的是 JDK 17+

如果没有 JDK 17，点击 SDK 下拉框 → `Add SDK` → `Download JDK`，选择版本 17 下载即可。

---

## 第二步：加载 Maven 依赖

打开项目后，IntelliJ 会自动识别 `backend/pom.xml`。

如果依赖没有自动加载：
1. 在左侧项目树中找到 `backend/pom.xml`
2. 右键 → `Maven` → `Reload project`
3. 等待右下角进度条完成（首次需要下载依赖，时间较长）

> 如果下载很慢，建议配置 Maven 阿里云镜像源，参考 `docs/environment-setup.md`。

---

## 第三步：配置数据库

### 3.1 创建数据库

在 MySQL 中按顺序执行以下两个脚本（详见 `docs/database.md`）：

```bash
# 方式一：命令行
mysql -u root -p < database/create_tables.sql
mysql -u root -p parking_system < database/init_data.sql
```

或用 Navicat / MySQL Workbench 打开文件执行。

### 3.2 修改连接配置

打开 `backend/src/main/resources/application.yml`，修改数据库密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parking_system?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 你的MySQL密码   # ← 改这里
```

---

## 第四步：运行项目

### 方式一：通过启动类运行（推荐）

1. 在左侧项目树中找到：
   `backend/src/main/java/com/parking/ParkingSystemApplication.java`
2. 打开文件，找到 `main` 方法
3. 点击方法左侧的绿色三角形 ▶ → `Run 'ParkingSystemApplication'`

### 方式二：通过 Maven 命令运行

在 IntelliJ 底部打开 `Terminal`，执行：

```bash
cd backend
mvn spring-boot:run
```

### 验证启动成功

控制台出现以下内容说明启动成功：

```
Started ParkingSystemApplication in x.xxx seconds
Tomcat started on port(s): 8080
```

浏览器访问：`http://localhost:8080/api/auth/login`（会返回 405 或 JSON，说明服务正常）

---

## 第五步：验证接口

可以用 Postman 或浏览器测试登录接口：

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

返回包含 `token` 字段的 JSON 即为成功。

---

## 常见问题

### 启动报错：Communications link failure
数据库连接失败。检查：
- MySQL 服务是否已启动
- `application.yml` 中密码是否正确
- 数据库 `parking_system` 是否已创建

### 启动报错：Table 'parking_system.user' doesn't exist
建表脚本未执行，按第三步执行 `create_tables.sql`。

### 端口 8080 被占用
修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081   # 改为其他端口
```

### Maven 依赖下载失败
配置阿里云镜像，参考 `docs/environment-setup.md` 中的 Maven 配置部分。

---

## Redis 说明

目前 `application.yml` 中 Redis 配置已被注释掉，**不需要安装 Redis 也可以正常启动**。
如需启用缓存功能，取消注释并确保本地 Redis 服务运行在 6379 端口。
