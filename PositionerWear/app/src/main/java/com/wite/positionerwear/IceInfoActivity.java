package com.wite.positionerwear;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.wite.positionerwear.model.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IceInfoActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private static final String TAG = "TAG";

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
        setContentView(R.layout.activity_ice_info);

        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();


        SharedPreferences mSharedPreferences = this.getSharedPreferences("User", MODE_PRIVATE);

        UserModel mUserModel = new UserModel();

        mUserModel.setName(mSharedPreferences.getString("Name", "张三"));
        mUserModel.setGender(mSharedPreferences.getString("Gender", "男"));
        mUserModel.setBirthday(mSharedPreferences.getString("Birthday", "19800101"));
        mUserModel.setBlood(mSharedPreferences.getString("Blood", "O"));
        mUserModel.setMedical(mSharedPreferences.getString("Medical", "病史"));
        mUserModel.setDrug(mSharedPreferences.getString("Drug", "药物"));
        mUserModel.setAnaphylaxis(mSharedPreferences.getString("Anaphylaxis", "过敏"));
        mUserModel.setEmergency(mSharedPreferences.getString("Emergency", "紧急联系人"));


        TextView textView = (TextView) findViewById(R.id.textmessage);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        String text = "Name:#name\nGender:#gender\nAge:#age\nBirthdate:#birthdate\nBlood type:#bloodtype\nMedical condition:\n#medical\nAnaphylaxis:#anap\nDrug user:#drug";


        Date lowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//小写的mm表示的是分钟
        String dstr = mUserModel.getBirthday();
        try {
            lowDate = sdf.parse(dstr);
        } catch (ParseException e) {
            e.printStackTrace();

            Log.d(TAG, "时间转换失败 IceInfoActivity");

        }
        Date newDate = new Date();
        int age = newDate.getYear() - lowDate.getYear();


        String ss = text.replaceAll("\\n", "\n")
                .replaceAll("#name", mUserModel.getName())
                .replaceAll("#gender", mUserModel.getGender())
                .replaceAll("#age", age + "")
                .replaceAll("#birthdate", mUserModel.getBirthday())
                .replaceAll("#bloodtype", mUserModel.getBlood())
                .replaceAll("#medical", mUserModel.getMedical())
                .replaceAll("#anap", mUserModel.getAnaphylaxis())
                .replaceAll("#drug", mUserModel.getDrug());
        textView.setText(ss);

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
