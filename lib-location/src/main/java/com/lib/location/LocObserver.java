package com.lib.location;


public abstract class LocObserver {

    private LocResponse lastResponse;

    protected boolean equal(LocResponse lastResponse, LocResponse newResponse) {
        return false;
    }

    final void noticeLocationChanged(LocResponse response) {
        if (!equal(lastResponse, response)) {
            lastResponse = response;
            onLocationChanged(response);
        }
    }

    protected abstract void onLocationChanged(LocResponse response);
}
