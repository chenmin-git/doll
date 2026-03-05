# 订单管理404错误排查指南

## 问题描述
点击卖家中心的"订单管理"菜单时，出现 "Request failed with status code 404" 错误。

## 可能的原因

### 1. 后端服务未启动或已停止
**解决方案**：
```bash
# 进入backend目录
cd backend

# 启动Spring Boot应用
mvn spring-boot:run
```

### 2. 后端接口路径问题
**检查项**：
- 确认 OrderController 中有 `@GetMapping("/seller/{sellerId}")` 接口
- 确认 `@RequestMapping("/api/order")` 路径正确

**已修复**：
- ✅ 添加了 `@GetMapping("/{id}")` 接口用于获取单个订单详情

### 3. 数据库连接问题
**检查项**：
```bash
# 确认MySQL服务正在运行
# 确认数据库 doll 存在
# 确认用户名密码正确：root/123456
```

### 4. sellerId 为空或无效
**检查项**：
- 确认登录时正确保存了 userId 到 localStorage
- 在浏览器控制台检查：`localStorage.getItem('userId')`

## 快速修复步骤

### 步骤1：检查后端服务
1. 打开新的终端
2. 进入 backend 目录
3. 运行：`mvn spring-boot:run`
4. 等待看到 "Started DollApplication" 消息

### 步骤2：检查数据库
1. 打开MySQL客户端
2. 运行：`USE doll;`
3. 检查表：`SHOW TABLES;`
4. 检查订单数据：`SELECT * FROM orders LIMIT 5;`

### 步骤3：检查前端配置
1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 点击"订单管理"
4. 查看失败的请求详情：
   - 请求URL是否正确：`/api/order/seller/{sellerId}`
   - sellerId 是否有值
   - 响应状态码和错误信息

### 步骤4：清除缓存并重新登录
1. 清除浏览器 localStorage：
   ```javascript
   localStorage.clear()
   ```
2. 重新登录卖家账号（seller1/123456）
3. 再次尝试访问订单管理

## 临时解决方案

如果以上方法都不行，可以尝试修改前端代码添加错误处理：

```javascript
const loadOrders = async () => {
  try {
    console.log('sellerId:', sellerId.value) // 调试日志
    const res = await request.get(`/order/seller/${sellerId.value}`)
    console.log('订单数据:', res.data) // 调试日志
    const ordersData = res.data || []
    
    // ... 其余代码
    orders.value = ordersData
  } catch (error) {
    console.error('加载订单失败:', error) // 详细错误信息
    console.error('错误响应:', error.response) // 查看响应详情
    ElMessage.error('加载订单失败: ' + (error.response?.data?.message || error.message))
  }
}
```

## 验证修复

修复后，应该能看到：
1. 订单列表正常显示
2. 显示商品信息（图片、名称）
3. 可以进行发货操作
4. 售后记录也能正常显示

## 相关文件

- 后端控制器：`backend/src/main/java/com/doll/controller/OrderController.java`
- 前端页面：`frontend/src/views/seller/Home.vue`
- 请求配置：`frontend/src/utils/request.js`
- 数据库配置：`backend/src/main/resources/application.yml`
