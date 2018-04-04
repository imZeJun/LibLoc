package com.lib.location;


public class LocResponse {

    private double altitude;
    private double latitude;
    private double longitude;
    private long responseTime;

    public LocResponse(Builder builder) {
        this.altitude = builder.altitude;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.responseTime = builder.responseTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static final class Builder {

        private double altitude;
        private double latitude;
        private double longitude;
        private long responseTime;

        public Builder altitude(double altitude) {
            this.altitude = altitude;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder responseTime(long responseTime) {
            this.responseTime = responseTime;
            return this;
        }

        public LocResponse build() {
            return new LocResponse(this);
        }

    }


}
