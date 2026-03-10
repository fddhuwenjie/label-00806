# 安全指南

## 概述

本文档描述了外卖平台的安全措施和最佳实践。

## 认证与授权

### JWT Token

- 使用 JWT (JSON Web Token) 进行无状态认证
- Token 有效期默认 24 小时
- Token 存储在请求头 `Authorization: Bearer <token>`

### 密码安全

- 使用 BCrypt 算法加密存储密码
- BCrypt 强度因子设置为 12
- 禁止明文存储密码

```java
// 密码加密示例
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

## 输入验证

### 参数校验

使用 Spring Validation 进行参数校验：

```java
@PostMapping
public Result add(@Valid @RequestBody MerchantDTO dto) {
    // ...
}
```

### XSS 防护

- 所有用户输入进行 HTML 转义
- 使用 `SecurityUtil.escapeHtml()` 方法

### SQL 注入防护

- 使用 MyBatis-Plus 参数化查询
- 禁止拼接 SQL 字符串
- 使用 `SecurityUtil.containsSqlInjection()` 检测

## HTTP 安全头

系统自动添加以下安全响应头：

| 响应头 | 值 | 作用 |
|--------|-----|------|
| X-Frame-Options | DENY | 防止点击劫持 |
| X-Content-Type-Options | nosniff | 防止 MIME 类型嗅探 |
| X-XSS-Protection | 1; mode=block | XSS 保护 |
| Content-Security-Policy | default-src 'self' | 内容安全策略 |
| Referrer-Policy | strict-origin-when-cross-origin | 引用策略 |

## HTTPS 配置

### Kubernetes Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
    - hosts:
        - api.fooddelivery.com
      secretName: food-delivery-tls
```

### Nginx 配置

```nginx
server {
    listen 443 ssl http2;
    server_name api.fooddelivery.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_prefer_server_ciphers off;
    
    # HSTS
    add_header Strict-Transport-Security "max-age=63072000" always;
}
```

## 敏感数据处理

### 数据脱敏

```java
// 手机号脱敏: 138****5678
securityUtil.maskPhone("13812345678");

// 身份证脱敏: 110101********1234
securityUtil.maskIdCard("110101199001011234");

// 邮箱脱敏: t***@example.com
securityUtil.maskEmail("test@example.com");
```

### 日志脱敏

- 禁止在日志中记录密码、Token 等敏感信息
- 记录用户信息时进行脱敏处理

## 密钥管理

### 环境变量

敏感配置通过环境变量注入：

```yaml
jwt:
  secret: ${JWT_SECRET}
  
spring:
  datasource:
    password: ${SPRING_DATASOURCE_PASSWORD}
```

### Kubernetes Secrets

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: food-delivery-secret
type: Opaque
stringData:
  JWT_SECRET: "your-secret-key"
  SPRING_DATASOURCE_PASSWORD: "your-db-password"
```

## 安全扫描

### CodeQL

项目配置了 GitHub CodeQL 自动安全扫描：

```yaml
# .github/workflows/codeql.yml
- name: Initialize CodeQL
  uses: github/codeql-action/init@v3
  with:
    languages: java, javascript
```

### 依赖漏洞扫描

定期检查依赖漏洞：

```bash
# Maven
mvn dependency-check:check

