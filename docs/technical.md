# 房屋租赁中介平台 - 技术文档

## 1. 系统架构设计

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           前端层                          │
│                    Vue 3 + Element Plus + Pinia                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           网关层                          │
│                         Nginx 反向代理 + 负载均衡                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         应用层                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │ Controller  │ │ Controller  │ │ Controller  │ │ Controller  │       │
│  │   (Auth)    │ │   (House)   │ │ (Appointment)│ │  (Payment)  │       │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         业务层                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │   Service   │ │   Service   │ │   Service   │ │   Service   │       │
│  │   (Auth)    │ │   (House)   │ │(Appointment)│ │  (Payment)  │       │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │   Service   │ │   Service   │ │   Service   │ │   Service   │       │
│  │(Maintenance)│ │(Notification)│ │   (Lease)   │ │   (Review)  │       │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         数据层                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │  Repository │ │  Repository │ │  Repository │ │  Repository │       │
│  │   (User)    │ │   (House)   │ │(Appointment)│ │  (Payment)  │       │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
            ┌───────────────────────┼───────────────────────┐
            ▼                       ▼                       ▼
┌───────────────────┐   ┌───────────────────┐   ┌───────────────────┐
│      MySQL        │   │      Redis        │   │    RabbitMQ       │
│   (主数据库)       │   │   (缓存/会话)      │   │   (消息队列)       │
└───────────────────┘   └───────────────────┘   └───────────────────┘
                                    │
                                    ▼
                        ┌───────────────────┐
                        │  Elasticsearch    │
                        │   (全文搜索)       │
                        └───────────────────┘
```

### 1.2 核心流程图

#### 预约看房流程

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  租客   │     │  系统   │     │  房东   │     │  系统   │
└────┬────┘     └────┬────┘     └────┬────┘     └────┬────┘
     │               │               │               │
     │ 1.选择房源     │               │               │
     │──────────────>│               │               │
     │               │               │               │
     │ 2.提交预约     │               │               │
     │──────────────>│               │               │
     │               │ 3.检测日程冲突 │               │
     │               │──────────────>│               │
     │               │               │               │
     │               │ 4.发送通知     │               │
     │               │──────────────────────────────>│
     │               │               │               │
     │               │               │ 5.确认/拒绝   │
     │               │               │──────────────>│
     │               │               │               │
     │               │ 6.更新状态     │               │
     │               │<──────────────│               │
     │               │               │               │
     │ 7.发送结果通知 │               │               │
     │<──────────────│               │               │
     │               │               │               │
     │ 8.看房完成     │               │               │
     │──────────────>│               │               │
     │               │               │               │
```

## 2. 技术栈选型

### 2.1 后端技术栈

| 类别 | 技术 | 版本 | 选型理由 |
|------|------|------|---------|
| **基础框架** | Spring Boot | 2.7.18 | 稳定成熟，生态完善 |
| **安全框架** | Spring Security | 5.8.8 | 企业级安全，JWT集成 |
| **ORM框架** | MyBatis Plus | 3.5.5 | 灵活高效，代码生成 |
| **数据库** | MySQL | 8.0.45 | 主流关系型数据库 |
| **缓存** | Redis | 7.0+ | 高性能缓存，会话管理 |
| **消息队列** | RabbitMQ | 3.12+ | 可靠消息，异步处理 |
| **搜索引擎** | Elasticsearch | 7.17+ | 全文搜索，高效检索 |
| **API文档** | Knife4j | 3.0.3 | Swagger增强，在线调试 |
| **连接池** | Druid | 1.2.20 | 监控统计，防SQL注入 |
| **工具库** | Hutool | 5.8.23 | 丰富工具方法 |
| **对象映射** | MapStruct | 1.5.5 | 编译时转换，高性能 |
| **认证授权** | JJWT | 0.11.5 | JWT标准实现 |

### 2.2 前端技术栈

| 类别 | 技术 | 版本 | 选型理由 |
|------|------|------|---------|
| **框架** | Vue.js | 3.4.21 | 响应式，组件化 |
| **UI组件** | Element Plus | 2.6.1 | 企业级UI，丰富组件 |
| **状态管理** | Pinia | 2.1.7 | Vue3官方推荐 |
| **路由** | Vue Router | 4.3.0 | SPA路由管理 |
| **HTTP客户端** | Axios | 1.6.8 | Promise支持，拦截器 |
| **构建工具** | Vite | 5.2.0 | 快速热更新 |
| **CSS预处理** | SCSS | 1.72.0 | 变量，嵌套 |

