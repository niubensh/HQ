package com.wite.positionerwear;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmClockActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private AnimationDrawable anim;
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
        setContentView(R.layout.activity_alarm_clock);

        tv_time= (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();
        ImageView alarm =(ImageView) findViewById(R.id.iamgeview_alarm);
        alarm.setBackgroundResource(R.drawable.alarmclock);
        anim = (AnimationDrawable) alarm.getBackground();
        anim.start();

    }
}
