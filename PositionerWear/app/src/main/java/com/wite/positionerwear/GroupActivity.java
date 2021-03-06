package com.wite.positionerwear;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.Toast;

import com.wite.positionerwear.DBHelper.VoiceDBHelper;
import com.wite.positionerwear.model.VoiceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private AnimationDrawable anim;
    private int requestCode;
    private VoiceDBHelper mVoiceDBHelper;
    private static final String TAG = "GroupActivity";

    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);
        tv_time.setText(str4 + ":" + str5);

    }

    //用户获取一个item
    private RelativeLayout FocusView;
    private TextView tv_time;
    private RecyclerView mRecyclerView;
    private List<VoiceModel> mDatas;
    private RecyclerView.Adapter mAdapter;
    //用于长按事件
    private boolean shortPress = false;
    //用于判断是否在播放语音
    private boolean isplayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
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
        mAdapter = new MyAdapter(initData());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setAdapter(mAdapter);
    }

    protected List<VoiceModel> initData() {
        mVoiceDBHelper = new VoiceDBHelper(GroupActivity.this, "voice", 1);
        List<VoiceModel> list = new ArrayList<>(); /*调用query()获取Cursor*/
        Cursor c = mVoiceDBHelper.query();
        while (c.moveToNext()) {
            VoiceModel v = new VoiceModel();
            v.setGuardianModel_id(c.getInt(c.getColumnIndex("id")));
            v.setName(c.getString(c.getColumnIndex("name")));
            v.setVoiceFile(c.getString(c.getColumnIndex("voicefile")));
            list.add(v);
          }
        mDatas = list;
        for (VoiceModel phone : list) {

            Log.d(TAG, "语音信息"+phone.getGuardianModel_id()+"名字"+phone.getName()+"文件地址"+phone.getVoiceFile() );

        }
        return mDatas;


    }





    public class MyAdapter extends RecyclerView.Adapter<GroupActivity.MyAdapter.ViewHolder> {
        public List<VoiceModel> datas = null;

        public MyAdapter(List<VoiceModel> datas) {
            this.datas = datas;
        }

        @Override
        public GroupActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mochat_group, parent, false);
            GroupActivity.MyAdapter.ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final GroupActivity.MyAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(datas.get(position).getName());
            holder.mTextViewletter.setText(datas.get(position).getName().substring(0,1));
            holder.mTextViewcorner.setText(0);
            holder.filename.setText(datas.get(position).getVoiceFile());
            holder.onclick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        FocusView = holder.onclick;
                    } else {
                        FocusView = null;
                    }

                }
            });


            if (position % 4 == 0) {
                holder.relativelayout.setBackgroundResource(R.drawable.group1);
            }
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
            public RelativeLayout relativelayout;
            public TextView mTextViewletter;
            public TextView mTextViewcorner;
            public RelativeLayout onclick;

            public TextView filename;

            public ViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView.findViewById(R.id.item_textview);
                mTextViewletter = itemView.findViewById(R.id.item_textview_letter);
                relativelayout = itemView.findViewById(R.id.item_rl_0);
                mTextViewcorner = itemView.findViewById(R.id.item_corner);
                onclick = itemView.findViewById(R.id.item_rl_onclick);
                filename=itemView.findViewById(R.id.filename);
           }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
        }
        if (keyCode == KeyEvent.KEYCODE_4) {
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }


        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {


        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {


        }


        if (keyCode == KeyEvent.KEYCODE_POWER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                event.startTracking(); //只有执行了这行代码才会调用onKeyLongPress
                if (event.getRepeatCount() == 0) {
                    shortPress = true;
                  /*  if (isplayer) {
                        Toast.makeText(this, "我在播放", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "没有在播放", Toast.LENGTH_SHORT).show();
                    }*/

                     Intent intent=new Intent(this,PlayerActivity.class);
                     TextView name=    (TextView)FocusView.findViewById(R.id.filename);
                    intent.putExtra("file",name.getText());

                  startActivity(intent);


                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            //if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            shortPress = false;
            //长按要执行的代码
            Intent intent = new Intent(this, RecordingActivity.class);
            requestCode = 0;
            startActivityForResult(intent, requestCode);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            if (shortPress) {
//                ImageView play = FocusView.findViewById(R.id.item_play);
//                play.setBackgroundResource(R.drawable.player);
//                anim = (AnimationDrawable) play.getBackground();
//                anim.start();
//                isplayer = true;
//                new Thread(
//                        new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(10000);
//
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                anim.stop();
//
//                                isplayer = false;
//
//                            }
//                        }).start();



            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            if (data.getExtras().getBoolean("istrue")) {
            }
            Toast.makeText(this, "发送成功！！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "发送失败！！！！！！！！！", Toast.LENGTH_SHORT).show();

        }


    }
}
