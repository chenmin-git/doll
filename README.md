# 二手玩偶交易系统

## 项目简介
基于 Spring Boot + MyBatis Plus + Vue 3 的二手玩偶交易平台，支持买家、卖家和管理员三种角色。

## 技术栈

### 后端
- Spring Boot 2.7.14
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- JWT 认证
- Lombok

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios
- Vite

## 功能模块

### 买家功能
- 注册登录
- 商品浏览与搜索
- 智能推荐
- 购物车管理
- 订单管理与售后
- 互动社区
- 评价反馈
- 个人中心
- 举报投诉

### 卖家功能
- 商品上架管理
- 定时上架
- 拍卖活动管理
- 评价管理
- 举报处理
- 店铺信息管理

### 管理员功能
- 用户管理
- 卖家管理
- 系统管理
- 内容管理（宣传图、公告资讯）
- 举报投诉处理
- 平台数据监控
- 订单监控与纠纷处理

## 快速开始

### 1. 数据库配置
```bash
# 导入数据库
mysql -u root -p123456 < schema.sql
```

### 2. 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
后端服务运行在 http://localhost:8080

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端服务运行在 http://localhost:3000

## 数据库配置
- 数据库名: doll
- 用户名: root
- 密码: 123456
- 端口: 3306

## API 接口

### 用户相关
- POST /api/user/login - 用户登录
- POST /api/user/register - 用户注册
- PUT /api/user/profile/{id} - 更新个人信息
- GET /api/user/{id} - 获取用户信息

### 商品相关
- GET /api/product/search - 搜索商品
- GET /api/product/recommend - 推荐商品
- POST /api/product - 创建商品
- PUT /api/product/{id} - 更新商品
- DELETE /api/product/{id} - 删除商品

### 购物车相关
- POST /api/cart - 添加到购物车
- GET /api/cart/user/{userId} - 获取购物车
- DELETE /api/cart/{id} - 删除购物车项
- PUT /api/cart/{id} - 更新购物车

### 订单相关
- POST /api/order - 创建订单
- GET /api/order/buyer/{buyerId} - 买家订单
- GET /api/order/seller/{sellerId} - 卖家订单
- PUT /api/order/{id}/status - 更新订单状态

### 评价相关
- POST /api/review - 创建评价
- GET /api/review/product/{productId} - 商品评价
- GET /api/review/seller/{sellerId} - 卖家评价

### 投诉相关
- POST /api/complaint - 提交投诉
- GET /api/complaint/user/{userId} - 用户投诉
- PUT /api/complaint/{id} - 处理投诉
- GET /api/complaint/list - 投诉列表

### 社区相关
- POST /api/post - 发布帖子
- GET /api/post/list - 帖子列表
- GET /api/post/{id} - 帖子详情

## 项目结构

```
doll-trading-system/
├── backend/                    # 后端项目
│   ├── src/main/java/com/doll/
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── service/           # 服务层
│   │   ├── controller/        # 控制器
│   │   ├── common/            # 公共类
│   │   └── util/              # 工具类
│   └── src/main/resources/
│       └── application.yml    # 配置文件
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── router/            # 路由配置
│   │   └── utils/             # 工具类
│   └── package.json
└── schema.sql                 # 数据库脚本
```

## 默认账号
系统启动后可以注册新用户，选择角色（买家/卖家）

## 注意事项
1. 确保 MySQL 服务已启动
2. 确保端口 8080 和 3000 未被占用
3. 首次运行需要执行数据库脚本
4. JWT Token 有效期为 7 天
