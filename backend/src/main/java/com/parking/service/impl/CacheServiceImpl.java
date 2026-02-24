package com.parking.service.impl;

import com.parking.entity.ParkingLot;
import com.parking.service.CacheService;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private ParkingLotService parkingLotService;

    // 缓存功能暂时禁用以解决启动问题
    private static final String PARKING_LOT_CACHE_KEY = "parking:lot:";
    private static final String SPOT_STATUS_CACHE_KEY = "parking:spot:status:";
    private static final String USER_SESSION_CACHE_KEY = "parking:session:";

    @Override
    public void cacheParkingLot(Long parkingLotId) {
        // 缓存功能暂时禁用
    }

    @Override
    public void clearParkingLotCache(Long parkingLotId) {
        // 缓存功能暂时禁用
    }

    @Override
    public void cacheSpotStatus(Long spotId, Integer status) {
        // 缓存功能暂时禁用
    }

    @Override
    public Integer getCachedSpotStatus(Long spotId) {
        // 缓存功能暂时禁用
        return null;
    }

    @Override
    public void clearSpotStatusCache(Long spotId) {
        // 缓存功能暂时禁用
    }

    @Override
    public void cacheUserSession(String token, Long userId) {
        // 缓存功能暂时禁用
    }

    @Override
    public Long getCachedUserSession(String token) {
        // 缓存功能暂时禁用
        return null;
    }

    @Override
    public void clearUserSession(String token) {
        // 缓存功能暂时禁用
    }
}