-- ============================================================
-- 停车系统演示数据
-- 生成时间：2026-02-25
-- 包含：3个停车场、200个车位（按区分布）、大量预约记录
-- ============================================================

USE parking_system;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reservation;
TRUNCATE TABLE parking_spot;
TRUNCATE TABLE pricing_rule;
TRUNCATE TABLE parking_lot;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 停车场数据（3个）
-- ============================================================
INSERT INTO parking_lot (id, name, address, longitude, latitude, total_spots, available_spots, hourly_rate, opening_time, closing_time, phone, description, status) VALUES
(1, '中央商务区停车场', '北京市朝阳区建国路88号', 116.4614, 39.9042, 200, 20, 8.00, '00:00:00', '23:59:59', '010-88001234', '位于CBD核心区域，24小时开放，配备智能导航系统', 1),
(2, '万象城购物中心停车场', '北京市海淀区中关村大街1号', 116.3175, 39.9839, 120, 15, 6.00, '08:00:00', '22:00:00', '010-62001234', '紧邻万象城购物中心，购物满200元可享2小时免费停车', 1),
(3, '国际机场T3航站楼停车场', '北京市顺义区天竺镇', 116.5883, 40.0799, 300, 45, 12.00, '00:00:00', '23:59:59', '010-96001234', '机场专属停车场，提供行李寄存、接送机服务', 1);

-- ============================================================
-- 计费规则
-- ============================================================
INSERT INTO pricing_rule (parking_lot_id, rule_name, pricing_type, base_fee, hourly_rate, daily_cap, discount_start_time, discount_end_time, discount_rate) VALUES
(1, '标准计费', 1, 0.00, 8.00, 80.00, '22:00:00', '08:00:00', 0.50),
(2, '购物优惠计费', 1, 0.00, 6.00, 60.00, '20:00:00', '08:00:00', 0.60),
(3, '机场计费', 1, 0.00, 12.00, 120.00, NULL, NULL, 1.00);