# npm
npm audit
```

## 安全检查清单

### 开发阶段

- [ ] 所有用户输入进行验证和转义
- [ ] 使用参数化查询，避免 SQL 注入
- [ ] 敏感数据加密存储
- [ ] 日志中不包含敏感信息
- [ ] 错误信息不暴露系统细节

### 部署阶段

- [ ] 启用 HTTPS
- [ ] 配置安全响应头
- [ ] 使用环境变量管理密钥
- [ ] 限制数据库访问权限
- [ ] 配置防火墙规则

### 运维阶段

- [ ] 定期更新依赖
- [ ] 监控异常登录
- [ ] 定期安全审计
- [ ] 备份加密存储

## 安全事件响应

### 发现漏洞

1. 立即评估影响范围
2. 临时缓解措施
3. 修复并测试
4. 部署更新
5. 事后复盘

### 安全事件分级

| 级别 | 描述 | 响应时间 |
|------|------|----------|
| P0 | 严重漏洞，数据泄露风险 | 立即响应 |
| P1 | 高危漏洞，可能被利用 | 4小时内 |
| P2 | 中危漏洞，有限影响 | 24小时内 |
| P3 | 低危漏洞，轻微影响 | 1周内 |

### 联系方式

安全问题请联系：security@fooddelivery.com

---

## 安全审计工具

### 推荐工具

| 工具 | 用途 | 使用频率 |
|------|------|----------|
| OWASP ZAP | Web 应用安全扫描 | 每次发布前 |
| SonarQube | 代码质量和安全分析 | 每次提交 |
| Trivy | 容器镜像漏洞扫描 | 每次构建 |
| npm audit | Node.js 依赖漏洞检查 | 每周 |
| OWASP Dependency-Check | Java 依赖漏洞检查 | 每周 |

### 自动化安全扫描配置

```yaml
# GitHub Actions 安全扫描示例
security-scan:
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    
    # CodeQL 分析
    - name: Initialize CodeQL
      uses: github/codeql-action/init@v3
      with:
        languages: java, javascript
    
    # 依赖漏洞扫描
    - name: Dependency Check
      run: |
        cd backend && mvn dependency-check:check
        cd ../frontend-admin && npm audit --audit-level=high
    
    # 容器镜像扫描
    - name: Trivy Scan
      uses: aquasecurity/trivy-action@master
      with:
        image-ref: 'food-delivery-backend:latest'
        severity: 'CRITICAL,HIGH'
```

---

## 防止 SQL 注入

### MyBatis-Plus 安全实践

```java
// 正确：使用参数化查询
@Select("SELECT * FROM merchant WHERE name = #{name}")
Merchant findByName(@Param("name") String name);

// 错误：字符串拼接（禁止使用）
// @Select("SELECT * FROM merchant WHERE name = '" + name + "'")

// 使用 QueryWrapper 安全查询
QueryWrapper<Merchant> wrapper = new QueryWrapper<>();
wrapper.eq("name", name)
       .eq("status", 1);
merchantMapper.selectList(wrapper);
```

### SQL 注入检测

```java
/**
 * 检测 SQL 注入风险
 */
public static boolean containsSqlInjection(String input) {
    if (input == null) return false;
    String[] sqlKeywords = {
        "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", 
        "UNION", "OR", "AND", "--", "/*", "*/"
    };
    String upperInput = input.toUpperCase();
    for (String keyword : sqlKeywords) {
        if (upperInput.contains(keyword)) {
            return true;
        }
    }
    return false;
}
```

---

## Rate Limiting（请求限流）

### 配置示例

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(100); // 每秒100个请求
    }
}

// 在 Controller 中使用
@GetMapping("/api/data")
public Result getData() {
    if (!rateLimiter.tryAcquire()) {
        throw new BusinessException("请求过于频繁，请稍后再试");
    }
    // 处理请求
}
```

### Nginx 限流配置

```nginx
# 定义限流区域
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

server {
    location /api/ {
        # 应用限流，允许突发20个请求
        limit_req zone=api_limit burst=20 nodelay;
        
        proxy_pass http://backend;
    }
}
```

---

## 安全配置检查清单

### 生产环境部署前检查

- [ ] 禁用调试模式和详细错误信息
- [ ] 配置生产环境 CORS 白名单
- [ ] 启用 HTTPS 并配置 HSTS
- [ ] 更新所有默认密码
- [ ] 配置数据库访问白名单
- [ ] 启用访问日志和审计日志
- [ ] 配置防火墙规则
- [ ] 设置合理的请求限流
- [ ] 配置自动安全扫描
- [ ] 准备安全事件响应预案

---

## 定期安全审计

### 审计周期

| 审计类型 | 频率 | 负责人 |
|----------|------|--------|
| 代码安全审查 | 每次 PR | 开发团队 |
| 依赖漏洞扫描 | 每周 | DevOps |
| 渗透测试 | 每季度 | 安全团队 |
| 安全配置审计 | 每月 | 运维团队 |
| 访问权限审计 | 每月 | 管理员 |

