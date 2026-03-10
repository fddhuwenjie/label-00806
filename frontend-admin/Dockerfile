# 多阶段构建 - 支持 ARM64 和 AMD64
FROM --platform=$BUILDPLATFORM node:20-alpine AS builder

WORKDIR /app

# 复制 package.json 并安装依赖
COPY package*.json ./
RUN npm install

# 复制源码并构建
COPY . .
RUN npm run build

# 运行阶段 - 使用 nginx 跨平台镜像
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 复制启动脚本
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# 暴露端口
EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
