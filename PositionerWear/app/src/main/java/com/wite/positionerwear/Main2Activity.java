package com.wite.positionerwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wite.positionerwear.service.BackgroundService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private TextView tv_date;
    private TextView tv_month;
    private TextView tv_am;
    private boolean is24Hour;
    private ImageView battery;
    private ImageView phonestate;

    //用于长按事件
    private boolean shortPress = false;
    private AlertDialog.Builder builder;
    private Boolean iscall = false;
    private AlertDialog dialog;
    private ImageView nosim;

    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
        String str1 = sdf0.format(date);
        String str2 = sdf1.format(date);
        String str3 = sdf2.format(date);
        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);

        String month = null;
        tv_date.setText(str1 + "-" + str2 + "-" + str3);
        if (is24Hour) {
            tv_am.setText("");
            tv_time.setText(str4 + ":" + str5);
        } else {
            int a = Integer.valueOf(str4);
            int cc;
            if (a >= 12) {
                tv_am.setText("PM");
                cc = a - 12;
            } else {
                tv_am.setText("AM");
                cc = a;
            }
            if (cc <= 9)
                str4 = "0" + cc;
            else
                str4 = "" + cc;
            tv_time.setText(str4 + ":" + str5);
        }
        switch (Integer.parseInt(str2)) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }
        tv_month.setText(month);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        is24Hour = DateFormat.is24HourFormat(this);
        tv_time = (TextView) findViewById(R.id.time);
        tv_date = (TextView) findViewById(R.id.date);
        tv_month = (TextView) findViewById(R.id.month);
        tv_am = (TextView) findViewById(R.id.tv_am);

        //电池
        battery = (ImageView) findViewById(R.id.imageView3);
        //信号

        phonestate = (ImageView) findViewById(R.id.signal);
        nosim = (ImageView) findViewById(R.id.nosim);


        //获取信号质量
        TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (telephoneManager.getSimSerialNumber() == null || telephoneManager.getSimSerialNumber().equals("")) {

            Toast.makeText(this, "对不起请插入SIM卡", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "对不起请插入SIM卡");

            nosim.setImageResource(R.drawable.nosim);
            phonestate.setVisibility(View.GONE);

        }

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            //   onSignalStrengthsChanged

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                StringBuffer sb = new StringBuffer();
                //  String strength = String.valueOf(signalStrength.getGsmSignalStrength());
                Integer strength = signalStrength.getGsmSignalStrength();
                if (strength != null) {
                    phonestate.setBackground(null);
                }

                nosim.setVisibility(View.GONE);
                phonestate.setVisibility(View.VISIBLE);


                if (strength >= 10) {
                    phonestate.setImageResource(R.drawable.signal5);
                } else if (strength < 10 && strength > 8) {
                    phonestate.setImageResource(R.drawable.signal4);
                } else if (strength < 8 && strength > 6) {
                    phonestate.setImageResource(R.drawable.signal3);
                } else if (strength < 6 && strength > 4) {
                    phonestate.setImageResource(R.drawable.signal2);
                } else if (strength < 4 && strength > 2) {
                    phonestate.setImageResource(R.drawable.signal1);
                } else if (strength < 0 && strength > 0) {
                    phonestate.setImageResource(R.drawable.signal0);
                }


            }
        };

        telephoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        final int type = telephoneManager.getNetworkType();


        IntentFilter filter = new IntentFilter();
        //电池电量广播
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        filter.addAction("com.wite.positionerwear.addmochat");

        filter.addAction("com.wite.positionerwear.addmessage");

        filter.addAction("android.intent.action.PHONE_STATE");


        registerReceiver(myReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();


    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        private  int lastCallState = TelephonyManager.CALL_STATE_IDLE;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == Intent.ACTION_BATTERY_CHANGED) {
                final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN); /* boolean isCharging = false;*/
                boolean mCharged = (status == BatteryManager.BATTERY_STATUS_FULL);
                boolean isCharging = (mCharged || status == BatteryManager.BATTERY_STATUS_CHARGING);
                int level = (int) (100f * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100));

                int test = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (test == BatteryManager.BATTERY_STATUS_CHARGING) {
                    Log.e(TAG, "----------------正在充电中-----------------------");

                    battery.setImageResource(R.drawable.inbattery);

                } else {
                    Log.e(TAG, "----------------没有检测 显示当前电量-----------------------");
                    if (level == 100) {
                        battery.setImageResource(R.drawable.electricity5);
                    } else if (level < 100 && level > 80) {
                        battery.setImageResource(R.drawable.electricity4);
                    } else if (level < 80 && level > 60) {
                        battery.setImageResource(R.drawable.electricity3);
                    } else if (level < 60 && level > 40) {
                        battery.setImageResource(R.drawable.electricity2);
                    } else if (level < 40 && level > 20) {
                        battery.setImageResource(R.drawable.electricity1);
                    } else if (level < 20 && level > 0) {
                        battery.setImageResource(R.drawable.electricity0);
                    }


                }


            }




            if (intent.getAction()=="android.intent.action.PHONE_STATE") {
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                int currentCallState = telephonyManager.getCallState();
                Log.d("PhoneStateReceiver", "currentCallState=" + currentCallState );
                if (currentCallState == TelephonyManager.CALL_STATE_IDLE) {// 空闲
                    //TODO
                } else if (currentCallState == TelephonyManager.CALL_STATE_RINGING) {// 响铃
                    //TODO
                } else if (currentCallState == TelephonyManager.CALL_STATE_OFFHOOK) {// 接听
                    //TODO
                }

                if(lastCallState == TelephonyManager.CALL_STATE_RINGING && currentCallState == TelephonyManager.CALL_STATE_IDLE){

                    Toast.makeText(context, "有未接来电！！！！！！！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Main2Activity.this, MenuActivity.class));
                }
                lastCallState = currentCallState;
            }




            //如果有新消息 返回主页面
            if (intent.getAction() == "com.wite.positionerwear.addmochat" || intent.getAction() == "com.wite.positionerwear.addmessage" ) {

                startActivity(new Intent(Main2Activity.this, MainActivity.class));

            }




        }
    };


    //监听按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_POWER) {
            //可能有问题！
            startActivity(new Intent(Main2Activity.this, MenuActivity.class));


        }
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//
//            startActivity(new Intent(this, AlarmClockActivity.class));
//        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

            startActivity(new Intent(this, MenuActivity.class));


        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {


            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                event.startTracking();
                if (event.getRepeatCount() == 0) {
                    shortPress = true;

                }
                return true;
            }
            if (dialog != null) {
                dialog.dismiss();
                iscall = false;
            }
            finish();

        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

            startActivity(new Intent(this, MenuActivity.class));


        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //不允许返回主界面
            //先注释掉
           // return true;


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Toast.makeText(this, "长按BACK键", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "长按BACK键-----------------------------");
            iscall = true;
            shortPress = false;
            //长按要执行的代码
            builder = new AlertDialog.Builder(Main2Activity.this);
            builder.setTitle("紧急呼叫");
            builder.setMessage("即将进入紧急呼叫流程！按BACK键退出！");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Main2Activity.this, "取消紧急呼叫！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Main2Activity.this, "开始紧急呼叫！", Toast.LENGTH_SHORT).show();

                }
            });
            dialog = builder.create();
            dialog.show();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(5000);
                    if (iscall) {

//                sosCall();
//                sosSendSms();d

                        dialog.dismiss();
                        Toast.makeText(Main2Activity.this, "开始紧急呼叫！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Main2Activity.this, BackgroundService.class);
                        startService(intent);
                        Intent intentbord = new Intent();
                        //设置intent的动作为com.example.broadcast，可以任意定义
                        intentbord.setAction("android.intent.action.SOS_LONG_PRESS");
                        //发送无序广播
                        sendBroadcast(intentbord);
                        //      sosCall();
                        //    sosSendSms();

                        iscall = true;
                    } else {
                        iscall = true;

                    }


                }
            }).start();


            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (shortPress) {
            } else {
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(myReceiver);
    }
}


