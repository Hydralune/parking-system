package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingSpot;
import com.parking.entity.Reservation;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.mapper.ReservationMapper;
import com.parking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Override
    public IPage<Reservation> getUserReservations(Page<Reservation> page, Long userId) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("create_time");
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Reservation> getReservations(Page<Reservation> page, Integer status) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("reservation_status", status);
        }
        wrapper.orderByDesc("create_time");
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        // 检查车位在指定时间段内是否可用
        if (isSpotAvailable(reservation.getParkingSpotId(), reservation.getStartTime(), reservation.getEndTime())) {
            // 设置初始状态
            reservation.setReservationStatus(2); // 已确认
            reservation.setPaymentStatus(0); // 未支付
            reservation.setReservationTime(LocalDateTime.now());
            save(reservation);

            // 更新车位状态为已预订
            ParkingSpot spot = parkingSpotMapper.selectById(reservation.getParkingSpotId());
            if (spot != null) {
                spot.setStatus(2); // 已预订
                parkingSpotMapper.updateById(spot);
            }

            return reservation;
        } else {
            throw new RuntimeException("车位在指定时间段内不可用");
        }
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationMapper.selectById(id);
    }

    @Override
    public boolean cancelReservation(Long id, Long userId) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation != null && reservation.getUserId().equals(userId)) {
            if (reservation.getReservationStatus() == 1 || reservation.getReservationStatus() == 2) { // 待确认或已确认状态
                reservation.setReservationStatus(3); // 已取消
                updateById(reservation);

                // 更新车位状态为可用
                ParkingSpot spot = parkingSpotMapper.selectById(reservation.getParkingSpotId());
                if (spot != null) {
                    spot.setStatus(1); // 可用
                    parkingSpotMapper.updateById(spot);
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public List<Reservation> getActiveReservations(Long parkingLotId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("parking_lot_id", parkingLotId)
               .and(w -> w
                   .le("start_time", endTime)
                   .ge("end_time", startTime)
               )
               .in("reservation_status", 1, 2, 4); // 待确认、已确认、已完成状态
        return reservationMapper.selectList(wrapper);
    }

    @Override
    public boolean updateReservationStatus(Long id, Integer status) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation != null) {
            reservation.setReservationStatus(status);
            return updateById(reservation);
        }
        return false;
    }

    /**
     * 检查车位在指定时间段内是否可用
     */
    private boolean isSpotAvailable(Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("parking_spot_id", spotId)
               .and(w -> w
                   .le("start_time", endTime)
                   .ge("end_time", startTime)
               )
               .in("reservation_status", 1, 2, 4); // 待确认、已确认、已完成状态
        return reservationMapper.selectCount(wrapper) == 0;
    }
}