package com.wite.positionerwear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wite.positionerwear.model.MissCallInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MissedcallActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private int result;
    private List<MissCallInfo> list;
    private List<MissCallInfo> newlist;

    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);
        tv_time.setText(str4 + ":" + str5);
    }

    private RelativeLayout FocusView;
    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missedcall);
        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        int spacingInPixels = 10;

        if (readMissCall() > 0) {
            list = getMissCallinfo();


            //发送广播有新的未接电话
            Intent addmissdcall = new Intent();
            addmissdcall.setAction("com.wite.positionerwear.addmissdcall");
            this.sendBroadcast(addmissdcall);


        } else {
            list = new ArrayList<>();

        }

        mAdapter = new MissedcallActivity.MyAdapter(list);


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setAdapter(mAdapter);
    }


    public class MyAdapter extends RecyclerView.Adapter<MissedcallActivity.MyAdapter.ViewHolder> {


        public List<MissCallInfo> datas = null;

        public MyAdapter(List<MissCallInfo> datas) {
            this.datas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_missedcall, parent, false);
            MissedcallActivity.MyAdapter.ViewHolder vh = new MissedcallActivity.MyAdapter.ViewHolder(view);
            return vh;

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTextView.setText(datas.get(position).getCallName());
            holder.mTextViewletter.setText(datas.get(position).getCallName().substring(0, 1));
            holder.Phonenum.setText(datas.get(position).getCallNumber());

            //  SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            // Date curDate = new Date();//获取当前时间
            //  String time = formatter.format(curDate);
            String time = datas.get(position).getCallDate();

            holder.mTextViewtime.setText(time.substring(time.length() - 5, time.length()));
            //有问题 "yyyy-MM-dd" "y"小写
            //  SimpleDateFormat Dateformatter = new SimpleDateFormat("yyyy-MM-dd");
            //  Date getDate = new Date(datas.get(position).getCallDate());//获取当前时间
            // String date = Dateformatter.format(getDate);

//            SimpleDateFormat test = new SimpleDateFormat("YYYY-MM-dd");
//            Date test1 = new Date(System.currentTimeMillis());//获取当前时间
//            String test2 = test.format(test1);
//
            holder.mTextViewdate.setText(time.substring(0, 10));
//            //有问题


            //设置角标
            holder.mTextViewcorner.setVisibility(View.GONE);


            holder.onclick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        FocusView = holder.onclick;
                    } else {


                    }

                }
            });


            switch (position % 4) {
                case 0:

                    holder.relativelayout.setBackgroundResource(R.drawable.hendcolor1);

                    break;
                case 1:
                    holder.relativelayout.setBackgroundResource(R.drawable.hendcolor2);
                    break;
                case 2:
                    holder.relativelayout.setBackgroundResource(R.drawable.hendcolor3);
                    break;
                case 3:
                    holder.relativelayout.setBackgroundResource(R.drawable.hendcolor4);
                    break;
                default:
                    holder.relativelayout.setBackgroundResource(R.drawable.hendcolor1);
                    break;

            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            public TextView mTextView;
            public TextView mTextViewletter;
            public RelativeLayout relativelayout;

            public TextView mTextViewtime;
            public TextView mTextViewcorner;
            public TextView mTextViewdate;
            public RelativeLayout onclick;
            public TextView Phonenum;
            public ViewHolder(View itemView) {


                super(itemView);


                mTextView = (TextView) itemView.findViewById(R.id.item_textview);
                mTextViewletter = itemView.findViewById(R.id.item_textview_letter);
                relativelayout = itemView.findViewById(R.id.item_rl_0);
                mTextViewtime = itemView.findViewById(R.id.textview_time);
                mTextViewcorner = itemView.findViewById(R.id.item_corner);
                mTextViewdate = itemView.findViewById(R.id.textview_date);
                onclick = itemView.findViewById(R.id.item_rl_onclick);

                Phonenum = itemView.findViewById(R.id.phonenum);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //关闭当前activity
            finish();

        }

        if (keyCode == KeyEvent.KEYCODE_POWER) {

            TextView textview = FocusView.findViewById(R.id.phonenum);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + textview.getText()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true;
            }
            startActivity(intent);

        }

        if (keyCode == KeyEvent.KEYCODE_4) {
            finish();

        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }


    private int readMissCall() {
        result = 0;
// Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
// new String[] { Calls.TYPE }, " type=? and new=?",
// new String[] { Calls.MISSED_TYPE + "", "1" }, "date desc");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.TYPE}, " type=?",
                new String[]{CallLog.Calls.MISSED_TYPE + ""}, "date desc");
        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }
        return result;
    }

    //获取未接来电
    private List<MissCallInfo> getMissCallinfo() {

        MissCallInfo missCallInfo;

        List<MissCallInfo> listcallinfo = new ArrayList<>();
        listcallinfo.clear();

        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs, sortOrder);

            while (cursor.moveToNext()) {

                missCallInfo = new MissCallInfo();
                missCallInfo.setCallNumber(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));

                if (cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)) == null) {
                    missCallInfo.setCallName(missCallInfo.getCallNumber());
                } else {
                    missCallInfo.setCallName(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)));
                }


                missCallInfo.setCallLogID(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID)));


                //需要对时间进行一定的处理
                String callDate = cursor.getString(cursor
                        .getColumnIndex(android.provider.CallLog.Calls.DATE));
                long callTime = Long.parseLong(callDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                callDate = sdf.format(new Date(callTime));
                missCallInfo.setCallDate(callDate);

                missCallInfo.setCallType(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE)));

                missCallInfo.setIsCallNew(cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW)));
//                    if (Integer.parseInt(callType) == (CallLog.Calls.MISSED_TYPE)
//                            && Integer.parseInt(isCallNew) > 0)  //通过call.new进行了限定，会对读取有一些问题，要删掉该限定
                if (Integer.parseInt(missCallInfo.getCallType()) == (CallLog.Calls.MISSED_TYPE)) {
                    //textView.setText(callType+"|"+callDate+"|"+callNumber+"|");
                    //只是以最简单ListView显示联系人的一些数据----适配器的如何配
                    listcallinfo.add(missCallInfo);

                }
            }
        } catch (Exception e) {


            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return listcallinfo;
    }


}
