# 微信小程序说明
# 微信小程序不需要 Docker 部署，需要使用微信开发者工具进行开发和发布
# 此 Dockerfile 仅用于提供一个静态文件服务，方便查看小程序源码

FROM nginx:alpine

WORKDIR /usr/share/nginx/html

# 复制小程序源码
COPY . .

# 创建一个简单的索引页面
RUN echo '<!DOCTYPE html><html><head><meta charset="utf-8"><title>微信小程序源码</title></head><body><h1>微信小程序源码目录</h1><p>请使用微信开发者工具打开此目录进行开发</p><p>后端API地址请在 utils/config.js 中配置</p></body></html>' > index.html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