### 2.3 基础设施

| 类别 | 技术 | 用途 |
|------|------|------|
| **容器化** | Docker | 应用容器 |
| **编排** | Kubernetes | 容器编排（生产环境） |
| **反向代理** | Nginx | 负载均衡，静态资源 |
| **文件存储** | MinIO/阿里云OSS | 图片、合同文件存储 |
| **监控** | Prometheus + Grafana | 系统监控 |
| **日志** | ELK Stack | 日志收集分析 |

## 3. 模块分解

### 3.1 项目结构

```
house-rental-platform/
├── house-rental-common/              # 公共模块
│   ├── config/                       # 配置类
│   ├── constant/                     # 常量定义
│   ├── enums/                        # 枚举类
│   ├── exception/                    # 异常定义
│   ├── result/                       # 统一响应
│   ├── utils/                        # 工具类
│   └── annotation/                   # 自定义注解
│
├── house-rental-system/              # 核心业务模块
│   ├── controller/                   # 控制器层
│   │   ├── AuthController.java       # 认证控制器
│   │   ├── HouseController.java      # 房源控制器
│   │   ├── AppointmentController.java# 预约控制器
│   │   ├── LeaseController.java      # 租约控制器
│   │   ├── PaymentController.java    # 支付控制器
│   │   ├── MaintenanceController.java# 维修控制器
│   │   ├── NotificationController.java# 通知控制器
│   │   └── ReviewController.java     # 评价控制器
│   │
│   ├── service/                      # 服务层接口
│   └── service/impl/                 # 服务层实现
│   │
│   ├── repository/                   # 数据访问层
│   │
│   ├── entity/                       # 实体类
│   │
│   ├── dto/                          # 数据传输对象
│   │   ├── request/                  # 请求DTO
│   │   └── response/                 # 响应DTO
│   │
│   ├── vo/                           # 视图对象
│   │
│   ├── converter/                    # 对象转换器
│   │
│   ├── security/                     # 安全配置
│   │
│   └── mq/                           # 消息队列
│       ├── producer/                 # 消息生产者
│       └── consumer/                 # 消息消费者
│
└── house-rental-frontend/            # 前端模块
    ├── src/
    │   ├── api/                      # API接口
    │   ├── components/               # 公共组件
    │   ├── views/                    # 页面视图
    │   ├── store/                    # 状态管理
    │   ├── router/                   # 路由配置
    │   ├── utils/                    # 工具函数
    │   └── styles/                   # 样式文件
    └── package.json
```

### 3.2 核心模块功能

| 模块 | 主要功能 | 关键类/接口 |
|------|---------|------------|
| **认证授权** | 用户登录/注册、JWT认证、RBAC权限 | AuthController、UserService |
| **房源管理** | 房源CRUD、搜索、审核 | HouseController、HouseService |
| **预约管理** | 预约创建、确认、冲突检测 | AppointmentController、AppointmentService |
| **租约管理** | 租约创建、签署、解约 | LeaseController、LeaseService |
| **支付管理** | 租金支付、记录管理、第三方支付 | PaymentController、PaymentService |
| **维修管理** | 维修申请、处理、状态跟踪 | MaintenanceController、MaintenanceService |
| **通知管理** | 消息推送、短信/邮件发送 | NotificationController、NotificationService |
| **评价管理** | 房源评价、回复管理 | ReviewController、ReviewService |

## 4. 接口规范

### 4.1 通用接口规范

- **请求方式**：RESTful风格
- **响应格式**：统一JSON格式
- **认证方式**：JWT Token
- **错误处理**：统一异常处理
- **分页规范**：统一分页参数

### 4.2 核心接口设计

#### 认证接口

| 接口 | 方法 | 描述 |
|------|------|------|
| /api/auth/register | POST | 用户注册 |
| /api/auth/login | POST | 用户登录 |
| /api/auth/logout | POST | 用户登出 |
| /api/auth/refresh | POST | 刷新Token |
| /api/auth/userinfo | GET | 获取用户信息 |
| /api/auth/password | PUT | 修改密码 |

