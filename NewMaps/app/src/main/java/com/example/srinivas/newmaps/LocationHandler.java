package com.example.srinivas.newmaps;

/**
 * Created by Srinivas on 29-06-2016.
 */
public class LocationHandler {
    private double latitude;
    private double longitude;
    private long time;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    public void setTime(long time) {
        this.time=time;
    }
}
