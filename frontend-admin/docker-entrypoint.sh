#!/bin/sh

echo ""
echo "----------------------------------------------------------"
echo "  ✓ Startup Success - 微信外卖平台管理后台启动成功!"
echo "----------------------------------------------------------"
echo "  Frontend Admin:  http://localhost:8081"
echo "  Backend API:     http://localhost:8080"
echo "----------------------------------------------------------"
echo ""

exec nginx -g "daemon off;"
