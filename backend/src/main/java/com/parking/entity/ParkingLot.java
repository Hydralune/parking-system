package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("parking_lot")
public class ParkingLot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer totalSpots;

    private Integer availableSpots;

    private BigDecimal hourlyRate;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private String phone;

    private String description;

    private Integer status; // 0-关闭, 1-营业中

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}