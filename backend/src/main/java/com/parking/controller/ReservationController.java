package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.dto.ResponseResult;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingSpot;
import com.parking.entity.Reservation;
import com.parking.entity.User;
import com.parking.mapper.ParkingLotMapper;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.mapper.UserMapper;
import com.parking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ParkingLotMapper parkingLotMapper;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    private Long getUserId(Principal principal) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", principal.getName()));
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getId();
    }

    @GetMapping
    public ResponseResult<Page<Reservation>> getUserReservations(
            Principal principal,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getUserId(principal);
        Page<Reservation> page = new Page<>(current, size);
        Page<Reservation> result = (Page<Reservation>) reservationService.getUserReservations(page, userId);
        return ResponseResult.success(result);
    }

    @PostMapping
    public ResponseResult<Reservation> createReservation(@RequestBody Reservation reservation, Principal principal) {
        Long userId = getUserId(principal);
        reservation.setUserId(userId);
        Reservation created = reservationService.createReservation(reservation);
        return ResponseResult.success("预约成功", created);
    }

    @GetMapping("/{id}")
    public ResponseResult<Reservation> getReservationById(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation != null && reservation.getUserId().equals(userId)) {
            return ResponseResult.success(reservation);
        } else {
            return ResponseResult.error("预约不存在或无权限访问");
        }
    }

    @GetMapping("/all")
    public ResponseResult<Page<Reservation>> getAllReservations(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Reservation> page = new Page<>(current, size);
        Page<Reservation> result = (Page<Reservation>) reservationService.getReservations(page, status);
        result.getRecords().forEach(r -> {
            User u = userMapper.selectById(r.getUserId());
            if (u != null) r.setUsername(u.getUsername());
            ParkingLot lot = parkingLotMapper.selectById(r.getParkingLotId());
            if (lot != null) r.setParkingLotName(lot.getName());
            ParkingSpot spot = parkingSpotMapper.selectById(r.getParkingSpotId());
            if (spot != null) r.setSpotNumber(spot.getSpotNumber());
        });
        return ResponseResult.success(result);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> deleteReservation(@PathVariable Long id) {
        boolean result = reservationService.removeById(id);
        return result ? ResponseResult.success("删除成功", true) : ResponseResult.error("删除失败");
    }

    @PutMapping("/{id}")
    public ResponseResult<Boolean> updateReservationStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body.get("status");
        boolean result = reservationService.updateReservationStatus(id, status);
        return result ? ResponseResult.success("更新成功", true) : ResponseResult.error("更新失败");
    }

    @PutMapping("/{id}/pay")
    public ResponseResult<Boolean> payReservation(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            return ResponseResult.error("订单不存在或无权限");
        }
        boolean result = reservationService.payReservation(id);
        return result ? ResponseResult.success("支付成功", true) : ResponseResult.error("支付失败");
    }

    @PutMapping("/{id}/cancel")
    public ResponseResult<Boolean> cancelReservation(@PathVariable Long id, Principal principal) {
        Long userId = getUserId(principal);
        boolean result = reservationService.cancelReservation(id, userId);
        if (result) {
            return ResponseResult.success("取消成功", true);
        } else {
            return ResponseResult.error("取消失败或无权限操作");
        }
    }
}