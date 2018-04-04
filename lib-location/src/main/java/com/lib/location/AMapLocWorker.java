package com.lib.location;

import android.content.Context;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

/**
 * 高德地图的定位实现类。
 */
public class AMapLocWorker implements ILocWorker {

    private Context context;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocParams lastParams;

    AMapLocWorker(Context context) {
        this.context = context;
    }

    @Override
    public void doRealLoc(ILocCallback callback, LocParams params) {
        initLocOption(params);
        initLocWorker();
        doRealLocWork(callback);
    }

    @Override
    public void stopLoc() {
        locationClient.stopLocation();
    }

    @Override
    public void destroyLoc() {
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 初始化定位配置。
     * @param params 定位配置。
     */
    private void initLocOption(LocParams params){
        if (params == null) {
            params = getDefaultParams();
        }
        if (locationOption == null || !params.equals(lastParams)) {
            locationOption = getLocOption(params);
        }
        lastParams = params;
    }

    /**
     * 初始化定位管理类，只初始化一次。
     */
    private void initLocWorker() {
        if (locationClient == null) {
            locationClient = new AMapLocationClient(context);
        }
        locationClient.setLocationOption(locationOption);
    }

    /**
     * 发起定位请求。
     * @param callback 定位的回调。
     */
    private void doRealLocWork(ILocCallback callback){
        if (locationClient != null) {
            locationClient.setLocationListener(new AMapLocationListener(callback));
            locationClient.startLocation();
        }
    }

    private LocParams getDefaultParams() {
        return new LocParams();
    }

    private AMapLocationClientOption getLocOption(LocParams params){
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setGpsFirst(false);
        option.setHttpTimeOut(30000);
        option.setInterval(2000);
        option.setNeedAddress(true);
        option.setOnceLocation(params.isOnce());
        option.setOnceLocationLatest(false);
        option.setSensorEnable(false);
        option.setWifiScan(false);
        option.setLocationCacheEnable(false);
        return option;
    }

    private class AMapLocationListener implements com.amap.api.location.AMapLocationListener {

        private ILocCallback locationCallback;

        AMapLocationListener(ILocCallback locationCallback) {
            this.locationCallback = locationCallback;
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            LocResponse response = dealResponse(aMapLocation);
            if (locationCallback != null) {
                locationCallback.onLocFinished(response);
            }
        }

        LocResponse dealResponse(AMapLocation aMapLocation) {
            LocResponse response = null;
            if (aMapLocation != null && aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                response = new LocResponse.Builder()
                        .responseTime(System.currentTimeMillis())
                        .altitude(aMapLocation.getAltitude())
                        .latitude(aMapLocation.getLatitude())
                        .longitude(aMapLocation.getLongitude()).build();
            }
            return response;
        }
    }




}
