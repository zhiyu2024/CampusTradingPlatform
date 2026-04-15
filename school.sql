/*
 Navicat Premium Data Transfer

 Source Server         : MySQL80
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3306
 Source Schema         : school

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 14/04/2026 17:47:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `quantity` int NOT NULL DEFAULT 1 COMMENT '商品数量',
  PRIMARY KEY (`cart_id`) USING BTREE,
  UNIQUE INDEX `uk_user_prod`(`user_id`, `product_id`) USING BTREE,
  INDEX `fk_cart_prod`(`product_id`) USING BTREE,
  CONSTRAINT `fk_cart_prod` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 5, 97, '2026-01-05 18:08:37', 1);
INSERT INTO `cart` VALUES (2, 80, 52, '2025-12-22 20:49:37', 1);
INSERT INTO `cart` VALUES (3, 50, 20, '2025-12-23 18:09:37', 1);
INSERT INTO `cart` VALUES (4, 76, 74, '2025-12-16 08:09:37', 1);
INSERT INTO `cart` VALUES (5, 87, 15, '2025-12-11 09:16:37', 1);
INSERT INTO `cart` VALUES (6, 10, 59, '2025-12-28 04:34:37', 1);
INSERT INTO `cart` VALUES (8, 20, 15, '2025-12-22 08:55:37', 1);
INSERT INTO `cart` VALUES (10, 76, 42, '2025-12-27 19:08:37', 1);
INSERT INTO `cart` VALUES (11, 100, 19, '2025-12-14 06:38:37', 1);
INSERT INTO `cart` VALUES (12, 88, 40, '2025-12-17 01:24:37', 1);
INSERT INTO `cart` VALUES (13, 40, 97, '2026-01-03 06:25:37', 1);
INSERT INTO `cart` VALUES (14, 17, 26, '2026-01-02 18:14:37', 1);
INSERT INTO `cart` VALUES (15, 83, 3, '2026-01-04 15:29:37', 1);
INSERT INTO `cart` VALUES (16, 27, 10, '2026-01-02 01:49:37', 1);
INSERT INTO `cart` VALUES (17, 56, 57, '2025-12-15 08:29:37', 1);
INSERT INTO `cart` VALUES (18, 13, 15, '2025-12-29 07:24:37', 1);
INSERT INTO `cart` VALUES (19, 94, 62, '2026-01-03 23:31:37', 1);
INSERT INTO `cart` VALUES (20, 76, 77, '2025-12-14 23:08:37', 1);
INSERT INTO `cart` VALUES (21, 65, 37, '2026-01-03 02:26:37', 1);
INSERT INTO `cart` VALUES (22, 49, 77, '2025-12-11 07:17:37', 1);
INSERT INTO `cart` VALUES (23, 29, 99, '2025-12-25 19:19:37', 1);
INSERT INTO `cart` VALUES (24, 84, 46, '2025-12-24 21:10:37', 1);
INSERT INTO `cart` VALUES (25, 25, 74, '2025-12-23 15:53:37', 1);
INSERT INTO `cart` VALUES (26, 38, 21, '2025-12-09 00:52:37', 1);
INSERT INTO `cart` VALUES (27, 23, 98, '2025-12-16 09:25:37', 1);
INSERT INTO `cart` VALUES (28, 21, 42, '2025-12-22 15:42:37', 1);
INSERT INTO `cart` VALUES (29, 38, 99, '2025-12-30 23:15:37', 1);
INSERT INTO `cart` VALUES (30, 53, 19, '2025-12-27 05:28:37', 1);
INSERT INTO `cart` VALUES (32, 6, 89, '2026-01-11 19:45:55', 1);
INSERT INTO `cart` VALUES (33, 3, 2, '2026-01-11 21:00:49', 1);
INSERT INTO `cart` VALUES (38, 110, 84, '2026-01-12 23:42:10', 3);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级ID',
  `sort_order` int NULL DEFAULT 0,
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '教材资料', 0, 1);
INSERT INTO `categories` VALUES (2, '电子产品', 0, 2);
INSERT INTO `categories` VALUES (3, '生活用品', 0, 3);
INSERT INTO `categories` VALUES (4, '运动乐器', 0, 4);
INSERT INTO `categories` VALUES (5, '考研资料', 1, 1);
INSERT INTO `categories` VALUES (6, '手机平板', 2, 1);

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID主键',
  `product_id` int NOT NULL COMMENT '关联商品ID（在哪件商品页面发起的沟通）',
  `sender_id` int NOT NULL COMMENT '发送者ID（买家或卖家）',
  `receiver_id` int NOT NULL COMMENT '接收者ID（另一方）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `message_type` tinyint NULL DEFAULT 1 COMMENT '消息类型：1-普通留言, 2-砍价请求, 3-回复, 4-系统通知',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0-未读, 1-已读',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_receiver`(`receiver_id`, `is_read`) USING BTREE COMMENT '接收者查询未读消息',
  INDEX `idx_product`(`product_id`) USING BTREE COMMENT '查询某商品的所有沟通记录',
  INDEX `idx_sender_receiver`(`sender_id`, `receiver_id`) USING BTREE COMMENT '查询两个人的对话历史',
  CONSTRAINT `fk_msg_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_msg_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_msg_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 28, 54, 36, '最低多少钱？诚心要', 1, 0, '2025-12-31 14:34:37');
INSERT INTO `messages` VALUES (2, 33, 78, 69, '可以便宜点吗？学生党', 3, 0, '2025-12-07 03:27:37');
INSERT INTO `messages` VALUES (3, 99, 61, 61, '明天能看货吗？在哪个宿舍？', 1, 0, '2025-12-18 19:06:37');
INSERT INTO `messages` VALUES (5, 70, 37, 9, '还能刀吗？可以的话现在下单', 2, 0, '2026-01-04 19:03:37');
INSERT INTO `messages` VALUES (6, 88, 79, 88, '还在吗？想现在过来看', 2, 0, '2025-12-16 20:54:37');
INSERT INTO `messages` VALUES (7, 17, 2, 83, '最低多少钱？诚心要', 2, 0, '2025-12-09 08:57:37');
INSERT INTO `messages` VALUES (8, 47, 56, 37, '还能刀吗？可以的话现在下单', 1, 1, '2025-12-08 02:38:37');
INSERT INTO `messages` VALUES (9, 45, 89, 93, '最低多少钱？诚心要', 1, 0, '2025-12-08 18:31:37');
INSERT INTO `messages` VALUES (10, 46, 42, 38, '还能刀吗？可以的话现在下单', 2, 0, '2026-01-04 18:59:37');
INSERT INTO `messages` VALUES (11, 26, 4, 98, '还能刀吗？可以的话现在下单', 2, 0, '2025-12-31 04:44:37');
INSERT INTO `messages` VALUES (12, 86, 57, 13, '已下单请尽快发货，急用', 1, 0, '2025-12-08 06:14:37');
INSERT INTO `messages` VALUES (13, 99, 89, 61, '能再拍几张实物图吗？', 2, 1, '2025-12-30 08:01:37');
INSERT INTO `messages` VALUES (14, 11, 19, 39, '还在吗？想现在过来看', 3, 1, '2025-12-20 07:49:37');
INSERT INTO `messages` VALUES (15, 28, 57, 36, '包邮吗？运费多少？', 3, 0, '2025-12-14 04:28:37');
INSERT INTO `messages` VALUES (16, 30, 16, 46, '可以便宜点吗？学生党', 2, 1, '2025-12-12 00:57:37');
INSERT INTO `messages` VALUES (17, 18, 8, 31, '可以便宜点吗？学生党', 3, 0, '2025-12-19 13:30:37');
INSERT INTO `messages` VALUES (18, 75, 33, 83, '还能刀吗？可以的话现在下单', 2, 1, '2025-12-27 18:01:37');
INSERT INTO `messages` VALUES (19, 14, 4, 59, '可以便宜点吗？学生党', 2, 0, '2026-01-05 11:05:37');
INSERT INTO `messages` VALUES (20, 27, 88, 65, '已下单请尽快发货，急用', 1, 0, '2025-12-27 13:12:37');
INSERT INTO `messages` VALUES (21, 91, 92, 28, '包邮吗？运费多少？', 3, 1, '2025-12-29 16:13:37');
INSERT INTO `messages` VALUES (22, 5, 60, 15, '有发票或购买记录吗？', 2, 1, '2026-01-04 12:06:37');
INSERT INTO `messages` VALUES (23, 99, 79, 61, '可以便宜点吗？学生党', 1, 1, '2026-01-01 13:37:37');
INSERT INTO `messages` VALUES (24, 72, 48, 53, '还在吗？想现在过来看', 3, 0, '2025-12-10 08:02:37');
INSERT INTO `messages` VALUES (25, 38, 51, 46, '明天能看货吗？在哪个宿舍？', 3, 1, '2025-12-12 20:02:37');
INSERT INTO `messages` VALUES (26, 7, 64, 45, '已下单请尽快发货，急用', 3, 1, '2025-12-09 18:29:37');
INSERT INTO `messages` VALUES (27, 9, 2, 87, '明天能看货吗？在哪个宿舍？', 1, 1, '2025-12-11 15:19:37');
INSERT INTO `messages` VALUES (28, 19, 91, 85, '可以便宜点吗？学生党', 2, 1, '2025-12-09 05:48:37');
INSERT INTO `messages` VALUES (29, 35, 4, 5, '最低多少钱？诚心要', 1, 1, '2025-12-21 06:50:37');
INSERT INTO `messages` VALUES (30, 39, 96, 14, '已下单请尽快发货，急用', 3, 0, '2025-12-12 01:29:37');
INSERT INTO `messages` VALUES (31, 82, 70, 91, '还在吗？想现在过来看', 2, 0, '2025-12-14 04:05:37');
INSERT INTO `messages` VALUES (32, 22, 58, 57, '有发票或购买记录吗？', 2, 0, '2025-12-18 01:09:37');
INSERT INTO `messages` VALUES (33, 42, 41, 2, '已下单请尽快发货，急用', 2, 1, '2025-12-30 17:57:37');
INSERT INTO `messages` VALUES (34, 3, 58, 90, '可以便宜点吗？学生党', 1, 0, '2025-12-18 21:38:37');
INSERT INTO `messages` VALUES (35, 63, 42, 34, '可以便宜点吗？学生党', 2, 1, '2025-12-31 08:55:37');
INSERT INTO `messages` VALUES (36, 15, 51, 38, '能再拍几张实物图吗？', 3, 1, '2025-12-17 07:26:37');
INSERT INTO `messages` VALUES (37, 10, 42, 44, '有发票或购买记录吗？', 3, 1, '2025-12-14 18:22:37');
INSERT INTO `messages` VALUES (38, 37, 10, 72, '还在吗？想现在过来看', 3, 1, '2025-12-07 09:01:37');
INSERT INTO `messages` VALUES (39, 89, 90, 81, '还能刀吗？可以的话现在下单', 2, 1, '2025-12-28 08:05:37');
INSERT INTO `messages` VALUES (40, 92, 79, 18, '还能刀吗？可以的话现在下单', 1, 0, '2025-12-24 11:06:37');
INSERT INTO `messages` VALUES (41, 21, 9, 20, '还能刀吗？可以的话现在下单', 2, 1, '2025-12-30 20:37:37');
INSERT INTO `messages` VALUES (42, 42, 57, 2, '有发票或购买记录吗？', 2, 1, '2025-12-10 16:58:37');
INSERT INTO `messages` VALUES (43, 18, 58, 31, '支持验机吗？有无拆修？', 3, 0, '2025-12-13 07:17:37');
INSERT INTO `messages` VALUES (44, 50, 3, 37, '支持验机吗？有无拆修？', 1, 1, '2026-01-02 10:42:37');
INSERT INTO `messages` VALUES (45, 97, 49, 72, '最低多少钱？诚心要', 3, 1, '2025-12-16 03:11:37');
INSERT INTO `messages` VALUES (46, 34, 97, 41, '包邮吗？运费多少？', 3, 0, '2026-01-01 17:55:37');
INSERT INTO `messages` VALUES (47, 15, 87, 38, '支持验机吗？有无拆修？', 2, 0, '2025-12-25 09:24:37');
INSERT INTO `messages` VALUES (48, 65, 86, 12, '还在吗？想现在过来看', 3, 1, '2025-12-19 07:41:37');
INSERT INTO `messages` VALUES (49, 68, 80, 96, '支持验机吗？有无拆修？', 1, 0, '2025-12-23 03:16:37');
INSERT INTO `messages` VALUES (50, 54, 97, 55, '还在吗？想现在过来看', 2, 1, '2025-12-27 14:35:37');
INSERT INTO `messages` VALUES (52, 2, 2, 4, '您好,请问可以砍价吗', 2, 1, '2026-01-11 13:25:15');
INSERT INTO `messages` VALUES (53, 2, 2, 9, '您好,请问可以砍价吗', 2, 0, '2026-01-11 13:25:19');
INSERT INTO `messages` VALUES (55, 2, 4, 2, '不可以,亲', 1, 0, '2026-01-12 21:35:25');
INSERT INTO `messages` VALUES (56, 14, 4, 59, '可以', 1, 0, '2026-01-12 21:39:35');
INSERT INTO `messages` VALUES (57, 26, 4, 98, '不能刀喔', 1, 0, '2026-01-12 21:42:54');
INSERT INTO `messages` VALUES (58, 17, 2, 83, '最低400块', 1, 0, '2026-01-12 22:01:47');
INSERT INTO `messages` VALUES (59, 18, 8, 31, '圈外人24', 1, 0, '2026-01-12 23:18:52');
INSERT INTO `messages` VALUES (60, 42, 2, 41, '可以', 1, 0, '2026-01-13 00:28:08');
INSERT INTO `messages` VALUES (61, 17, 2, 83, '几百块', 1, 0, '2026-01-13 00:56:31');
INSERT INTO `messages` VALUES (62, 42, 2, 41, '回复', 1, 0, '2026-01-13 12:39:35');
INSERT INTO `messages` VALUES (63, 42, 2, 41, '胡椒粉', 1, 0, '2026-01-13 12:44:56');
INSERT INTO `messages` VALUES (64, 42, 2, 41, '111', 1, 0, '2026-04-06 23:48:18');
INSERT INTO `messages` VALUES (65, 84, 66, 92, '我想咨询这个商品', 1, 0, '2026-04-08 15:12:15');
INSERT INTO `messages` VALUES (66, 84, 2, 92, '我想咨询这个商品', 1, 0, '2026-04-08 15:12:16');
INSERT INTO `messages` VALUES (67, 28, 2, 54, '我想咨询这个商品', 1, 0, '2026-04-08 15:59:00');
INSERT INTO `messages` VALUES (68, 10, 66, 44, '我想咨询这个商品', 1, 0, '2026-04-08 16:38:08');
INSERT INTO `messages` VALUES (69, 36, 66, 58, '我想咨询这个商品', 1, 0, '2026-04-08 16:39:21');
INSERT INTO `messages` VALUES (70, 42, 66, 2, '我想咨询这个商品', 1, 1, '2026-04-08 16:39:33');
INSERT INTO `messages` VALUES (71, 11, 77, 39, '测试聊天会话', 1, 0, '2026-04-08 18:13:02');
INSERT INTO `messages` VALUES (72, 5, 77, 15, '这是?', 1, 0, '2026-04-08 18:13:35');
INSERT INTO `messages` VALUES (73, 28, 55, 36, '感觉镜子不错,还可以优惠一点吗?', 1, 0, '2026-04-08 19:24:10');
INSERT INTO `messages` VALUES (74, 42, 2, 57, '没有', 1, 0, '2026-04-08 19:46:51');
INSERT INTO `messages` VALUES (75, 42, 2, 57, '没有', 1, 0, '2026-04-08 19:46:51');
INSERT INTO `messages` VALUES (76, 84, 2, 92, '在吗', 1, 0, '2026-04-08 19:47:44');
INSERT INTO `messages` VALUES (77, 36, 11, 58, '你好', 1, 0, '2026-04-08 21:05:24');
INSERT INTO `messages` VALUES (78, 10, 44, 66, '多少钱', 1, 0, '2026-04-14 10:55:30');

-- ----------------------------
-- Table structure for order_comments
-- ----------------------------
DROP TABLE IF EXISTS `order_comments`;
CREATE TABLE `order_comments`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `product_id` int NOT NULL COMMENT '商品ID',
  `buyer_id` int NOT NULL COMMENT '买家ID',
  `seller_id` int NOT NULL COMMENT '卖家ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价内容',
  `score` tinyint NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `sentiment` int NULL DEFAULT 0 COMMENT 'AI情感倾向：0未分析 1好评 2差评 3中性',
  `confidence` decimal(5, 4) NULL DEFAULT 0.0000 COMMENT 'AI置信度 0-1',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id`) USING BTREE,
  INDEX `idx_seller_id`(`seller_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_comments
-- ----------------------------
INSERT INTO `order_comments` VALUES (12, 30, 'ORD176811341809333800', 2, 2, 90, '非常有用,还有学长的笔记!!!', 5, '2026-04-08 10:06:30', 0, 0.0000);
INSERT INTO `order_comments` VALUES (13, 1, 'ORD17676216971071059', 18, 4, 31, '非常好,很灵敏,不像是二手', 5, '2026-04-08 10:30:06', 0, 0.0000);
INSERT INTO `order_comments` VALUES (14, 14, 'ORD17676216971136634', 55, 66, 47, '非常好,笔记非常有用', 5, '2026-04-08 10:32:48', 0, 0.0000);
INSERT INTO `order_comments` VALUES (15, 67, 'ORD177562841667847165', 40, 66, 56, '书不错,价格低廉,实惠,痕迹少', 5, '2026-04-08 14:10:38', 0, 0.0000);
INSERT INTO `order_comments` VALUES (16, 68, 'ORD177562841694356938', 65, 66, 12, '稍微有点旧,但是能接受,折痕多,有点泛黄', 4, '2026-04-08 14:11:17', 0, 0.0000);
INSERT INTO `order_comments` VALUES (17, 69, 'ORD177562841701841829', 88, 66, 88, '台灯超级亮,而且很好看,感觉桌面都漂亮了,哈哈哈', 5, '2026-04-08 14:11:47', 0, 0.0000);
INSERT INTO `order_comments` VALUES (18, 66, 'ORD177562841633216433', 73, 66, 78, '台灯不戳啊,很新,还便宜,比网上的好多了', 5, '2026-04-08 14:12:17', 0, 0.0000);
INSERT INTO `order_comments` VALUES (19, 62, 'ORD177562715066403462', 5, 66, 15, '比直接买实惠多了,谢谢学长的笔记~', 5, '2026-04-08 14:12:42', 0, 0.0000);
INSERT INTO `order_comments` VALUES (20, 63, 'ORD177562715075798878', 10, 66, 44, '不戳不戳,这风扇和新的一样,推荐入手,价格低廉!', 5, '2026-04-08 14:13:36', 0, 0.0000);
INSERT INTO `order_comments` VALUES (21, 64, 'ORD177562715100883787', 89, 66, 81, '有点卡顿,但无伤大雅,还是可以用的,比在某鱼实惠,还不用等物流', 4, '2026-04-08 14:14:20', 0, 0.0000);
INSERT INTO `order_comments` VALUES (22, 65, 'ORD177562715110039271', 84, 66, 92, '这是全新风扇吗?咋积灰比我家用5年的风扇都多', 3, '2026-04-08 14:15:10', 0, 0.0000);
INSERT INTO `order_comments` VALUES (23, 60, 'ORD177562715002971554', 39, 66, 14, '不戳啊不戳,这个吉他很便宜,我是买来在大学陶冶情操的,用不着全新,这个就刚刚好\\(^o^)/~', 5, '2026-04-08 14:15:57', 0, 0.0000);
INSERT INTO `order_comments` VALUES (24, 61, 'ORD177562715040569571', 42, 66, 2, '一般般', 3, '2026-04-08 14:16:09', 0, 0.0000);
INSERT INTO `order_comments` VALUES (25, 58, 'ORD177561935217811531', 39, 66, 14, '不错的吉他,挺新的', 5, '2026-04-08 14:16:27', 0, 0.0000);
INSERT INTO `order_comments` VALUES (26, 59, 'ORD177561935244298548', 42, 66, 2, '还行蛤,能用,主要是便宜', 4, '2026-04-08 14:16:41', 0, 0.0000);
INSERT INTO `order_comments` VALUES (27, 57, 'ORD177561934671093993', 42, 66, 2, '感谢学长,哈哈哈,砍价砍了5元,赞👍', 5, '2026-04-08 14:17:12', 0, 0.0000);
INSERT INTO `order_comments` VALUES (28, 56, 'ORD177561934636655546', 39, 66, 14, '吉他有点旧,但是音色还不错', 5, '2026-04-08 14:17:31', 0, 0.0000);
INSERT INTO `order_comments` VALUES (29, 54, 'ORD177561932269226015', 39, 66, 14, '这个吉他有点破', 3, '2026-04-08 14:17:50', 0, 0.0000);
INSERT INTO `order_comments` VALUES (30, 52, 'ORD177561931765532970', 39, 66, 14, '外表看着挺好的,一弹发现音色差就算了,吉他弦好像是断了重接的,质量很差', 3, '2026-04-08 14:19:11', 0, 0.0000);
INSERT INTO `order_comments` VALUES (31, 55, 'ORD177561932304844584', 42, 66, 2, '挺好挺好,比直接买便宜一大半,就是痕迹多了一丢丢', 4, '2026-04-08 14:19:41', 0, 0.0000);
INSERT INTO `order_comments` VALUES (32, 53, 'ORD177561931769036425', 42, 66, 2, '非常好,感觉有9成新了', 5, '2026-04-08 14:19:56', 0, 0.0000);
INSERT INTO `order_comments` VALUES (33, 51, 'ORD177561931223845060', 42, 66, 2, '不错', 5, '2026-04-08 14:20:02', 0, 0.0000);
INSERT INTO `order_comments` VALUES (34, 50, 'ORD177561931196738258', 39, 66, 14, '非常好', 5, '2026-04-08 14:20:10', 0, 0.0000);
INSERT INTO `order_comments` VALUES (35, 77, 'ORD177565112227481004', 3, 2, 90, '非常不戳!!\\(^o^)/~', 5, '2026-04-08 20:25:55', 0, 0.0000);
INSERT INTO `order_comments` VALUES (36, 79, 'ORD177565355967702459', 84, 11, 92, '风扇还很新,价格实惠,学长还便宜了3块给我,哈哈哈^_^', 5, '2026-04-08 21:07:14', 0, 0.0000);
INSERT INTO `order_comments` VALUES (37, 80, 'ORD177609448673111828', 5, 44, 15, '质量还不错诶,比我想象的好,线下见面的时候,除了这本书,小姐姐还送了本笔记本,哈哈哈', 5, '2026-04-13 23:35:56', 1, 0.9996);
INSERT INTO `order_comments` VALUES (38, 84, 'ORD177613851390735955', 119, 2, 22, '书很新,学姐很快就将书送到', 5, '2026-04-14 11:49:05', 0, 0.0000);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `product_id` int NOT NULL COMMENT '商品ID',
  `buyer_id` int NOT NULL COMMENT '买家ID',
  `seller_id` int NOT NULL COMMENT '卖家ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态:0-待付款,1-已完成,2-已取消',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `buyer_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '买家留言（替代单独消息表）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `quantity` int NOT NULL DEFAULT 1 COMMENT '商品数量',
  `has_comment` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否评价：0未评价 1已评价',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_buyer`(`buyer_id`) USING BTREE,
  INDEX `idx_seller`(`seller_id`) USING BTREE,
  INDEX `fk_ord_prod`(`product_id`) USING BTREE,
  CONSTRAINT `fk_ord_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ord_prod` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ord_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 80 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD17676216971071059', 18, 4, 31, 1, 61.39, '', '2026-01-04 11:19:37', '2026-04-08 10:29:31', 1, 1);
INSERT INTO `orders` VALUES (2, 'ORD17676216971121513', 19, 23, 85, 1, 107.12, '', '2026-01-04 13:43:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (3, 'ORD17676216971125654', 22, 16, 57, 1, 25.92, '', '2025-12-29 06:28:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (4, 'ORD17676216971127165', 29, 1, 9, 1, 72.15, '', '2025-12-16 17:18:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (5, 'ORD17676216971125019', 33, 55, 69, 0, 134.62, '', '2026-01-05 09:13:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (6, 'ORD17676216971127889', 35, 27, 5, 1, 56.20, '麻烦尽快发货，急用', '2025-12-29 21:47:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (7, 'ORD17676216971139214', 36, 59, 58, 1, 89.69, '', '2026-01-05 00:59:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (8, 'ORD17676216971136886', 37, 92, 72, 1, 45.31, '', '2025-12-09 12:22:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (9, 'ORD17676216971133852', 47, 58, 37, 1, 43.67, '', '2025-12-23 01:40:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (10, 'ORD17676216971135825', 48, 70, 43, 0, 121.94, '', '2025-12-11 08:02:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (11, 'ORD17676216971137271', 49, 12, 80, 1, 66.81, '麻烦尽快发货，急用', '2025-12-17 13:56:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (12, 'ORD17676216971136390', 51, 100, 53, 1, 172.24, '', '2026-01-04 04:10:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (13, 'ORD17676216971131165', 52, 20, 79, 1, 349.22, '', '2025-12-25 15:10:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (14, 'ORD17676216971136634', 55, 66, 47, 1, 62.61, '麻烦尽快发货，急用', '2025-12-07 15:07:37', '2026-04-08 10:32:25', 1, 1);
INSERT INTO `orders` VALUES (15, 'ORD17676216971139578', 59, 95, 35, 1, 269.27, '', '2025-12-27 15:00:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (16, 'ORD17676216971135624', 60, 62, 96, 0, 285.11, '', '2025-12-11 22:38:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (17, 'ORD17676216971132885', 61, 86, 11, 0, 90.84, '', '2025-12-21 07:03:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (18, 'ORD17676216971133325', 64, 42, 7, 0, 139.14, '麻烦尽快发货，急用', '2025-12-28 02:04:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (19, 'ORD17676216971138816', 68, 26, 96, 1, 229.84, '', '2026-01-02 09:32:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (20, 'ORD17676216971130136', 69, 84, 69, 1, 43.53, '麻烦尽快发货，急用', '2025-12-18 03:30:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (21, 'ORD17676216971139777', 70, 75, 9, 0, 49.71, '麻烦尽快发货，急用', '2025-12-29 04:06:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (22, 'ORD17676216971137698', 71, 2, 7, 1, 87.62, '', '2026-01-03 22:30:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (23, 'ORD17676216971147098', 80, 10, 82, 1, 54.73, '', '2025-12-18 23:18:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (24, 'ORD17676216971140319', 82, 65, 91, 1, 83.00, '', '2025-12-19 05:37:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (25, 'ORD17676216971142473', 91, 100, 28, 0, 84.65, '', '2025-12-29 03:10:37', '2026-01-05 22:01:37', 1, 0);
INSERT INTO `orders` VALUES (26, 'ORD176811333607797474', 2, 2, 90, 0, 47.65, '麻烦尽快发货', '2026-01-11 14:35:36', '2026-01-11 14:35:36', 1, 0);
INSERT INTO `orders` VALUES (27, 'ORD176811340098153519', 2, 2, 90, 0, 47.65, '麻烦尽快发货', '2026-01-11 14:36:41', '2026-01-11 14:36:41', 1, 0);
INSERT INTO `orders` VALUES (28, 'ORD176811340601510459', 2, 2, 90, 0, 47.65, '麻烦尽快发货', '2026-01-11 14:36:46', '2026-01-11 14:36:46', 1, 0);
INSERT INTO `orders` VALUES (29, 'ORD176811340816539847', 2, 2, 90, 0, 47.65, '麻烦尽快发货', '2026-01-11 14:36:48', '2026-01-11 14:36:48', 1, 0);
INSERT INTO `orders` VALUES (30, 'ORD176811341809333800', 2, 2, 90, 1, 47.65, '麻烦尽快发货', '2026-01-11 14:36:58', '2026-01-11 14:46:41', 1, 1);
INSERT INTO `orders` VALUES (31, 'ORD176811342307511873', 2, 2, 90, 2, 47.65, '麻烦尽快发货', '2026-01-11 14:37:03', '2026-01-11 14:44:56', 1, 0);
INSERT INTO `orders` VALUES (32, 'ORD176823117523868693', 90, 8, 31, 0, 81.61, '', '2026-01-12 23:19:35', '2026-01-12 23:19:35', 1, 0);
INSERT INTO `orders` VALUES (36, 'ORD176823277752716776', 84, 112, 92, 0, 28.77, '', '2026-01-12 23:46:18', '2026-01-12 23:46:18', 1, 0);
INSERT INTO `orders` VALUES (37, 'ORD176823786662273820', 5, 4, 15, 0, 33.28, '', '2026-01-13 01:11:07', '2026-01-13 01:11:07', 1, 0);
INSERT INTO `orders` VALUES (38, 'ORD176827943894148584', 95, 2, 27, 0, 37.77, '', '2026-01-13 12:43:59', '2026-01-13 12:43:59', 1, 0);
INSERT INTO `orders` VALUES (43, 'ORD177556880653473700', 6, 2, 5, 0, 43.64, '', '2026-04-07 21:33:27', '2026-04-07 21:33:27', 2, 0);
INSERT INTO `orders` VALUES (45, 'ORD177557083674754352', 39, 2, 14, 0, 118.98, '', '2026-04-07 22:07:17', '2026-04-07 22:07:17', 1, 0);
INSERT INTO `orders` VALUES (46, 'ORD177557179950641657', 84, 2, 92, 0, 28.77, '', '2026-04-07 22:23:20', '2026-04-07 22:23:20', 1, 0);
INSERT INTO `orders` VALUES (47, 'ORD177557200970404367', 73, 2, 78, 0, 37.01, '', '2026-04-07 22:26:50', '2026-04-07 22:26:50', 1, 0);
INSERT INTO `orders` VALUES (50, 'ORD177561931196738258', 39, 66, 14, 1, 118.98, '', '2026-04-08 11:35:12', '2026-04-08 14:07:42', 1, 1);
INSERT INTO `orders` VALUES (51, 'ORD177561931223845060', 42, 66, 2, 1, 98.50, '', '2026-04-08 11:35:12', '2026-04-08 14:07:44', 1, 1);
INSERT INTO `orders` VALUES (52, 'ORD177561931765532970', 39, 66, 14, 1, 118.98, '', '2026-04-08 11:35:18', '2026-04-08 14:07:39', 1, 1);
INSERT INTO `orders` VALUES (53, 'ORD177561931769036425', 42, 66, 2, 1, 98.50, '', '2026-04-08 11:35:18', '2026-04-08 14:07:41', 1, 1);
INSERT INTO `orders` VALUES (54, 'ORD177561932269226015', 39, 66, 14, 1, 118.98, '', '2026-04-08 11:35:23', '2026-04-08 14:07:36', 1, 1);
INSERT INTO `orders` VALUES (55, 'ORD177561932304844584', 42, 66, 2, 1, 98.50, '', '2026-04-08 11:35:23', '2026-04-08 14:07:38', 1, 1);
INSERT INTO `orders` VALUES (56, 'ORD177561934636655546', 39, 66, 14, 1, 118.98, '', '2026-04-08 11:35:46', '2026-04-08 14:07:34', 1, 1);
INSERT INTO `orders` VALUES (57, 'ORD177561934671093993', 42, 66, 2, 1, 98.50, '', '2026-04-08 11:35:47', '2026-04-08 14:07:32', 1, 1);
INSERT INTO `orders` VALUES (58, 'ORD177561935217811531', 39, 66, 14, 1, 118.98, '', '2026-04-08 11:35:52', '2026-04-08 14:07:29', 1, 1);
INSERT INTO `orders` VALUES (59, 'ORD177561935244298548', 42, 66, 2, 1, 98.50, '', '2026-04-08 11:35:52', '2026-04-08 14:07:31', 1, 1);
INSERT INTO `orders` VALUES (60, 'ORD177562715002971554', 39, 66, 14, 1, 118.98, '', '2026-04-08 13:45:50', '2026-04-08 14:07:23', 1, 1);
INSERT INTO `orders` VALUES (61, 'ORD177562715040569571', 42, 66, 2, 1, 98.50, '', '2026-04-08 13:45:50', '2026-04-08 14:07:24', 1, 1);
INSERT INTO `orders` VALUES (62, 'ORD177562715066403462', 5, 66, 15, 1, 33.28, '', '2026-04-08 13:45:51', '2026-04-08 14:07:15', 1, 1);
INSERT INTO `orders` VALUES (63, 'ORD177562715075798878', 10, 66, 44, 1, 22.22, '', '2026-04-08 13:45:51', '2026-04-08 14:07:17', 1, 1);
INSERT INTO `orders` VALUES (64, 'ORD177562715100883787', 89, 66, 81, 1, 93.01, '', '2026-04-08 13:45:51', '2026-04-08 14:07:19', 1, 1);
INSERT INTO `orders` VALUES (65, 'ORD177562715110039271', 84, 66, 92, 1, 28.77, '', '2026-04-08 13:45:51', '2026-04-08 14:07:21', 1, 1);
INSERT INTO `orders` VALUES (66, 'ORD177562841633216433', 73, 66, 78, 1, 37.01, '', '2026-04-08 14:06:56', '2026-04-08 14:07:14', 1, 1);
INSERT INTO `orders` VALUES (67, 'ORD177562841667847165', 40, 66, 56, 1, 99.94, '', '2026-04-08 14:06:57', '2026-04-08 14:07:08', 1, 1);
INSERT INTO `orders` VALUES (68, 'ORD177562841694356938', 65, 66, 12, 1, 87.23, '', '2026-04-08 14:06:57', '2026-04-08 14:07:10', 1, 1);
INSERT INTO `orders` VALUES (69, 'ORD177562841701841829', 88, 66, 88, 1, 44.64, '', '2026-04-08 14:06:57', '2026-04-08 14:07:12', 1, 1);
INSERT INTO `orders` VALUES (70, 'ORD177564818612324712', 89, 2, 81, 0, 93.01, '', '2026-04-08 19:36:26', '2026-04-08 19:36:26', 1, 0);
INSERT INTO `orders` VALUES (71, 'ORD177564967377805974', 73, 2, 78, 0, 37.01, '', '2026-04-08 20:01:14', '2026-04-08 20:01:14', 1, 0);
INSERT INTO `orders` VALUES (72, 'ORD177564968653865376', 5, 2, 15, 1, 33.28, '', '2026-04-08 20:01:27', '2026-04-08 20:01:43', 1, 0);
INSERT INTO `orders` VALUES (73, 'ORD177565031433773471', 65, 2, 12, 0, 87.23, '', '2026-04-08 20:11:54', '2026-04-08 20:11:54', 1, 0);
INSERT INTO `orders` VALUES (74, 'ORD177565032300364013', 40, 2, 56, 2, 99.94, '', '2026-04-08 20:12:03', '2026-04-08 20:12:18', 1, 0);
INSERT INTO `orders` VALUES (75, 'ORD177565033028916059', 42, 2, 2, 2, 98.50, '', '2026-04-08 20:12:10', '2026-04-08 20:12:13', 1, 0);
INSERT INTO `orders` VALUES (76, 'ORD177565110461339435', 65, 2, 12, 2, 87.23, '', '2026-04-08 20:25:05', '2026-04-08 20:25:09', 1, 0);
INSERT INTO `orders` VALUES (77, 'ORD177565112227481004', 3, 2, 90, 1, 10.68, '', '2026-04-08 20:25:22', '2026-04-08 20:25:43', 1, 1);
INSERT INTO `orders` VALUES (78, 'ORD177565113016590845', 40, 2, 56, 2, 99.94, '', '2026-04-08 20:25:30', '2026-04-08 20:25:36', 1, 0);
INSERT INTO `orders` VALUES (79, 'ORD177565355967702459', 84, 11, 92, 1, 28.77, '', '2026-04-08 21:06:00', '2026-04-08 21:06:02', 1, 1);
INSERT INTO `orders` VALUES (80, 'ORD177609448673111828', 5, 44, 15, 1, 33.28, '', '2026-04-13 23:34:47', '2026-04-13 23:34:50', 1, 1);
INSERT INTO `orders` VALUES (81, 'ORD177609448698742463', 10, 44, 44, 1, 22.22, '', '2026-04-13 23:34:47', '2026-04-13 23:34:52', 1, 0);
INSERT INTO `orders` VALUES (82, 'ORD177609448707522682', 89, 44, 81, 1, 93.01, '', '2026-04-13 23:34:47', '2026-04-13 23:34:54', 1, 0);
INSERT INTO `orders` VALUES (83, 'ORD177609448734605964', 84, 44, 92, 1, 28.77, '', '2026-04-13 23:34:47', '2026-04-13 23:34:55', 1, 0);
INSERT INTO `orders` VALUES (84, 'ORD177613851390735955', 119, 2, 22, 1, 20.00, '', '2026-04-14 11:48:34', '2026-04-14 11:48:36', 1, 1);

-- ----------------------------
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images`  (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL COMMENT '商品ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `idx_prod`(`product_id`) USING BTREE,
  CONSTRAINT `fk_img_prod` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_images
-- ----------------------------
INSERT INTO `product_images` VALUES (1, 1, 'http://localhost:8090/images/bike1.png', 0);
INSERT INTO `product_images` VALUES (2, 1, 'http://localhost:8090/images/bike3.png', 1);
INSERT INTO `product_images` VALUES (3, 1, 'http://localhost:8090/images/music5.png', 2);
INSERT INTO `product_images` VALUES (4, 2, 'http://localhost:8090/images/material6.png', 0);
INSERT INTO `product_images` VALUES (5, 2, 'http://localhost:8090/images/material7.png', 1);
INSERT INTO `product_images` VALUES (6, 2, 'http://localhost:8090/images/material1.png', 2);
INSERT INTO `product_images` VALUES (7, 3, 'http://localhost:8090/images/live2.png', 0);
INSERT INTO `product_images` VALUES (8, 3, 'http://localhost:8090/images/live6.png', 1);
INSERT INTO `product_images` VALUES (9, 3, 'http://localhost:8090/images/live10.png', 2);
INSERT INTO `product_images` VALUES (10, 4, 'http://localhost:8090/images/camera5.png', 0);
INSERT INTO `product_images` VALUES (11, 4, 'http://localhost:8090/images/switch3.png', 1);
INSERT INTO `product_images` VALUES (12, 4, 'http://localhost:8090/images/switch5.png', 2);
INSERT INTO `product_images` VALUES (13, 5, 'http://localhost:8090/images/book4.png', 0);
INSERT INTO `product_images` VALUES (14, 5, 'http://localhost:8090/images/book5.png', 1);
INSERT INTO `product_images` VALUES (15, 5, 'http://localhost:8090/images/book9.png', 2);
INSERT INTO `product_images` VALUES (16, 6, 'http://localhost:8090/images/material2.png', 0);
INSERT INTO `product_images` VALUES (17, 6, 'http://localhost:8090/images/material7.png', 1);
INSERT INTO `product_images` VALUES (18, 6, 'http://localhost:8090/images/material5.png', 2);
INSERT INTO `product_images` VALUES (19, 7, 'http://localhost:8090/images/ear2.png', 0);
INSERT INTO `product_images` VALUES (20, 7, 'http://localhost:8090/images/computed1.png', 1);
INSERT INTO `product_images` VALUES (21, 7, 'http://localhost:8090/images/phone4.png', 2);
INSERT INTO `product_images` VALUES (22, 8, 'http://localhost:8090/images/book10.png', 0);
INSERT INTO `product_images` VALUES (23, 8, 'http://localhost:8090/images/book8.png', 1);
INSERT INTO `product_images` VALUES (24, 8, 'http://localhost:8090/images/book4.png', 2);
INSERT INTO `product_images` VALUES (25, 9, 'http://localhost:8090/images/camera3.png', 0);
INSERT INTO `product_images` VALUES (26, 9, 'http://localhost:8090/images/ear1.png', 1);
INSERT INTO `product_images` VALUES (27, 9, 'http://localhost:8090/images/computed5.png', 2);
INSERT INTO `product_images` VALUES (28, 10, 'http://localhost:8090/images/live6.png', 0);
INSERT INTO `product_images` VALUES (29, 10, 'http://localhost:8090/images/live2.png', 1);
INSERT INTO `product_images` VALUES (30, 10, 'http://localhost:8090/images/live9.png', 2);
INSERT INTO `product_images` VALUES (31, 11, 'http://localhost:8090/images/camera1.png', 0);
INSERT INTO `product_images` VALUES (32, 11, 'http://localhost:8090/images/camera5.png', 1);
INSERT INTO `product_images` VALUES (33, 11, 'http://localhost:8090/images/computed4.png', 2);
INSERT INTO `product_images` VALUES (34, 12, 'http://localhost:8090/images/book2.png', 0);
INSERT INTO `product_images` VALUES (35, 12, 'http://localhost:8090/images/book9.png', 1);
INSERT INTO `product_images` VALUES (36, 12, 'http://localhost:8090/images/book4.png', 2);
INSERT INTO `product_images` VALUES (40, 14, 'http://localhost:8090/images/book7.png', 0);
INSERT INTO `product_images` VALUES (41, 14, 'http://localhost:8090/images/book10.png', 1);
INSERT INTO `product_images` VALUES (42, 14, 'http://localhost:8090/images/book4.png', 2);
INSERT INTO `product_images` VALUES (43, 15, 'http://localhost:8090/images/computed1.png', 0);
INSERT INTO `product_images` VALUES (44, 15, 'http://localhost:8090/images/camera1.png', 1);
INSERT INTO `product_images` VALUES (45, 15, 'http://localhost:8090/images/computed5.png', 2);
INSERT INTO `product_images` VALUES (46, 16, 'http://localhost:8090/images/live8.png', 0);
INSERT INTO `product_images` VALUES (47, 16, 'http://localhost:8090/images/live10.png', 1);
INSERT INTO `product_images` VALUES (48, 16, 'http://localhost:8090/images/live9.png', 2);
INSERT INTO `product_images` VALUES (49, 17, 'http://localhost:8090/images/music5.png', 0);
INSERT INTO `product_images` VALUES (50, 17, 'http://localhost:8090/images/music4.png', 1);
INSERT INTO `product_images` VALUES (51, 17, 'http://localhost:8090/images/bike4.png', 2);
INSERT INTO `product_images` VALUES (52, 18, 'http://localhost:8090/images/ear2.png', 0);
INSERT INTO `product_images` VALUES (53, 18, 'http://localhost:8090/images/switch2.png', 1);
INSERT INTO `product_images` VALUES (54, 18, 'http://localhost:8090/images/ear5.png', 2);
INSERT INTO `product_images` VALUES (55, 19, 'http://localhost:8090/images/switch2.png', 0);
INSERT INTO `product_images` VALUES (56, 19, 'http://localhost:8090/images/camera5.png', 1);
INSERT INTO `product_images` VALUES (57, 19, 'http://localhost:8090/images/camera2.png', 2);
INSERT INTO `product_images` VALUES (58, 20, 'http://localhost:8090/images/music5.png', 0);
INSERT INTO `product_images` VALUES (59, 20, 'http://localhost:8090/images/bike1.png', 1);
INSERT INTO `product_images` VALUES (60, 20, 'http://localhost:8090/images/music4.png', 2);
INSERT INTO `product_images` VALUES (61, 21, 'http://localhost:8090/images/camera3.png', 0);
INSERT INTO `product_images` VALUES (62, 21, 'http://localhost:8090/images/switch4.png', 1);
INSERT INTO `product_images` VALUES (63, 21, 'http://localhost:8090/images/ear2.png', 2);
INSERT INTO `product_images` VALUES (64, 22, 'http://localhost:8090/images/live6.png', 0);
INSERT INTO `product_images` VALUES (65, 22, 'http://localhost:8090/images/live9.png', 1);
INSERT INTO `product_images` VALUES (66, 22, 'http://localhost:8090/images/live5.png', 2);
INSERT INTO `product_images` VALUES (67, 23, 'http://localhost:8090/images/bike3.png', 0);
INSERT INTO `product_images` VALUES (68, 23, 'http://localhost:8090/images/music5.png', 1);
INSERT INTO `product_images` VALUES (69, 23, 'http://localhost:8090/images/bike2.png', 2);
INSERT INTO `product_images` VALUES (70, 24, 'http://localhost:8090/images/phone2.png', 0);
INSERT INTO `product_images` VALUES (71, 24, 'http://localhost:8090/images/phone3.png', 1);
INSERT INTO `product_images` VALUES (72, 24, 'http://localhost:8090/images/phone1.png', 2);
INSERT INTO `product_images` VALUES (73, 25, 'http://localhost:8090/images/computed3.png', 0);
INSERT INTO `product_images` VALUES (74, 25, 'http://localhost:8090/images/switch2.png', 1);
INSERT INTO `product_images` VALUES (75, 25, 'http://localhost:8090/images/switch4.png', 2);
INSERT INTO `product_images` VALUES (76, 26, 'http://localhost:8090/images/book3.png', 0);
INSERT INTO `product_images` VALUES (77, 26, 'http://localhost:8090/images/book1.png', 1);
INSERT INTO `product_images` VALUES (78, 26, 'http://localhost:8090/images/book2.png', 2);
INSERT INTO `product_images` VALUES (79, 27, 'http://localhost:8090/images/live2.png', 0);
INSERT INTO `product_images` VALUES (80, 27, 'http://localhost:8090/images/live9.png', 1);
INSERT INTO `product_images` VALUES (81, 27, 'http://localhost:8090/images/live5.png', 2);
INSERT INTO `product_images` VALUES (82, 28, 'http://localhost:8090/images/live10.png', 0);
INSERT INTO `product_images` VALUES (83, 28, 'http://localhost:8090/images/live4.png', 1);
INSERT INTO `product_images` VALUES (84, 28, 'http://localhost:8090/images/live9.png', 2);
INSERT INTO `product_images` VALUES (85, 29, 'http://localhost:8090/images/phone1.png', 0);
INSERT INTO `product_images` VALUES (86, 29, 'http://localhost:8090/images/switch3.png', 1);
INSERT INTO `product_images` VALUES (87, 29, 'http://localhost:8090/images/phone2.png', 2);
INSERT INTO `product_images` VALUES (88, 30, 'http://localhost:8090/images/camera1.png', 0);
INSERT INTO `product_images` VALUES (89, 30, 'http://localhost:8090/images/ear4.png', 1);
INSERT INTO `product_images` VALUES (90, 30, 'http://localhost:8090/images/computed2.png', 2);
INSERT INTO `product_images` VALUES (91, 31, 'http://localhost:8090/images/bike3.png', 0);
INSERT INTO `product_images` VALUES (92, 31, 'http://localhost:8090/images/music1.png', 1);
INSERT INTO `product_images` VALUES (93, 31, 'http://localhost:8090/images/bike4.png', 2);
INSERT INTO `product_images` VALUES (94, 32, 'http://localhost:8090/images/camera1.png', 0);
INSERT INTO `product_images` VALUES (95, 32, 'http://localhost:8090/images/camera4.png', 1);
INSERT INTO `product_images` VALUES (96, 32, 'http://localhost:8090/images/camera2.png', 2);
INSERT INTO `product_images` VALUES (97, 33, 'http://localhost:8090/images/ear5.png', 0);
INSERT INTO `product_images` VALUES (98, 33, 'http://localhost:8090/images/computed3.png', 1);
INSERT INTO `product_images` VALUES (99, 33, 'http://localhost:8090/images/ear3.png', 2);
INSERT INTO `product_images` VALUES (100, 34, 'http://localhost:8090/images/book6.png', 0);
INSERT INTO `product_images` VALUES (101, 34, 'http://localhost:8090/images/book8.png', 1);
INSERT INTO `product_images` VALUES (102, 34, 'http://localhost:8090/images/book9.png', 2);
INSERT INTO `product_images` VALUES (103, 35, 'http://localhost:8090/images/material5.png', 0);
INSERT INTO `product_images` VALUES (104, 35, 'http://localhost:8090/images/material6.png', 1);
INSERT INTO `product_images` VALUES (105, 35, 'http://localhost:8090/images/material7.png', 2);
INSERT INTO `product_images` VALUES (106, 36, 'http://localhost:8090/images/book10.png', 0);
INSERT INTO `product_images` VALUES (107, 36, 'http://localhost:8090/images/book2.png', 1);
INSERT INTO `product_images` VALUES (108, 36, 'http://localhost:8090/images/book8.png', 2);
INSERT INTO `product_images` VALUES (109, 37, 'http://localhost:8090/images/live5.png', 0);
INSERT INTO `product_images` VALUES (110, 37, 'http://localhost:8090/images/live1.png', 1);
INSERT INTO `product_images` VALUES (111, 37, 'http://localhost:8090/images/live8.png', 2);
INSERT INTO `product_images` VALUES (112, 38, 'http://localhost:8090/images/ear5.png', 0);
INSERT INTO `product_images` VALUES (113, 38, 'http://localhost:8090/images/camera5.png', 1);
INSERT INTO `product_images` VALUES (114, 38, 'http://localhost:8090/images/ear4.png', 2);
INSERT INTO `product_images` VALUES (115, 39, 'http://localhost:8090/images/bike4.png', 0);
INSERT INTO `product_images` VALUES (116, 39, 'http://localhost:8090/images/bike3.png', 1);
INSERT INTO `product_images` VALUES (117, 39, 'http://localhost:8090/images/music3.png', 2);
INSERT INTO `product_images` VALUES (118, 40, 'http://localhost:8090/images/book2.png', 0);
INSERT INTO `product_images` VALUES (119, 40, 'http://localhost:8090/images/book4.png', 1);
INSERT INTO `product_images` VALUES (120, 40, 'http://localhost:8090/images/book5.png', 2);
INSERT INTO `product_images` VALUES (121, 41, 'http://localhost:8090/images/material1.png', 0);
INSERT INTO `product_images` VALUES (122, 41, 'http://localhost:8090/images/material5.png', 1);
INSERT INTO `product_images` VALUES (123, 41, 'http://localhost:8090/images/material2.png', 2);
INSERT INTO `product_images` VALUES (124, 42, 'http://localhost:8090/images/book9.png', 0);
INSERT INTO `product_images` VALUES (125, 42, 'http://localhost:8090/images/book1.png', 1);
INSERT INTO `product_images` VALUES (126, 42, 'http://localhost:8090/images/book3.png', 2);
INSERT INTO `product_images` VALUES (127, 43, 'http://localhost:8090/images/switch4.png', 0);
INSERT INTO `product_images` VALUES (128, 43, 'http://localhost:8090/images/phone3.png', 1);
INSERT INTO `product_images` VALUES (129, 43, 'http://localhost:8090/images/phone2.png', 2);
INSERT INTO `product_images` VALUES (130, 44, 'http://localhost:8090/images/computed3.png', 0);
INSERT INTO `product_images` VALUES (131, 44, 'http://localhost:8090/images/phone1.png', 1);
INSERT INTO `product_images` VALUES (132, 44, 'http://localhost:8090/images/camera2.png', 2);
INSERT INTO `product_images` VALUES (133, 45, 'http://localhost:8090/images/camera5.png', 0);
INSERT INTO `product_images` VALUES (134, 45, 'http://localhost:8090/images/phone3.png', 1);
INSERT INTO `product_images` VALUES (135, 45, 'http://localhost:8090/images/camera1.png', 2);
INSERT INTO `product_images` VALUES (136, 46, 'http://localhost:8090/images/live2.png', 0);
INSERT INTO `product_images` VALUES (137, 46, 'http://localhost:8090/images/live3.png', 1);
INSERT INTO `product_images` VALUES (138, 46, 'http://localhost:8090/images/live8.png', 2);
INSERT INTO `product_images` VALUES (139, 47, 'http://localhost:8090/images/bike5.png', 0);
INSERT INTO `product_images` VALUES (140, 47, 'http://localhost:8090/images/music3.png', 1);
INSERT INTO `product_images` VALUES (141, 47, 'http://localhost:8090/images/bike2.png', 2);
INSERT INTO `product_images` VALUES (142, 48, 'http://localhost:8090/images/phone2.png', 0);
INSERT INTO `product_images` VALUES (143, 48, 'http://localhost:8090/images/phone5.png', 1);
INSERT INTO `product_images` VALUES (144, 48, 'http://localhost:8090/images/camera2.png', 2);
INSERT INTO `product_images` VALUES (145, 49, 'http://localhost:8090/images/book2.png', 0);
INSERT INTO `product_images` VALUES (146, 49, 'http://localhost:8090/images/book7.png', 1);
INSERT INTO `product_images` VALUES (147, 49, 'http://localhost:8090/images/book9.png', 2);
INSERT INTO `product_images` VALUES (148, 50, 'http://localhost:8090/images/book7.png', 0);
INSERT INTO `product_images` VALUES (149, 50, 'http://localhost:8090/images/book1.png', 1);
INSERT INTO `product_images` VALUES (150, 50, 'http://localhost:8090/images/book8.png', 2);
INSERT INTO `product_images` VALUES (151, 51, 'http://localhost:8090/images/bike4.png', 0);
INSERT INTO `product_images` VALUES (152, 51, 'http://localhost:8090/images/bike2.png', 1);
INSERT INTO `product_images` VALUES (153, 51, 'http://localhost:8090/images/music2.png', 2);
INSERT INTO `product_images` VALUES (154, 52, 'http://localhost:8090/images/computed1.png', 0);
INSERT INTO `product_images` VALUES (155, 52, 'http://localhost:8090/images/computed3.png', 1);
INSERT INTO `product_images` VALUES (156, 52, 'http://localhost:8090/images/camera4.png', 2);
INSERT INTO `product_images` VALUES (157, 53, 'http://localhost:8090/images/live5.png', 0);
INSERT INTO `product_images` VALUES (158, 53, 'http://localhost:8090/images/live7.png', 1);
INSERT INTO `product_images` VALUES (159, 53, 'http://localhost:8090/images/live10.png', 2);
INSERT INTO `product_images` VALUES (160, 54, 'http://localhost:8090/images/material5.png', 0);
INSERT INTO `product_images` VALUES (161, 54, 'http://localhost:8090/images/material3.png', 1);
INSERT INTO `product_images` VALUES (162, 54, 'http://localhost:8090/images/material1.png', 2);
INSERT INTO `product_images` VALUES (163, 55, 'http://localhost:8090/images/material3.png', 0);
INSERT INTO `product_images` VALUES (164, 55, 'http://localhost:8090/images/material5.png', 1);
INSERT INTO `product_images` VALUES (165, 55, 'http://localhost:8090/images/material4.png', 2);
INSERT INTO `product_images` VALUES (166, 56, 'http://localhost:8090/images/book1.png', 0);
INSERT INTO `product_images` VALUES (167, 56, 'http://localhost:8090/images/book2.png', 1);
INSERT INTO `product_images` VALUES (168, 56, 'http://localhost:8090/images/book3.png', 2);
INSERT INTO `product_images` VALUES (169, 57, 'http://localhost:8090/images/phone3.png', 0);
INSERT INTO `product_images` VALUES (170, 57, 'http://localhost:8090/images/camera4.png', 1);
INSERT INTO `product_images` VALUES (171, 57, 'http://localhost:8090/images/computed3.png', 2);
INSERT INTO `product_images` VALUES (172, 58, 'http://localhost:8090/images/book7.png', 0);
INSERT INTO `product_images` VALUES (173, 58, 'http://localhost:8090/images/book9.png', 1);
INSERT INTO `product_images` VALUES (174, 58, 'http://localhost:8090/images/book10.png', 2);
INSERT INTO `product_images` VALUES (175, 59, 'http://localhost:8090/images/computed2.png', 0);
INSERT INTO `product_images` VALUES (176, 59, 'http://localhost:8090/images/computed3.png', 1);
INSERT INTO `product_images` VALUES (177, 59, 'http://localhost:8090/images/phone5.png', 2);
INSERT INTO `product_images` VALUES (178, 60, 'http://localhost:8090/images/camera1.png', 0);
INSERT INTO `product_images` VALUES (179, 60, 'http://localhost:8090/images/ear5.png', 1);
INSERT INTO `product_images` VALUES (180, 60, 'http://localhost:8090/images/computed2.png', 2);
INSERT INTO `product_images` VALUES (181, 61, 'http://localhost:8090/images/ear5.png', 0);
INSERT INTO `product_images` VALUES (182, 61, 'http://localhost:8090/images/phone2.png', 1);
INSERT INTO `product_images` VALUES (183, 61, 'http://localhost:8090/images/phone3.png', 2);
INSERT INTO `product_images` VALUES (184, 62, 'http://localhost:8090/images/book10.png', 0);
INSERT INTO `product_images` VALUES (185, 62, 'http://localhost:8090/images/book4.png', 1);
INSERT INTO `product_images` VALUES (186, 62, 'http://localhost:8090/images/book1.png', 2);
INSERT INTO `product_images` VALUES (187, 63, 'http://localhost:8090/images/phone4.png', 0);
INSERT INTO `product_images` VALUES (188, 63, 'http://localhost:8090/images/phone1.png', 1);
INSERT INTO `product_images` VALUES (189, 63, 'http://localhost:8090/images/phone2.png', 2);
INSERT INTO `product_images` VALUES (190, 64, 'http://localhost:8090/images/music5.png', 0);
INSERT INTO `product_images` VALUES (191, 64, 'http://localhost:8090/images/music1.png', 1);
INSERT INTO `product_images` VALUES (192, 64, 'http://localhost:8090/images/bike5.png', 2);
INSERT INTO `product_images` VALUES (193, 65, 'http://localhost:8090/images/book10.png', 0);
INSERT INTO `product_images` VALUES (194, 65, 'http://localhost:8090/images/book4.png', 1);
INSERT INTO `product_images` VALUES (195, 65, 'http://localhost:8090/images/book7.png', 2);
INSERT INTO `product_images` VALUES (196, 66, 'http://localhost:8090/images/book9.png', 0);
INSERT INTO `product_images` VALUES (197, 66, 'http://localhost:8090/images/book8.png', 1);
INSERT INTO `product_images` VALUES (198, 66, 'http://localhost:8090/images/book10.png', 2);
INSERT INTO `product_images` VALUES (199, 67, 'http://localhost:8090/images/phone2.png', 0);
INSERT INTO `product_images` VALUES (200, 67, 'http://localhost:8090/images/phone1.png', 1);
INSERT INTO `product_images` VALUES (201, 67, 'http://localhost:8090/images/computed4.png', 2);
INSERT INTO `product_images` VALUES (202, 68, 'http://localhost:8090/images/camera5.png', 0);
INSERT INTO `product_images` VALUES (203, 68, 'http://localhost:8090/images/switch1.png', 1);
INSERT INTO `product_images` VALUES (204, 68, 'http://localhost:8090/images/computed4.png', 2);
INSERT INTO `product_images` VALUES (205, 69, 'http://localhost:8090/images/material1.png', 0);
INSERT INTO `product_images` VALUES (206, 69, 'http://localhost:8090/images/material3.png', 1);
INSERT INTO `product_images` VALUES (207, 69, 'http://localhost:8090/images/material6.png', 2);
INSERT INTO `product_images` VALUES (208, 70, 'http://localhost:8090/images/book9.png', 0);
INSERT INTO `product_images` VALUES (209, 70, 'http://localhost:8090/images/book6.png', 1);
INSERT INTO `product_images` VALUES (210, 70, 'http://localhost:8090/images/book2.png', 2);
INSERT INTO `product_images` VALUES (211, 71, 'http://localhost:8090/images/book6.png', 0);
INSERT INTO `product_images` VALUES (212, 71, 'http://localhost:8090/images/book4.png', 1);
INSERT INTO `product_images` VALUES (213, 71, 'http://localhost:8090/images/book1.png', 2);
INSERT INTO `product_images` VALUES (214, 72, 'http://localhost:8090/images/book8.png', 0);
INSERT INTO `product_images` VALUES (215, 72, 'http://localhost:8090/images/book6.png', 1);
INSERT INTO `product_images` VALUES (216, 72, 'http://localhost:8090/images/book10.png', 2);
INSERT INTO `product_images` VALUES (217, 73, 'http://localhost:8090/images/live7.png', 0);
INSERT INTO `product_images` VALUES (218, 73, 'http://localhost:8090/images/live3.png', 1);
INSERT INTO `product_images` VALUES (219, 73, 'http://localhost:8090/images/live4.png', 2);
INSERT INTO `product_images` VALUES (220, 74, 'http://localhost:8090/images/live1.png', 0);
INSERT INTO `product_images` VALUES (221, 74, 'http://localhost:8090/images/live8.png', 1);
INSERT INTO `product_images` VALUES (222, 74, 'http://localhost:8090/images/live5.png', 2);
INSERT INTO `product_images` VALUES (223, 75, 'http://localhost:8090/images/live2.png', 0);
INSERT INTO `product_images` VALUES (224, 75, 'http://localhost:8090/images/live6.png', 1);
INSERT INTO `product_images` VALUES (225, 75, 'http://localhost:8090/images/live10.png', 2);
INSERT INTO `product_images` VALUES (226, 76, 'http://localhost:8090/images/computed3.png', 0);
INSERT INTO `product_images` VALUES (227, 76, 'http://localhost:8090/images/switch2.png', 1);
INSERT INTO `product_images` VALUES (228, 76, 'http://localhost:8090/images/camera4.png', 2);
INSERT INTO `product_images` VALUES (229, 77, 'http://localhost:8090/images/live7.png', 0);
INSERT INTO `product_images` VALUES (230, 77, 'http://localhost:8090/images/live5.png', 1);
INSERT INTO `product_images` VALUES (231, 77, 'http://localhost:8090/images/live6.png', 2);
INSERT INTO `product_images` VALUES (232, 78, 'http://localhost:8090/images/live10.png', 0);
INSERT INTO `product_images` VALUES (233, 78, 'http://localhost:8090/images/live7.png', 1);
INSERT INTO `product_images` VALUES (234, 78, 'http://localhost:8090/images/live2.png', 2);
INSERT INTO `product_images` VALUES (235, 79, 'http://localhost:8090/images/ear5.png', 0);
INSERT INTO `product_images` VALUES (236, 79, 'http://localhost:8090/images/camera3.png', 1);
INSERT INTO `product_images` VALUES (237, 79, 'http://localhost:8090/images/switch4.png', 2);
INSERT INTO `product_images` VALUES (238, 80, 'http://localhost:8090/images/book4.png', 0);
INSERT INTO `product_images` VALUES (239, 80, 'http://localhost:8090/images/book3.png', 1);
INSERT INTO `product_images` VALUES (240, 80, 'http://localhost:8090/images/book8.png', 2);
INSERT INTO `product_images` VALUES (241, 81, 'http://localhost:8090/images/book4.png', 0);
INSERT INTO `product_images` VALUES (242, 81, 'http://localhost:8090/images/book6.png', 1);
INSERT INTO `product_images` VALUES (243, 81, 'http://localhost:8090/images/book10.png', 2);
INSERT INTO `product_images` VALUES (244, 82, 'http://localhost:8090/images/music3.png', 0);
INSERT INTO `product_images` VALUES (245, 82, 'http://localhost:8090/images/music1.png', 1);
INSERT INTO `product_images` VALUES (246, 82, 'http://localhost:8090/images/bike5.png', 2);
INSERT INTO `product_images` VALUES (247, 83, 'http://localhost:8090/images/switch2.png', 0);
INSERT INTO `product_images` VALUES (248, 83, 'http://localhost:8090/images/camera5.png', 1);
INSERT INTO `product_images` VALUES (249, 83, 'http://localhost:8090/images/ear5.png', 2);
INSERT INTO `product_images` VALUES (250, 84, 'http://localhost:8090/images/live9.png', 0);
INSERT INTO `product_images` VALUES (251, 84, 'http://localhost:8090/images/live4.png', 1);
INSERT INTO `product_images` VALUES (252, 84, 'http://localhost:8090/images/live5.png', 2);
INSERT INTO `product_images` VALUES (253, 85, 'http://localhost:8090/images/live10.png', 0);
INSERT INTO `product_images` VALUES (254, 85, 'http://localhost:8090/images/live2.png', 1);
INSERT INTO `product_images` VALUES (255, 85, 'http://localhost:8090/images/live5.png', 2);
INSERT INTO `product_images` VALUES (256, 86, 'http://localhost:8090/images/ear4.png', 0);
INSERT INTO `product_images` VALUES (257, 86, 'http://localhost:8090/images/switch4.png', 1);
INSERT INTO `product_images` VALUES (258, 86, 'http://localhost:8090/images/phone5.png', 2);
INSERT INTO `product_images` VALUES (259, 87, 'http://localhost:8090/images/music5.png', 0);
INSERT INTO `product_images` VALUES (260, 87, 'http://localhost:8090/images/bike3.png', 1);
INSERT INTO `product_images` VALUES (261, 87, 'http://localhost:8090/images/music2.png', 2);
INSERT INTO `product_images` VALUES (262, 88, 'http://localhost:8090/images/live6.png', 0);
INSERT INTO `product_images` VALUES (263, 88, 'http://localhost:8090/images/live7.png', 1);
INSERT INTO `product_images` VALUES (264, 88, 'http://localhost:8090/images/live3.png', 2);
INSERT INTO `product_images` VALUES (265, 89, 'http://localhost:8090/images/computed2.png', 0);
INSERT INTO `product_images` VALUES (266, 89, 'http://localhost:8090/images/camera5.png', 1);
INSERT INTO `product_images` VALUES (267, 89, 'http://localhost:8090/images/switch3.png', 2);
INSERT INTO `product_images` VALUES (268, 90, 'http://localhost:8090/images/phone3.png', 0);
INSERT INTO `product_images` VALUES (269, 90, 'http://localhost:8090/images/switch5.png', 1);
INSERT INTO `product_images` VALUES (270, 90, 'http://localhost:8090/images/computed5.png', 2);
INSERT INTO `product_images` VALUES (271, 91, 'http://localhost:8090/images/book3.png', 0);
INSERT INTO `product_images` VALUES (272, 91, 'http://localhost:8090/images/book1.png', 1);
INSERT INTO `product_images` VALUES (273, 91, 'http://localhost:8090/images/book8.png', 2);
INSERT INTO `product_images` VALUES (274, 92, 'http://localhost:8090/images/phone2.png', 0);
INSERT INTO `product_images` VALUES (275, 92, 'http://localhost:8090/images/phone5.png', 1);
INSERT INTO `product_images` VALUES (276, 92, 'http://localhost:8090/images/phone1.png', 2);
INSERT INTO `product_images` VALUES (277, 93, 'http://localhost:8090/images/live5.png', 0);
INSERT INTO `product_images` VALUES (278, 93, 'http://localhost:8090/images/live3.png', 1);
INSERT INTO `product_images` VALUES (279, 93, 'http://localhost:8090/images/live10.png', 2);
INSERT INTO `product_images` VALUES (280, 94, 'http://localhost:8090/images/book6.png', 0);
INSERT INTO `product_images` VALUES (281, 94, 'http://localhost:8090/images/book1.png', 1);
INSERT INTO `product_images` VALUES (282, 94, 'http://localhost:8090/images/book9.png', 2);
INSERT INTO `product_images` VALUES (283, 95, 'http://localhost:8090/images/bike4.png', 0);
INSERT INTO `product_images` VALUES (284, 95, 'http://localhost:8090/images/music1.png', 1);
INSERT INTO `product_images` VALUES (285, 95, 'http://localhost:8090/images/bike2.png', 2);
INSERT INTO `product_images` VALUES (286, 96, 'http://localhost:8090/images/switch3.png', 0);
INSERT INTO `product_images` VALUES (287, 96, 'http://localhost:8090/images/computed2.png', 1);
INSERT INTO `product_images` VALUES (288, 96, 'http://localhost:8090/images/phone2.png', 2);
INSERT INTO `product_images` VALUES (289, 97, 'http://localhost:8090/images/phone3.png', 0);
INSERT INTO `product_images` VALUES (290, 97, 'http://localhost:8090/images/computed4.png', 1);
INSERT INTO `product_images` VALUES (291, 97, 'http://localhost:8090/images/camera1.png', 2);
INSERT INTO `product_images` VALUES (292, 98, 'http://localhost:8090/images/ear4.png', 0);
INSERT INTO `product_images` VALUES (293, 98, 'http://localhost:8090/images/computed3.png', 1);
INSERT INTO `product_images` VALUES (294, 98, 'http://localhost:8090/images/phone1.png', 2);
INSERT INTO `product_images` VALUES (295, 99, 'http://localhost:8090/images/book6.png', 0);
INSERT INTO `product_images` VALUES (296, 99, 'http://localhost:8090/images/book2.png', 1);
INSERT INTO `product_images` VALUES (297, 99, 'http://localhost:8090/images/book8.png', 2);
INSERT INTO `product_images` VALUES (298, 100, 'http://localhost:8090/images/book1.png', 0);
INSERT INTO `product_images` VALUES (299, 100, 'http://localhost:8090/images/book7.png', 1);
INSERT INTO `product_images` VALUES (300, 100, 'http://localhost:8090/images/book6.png', 2);
INSERT INTO `product_images` VALUES (327, 119, '/res/images/119_1776101802183_0.png', 0);

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `category_id` int NOT NULL COMMENT '分类ID',
  `seller_id` int NOT NULL COMMENT '卖家ID',
  `stock` tinyint NULL DEFAULT 1 COMMENT '库存（二手默认为1）',
  `status` tinyint NULL DEFAULT 1 COMMENT '1-在售, 2-已售出, 3-下架',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_bargainable` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持砍价：0-不支持，1-支持',
  `discount_rate` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '折扣率（0.00-1.00，如0.85表示85折）',
  `ai_generated_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的商品描述（历史版本）',
  `ai_desc_version` int NULL DEFAULT 0 COMMENT 'AI描述版本号，用于迭代更新',
  `exchange_type` tinyint NULL DEFAULT 0 COMMENT '0:出售 1:以物换物 2:求购',
  `exchange_want` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '想要交换的物品描述',
  `exchange_match_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT 'AI匹配度评分',
  `audit_status` tinyint NULL DEFAULT 0 COMMENT '0:待审核 1:已通过 2:疑似违规 3:已拒绝',
  `audit_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI审核结果/违规原因',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_cat`(`category_id`) USING BTREE,
  INDEX `idx_seller`(`seller_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `fk_prod_cat` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_prod_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '吉他 进阶级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 199.46, 4, 67, 4, 1, 447, '2025-12-28 04:44:37', '2026-04-07 08:28:57', 1, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (2, '数学考研 冲刺卷 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 47.65, 5, 90, 5, 1, 172, '2025-12-25 16:00:37', '2026-04-08 10:33:54', 1, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (3, '闲置台灯 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 10.68, 3, 90, 1, 1, 489, '2026-01-03 06:58:37', '2026-04-08 20:25:20', 0, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (4, 'Lightweight Paper Car 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 110.58, 2, 96, 2, 1, 177, '2025-12-17 04:16:37', '2026-04-08 18:51:33', 0, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (5, 'The World, the Flesh and the Devil Mohammed Kemmer 83成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 33.28, 1, 15, 4, 1, 382, '2026-01-05 00:35:37', '2026-04-14 11:47:54', 1, 0.82, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (6, '计算机考研 冲刺卷 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 21.82, 5, 5, 2, 1, 142, '2025-12-20 03:04:37', '2026-04-08 10:34:05', 1, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (7, 'Sleek Iron Chair 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 194.10, 2, 45, 5, 1, 352, '2025-12-28 20:43:37', '2026-01-12 19:45:46', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (8, 'Ring of Bright Water Kim Roob 88成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 27.74, 1, 71, 4, 1, 334, '2025-12-12 17:52:37', '2026-04-13 23:31:40', 0, 0.94, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (9, 'Incredible Cotton Plate 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 224.40, 2, 87, 1, 1, 210, '2025-12-31 13:07:37', '2026-01-12 18:09:59', 1, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (10, '闲置风扇 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 22.22, 3, 44, 3, 1, 474, '2026-01-05 12:52:37', '2026-04-13 23:34:19', 1, 0.92, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (11, 'Lightweight Aluminum Plate 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 321.12, 2, 39, 3, 1, 346, '2025-12-28 16:39:37', '2026-04-08 18:12:47', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (12, 'Lilies of the Field Rich Collins 75成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 28.40, 1, 84, 2, 1, 26, '2025-12-17 01:33:37', '2026-04-08 19:10:48', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (14, 'Now Sleeps the Crimson Petal Carylon Auer MD 84成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 76.19, 1, 59, 4, 1, 282, '2025-12-28 11:16:37', '2026-01-12 18:10:18', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (15, 'Practical Copper Chair 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 233.54, 2, 38, 1, 1, 43, '2025-12-09 16:22:37', '2026-01-05 22:01:37', 0, 0.93, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (16, '闲置台灯 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 30.88, 3, 22, 1, 1, 144, '2025-12-28 22:18:37', '2026-01-12 18:10:10', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (17, '篮球 入门级 适合初学者', '【运动乐器】保养得当，附赠配件。兴趣转移 可小刀，价格面议。', 99.13, 4, 83, 5, 1, 387, '2025-12-17 01:56:37', '2026-04-08 18:13:47', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (18, 'Rustic Marble Plate 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 61.39, 2, 31, 4, 2, 341, '2025-12-09 22:26:37', '2026-01-05 22:01:37', 0, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (19, 'Lightweight Rubber Bench 键盘 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 107.12, 2, 85, 5, 2, 84, '2025-12-11 06:46:37', '2026-01-05 22:01:37', 0, 0.80, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (20, '吉他 入门级 适合初学者', '【运动乐器】保养得当，附赠配件。兴趣转移 可小刀，价格面议。', 96.75, 4, 88, 4, 1, 457, '2025-12-25 05:14:37', '2026-01-05 22:01:37', 0, 0.83, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (21, 'Enormous Linen Wallet 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 188.27, 2, 20, 4, 1, 252, '2025-12-11 00:53:37', '2026-01-05 22:01:37', 0, 0.80, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (22, '闲置衣架 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 25.92, 3, 57, 4, 2, 420, '2025-12-16 17:06:37', '2026-01-05 22:01:37', 0, 0.88, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (23, '羽毛球拍 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 160.52, 4, 61, 1, 1, 225, '2025-12-11 06:44:37', '2026-01-05 22:01:37', 1, 0.93, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (24, 'Synergistic Copper Table 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 187.39, 2, 47, 4, 1, 339, '2026-01-01 16:34:37', '2026-01-05 22:01:37', 1, 0.83, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (25, 'Sleek Paper Watch 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 99.38, 2, 6, 2, 1, 28, '2025-12-16 15:25:37', '2026-01-05 22:01:37', 0, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (26, 'When the Green Woods Laugh Ms. Herman Walker 99成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 61.71, 1, 98, 3, 1, 188, '2025-12-14 03:37:37', '2026-01-05 22:01:37', 0, 0.92, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (27, '闲置收纳盒 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 10.30, 3, 65, 5, 1, 425, '2025-12-24 12:06:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (28, '闲置台灯 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 48.74, 3, 36, 2, 1, 443, '2025-12-29 14:13:37', '2026-04-08 19:23:50', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (29, 'Mediocre Leather Knife 键盘 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 72.15, 2, 9, 2, 2, 407, '2026-01-02 23:11:37', '2026-04-08 19:46:25', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (30, 'Ergonomic Copper Gloves 键盘 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 349.49, 2, 46, 1, 1, 96, '2025-12-15 05:33:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (31, '羽毛球拍 进阶级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 213.70, 4, 5, 5, 1, 53, '2025-12-21 09:14:37', '2026-01-05 22:01:37', 1, 0.85, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (32, 'Mediocre Rubber Coat 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 200.84, 2, 48, 1, 1, 200, '2025-12-22 18:36:37', '2026-01-05 22:01:37', 1, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (33, 'Mediocre Concrete Table 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 134.62, 2, 69, 3, 2, 489, '2025-12-07 03:26:37', '2026-01-05 22:01:37', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (34, 'Stranger in a Strange Land Shane Fahey 81成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 81.72, 1, 41, 3, 1, 337, '2025-12-24 09:44:37', '2026-01-05 22:01:37', 1, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (35, '政治考研 冲刺卷 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 56.20, 5, 5, 3, 2, 462, '2025-12-08 14:13:37', '2026-01-05 22:01:37', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (36, 'The Waste Land Serina Bergstrom 97成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 89.69, 1, 58, 4, 2, 357, '2026-01-05 19:46:37', '2026-04-13 23:34:15', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (37, '闲置收纳盒 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 45.31, 3, 72, 3, 2, 118, '2025-12-11 16:04:37', '2026-01-05 22:01:37', 0, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (38, 'Rustic Rubber Bench 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 315.10, 2, 46, 3, 1, 137, '2025-12-17 03:09:37', '2026-01-05 22:01:37', 1, 0.80, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (39, '吉他 进阶级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 118.98, 4, 14, 1, 1, 245, '2026-01-04 23:26:37', '2026-04-08 21:05:40', 1, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (40, 'Paths of Glory Rose Ebert 99成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 99.94, 1, 56, 5, 1, 95, '2026-01-03 17:56:37', '2026-04-08 21:05:35', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (41, '计算机考研 冲刺卷 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 15.40, 5, 7, 5, 1, 497, '2025-12-17 00:04:37', '2026-04-08 10:34:09', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (42, 'Warren Fahey 78成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 98.50, 1, 2, 2, 1, 96, '2026-01-04 23:28:37', '2026-04-08 21:05:30', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (43, 'Enormous Silk Bottle 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 96.90, 2, 30, 2, 1, 504, '2025-12-30 23:22:37', '2026-01-05 22:01:37', 1, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (44, 'Synergistic Marble Pants 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 59.15, 2, 41, 4, 1, 97, '2025-12-26 06:07:37', '2026-01-05 22:01:37', 1, 0.88, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (45, 'Heavy Duty Rubber Watch 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 93.38, 2, 93, 1, 1, 185, '2025-12-23 02:03:37', '2026-01-05 22:01:37', 1, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (46, '闲置收纳盒 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 32.60, 3, 38, 5, 1, 25, '2025-12-23 23:54:37', '2026-01-05 22:01:37', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (47, '吉他 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 43.67, 4, 37, 2, 2, 477, '2025-12-14 19:33:37', '2026-01-12 19:19:35', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (48, 'Practical Plastic Bottle 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 121.94, 2, 43, 1, 2, 31, '2025-12-31 17:50:37', '2026-01-05 22:01:37', 0, 0.94, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (49, 'Jacob Have I Loved Trina Wolf 89成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 66.81, 1, 80, 4, 2, 363, '2025-12-20 13:57:37', '2026-01-05 22:01:37', 0, 0.95, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (50, 'A Confederacy of Dunces Latoria Crooks 77成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 73.30, 1, 37, 5, 1, 451, '2025-12-27 07:00:37', '2026-01-05 22:01:37', 0, 0.90, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (51, '尤克里里 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 172.24, 4, 53, 2, 2, 366, '2025-12-28 17:44:37', '2026-01-05 22:01:37', 1, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (52, 'Small Concrete Hat 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 349.22, 2, 79, 3, 2, 295, '2025-12-21 15:55:37', '2026-01-05 22:01:37', 0, 0.83, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (53, '闲置收纳盒 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 33.21, 3, 37, 5, 1, 57, '2025-12-08 23:47:37', '2026-01-05 22:01:37', 1, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (54, '数学考研 复习全书 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 21.64, 5, 55, 2, 1, 102, '2025-12-16 02:41:37', '2026-04-08 10:33:57', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (55, '数学考研 复习全书 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 62.61, 5, 47, 1, 2, 455, '2025-12-23 11:13:37', '2026-04-08 10:51:43', 1, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (56, 'Recalled to Life Kitty Langworth 94成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 35.90, 1, 15, 5, 1, 69, '2025-12-30 01:00:37', '2026-04-08 10:14:48', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (57, 'Incredible Granite Computer 键盘 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 71.14, 2, 48, 1, 1, 477, '2025-12-18 19:56:37', '2026-01-05 22:01:37', 0, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (58, 'The Lathe of Heaven Hassan Schaden 96成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 55.39, 1, 82, 5, 1, 137, '2025-12-12 04:36:37', '2026-01-05 22:01:37', 0, 0.82, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (59, 'Ergonomic Copper Pants 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 269.27, 2, 35, 2, 2, 342, '2025-12-21 05:09:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (60, 'Enormous Wool Plate 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 285.11, 2, 96, 4, 2, 310, '2025-12-23 09:46:37', '2026-04-08 17:45:44', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (61, 'Incredible Concrete Bench 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 90.84, 2, 11, 3, 2, 392, '2026-01-05 11:57:37', '2026-04-13 23:34:23', 0, 0.93, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (62, 'A Confederacy of Dunces Miss Hiram Bernier 95成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 39.85, 1, 64, 5, 1, 76, '2025-12-11 21:22:37', '2026-01-05 22:01:37', 0, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (63, 'Durable Aluminum Car 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 316.16, 2, 34, 5, 1, 464, '2025-12-11 07:18:37', '2026-01-05 22:01:37', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (64, '羽毛球拍 进阶级 适合初学者', '【运动乐器】保养得当，附赠配件。兴趣转移 可小刀，价格面议。', 139.14, 4, 7, 3, 2, 281, '2025-12-30 23:22:37', '2026-01-05 22:01:37', 1, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (65, 'Nine Coaches Waiting Liana Conroy 77成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 87.23, 1, 12, 4, 1, 382, '2026-01-04 00:29:37', '2026-04-08 20:25:00', 1, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (66, 'The Skull Beneath the Skin Luis Rogahn 86成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 57.26, 1, 60, 4, 1, 479, '2025-12-24 03:44:37', '2026-01-05 22:01:37', 1, 0.94, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (67, 'Small Copper Knife 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 117.72, 2, 10, 3, 1, 458, '2025-12-28 11:42:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (68, 'Aerodynamic Wool Clock 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 229.84, 2, 96, 4, 2, 220, '2025-12-12 14:44:37', '2026-04-08 18:41:43', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (69, '数学考研 历年真题 真题+笔记', '【考研资料】上岸学长自用，包含重点标注和心得。成功上岸 可小刀，赠送电子版。', 43.53, 5, 69, 2, 2, 372, '2025-12-15 01:13:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (70, 'A Many-Splendoured Thing Ms. Aracelis Jacobs 83成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 49.71, 1, 9, 1, 2, 470, '2025-12-30 22:50:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (71, 'A Monstrous Regiment of Women Deirdre Trantow 91成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 87.62, 1, 7, 2, 2, 452, '2025-12-13 09:45:37', '2026-01-05 22:01:37', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (72, 'The Grapes of Wrath Dr. Efrain Cummerata 74成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 99.67, 1, 53, 2, 1, 497, '2025-12-15 11:07:37', '2026-01-05 22:01:37', 0, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (73, '闲置台灯 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 37.01, 3, 78, 2, 1, 287, '2026-01-03 22:25:37', '2026-04-08 21:05:51', 0, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (74, '闲置收纳盒 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 30.27, 3, 14, 5, 1, 20, '2025-12-23 19:56:37', '2026-04-08 17:43:53', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (75, '闲置收纳盒 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 48.44, 3, 83, 5, 1, 89, '2025-12-09 21:05:37', '2026-01-05 22:01:37', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (76, 'Rustic Linen Watch 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 114.74, 2, 31, 1, 1, 208, '2025-12-19 10:46:37', '2026-01-05 22:01:37', 1, 0.92, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (77, '闲置衣架 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 12.83, 3, 70, 1, 1, 231, '2026-01-02 19:10:37', '2026-01-05 22:01:37', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (78, '闲置台灯 9成新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 22.22, 3, 46, 3, 1, 20, '2025-12-15 08:56:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (79, 'Durable Iron Table 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 216.91, 2, 13, 2, 1, 87, '2025-12-30 18:52:37', '2026-01-05 22:01:37', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (80, 'Jesting Pilate Geoffrey Bosco 75成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 54.73, 1, 82, 4, 2, 66, '2026-01-01 23:50:37', '2026-01-05 22:01:37', 1, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (81, 'The Moon by Night Ms. Cecelia Hartmann 98成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 35.17, 1, 77, 3, 1, 81, '2026-01-03 22:43:37', '2026-04-08 15:50:57', 0, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (82, '羽毛球拍 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 83.00, 4, 91, 3, 2, 239, '2025-12-30 20:24:37', '2026-01-13 00:22:27', 0, 1.00, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (83, 'Small Concrete Table 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 162.96, 2, 30, 5, 1, 334, '2025-12-11 11:32:37', '2026-01-05 22:01:37', 1, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (84, '闲置风扇 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 28.77, 3, 92, 4, 1, 480, '2026-01-05 21:38:37', '2026-04-14 11:58:24', 0, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (85, '闲置风扇 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 30.00, 3, 49, 3, 1, 439, '2025-12-15 20:17:37', '2026-01-05 22:01:37', 1, 0.93, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (86, 'Heavy Duty Granite Coat 耳机 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 193.12, 2, 13, 2, 1, 184, '2025-12-20 02:55:37', '2026-01-05 22:01:37', 1, 0.81, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (87, '羽毛球拍 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 127.02, 4, 39, 5, 1, 167, '2025-12-25 10:16:37', '2026-01-05 22:01:37', 1, 0.91, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (88, '闲置台灯 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 44.64, 3, 88, 5, 1, 303, '2026-01-03 15:41:37', '2026-04-08 19:10:57', 1, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (89, 'Aerodynamic Silk Bag 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 93.01, 2, 81, 2, 1, 236, '2026-01-05 20:27:37', '2026-04-13 23:34:12', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, '审核失败');
INSERT INTO `products` VALUES (90, 'Incredible Bronze Knife 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 81.61, 2, 31, 4, 1, 163, '2026-01-04 03:43:37', '2026-04-08 18:51:38', 0, 0.84, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (91, 'The Heart Is Deceitful Above All Things Mr. Lance Jenkins 83成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 84.65, 1, 28, 4, 2, 429, '2025-12-12 08:09:37', '2026-01-05 22:01:37', 1, 0.88, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (92, 'Rustic Rubber Bag 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 128.07, 2, 18, 20, 2, 279, '2025-12-08 07:16:37', '2026-04-13 23:32:18', 0, 0.82, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (93, '闲置台灯 几乎全新', '【生活用品】购买后使用次数少，保存完好。搬家急出 可小刀，可小刀。', 35.19, 3, 70, 20, 1, 468, '2025-12-20 13:27:37', '2026-04-13 23:32:22', 1, 0.88, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (94, 'Stranger in a Strange Land Maynard Watsica 71成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 66.86, 1, 59, 20, 2, 390, '2025-12-22 16:11:37', '2026-04-13 23:32:25', 0, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (95, '吉他 入门级 适合初学者', '【运动乐器】保养得当，有入门教程。兴趣转移 可小刀，价格面议。', 37.77, 4, 27, 20, 1, 191, '2025-12-28 04:43:37', '2026-04-13 23:32:27', 1, 0.88, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (96, 'Rustic Granite Car 鼠标 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 289.30, 2, 54, 20, 2, 118, '2025-12-23 00:10:37', '2026-04-13 23:32:31', 0, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (97, 'Lightweight Steel Shoes 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 265.55, 2, 72, 20, 1, 113, '2025-12-28 21:15:37', '2026-04-13 23:32:34', 1, 0.89, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (98, 'Heavy Duty Copper Hat 充电宝 功能完好', '【电子产品】功能一切正常，无损坏，配件齐全。寝室闲置 可小刀，支持验机。', 349.24, 2, 51, 20, 1, 493, '2025-12-30 07:04:37', '2026-04-13 23:32:36', 0, 0.83, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (99, 'His Dark Materials Stewart Reilly 86成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，包邮。', 94.34, 1, 61, 20, 1, 391, '2025-12-23 20:21:37', '2026-04-13 23:32:39', 0, 0.86, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (100, 'A Many-Splendoured Thing Dorian Fisher PhD 85成新', '【教材资料】正版教材，划重点清晰，有少量笔记但不影响阅读。毕业清仓 可小刀，自提。', 40.44, 1, 13, 20, 1, 24, '2025-12-30 12:04:37', '2026-04-13 23:32:42', 1, 0.87, NULL, 0, 0, NULL, 0.00, 0, NULL);
INSERT INTO `products` VALUES (119, 'java程序设计二手书 9成新笔记少 24届学姐用书', '9成新Java程序设计二手书，24届学姐自用哒~笔记超少很整洁，书页几乎无磨损。教材知识点全，备考、课设都能用，学生党学Java超省钱，性价比巨高！适合', 20.00, 1, 22, 1, 1, 6, '2026-04-14 01:36:42', '2026-04-14 11:49:24', 0, 0.85, NULL, 0, 0, NULL, 0.00, 0, '审核失败：返回结果为空');
INSERT INTO `products` VALUES (120, '电脑', NULL, 0.00, 1, 2, 1, 0, 0, '2026-04-14 09:16:57', '2026-04-14 09:16:57', 1, 1.00, NULL, 0, 1, '手机', 0.00, 0, NULL);
INSERT INTO `products` VALUES (121, '手机', NULL, 0.00, 1, 2, 1, 0, 0, '2026-04-14 09:17:14', '2026-04-14 09:17:14', 1, 1.00, NULL, 0, 1, '平板', 0.00, 0, NULL);
INSERT INTO `products` VALUES (127, 'html设计二手书', NULL, 0.00, 1, 2, 1, 0, 1, '2026-04-14 09:18:11', '2026-04-14 11:56:24', 1, 1.00, NULL, 0, 1, 'java程序', 0.00, 0, NULL);
INSERT INTO `products` VALUES (128, '玩偶', NULL, 0.00, 1, 44, 1, 0, 0, '2026-04-14 09:27:32', '2026-04-14 09:29:08', 1, 1.00, NULL, 0, 1, 'html设计', 0.00, 0, NULL);
INSERT INTO `products` VALUES (129, 'java程序设计', NULL, 0.00, 1, 44, 1, 0, 0, '2026-04-14 09:40:26', '2026-04-14 09:40:26', 1, 1.00, NULL, 0, 1, 'html设计', 0.00, 0, '审核失败：返回结果为空');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像URL',
  `campus` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '主校区' COMMENT '所在校区（便于筛选）',
  `role` tinyint NULL DEFAULT 0 COMMENT '0-学生, 1-管理员',
  `status` tinyint NULL DEFAULT 1 COMMENT '1-正常, 0-禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `student_no`(`student_no`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '', 'admin', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', '系统管理员', '', '', '主校区', 1, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (2, '202400002', 'user2', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', '西区小李', '002.424.9790', '/res/images/img_1775531963865.jpg', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (3, '202400003', 'user3', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Leeann53号', '(905) 737-9964', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (4, '202400004', 'user4', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Leopoldo12号', '856-054-4517', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (5, '202400005', 'user5', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Thomas77号', '344.739.9897', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (6, '202400006', 'user6', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Josh23号', '(220) 944-6749', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (7, '202400007', 'user7', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Virgina57号', '987.048.6311', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (8, '202400008', 'user8', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Tereasa60号', '478-041-9829', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (9, '202400009', 'user9', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Justin99号', '074.283.2958', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (10, '202400010', 'user10', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Anderson67号', '675.705.1849', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (11, '202400011', 'user11', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Alonso57号', '919-462-2547', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (12, '202400012', 'user12', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Vina8号', '1-327-312-6312', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (13, '202400013', 'user13', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Clifton71号', '897.026.3122', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (14, '202400014', 'user14', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jim59号', '(976) 420-5616', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (15, '202400015', 'user15', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Kieth96号', '657.586.5354', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (16, '202400016', 'user16', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Melva6号', '1-445-520-5390', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (17, '202400017', 'user17', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Alia98号', '(858) 090-2384', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (18, '202400018', 'user18', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Austin24号', '021.676.2343', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (19, '202400019', 'user19', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Dionna80号', '855.505.3617', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (20, '202400020', 'user20', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Miles96号', '(506) 449-4163', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (21, '202400021', 'user21', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Brandon89号', '530.244.5761', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (22, '202400022', 'user22', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Michel24号', '(934) 915-9034', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (23, '202400023', 'user23', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jeffry51号', '240-832-6453', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (24, '202400024', 'user24', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Timmy60号', '(380) 899-6214', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (25, '202400025', 'user25', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Harmony68号', '1-967-509-4587', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (26, '202400026', 'user26', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Lisabeth42号', '399-416-1381', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (27, '202400027', 'user27', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Margy14号', '395-063-3513', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (28, '202400028', 'user28', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Asa76号', '997-921-0098', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (29, '202400029', 'user29', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Tonette74号', '155.859.4791', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (30, '202400030', 'user30', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Dewey5号', '(743) 728-1418', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (31, '202400031', 'user31', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Chantal81号', '103-058-0936', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (32, '202400032', 'user32', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Aubrey22号', '143-780-0657', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (33, '202400033', 'user33', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Marisha1号', '(467) 222-1487', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (34, '202400034', 'user34', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jessenia61号', '(968) 912-3075', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (35, '202400035', 'user35', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Julio40号', '1-861-843-7259', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (36, '202400036', 'user36', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Fatimah96号', '(389) 323-5340', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (37, '202400037', 'user37', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Rufus40号', '547-203-9895', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (38, '202400038', 'user38', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Johnnie49号', '347-925-0253', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (39, '202400039', 'user39', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Leeanne48号', '1-452-396-0403', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (40, '202400040', 'user40', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Romona85号', '703.218.6705', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (41, '202400041', 'user41', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Debora72号', '235-150-2454', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (42, '202400042', 'user42', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jeramy7号', '1-193-596-0241', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (43, '202400043', 'user43', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Lorrine15号', '079.173.9171', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (44, '202400044', 'user44', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Chan10号', '(911) 616-1503', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (45, '202400045', 'user45', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Herb11号', '(954) 988-9996', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (46, '202400046', 'user46', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Hye37号', '215.769.1591', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (47, '202400047', 'user47', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Glennie47号', '1-894-277-8299', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (48, '202400048', 'user48', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Coleman23号', '(045) 523-4659', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (49, '202400049', 'user49', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Casey59号', '1-895-011-1329', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (50, '202400050', 'user50', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Ana91号', '899.976.0845', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (51, '202400051', 'user51', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Elvina60号', '1-036-081-5030', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (52, '202400052', 'user52', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Malcolm66号', '004.594.2562', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (53, '202400053', 'user53', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Darius13号', '1-261-622-7073', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (54, '202400054', 'user54', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Charlena8号', '930.849.9912', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (55, '202400055', 'user55', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jason90号', '612-460-0939', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (56, '202400056', 'user56', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Bonny90号', '372-204-9365', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (57, '202400057', 'user57', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Adrienne98号', '1-037-462-5033', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (58, '202400058', 'user58', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Leena76号', '048-598-6499', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (59, '202400059', 'user59', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Adrian75号', '125-628-5144', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (60, '202400060', 'user60', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Alise41号', '1-825-854-2998', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (61, '202400061', 'user61', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Avery14号', '882-868-7550', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (62, '202400062', 'user62', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Arlen63号', '(667) 460-2632', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (63, '202400063', 'user63', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Merle34号', '(205) 221-5774', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (64, '202400064', 'user64', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Raven75号', '674-521-8984', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (65, '202400065', 'user65', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Roberto52号', '(335) 995-7355', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (66, '202400066', 'user66', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Clare82号', '1-417-268-6304', '/res/images/img_1775615538787.png', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (67, '202400067', 'user67', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Karlene20号', '(904) 878-4796', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (68, '202400068', 'user68', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Samual93号', '(933) 684-9792', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (69, '202400069', 'user69', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Willie84号', '1-364-546-6561', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (70, '202400070', 'user70', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Rayna66号', '546-708-2977', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (71, '202400071', 'user71', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Victor44号', '020-590-2475', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (72, '202400072', 'user72', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Lyman12号', '382.308.9039', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (73, '202400073', 'user73', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Darrin28号', '(446) 508-5889', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (74, '202400074', 'user74', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Jeramy5号', '660.290.7353', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (75, '202400075', 'user75', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Milton72号', '713-589-1251', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (76, '202400076', 'user76', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Heriberto1号', '525.643.4446', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (77, '202400077', 'user77', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Rex21号', '(999) 774-3504', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (78, '202400078', 'user78', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Claude86号', '(874) 281-6318', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (79, '202400079', 'user79', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Maida98号', '(016) 826-4149', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (80, '202400080', 'user80', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Charmaine61号', '460-194-8633', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (81, '202400081', 'user81', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Adriane82号', '652.320.5461', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (82, '202400082', 'user82', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Brice87号', '(511) 383-0503', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (83, '202400083', 'user83', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Stefan59号', '421-504-8627', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (84, '202400084', 'user84', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Teddy24号', '079-856-2259', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (85, '202400085', 'user85', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Louis45号', '(299) 442-6626', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (86, '202400086', 'user86', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Minda99号', '545-119-9838', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (87, '202400087', 'user87', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Gavin74号', '885-031-9164', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (88, '202400088', 'user88', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Brooks8号', '210-340-7691', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (89, '202400089', 'user89', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Lance73号', '921-719-2718', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (90, '202400090', 'user90', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Margit7号', '1-142-430-7461', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (91, '202400091', 'user91', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Deena69号', '116-151-3179', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (92, '202400092', 'user92', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Justine44号', '805.223.5817', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (93, '202400093', 'user93', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Noreen11号', '(963) 272-9082', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (94, '202400094', 'user94', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Rene79号', '721.928.6760', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (95, '202400095', 'user95', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Erick42号', '411-006-2143', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (96, '202400096', 'user96', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Kay84号', '610.797.9995', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (97, '202400097', 'user97', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Mui39号', '461.248.8174', '', '西区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (98, '202400098', 'user98', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Chun10号', '750.624.0746', '', '东区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (99, '202400099', 'user99', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Rosario25号', '(315) 128-2878', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (100, '202400100', 'user100', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', 'Kara52号', '343.652.0862', '', '南区', 0, 1, '2026-01-05 22:01:36');
INSERT INTO `users` VALUES (101, NULL, 'admin2', '$2a$10$jFPhptSCkIZpp47HsWJIZuclK.V0nkXqRkFePSDAVM/QyxkXpKHge', '系统管理员', NULL, '', '主校区', 1, 1, '2026-01-03 13:14:10');
INSERT INTO `users` VALUES (102, '2401140000', 'test2024001', '$2a$12$LS1/YquIzTHOCldIeKtMxObstFe3MOH5Aa3MNISlp4o8V/4r6U4Xi', '测试用户01', '13800138000', '000', '主校区', 0, 1, '2026-01-07 14:08:16');
INSERT INTO `users` VALUES (103, '2401140000999', 'test2024001999', '$2a$10$bZ1SdP/ZUj1RFhiqCGyp9OJf2Di6E7iroVFn3OWuBZFZlDsYRkpNe', '测试用户01', '1380013800000', '000', '主校区', 0, 1, '2026-01-08 15:50:05');
INSERT INTO `users` VALUES (105, '32943290', 'test01', '$2a$10$HMBT0OgcSQu0GD2kjM/rp.QXKQ/FYY7AcxZe0TH1PoAMaXO8viySK', '测试用户001', '13800138000002', '/res/images/img_1768098691566.png', '东校区', 0, 1, '2026-01-11 10:31:31');
INSERT INTO `users` VALUES (106, NULL, 'admintest', '$2a$10$QLMP3zowjBG8HAGCjYzjSOQjaF/5wMZz5RexlAbGIr6vPZ2BwVIE6', '管理员账号', NULL, '', '主校区', 1, 1, '2026-01-11 14:55:48');
INSERT INTO `users` VALUES (107, NULL, 'admintest02', '$2a$10$boZkFAl66EPa1FoOj1Qyn.2Nav6JfTfVeD4p2D2/vPnt1G2mV6Xra', '管理员账号', NULL, '', '主校区', 1, 1, '2026-01-11 14:59:45');
INSERT INTO `users` VALUES (108, '2401140114', 'testuser001', '$2a$10$bFWXCH4YlFIbsDaATDNsYOxdYi0gI0AXfCEVQ.OT.KARacAl7N6ci', '张三', '13421357128', '/res/images/img_1768205658232.png', '西区', 0, 1, '2026-01-12 16:14:18');
INSERT INTO `users` VALUES (109, '2401140147', 'tetwtewtewye', '$2a$10$n5ViNNYi8H2FcZ8svQo0Puz9fPygewOQHruH6uW.spGmSfVRAKkO.', '山东科技阿是大数据', '13456789011', '/res/images/img_1768231762173.png', '北校区', 0, 1, '2026-01-12 23:29:22');
INSERT INTO `users` VALUES (110, '2401140117', '22wdswfdffe', '$2a$10$V/SZmwhCgAt3PPcSHpRCD.RIWlLiSOJ.8fZHQROrJzS/zoig/SciW', '学生完成新说唱导师', '13498729881', '/res/images/img_1768232407506.png', '北校区', 0, 1, '2026-01-12 23:40:08');
INSERT INTO `users` VALUES (112, '2401140137', '234dffddfd', '$2a$10$As8At0ET9OHlS/ND/jnYWOhXYy9W0tg2eMDHQLD.yGo9ODNJOl8iG', 'd地方大打出手', '13456245645', '/res/images/img_1768232713409.png', '主校区', 0, 1, '2026-01-12 23:45:13');
INSERT INTO `users` VALUES (116, '2401140126', '677sggggvgg', '$2a$10$Wg3gAK71yz9/huSBil4KYO.pQScvmDHMx1Y5LzMtgnr0NEvYQWov.', 'dfecvcds', '14325645678', '/res/images/img_1768237768442.png', '主校区', 0, 1, '2026-01-13 01:09:28');
INSERT INTO `users` VALUES (118, '329432905', 'tes7666546', '$2a$10$zYAYrUggcsDOEi331LivuuPtXEt9D8y.khOCgKHEaagNnPY9V0Q0O', '测试用户006', '9086538000', '/res/images/img_1768278374203.png', '东校区', 0, 1, '2026-01-13 12:26:14');
INSERT INTO `users` VALUES (119, '3294329057', 'tes766654699', '$2a$10$60pDkNatCYHMHVqwdlwJyOnjH6JWC7iv3R0/vfzSffN/uVShnNRSO', '测试用户006', '908653800088', '/res/images/img_1768278981639.png', '东校区', 0, 1, '2026-01-13 12:36:21');
INSERT INTO `users` VALUES (120, '20241112', 'lanhua121212', '$2a$10$X.yxbCbGBlmB2b4QUOpCMOCi4hbNs51jpBkDxs/0QlqrN5Kovsp5u', 'lanhua', '13425678911', '/res/images/img_1775532600228.jpg', '主校区', 0, 1, '2026-04-07 11:30:00');

SET FOREIGN_KEY_CHECKS = 1;
