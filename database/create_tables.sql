-- 创建数据库
CREATE DATABASE IF NOT EXISTS parking_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE parking_system;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `user_type` TINYINT DEFAULT 0 COMMENT '用户类型: 0-普通用户, 1-管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `avatar` VARCHAR(255) COMMENT '头像',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_user_type` (`user_type`)
) COMMENT='用户表';

-- 停车场表
CREATE TABLE `parking_lot` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '停车场ID',
    `name` VARCHAR(100) NOT NULL COMMENT '停车场名称',
    `address` VARCHAR(255) NOT NULL COMMENT '地址',
    `longitude` DECIMAL(10, 7) COMMENT '经度',
    `latitude` DECIMAL(10, 7) COMMENT '纬度',
    `total_spots` INT DEFAULT 0 COMMENT '总车位数',
    `available_spots` INT DEFAULT 0 COMMENT '剩余车位数',
    `hourly_rate` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '每小时费用',
    `opening_time` TIME COMMENT '营业开始时间',
    `closing_time` TIME COMMENT '营业结束时间',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `description` TEXT COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-关闭, 1-营业中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_location` (`longitude`, `latitude`),
    INDEX `idx_status` (`status`)
) COMMENT='停车场表';

-- 车位表
CREATE TABLE `parking_spot` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '车位ID',
    `parking_lot_id` BIGINT NOT NULL COMMENT '停车场ID',
    `spot_number` VARCHAR(20) NOT NULL COMMENT '车位编号',
    `spot_type` TINYINT DEFAULT 1 COMMENT '车位类型: 1-小型车, 2-大型车, 3-残疾人专位',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-维护中, 1-可用, 2-已预订, 3-已占用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`parking_lot_id`) REFERENCES `parking_lot`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_parking_lot_spot` (`parking_lot_id`, `spot_number`),
    INDEX `idx_parking_lot_id` (`parking_lot_id`),
    INDEX `idx_status` (`status`)
) COMMENT='车位表';

-- 计费规则表
CREATE TABLE `pricing_rule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    `parking_lot_id` BIGINT NOT NULL COMMENT '停车场ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `pricing_type` TINYINT DEFAULT 1 COMMENT '计费类型: 1-按时, 2-按次, 3-包月',
    `base_fee` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '基础费用',
    `hourly_rate` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '每小时费用',
    `daily_cap` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '每日上限',
    `discount_start_time` TIME COMMENT '优惠时段开始时间',
    `discount_end_time` TIME COMMENT '优惠时段结束时间',
    `discount_rate` DECIMAL(5, 2) DEFAULT 1.00 COMMENT '优惠费率',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`parking_lot_id`) REFERENCES `parking_lot`(`id`) ON DELETE CASCADE,
    INDEX `idx_parking_lot_id` (`parking_lot_id`)
) COMMENT='计费规则表';

-- 预约订单表
CREATE TABLE `reservation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `parking_lot_id` BIGINT NOT NULL COMMENT '停车场ID',
    `parking_spot_id` BIGINT NOT NULL COMMENT '车位ID',
    `start_time` DATETIME NOT NULL COMMENT '预约开始时间',
    `end_time` DATETIME NOT NULL COMMENT '预约结束时间',
    `actual_check_in` DATETIME COMMENT '实际入场时间',
    `actual_check_out` DATETIME COMMENT '实际离场时间',
    `reservation_status` TINYINT DEFAULT 1 COMMENT '预约状态: 1-待确认, 2-已确认, 3-已取消, 4-已完成, 5-已过期',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态: 0-未支付, 1-已支付, 2-已退款',
    `total_fee` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '总费用',
    `reservation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    `license_plate` VARCHAR(20) COMMENT '车牌号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`parking_lot_id`) REFERENCES `parking_lot`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`parking_spot_id`) REFERENCES `parking_spot`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parking_lot_id` (`parking_lot_id`),
    INDEX `idx_reservation_time` (`reservation_time`),
    INDEX `idx_start_time_end_time` (`start_time`, `end_time`),
    INDEX `idx_reservation_status` (`reservation_status`)
) COMMENT='预约订单表';

-- 会员信息表
CREATE TABLE `member` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会员ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `member_level` TINYINT DEFAULT 1 COMMENT '会员等级: 1-普通, 2-银卡, 3-金卡, 4-钻石',
    `points` INT DEFAULT 0 COMMENT '会员积分',
    `discount_rate` DECIMAL(5, 2) DEFAULT 1.00 COMMENT '会员折扣',
    `valid_from` DATE COMMENT '有效期开始',
    `valid_to` DATE COMMENT '有效期结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_member_level` (`member_level`),
    INDEX `idx_valid_to` (`valid_to`)
) COMMENT='会员信息表';

-- 添加外键约束的索引
ALTER TABLE `pricing_rule` ADD CONSTRAINT `fk_pricing_rule_parking_lot` 
    FOREIGN KEY (`parking_lot_id`) REFERENCES `parking_lot`(`id`) ON DELETE CASCADE;

ALTER TABLE `reservation` ADD CONSTRAINT `fk_reservation_user` 
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;

ALTER TABLE `reservation` ADD CONSTRAINT `fk_reservation_parking_lot` 
    FOREIGN KEY (`parking_lot_id`) REFERENCES `parking_lot`(`id`) ON DELETE CASCADE;

ALTER TABLE `reservation` ADD CONSTRAINT `fk_reservation_parking_spot` 
    FOREIGN KEY (`parking_spot_id`) REFERENCES `parking_spot`(`id`) ON DELETE CASCADE;

ALTER TABLE `member` ADD CONSTRAINT `fk_member_user` 
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;

-- 创建用于防止车位重复预订的唯一约束（在非冲突时间段内）
-- 通过触发器或应用程序逻辑确保同一车位在同一时间段内不能被多次预订