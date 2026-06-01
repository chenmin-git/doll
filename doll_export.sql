-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: doll
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver` varchar(50) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,2,'演示买家','13000000001','','','','11',1,'2026-03-06 01:43:45','2026-03-06 01:43:45',0);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `after_sales`
--

DROP TABLE IF EXISTS `after_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `after_sales` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后原因',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-申请中, 1-同意退款/退货, 2-拒绝, 3-已完成',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '问题描述',
  `images` json DEFAULT NULL COMMENT '图片凭证(JSON数组)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='售后表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `after_sales`
--

LOCK TABLES `after_sales` WRITE;
/*!40000 ALTER TABLE `after_sales` DISABLE KEYS */;
INSERT INTO `after_sales` VALUES (1,6,2,NULL,'不喜欢',2,'',NULL,'2026-03-06 00:59:21','2026-03-06 02:10:35',0);
/*!40000 ALTER TABLE `after_sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auctions`
--

DROP TABLE IF EXISTS `auctions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auctions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `start_price` decimal(10,2) NOT NULL COMMENT '起拍价',
  `current_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前最高价',
  `winner_id` bigint DEFAULT NULL COMMENT '中标人ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-未开始, 1-进行中, 2-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='拍卖活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auctions`
--

LOCK TABLES `auctions` WRITE;
/*!40000 ALTER TABLE `auctions` DISABLE KEYS */;
INSERT INTO `auctions` VALUES (1,1,7,800.00,0.00,NULL,'2026-03-05 14:52:34','2026-03-05 16:00:00',1,'2026-03-05 22:52:38','2026-03-05 22:52:40',0),(2,1,7,50.00,98.00,2,'2024-03-01 10:00:00','2024-03-20 22:00:00',2,'2026-03-06 01:34:31','2026-03-06 01:34:31',0),(3,4,8,150.00,198.00,NULL,'2024-03-02 10:00:00','2024-03-25 22:00:00',1,'2026-03-06 01:34:31','2026-03-06 01:34:31',0),(4,7,9,200.00,288.00,NULL,'2024-02-28 10:00:00','2024-03-05 22:00:00',2,'2026-03-06 01:34:31','2026-03-06 01:34:31',0),(5,3,7,66.00,10.00,2,'2026-03-05 16:00:00','2026-03-06 16:00:00',2,'2026-03-06 02:34:55','2026-03-06 02:34:55',0);
/*!40000 ALTER TABLE `auctions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '买家ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,4,3,1,'2024-03-05 10:00:00','2026-03-05 21:06:33',0),(2,4,6,2,'2024-03-05 11:30:00','2026-03-05 21:06:33',0),(3,5,8,1,'2024-03-05 14:20:00','2026-03-05 21:06:33',0),(4,2,1,1,'2026-03-05 22:08:54','2026-03-05 22:34:35',1),(5,2,1,1,'2026-03-05 22:12:57','2026-03-05 22:34:35',1),(6,2,1,1,'2026-03-06 00:16:14','2026-03-06 00:59:10',1),(7,2,1,1,'2026-03-06 00:58:40','2026-03-06 00:59:10',1),(8,2,1,1,'2026-03-06 01:05:41','2026-03-06 01:05:41',0),(9,2,2,1,'2026-03-06 01:47:34','2026-03-06 01:47:34',0);
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaints`
--

DROP TABLE IF EXISTS `complaints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaints` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submitter_id` bigint NOT NULL COMMENT '投诉人ID',
  `target_id` bigint NOT NULL COMMENT '被投诉目标ID(可能是用户或商品)',
  `target_name` varchar(255) DEFAULT NULL,
  `type` tinyint NOT NULL COMMENT '投诉类型: 1-针对用户, 2-针对商品',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉原因',
  `images` text,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-待处理, 1-处理中, 2-已处理',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_submitter_id` (`submitter_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投诉表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaints`
--

LOCK TABLES `complaints` WRITE;
/*!40000 ALTER TABLE `complaints` DISABLE KEYS */;
INSERT INTO `complaints` VALUES (1,5,7,NULL,1,'卖家发货太慢',NULL,2,'已与卖家沟通，卖家承诺加快发货速度','2024-03-04 16:00:00','2026-03-05 21:06:46',0),(2,6,3,NULL,2,'商品描述与实物不符',NULL,1,NULL,'2024-03-05 09:30:00','2026-03-05 21:06:40',0),(3,2,2,NULL,2,'不好',NULL,2,'1','2026-03-05 23:47:15','2026-03-06 01:12:12',0);
/*!40000 ALTER TABLE `complaints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (2,2,2,'2026-03-05 23:46:40',0),(4,2,1,'2026-03-06 01:05:42',0);
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL COMMENT '发布管理员ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '封面宣传图',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资讯公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,3,'平台春季促销活动开启','亲爱的用户们，我们的春季促销活动正式开启，全场商品8折起，更有限量款玩偶等你来抢购！','/api/upload/image/2f9fc8136f794d7aa5fd3dd9d3a86f3e.jpeg','2024-03-01 09:00:00','2026-03-05 22:15:04',0),(2,3,'新增玩偶鉴定服务','为了保障用户权益，平台现推出专业玩偶鉴定服务，确保每一件商品的真实性。','/api/upload/image/4136aa5369514d9eadb992038d0fab4d.jpeg','2024-03-03 10:00:00','2026-03-05 22:15:35',0),(3,3,'平台用户突破10万','感谢大家的支持，我们的注册用户已突破10万，未来将继续为大家提供更优质的服务！','/api/upload/image/20d3d9f89f4a4639bcd72d252f8a32be.jpeg','2024-03-04 15:00:00','2026-03-06 15:48:27',0);
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '数量',
  `price` decimal(10,2) NOT NULL COMMENT '购买时价格',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,1,88.00,'2026-03-05 21:05:56'),(2,2,4,1,198.00,'2026-03-05 21:05:56'),(3,3,2,1,128.00,'2026-03-05 21:05:56'),(4,4,7,1,288.00,'2026-03-05 21:05:56'),(5,5,5,1,168.00,'2026-03-05 21:05:56'),(6,6,1,1,88.00,'2026-03-05 22:26:40'),(7,7,1,1,88.00,'2026-03-05 22:34:35'),(8,8,1,1,88.00,'2026-03-06 00:59:10'),(9,9,1,1,88.00,'2026-03-06 00:59:10'),(10,10,1,1,88.00,'2026-03-06 01:43:45'),(11,11,2,1,128.00,'2026-03-06 01:48:37'),(12,12,1,1,98.00,'2026-03-06 02:28:40'),(13,13,3,1,10.00,'2026-03-06 02:36:09');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-待支付, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-纠纷中',
  `shipping_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '物流信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'ORD202403050001',4,7,88.00,3,NULL,'2024-03-01 10:30:00','2024-03-01 10:35:00','2026-03-05 21:05:46',0),(2,'ORD202403050002',4,8,198.00,3,NULL,'2024-03-02 14:20:00','2024-03-02 14:25:00','2026-03-05 21:05:46',0),(3,'ORD202403050003',5,7,128.00,4,NULL,'2024-03-03 09:15:00','2024-03-03 09:20:00','2026-03-05 23:12:20',0),(4,'ORD202403050004',5,9,288.00,4,NULL,'2024-03-04 16:40:00','2024-03-04 16:45:00','2026-03-05 23:12:26',0),(5,'ORD202403050005',6,8,168.00,3,NULL,'2024-03-04 11:10:00','2024-03-04 11:15:00','2026-03-05 21:05:46',0),(6,'0e793aef21784b139357061e80868de9',2,7,88.00,3,'演示买家,13000000001,演示城市','2026-03-05 22:26:40',NULL,'2026-03-05 23:22:07',0),(7,'3c65ab452ffe40508a18e99fbe211e03',2,7,88.00,2,'演示买家,13000000001,演示城市','2026-03-05 22:34:35',NULL,'2026-03-06 02:30:33',0),(8,'e6152df7a5ed4c76917f6e920963043d',2,7,88.00,2,'演示买家,13000000001,演示城市','2026-03-06 00:59:10',NULL,'2026-03-06 02:30:46',0),(9,'db90327c7c15414cb21264361cb97270',2,7,88.00,2,'演示买家,13000000001,演示城市','2026-03-06 00:59:10',NULL,'2026-03-06 15:59:49',0),(10,'daf6758c01ed4d95b2c0ebc6887aef9f',2,7,88.00,1,'演示买家,13000000001,11','2026-03-06 01:43:45',NULL,'2026-03-06 01:43:45',0),(11,'3041cfbb902d4a82b4d15c5158a97c4a',2,7,128.00,1,'演示买家,13000000001,演示省演示市演示区 1 号','2026-03-06 01:48:37',NULL,'2026-03-06 01:48:37',0),(12,'783dacf5d94b49a8832dc7a44e391e04',2,7,98.00,1,'AUCTION_WIN:2','2026-03-06 02:28:40',NULL,'2026-03-06 02:29:11',0),(13,'62345048512747f3ac9ba39087a507b2',2,7,10.00,1,'AUCTION_WIN:5','2026-03-06 02:36:09',NULL,'2026-03-06 02:36:29',0);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '发布者ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `images` json DEFAULT NULL COMMENT '图片列表(JSON数组)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-隐藏, 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,4,'分享我的玩偶收藏','最近收集了好多可爱的玩偶，给大家分享一下我的收藏心得...',NULL,1,'2024-03-01 20:00:00','2026-03-05 21:06:14',0),(2,5,'如何鉴别正版玩偶','作为一个玩偶爱好者，教大家如何辨别正版和盗版玩偶...',NULL,1,'2024-03-02 18:30:00','2026-03-05 21:06:14',0),(3,6,'二手玩偶购买指南','购买二手玩偶需要注意的几个要点，避免踩坑...',NULL,1,'2024-03-03 21:15:00','2026-03-05 21:06:14',0);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `images` json DEFAULT NULL COMMENT '商品图片列表(JSON数组)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-下架, 1-上架, 2-定时上架',
  `publish_time` datetime DEFAULT NULL COMMENT '定时上架时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,7,'泰迪熊玩偶','经典款泰迪熊，九成新，高约30cm，毛绒柔软',88.00,0,'[\"/api/upload/image/00213e8414a34f64958f46726a53a86b.jpg\"]',1,'2026-03-05 18:38:08','2026-03-05 21:05:37','2026-03-06 02:38:09',0),(2,7,'Hello Kitty公仔','正版Hello Kitty，八成新，适合收藏',128.00,2,'[\"/api/upload/image/f87c2b95490b4ee6afac94b0f1bfd372.jpg\"]',1,'2026-03-05 18:39:22','2026-03-05 21:05:37','2026-03-06 02:38:39',0),(3,7,'小黄鸭玩偶','可爱小黄鸭，全新未拆封，限量版',158.00,2,'[\"/api/upload/image/4ccf9e8b6d6b46a8a8bb84759b7251a7.jpg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:04:00',0),(4,8,'迪士尼米奇公仔','迪士尼正版米奇，九成新，高约25cm',198.00,4,'[\"/api/upload/image/699500c722e04709950ac2e8edc65d65.jpeg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:05:08',0),(5,8,'皮卡丘毛绒玩具','宝可梦皮卡丘，全新，超萌超可爱',168.00,6,'[\"/api/upload/image/304d988669a0449786dd17b7c7ba9aa2.jpg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:05:35',0),(6,8,'史迪奇玩偶','迪士尼史迪奇，八成新，表情生动',138.00,3,'[\"/api/upload/image/1b704654e84c4480ae32bcf0079549e4.jpeg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:05:53',0),(7,9,'芭比娃娃套装','经典芭比娃娃，配多套服装，九成新',288.00,2,'[\"/api/upload/image/213377a98d944585a47e408751b84c35.gif\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:06:32',0),(8,9,'龙猫玩偶','宫崎骏龙猫，正版授权，全新',218.00,5,'[\"/api/upload/image/38d133e1c3ac4bffab9150812486f4fc.png\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:06:54',0),(9,9,'小熊维尼','迪士尼小熊维尼，九成新，经典款',178.00,4,'[\"/api/upload/image/90cf81169ebb41af9465c9386c836dd9.jpg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:07:38',0),(10,9,'海绵宝宝公仔','海绵宝宝毛绒玩具，八成新',98.00,8,'[\"/api/upload/image/f9258f334461476ab2c81d24298da397.jpeg\"]',1,NULL,'2026-03-05 21:05:37','2026-03-05 22:07:53',0),(11,7,'米奇','',66.00,1,'[\"/api/upload/image/1888c7447f4e47c8877024000d93edc8.jpg\"]',1,'2026-03-05 18:50:56','2026-03-06 02:42:27','2026-03-06 02:51:31',1),(12,7,'ceshi','1',88.00,2,'[\"/api/upload/image/baf116ecacc2408a8ef74e46bc9c1274.jpeg\"]',0,'2026-03-06 02:58:11','2026-03-06 02:51:57','2026-03-08 22:53:27',0);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `rating` tinyint NOT NULL DEFAULT '5' COMMENT '评分(1-5)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,1,1,4,7,5,'玩偶质量很好，卖家服务态度也很棒！','2024-03-02 10:00:00','2026-03-05 21:06:06',0),(2,2,4,4,8,5,'正版米奇，很满意，物流也快','2024-03-03 15:30:00','2026-03-05 21:06:06',0),(3,5,5,6,8,4,'皮卡丘很可爱，就是价格稍微有点贵','2024-03-05 09:20:00','2026-03-05 21:06:06',0),(4,6,1,2,7,5,'很喜欢','2026-03-05 23:40:49','2026-03-05 23:40:49',0),(5,6,1,2,7,5,'很好呀','2026-03-06 00:57:51','2026-03-06 00:57:51',0);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '角色: 1-买家, 2-卖家, 3-管理员',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '店铺名(仅卖家有效)',
  `shop_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '店铺描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-禁用, 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'lala','',1,'',NULL,'','',NULL,1,'2026-03-05 20:58:28','2026-03-05 20:58:28',0),(2,'lalala','1234567',1,'拉拉','/api/upload/image/4f26f331fbf349a390ed77823da67aa1.jpg','13000000001','',NULL,1,'2026-03-05 20:58:44','2026-03-05 22:11:52',0),(3,'admin','admin123',3,'系统管理员',NULL,NULL,NULL,NULL,1,'2026-03-05 21:00:37','2026-03-05 21:00:37',0),(4,'buyer1','123456',1,'小明',NULL,'13000000011',NULL,NULL,1,'2026-03-05 21:05:16','2026-03-05 21:05:16',0),(5,'buyer2','123456',1,'小红',NULL,'13000000012',NULL,NULL,1,'2026-03-05 21:05:16','2026-03-05 21:05:16',0),(6,'buyer3','123456',1,'小刚',NULL,'13000000013',NULL,NULL,1,'2026-03-05 21:05:16','2026-03-05 21:05:16',0),(7,'seller1','123456',2,'玩偶小店','/api/upload/image/7172edd14485470293ef9c73b756b129.jpg','13000000021','玩偶小店','专营各类二手玩偶，品质保证',1,'2026-03-05 21:05:16','2026-03-06 15:08:53',0),(8,'seller2','123456',2,'二手玩偶屋',NULL,'13000000022','二手玩偶屋','精品二手玩偶，价格实惠',1,'2026-03-05 21:05:16','2026-03-05 21:05:23',0),(9,'seller3','123456',2,'娃娃收藏馆',NULL,'13000000023','娃娃收藏馆','收藏级玩偶，限量珍品',1,'2026-03-05 21:05:16','2026-03-05 21:05:24',0),(10,'user','123456',2,'',NULL,'','',NULL,1,'2026-03-05 21:20:02','2026-03-05 21:20:02',0),(11,'user1','123456',2,'user',NULL,'13000000002','多余',NULL,1,'2026-03-05 21:31:29','2026-03-05 21:31:29',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-08 23:20:13
