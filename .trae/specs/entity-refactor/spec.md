# 房屋租赁平台 - 数据库结构对齐重构 PRD

## Overview
- **Summary**: 根据现有数据库表结构（init_project_v3.sql），对后端Entity层和前端API层进行全面对齐和重构，确保代码与数据库结构100%一致。
- **Purpose**: 解决当前代码与数据库结构不一致的问题，消除硬编码字段名，统一前后端数据交互格式。
- **Target Users**: 开发团队、维护人员

## Goals
- Entity层字段与SQL表结构100%驼峰映射
- MyBatis-Plus自动处理下划线与驼峰转换
- 前端request.js自动解包Result<T>
- 前端API参数统一使用驼峰命名

## Non-Goals (Out of Scope)
- 修改数据库表结构（数据库是真理，不可修改）
- 修改业务逻辑实现
- 新增功能特性

## Background & Context
当前系统存在以下问题：
1. 部分Entity字段与SQL表字段不匹配
2. 存在硬编码字段名的情况
3. 前端响应未自动解包Result<T>
4. 前端API参数命名不统一

## Functional Requirements
- **FR-1**: 检查并修复所有Entity类，确保字段与SQL表完全一致
- **FR-2**: 确保MyBatis-Plus配置正确开启驼峰转换
- **FR-3**: 修改前端request.js，自动解包Result<T>的data字段
- **FR-4**: 检查并修复前端API参数，确保统一使用驼峰命名

## Non-Functional Requirements
- **NFR-1**: 代码符合Java和JavaScript编码规范
- **NFR-2**: 不影响现有业务逻辑
- **NFR-3**: 保持向后兼容

## Constraints
- **Technical**: Spring Boot 2.7.x, MyBatis-Plus 3.x, Vue 3, Element Plus
- **Dependencies**: 数据库结构固定，不可修改

## Assumptions
- 数据库已正确初始化（init_project_v3.sql已执行）
- 所有Entity继承自BaseEntity，BaseEntity已包含id, createTime, updateTime, createBy, updateBy, isDeleted, remark, version

## Acceptance Criteria

### AC-1: Entity字段与SQL表完全一致
- **Given**: SQL表结构作为基准
- **When**: 检查所有Entity类
- **Then**: 每个SQL字段都有对应的Java驼峰字段，@TableLogic和@Version注解正确
- **Verification**: `human-judgment`

### AC-2: MyBatis-Plus驼峰转换配置正确
- **Given**: application.yml配置文件
- **When**: 检查mybatis-plus配置
- **Then**: map-underscore-to-camel-case设置为true
- **Verification**: `programmatic`

### AC-3: 前端响应自动解包
- **Given**: 后端返回Result<T>格式
- **When**: 前端发起请求
- **Then**: 响应拦截器自动提取data字段返回
- **Verification**: `programmatic`

### AC-4: 前端API参数驼峰命名
- **Given**: 前端API文件
- **When**: 检查所有请求参数
- **Then**: 所有JSON Payload使用驼峰命名，无下划线字段
- **Verification**: `human-judgment`

## Open Questions
- [ ] 是否需要添加字段校验注解？