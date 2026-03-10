# 外卖平台 API 文档

## 概述

本文档描述了外卖平台的 RESTful API 接口规范。API 分为两大类：
- **管理端 API** (`/admin/*`): 供后台管理系统使用
- **小程序端 API** (`/wx/*`): 供微信小程序使用

## 基础信息

- **Base URL**: `https://api.fooddelivery.com`
- **API 版本**: v1.0.0
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证方式

所有需要认证的接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer <token>
```

### Token 说明

| 属性 | 值 |
|------|-----|
| 有效期 | 24小时（可配置） |
| 算法 | HS256 |
| 刷新机制 | 重新登录获取新 Token |

### cURL 请求示例

```bash
# 登录获取 Token
curl -X POST https://api.fooddelivery.com/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 携带 Token 请求
curl -X GET https://api.fooddelivery.com/admin/merchant/page \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "错误信息描述",
  "data": null
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "records": []
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200=成功，500=失败 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

---

## 管理端 API

### 1. 认证模块

#### 1.1 管理员登录

**POST** `/admin/auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 1,
    "username": "admin",
    "realName": "管理员"
  }
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "用户名或密码错误",
  "data": null
}
```

#### 1.2 获取当前用户信息

**GET** `/admin/auth/info`

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "phone": "13800138000"
  }
}
```

---

### 2. 商家管理

#### 2.1 分页查询商家

**GET** `/admin/merchant/page`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| name | String | 否 | 商家名称（模糊查询） |
| status | Integer | 否 | 状态：0=禁用，1=启用 |

**请求示例**:
```
GET /admin/merchant/page?page=1&pageSize=10&name=美味&status=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "name": "美味餐厅",
        "image": "https://xxx.com/image.jpg",
        "address": "北京市朝阳区xxx",
        "phone": "13800138000",
        "description": "正宗川菜",
        "minPrice": 20.00,
        "deliveryFee": 5.00,
        "deliveryTime": "30-45分钟",
        "score": 4.8,
        "monthlySales": 1000,
        "status": 1,
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

#### 2.2 获取商家详情

**GET** `/admin/merchant/{id}`

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 商家ID |

**请求示例**:
```
GET /admin/merchant/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "美味餐厅",
    "image": "https://xxx.com/image.jpg",
    "address": "北京市朝阳区xxx",
    "phone": "13800138000",
    "description": "正宗川菜，麻辣鲜香",
    "minPrice": 20.00,
    "deliveryFee": 5.00,
    "deliveryTime": "30-45分钟",
    "score": 4.8,
    "monthlySales": 1000,
    "status": 1,
    "createTime": "2024-01-01 10:00:00"
  }
}
```

#### 2.3 新增商家

**POST** `/admin/merchant`

**请求参数**:
```json
{
  "name": "美味餐厅",
  "address": "北京市朝阳区xxx",
  "phone": "13800138000",
  "minPrice": 20.00,
  "deliveryFee": 5.00,
  "deliveryTime": "30-45分钟",
  "image": "https://xxx.com/image.jpg",
  "description": "商家描述"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 商家名称 |
| address | String | 是 | 商家地址 |
| phone | String | 是 | 联系电话 |
| minPrice | BigDecimal | 否 | 起送价格，默认0 |
| deliveryFee | BigDecimal | 否 | 配送费，默认0 |
| deliveryTime | String | 否 | 配送时间 |
| image | String | 否 | 商家图片URL |
| description | String | 否 | 商家描述 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 2.4 修改商家

**PUT** `/admin/merchant`

**请求参数**:
```json
{
  "id": 1,
  "name": "美味餐厅（新）",
  "address": "北京市朝阳区xxx",
  "phone": "13800138000",
  "minPrice": 25.00,
  "deliveryFee": 3.00
}
```

#### 2.5 删除商家

**DELETE** `/admin/merchant/{id}`

**请求示例**:
```
DELETE /admin/merchant/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 2.6 修改商家状态

**PUT** `/admin/merchant/status`

