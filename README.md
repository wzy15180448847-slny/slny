# 房屋租赁平台

## 项目概述

基于 **Spring Boot 2.7.x + Vue 3** 开发的企业级房屋租赁平台，采用前后端分离架构设计。平台支持**房东、租客、管理员**三种角色，提供完整的房源管理、租赁交易、预约看房、维修管理、投诉仲裁等核心业务功能，致力于为用户提供安全、便捷的房屋租赁服务体验。

### 项目特点

- **前后端分离**: 前端使用 Vue 3 + Element Plus，后端使用 Spring Boot，通过 RESTful API 进行通信
- **角色权限管理**: 基于 RBAC 的权限控制体系，支持角色动态分配
- **数据持久化**: 使用 MySQL 数据库存储业务数据，支持数据备份与恢复
- **安全认证**: JWT Token 认证机制，密码 BCrypt 加密存储
- **响应式设计**: 前端采用响应式布局，支持多种设备访问

## 技术架构

### 后端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 基础框架 | Spring Boot | 2.7.18 | 企业级 Java 开发框架 |
| 安全框架 | Spring Security | 5.8.8 | 安全认证与权限控制 |
| JWT | jjwt | 0.11.5 | JSON Web Token 生成与验证 |
| 数据持久层 | Spring Data JPA | 2.7.x | ORM 框架 |
| 数据持久层 | MyBatis Plus | 3.5.x | SQL 映射框架 |
| 数据库 | MySQL | 8.0+ | 关系型数据库 |
| 连接池 | Druid | 1.2.x | 数据库连接池 |
| 工具类 | Hutool | 5.8.x | Java 工具库 |
| 对象映射 | MapStruct | 1.5.x | 对象转换工具 |

### 前端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue | 3.4.x | 渐进式 JavaScript 框架 |
| 状态管理 | Pinia | 2.1.x | Vue 官方状态管理库 |
| 路由 | Vue Router | 4.2.x | Vue 路由管理器 |
| UI组件 | Element Plus | 2.5.x | Vue 3 组件库 |
| 构建工具 | Vite | 5.0.x | 下一代前端构建工具 |
| 图标 | Element Plus Icons | 2.3.x | Element Plus 图标库 |
| HTTP客户端 | Axios | 1.6.x | HTTP 请求库 |
| 进度条 | NProgress | 0.2.x | 页面加载进度条 |

### 项目结构

