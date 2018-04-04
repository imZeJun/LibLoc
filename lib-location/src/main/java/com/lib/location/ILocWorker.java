package com.lib.location;


import android.support.annotation.NonNull;

public interface ILocWorker {

    /**
     * 发起定位请求。
     * @param callback 回调。
     * @param params 定位参数，如果为空，那么使用默认的配置。
     *
     */
    void doRealLoc(ILocCallback callback, @NonNull LocParams params);

    /**
     * 停止定位。
     */
    void stopLoc();

    /**
     * 销毁定位。
     */
    void destroyLoc();
}
