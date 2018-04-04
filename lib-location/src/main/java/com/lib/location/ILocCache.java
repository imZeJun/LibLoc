package com.lib.location;


public interface ILocCache {

    /**
     * 获取地理位置缓存。
     * @return 缓存的地理位置。
     */
    LocResponse get();

    /**
     * 存入地理位置缓存。
     * @param response 存入的地理位置缓存。
     */
    void put(LocResponse response);

    /**
     * 判断缓存是否过期。
     * @param response 缓存的请求。
     * @return 是否过期。
     */
    boolean expire(LocResponse response);
}
