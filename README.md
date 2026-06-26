<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://img.shields.io/badge/LHXCSDN%20%E7%A4%BE%E5%8C%BA-1a1a2e?style=for-the-badge&logo=dev.to&logoColor=white">
    <img alt="LHXCSDN Community" src="https://img.shields.io/badge/LHXCSDN%20%E7%A4%BE%E5%8C%BA-0055A4?style=for-the-badge&logo=dev.to&logoColor=white">
  </picture>
</p>

<p align="center">
  基于 <b>Spring Boot 3</b> + <b>Vue 3</b> 的全栈技术博客社区，模拟 CSDN 核心功能，集成 RBAC 权限管理、文章发布、评论审核、全站搜索等完整特性。
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.4+-4FC08D?logo=vuedotjs&logoColor=white" alt="Vue 3">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white" alt="Redis">
  <img src="https://img.shields.io/badge/MyBatis-3.0-000000?logo=mybatis&logoColor=white" alt="MyBatis">
  <img src="https://img.shields.io/badge/Element%20Plus-409EFF?logo=element&logoColor=white" alt="Element Plus">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

---

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [核心功能](#核心功能)
- [系统架构](#系统架构)
- [快速开始](#快速开始)
- [API 概览](#api-概览)
- [数据库设计](#数据库设计)
- [项目结构](#项目结构)
- [许可证](#许可证)

---

## 项目简介

LHXCSDN 是一个仿 CSDN 的技术博客社区平台，采用前后端分离架构。后端基于 Spring Boot 3 + MyBatis + Spring Security + JWT 构建，前端基于 Vue 3 + Element Plus + ECharts 构建。项目实现了完整的用户认证授权、文章发布与管理、评论审核、全站搜索、热度排行、收藏点赞、管理后台等核心功能，并基于 RBAC 模型实现了细粒度的权限控制。

---

## 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| Spring Boot 3.5.13 | 应用基础框架 |
| Spring Security | 认证与授权框架 |
| MyBatis 3.0.5 | ORM 持久层框架 |
| MySQL 8.0 | 关系型数据库 |
| Redis 7 | 缓存中间件 |
| Druid 1.2.20 | 数据库连接池 |
| JWT (jjwt 0.11.5) | 无状态令牌认证 |
| FastJSON 2.0.43 | JSON 序列化工具 |
| PageHelper 2.1.0 | 分页插件 |
| Lombok | 代码简化工具 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式前端框架 |
| Vue Router 4 | 前端路由管理 |
| Vuex 4 | 状态管理 |
| Element Plus | UI 组件库 |
| ECharts 6 | 数据可视化图表 |
| md-editor-v3 | Markdown 编辑器 |
| Axios | HTTP 请求库 |
| Sass | CSS 预处理器 |

---

## 核心功能

### 用户系统
- 用户注册与登录（JWT 无状态认证）
- 个人资料管理（头像上传、昵称、简介、职业、教育背景等）
- 个人中心（文章管理、收藏管理）

### 文章系统
- 文章发布与编辑（支持 Markdown 编辑器）
- 文章状态管理（草稿/已发布/已删除）
- 文章分类与多标签关联
- 全站文章搜索（按标题/摘要模糊匹配）
- 热度排行（按浏览量排序 Top10）
- 文章点赞与收藏

### 评论系统
- 文章评论
- 评论审核机制（待审核/通过/拒绝）
- 管理员批量审核
- 评论统计

### 权限管理 (RBAC)
- 完整的角色-权限模型
- 四层角色体系：SUPER_ADMIN / CONTENT_ADMIN / USER_ADMIN / USER
- 细粒度方法级权限控制（@PreAuthorize）
- 管理后台用户/角色/权限管理

### 管理后台
- 用户管理（列表/删除/角色分配）
- 文章管理（列表/筛选/编辑/删除）
- 评论审核（单条审核/批量审核）
- 标签管理（CRUD）
- 分类管理
- 数据统计仪表盘（ECharts 图表）

---

## 系统架构

```
lhx-csdn/
├── backend/                         # Spring Boot 后端
│   ├── controller/                  # 控制器层（REST API）
│   │   ├── ArticleController        # 文章公共接口
│   │   ├── UserController           # 用户接口
│   │   ├── FavoriteController       # 收藏接口
│   │   ├── AdminController          # 系统管理接口
│   │   └── AdminArticleController   # 内容管理接口
│   ├── service/
│   │   ├── ArticleService           # 文章服务
│   │   └── UserService              # 用户服务
│   ├── mapper/                      # MyBatis 数据访问层
│   ├── pojo/entity/                 # 数据实体
│   ├── config/
│   │   └── SecurityConfig           # Spring Security 配置
│   ├── filter/
│   │   └── JwtAuthenticationFilter  # JWT 鉴权过滤器
│   ├── utils/
│   │   └── JwtUtils                 # JWT 工具类
│   └── common/
│       └── Result                   # 统一响应封装
│
└── web/                             # Vue 3 前端
    ├── src/
    │   ├── views/
    │   │   ├── Login.vue            # 登录/注册
    │   │   ├── Home.vue             # 首页
    │   │   ├── ArticleDetail.vue    # 文章详情
    │   │   ├── Publish.vue          # 发布文章
    │   │   ├── UserCenter.vue       # 个人中心
    │   │   ├── AdminDashboard.vue   # 管理后台
    │   │   └── Search.vue           # 搜索
    │   ├── router/                  # 路由配置
    │   └── store/                   # 状态管理
    └── vue.config.js                # 代理配置
```

---

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 7+
- Node.js 18+
- Maven 3.8+（或使用项目内置 mvnw）

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lhx_csdn DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

项目启动时会自动执行 `schema.sql` 和 `data.sql` 完成建表与初始数据填充。

### 配置修改

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lhx_csdn?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456       # 修改为你的 MySQL 密码
  data:
    redis:
      host: localhost
      port: 6379
```

### 一键启动

项目提供了 `start.sh` 一键启动脚本：

```bash
# 启动服务（自动检查环境、构建前端、Maven 打包、启动后端）
./start.sh

# 停止服务
./start.sh stop

# 重启服务
./start.sh restart

# 强制重新构建前端后启动
./start.sh --rebuild

# 查看运行状态
./start.sh status
```

### 手动启动

```bash
# 1. 构建后端
./mvnw clean package -DskipTests

# 2. 构建前端
cd web && npm install && npm run build

# 3. 启动后端
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 访问项目

| 地址 | 说明 |
|------|------|
| http://localhost:8088/ | 前端首页 |
| http://localhost:8088/#/login | 登录页面 |
| http://localhost:8088/#/admin | 管理后台 |

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin |
| 普通用户 | （需注册） | - |

---

## API 概览

### 用户接口 (`/api/user`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/user/register | 用户注册 | 游客 |
| POST | /api/user/login | 用户登录 | 游客 |
| GET | /api/user/me | 获取当前用户信息 | 登录用户 |
| POST | /api/user/update | 更新个人资料 | 登录用户 |
| POST | /api/user/upload-avatar | 上传头像 | 登录用户 |

### 文章接口 (`/api/article`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/article/list | 文章列表（首页） | 游客 |
| GET | /api/article/{id} | 文章详情 | 游客 |
| GET | /api/article/search?keyword= | 全站搜索 | 游客 |
| GET | /api/article/hot | 热度排行 Top10 | 游客 |
| GET | /api/article/category/{category} | 按分类获取文章 | 游客 |
| GET | /api/article/{id}/comments | 获取评论 | 游客 |
| GET | /api/article/user/{authorId} | 用户文章列表 | 游客 |
| POST | /api/article/publish | 发布文章 | 登录用户 |
| POST | /api/article/comment | 提交评论 | 登录用户 |
| POST | /api/article/{id}/like | 点赞 | 登录用户 |

### 管理接口 (`/api/admin`)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/admin/users | 用户列表 | sys:user:view |
| POST | /api/admin/assignRole | 分配角色 | sys:role:assign |
| DELETE | /api/admin/user/{id} | 删除用户 | sys:user:delete |
| GET | /api/admin/articles | 文章管理列表 | sys:article:view |
| PUT | /api/admin/articles/{id}/status | 更新文章状态 | sys:article:edit |
| PUT | /api/admin/articles/comments/{id}/audit | 审核评论 | sys:comment:audit |
| GET | /api/admin/articles/tags | 标签列表 | sys:tag:manage |
| POST | /api/admin/articles/tags | 创建标签 | sys:tag:manage |

---

## 数据库设计

### 实体关系图

数据库 `lhx_csdn` 共包含 9 张表：

| 表名 | 说明 |
|------|------|
| user | 用户表（username, password, nickname, avatar, bio, fans_count 等） |
| article | 文章表（title, content, summary, category, status, view_count 等） |
| comment | 评论表（content, status, audit_reason, audit_by 等） |
| tag | 标签表（name, description） |
| article_tag | 文章-标签关联表（多对多） |
| favorite | 收藏表（用户-文章关联） |
| role | 角色表（code, name, description） |
| permission | 权限表（code, name, description） |
| user_role | 用户-角色关联表 |

### RBAC 权限模型

- **SUPER_ADMIN** -- 拥有所有系统权限
- **CONTENT_ADMIN** -- 内容管理（文章/评论审核，分类/标签管理）
- **USER_ADMIN** -- 用户管理（删除用户，分配角色，日志清理）
- **USER** -- 普通用户（基础文章浏览与发布权限）

---

## 项目结构

```
LHXCSDN/
├── pom.xml                          # Maven 依赖配置
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── start.sh                         # 一键启动脚本
├── src/
│   └── main/
│       ├── java/com/lhxcsdn/demo/
│       │   ├── LhxcsdnApplication.java
│       │   ├── common/              # 通用类
│       │   ├── config/              # 配置类
│       │   ├── controller/          # 控制器
│       │   ├── filter/              # 过滤器
│       │   ├── mapper/              # 数据访问
│       │   ├── pojo/entity/         # 实体类
│       │   ├── service/             # 服务层
│       │   └── utils/               # 工具类
│       └── resources/
│           ├── application.yml      # 应用配置
│           ├── schema.sql           # 建表脚本
│           └── data.sql             # 初始数据
├── web/                             # Vue 3 前端
│   ├── package.json
│   ├── vue.config.js
│   └── src/
│       ├── views/                   # 页面组件
│       ├── router/                  # 路由
│       └── store/                   # 状态管理
├── uploads/                         # 上传文件目录
└── target/                          # 构建产物
```

---

## 许可证

本项目基于 MIT 许可证开源。详见 [LICENSE](LICENSE) 文件。

