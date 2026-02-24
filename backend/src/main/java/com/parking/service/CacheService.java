package com.parking.service;

public interface CacheService {
    /**
     * 缓存停车场信息
     */
    void cacheParkingLot(Long parkingLotId);

    /**
     * 清除停车场缓存
     */
    void clearParkingLotCache(Long parkingLotId);

    /**
     * 缓存车位状态信息
     */
    void cacheSpotStatus(Long spotId, Integer status);

    /**
     * 获取缓存的车位状态
     */
    Integer getCachedSpotStatus(Long spotId);

    /**
     * 清除车位状态缓存
     */
    void clearSpotStatusCache(Long spotId);

    /**
     * 缓存用户会话信息
     */
    void cacheUserSession(String token, Long userId);

    /**
     * 获取缓存的用户会话
     */
    Long getCachedUserSession(String token);

    /**
     * 清除用户会话缓存
     */
    void clearUserSession(String token);
}