```
slny/                              # 项目根目录
├── house-rental-system/            # 后端核心业务模块 (Maven 模块)
│   ├── src/
│   │   └── main/
│   │       ├── java/com/houserental/
│   │       │   ├── config/           # Spring 配置类
│   │       │   │   ├── SecurityConfig.java       # 安全配置
│   │       │   │   ├── JwtConfig.java            # JWT 配置
│   │       │   │   └── CorsConfig.java           # 跨域配置
│   │       │   ├── controller/       # REST API 控制器
│   │       │   │   ├── AuthController.java        # 认证接口
│   │       │   │   ├── HouseController.java      # 房源接口
│   │       │   │   ├── UserController.java       # 用户接口
│   │       │   │   └── ComplaintController.java  # 投诉接口
│   │       │   ├── service/          # 业务服务层
│   │       │   │   ├── impl/                     # 服务实现类
│   │       │   │   ├── UserService.java          # 用户服务接口
│   │       │   │   └── HouseService.java         # 房源服务接口
│   │       │   ├── repository/       # 数据访问层 (JPA)
│   │       │   ├── mapper/           # 数据访问层 (MyBatis)
│   │       │   ├── entity/           # 数据库实体类
│   │       │   │   ├── User.java                 # 用户实体
│   │       │   │   ├── House.java                # 房源实体
│   │       │   │   └── Role.java                 # 角色实体
│   │       │   ├── dto/              # 数据传输对象
│   │       │   │   ├── request/                  # 请求 DTO
│   │       │   │   └── response/                 # 响应 DTO
│   │       │   ├── security/         # 安全相关
│   │       │   │   ├── JwtTokenProvider.java     # JWT 工具类
│   │       │   │   └── CustomUserDetailsService.java  # 用户详情服务
│   │       │   └── HouseRentalApplication.java  # Spring Boot 启动类
│   │       └── resources/
│   │           ├── application.yml   # 应用配置文件
│   │           └── init_project_v2.sql  # 数据库初始化脚本
│   └── pom.xml                       # Maven 依赖管理
│
├── house-rental-common/            # 后端公共模块 (Maven 模块)
│   ├── src/main/java/com/houserental/common/
│   │   ├── result/                  # 统一响应结果
│   │   │   ├── Result.java          # 通用响应封装
│   │   │   └── PageResult.java      # 分页响应封装
│   │   ├── exception/               # 异常处理
│   │   │   ├── BusinessException.java       # 业务异常
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   ├── utils/                   # 工具类
│   │   │   ├── PasswordUtils.java   # 密码工具
│   │   │   └── DateUtils.java       # 日期工具
│   │   └── dto/                     # 公共 DTO
│   └── pom.xml
│
└── frontend-vue/                   # 前端 Vue 项目
    ├── src/
    │   ├── views/                   # 页面视图组件
    │   │   ├── admin/               # 管理员页面
    │   │   │   ├── dashboard.vue    # 数据大屏
    │   │   │   ├── houses.vue       # 房源列表
    │   │   │   ├── house-audit.vue  # 房源审核
    │   │   │   ├── users.vue        # 用户管理
    │   │   │   ├── qualification.vue # 资质审核
    │   │   │   ├── credit.vue       # 信用管理
    │   │   │   ├── complaints.vue   # 投诉仲裁
    │   │   │   ├── profile.vue      # 个人中心
    │   │   │   └── edit-house.vue   # 编辑房源
    │   │   ├── landlord/            # 房东页面
    │   │   │   ├── index.vue        # 房东首页
    │   │   │   ├── profile.vue      # 个人中心
    │   │   │   ├── houses.vue       # 房源管理
    │   │   │   ├── add-house.vue    # 发布房源
    │   │   │   ├── edit-house.vue   # 编辑房源
    │   │   │   ├── appointments.vue # 预约管理
    │   │   │   ├── contracts.vue    # 合同管理
    │   │   │   ├── bills.vue        # 账单管理
    │   │   │   └── repairs.vue      # 维修管理
    │   │   ├── tenant/              # 租客页面
    │   │   │   ├── index.vue        # 租客首页
    │   │   │   ├── profile.vue      # 个人中心
    │   │   │   ├── favorites.vue    # 我的收藏
    │   │   │   ├── appointments.vue # 我的预约
    │   │   │   ├── contracts.vue    # 合同管理
    │   │   │   ├── bills.vue        # 账单支付
    │   │   │   ├── repairs.vue      # 报修管理
    │   │   │   └── wallet.vue       # 我的钱包
    │   │   ├── house/               # 房源相关页面
    │   │   │   ├── search.vue       # 房源搜索
    │   │   │   ├── detail.vue       # 房源详情
    │   │   │   └── publish.vue      # 发布房源
    │   │   ├── auth/                # 认证页面
    │   │   │   ├── login.vue        # 登录页面
    │   │   │   └── register.vue     # 注册页面
    │   │   └── home/                # 首页
    │   │       └── index.vue        # 首页
    │   ├── components/              # 公共组件
    │   │   ├── layout/              # 布局组件
    │   │   │   ├── MainLayout.vue   # 主布局
    │   │   │   ├── AdminLayout.vue  # 管理员布局
    │   │   │   ├── LandlordLayout.vue # 房东布局
    │   │   │   └── TenantLayout.vue # 租客布局
    │   │   ├── SearchBox.vue        # 搜索框组件
    │   │   └── HouseCard.vue        # 房源卡片组件
    │   ├── store/                   # Pinia 状态管理
    │   │   ├── user.js              # 用户状态
    │   │   └── house.js             # 房源状态
    │   ├── api/                     # API 接口封装
    │   │   ├── auth.js              # 认证接口
    │   │   ├── house.js             # 房源接口
    │   │   ├── complaint.js         # 投诉接口
    │   │   └── qualification.js     # 资质审核接口
    │   ├── router/                  # 路由配置
    │   │   └── index.js             # 路由定义
    │   ├── utils/                   # 工具函数
    │   │   ├── request.js           # HTTP 请求封装
    │   │   └── auth.js              # 认证工具
    │   ├── styles/                  # 样式文件
    │   │   ├── variables.scss       # SCSS 变量
    │   │   └── global.scss          # 全局样式
    │   └── main.js                  # 应用入口
    ├── index.html                   # HTML 模板
    ├── package.json                 # 依赖配置
    ├── vite.config.js               # Vite 配置
    ├── vue.config.js                # Vue 配置
    └── .env                         # 环境变量
```

## 功能模块

### 1. 用户管理模块

