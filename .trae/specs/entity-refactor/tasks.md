# 房屋租赁平台 - 数据库结构对齐重构任务列表

## [x] Task 1: 检查并修复核心Entity类字段与SQL对齐
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 对照init_project_v3.sql检查User、House、UserWallet等核心Entity类
  - 确保每个SQL字段都有对应的Java驼峰字段
  - 确保@TableLogic和@Version注解正确配置
- **Acceptance Criteria Addressed**: [AC-1]
- **Test Requirements**:
  - `human-judgment` TR-1.1: User实体字段与sys_user表完全对应 ✓
  - `human-judgment` TR-1.2: House实体字段与biz_house表完全对应 ✓
  - `human-judgment` TR-1.3: 所有逻辑删除字段有@TableLogic注解，版本字段有@Version注解 ✓
- **Notes**: 需要特别注意SQL中默认值和约束

## [x] Task 2: 检查并修复其他Entity类
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 检查剩余的Entity类（LoginLog、Appointment、Complaint等）
  - 确保所有字段与SQL表结构一致
- **Acceptance Criteria Addressed**: [AC-1]
- **Test Requirements**:
  - `human-judgment` TR-2.1: 所有Entity字段与对应SQL表完全对应 ✓
- **Notes**: 注意关联表的外键字段

## [x] Task 3: 验证MyBatis-Plus配置
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查application.yml中mybatis-plus配置
  - 确保map-underscore-to-camel-case: true
  - 检查全局配置中的逻辑删除和乐观锁配置
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-3.1: 启动应用无配置错误
  - `human-judgment` TR-3.2: 配置文件中map-underscore-to-camel-case设置正确 ✓
- **Notes**: 配置已存在，只需验证

## [x] Task 4: 修改前端request.js自动解包Result<T>
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 修改响应拦截器，自动提取Result<T>的data字段
  - 保留401跳转登录的逻辑
- **Acceptance Criteria Addressed**: [AC-3]
- **Test Requirements**:
  - `programmatic` TR-4.1: 成功响应直接返回data字段
  - `programmatic` TR-4.2: code=401时跳转到登录页
- **Notes**: 需要确保不影响错误处理逻辑

## [x] Task 5: 检查并修复前端API参数命名
- **Priority**: P1
- **Depends On**: Task 4
- **Description**: 
  - 检查所有前端API文件（admin.js, house.js等）
  - 确保所有请求参数使用驼峰命名
  - 修复使用下划线的参数
- **Acceptance Criteria Addressed**: [AC-4]
- **Test Requirements**:
  - `human-judgment` TR-5.1: 所有请求体参数使用驼峰命名 ✓
  - `human-judgment` TR-5.2: URL参数保持原格式（后端已处理） ✓
- **Notes**: URL参数通常由后端框架自动处理，主要检查请求体

## [x] Task 6: 编译验证
- **Priority**: P0
- **Depends On**: Task 1-5
- **Description**: 
  - 编译后端项目确保无语法错误
  - 启动后端服务验证配置正确
- **Acceptance Criteria Addressed**: [所有AC]
- **Test Requirements**:
  - `programmatic` TR-6.1: mvn compile成功 ✓
  - `programmatic` TR-6.2: 应用启动成功 ✓
- **Notes**: 需要数据库正常运行