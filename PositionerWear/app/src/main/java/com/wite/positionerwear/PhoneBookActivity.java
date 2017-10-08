package com.wite.positionerwear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkrace.orderlibrary.OrderUtil;
import com.wite.positionerwear.model.PhoneUser;
import com.wite.positionerwear.DBHelper.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Ctrl+Shift+J 秘籍
public class PhoneBookActivity extends AppCompatActivity {
    private static final String TAG = "TAG";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 0:

                    break;
                case 1:

                    break;

                default:
                    Toast.makeText(PhoneBookActivity.this, "滚", Toast.LENGTH_SHORT).show();
                    break;

            }


            refreshUI();
        }
    };
    private TextView tv_time;
    private List<PhoneUser> lists;

    private void refreshUI() {

        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");

        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);

        tv_time.setText(str4 + ":" + str5);

    }

    private RelativeLayout FocusView;
    private int ItemPosion = 0;
    private RecyclerView mRecyclerView;

    private List<PhoneUser> mDatas;

    private RecyclerView.Adapter mAdapter;
    private View view;
    private LinearLayoutManager move;

    //定义数据库帮助类
    private DBHelper dbHelper;
    PhoneUser mPhoneUser;

    //单例模式
    final OrderUtil orderUtil = OrderUtil.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);

        tv_time = (TextView) findViewById(R.id.tv_time);

        lists = initData();

        if (lists.size() > 0) {
            Log.e(TAG, "初始化数据成功 ");
        } else {
            Log.e(TAG, "初始化数据是空的 ");
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LayoutManager layoutManager = new LinearLayoutManager(this);
        move = (LinearLayoutManager) layoutManager;

        // layoutManager.onInterceptFocusSearch(mRecyclerView,ItemPosion);


        //   layoutManager.onInterceptFocusSearch();

        int spacingInPixels = 10;


        new Thread(new Runnable() {
            @Override
            public void run() {


                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());

                // mAdapter = new MyAdapter(initData());

            }
        }).start();


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mAdapter = new MyAdapter(lists);

        mRecyclerView.setAdapter(mAdapter);

        Intent mIntent = new Intent();
//        TextView result = (TextView) FocusView.findViewById(R.id.phonenum);
//        mIntent.putExtra("number", result.getText());
//        // 设置结果，并进行传送
//        this.setResult(0, mIntent);

    }

    protected List<PhoneUser> initData() {
        dbHelper = new DBHelper(PhoneBookActivity.this, "phone", 1);
        List<PhoneUser> list = new ArrayList<>(); /*调用query()获取Cursor*/
        Cursor c = dbHelper.query();
        while (c.moveToNext()) {
            PhoneUser p = new PhoneUser();
            p.set_id(c.getInt(c.getColumnIndex("_id")));
            p.setName(c.getString(c.getColumnIndex("name")));
            p.setPhonenum(c.getString(c.getColumnIndex("phonenum")));
            p.setIntime(c.getString(c.getColumnIndex("inttime")));
            p.setLetter(c.getString(c.getColumnIndex("letter")));
            list.add(p);
        }
        mDatas = list;
        for (PhoneUser phone : list) {

            Log.e(TAG, "PhoneBook+++++++++++++++++LocationService.BP52:数据库的数据******************" + phone.getName() + phone.getPhonenum());


        }
        return mDatas;


    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        public List<PhoneUser> datas = null;


        public MyAdapter(List<PhoneUser> datas) {
            this.datas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phonebook, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextView.setText(datas.get(position).getName());
            holder.mTextViewletter.setText(datas.get(position).getName().substring(0, 1));
            holder.Phonenum.setText(datas.get(position).getPhonenum());
            holder.onclick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        FocusView = holder.onclick;
                    } else {

                    }
                }
            });
            holder.onclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(PhoneBookActivity.this, "电话号码是" + datas.get(position).getPhonenum(), Toast.LENGTH_SHORT).show();


                    Log.e(TAG, "电话号码是" + datas.get(position).getPhonenum());
                    //    String number = datas.get(position).getPhonenum();
                    //  Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //    startActivity(intent);

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
            public TextView Phonenum;
            public RelativeLayout relativelayout;
            public RelativeLayout onclick;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.item_textview);
                mTextViewletter = view.findViewById(R.id.item_textview_letter);
                relativelayout = view.findViewById(R.id.item_rl_0);
                onclick = view.findViewById(R.id.item_rl_onclick);

                Phonenum = view.findViewById(R.id.phonenum);
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
}