#### 房源接口

| 接口 | 方法 | 描述 |
|------|------|------|
| /api/houses | POST | 发布房源 |
| /api/houses/{id} | PUT | 更新房源 |
| /api/houses/{id} | DELETE | 删除房源 |
| /api/houses/{id} | GET | 房源详情 |
| /api/houses | GET | 房源列表 |
| /api/houses/search | GET | 房源搜索 |
| /api/houses/recommend | GET | 推荐房源 |
| /api/houses/my | GET | 我的房源 |

#### 预约接口

| 接口 | 方法 | 描述 |
|------|------|------|
| /api/appointments | POST | 创建预约 |
| /api/appointments/{id} | PUT | 更新预约 |
| /api/appointments/{id} | DELETE | 取消预约 |
| /api/appointments/{id} | GET | 预约详情 |
| /api/appointments/my | GET | 我的预约 |
| /api/appointments/{id}/confirm | POST | 确认预约 |
| /api/appointments/{id}/reject | POST | 拒绝预约 |

#### 支付接口

| 接口 | 方法 | 描述 |
|------|------|------|
| /api/payments | POST | 创建支付 |
| /api/payments/{id} | GET | 支付详情 |
| /api/payments/my | GET | 我的支付记录 |
| /api/payments/{id}/refund | POST | 申请退款 |
| /api/payments/callback/wechat | POST | 微信回调 |
| /api/payments/callback/alipay | POST | 支付宝回调 |

## 5. 数据库设计

### 5.1 核心表结构

#### 用户表 (sys_user)

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY | 用户ID |
| username | VARCHAR(50) | UNIQUE NOT NULL | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码 |
| nickname | VARCHAR(50) | | 昵称 |
| real_name | VARCHAR(50) | | 真实姓名 |
| phone | VARCHAR(20) | | 手机号 |
| email | VARCHAR(100) | | 邮箱 |
| user_type | VARCHAR(20) | NOT NULL | 用户类型 |
| status | TINYINT | NOT NULL | 状态 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

#### 房源表 (biz_house)

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY | 房源ID |
| house_no | VARCHAR(32) | UNIQUE NOT NULL | 房源编号 |
| landlord_id | BIGINT | NOT NULL | 房东ID |
| title | VARCHAR(200) | NOT NULL | 房源标题 |
| description | TEXT | | 房源描述 |
| province | VARCHAR(50) | | 省份 |
| city | VARCHAR(50) | | 城市 |
| district | VARCHAR(50) | | 区县 |
| address | VARCHAR(200) | NOT NULL | 详细地址 |
| rent_price | DECIMAL(10,2) | NOT NULL | 租金 |
| status | TINYINT | NOT NULL | 状态 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

#### 预约表 (biz_appointment)

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY | 预约ID |
| appointment_no | VARCHAR(32) | UNIQUE NOT NULL | 预约编号 |
| house_id | BIGINT | NOT NULL | 房源ID |
| tenant_id | BIGINT | NOT NULL | 租客ID |
| landlord_id | BIGINT | NOT NULL | 房东ID |
| view_time | DATETIME | NOT NULL | 看房时间 |
| status | TINYINT | | 状态 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

#### 租约表 (biz_lease)

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY | 租约ID |
| lease_no | VARCHAR(32) | UNIQUE NOT NULL | 租约编号 |
| house_id | BIGINT | NOT NULL | 房源ID |
| landlord_id | BIGINT | NOT NULL | 房东ID |
| tenant_id | BIGINT | NOT NULL | 租客ID |
| start_date | DATE | NOT NULL | 起租日期 |
| end_date | DATE | NOT NULL | 到期日期 |
| monthly_rent | DECIMAL(10,2) | NOT NULL | 月租金 |
| deposit | DECIMAL(10,2) | NOT NULL | 押金 |
| status | TINYINT | | 状态 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

#### 支付表 (biz_payment)

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY | 支付ID |
| payment_no | VARCHAR(32) | UNIQUE NOT NULL | 支付编号 |
| lease_id | BIGINT | NOT NULL | 租约ID |
| payer_id | BIGINT | NOT NULL | 付款人ID |
| payee_id | BIGINT | NOT NULL | 收款人ID |
| amount | DECIMAL(10,2) | NOT NULL | 支付金额 |
| payment_type | TINYINT | NOT NULL | 支付类型 |
| status | TINYINT | | 状态 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

