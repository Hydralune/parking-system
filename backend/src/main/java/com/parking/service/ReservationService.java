package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.Reservation;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService extends IService<Reservation> {
    IPage<Reservation> getUserReservations(Page<Reservation> page, Long userId);
    IPage<Reservation> getReservations(Page<Reservation> page, Integer status);
    Reservation createReservation(Reservation reservation);
    Reservation getReservationById(Long id);
    boolean cancelReservation(Long id, Long userId);
    List<Reservation> getActiveReservations(Long parkingLotId, LocalDateTime startTime, LocalDateTime endTime);
    boolean updateReservationStatus(Long id, Integer status);
    boolean payReservation(Long id);
}