package com.wite.positionerwear;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wite.positionerwear.DBHelper.MessageDBHelper;
import com.wite.positionerwear.model.MessageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;
    private MessageDBHelper messageDBHelper;
    private static final String TAG="TAG";


    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);
        tv_time.setText(str4 + ":" + str5);
    }

    private RelativeLayout FocusView;
    //封装联系人对象

   private MessageModel mMessageModel;





    private RecyclerView mRecyclerView;
    private List<MessageModel> mDatas;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        tv_time = (TextView) findViewById(R.id.tv_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        int spacingInPixels = 10;
        mAdapter = new MyAdapter(initData());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setAdapter(mAdapter);


    }


    protected List<MessageModel> initData() {
        mDatas = new ArrayList<MessageModel>();

      messageDBHelper = new MessageDBHelper(MessageActivity.this);
//
//
//        /**------------测试数据----------------------*/
    //    messageDBHelper.celer();
//        ContentValues test=new ContentValues();
//
//        //IWBPCD,6654111,D3590D54,080835,3,XXXXXXXXXXXXXXXX#
//        test.put("id",6654111);
//
//        test.put("name", UnicodeUtil.UNstringToUnicode("D3590D54"));
//        test.put("textMessage","这是一条来自程序员的信息  你完了你完了你完了！");
//        Date inDate=new Date();
//        //注意时间格式
//        SimpleDateFormat intime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        test.put("messageintime",  intime.format(inDate));
//
//        messageDBHelper.insert(test);






        /**------------测试数据----------------------*/






        Cursor messageCursor=  messageDBHelper.query();
        List<MessageModel> messageModelList=new ArrayList<>();
        while (messageCursor.moveToNext()){
            mMessageModel=new MessageModel();
            mMessageModel.setGuardianModel_id(messageCursor.getInt(messageCursor.getColumnIndex("id")));
            mMessageModel.setName(messageCursor.getString(messageCursor.getColumnIndex("name")));
            mMessageModel.setTextMessage(messageCursor.getString(messageCursor.getColumnIndex("textMessage")));
            mMessageModel.setMessageInTime(messageCursor.getString(messageCursor.getColumnIndex("messageintime")));
            messageModelList.add(mMessageModel);
        }

        mDatas=messageModelList;
        return mDatas;

    }



    @Override
    public void onClick(View view) {
    }

    public class MyAdapter extends RecyclerView.Adapter<MessageActivity.MyAdapter.ViewHolder> {
        public List<MessageModel> datas = null;

        public MyAdapter(List<MessageModel> datas) {
            this.datas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTextView.setText(datas.get(position).getName());
            holder.mTextViewletter.setText(datas.get(position).getName().substring(0,1));
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
          //转换时间

            Log.d(TAG, "你说我时间错了 我看一下 " +datas.get(position).getMessageInTime().toString());
            String str = null;
            try {
                Date cc = formatter.parse(datas.get(position).getMessageInTime());
                str=cc.getHours()+":"+cc.getMinutes();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.mTextViewtime.setText(str);
            holder.mTextViewcorner.setText(position + "");
          //设置一个id
           holder.Guardian_id.setText(datas.get(position).getGuardianModel_id()+"");

            holder.onclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView id=FocusView.findViewById(R.id.Guardian_id);
                    Intent intent = new Intent(MessageActivity.this, MessageInfoActivity.class);
                    intent.putExtra("id", id.getText());
                    startActivity(intent);
                }
            });
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
            public TextView  Guardian_id;

            public RelativeLayout onclick;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.item_textview);
                mTextViewletter = itemView.findViewById(R.id.item_textview_letter);
                relativelayout = itemView.findViewById(R.id.item_rl_0);

                mTextViewtime = itemView.findViewById(R.id.item_textview_time);

                mTextViewcorner = itemView.findViewById(R.id.item_corner);

                onclick = itemView.findViewById(R.id.item_rl_onclick);
                Guardian_id=itemView.findViewById(R.id.Guardian_id);

            }
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

        if (keyCode == KeyEvent.KEYCODE_POWER) {
            //设置用户信息
            TextView id=FocusView.findViewById(R.id.Guardian_id);
            Intent intent = new Intent(MessageActivity.this, MessageInfoActivity.class);
            //给Activity传值方式一：创建一个Bundle对象封装数据
            intent.putExtra("id", id.getText());
            startActivity(intent);


        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
