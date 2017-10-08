package com.wite.positionerwear;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wite.positionerwear.DBHelper.MessageDBHelper;
import com.wite.positionerwear.model.MessageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MessageInfoActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    //设置时间
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private MessageDBHelper messageDBHelper;
    private Date mDate;

    private void refreshUI() {

        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");

        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);

        tv_time.setText(str4 + ":" + str5);

    }
  //定义数据模型
    private MessageModel mMessageModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();

        //接收Intent的值
        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "intent是空的！！！！！！！！: ");
        }

        String id = intent.getStringExtra("id");


        if (id == null) {
            Log.e(TAG, "id是空的！！！！！！！！连人都没找到！！ ");
        }

        messageDBHelper = new MessageDBHelper(MessageInfoActivity.this);

        String[] idarray=new String[]{id};
        Cursor messageCursor=  messageDBHelper.queryforid(idarray);




        while (messageCursor.moveToNext()){
            mMessageModel=new MessageModel();
            mMessageModel.setGuardianModel_id(messageCursor.getInt(messageCursor.getColumnIndex("id")));
            mMessageModel.setName(messageCursor.getString(messageCursor.getColumnIndex("name")));
            mMessageModel.setTextMessage(messageCursor.getString(messageCursor.getColumnIndex("textMessage")));
            mMessageModel.setMessageInTime(messageCursor.getString(messageCursor.getColumnIndex("messageintime")));
        }






        TextView textView = (TextView) findViewById(R.id.textmessage);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());



        textView.setText(mMessageModel.getTextMessage());

        //把letter设置为了name
        TextView letter = (TextView) findViewById(R.id.item_textview_letter);
        letter.setText(mMessageModel.getName().substring(0,1));


        SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            mDate = mSimpleDateFormat.parse(mMessageModel.getMessageInTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mDate!=null) {
            SimpleDateFormat timeSimpleDateFormat=new SimpleDateFormat("HH:mm");
          String strTime=  timeSimpleDateFormat.format(mDate);
            SimpleDateFormat dateSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            String strDate=  dateSimpleDateFormat.format(mDate);
            TextView time = (TextView) findViewById(R.id.textviewtime);
            time.setText(strTime);
            TextView date = (TextView) findViewById(R.id.textviewdate);
            date.setText(strDate);


        }

        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.item_rl_0);

        //弄个随机数
        Random random = new java.util.Random();// 定义随机类
        int result = random.nextInt(4);
        switch (result) {
            case 0:

                mRelativeLayout.setBackgroundResource(R.drawable.hendcolor1);

                break;
            case 1:
                mRelativeLayout.setBackgroundResource(R.drawable.hendcolor2);
                break;
            case 2:
                mRelativeLayout.setBackgroundResource(R.drawable.hendcolor3);
                break;
            case 3:
                mRelativeLayout.setBackgroundResource(R.drawable.hendcolor4);
                break;
            default:
                mRelativeLayout.setBackgroundResource(R.drawable.hendcolor1);
                break;
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //关闭当前activity
            finish();

        }
        if (keyCode == KeyEvent.KEYCODE_4) {
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
