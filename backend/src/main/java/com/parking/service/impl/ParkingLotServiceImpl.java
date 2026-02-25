package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingSpot;
import com.parking.mapper.ParkingLotMapper;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements ParkingLotService {

    @Autowired
    private ParkingLotMapper parkingLotMapper;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Override
    public IPage<ParkingLot> getParkingLots(Page<ParkingLot> page, String keyword) {
        QueryWrapper<ParkingLot> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("address", keyword);
        }
        wrapper.eq("status", 1);
        IPage<ParkingLot> result = parkingLotMapper.selectPage(page, wrapper);
        // 动态填充实时可用车位数
        result.getRecords().forEach(lot -> lot.setAvailableSpots(getAvailableSpotsCount(lot.getId())));
        return result;
    }

    @Override
    public List<ParkingLot> getAllParkingLots() {
        QueryWrapper<ParkingLot> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        List<ParkingLot> list = parkingLotMapper.selectList(wrapper);
        list.forEach(lot -> lot.setAvailableSpots(getAvailableSpotsCount(lot.getId())));
        return list;
    }

    @Override
    public ParkingLot getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotMapper.selectById(id);
        if (parkingLot != null) {
            parkingLot.setAvailableSpots(getAvailableSpotsCount(id));
        }
        return parkingLot;
    }

    @Override
    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        // 设置默认值
        if (parkingLot.getAvailableSpots() == null) {
            parkingLot.setAvailableSpots(parkingLot.getTotalSpots());
        }
        parkingLot.setStatus(1); // 默认启用
        save(parkingLot);
        return parkingLot;
    }

    @Override
    public ParkingLot updateParkingLot(ParkingLot parkingLot) {
        updateById(parkingLot);
        return parkingLot;
    }

    @Override
    public boolean deleteParkingLot(Long id) {
        boolean result = removeById(id);
        return result;
    }

    @Override
    public int getAvailableSpotsCount(Long parkingLotId) {
        QueryWrapper<ParkingSpot> wrapper = new QueryWrapper<>();
        wrapper.eq("parking_lot_id", parkingLotId)
               .eq("status", 1); // 只统计可用的车位
        return parkingSpotMapper.selectCount(wrapper).intValue();
    }
}