#### 1.1 认证功能
- **用户注册**: 支持手机号/邮箱注册，密码强度校验
- **用户登录**: 支持用户名/密码登录，记住我功能
- **用户登出**: 清除 Token，安全退出
- **Token 刷新**: 支持 Access Token 过期自动刷新

#### 1.2 用户管理
- **用户列表**: 管理员可查看所有用户
- **用户详情**: 查看用户详细信息
- **角色管理**: 支持角色分配与切换
- **状态管理**: 支持禁用/启用用户账户
- **密码修改**: 用户可修改自身密码

### 2. 房东模块

#### 2.1 房源管理
- **房源列表**: 查看个人发布的房源
- **发布房源**: 填写房源信息，上传图片
- **编辑房源**: 修改房源信息和图片
- **状态管理**: 房源上架/下架操作
- **房源删除**: 删除已发布的房源

#### 2.2 预约管理
- **预约列表**: 查看租客的看房预约
- **确认预约**: 同意租客的预约请求
- **拒绝预约**: 拒绝不合适的预约请求
- **预约详情**: 查看预约详细信息

#### 2.3 合同管理
- **合同列表**: 查看所有租赁合同
- **合同预览**: 预览合同内容
- **合同签署**: 电子签名签署合同

#### 2.4 维修管理
- **维修列表**: 查看租客的维修申请
- **处理申请**: 指派维修人员处理
- **进度跟踪**: 查看维修进度状态

#### 2.5 账单管理
- **账单列表**: 查看租金账单
- **账单详情**: 查看账单详细信息

### 3. 租客模块

#### 3.1 房源浏览
- **房源列表**: 查看所有可租房源
- **筛选搜索**: 按条件筛选房源（价格、面积、户型等）
- **排序功能**: 按价格、面积、发布时间排序
- **收藏功能**: 收藏喜欢的房源

#### 3.2 预约看房
- **提交预约**: 预约看房时间
- **预约状态**: 查看预约审核状态
- **取消预约**: 取消未确认的预约

#### 3.3 合同管理
- **合同列表**: 查看已签署的合同
- **合同预览**: 查看合同详细内容
- **电子签署**: 在线签署电子合同

#### 3.4 售后报修
- **提交申请**: 提交维修申请，上传图片
- **进度跟踪**: 查看维修处理进度
- **服务评价**: 评价维修服务质量

#### 3.5 钱包管理
- **余额查看**: 查看账户余额
- **充值功能**: 在线充值
- **提现功能**: 申请提现
- **账单支付**: 在线支付租金

### 4. 管理员模块

#### 4.1 用户管理
- **用户列表**: 管理平台所有用户
- **用户搜索**: 按条件搜索用户
- **权限管理**: 管理用户角色和权限
- **用户操作**: 禁用、启用、删除用户

#### 4.2 房源管理
- **房源列表**: 查看平台所有房源
- **房源审核**: 审核房东发布的房源
- **房源编辑**: 修改房源信息
- **房源删除**: 删除违规房源

#### 4.3 资质审核
- **审核列表**: 查看待审核的资质申请
- **审核通过**: 批准资质申请
- **审核拒绝**: 拒绝申请并填写原因
- **重新审核**: 重新处理已拒绝的申请

#### 4.4 投诉仲裁
- **投诉列表**: 查看用户投诉
- **投诉详情**: 查看投诉详细信息
- **处理投诉**: 调解纠纷，给出处理结果

#### 4.5 信用管理
- **信用列表**: 查看用户信用评分
- **信用调整**: 调整用户信用分数
- **信用记录**: 查看信用变更记录

#### 4.6 数据统计
- **数据大屏**: 展示平台运营数据
- **用户统计**: 用户增长趋势
- **交易统计**: 租赁交易数据

## 数据库设计

### 核心表结构

#### 用户相关表

| 表名 | 说明 | 主键 | 主要字段 |
|------|------|------|----------|
| sys_user | 用户表 | id | username, password, nickname, phone, email, role |
| sys_role | 角色表 | id | name, code, description |
| sys_permission | 权限表 | id | name, code, url |
| sys_user_role | 用户角色关联 | user_id, role_id | 用户角色映射 |
| sys_role_permission | 角色权限关联 | role_id, perm_id | 角色权限映射 |

#### 房源相关表

| 表名 | 说明 | 主键 | 主要字段 |
|------|------|------|----------|
| biz_house | 房源表 | id | title, address, price, area, status, landlord_id |
| biz_house_image | 房源图片表 | id | house_id, image_url |
| biz_favorite | 收藏表 | id | user_id, house_id |

#### 交易相关表

