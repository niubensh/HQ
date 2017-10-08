package com.wite.positionerwear.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/9/13.
 */

//需要在源码中编译
    //android:sharedUserId="android.uid.system"

public class SetSystem {



    //音量控制模式
    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    public static final int RINGER_MODE_NORMAL = 2;

   public static void setDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }

    public  static void setTime(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }




/**AudioManager.STREAM_MUSIC /音乐回放即媒体音量/

 AudioManager.STREAM_RING /铃声/

 AudioManager.STREAM_ALARM /警报/

 AudioManager.STREAM_NOTIFICATION /窗口顶部状态栏通知声/

 AudioManager.STREAM_SYSTEM /系统/

 AudioManager.STREAM_VOICECALL /通话 /

 AudioManager.STREAM_DTMF /双音多频,不是很明白什么东西 /
 *
 * 设置系统音量
 * */
    public void setStreamVolume (Activity mActivity, int streamType, int flags){
        mActivity.setVolumeControlStream(streamType);
        AudioManager am=(AudioManager) mActivity.getSystemService(mActivity.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI );
        am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的最大值
        am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的当前值
    }


    public void shutdown(){
    Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
    intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

}

    //音量控制
   // private AudioManager audio;
 //设置禁音
public  void toMute(AudioManager audio){
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
 //设置正常
   public  void toNormal(AudioManager audio){
    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    audio.setStreamVolume(AudioManager.RINGER_MODE_NORMAL,50, 0);
    }
    /**设置震动
   public  void toshock(AudioManager audio){
       int vibrate_setting = -1;
       int ring_mode = -1;
       switch(profile.vibrate) {
           case ProfileConstants.VIBRATE_ALWAYS_ON:// 总是振动
               vibrate_setting = AudioManager.VIBRATE_SETTING_ON;
               ring_mode = 1;
               break;
           case ProfileConstants.VIBRATE_NEVER://重不振动
               vibrate_setting = AudioManager.VIBRATE_SETTING_OFF;
               ring_mode = 0;
               break;
           case ProfileConstants.VIBRATE_ONLY_IN_SILENT://静音下振动
               vibrate_setting = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
               ring_mode = 1;
               break;
           case ProfileConstants.VIBRATE_UNLESS_SILENT://非静音下振动
               vibrate_setting = AudioManager.VIBRATE_SETTING_ON;
               ring_mode = 0;
               break;
       }
       Settings.System.putInt(resolver, "vibrate_in_silent", ring_mode);
       audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, vibrate_setting);
    }
   */

}
