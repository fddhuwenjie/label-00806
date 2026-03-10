-- 微信外卖平台数据库建表脚本
-- Database: food_delivery

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 管理员表
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `role` TINYINT DEFAULT 1 COMMENT '角色: 1-普通管理员 2-超级管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员 密码: admin123 (BCrypt加密)
INSERT INTO `admin` (`username`, `password`, `real_name`, `role`, `status`) 
VALUES ('admin', '$2a$10$fp5daTKGXqqJ7d7n83yMeevDzf3NMM0vmPgAN284eyMgZ2jWtjbrK', '超级管理员', 2, 1);

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `openid` VARCHAR(100) NOT NULL COMMENT '微信openid',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 商家表
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商家名称',
    `logo` LONGTEXT DEFAULT NULL COMMENT '商家Logo',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '商家地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `min_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '起送价',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-休息 1-营业',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `monthly_sales` INT DEFAULT 0 COMMENT '月销量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- ----------------------------
-- 分类表
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ----------------------------
-- 菜品表
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
    `image` LONGTEXT DEFAULT NULL COMMENT '菜品图片',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '菜品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT DEFAULT 999 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ----------------------------
-- 地址表
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '市',
    `district` VARCHAR(50) DEFAULT NULL COMMENT '区',
    `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';

-- ----------------------------
-- 购物车表
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `address_id` BIGINT DEFAULT NULL COMMENT '地址ID',
    `address_snapshot` TEXT COMMENT '地址快照JSON',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待支付 1-待接单 2-配送中 3-已完成 4-已取消',
    `user_deleted` TINYINT DEFAULT 0 COMMENT '用户侧删除: 0-未删除 1-已删除',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `deliver_time` DATETIME DEFAULT NULL COMMENT '配送时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `dish_name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
    `dish_image` LONGTEXT DEFAULT NULL COMMENT '菜品图片',
    `dish_price` DECIMAL(10,2) NOT NULL COMMENT '菜品单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `admin_id` BIGINT DEFAULT NULL COMMENT '管理员ID',
    `admin_name` VARCHAR(50) DEFAULT NULL COMMENT '管理员名称',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

SET FOREIGN_KEY_CHECKS = 1;
