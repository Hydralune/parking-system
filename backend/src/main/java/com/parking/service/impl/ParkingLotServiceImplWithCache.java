package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingSpot;
import com.parking.mapper.ParkingLotMapper;
import com.parking.mapper.ParkingSpotMapper;
import com.parking.service.CacheService;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service
public class ParkingLotServiceImplWithCache extends ServiceImpl<ParkingLotMapper, ParkingLot> implements ParkingLotService {

    @Autowired
    private ParkingLotMapper parkingLotMapper;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    @Cacheable(value = "parkingLots", key = "#keyword == null ? 'all' : #keyword")
    public IPage<ParkingLot> getParkingLots(Page<ParkingLot> page, String keyword) {
        QueryWrapper<ParkingLot> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("address", keyword);
        }
        wrapper.eq("status", 1); // 只查询营业中的停车场
        return parkingLotMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ParkingLot> getAllParkingLots() {
        QueryWrapper<ParkingLot> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 只查询营业中的停车场
        return parkingLotMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(value = "parkingLot", key = "#id")
    public ParkingLot getParkingLotById(Long id) {
        return parkingLotMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = {"parkingLots", "parkingLot"}, allEntries = true)
    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        // 设置默认值
        if (parkingLot.getAvailableSpots() == null) {
            parkingLot.setAvailableSpots(parkingLot.getTotalSpots());
        }
        parkingLot.setStatus(1); // 默认启用
        save(parkingLot);
        
        // 缓存新创建的停车场
        cacheService.cacheParkingLot(parkingLot.getId());
        
        return parkingLot;
    }

    @Override
    @CacheEvict(value = {"parkingLots", "parkingLot"}, allEntries = true)
    public ParkingLot updateParkingLot(ParkingLot parkingLot) {
        updateById(parkingLot);
        
        // 清除缓存
        cacheService.clearParkingLotCache(parkingLot.getId());
        
        // 重新缓存
        cacheService.cacheParkingLot(parkingLot.getId());
        
        return parkingLot;
    }

    @Override
    @CacheEvict(value = {"parkingLots", "parkingLot"}, allEntries = true)
    public boolean deleteParkingLot(Long id) {
        boolean result = removeById(id);
        
        // 清除缓存
        cacheService.clearParkingLotCache(id);
        
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