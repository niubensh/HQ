package com.wite.positionerwear;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wite.positionerwear.utils.AudioRecoderUtils;
import com.wite.positionerwear.utils.FileUploaderUtil;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordingActivity extends AppCompatActivity {

    private ImageView image;
    private ImageView send;
    //录音帮助类
    AudioRecoderUtils mAudioRecoderUtils;
    private static final   String TAG="TAG";
    private  String redingpath;


    //用于长按事件
    private boolean shortPress = false;
    private String imei;
    private Long starttime;
    private long stopttime;
    private int resultCode;
    private  boolean istrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        mAudioRecoderUtils=new AudioRecoderUtils();
        send = (ImageView) findViewById(R.id.iamge_send);
        send.setBackgroundResource(R.drawable.send);
        send.setVisibility(View.GONE);
        image = (ImageView) findViewById(R.id.iamge_animation);
        image.setBackgroundResource(R.drawable.soundrecording);
        AnimationDrawable anim = (AnimationDrawable) image.getBackground();
        anim.start();

        Date dt= new Date();
        starttime = dt.getTime();

        //获取iemi号
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //nox 有iemi号
        imei = TelephonyMgr.getDeviceId();


        mAudioRecoderUtils.startRecord();
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
                @Override
                public void onUpdate(double db, long time) {
                    Log.e(TAG, "录音中"+db+"时间"+time );
                }
                @Override
                public void onStop(String filePath) {
                    Log.e(TAG, "停止 文件保存位置"+filePath  );
                    redingpath=filePath;
                }
            });

        Intent mIntent = new Intent();
        mIntent.putExtra("istrue", istrue);

        // 设置结果，并进行传送
        resultCode=0;
        this.setResult(resultCode, mIntent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            shortPress = false;
            //长按要执行的代码
            Toast.makeText(this, "我是长按时事件ain", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            image.setVisibility(View.GONE);
            mAudioRecoderUtils.stopRecord();
            Log.e(TAG, "onClick: 停止录音" );
            send.setVisibility(View.VISIBLE);
            final AnimationDrawable animsend = (AnimationDrawable) send.getBackground();
            animsend.start();
            Date dt= new Date();
            stopttime = dt.getTime();

            /**
             * IMEI -> 设备 imei
             Long -> 语音时长
             Type -> 群聊或单聊 1 为群聊 2 为单聊
             Target -> 单聊目标对像id，群聊为空
             */
            final HashMap<String,String> map = new HashMap<String,String>();
            map.put("IMEI",imei);
            map.put("Long",String.valueOf(stopttime-starttime));
            map.put("Type","1");
            map.put("Target","");
            Log.e(TAG, "IMEI"+imei+"录音时长" +String.valueOf(stopttime-starttime)+"Type==1");
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        FileUploaderUtil.upload("http://iwapi.gpscar.cn/api/Files/PostFile", new File(redingpath), map, new FileUploaderUtil.FileUploadListener() {
                            @Override
                            public void onProgress(long pro, double precent) {
                                Log.e(TAG,  precent+"" );

                            }

                            @Override
                            public void onFinish(int code, String res, Map<String, List<String>> headers) {
                                Log.e(TAG,  "res="+res+"code="+code);
                                Log.e(TAG, "onFinish: 发送完成" );
                                finish();
                                istrue=true;
                            }
                        });
                        Log.e(TAG, "host:http://iwapi.gpscar.cn/api/Files/PostFile "+new File(redingpath).getPath().toString() );

                    }
                }.start();




            shortPress = false;
            return true;
        }


        return super.onKeyUp(keyCode, event);

    }

}