**请求参数**:
```json
{
  "id": 1,
  "status": 0
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 商家ID |
| status | Integer | 是 | 状态：0=禁用，1=启用 |

---

### 3. 菜品管理

#### 3.1 分页查询菜品

**GET** `/admin/dish/page`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| merchantId | Long | 否 | 商家ID |
| categoryId | Long | 否 | 分类ID |
| name | String | 否 | 菜品名称（模糊查询） |
| status | Integer | 否 | 状态：0=下架，1=上架 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "merchantId": 1,
        "merchantName": "美味餐厅",
        "categoryId": 1,
        "categoryName": "热菜",
        "name": "宫保鸡丁",
        "image": "https://xxx.com/dish.jpg",
        "price": 38.00,
        "description": "经典川菜",
        "monthlySales": 500,
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

#### 3.2 新增菜品

**POST** `/admin/dish`

**请求参数**:
```json
{
  "merchantId": 1,
  "categoryId": 1,
  "name": "宫保鸡丁",
  "price": 38.00,
  "image": "https://xxx.com/dish.jpg",
  "description": "经典川菜，鸡肉嫩滑"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchantId | Long | 是 | 商家ID |
| categoryId | Long | 否 | 分类ID |
| name | String | 是 | 菜品名称 |
| price | BigDecimal | 是 | 菜品价格 |
| image | String | 否 | 菜品图片URL |
| description | String | 否 | 菜品描述 |

---

### 4. 订单管理

#### 4.1 分页查询订单

**GET** `/admin/order/page`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| orderNo | String | 否 | 订单号 |
| status | Integer | 否 | 订单状态 |
| startTime | String | 否 | 开始时间 (yyyy-MM-dd) |
| endTime | String | 否 | 结束时间 (yyyy-MM-dd) |

**订单状态说明**:
| 状态值 | 说明 |
|--------|------|
| 0 | 待支付 |
| 1 | 待接单 |
| 2 | 配送中 |
| 3 | 已完成 |
| 4 | 已取消 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 200,
    "records": [
      {
        "id": 1,
        "orderNo": "202401010001",
        "userId": 1,
        "merchantId": 1,
        "merchantName": "美味餐厅",
        "consignee": "张三",
        "phone": "13800138000",
        "address": "北京市朝阳区xxx",
        "totalAmount": 68.00,
        "deliveryFee": 5.00,
        "remark": "少放辣",
        "status": 1,
        "createTime": "2024-01-01 12:00:00",
        "orderItems": [
          {
            "dishId": 1,
            "dishName": "宫保鸡丁",
            "dishImage": "https://xxx.com/dish.jpg",
            "price": 38.00,
            "quantity": 1,
            "amount": 38.00
          }
        ]
      }
    ]
  }
}
```

#### 4.2 修改订单状态

**PUT** `/admin/order/status`

**请求参数**:
```json
{
  "id": 1,
  "status": 2
}
```

### 5. 数据概览

#### 5.1 获取统计数据

**GET** `/admin/dashboard/statistics`

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "todayOrders": 25,
    "totalOrders": 1280,
    "todayAmount": 3680.50,
    "totalAmount": 158920.00,
    "totalUsers": 856,
    "totalMerchants": 5
  }
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| todayOrders | Integer | 今日订单数（不含已取消） |
| totalOrders | Integer | 总订单数（不含已取消） |
| todayAmount | Decimal | 今日营业额（已完成订单） |
| totalAmount | Decimal | 总营业额（已完成订单） |
| totalUsers | Integer | 注册用户总数 |
| totalMerchants | Integer | 商家总数 |

---

## 小程序端 API

### 1. 认证模块

#### 1.1 微信登录

**POST** `/wx/auth/login`

**请求参数**:
```json
{
  "code": "微信授权code"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "openid": "oXXXX",
    "isNew": false
  }
}
```

---

### 2. 商家模块

#### 2.1 获取商家列表

**GET** `/wx/merchant/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| keyword | String | 否 | 搜索关键词 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "name": "美味餐厅",
        "image": "https://xxx.com/image.jpg",
        "minPrice": 20.00,
        "deliveryFee": 5.00,
        "deliveryTime": "30-45分钟",
        "score": 4.8,
        "monthlySales": 1000
      }
    ]
  }
}
```

#### 2.2 获取商家详情

**GET** `/wx/merchant/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "美味餐厅",
    "image": "https://xxx.com/image.jpg",
    "address": "北京市朝阳区xxx",
    "phone": "13800138000",
    "description": "正宗川菜",
    "minPrice": 20.00,
    "deliveryFee": 5.00,
    "deliveryTime": "30-45分钟",
    "score": 4.8,
    "monthlySales": 1000,
    "categories": [
      {
        "id": 1,
        "name": "热菜",
        "dishes": [
          {
            "id": 1,
            "name": "宫保鸡丁",
            "image": "https://xxx.com/dish.jpg",
            "price": 38.00,
            "description": "经典川菜",
            "monthlySales": 500
          }
        ]
      }
    ]
  }
}
```

---

### 3. 购物车模块

#### 3.1 添加购物车

**POST** `/wx/cart/add`

**请求参数**:
```json
{
  "dishId": 1,
  "quantity": 1
}
```

#### 3.2 获取购物车

**GET** `/wx/cart`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "merchantId": 1,
    "merchantName": "美味餐厅",
    "items": [
      {
        "id": 1,
        "dishId": 1,
        "dishName": "宫保鸡丁",
        "dishImage": "https://xxx.com/dish.jpg",
        "price": 38.00,
        "quantity": 2
      }
    ],
    "totalPrice": 76.00,
    "totalCount": 2
  }
}
```

