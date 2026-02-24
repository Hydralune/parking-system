package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.ParkingLot;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ParkingLotService extends IService<ParkingLot> {
    IPage<ParkingLot> getParkingLots(Page<ParkingLot> page, String keyword);
    List<ParkingLot> getAllParkingLots();
    ParkingLot getParkingLotById(Long id);
    ParkingLot createParkingLot(ParkingLot parkingLot);
    ParkingLot updateParkingLot(ParkingLot parkingLot);
    boolean deleteParkingLot(Long id);
    int getAvailableSpotsCount(Long parkingLotId);
}