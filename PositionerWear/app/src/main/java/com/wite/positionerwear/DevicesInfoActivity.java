package com.wite.positionerwear;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DevicesInfoActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private String imei;
    private String sn;
    private static final String TAG = "DevicesInfoActivity";
    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");

        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);

        tv_time.setText(str4+":"+str5);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicesinfo);

        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();

        TextView textView = (TextView) findViewById(R.id.textmessage);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//autoSplitText
     //   final String s = textView.autoSplitText();
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //nox 有iemi号
        imei = TelephonyMgr.getDeviceId();
        Log.d(TAG, "++++++++++++++++++++++++onCreate:  设备IMEI号码" + imei);
        sn = TelephonyMgr.getSimSerialNumber();
        Log.d(TAG, "++++++++++++++++++++++++onCreate:  设备SN号码" + sn);
        //textView.autoSplitText

        String info="IMEI:"+imei+"\n"+"SN:"+sn;
        info.replace("\\n", "\n");
        textView.setText(info);





    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {




        if (keyCode == KeyEvent.KEYCODE_4) {
            finish();

        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

        }


        return super.onKeyDown(keyCode, event);
    }

}
