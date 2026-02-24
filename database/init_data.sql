USE parking_system;

-- 用户数据由后端 DataInitializer 在启动时自动创建（密码会被正确加密）

-- 初始化测试停车场数据
INSERT INTO `parking_lot` (`name`, `address`, `longitude`, `latitude`, `total_spots`, `available_spots`, `hourly_rate`, `opening_time`, `closing_time`, `phone`, `description`, `status`, `create_time`) VALUES
('中央商务区停车场', '北京市朝阳区建国门外大街1号', 116.407428, 39.904211, 200, 180, 5.00, '06:00:00', '23:00:00', '010-88889999', '位于CBD核心区域，交通便利，设施完善', 1, NOW()),
('购物中心地下停车场', '上海市浦东新区陆家嘴环路1000号', 121.490407, 31.233632, 300, 250, 4.00, '08:00:00', '23:59:59', '021-66667777', '大型购物中心配套停车场，24小时开放', 1, NOW()),
('科技园区停车场', '深圳市南山区科技园南区高新南大道9号', 113.941255, 22.544752, 150, 140, 3.50, '06:30:00', '22:30:00', '0755-55556666', '高新技术园区配套停车场，支持月卡办理', 1, NOW());

-- 初始化车位数据 (为每个停车场创建一些车位)
-- 中央商务区停车场车位
INSERT INTO `parking_spot` (`parking_lot_id`, `spot_number`, `spot_type`, `status`, `create_time`) VALUES
(1, 'A001', 1, 1, NOW()),
(1, 'A002', 1, 1, NOW()),
(1, 'A003', 1, 1, NOW()),
(1, 'A004', 1, 1, NOW()),
(1, 'A005', 1, 1, NOW()),
(1, 'B001', 1, 1, NOW()),
(1, 'B002', 1, 1, NOW()),
(1, 'B003', 1, 1, NOW()),
(1, 'B004', 1, 1, NOW()),
(1, 'B005', 1, 1, NOW()),
(1, 'C001', 2, 1, NOW()),
(1, 'C002', 2, 1, NOW()),
(1, 'D001', 3, 1, NOW());

-- 购物中心地下停车场车位
INSERT INTO `parking_spot` (`parking_lot_id`, `spot_number`, `spot_type`, `status`, `create_time`) VALUES
(2, 'P101', 1, 1, NOW()),
(2, 'P102', 1, 1, NOW()),
(2, 'P103', 1, 1, NOW()),
(2, 'P104', 1, 1, NOW()),
(2, 'P105', 1, 1, NOW()),
(2, 'P106', 1, 1, NOW()),
(2, 'P107', 1, 1, NOW()),
(2, 'P108', 1, 1, NOW()),
(2, 'P109', 1, 1, NOW()),
(2, 'P110', 1, 1, NOW()),
(2, 'VIP01', 1, 1, NOW()),
(2, 'VIP02', 1, 1, NOW());

-- 科技园区停车场车位
INSERT INTO `parking_spot` (`parking_lot_id`, `spot_number`, `spot_type`, `status`, `create_time`) VALUES
(3, 'T001', 1, 1, NOW()),
(3, 'T002', 1, 1, NOW()),
(3, 'T003', 1, 1, NOW()),
(3, 'T004', 1, 1, NOW()),
(3, 'T005', 1, 1, NOW()),
(3, 'T006', 1, 1, NOW()),
(3, 'T007', 1, 1, NOW()),
(3, 'T008', 1, 1, NOW()),
(3, 'T009', 1, 1, NOW()),
(3, 'T010', 1, 1, NOW());

-- 初始化计费规则
INSERT INTO `pricing_rule` (`parking_lot_id`, `rule_name`, `pricing_type`, `base_fee`, `hourly_rate`, `daily_cap`, `discount_start_time`, `discount_end_time`, `discount_rate`, `create_time`) VALUES
(1, '中央商务区标准计费', 1, 0.00, 5.00, 40.00, '22:00:00', '08:00:00', 0.80, NOW()),
(1, '中央商务区夜间优惠', 1, 0.00, 4.00, 30.00, '23:00:00', '06:00:00', 0.70, NOW()),
(2, '购物中心标准计费', 1, 0.00, 4.00, 35.00, '21:00:00', '08:00:00', 0.90, NOW()),
(3, '科技园区标准计费', 1, 0.00, 3.50, 30.00, '20:00:00', '07:00:00', 0.85, NOW());

-- 初始化会员信息
INSERT INTO `member` (`user_id`, `member_level`, `points`, `discount_rate`, `valid_from`, `valid_to`, `create_time`) VALUES
(3, 2, 1500, 0.95, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 YEAR), NOW()),
(4, 3, 3200, 0.90, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 1 YEAR), NOW());

-- 更新停车场剩余车位数
UPDATE `parking_lot` SET `available_spots` = `total_spots` WHERE id IN (1, 2, 3);