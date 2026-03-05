CREATE DATABASE IF NOT EXISTS `doll` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `doll`;

-- users table
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色: 1-买家, 2-卖家, 3-管理员',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `shop_name` VARCHAR(100) DEFAULT NULL COMMENT '店铺名(仅卖家有效)',
    `shop_description` TEXT DEFAULT NULL COMMENT '店铺描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT='用户表';

-- products table
CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `images` JSON COMMENT '商品图片列表(JSON数组)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架, 2-定时上架',
    `publish_time` DATETIME DEFAULT NULL COMMENT '定时上架时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_seller_id` (`seller_id`)
) COMMENT='商品表';

-- carts table
CREATE TABLE `carts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '买家ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_id` (`user_id`)
) COMMENT='购物车表';

-- orders table
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-纠纷中',
    `shipping_info` VARCHAR(255) DEFAULT NULL COMMENT '物流信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_buyer_id` (`buyer_id`),
    INDEX `idx_seller_id` (`seller_id`)
) COMMENT='订单表';

-- order_items table
CREATE TABLE `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL COMMENT '数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '购买时价格',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_id` (`order_id`)
) COMMENT='订单明细表';

-- after_sales table
CREATE TABLE `after_sales` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `reason` VARCHAR(255) NOT NULL COMMENT '售后原因',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-申请中, 1-同意退款/退货, 2-拒绝, 3-已完成',
    `description` TEXT COMMENT '问题描述',
    `images` JSON COMMENT '图片凭证(JSON数组)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_order_id` (`order_id`)
) COMMENT='售后表';

-- posts table
CREATE TABLE `posts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `images` JSON COMMENT '图片列表(JSON数组)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏, 1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_id` (`user_id`)
) COMMENT='社区帖子表';

-- reviews table
CREATE TABLE `reviews` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
    `content` TEXT COMMENT '评价内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_seller_id` (`seller_id`)
) COMMENT='评价表';

-- complaints table
CREATE TABLE `complaints` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `submitter_id` BIGINT NOT NULL COMMENT '投诉人ID',
    `target_id` BIGINT NOT NULL COMMENT '被投诉目标ID(可能是用户或商品)',
    `type` TINYINT NOT NULL COMMENT '投诉类型: 1-针对用户, 2-针对商品',
    `reason` VARCHAR(255) NOT NULL COMMENT '投诉原因',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理, 1-处理中, 2-已处理',
    `result` TEXT COMMENT '处理结果',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_submitter_id` (`submitter_id`)
) COMMENT='投诉表';

-- news table
CREATE TABLE `news` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `admin_id` BIGINT NOT NULL COMMENT '发布管理员ID',
    `title` VARCHAR(100) NOT NULL COMMENT '资讯标题',
    `content` TEXT NOT NULL COMMENT '资讯内容',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面宣传图',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT='资讯公告表';

-- auctions table
CREATE TABLE `auctions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `start_price` DECIMAL(10,2) NOT NULL COMMENT '起拍价',
    `current_price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '当前最高价',
    `winner_id` BIGINT DEFAULT NULL COMMENT '中标人ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_product_id` (`product_id`)
) COMMENT='拍卖活动表';