| 表名 | 说明 | 主键 | 主要字段 |
|------|------|------|----------|
| biz_appointment | 预约表 | id | house_id, user_id, status, appointment_time |
| biz_lease | 租约表 | id | house_id, tenant_id, landlord_id, start_date, end_date |
| biz_payment | 支付记录表 | id | lease_id, amount, pay_type, status |

#### 售后相关表

| 表名 | 说明 | 主键 | 主要字段 |
|------|------|------|----------|
| biz_maintenance | 维修申请表 | id | house_id, user_id, status, description |
| biz_review | 评价表 | id | house_id, user_id, score, comment |
| biz_complaint | 投诉表 | id | complainant_id, defendant_id, status, content |

## 快速开始

### 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 11+ | Java 开发环境 |
| Maven | 3.6+ | Java 项目构建工具 |
| MySQL | 8.0+ | 关系型数据库 |
| Node.js | 18+ | JavaScript 运行环境 |
| npm | 9+ | 前端包管理器 |

### 数据库初始化

1. **创建数据库**

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS house_rental 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

# 退出
exit
```

2. **执行初始化脚本**

```bash
mysql -u root -p house_rental < house-rental-system/src/main/resources/init_project_v2.sql
```

### 配置文件

#### 后端配置

后端配置文件位于 `house-rental-system/src/main/resources/application.yml`

**数据库配置**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/house_rental?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**JWT 配置**:
```yaml
jwt:
  secret: house-rental-platform-secret-key
  expiration: 86400000  # 24小时
```

#### 前端配置

前端环境变量文件位于 `frontend-vue/.env`

```env
# API 基础地址
VUE_APP_API_BASE_URL=http://localhost:8080/api

# 开发服务器端口
PORT=3000
```

### 启动后端服务

```bash
# 进入后端项目目录
cd house-rental-system

# 使用 Maven 编译运行
mvn spring-boot:run -DskipTests

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/house-rental-system-1.0.0.jar
```

启动成功后访问: http://localhost:8080

### 启动前端服务

```bash
# 进入前端项目目录
cd frontend-vue

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

启动成功后访问: http://localhost:3000

### 默认测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员，拥有所有权限 |
| 房东 | landlord | 123456 | 普通房东账号 |
| 租客 | tenant | 123456 | 普通租客账号 |

## API 接口说明

### 认证接口

| 接口路径 | HTTP方法 | 说明 | 权限 |
|----------|----------|------|------|
| /auth/register | POST | 用户注册 | 公开 |
| /auth/login | POST | 用户登录 | 公开 |
| /auth/logout | POST | 用户登出 | 登录用户 |
| /auth/refresh | POST | 刷新Token | 登录用户 |
| /auth/profile | GET | 获取用户信息 | 登录用户 |
| /auth/profile | PUT | 更新用户信息 | 登录用户 |
| /auth/password | PUT | 修改密码 | 登录用户 |

**登录请求示例**:
```json
POST /auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

**登录响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "role": "ADMIN"
    }
  }
}
```

### 房源接口

| 接口路径 | HTTP方法 | 说明 | 权限 |
|----------|----------|------|------|
| /houses | GET | 获取房源列表 | 公开 |
| /houses/{id} | GET | 获取房源详情 | 公开 |
| /houses | POST | 发布房源 | 房东 |
| /houses/{id} | PUT | 更新房源 | 房东/管理员 |
| /houses/{id} | DELETE | 删除房源 | 房东/管理员 |
| /houses/{id}/favorite | POST | 收藏房源 | 租客 |
| /houses/{id}/favorite | DELETE | 取消收藏 | 租客 |

**获取房源列表请求**:
```json
GET /houses?page=1&size=10&keyword=阳光&minPrice=2000&maxPrice=5000
```

### 预约接口

| 接口路径 | HTTP方法 | 说明 | 权限 |
|----------|----------|------|------|
| /appointments | GET | 获取预约列表 | 登录用户 |
| /appointments | POST | 提交预约 | 租客 |
| /appointments/{id} | GET | 获取预约详情 | 登录用户 |
| /appointments/{id}/confirm | POST | 确认预约 | 房东 |
| /appointments/{id}/reject | POST | 拒绝预约 | 房东 |
| /appointments/{id} | DELETE | 取消预约 | 租客 |

### 投诉接口

| 接口路径 | HTTP方法 | 说明 | 权限 |
|----------|----------|------|------|
| /complaints | GET | 获取投诉列表 | 管理员 |
| /complaints/{id} | GET | 获取投诉详情 | 管理员 |
| /complaints | POST | 提交投诉 | 登录用户 |
| /complaints/{id}/process | POST | 处理投诉 | 管理员 |

