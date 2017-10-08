package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/14.
 * 某一个卫星
 */

public class GpsStatellite {



    //卫星的方位角，浮点型数据
    private float Azimuth;
    //卫星的高度，浮点型数据
    private float Elevation;
    //卫星的伪随机噪声码，整形数据
    private int Prn;

    //卫星的信噪比，浮点型数据
    private float Snr;

    //卫星是否有年历表，布尔型数据
    private boolean hasAlmanacyear;
    //卫星是否有星历表，布尔型数据
    private boolean hasEphemeris;
    private boolean hasAlmanac;

    //卫星是否被用于近期的GPS修正计算
    //    Log.e(TAG, "卫星是否被用于近期的GPS修正计算：" + gpssatellite.hasAlmanac());


    public GpsStatellite() {
    }

    public GpsStatellite(float azimuth, float elevation, int prn, float snr, boolean hasAlmanacyear, boolean hasEphemeris, boolean hasAlmanac) {
        Azimuth = azimuth;
        Elevation = elevation;
        Prn = prn;
        Snr = snr;
        this.hasAlmanacyear = hasAlmanacyear;
        this.hasEphemeris = hasEphemeris;
        this.hasAlmanac = hasAlmanac;
    }

    public float getAzimuth() {
        return Azimuth;
    }

    public void setAzimuth(float azimuth) {
        Azimuth = azimuth;
    }

    public float getElevation() {
        return Elevation;
    }

    public void setElevation(float elevation) {
        Elevation = elevation;
    }

    public int getPrn() {
        return Prn;
    }

    public void setPrn(int prn) {
        Prn = prn;
    }

    public float getSnr() {
        return Snr;
    }

    public void setSnr(float snr) {
        Snr = snr;
    }

    public boolean isHasAlmanacyear() {
        return hasAlmanacyear;
    }

    public void setHasAlmanacyear(boolean hasAlmanacyear) {
        this.hasAlmanacyear = hasAlmanacyear;
    }

    public boolean isHasEphemeris() {
        return hasEphemeris;
    }

    public void setHasEphemeris(boolean hasEphemeris) {
        this.hasEphemeris = hasEphemeris;
    }

    public boolean isHasAlmanac() {
        return hasAlmanac;
    }

    public void setHasAlmanac(boolean hasAlmanac) {
        this.hasAlmanac = hasAlmanac;
    }
}
