# 微信外卖平台

基于 Spring Boot + Vue 3 + 微信小程序的外卖点餐系统，包含用户端小程序和管理后台。

---

## 快速启动

### 一键启动（推荐）

```bash
# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看启动日志（确认启动成功）
docker compose logs backend frontend-admin
```

> 前置要求：[Docker](https://www.docker.com/products/docker-desktop/) 20.10+ 和 Docker Compose V2

启动成功后，日志将显示：

```
✓ Startup Success - 微信外卖平台后端服务启动成功!
✓ Startup Success - 微信外卖平台管理后台启动成功!
```

### 常用命令

```bash
docker compose down                              # 停止服务
docker compose restart                           # 重启服务
docker compose up -d --build                     # 重建并启动
docker compose down -v && docker compose up -d --build  # 重置数据库
docker compose logs -f backend                   # 实时查看后端日志
docker compose logs -f frontend-admin            # 实时查看前端日志
```

---

## 服务访问

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost:8081 | Vue 3 + Element Plus |
| 后端 API | http://localhost:8080 | Spring Boot RESTful API |
| API 文档 | http://localhost:8080/swagger-ui.html | Swagger UI |

---

## 测试账号

### 管理后台

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 超级管理员 |

### 小程序端（开发环境）

开发环境下，小程序登录会自动使用预置的测试用户：

| 昵称 | 手机号 | 预置数据 |
|------|--------|---------|
| 测试用户 | 13800138000 | 2个收货地址、5条测试订单 |

#### 小程序启动步骤

1. 下载 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 导入项目，目录选择 `frontend-mp/`
3. AppID 填写 `wx3c8ed09e3826f818`（或测试号）
4. 「详情 → 本地设置」勾选「不校验合法域名」
5. 确保后端已启动，点击「编译」运行

---

## 项目结构

```
├── backend/                 # 后端服务 (Spring Boot)
├── frontend-admin/          # 管理后台 (Vue 3 + Element Plus)
├── frontend-mp/             # 微信小程序
├── docs/                    # 项目文档
└── docker-compose.yml       # Docker Compose 配置
```

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17、Spring Boot 3.2、MyBatis-Plus、MySQL 8.0、Redis、JWT |
| 管理端 | Vue 3、Vite 5、Element Plus、Pinia |
| 小程序 | 微信原生开发 |
| DevOps | Docker、Docker Compose、Kubernetes、GitHub Actions |

---

## 模拟数据

项目启动时自动初始化测试数据，如需重置可执行：

```bash
docker compose down -v && docker compose up -d --build
```

### 商家数据

| 商家 | 起送价 | 配送费 | 评分 |
|------|--------|--------|------|
| 麦当劳(中关村店) | ¥20 | ¥5 | 4.8 |
| 肯德基(五道口店) | ¥25 | ¥4 | 4.7 |
| 海底捞火锅(望京店) | ¥100 | 免配送 | 4.9 |
| 星巴克(国贸店) | ¥30 | ¥6 | 4.6 |
| 沙县小吃(西单店) | ¥10 | ¥3 | 4.5 |

### 菜品数据

| 商家 | 分类 | 菜品数 |
|------|------|--------|
| 麦当劳 | 超值套餐、汉堡主食、小食甜点、饮料 | 10 |
| 肯德基 | 炸鸡套餐、汉堡、小食、饮品 | 5 |
| 海底捞 | 锅底、肉类 | 4 |
| 星巴克 | 咖啡、茶饮、甜点 | 7 |
| 沙县小吃 | 拌面、汤类、蒸饺 | 4 |

---

## 本地开发

> 适合需要修改代码的开发者

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

### 启动步骤

```bash
# 1. 配置环境变量
cp .env.example .env

# 2. 启动后端
cd backend && mvn spring-boot:run

# 3. 启动前端（新终端）
cd frontend-admin && npm install && npm run dev
```

---

## 测试

```bash
# 后端测试
cd backend
mvn test                           # 运行所有测试
mvn jacoco:report                  # 生成覆盖率报告

# 前端测试
cd frontend-admin
npm run test                       # 运行单元测试
npm run lint                       # ESLint 检查
```

---

## 生产部署

### Docker 部署

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Kubernetes 部署

```bash
kubectl apply -k k8s/
```

详细说明请参考 [部署指南](docs/DEPLOYMENT.md)

---

## 项目文档

### 核心文档

| 文档 | 说明 |
|------|------|
| [需求文档](docs/Requirements.md) | 详细功能需求、数据模型、页面清单、验收标准 |
| [接口设计文档](docs/API.md) | 完整的 RESTful API 接口规范和示例 |
| [项目设计文档](docs/project_design.md) | 系统架构、ER 图、接口清单、UI 规范 |

### 其他文档

| 文档 | 说明 |
|------|------|
| [技术选型说明](docs/TECH_STACK.md) | 技术栈选择理由 |
| [部署指南](docs/DEPLOYMENT.md) | 生产环境部署说明 |
| [用户手册](docs/USER_MANUAL.md) | 系统使用说明 |

---

## 许可证

MIT License