### 审计内容

#### 1. 代码安全审查

```bash
# 使用 SonarQube 进行代码分析
mvn sonar:sonar \
  -Dsonar.projectKey=food-delivery \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token

# 检查项目
# - SQL 注入风险
# - XSS 漏洞
# - 敏感信息泄露
# - 不安全的加密算法
# - 硬编码密码
```

#### 2. 依赖漏洞扫描

```bash
# Java 依赖检查
cd backend
mvn dependency-check:check
# 报告位置: target/dependency-check-report.html

# Node.js 依赖检查
cd frontend-admin
npm audit --json > audit-report.json
npm audit fix  # 自动修复

# 容器镜像扫描
trivy image food-delivery-backend:latest --severity HIGH,CRITICAL
```

#### 3. 渗透测试清单

- [ ] 身份认证测试
  - 暴力破解防护
  - 会话管理
  - 密码策略
- [ ] 授权测试
  - 越权访问
  - 权限提升
- [ ] 输入验证测试
  - SQL 注入
  - XSS 攻击
  - 命令注入
- [ ] 业务逻辑测试
  - 支付绕过
  - 订单篡改
- [ ] API 安全测试
  - 未授权访问
  - 数据泄露

### 审计报告模板

```markdown
# 安全审计报告

## 基本信息
- 审计日期: YYYY-MM-DD
- 审计范围: [系统/模块名称]
- 审计人员: [姓名]

## 发现问题

### 高危问题
| 编号 | 问题描述 | 影响范围 | 修复建议 | 状态 |
|------|----------|----------|----------|------|
| H001 | ... | ... | ... | 待修复 |

### 中危问题
| 编号 | 问题描述 | 影响范围 | 修复建议 | 状态 |
|------|----------|----------|----------|------|
| M001 | ... | ... | ... | 待修复 |

### 低危问题
| 编号 | 问题描述 | 影响范围 | 修复建议 | 状态 |
|------|----------|----------|----------|------|
| L001 | ... | ... | ... | 待修复 |

## 修复跟踪
- 高危问题修复截止日期: [日期]
- 下次复查日期: [日期]

## 总结与建议
[审计总结和改进建议]
```

### 自动化审计脚本

```bash
#!/bin/bash
# security-audit.sh - 自动化安全审计脚本

DATE=$(date +%Y%m%d)
REPORT_DIR="./security-reports/${DATE}"
mkdir -p ${REPORT_DIR}

echo "=== 开始安全审计 ==="

# 1. 依赖漏洞扫描
echo "检查 Java 依赖..."
cd backend
mvn dependency-check:check -DoutputDirectory=${REPORT_DIR}

echo "检查 Node.js 依赖..."
cd ../frontend-admin
npm audit --json > ${REPORT_DIR}/npm-audit.json

# 2. 代码安全扫描
echo "运行 CodeQL 分析..."
# codeql database create --language=java ${REPORT_DIR}/codeql-db
# codeql database analyze ${REPORT_DIR}/codeql-db --format=sarif-latest --output=${REPORT_DIR}/codeql-results.sarif

# 3. 容器镜像扫描
echo "扫描容器镜像..."
trivy image food-delivery-backend:latest --format json --output ${REPORT_DIR}/trivy-backend.json
trivy image food-delivery-admin:latest --format json --output ${REPORT_DIR}/trivy-admin.json

# 4. 生成汇总报告
echo "生成审计报告..."
cat > ${REPORT_DIR}/summary.md << EOF
# 安全审计汇总 - ${DATE}

## 扫描结果

### 依赖漏洞
- Java: 查看 dependency-check-report.html
- Node.js: 查看 npm-audit.json

### 容器镜像
- Backend: 查看 trivy-backend.json
- Admin: 查看 trivy-admin.json

## 下一步行动
1. 审查高危漏洞
2. 制定修复计划
3. 更新依赖版本
EOF

echo "=== 审计完成，报告保存在 ${REPORT_DIR} ==="
```
