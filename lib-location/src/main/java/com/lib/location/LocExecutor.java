package com.lib.location;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class LocExecutor {

    private static final int DELAY_LOC_TIME = 2000;
    private static final int CANCEL_LOC_TIMER = 30000;

    private ILocCache cache;
    private ILocWorker worker;
    private Handler locHandler;
    private long delayLocTime;
    private long forceCancelTime;
    private List<LocObserver> observers = new ArrayList<>();

    public void LocExecutor(ILocWorker worker, ILocCache cache) {
        this.worker = worker;
        this.cache = cache;
        this.locHandler = new LocHandler(Looper.getMainLooper());
    }

    /**
     * 发起定位请求。
     * @param callback 定位请求的回调。
     * @param params 定位参数，如果为空，那么使用默认的配置。
     *
     */
    public void startLoc(final ILocCallback callback, LocParams params) {
        if (params == null) {
            return;
        }
        LocResponse cacheResponse = getLocCache();
        boolean isExpire = cacheExpire(cacheResponse);
        if (cacheResponse == null) {
            //如果没有缓存，那么立即发起定位。
            doRealLoc(callback, params);
        } else {
            //首先将缓存返回给调用者。
            if (callback != null) {
                callback.onLocFinished(cacheResponse);
            }
            //如果缓存过期，那么间隔一段时间再发起定位。
            if (isExpire) {
                removeDelayLoc();
                doDelayLoc(callback, params);
            }
        }
    }

    /**
     * 取消定位。
     */
    public void stopLoc() {
        removeCancelTimer();
        removeDelayLoc();
        if (worker != null) {
            worker.stopLoc();
        }
    }

    /**
     * 销毁定位。
     */
    public void destroyLoc() {
        removeCancelTimer();
        removeDelayLoc();
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
     * @param observer 监听。
     */
    public void addLocObsever(LocObserver observer) {
        observers.add(observer);
    }

    /**
     * 移除请求导致的地理位置变化。
     * @param observer 监听。
     */
    public void removeLocObserver(LocObserver observer) {
        observers.remove(observer);
    }

    /**
     * 真正发起定位请求。
     * @param callback 定位请求回调。
     * @param params 参数。
     */
    private void doRealLoc(final ILocCallback callback, @NonNull LocParams params) {
        if (worker != null) {
            worker.doRealLoc(new LocCallbackWrapper(callback), params);
        }
        removeCancelTimer();
        doCancelTimer(callback);
    }

    /**
     * 当已经有缓存的时候，延迟一段时间再发起定位。
     * @param callback 定位请求回调。
     * @param params 参数。
     */
    private void doDelayLoc(final ILocCallback callback, @NonNull LocParams params) {
        Message message = locHandler.obtainMessage(LocHandler.MSG_DELAY_LOC, new Pair<>(callback, params));
        locHandler.sendMessageDelayed(message, getDelayLocTime());
    }

    /**
     * 取消延时定位请求。
     */
    private void removeDelayLoc() {
        locHandler.removeMessages(LocHandler.MSG_DELAY_LOC);
    }

    /**
     * 为了防止定位时间过长，当定位时间过长时，会强制取消定位请求，并返回缓存的位置。
     * @param callback 回调
     */
    private void doCancelTimer(ILocCallback callback) {
        Message message = locHandler.obtainMessage(LocHandler.MSG_FORCE_CANCEL_LOC, callback);
        locHandler.sendMessageDelayed(message, getForceCancelTime());
    }

    /**
     * 取消定位时长的计时器。
     */
    private void removeCancelTimer() {
        locHandler.removeMessages(LocHandler.MSG_FORCE_CANCEL_LOC);
    }

    /**
     * 通知监听者位置发生了变化。
     * @param response 新的地理位置信息。
     */
    private void noticeLocChanged(LocResponse response) {
        for (LocObserver listener : observers) {
            listener.onLocationChanged(response);
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

    public long getDelayLocTime() {
        return delayLocTime > 0 ? delayLocTime : DELAY_LOC_TIME;
    }

    public long getForceCancelTime() {
        return forceCancelTime > 0 ? forceCancelTime : CANCEL_LOC_TIMER;
    }

    public void setDelayLocTime(long delayLocTime) {
        this.delayLocTime = delayLocTime;
    }

    public void setForceCancelTime(long forceCancelTime) {
        this.forceCancelTime = forceCancelTime;
    }

    private final class LocCallbackWrapper implements ILocCallback {

        private ILocCallback callback;

        LocCallbackWrapper(ILocCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onLocFinished(LocResponse response) {
            removeCancelTimer();
            if (response == null) {
                response = getLocCache();
            } else {
                cacheLocResponse(response);
            }
            if (callback != null) {
                callback.onLocFinished(response);
            }
            noticeLocChanged(response);
        }
    }

    private final class LocHandler extends Handler {

        private static final int MSG_DELAY_LOC = 0;
        private static final int MSG_FORCE_CANCEL_LOC = 1;

        public LocHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELAY_LOC:
                    Pair<ILocCallback, LocParams> obj = (Pair<ILocCallback, LocParams>) msg.obj;
                    if (obj != null) {
                        doRealLoc(obj.first, obj.second);
                    }
                    break;
                case MSG_FORCE_CANCEL_LOC:
                    stopLoc();
                    LocResponse cache = getLocCache();
                    ILocCallback callback = (ILocCallback) msg.obj;
                    if (callback != null) {
                        callback.onLocFinished(cache);
                    }
                    noticeLocChanged(cache);
                    break;
                default:
                    break;
            }
        }
    }
}
