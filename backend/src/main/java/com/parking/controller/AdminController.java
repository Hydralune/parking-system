package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.dto.ResponseResult;
import com.parking.entity.Member;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingSpot;
import com.parking.entity.Reservation;
import com.parking.entity.User;
import com.parking.mapper.MemberMapper;
import com.parking.mapper.ParkingLotMapper;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.mapper.ReservationMapper;
import com.parking.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ReservationMapper reservationMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private ParkingLotMapper parkingLotMapper;
    @Autowired private ParkingSpotMapper parkingSpotMapper;
    @Autowired private MemberMapper memberMapper;

    @GetMapping("/stats")
    public ResponseResult<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 基础统计
        stats.put("totalReservations", reservationMapper.selectCount(null));
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalParkingLots", parkingLotMapper.selectCount(new QueryWrapper<ParkingLot>().eq("status", 1)));

        // 本月收入
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        BigDecimal monthlyIncome = reservationMapper.selectList(
            new QueryWrapper<Reservation>().eq("payment_status", 1).ge("reservation_time", monthStart))
            .stream().map(r -> r.getTotalFee() != null ? r.getTotalFee() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("monthlyIncome", monthlyIncome);

        // 今日预约数
        stats.put("todayReservations", reservationMapper.selectCount(
            new QueryWrapper<Reservation>().ge("reservation_time", LocalDate.now().atStartOfDay())));

        // 待支付数
        stats.put("pendingPayment", reservationMapper.selectCount(
            new QueryWrapper<Reservation>().eq("payment_status", 0).eq("reservation_status", 2)));

        // 近7天每天预约数
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            long cnt = reservationMapper.selectCount(
                new QueryWrapper<Reservation>()
                    .ge("reservation_time", day.atStartOfDay())
                    .lt("reservation_time", day.plusDays(1).atStartOfDay()));
            Map<String, Object> item = new HashMap<>();
            item.put("day", day.toString());
            item.put("count", cnt);
            weeklyData.add(item);
        }
        stats.put("weeklyReservations", weeklyData);

        // 停车场使用率
        List<Map<String, Object>> lotUsage = new ArrayList<>();
        parkingLotMapper.selectList(new QueryWrapper<ParkingLot>().eq("status", 1)).forEach(lot -> {
            long total = parkingSpotMapper.selectCount(new QueryWrapper<ParkingSpot>().eq("parking_lot_id", lot.getId()));
            long occupied = parkingSpotMapper.selectCount(new QueryWrapper<ParkingSpot>().eq("parking_lot_id", lot.getId()).ne("status", 1));
            Map<String, Object> item = new HashMap<>();
            item.put("name", lot.getName());
            item.put("occupied", occupied);
            item.put("total", total);
            lotUsage.add(item);
        });
        stats.put("lotUsage", lotUsage);

        // 会员等级分布
        Map<Integer, Long> memberDist = new HashMap<>();
        memberMapper.selectList(null).forEach(m -> memberDist.merge(m.getMemberLevel(), 1L, Long::sum));
        stats.put("memberDistribution", memberDist);

        return ResponseResult.success(stats);
    }
}
