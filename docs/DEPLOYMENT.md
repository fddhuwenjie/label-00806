# 部署指南

## 目录

1. [环境要求](#环境要求)
2. [本地开发部署](#本地开发部署)
3. [Docker 部署](#docker-部署)
4. [Kubernetes 部署](#kubernetes-部署)
5. [云服务部署](#云服务部署)
6. [环境变量配置](#环境变量配置)
7. [HTTPS 配置](#https-配置)
8. [监控配置](#监控配置)
9. [性能调优](#性能调优)
10. [备份与恢复](#备份与恢复)

## 环境要求

### 后端
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

### 前端
- Node.js 18+
- npm 9+

## 本地开发部署

### 1. 克隆项目

```bash
git clone <repository-url>
cd food-delivery
```

### 2. 配置环境变量

```bash
cp .env.example .env
# 编辑 .env 文件，填入实际配置
```

### 3. 启动 MySQL 和 Redis

```bash
# 使用 Docker 快速启动依赖服务
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=food_delivery \
  mysql:8.0

docker run -d --name redis -p 6379:6379 redis:7
```

### 4. 初始化数据库

```bash
# 连接 MySQL 执行初始化脚本
mysql -h localhost -u root -p food_delivery < backend/src/main/resources/schema.sql
```

### 5. 启动后端服务

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 6. 启动前端管理端

```bash
cd frontend-admin
npm install
npm run dev
```

### 7. 访问服务

- 后端 API: http://localhost:8080
- API 文档: http://localhost:8080/swagger-ui.html
- 管理后台: http://localhost:5173 (开发模式) 或 http://localhost:8081 (Docker部署)

## Docker 部署

### 1. 构建镜像

```bash
# 构建后端镜像
cd backend
docker build -t food-delivery-backend:latest .

# 构建前端管理端镜像
cd frontend-admin
docker build -t food-delivery-admin:latest .
```

### 2. 使用 Docker Compose 启动

```bash
# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看启动日志（确认启动成功）
docker compose logs backend frontend-admin
```

启动成功后，日志将显示：
```
✓ Startup Success - 微信外卖平台后端服务启动成功!
✓ Startup Success - 微信外卖平台管理后台启动成功!
```

### 3. 服务访问

- 后端 API: http://localhost:8080
- 管理后台: http://localhost:8081
- API 文档: http://localhost:8080/swagger-ui.html

## Kubernetes 部署

### 1. 创建命名空间

```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. 创建配置和密钥

```bash
# 编辑 secret.yaml 填入实际的密钥值
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
```

### 3. 部署应用

```bash
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```

### 4. 验证部署

```bash
# 查看 Pod 状态
kubectl get pods -n food-delivery

# 查看服务状态
kubectl get svc -n food-delivery

# 查看 Ingress
kubectl get ingress -n food-delivery
```

## 环境变量配置

### 后端环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| SPRING_PROFILES_ACTIVE | 激活的配置文件 | dev |
| SPRING_DATASOURCE_URL | 数据库连接URL | - |
| SPRING_DATASOURCE_USERNAME | 数据库用户名 | root |
| SPRING_DATASOURCE_PASSWORD | 数据库密码 | - |
| REDIS_HOST | Redis 主机 | localhost |
| REDIS_PORT | Redis 端口 | 6379 |
| REDIS_PASSWORD | Redis 密码 | - |
| JWT_SECRET | JWT 密钥 | - |
| JWT_EXPIRATION | JWT 过期时间(ms) | 86400000 |
| WECHAT_APPID | 微信小程序 AppID | - |
| WECHAT_SECRET | 微信小程序 Secret | - |

## HTTPS 配置

### 使用 Let's Encrypt (Kubernetes)

1. 安装 cert-manager:
```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

2. 创建 ClusterIssuer:
```yaml
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: your-email@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
      - http01:
          ingress:
            class: nginx
```

### Nginx 配置 (Docker)

在 `nginx.conf` 中添加 SSL 配置:
```nginx
server {
    listen 443 ssl;
    server_name api.fooddelivery.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    
    location / {
        proxy_pass http://backend:8080;
    }
}
```

## 监控配置

### Actuator 端点

后端服务暴露以下监控端点：

- `/actuator/health` - 健康检查
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 指标数据
- `/actuator/prometheus` - Prometheus 格式指标

### Prometheus 配置

```yaml
scrape_configs:
  - job_name: 'food-delivery-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```

### Grafana 仪表板

推荐使用以下 Grafana 仪表板：
- Spring Boot Statistics: ID 12900
- JVM Micrometer: ID 4701

## 性能调优

### JVM 参数配置

```bash
# 生产环境推荐 JVM 参数
JAVA_OPTS="-Xms512m -Xmx1024m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/logs/heapdump.hprof"
```

### 数据库连接池配置

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000
```

### Redis 缓存配置

```yaml
spring:
  cache:
    redis:
      time-to-live: 3600000  # 1小时
      cache-null-values: false
  data:
    redis:
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 4
```

### Nginx 性能优化

```nginx
# nginx.conf
worker_processes auto;
worker_connections 4096;

http {
    # 开启 gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript;
    
    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
    
    # 连接优化
    keepalive_timeout 65;
    keepalive_requests 1000;
    
    # 缓冲区配置
    proxy_buffer_size 128k;
    proxy_buffers 4 256k;
    proxy_busy_buffers_size 256k;
}
```

## 备份与恢复

### MySQL 备份

```bash
# 全量备份
mysqldump -h localhost -u root -p food_delivery > backup_$(date +%Y%m%d).sql

# 定时备份脚本 (crontab)
0 2 * * * /path/to/backup.sh

# backup.sh
#!/bin/bash
BACKUP_DIR=/data/backup/mysql
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -h localhost -u root -p${MYSQL_PASSWORD} food_delivery | gzip > ${BACKUP_DIR}/food_delivery_${DATE}.sql.gz
# 保留最近 7 天的备份
find ${BACKUP_DIR} -name "*.sql.gz" -mtime +7 -delete
```

### MySQL 恢复

```bash
# 从备份恢复
gunzip < backup_20240101.sql.gz | mysql -h localhost -u root -p food_delivery
```

### Redis 备份

```bash
# 触发 RDB 快照
redis-cli BGSAVE

# 复制 RDB 文件
cp /var/lib/redis/dump.rdb /data/backup/redis/dump_$(date +%Y%m%d).rdb
```

## 云服务部署

### AWS 部署架构

```
                    ┌─────────────┐
                    │   Route 53  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │     ALB     │
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼────┐      ┌────▼────┐      ┌────▼────┐
    │   ECS   │      │   ECS   │      │   ECS   │
    │ Backend │      │ Backend │      │ Frontend│
    └────┬────┘      └────┬────┘      └─────────┘
         │                │
         └────────┬───────┘
                  │
    ┌─────────────┼─────────────┐
    │             │             │
┌───▼───┐   ┌────▼────┐   ┌────▼────┐
│  RDS  │   │ElastiCache│  │   S3   │
│ MySQL │   │  Redis   │   │ Static │
└───────┘   └──────────┘   └────────┘
```

### AWS ECS 部署

```bash
# 创建 ECR 仓库
aws ecr create-repository --repository-name food-delivery-backend

# 登录 ECR
aws ecr get-login-password | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com

# 推送镜像
docker tag food-delivery-backend:latest <account>.dkr.ecr.<region>.amazonaws.com/food-delivery-backend:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/food-delivery-backend:latest
```

### 阿里云部署

```bash
# 使用阿里云容器服务 ACK
# 1. 创建 ACK 集群
# 2. 配置镜像仓库
# 3. 部署应用

# 推送镜像到阿里云镜像仓库
docker login --username=<username> registry.cn-hangzhou.aliyuncs.com
docker tag food-delivery-backend:latest registry.cn-hangzhou.aliyuncs.com/<namespace>/food-delivery-backend:latest
docker push registry.cn-hangzhou.aliyuncs.com/<namespace>/food-delivery-backend:latest
```

## 故障排查

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接字符串和凭据

2. **Redis 连接失败**
   - 检查 Redis 服务状态
   - 验证密码配置

3. **JWT 认证失败**
   - 确保 JWT_SECRET 在所有实例中一致
   - 检查 Token 是否过期

### 日志查看

```bash
# Docker
docker-compose logs -f backend

# Kubernetes
kubectl logs -f deployment/food-delivery-backend -n food-delivery
```