## 6. 实现计划

### 6.1 开发阶段划分

| 阶段 | 时间 | 主要任务 |
|------|------|---------|
| **阶段一** | 第1-2周 | 项目评估、环境搭建、依赖安装 |
| **阶段二** | 第3-4周 | 用户认证授权模块开发 |
| **阶段三** | 第5-7周 | 房屋信息管理模块开发 |
| **阶段四** | 第8-10周 | 租赁交易流程模块开发 |
| **阶段五** | 第11-12周 | 支付集成模块开发 |
| **阶段六** | 第13-14周 | 通知模块开发 |
| **阶段七** | 第15周 | 系统集成测试 |
| **阶段八** | 第16周 | 生产环境部署 |

### 6.2 关键实现技术

#### 认证授权实现
- 使用Spring Security + JWT实现无状态认证
- 基于RBAC模型的权限控制
- Redis存储Token和会话信息

#### 房源搜索实现
- Elasticsearch实现全文搜索
- 多条件组合查询
- 地理位置搜索

#### 支付集成实现
- 微信支付API对接
- 支付宝API对接
- 支付回调处理

#### 通知系统实现
- RabbitMQ消息队列
- 短信通知（阿里云SMS）
- 邮件通知

### 6.3 性能优化策略

- **数据库优化**：合理索引、分表策略、查询优化
- **缓存策略**：Redis缓存热点数据、用户会话
- **异步处理**：RabbitMQ处理异步任务
- **负载均衡**：Nginx反向代理和负载均衡
- **代码优化**：减少重复代码、优化算法

## 7. 测试计划

### 7.1 测试策略

- **单元测试**：JUnit5 + Mockito，覆盖率≥80%
- **集成测试**：Spring Boot Test，核心流程100%
- **接口测试**：Postman/JMeter，所有接口100%
- **性能测试**：JMeter压力测试，200并发用户
- **安全测试**：OWASP ZAP，OWASP Top 10标准

### 7.2 测试环境

| 环境 | 配置 | 用途 |
|------|------|------|
| 开发环境 | 8核16G | 开发和单元测试 |
| 测试环境 | 8核16G | 集成测试和性能测试 |
| 生产环境 | 16核32G | 生产部署 |

## 8. 部署方案

### 8.1 容器化部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    volumes:
      - mysql-data:/var/lib/mysql
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=house_rental

  redis:
    image: redis:7.0
    volumes:
      - redis-data:/data

  rabbitmq:
    image: rabbitmq:3.12-management
    ports:
      - "15672:15672"

  elasticsearch:
    image: elasticsearch:7.17.0
    volumes:
      - es-data:/usr/share/elasticsearch/data

  app:
    image: house-rental-platform:latest
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
      - rabbitmq
      - elasticsearch

volumes:
  mysql-data:
  redis-data:
  es-data:
```

### 8.2 CI/CD流程

1. **代码提交**：开发人员提交代码到Git仓库
2. **构建**：Jenkins自动构建，运行单元测试
3. **测试**：运行集成测试和安全扫描
4. **部署**：部署到测试环境
5. **验证**：测试通过后部署到生产环境

## 9. 监控与维护

### 9.1 监控方案

- **系统监控**：Prometheus + Grafana
- **应用监控**：Spring Boot Actuator
- **日志监控**：ELK Stack
- **告警机制**：邮件/短信告警

### 9.2 维护策略

- **定期备份**：数据库定期备份
- **版本管理**：Git版本控制
- **问题跟踪**：Jira问题管理
- **性能优化**：定期性能分析和优化

## 10. 技术风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| 第三方支付对接失败 | 支付功能无法使用 | 提前调研，准备备选方案 |
| 短信服务配置错误 | 通知功能异常 | 确保配置正确，备用邮件通知 |
| 性能瓶颈 | 系统响应缓慢 | 优化数据库，使用缓存，负载均衡 |
| 安全漏洞 | 数据泄露 | 定期安全扫描，及时更新依赖 |
| 数据迁移问题 | 历史数据丢失 | 制定详细迁移计划，数据验证 |

---

**文档版本**：v1.0
**创建时间**：2026-03-15
**维护人员**：系统架构师