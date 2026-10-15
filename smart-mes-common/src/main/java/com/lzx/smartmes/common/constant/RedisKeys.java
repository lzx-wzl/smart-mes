package com.lzx.smartmes.common.constant;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String deviceStatus(Long id) {
        return "smart-mes:device:" + id + ":status";
    }

    public static String processed(String id) {
        return "smart-mes:mq:processed:" + id;
    }

    public static String maintenanceLock(Long id) {
        return "smart-mes:lock:maintenance:" + id;
    }
}