package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/14.
 */

public class LocationModel {



    //"纬度："
    private String Latitude;
    // "经度："
    private String Longitude;
    //"海拔："
    private String Altitude;
    //"时间："
    private String Time;
    //速度
    private String Speed;


    public LocationModel() {
    }

    public LocationModel(String latitude, String longitude, String altitude, String time, String speed) {
        Latitude = latitude;
        Longitude = longitude;
        Altitude = altitude;
        Time = time;
        Speed = speed;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }
}