### 维修接口

| 接口路径 | HTTP方法 | 说明 | 权限 |
|----------|----------|------|------|
| /maintenance | GET | 获取维修列表 | 登录用户 |
| /maintenance | POST | 提交维修申请 | 租客 |
| /maintenance/{id} | GET | 获取维修详情 | 登录用户 |
| /maintenance/{id}/process | POST | 处理维修申请 | 房东 |
| /maintenance/{id}/complete | POST | 完成维修 | 房东 |

## 安全特性

### 认证机制
- **JWT Token**: 无状态认证，Token 存储在客户端
- **Token 刷新**: Access Token 过期后自动刷新
- **密码加密**: 使用 BCrypt 算法加密存储

### 权限控制
- **RBAC 模型**: 基于角色的权限控制
- **方法级别权限**: 使用 @PreAuthorize 注解
- **动态权限分配**: 支持角色和权限的动态配置

### 安全防护
- **SQL注入防护**: 使用参数化查询
- **XSS攻击防护**: 前端数据过滤
- **CSRF防护**: Token 验证机制
- **接口限流**: 防止恶意请求

## 开发规范

### 后端规范

#### 代码风格
- 遵循 **Alibaba Java Coding Guidelines**
- 使用 **Lombok** 简化代码（@Data, @Service, @Controller）
- 方法命名使用驼峰命名法
- 类名使用 Pascal 命名法

#### 接口设计
- 遵循 **RESTful API** 设计规范
- 统一响应格式（Result 封装）
- 错误码统一管理
- 接口文档使用 Knife4j

#### 异常处理
- 使用统一异常处理器
- 业务异常继承 BusinessException
- 异常信息清晰明确

### 前端规范

#### 代码风格
- 使用 **Vue 3 Composition API**
- 组件命名使用 PascalCase
- 变量命名使用 camelCase
- 使用 ESLint 进行代码检查

#### 组件开发
- 组件职责单一
- 使用 props 传递数据
- 使用 emit 触发事件
- 避免直接操作 DOM

#### API 调用
- 统一封装 API 调用
- 使用 Axios 拦截器处理请求
- 统一错误处理

## 部署

### 开发环境部署

```bash
# 1. 启动数据库
mysql -u root -p

# 2. 启动 Redis（如果需要）
redis-server

# 3. 启动后端
cd house-rental-system
mvn spring-boot:run -DskipTests

# 4. 启动前端
cd frontend-vue
npm install
npm run dev
```

### 生产环境部署

#### 前端构建

```bash
cd frontend-vue
npm install
npm run build
```

构建产物位于 `dist` 目录，可部署到 Nginx 或静态文件服务器。

#### 后端部署

```bash
cd house-rental-system
mvn clean package -DskipTests

# 运行
java -jar target/house-rental-system-1.0.0.jar --spring.profiles.active=prod
```

#### Docker 部署

```dockerfile
# Dockerfile
FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/house-rental-system-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
```

```bash
# 构建镜像
docker build -t house-rental-platform .

# 运行容器
docker run -p 8080:8080 house-rental-platform
```

## 测试

### 单元测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 生成测试报告
mvn jacoco:report
```

### 集成测试

```bash
# 运行集成测试
mvn test -Dtest=*IntegrationTest
```

### API 测试

使用 Postman 或 curl 测试 API 接口：

```bash
# 测试登录接口
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试获取房源列表
curl -X GET http://localhost:8080/houses \
  -H "Authorization: Bearer your_token"
```

## 贡献指南

### 开发流程

1. **Fork 项目**
2. **创建分支**
```bash
git checkout -b feature/feature-name
```
3. **提交代码**
```bash
git add .
git commit -m "feat: add feature-name"
```
4. **推送到远程**
```bash
git push origin feature/feature-name
```
5. **创建 Pull Request**

### 代码审查

提交 PR 后需要经过以下审查：
- 代码风格检查
- 功能完整性检查
- 单元测试覆盖率
- 安全性检查

### 代码规范

- 前端使用 ESLint 检查
- 后端使用 Checkstyle 检查
- 所有代码需要通过 CI 检查

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- 项目地址: https://github.com/yourusername/house-rental-platform
- 问题反馈: https://github.com/yourusername/house-rental-platform/issues
- 开发者邮箱: developer@example.com

---

**最后更新**: 2026年4月
**版本**: v1.0.0