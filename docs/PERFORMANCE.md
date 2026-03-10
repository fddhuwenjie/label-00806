# 性能优化指南

## 概述

本文档描述了外卖平台的性能优化策略和最佳实践。

## 数据库优化

### 连接池配置

使用 HikariCP 连接池，配置如下：

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5          # 最小空闲连接数
      maximum-pool-size: 20    # 最大连接数
      idle-timeout: 30000      # 空闲超时时间 (ms)
      max-lifetime: 1800000    # 连接最大生命周期 (ms)
      connection-timeout: 30000 # 连接超时时间 (ms)
      pool-name: FoodDeliveryHikariCP
```

### 不同环境配置

| 环境 | minimum-idle | maximum-pool-size |
|------|--------------|-------------------|
| 开发 | 2 | 10 |
| 测试 | 3 | 15 |
| 生产 | 10 | 50 |

### 索引优化

确保常用查询字段有索引：

```sql
-- 商家表索引
CREATE INDEX idx_merchant_status ON merchant(status);
CREATE INDEX idx_merchant_name ON merchant(name);

-- 订单表索引
CREATE INDEX idx_order_user_id ON `order`(user_id);
CREATE INDEX idx_order_status ON `order`(status);
CREATE INDEX idx_order_create_time ON `order`(create_time);

-- 菜品表索引
CREATE INDEX idx_dish_merchant_id ON dish(merchant_id);
CREATE INDEX idx_dish_category_id ON dish(category_id);
```

## 缓存策略

### Redis 缓存配置

```java
@Bean
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    
    // 商家缓存 30 分钟
    cacheConfigurations.put("merchants", 
        defaultConfig.entryTtl(Duration.ofMinutes(30)));
    
    // 菜品缓存 15 分钟
    cacheConfigurations.put("dishes", 
        defaultConfig.entryTtl(Duration.ofMinutes(15)));
    
    // 分类缓存 2 小时
    cacheConfigurations.put("categories", 
        defaultConfig.entryTtl(Duration.ofHours(2)));
    
    return RedisCacheManager.builder(connectionFactory)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
}
```

### 缓存使用示例

```java
@Service
public class MerchantServiceImpl {
    
    @Cacheable(value = "merchants", key = "#id")
    public Merchant getById(Long id) {
        return merchantMapper.selectById(id);
    }
    
    @CacheEvict(value = "merchants", key = "#merchant.id")
    public void update(Merchant merchant) {
        merchantMapper.updateById(merchant);
    }
}
```

### 缓存策略

| 数据类型 | 缓存时间 | 更新策略 |
|----------|----------|----------|
| 商家信息 | 30 分钟 | 更新时失效 |
| 菜品列表 | 15 分钟 | 更新时失效 |
| 分类信息 | 2 小时 | 更新时失效 |
| 用户信息 | 10 分钟 | 更新时失效 |

## 异步处理

### 异步日志

使用 Logback 异步日志减少 I/O 阻塞：

```xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <discardingThreshold>0</discardingThreshold>
    <queueSize>512</queueSize>
    <appender-ref ref="FILE"/>
</appender>
```

### 异步任务

```java
@Async
public void sendOrderNotification(Order order) {
    // 异步发送通知
}
```

## 分页优化

### 深分页优化

避免使用 `OFFSET` 进行深分页，改用游标分页：

```java
// 不推荐：深分页性能差
SELECT * FROM orders LIMIT 10000, 10;

// 推荐：使用游标分页
SELECT * FROM orders WHERE id > #{lastId} LIMIT 10;
```

### 分页配置

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setMaxLimit(100L); // 限制单页最大数量
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }
}
```

## 接口优化

### 响应压缩

```yaml
server:
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/plain
    min-response-size: 1024
```

### 批量操作

```java
// 批量插入
@Transactional
public void batchInsert(List<Dish> dishes) {
    dishService.saveBatch(dishes, 500); // 每批 500 条
}
```

## 监控指标

### Actuator 端点

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### 关键指标

| 指标 | 说明 | 告警阈值 |
|------|------|----------|
| http_server_requests | HTTP 请求延迟 | P99 > 1s |
| hikaricp_connections_active | 活跃连接数 | > 80% |
| jvm_memory_used | JVM 内存使用 | > 80% |
| redis_connections_active | Redis 连接数 | > 80% |

### Prometheus 配置

```yaml
scrape_configs:
  - job_name: 'food-delivery-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```

## 压测工具

### JMeter 测试计划

```xml
<!-- 线程组配置 -->
<ThreadGroup>
    <stringProp name="ThreadGroup.num_threads">100</stringProp>
    <stringProp name="ThreadGroup.ramp_time">10</stringProp>
    <stringProp name="ThreadGroup.duration">300</stringProp>
</ThreadGroup>
```

### 压测场景

| 场景 | 并发数 | 持续时间 | 目标 TPS |
|------|--------|----------|----------|
| 商家列表 | 100 | 5 分钟 | 500 |
| 下单 | 50 | 5 分钟 | 100 |
| 订单查询 | 100 | 5 分钟 | 300 |

## 自动扩缩容

### Kubernetes HPA

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

## 性能检查清单

- [ ] 数据库连接池配置合理
- [ ] 常用查询字段有索引
- [ ] 热点数据使用缓存
- [ ] 日志使用异步写入
- [ ] 分页查询有数量限制
- [ ] 响应压缩已启用
- [ ] 监控指标已配置
- [ ] 自动扩缩容已配置
