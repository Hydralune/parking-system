package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reservation")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long parkingLotId;

    private Long parkingSpotId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime actualCheckIn;

    private LocalDateTime actualCheckOut;

    private Integer reservationStatus; // 1-待确认, 2-已确认, 3-已取消, 4-已完成, 5-已过期

    private Integer paymentStatus; // 0-未支付, 1-已支付, 2-已退款

    private BigDecimal totalFee;

    private LocalDateTime reservationTime;

    private String licensePlate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String parkingLotName;

    @TableField(exist = false)
    private String spotNumber;
}