package com.loonggg.lib.alarmmanager.clock;

import android.app.Activity;
import android.app.Service;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ClockAlarmActivity extends Activity {

    private int flag;
    private  String message;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private AnimationDrawable anim;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private TextView alarmtime;
    private TextView tv_am;
    private boolean is24Hour;
    private TextView colormessage;
    private TextView colormessage1;

    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);
        tv_time.setText(str4 + ":" + str5);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
       message = this.getIntent().getStringExtra("msg");


       flag = this.getIntent().getIntExtra("flag", 0);
        is24Hour = DateFormat.is24HourFormat(this);
        tv_time= (TextView) findViewById(R.id.tv_time);
        alarmtime = findViewById(R.id.alarm_time);
        tv_am = findViewById(R.id.tv_am);

        colormessage1 = findViewById(R.id.message);

        colormessage1.setText(message);



        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();
        Date date=new Date();
        SimpleDateFormat HH=new SimpleDateFormat("HH");
        SimpleDateFormat mm=new SimpleDateFormat("mm");
        String   timeHH= HH.format(date);
        String   timemm= mm.format(date);
        if (is24Hour) {
            tv_am.setText("");
            tv_time.setText(timeHH + ":" + timemm);
        } else {
            int a = Integer.valueOf(timeHH);
            int cc;
            if (a >= 12) {
                tv_am.setText("PM");
                cc = a - 12;
            } else {
                tv_am.setText("AM");
                cc = a;
            }
            if (cc <= 9)
                timeHH = "0" + cc;
            else
                timeHH = "" + cc;
            alarmtime.setText(timeHH + ":" + timemm);
        }


        ImageView alarm =(ImageView) findViewById(R.id.iamgeview_alarm);
        alarm.setBackgroundResource(R.drawable.alarmclock);
        anim = (AnimationDrawable) alarm.getBackground();
        anim.start();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flag == 1 || flag == 2) {
            mediaPlayer = MediaPlayer.create(this, R.raw.in_call_alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        if (flag == 0 || flag == 2) {
            vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
        }


        if (flag == 1 || flag == 2) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (flag == 0 || flag == 2) {
            vibrator.cancel();
        }

        Toast.makeText(this, "闹钟关闭", Toast.LENGTH_SHORT).show();

        finish();

        return super.onKeyDown(keyCode, event);
    }
}
