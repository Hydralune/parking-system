package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.dto.ResponseResult;
import com.parking.entity.Reservation;
import com.parking.entity.User;
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