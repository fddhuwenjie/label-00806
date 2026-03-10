-- 示例数据
-- 注意: 此文件在 schema.sql 之后执行

SET NAMES utf8mb4;

-- ----------------------------
-- 商家示例数据
-- ----------------------------
INSERT IGNORE INTO `merchant` (`id`, `name`, `logo`, `address`, `phone`, `min_price`, `delivery_fee`, `status`, `rating`, `monthly_sales`) VALUES
(1, '麦当劳(中关村店)', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200&h=200&fit=crop', '北京市海淀区中关村大街1号', '010-12345678', 20.00, 5.00, 1, 4.8, 1520),
(2, '肯德基(五道口店)', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=200&h=200&fit=crop', '北京市海淀区五道口地铁站B口', '010-87654321', 25.00, 4.00, 1, 4.7, 2100),
(3, '海底捞火锅(望京店)', 'https://images.unsplash.com/photo-1666278172017-ad93e14c329d?w=200&h=200&fit=crop', '北京市朝阳区望京SOHO', '010-11112222', 100.00, 0.00, 1, 4.9, 890),
(4, '星巴克(国贸店)', 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=200&h=200&fit=crop', '北京市朝阳区国贸商城B1层', '010-33334444', 30.00, 6.00, 1, 4.6, 3200),
(5, '沙县小吃(西单店)', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=200&h=200&fit=crop', '北京市西城区西单北大街', '010-55556666', 10.00, 3.00, 1, 4.5, 4500);

-- ----------------------------
-- 分类示例数据
-- ----------------------------
-- 麦当劳分类
INSERT IGNORE INTO `category` (`id`, `merchant_id`, `name`, `sort_order`, `status`) VALUES
(1, 1, '超值套餐', 1, 1),
(2, 1, '汉堡主食', 2, 1),
(3, 1, '小食甜点', 3, 1),
(4, 1, '饮料', 4, 1);

-- 肯德基分类
INSERT IGNORE INTO `category` (`id`, `merchant_id`, `name`, `sort_order`, `status`) VALUES
(5, 2, '炸鸡套餐', 1, 1),
(6, 2, '汉堡', 2, 1),
(7, 2, '小食', 3, 1),
(8, 2, '饮品', 4, 1);

-- 海底捞分类
INSERT IGNORE INTO `category` (`id`, `merchant_id`, `name`, `sort_order`, `status`) VALUES
(9, 3, '锅底', 1, 1),
(10, 3, '肉类', 2, 1),
(11, 3, '蔬菜', 3, 1),
(12, 3, '主食', 4, 1);

-- 星巴克分类
INSERT IGNORE INTO `category` (`id`, `merchant_id`, `name`, `sort_order`, `status`) VALUES
(13, 4, '咖啡', 1, 1),
(14, 4, '茶饮', 2, 1),
(15, 4, '甜点', 3, 1);

-- 沙县小吃分类
INSERT IGNORE INTO `category` (`id`, `merchant_id`, `name`, `sort_order`, `status`) VALUES
(16, 5, '拌面', 1, 1),
(17, 5, '汤类', 2, 1),
(18, 5, '蒸饺', 3, 1),
(19, 5, '小吃', 4, 1);

-- ----------------------------
-- 菜品示例数据
-- ----------------------------
-- 麦当劳菜品
INSERT IGNORE INTO `dish` (`merchant_id`, `category_id`, `name`, `image`, `description`, `price`, `stock`, `sales`, `status`) VALUES
(1, 1, '巨无霸套餐', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop', '巨无霸汉堡+中薯条+中可乐', 39.00, 999, 520, 1),
(1, 1, '麦辣鸡腿堡套餐', 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=400&h=300&fit=crop', '麦辣鸡腿堡+中薯条+中可乐', 35.00, 999, 480, 1),
(1, 2, '巨无霸', 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400&h=300&fit=crop', '双层牛肉饼配特制酱料', 25.00, 999, 320, 1),
(1, 2, '麦辣鸡腿堡', 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400&h=300&fit=crop', '香辣鸡腿肉配生菜', 22.00, 999, 290, 1),
(1, 2, '双层吉士汉堡', 'https://images.unsplash.com/photo-1572802419224-296b0aeee0d9?w=400&h=300&fit=crop', '双层牛肉饼配芝士', 18.00, 999, 210, 1),
(1, 3, '薯条(大)', 'https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?w=400&h=300&fit=crop', '金黄酥脆薯条', 15.00, 999, 450, 1),
(1, 3, '麦辣鸡翅(4块)', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=300&fit=crop', '香辣鸡翅', 16.00, 999, 380, 1),
(1, 3, '苹果派', 'https://images.unsplash.com/photo-1621743478914-cc8a86d7e7b5?w=400&h=300&fit=crop', '香甜苹果派', 8.00, 999, 220, 1),
(1, 4, '可口可乐(中)', 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400&h=300&fit=crop', '冰爽可乐', 10.00, 999, 600, 1),
(1, 4, '雪碧(中)', 'https://images.unsplash.com/photo-1680404005217-a441afdefe83?w=400&h=300&fit=crop', '清爽雪碧', 10.00, 999, 350, 1);

-- 肯德基菜品
INSERT IGNORE INTO `dish` (`merchant_id`, `category_id`, `name`, `image`, `description`, `price`, `stock`, `sales`, `status`) VALUES
(2, 5, '吮指原味鸡套餐', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=300&fit=crop', '2块原味鸡+中薯条+中可乐', 45.00, 999, 680, 1),
(2, 5, '香辣鸡腿堡套餐', 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=400&h=300&fit=crop', '香辣鸡腿堡+中薯条+中可乐', 38.00, 999, 520, 1),
(2, 6, '香辣鸡腿堡', 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=400&h=300&fit=crop', '香辣鸡腿肉配生菜沙拉酱', 23.00, 999, 410, 1),
(2, 6, '新奥尔良烤鸡腿堡', 'https://images.unsplash.com/photo-1553909489-cd47e0907980?w=400&h=300&fit=crop', '烤制鸡腿肉配特制酱料', 25.00, 999, 380, 1),
(2, 8, '九珍果汁', 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&h=300&fit=crop', '混合果汁饮料', 12.00, 999, 280, 1);

-- 海底捞菜品
INSERT IGNORE INTO `dish` (`merchant_id`, `category_id`, `name`, `image`, `description`, `price`, `stock`, `sales`, `status`) VALUES
(3, 9, '番茄锅底', 'https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=400&h=300&fit=crop', '酸甜番茄汤底', 68.00, 99, 120, 1),
(3, 9, '麻辣锅底', 'https://images.unsplash.com/photo-1666278172017-ad93e14c329d?w=400&h=300&fit=crop', '正宗四川麻辣锅底', 78.00, 99, 180, 1),
(3, 9, '清汤锅底', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', '清淡养生汤底', 58.00, 99, 90, 1),
(3, 10, '精品牛肉', 'https://images.unsplash.com/photo-1504973960431-1c467e159aa4?w=400&h=300&fit=crop', '优质牛肉', 68.00, 99, 210, 1);

-- 星巴克菜品
INSERT IGNORE INTO `dish` (`merchant_id`, `category_id`, `name`, `image`, `description`, `price`, `stock`, `sales`, `status`) VALUES
(4, 13, '拿铁(大杯)', 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=300&fit=crop', '经典意式拿铁', 36.00, 999, 890, 1),
(4, 13, '卡布奇诺(大杯)', 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400&h=300&fit=crop', '意式卡布奇诺', 36.00, 999, 420, 1),
(4, 13, '摩卡(大杯)', 'https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=400&h=300&fit=crop', '巧克力摩卡咖啡', 38.00, 999, 380, 1),
(4, 14, '抹茶拿铁(大杯)', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop', '日式抹茶拿铁', 38.00, 999, 520, 1),
(4, 14, '红茶拿铁(大杯)', 'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400&h=300&fit=crop', '英式红茶拿铁', 35.00, 999, 310, 1),
(4, 15, '提拉米苏', 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&h=300&fit=crop', '意式提拉米苏蛋糕', 42.00, 50, 180, 1),
(4, 15, '芝士蛋糕', 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=400&h=300&fit=crop', '纽约芝士蛋糕', 38.00, 50, 220, 1);

-- 沙县小吃菜品
INSERT IGNORE INTO `dish` (`merchant_id`, `category_id`, `name`, `image`, `description`, `price`, `stock`, `sales`, `status`) VALUES
(5, 16, '拌面', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', '花生酱拌面', 8.00, 999, 1200, 1),
(5, 16, '葱油拌面', 'https://images.unsplash.com/photo-1552611052-33e04de081de?w=400&h=300&fit=crop', '葱油香拌面', 9.00, 999, 980, 1),
(5, 17, '排骨汤', 'https://images.unsplash.com/photo-1603105037880-880cd4edfb0d?w=400&h=300&fit=crop', '玉米排骨汤', 22.00, 99, 280, 1),
(5, 18, '蒸饺(10个)', 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400&h=300&fit=crop', '鲜肉蒸饺', 12.00, 999, 850, 1);

-- ----------------------------
-- 测试用户数据
-- ----------------------------
INSERT IGNORE INTO `user` (`id`, `openid`, `nickname`, `avatar`, `phone`, `status`) VALUES
(1, 'dev_openid_test_user', '测试用户', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop', '13800138000', 1);

-- ----------------------------
-- 测试地址数据
-- ----------------------------
INSERT IGNORE INTO `address` (`user_id`, `contact_name`, `contact_phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(1, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号院2号楼3单元401室', 1),
(1, '李四', '13900139000', '北京市', '北京市', '朝阳区', '望京SOHO T1 15层', 0);

-- ----------------------------
-- 测试订单数据
-- ----------------------------
INSERT IGNORE INTO `order` (`id`, `order_no`, `user_id`, `merchant_id`, `address_id`, `address_snapshot`, `total_amount`, `delivery_fee`, `actual_amount`, `status`, `remark`, `create_time`, `pay_time`, `deliver_time`, `complete_time`) VALUES
(1, '202603100001', 1, 1, 1, '{"contactName":"张三","contactPhone":"13800138000","province":"北京市","city":"北京市","district":"海淀区","detail":"中关村大街1号院2号楼3单元401室"}',
  64.00, 5.00, 69.00, 3, '不要辣', '2026-03-09 12:30:00', '2026-03-09 12:30:15', '2026-03-09 12:45:00', '2026-03-09 13:15:00'),
(2, '202603100002', 1, 2, 1, '{"contactName":"张三","contactPhone":"13800138000","province":"北京市","city":"北京市","district":"海淀区","detail":"中关村大街1号院2号楼3单元401室"}',
  57.00, 4.00, 61.00, 2, '多加番茄酱', '2026-03-09 18:20:00', '2026-03-09 18:20:30', '2026-03-09 18:35:00', NULL),
(3, '202603100003', 1, 4, 2, '{"contactName":"李四","contactPhone":"13900139000","province":"北京市","city":"北京市","district":"朝阳区","detail":"望京SOHO T1 15层"}',
  72.00, 6.00, 78.00, 1, NULL, '2026-03-10 09:15:00', '2026-03-10 09:15:20', NULL, NULL),
(4, '202603100004', 1, 3, 2, '{"contactName":"李四","contactPhone":"13900139000","province":"北京市","city":"北京市","district":"朝阳区","detail":"望京SOHO T1 15层"}',
  214.00, 0.00, 214.00, 0, '微辣锅底', '2026-03-10 11:00:00', NULL, NULL, NULL),
(5, '202603100005', 1, 5, 1, '{"contactName":"张三","contactPhone":"13800138000","province":"北京市","city":"北京市","district":"海淀区","detail":"中关村大街1号院2号楼3单元401室"}',
  28.00, 3.00, 31.00, 4, NULL, '2026-03-08 19:30:00', NULL, NULL, NULL);

-- ----------------------------
-- 测试订单明细数据
-- ----------------------------
INSERT IGNORE INTO `order_item` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_image`, `dish_price`, `quantity`) VALUES
-- 订单1：麦当劳，两件商品
(1, 1, 1, '巨无霸套餐', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop', 39.00, 1),
(2, 1, 3, '巨无霸', 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=400&h=300&fit=crop', 25.00, 1),
-- 订单2：肯德基，两件商品
(3, 2, 11, '吮指原味鸡套餐', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=300&fit=crop', 45.00, 1),
(4, 2, 15, '九珍果汁', 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&h=300&fit=crop', 12.00, 1),
-- 订单3：星巴克，两杯拿铁
(5, 3, 20, '拿铁(大杯)', 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=300&fit=crop', 36.00, 2),
-- 订单4：海底捞，三道菜
(6, 4, 16, '番茄锅底', 'https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=400&h=300&fit=crop', 68.00, 1),
(7, 4, 19, '精品牛肉', 'https://images.unsplash.com/photo-1504973960431-1c467e159aa4?w=400&h=300&fit=crop', 68.00, 1),
(8, 4, 17, '麻辣锅底', 'https://images.unsplash.com/photo-1666278172017-ad93e14c329d?w=400&h=300&fit=crop', 78.00, 1),
-- 订单5：沙县小吃，拌面*2 + 蒸饺
(9, 5, 27, '拌面', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', 8.00, 2),
(10, 5, 30, '蒸饺(10个)', 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400&h=300&fit=crop', 12.00, 1);