#### 3.3 更新购物车数量

**PUT** `/wx/cart/update`

**请求参数**:
```json
{
  "id": 1,
  "quantity": 3
}
```

#### 3.4 清空购物车

**DELETE** `/wx/cart/clear`

---

### 4. 订单模块

#### 4.1 提交订单

**POST** `/wx/order/submit`

**请求参数**:
```json
{
  "addressId": 1,
  "remark": "少放辣"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "202401010001",
    "totalAmount": 81.00
  }
}
```

#### 4.2 获取订单列表

**GET** `/wx/order/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| status | Integer | 否 | 订单状态 |

#### 4.3 获取订单详情

**GET** `/wx/order/{id}`

#### 4.4 取消订单

**PUT** `/wx/order/cancel`

**请求参数**:
```json
{
  "id": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

---

### 5. 地址模块

#### 5.1 获取地址列表

**GET** `/wx/address/list`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "address": "xxx街道xxx号",
      "isDefault": 1
    }
  ]
}
```

#### 5.2 新增地址

**POST** `/wx/address`

**请求参数**:
```json
{
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "address": "xxx街道xxx号",
  "isDefault": 0
}
```

#### 5.3 修改地址

**PUT** `/wx/address`

#### 5.4 删除地址

**DELETE** `/wx/address/{id}`

#### 5.5 设置默认地址

**PUT** `/wx/address/default/{id}`

---

## 错误码说明

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，请先登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 422 | 参数验证失败 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |
| 502 | 网关错误 |
| 503 | 服务暂时不可用 |

## 业务错误码

| code | 说明 |
|------|------|
| 200 | 操作成功 |
| 500 | 操作失败，具体原因见 message 字段 |

## 常见错误响应示例

### 参数验证失败
```json
{
  "code": 500,
  "message": "商家名称不能为空",
  "data": null
}
```

### Token 过期
```json
{
  "code": 500,
  "message": "登录已过期，请重新登录",
  "data": null
}
```

### 资源不存在
```json
{
  "code": 500,
  "message": "商家不存在",
  "data": null
}
```

### 业务逻辑错误
```json
{
  "code": 500,
  "message": "订单已完成，无法取消",
  "data": null
}
```

---

## 在线文档

启动服务后，可访问 Swagger UI 查看完整的 API 文档：
- 开发环境: `http://localhost:8080/swagger-ui.html`
- API JSON: `http://localhost:8080/api-docs`

## 最佳实践

### 1. 错误处理
```javascript
// 前端统一错误处理示例
axios.interceptors.response.use(
  response => {
    if (response.data.code !== 200) {
      // 业务错误
      showToast(response.data.message)
      return Promise.reject(response.data)
    }
    return response.data
  },
  error => {
    const status = error.response?.status
    switch (status) {
      case 401:
        redirectToLogin()
        break
      case 403:
        showToast('没有权限执行此操作')
        break
      case 429:
        showToast('请求过于频繁，请稍后再试')
        break
      default:
        showToast(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)
```

### 2. 分页请求
```javascript
// 分页请求示例
async function loadMerchants(page = 1, filters = {}) {
  const res = await request.get('/wx/merchant/list', {
    params: { 
      page, 
      pageSize: 10,
      ...filters 
    }
  })
  return {
    list: res.data.records,
    total: res.data.total,
    hasMore: page * 10 < res.data.total
  }
}

// 带搜索的分页
async function searchMerchants(keyword, page = 1) {
  return loadMerchants(page, { keyword })
}
```

### 3. Token 刷新
建议在 Token 即将过期时自动刷新，避免用户操作中断。

```javascript
// Token 刷新示例
const TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000 // 5分钟

function shouldRefreshToken(token) {
  const payload = JSON.parse(atob(token.split('.')[1]))
  const expTime = payload.exp * 1000
  return expTime - Date.now() < TOKEN_REFRESH_THRESHOLD
}

async function refreshTokenIfNeeded() {
  const token = getToken()
  if (token && shouldRefreshToken(token)) {
    const newToken = await refreshToken()
    setToken(newToken)
  }
}
```

### 4. 请求重试
```javascript
// 带重试机制的请求
async function requestWithRetry(config, retries = 3) {
  for (let i = 0; i < retries; i++) {
    try {
      return await request(config)
    } catch (error) {
      if (i === retries - 1) throw error
      if (error.response?.status >= 500) {
        await sleep(1000 * (i + 1)) // 指数退避
      } else {
        throw error // 非服务器错误不重试
      }
    }
  }
}
```

### 5. 请求取消
```javascript
// 使用 AbortController 取消请求
const controller = new AbortController()

request.get('/api/data', {
  signal: controller.signal
})

// 取消请求
controller.abort()
```
