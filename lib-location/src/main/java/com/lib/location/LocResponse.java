package com.lib.location;


public class LocResponse {

    private String city;
    private String cityCode;
    private double latitude;
    private double longitude;
    private long responseTime;

    public LocResponse(Builder builder) {
        this.city = builder.city;
        this.cityCode = builder.cityCode;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.responseTime = builder.responseTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCityCode() {
        return cityCode;
    }

    public static final class Builder {

        private String city;
        private String cityCode;
        private double latitude;
        private double longitude;
        private long responseTime;

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder cityCode(String cityCode) {
            this.cityCode = cityCode;
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
