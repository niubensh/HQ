package com.wite.positionerwear.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.wite.positionerwear.model.GpsStatellite;
import com.wite.positionerwear.model.LocationModel;
import com.wite.positionerwear.model.StationInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

@TargetApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("NewApi")
public class LocationUtil {

    private static final String TAG = "LocationUtil";
    private static final List<GpsSatellite> TODO = null;

    private static LocationUtil instance;
    private static Activity mActivity;
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static Location location;

    public static LocationUtil getInstance(Activity activity) {
        mActivity = activity;
        if (instance == null) {
            instance = new LocationUtil();
        }
        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        return instance;
    }

    /**
     * 判断GPS导航是否打开.
     * false：弹窗提示打开,不建议采用在后台强行开启的方式。
     * true:不做任何处理
     *
     * @return
     */
    public static Boolean isOpenGPS() {

        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setMessage("GPS未打开，是否打开?");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    // 设置完成后返回到原来的界面  
                    mActivity.startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        return true;
        }
        return false;

    }

    /**
     * 通过LocationListener来获取Location信息
     */
    public void formListenerGetLocation() {

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //位置信息变化时触发  
                Log.e(TAG, "纬度：" + location.getLatitude());
                Log.e(TAG, "经度：" + location.getLongitude());
                Log.e(TAG, "海拔：" + location.getAltitude());
                Log.e(TAG, "时间：" + location.getTime());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //GPS状态变化时触发  

            }

            @Override
            public void onProviderEnabled(String provider) {
                //GPS禁用时触发  
            }

            @Override
            public void onProviderDisabled(String provider) {
                //GPS开启时触发   
            }
        };
        /**
         * 绑定监听 
         * 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种，前者是GPS,后者是GPRS以及WIFI定位 
         * 参数2，位置信息更新周期.单位是毫秒 
         * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息 
         * 参数4，监听 
         * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新 
         */

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(getProvider(), 2000, 1, locationListener);
    }


    public static LocationModel getLocation() {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new LocationModel();
        }
     //   location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    location = locationManager.getLastKnownLocation(getProvider());
        Log.e(TAG, "纬度：" + location.getLatitude());
        Log.e(TAG, "经度：" + location.getLongitude());
        Log.e(TAG, "海拔：" + location.getAltitude());
        Log.e(TAG, "时间：" + location.getTime());
        GPSFormatUtils mGpsFormatUtils=new GPSFormatUtils();
        LocationModel mLocationModel=new LocationModel();

        mLocationModel.setLongitude(mGpsFormatUtils.DDtoDMS_long(location.getLongitude()));
        mLocationModel.setLatitude(mGpsFormatUtils.DDtoDMS_photo(location.getLatitude()));

        Date date = new Date();
        date.setTime(Long.valueOf(location.getTime()));
       // Log.e(TAG, "speed是什么: " + speed);
        //速度
       // speed_str = speed + "";
         //location.getSpeed()+""
        mLocationModel.setSpeed(location.getSpeed()+"");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
       mLocationModel.setTime(simpleDateFormat.format(date));

   //     new LocationModel(""+location.getLatitude(), ""+location.getLongitude(),""+ location.getAltitude(), ""+location.getTime(),""+ location.getSpeed());
        return mLocationModel;

    }

    private static String getProvider() {
        // TODO Auto-generated method stub
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(true);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(true);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
        // provider
        return  locationManager.getBestProvider(criteria, true);
    }




    /**
     * 获取GPS状态监听，包括GPS启动、停止、第一次定位、卫星变化等事件。
     */
    public static void getStatusListener() {

        GpsStatus.Listener listener = new GpsStatus.Listener() {

            @Override
            public void onGpsStatusChanged(int event) {
                if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
                    //第一次定位  
                } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                    //卫星状态改变  

                    if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    GpsStatus gpsStauts = locationManager.getGpsStatus(null); // 取当前状态
                    int maxSatellites = gpsStauts.getMaxSatellites(); //获取卫星颗数的默认最大值  
                    Iterator<GpsSatellite> it = gpsStauts.getSatellites().iterator();//创建一个迭代器保存所有卫星
                    int count = 0;
                    while (it.hasNext() && count <= maxSatellites) {
                        count++;
                        GpsSatellite s = it.next();
                    }
                    Log.e(TAG, "搜索到：" + count + "颗卫星");
                } else if (event == GpsStatus.GPS_EVENT_STARTED) {
                    //定位启动  
                } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
                    //定位结束  
                }
            }
        };
        //绑定  
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(listener);
    }

    /**
     * 获取所有卫星状态
     *
     * @return
     */
    public static List<GpsSatellite> getGpsStatus() {
        List<GpsSatellite> result = new ArrayList<GpsSatellite>();


        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        GpsStatus gpsStatus = locationManager.getGpsStatus(null); // 取当前状态
        //获取默认最大卫星数  
        int maxSatellites = gpsStatus.getMaxSatellites();
        //获取第一次定位时间（启动到第一次定位）  
        int costTime = gpsStatus.getTimeToFirstFix();
        Log.e(TAG, "第一次定位时间:" + costTime);
        //获取卫星  
        Iterable<GpsSatellite> iterable = gpsStatus.getSatellites();
        //一般再次转换成Iterator  
        Iterator<GpsSatellite> itrator = iterable.iterator();
        int count = 0;
        while (itrator.hasNext() && count <= maxSatellites) {
            count++;

            GpsSatellite s = itrator.next();
            result.add(s);
        }
        return result;
    }

    /**
     * 某一个卫星的信息.
     *
     * @param gpssatellite
     */
    public static GpsStatellite getGpsStatelliteInfo(GpsSatellite gpssatellite) {

        //卫星的方位角，浮点型数据    
        Log.e(TAG, "卫星的方位角：" + gpssatellite.getAzimuth());
        //卫星的高度，浮点型数据    
        Log.e(TAG, "卫星的高度：" + gpssatellite.getElevation());
        //卫星的伪随机噪声码，整形数据    
        Log.e(TAG, "卫星的伪随机噪声码：" + gpssatellite.getPrn());
        //卫星的信噪比，浮点型数据    
        Log.e(TAG, "卫星的信噪比：" + gpssatellite.getSnr());
        //卫星是否有年历表，布尔型数据    
        Log.e(TAG, "卫星是否有年历表：" + gpssatellite.hasAlmanac());
        //卫星是否有星历表，布尔型数据    
        Log.e(TAG, "卫星是否有星历表：" + gpssatellite.hasEphemeris());
        //卫星是否被用于近期的GPS修正计算    
        Log.e(TAG, "卫星是否被用于近期的GPS修正计算：" + gpssatellite.hasAlmanac());

  return new GpsStatellite(gpssatellite.getAzimuth(), gpssatellite.getElevation(),gpssatellite.getPrn(),gpssatellite.getSnr(),gpssatellite.hasAlmanac(),gpssatellite.hasEphemeris(),gpssatellite.hasAlmanac());
    }

    public static StationInfo getCellInfo() {
        StationInfo stationInfo = new StationInfo();

        /** 调用API获取基站信息 */
        TelephonyManager telephonyManager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);

        if (!hasSimCard(mActivity)){ //判断有没有sim卡，如果没有安装sim卡下面则会异常
            Toast.makeText(mActivity,"请安装sim卡", Toast.LENGTH_LONG).show();
            return null;
        }
        String operator = telephonyManager.getNetworkOperator();
        Log.e("operator","operator="+operator);
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));


        int cid = 0;
        int lac = 0;
        if (mnc == 11 || mnc == 03 || mnc == 05){  //03 05 11 为电信CDMA
            CdmaCellLocation location = (CdmaCellLocation) telephonyManager.getCellLocation();
            //这里的值可根据接口需要的参数获取
            cid = location.getBaseStationId();
            lac = location.getNetworkId();
            mnc = location.getSystemId();
        } else {
            GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
            cid = location.getCid();
            lac = location.getLac();
        }

        /** 将获得的数据放到结构体中 */
        stationInfo.setMCC(mcc);
        stationInfo.setMNC(mnc);
        stationInfo.setLAC(lac);
        stationInfo.setCID(cid);

        return stationInfo;
    }

    public static boolean hasSimCard(Context mContext){
        TelephonyManager telMgr = (TelephonyManager)
                mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡 break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        Log.d(TAG, result ? "有SIM卡" : "无SIM卡");
        return result;
    }



}