-- ============================================================
-- 停车场1车位（200个，A/B/C/D四个区，各50个）
-- 其中180个占用，20个可用
-- ============================================================
DROP PROCEDURE IF EXISTS gen_spots_lot1;
DELIMITER //
CREATE PROCEDURE gen_spots_lot1()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE zone_prefix CHAR(1);
  DECLARE spot_num VARCHAR(20);
  DECLARE spot_status TINYINT;
  DECLARE spot_type TINYINT;

  -- A区：A001-A050，保留 A003,A011,A022,A035,A048 共5个可用
  SET i = 1;
  WHILE i <= 50 DO
    SET spot_num = CONCAT('A', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 20 = 0, 3, IF(i % 7 = 0, 2, 1));
    SET spot_status = IF(i IN (3,11,22,35,48), 1, IF(i % 3 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (1, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;

  -- B区：B001-B050，保留 B005,B018,B029,B041,B050 共5个可用
  SET i = 1;
  WHILE i <= 50 DO
    SET spot_num = CONCAT('B', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 25 = 0, 3, IF(i % 8 = 0, 2, 1));
    SET spot_status = IF(i IN (5,18,29,41,50), 1, IF(i % 4 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (1, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;

  -- C区：C001-C050，保留 C007,C015,C028,C039,C044 共5个可用
  SET i = 1;
  WHILE i <= 50 DO
    SET spot_num = CONCAT('C', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 15 = 0, 3, IF(i % 6 = 0, 2, 1));
    SET spot_status = IF(i IN (7,15,28,39,44), 1, IF(i % 5 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (1, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;

  -- D区：D001-D050，保留 D002,D013,D024,D037,D049 共5个可用
  SET i = 1;
  WHILE i <= 50 DO
    SET spot_num = CONCAT('D', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 10 = 0, 3, IF(i % 9 = 0, 2, 1));
    SET spot_status = IF(i IN (2,13,24,37,49), 1, IF(i % 6 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (1, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;
CALL gen_spots_lot1();
DROP PROCEDURE gen_spots_lot1;

-- ============================================================
-- 停车场2车位（120个，E/F两区，各60个）
-- 其中105个占用，15个可用
-- ============================================================
DROP PROCEDURE IF EXISTS gen_spots_lot2;
DELIMITER //
CREATE PROCEDURE gen_spots_lot2()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE spot_num VARCHAR(20);
  DECLARE spot_status TINYINT;
  DECLARE spot_type TINYINT;

  -- E区：E001-E060，保留 E004,E012,E023,E034,E045,E056,E060 共7个可用
  SET i = 1;
  WHILE i <= 60 DO
    SET spot_num = CONCAT('E', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 20 = 0, 3, IF(i % 7 = 0, 2, 1));
    SET spot_status = IF(i IN (4,12,23,34,45,56,60), 1, IF(i % 4 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (2, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;

  -- F区：F001-F060，保留 F008,F019,F031,F042,F053,F058,F059 共8个可用
  SET i = 1;
  WHILE i <= 60 DO
    SET spot_num = CONCAT('F', LPAD(i, 3, '0'));
    SET spot_type = IF(i % 30 = 0, 3, IF(i % 8 = 0, 2, 1));
    SET spot_status = IF(i IN (8,19,31,42,53,58,59), 1, IF(i % 5 = 0, 3, 2));
    INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (2, spot_num, spot_type, spot_status);
    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;
CALL gen_spots_lot2();
DROP PROCEDURE gen_spots_lot2;

-- ============================================================
-- 停车场3车位（300个，G/H/I/J/K/F六区，各50个）
-- 其中255个占用，45个可用
-- ============================================================
DROP PROCEDURE IF EXISTS gen_spots_lot3;
DELIMITER //
CREATE PROCEDURE gen_spots_lot3()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE spot_num VARCHAR(20);
  DECLARE spot_status TINYINT;
  DECLARE spot_type TINYINT;
  DECLARE zones_list VARCHAR(20) DEFAULT 'GHIJKL';
  DECLARE zone_char CHAR(1);
  DECLARE z INT DEFAULT 1;

  WHILE z <= 6 DO
    SET zone_char = SUBSTRING(zones_list, z, 1);
    SET i = 1;
    WHILE i <= 50 DO
      SET spot_num = CONCAT(zone_char, LPAD(i, 3, '0'));
      SET spot_type = IF(i % 25 = 0, 3, IF(i % 8 = 0, 2, 1));
      -- 每区保留约7-8个可用
      SET spot_status = IF(i IN (3,9,17,26,33,41,48), 1, IF(i % 5 = 0, 3, 2));
      INSERT INTO parking_spot (parking_lot_id, spot_number, spot_type, status) VALUES (3, spot_num, spot_type, spot_status);
      SET i = i + 1;
    END WHILE;
    SET z = z + 1;
  END WHILE;
END //
DELIMITER ;
CALL gen_spots_lot3();
DROP PROCEDURE gen_spots_lot3;

-- ============================================================
-- 预约记录（基于真实时间，今天=2026-02-25）
-- 部分今天过期，车位将被释放
-- ============================================================
INSERT INTO reservation (user_id, parking_lot_id, parking_spot_id, start_time, end_time, reservation_status, payment_status, total_fee, license_plate, reservation_time) VALUES
-- 已完成已支付（历史记录）
(1, 1, 1,  '2026-02-20 08:00:00', '2026-02-20 10:00:00', 4, 1, 16.00, '京A12345', '2026-02-19 20:00:00'),
(1, 1, 2,  '2026-02-21 09:00:00', '2026-02-21 12:00:00', 4, 1, 24.00, '京A12345', '2026-02-21 08:00:00'),
(1, 2, 51, '2026-02-22 14:00:00', '2026-02-22 18:00:00', 4, 1, 24.00, '京A12345', '2026-02-22 13:00:00'),
-- 当前进行中（今天，占用车位）
(1, 1, 4,  '2026-02-25 07:00:00', '2026-02-25 23:59:00', 2, 1, 128.00, '京A12345', '2026-02-25 06:30:00'),
(1, 1, 5,  '2026-02-25 08:00:00', '2026-02-26 08:00:00', 2, 1, 192.00, '京B88888', '2026-02-24 20:00:00'),
-- 今天到期（车位即将释放）
(1, 1, 6,  '2026-02-24 10:00:00', '2026-02-25 18:00:00', 2, 1, 256.00, '京C66666', '2026-02-24 09:00:00'),
-- 未支付待处理
(1, 1, 7,  '2026-02-25 10:00:00', '2026-02-25 14:00:00', 2, 0, 32.00, '京A12345', '2026-02-25 09:30:00'),
-- 已取消
(1, 2, 52, '2026-02-23 09:00:00', '2026-02-23 11:00:00', 3, 2, 12.00, '京A12345', '2026-02-22 20:00:00');

-- ============================================================
-- 更新停车场可用车位数（与实际车位状态同步）
-- ============================================================
UPDATE parking_lot SET available_spots = (
  SELECT COUNT(*) FROM parking_spot WHERE parking_lot_id = 1 AND status = 1
) WHERE id = 1;

UPDATE parking_lot SET available_spots = (
  SELECT COUNT(*) FROM parking_spot WHERE parking_lot_id = 2 AND status = 1
) WHERE id = 2;

UPDATE parking_lot SET available_spots = (
  SELECT COUNT(*) FROM parking_spot WHERE parking_lot_id = 3 AND status = 1
) WHERE id = 3;
