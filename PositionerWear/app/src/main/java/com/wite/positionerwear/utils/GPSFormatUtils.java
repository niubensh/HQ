package com.wite.positionerwear.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/9/13.
 */

public class GPSFormatUtils {
    private static final String TAG = "TAG";
    /**
     * 功能：         度-->度分秒
     * @param d 传入待转化格式的经度或者纬度
     */
    public  void DDtoDMS(Double d){
        String[] array=d.toString().split("[.]");
        String degrees=array[0];//得到度
        Double m= Double.parseDouble("0."+array[1])*60;
        String[] array1=m.toString().split("[.]");
        String minutes=array1[0];//得到分
        Double s= Double.parseDouble("0."+array1[1])*60;
        String[] array2=s.toString().split("[.]");
        String seconds=array2[0];//得到秒
        Log.e(TAG, "DDtoDMS: "+degrees+"  "+minutes+"  "+seconds );
    }

    /**
     * 功能：  度-->度分秒（满足图片格式）
     * @param d   传入待转化格式的经度或者纬度
     * @return
     */
    public String DDtoDMS_photo(Double d){

        String[] array=d.toString().split("[.]");
        String D;
        if(array[0].length()<2){
           D="0"+array[0];

        }
      D=array[0].substring(0,2);//得到度

        Double m= Double.parseDouble("0."+array[1])*60;
        String[] array1=m.toString().split("[.]");

        String M;
        if(array1[0].length()<2){
            M="0"+array1[0];//得到分

        }else{
            M=array1[0].substring(0,2);//得到分

        }



        Double s= Double.parseDouble("0."+array1[1])*60*10000;
        String[] array2=s.toString().split("[.]");
        String S=array2[0];//得到秒
        Log.e(TAG, "DDtoDMS_photo: "+D+"/1,"+M+"/1,"+S+"/10000" );
      String newS=S.substring(0,4);

        return  D+M+"."+newS;
    }
    public String DDtoDMS_long(Double d){

        String[] array=d.toString().split("[.]");
        String D=array[0].substring(0,3);//得到度

        Double m= Double.parseDouble("0."+array[1])*60;
        String[] array1=m.toString().split("[.]");
        String M;
        if(array1[0].length()<2){
            M="0"+array1[0];//得到分

        }else{
            M=array1[0].substring(0,2);//得到分

        }


        Double s= Double.parseDouble("0."+array1[1])*60*10000;
        String[] array2=s.toString().split("[.]");

        String S=array2[0];//得到秒

        String array3=S.substring(0,4);

      //  String array4=S.substring(1,5);
        Log.e(TAG, "DDtoDMS_long: array3-----------"+array3 );
//        Log.e(TAG, "DDtoDMS_long:array4-----------"+array4 );
//        Log.e(TAG, "DDtoDMS_long:  D+M+array3+array4-----------"+ D+M+array3+"."+array4 );
//        array3


        return  D+M+"."+array3;
    }
}
