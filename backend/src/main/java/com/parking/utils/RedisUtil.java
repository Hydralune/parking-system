package com.parking.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

// @Component  // 注释掉以避免Spring扫描和实例化
public class RedisUtil {

    // RedisUtil功能暂时禁用以解决启动问题
    // 后续可通过适当配置重新启用

    public boolean expire(String key, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public long getExpire(String key) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public boolean hasKey(String key) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public void del(String... key) {
        // RedisUtil功能暂时禁用
    }

    public Object get(String key) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public boolean set(String key, Object value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean set(String key, Object value, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public long incr(String key, long delta) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public long decr(String key, long delta) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public Object hget(String key, String item) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public Map<Object, Object> hmget(String key) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public boolean hmset(String key, Map<String, Object> map) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean hmset(String key, Map<String, Object> map, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean hset(String key, String item, Object value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean hset(String key, String item, Object value, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public void hdel(String key, Object... item) {
        // RedisUtil功能暂时禁用
    }

    public boolean hHasKey(String key, String item) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public Set<Object> sGet(String key) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public boolean sHasKey(String key, Object value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public long sSet(String key, Object... values) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public long sSetAndTime(String key, long time, Object... values) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public long sGetSetSize(String key) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public long setRemove(String key, Object... values) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public List<Object> lGet(String key, long start, long end) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public long lGetListSize(String key) {
        // RedisUtil功能暂时禁用
        return 0;
    }

    public Object lGetIndex(String key, long index) {
        // RedisUtil功能暂时禁用
        return null;
    }

    public boolean lSet(String key, Object value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean lSet(String key, Object value, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean lSet(String key, List<Object> value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean lSet(String key, List<Object> value, long time) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public boolean lUpdateIndex(String key, long index, Object value) {
        // RedisUtil功能暂时禁用
        return false;
    }

    public long lRemove(String key, long count, Object value) {
        // RedisUtil功能暂时禁用
        return 0;
    }
}