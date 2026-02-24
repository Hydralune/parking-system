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
@TableName("pricing_rule")
public class PricingRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parkingLotId;

    private String ruleName;

    private Integer pricingType; // 1-按时, 2-按次, 3-包月

    private BigDecimal baseFee;

    private BigDecimal hourlyRate;

    private BigDecimal dailyCap;

    private LocalTime discountStartTime;

    private LocalTime discountEndTime;

    private BigDecimal discountRate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}