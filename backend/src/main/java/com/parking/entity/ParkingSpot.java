package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("parking_spot")
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parkingLotId;

    private String spotNumber;

    private Integer spotType; // 1-小型车, 2-大型车, 3-残疾人专位

    private Integer status; // 0-维护中, 1-可用, 2-已预订, 3-已占用

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}