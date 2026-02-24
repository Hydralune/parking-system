# 开发环境配置指南

## 1. Java开发环境配置

### 安装JDK 17+
1. 访问 [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 或 [OpenJDK](https://adoptium.net/) 下载JDK 17+
2. 安装JDK到默认路径（如：C:\Program Files\Java\jdk-17）
3. 配置环境变量：
   - 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"
   - 新建系统变量：`JAVA_HOME` = JDK安装路径
   - 编辑系统变量：`PATH` 添加 `%JAVA_HOME%\bin`

### 安装Maven
1. 访问 [Maven官网](https://maven.apache.org/download.cgi) 下载最新版
2. 解压到指定目录（如：C:\apache-maven-3.x.x）
3. 配置环境变量：
   - 新建系统变量：`MAVEN_HOME` = Maven解压路径
   - 编辑系统变量：`PATH` 添加 `%MAVEN_HOME%\bin`
4. 配置阿里云镜像源（settings.xml）：
```xml
<mirrors>
  <mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 2. Node.js开发环境配置

### 安装Node.js 18+
1. 访问 [Node.js官网](https://nodejs.org/) 下载LTS版本
2. 安装Node.js（会自动配置npm）
3. 验证安装：`node -v` 和 `npm -v`

### 配置npm镜像源
```bash
npm config set registry https://registry.npmmirror.com
```

### 安装Vue CLI
```bash
npm install -g @vue/cli
```

## 3. 数据库环境配置

### 安装MySQL 8.0+
1. 访问 [MySQL官网](https://dev.mysql.com/downloads/mysql/) 下载MySQL 8.0+
2. 按照安装向导完成安装
3. 记住root用户密码

### 安装Redis 7.0+
1. 访问 [Redis官网](https://redis.io/download/) 或使用Windows版本
2. 解压并配置Redis服务

### 数据库管理工具
推荐安装 [Navicat Premium](https://www.navicat.com.cn/) 或 [MySQL Workbench](https://www.mysql.com/products/workbench/)

## 4. 开发工具配置

### IntelliJ IDEA
1. 访问 [IntelliJ IDEA官网](https://www.jetbrains.com/idea/download/) 下载
2. 安装Community或Ultimate版本

### VS Code
1. 访问 [VS Code官网](https://code.visualstudio.com/) 下载安装
2. 推荐安装插件：
   - Vetur 或 Volar
   - Element Plus Snippets
   - GitLens
   - Prettier

### Postman
1. 访问 [Postman官网](https://www.postman.com/downloads/) 下载安装

### Git
1. 访问 [Git官网](https://git-scm.com/downloads) 下载安装
2. 配置用户信息：
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

## 5. 验证环境配置

### 验证Java环境
```bash
java -version
mvn -version
```

### 验证Node.js环境
```bash
node --version
npm --version
vue --version
```

### 验证数据库
```bash
# MySQL
mysql --version

# Redis
redis-server --version
```

### 验证Git
```bash
git --version
```