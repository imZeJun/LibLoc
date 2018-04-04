package com.lib.location;

import java.util.ArrayList;
import java.util.List;

public class LocExecutor {

    private ILocCache cache;
    private ILocWorker worker;
    private List<LocChangedListener> listeners = new ArrayList<>();

    private static final class Holder {
        private static final LocExecutor INSTANCE = new LocExecutor();
    }

    public static LocExecutor getInstance() {
        return Holder.INSTANCE;
    }

    public void buildWorker(ILocWorker worker, ILocCache cache) {
        this.worker = worker;
        this.cache = cache;
    }

    /**
     * 发起定位请求。
     * @param callback 定位参数，如果为空，那么使用默认的配置。
     */
    public void startLoc(final ILocCallback callback) {
        startLoc(callback, null);
    }

    /**
     * 发起定位请求。
     * @param callback 定位请求的回调。
     * @param params 定位参数，如果为空，那么使用默认的配置。
     *
     */
    public void startLoc(final ILocCallback callback, LocParams params) {
        LocResponse cacheResponse = getLocCache();
        boolean isExpire = cacheExpire(cacheResponse);
        if (isExpire) {
            doRealLoc(callback, params);
        } else if (callback != null) {
            callback.onLocFinished(cacheResponse);
        }
    }

    /**
     * 取消定位。
     */
    public void stopLoc() {
        if (worker != null) {
            worker.stopLoc();
        }
    }

    /**
     * 销毁定位。
     */
    public void destroyLoc() {
        if (worker != null) {
            worker.destroyLoc();
        }
    }

    /**
     * 获取上次请求的位置。
     * @return 上次请求的位置。
     */
    public LocResponse getLastLoc() {
        return getLocCache();
    }

    /**
     * 监听请求导致的地理位置变化。
     * @param listener 监听。
     */
    public void addLocChangedListener(LocChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除请求导致的地理位置变化。
     * @param listener 监听。
     */
    public void removeLocChangedListener(LocChangedListener listener) {
        listeners.remove(listener);
    }

    private void doRealLoc(final ILocCallback callback, LocParams params) {
        if (worker != null) {
            worker.doRealLoc(new LocCallbackWrapper(callback), params);
        }
    }

    private void noticeLocChanged(LocResponse response) {
        for (LocChangedListener listener : listeners) {
            listener.noticeLocationChanged(response);
        }
    }

    private LocResponse getLocCache() {
        return cache != null ? cache.get() : null;
    }

    private void cacheLocResponse(LocResponse response) {
        if (cache != null) {
            cache.put(response);
        }
    }

    private boolean cacheExpire(LocResponse response) {
        return cache == null || cache.expire(response);
    }

    private static final class LocCallbackWrapper implements ILocCallback {

        private ILocCallback callback;

        LocCallbackWrapper(ILocCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onLocFinished(LocResponse response) {
            if (response == null) {
                response = Holder.INSTANCE.getLocCache();
            } else {
                Holder.INSTANCE.cacheLocResponse(response);
            }
            if (callback != null) {
                callback.onLocFinished(response);
            }
            Holder.INSTANCE.noticeLocChanged(response);
        }
    }
}
