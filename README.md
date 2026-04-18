# 房屋租赁中介平台

## 项目概述

基于Spring Boot 2.7.x开发的企业级房屋租赁中介平台，采用前后端分离架构，支持房东、租客、中介、管理员四种角色，提供完整的房源管理、租赁交易、消息通知、评价系统等核心业务功能。

## 技术架构

### 后端技术栈

- **基础框架**: Spring Boot 2.7.18
- **安全框架**: Spring Security 5.8.8 + JWT
- **数据持久层**: Spring Data JPA + MyBatis Plus
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **API文档**: Knife4j (Swagger增强)
- **连接池**: Druid
- **工具类**: Hutool
- **对象映射**: MapStruct

### 项目结构

```
house-rental-platform/
├── house-rental-common/          # 公共模块
│   ├── result/                   # 统一响应结果
│   ├── exception/                # 异常处理
│   ├── utils/                    # 工具类
│   └── dto/                      # 数据传输对象
│
└── house-rental-system/          # 核心业务模块
    ├── config/                   # 配置类
    ├── controller/               # 控制器层
    ├── service/                  # 服务层
    ├── repository/               # 数据访问层
    ├── entity/                   # 实体类
    ├── dto/                      # 数据传输对象
    ├── vo/                       # 视图对象
    ├── security/                 # 安全配置
    ├── mq/                       # 消息队列
    └── HouseRentalApplication.java
```

## 功能模块

### 1. 用户管理模块
- 用户注册/登录/登出
- JWT Token认证
- 基于RBAC的权限控制
- 用户信息管理
- 密码修改与重置

### 2. 房东模块
- **房源管理**: 房源CRUD、图片上传、状态管理
- **预约管理**: 查看预约、确认/拒绝预约、设置看房时间
- **租金管理**: 租金到账记录、催缴单生成、历史账单
- **租约管理**: 解约申请处理、租约状态查看、合同下载
- **维修管理**: 维修申请接收、维修人员指派、进度跟踪

### 3. 租客模块
- **房源浏览**: 多条件筛选、排序、收藏
- **预约看房**: 提交预约、查看预约状态
- **租赁管理**: 查看租约、支付租金、提交解约申请
- **售后报修**: 提交维修申请、图文描述、进度跟踪
- **评价系统**: 房源评分、服务评价

### 4. 管理员模块
- **人员管理**: 中介账户管理、区域权限分配
- **权限配置**: 角色管理、功能权限配置
- **内容管理**: 房源审核、投诉处理、公告管理
- **数据统计**: 运营数据、用户增长、交易统计

### 5. 中介模块
- **房源审核**: 真实性/合规性审核
- **租客审核**: 身份验证、资质审核
- **交易协调**: 纠纷调解、沟通协调
- **订单管理**: 异常订单处理、仲裁、逾期跟进

## 数据库设计

### 核心表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_permission | 权限表 |
| sys_user_role | 用户角色关联表 |
| sys_role_permission | 角色权限关联表 |
| biz_house | 房源表 |
| biz_appointment | 预约表 |
| biz_lease | 租约表 |
| biz_payment | 支付记录表 |
| biz_maintenance | 维修申请表 |
| biz_review | 评价表 |
| biz_favorite | 收藏表 |

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

### 数据库初始化

使用项目根目录下的 `database/init_project_v2.sql` 文件初始化数据库：

```bash
# 创建数据库
CREATE DATABASE IF NOT EXISTS house_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本
mysql -u root -p house_rental < database/init_project_v2.sql
```

### 配置文件

项目支持通过环境变量注入敏感配置，开发环境可直接使用 `application-dev.yml` 模板。

**环境变量说明：**

| 环境变量 | 说明 | 默认值 |
|---------|------|--------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/house_rental |
| DB_USER | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root |
| APP_JWT_SECRET | JWT密钥 | house-rental-platform-secret-key |
| APP_MINIO_URL | MinIO服务地址 | http://localhost:9000 |
| APP_MINIO_ACCESS_KEY | MinIO访问密钥 | minioadmin |
| APP_MINIO_SECRET_KEY | MinIO秘密密钥 | minioadmin |
| ALIPAY_APP_ID | 支付宝应用ID | - |
| ALIPAY_PRIVATE_KEY | 支付宝私钥 | - |
| ALIPAY_PUBLIC_KEY | 支付宝公钥 | - |
| ALIYUN_SMS_ACCESS_KEY_ID | 阿里云AccessKey ID | - |
| ALIYUN_SMS_ACCESS_KEY_SECRET | 阿里云AccessKey Secret | - |

**开发环境配置：**

复制 `application-dev.yml` 并修改相关配置：

```bash
# 使用开发环境配置启动
java -jar house-rental-system/target/house-rental-system-1.0.0.jar --spring.profiles.active=dev
```

### 启动项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd house-rental-platform

# 编译打包
mvn clean package -DskipTests

# 运行
java -jar house-rental-system/target/house-rental-system-1.0.0.jar
```

### 访问API文档

启动成功后访问：http://localhost:8080/api/doc.html

## API接口说明

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /auth/register | POST | 用户注册 |
| /auth/login | POST | 用户登录 |
| /auth/logout | POST | 用户登出 |
| /auth/refresh | POST | 刷新Token |

### 房源接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /houses | GET | 房源列表 |
| /houses/{id} | GET | 房源详情 |
| /houses | POST | 发布房源 |
| /houses/{id} | PUT | 更新房源 |
| /houses/{id} | DELETE | 删除房源 |

## 安全特性

- HTTPS加密传输
- JWT Token认证
- 密码BCrypt加密存储
- SQL注入防护（参数化查询）
- XSS攻击防护
- CSRF Token验证
- 接口权限控制

## 性能优化

- Redis热点数据缓存
- 数据库索引优化
- 连接池配置
- 分页查询
- 异步消息处理

## 开发规范

- 遵循Alibaba Java Coding Guidelines
- 使用Lombok简化代码
- 统一异常处理
- RESTful API设计
- Javadoc注释规范

## 测试

```bash
# 运行单元测试
mvn test

# 生成测试报告
mvn jacoco:report
```

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t house-rental-platform .

# 运行容器
docker run -p 8080:8080 house-rental-platform
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

[MIT](LICENSE)

## 联系方式

- 项目主页: https://github.com/yourusername/house-rental-platform
- 问题反馈: https://github.com/yourusername/house-rental-platform/issues
