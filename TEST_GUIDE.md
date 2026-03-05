# 测试指南

## 数据库修复完成

已修复的问题：
- ✅ favorites 表添加了 is_deleted 字段
- ✅ 添加了拍卖测试数据
- ✅ 所有表结构完整

## 测试步骤

### 1. 重启后端服务
```bash
cd backend
mvn spring-boot:run
```

### 2. 测试账号

**买家账号：**
- 用户名：buyer1
- 密码：123456

**卖家账号：**
- 用户名：seller1  
- 密码：123456

**管理员账号：**
- 用户名：admin
- 密码：admin123

### 3. 功能测试清单

#### 买家端测试
1. ✅ 登录 → 应跳转到买家中心
2. ✅ 商品浏览 → 显示商品列表和图片
3. ✅ 点击商品 → 跳转到商品详情页
4. ✅ 加入购物车 → 成功提示
5. ✅ 购物车 → 显示商品列表
6. ✅ 立即结算 → 弹出结算对话框
7. ✅ 确认支付 → 创建订单成功
8. ✅ 订单管理 → 显示订单列表和商品图片
9. ✅ 拍卖专区 → 显示拍卖商品
10. ✅ 参与竞拍 → 出价成功
11. ✅ 社区动态 → 显示帖子列表
12. ✅ 发布动态 → 发布成功
13. ✅ 收藏商品 → 收藏/取消收藏
14. ✅ 申请售后 → 提交成功
15. ✅ 提交投诉 → 提交成功
16. ✅ 资讯浏览 → 显示资讯列表

#### 卖家端测试
1. ✅ 登录 → 应跳转到卖家中心
2. ✅ 商品管理 → 显示商品列表
3. ✅ 上架商品 → 支持添加图片
4. ✅ 定时上架 → 设置上架时间
5. ✅ 订单管理 → 显示订单列表
6. ✅ 发货操作 → 更新订单状态
7. ✅ 评价管理 → 显示买家评价
8. ✅ 投诉管理 → 处理投诉
9. ✅ 店铺设置 → 修改店铺信息

#### 管理员端测试
1. ✅ 登录 → 应跳转到管理员后台
2. ✅ 用户管理 → 显示用户列表
3. ✅ 启用/禁用用户 → 更新用户状态
4. ✅ 投诉管理 → 显示所有投诉
5. ✅ 处理投诉 → 更新投诉状态

## 已知问题和解决方案

### 问题1：商品评价 (0)
**原因：** 暂无评价数据
**解决：** 完成订单后发表评价即可

### 问题2：图片不显示
**原因：** 使用的是图片URL
**解决：** 使用在线图片地址，例如：
- https://via.placeholder.com/300/FFB6C1/000000?text=Teddy+Bear
- https://via.placeholder.com/300/87CEEB/000000?text=Hello+Kitty

### 问题3：数据库连接失败
**原因：** MySQL服务未启动或配置错误
**解决：** 
1. 确保MySQL服务运行中
2. 检查application.yml中的数据库配置
3. 用户名：root，密码：123456，数据库：doll

## API接口列表

### 用户相关
- POST /api/user/login - 登录
- POST /api/user/register - 注册
- GET /api/user/{id} - 获取用户信息
- PUT /api/user/profile/{id} - 更新用户信息

### 商品相关
- GET /api/product/search - 搜索商品
- GET /api/product/recommend - 推荐商品
- GET /api/product/{id} - 商品详情
- POST /api/product - 创建商品
- PUT /api/product/{id} - 更新商品
- DELETE /api/product/{id} - 删除商品

### 购物车相关
- POST /api/cart - 添加到购物车
- GET /api/cart/user/{userId} - 获取购物车
- PUT /api/cart/{id} - 更新购物车
- DELETE /api/cart/{id} - 删除购物车项

### 订单相关
- POST /api/order - 创建订单
- GET /api/order/buyer/{buyerId} - 买家订单
- GET /api/order/seller/{sellerId} - 卖家订单
- PUT /api/order/{id}/status - 更新订单状态

### 售后相关
- POST /api/aftersale - 创建售后
- GET /api/aftersale/buyer/{buyerId} - 买家售后
- GET /api/aftersale/list - 所有售后

### 评价相关
- POST /api/review - 创建评价
- GET /api/review/product/{productId} - 商品评价
- GET /api/review/seller/{sellerId} - 卖家评价

### 投诉相关
- POST /api/complaint - 提交投诉
- GET /api/complaint/user/{userId} - 用户投诉
- PUT /api/complaint/{id} - 处理投诉
- GET /api/complaint/list - 所有投诉

### 社区相关
- POST /api/post - 发布帖子
- GET /api/post/list - 帖子列表
- GET /api/post/{id} - 帖子详情

### 拍卖相关
- GET /api/auction/list - 拍卖列表
- PUT /api/auction/{id}/bid - 参与竞拍

### 收藏相关
- POST /api/favorite - 收藏/取消收藏
- GET /api/favorite/user/{userId} - 用户收藏

### 资讯相关
- GET /api/news/list - 资讯列表
- GET /api/news/{id} - 资讯详情
- POST /api/news - 发布资讯（管理员）

## 数据库表

所有表都已创建并包含测试数据：
- users (用户表) - 10条数据
- products (商品表) - 10条数据
- orders (订单表) - 5条数据
- order_items (订单明细表) - 5条数据
- carts (购物车表) - 3条数据
- reviews (评价表) - 3条数据
- posts (帖子表) - 3条数据
- complaints (投诉表) - 2条数据
- news (资讯表) - 3条数据
- auctions (拍卖表) - 3条数据 ✨
- favorites (收藏表) - 已创建 ✨
- after_sales (售后表) - 已创建

## 前端访问地址

- 前端：http://localhost:3000
- 后端：http://localhost:8080

## 常见错误排查

1. **404错误** - 检查后端是否启动
2. **500错误** - 查看后端控制台日志
3. **跨域错误** - 确认@CrossOrigin注解已添加
4. **数据库错误** - 检查MySQL服务和表结构
5. **登录失败** - 确认用户名密码正确

## 开发建议

1. 使用浏览器开发者工具查看网络请求
2. 查看后端控制台日志定位问题
3. 使用Postman测试API接口
4. 定期备份数据库
