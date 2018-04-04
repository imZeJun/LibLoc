package com.lib.location;


public abstract class LocChangedListener {

    private LocResponse lastResponse;

    protected boolean isEqualResponse(LocResponse lastResponse, LocResponse newResponse) {
        return false;
    }

    final void noticeLocationChanged(LocResponse response) {
        if (!isEqualResponse(lastResponse, response)) {
            lastResponse = response;
            onLocationChanged(response);
        }
    }

    protected abstract void onLocationChanged(LocResponse response);
}
