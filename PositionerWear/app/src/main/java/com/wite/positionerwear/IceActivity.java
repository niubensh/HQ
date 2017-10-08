package com.wite.positionerwear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IceActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };
    private TextView tv_time;

    private void refreshUI() {
        Date date = new Date();
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf4 = new SimpleDateFormat("mm");

        String str4 = sdf3.format(date);
        String str5 = sdf4.format(date);

        tv_time.setText(str4 + ":" + str5);

    }

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private RecyclerView.Adapter mAdapter;
    private RelativeLayout FocusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice);

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
        mAdapter = new MyAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setAdapter(mAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<IceActivity.MyAdapter.ViewHolder> {
        @Override
        public IceActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ice, parent, false);
            IceActivity.MyAdapter.ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final IceActivity.MyAdapter.ViewHolder holder, int position) {
            holder.mTextViewletter.setText("");
            holder.mTextViewdevice.setText("");
            holder.mTextViewinformation.setText("");
            holder.mTextView.setText("");
            switch (position) {
                case 0:
                    holder.mTextView.setText("User");
                    holder.relativelayout.setBackgroundResource(R.drawable.user);
                    holder.onclick.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(IceActivity.this, IceInfoActivity.class));
                        }
                    });
                    break;
                case 1:
                    holder.mTextViewdevice.setText("Emergence");
                    holder.mTextViewinformation.setText("contact");
                    holder.relativelayout.setBackgroundResource(R.drawable.emergence);
                    holder.onclick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(IceActivity.this, EmergenceActivity.class));

                        }
                    });

                    break;
                case 2:
                    holder.mTextViewdevice.setText("DECVICE");
                    holder.mTextViewinformation.setText("information");
                    holder.relativelayout.setBackgroundResource(R.drawable.device);
                    holder.onclick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(IceActivity.this, DevicesInfoActivity.class));
                        }
                    });

                    break;
                case 3:
                    holder.mTextView.setText("QR code");
                    holder.relativelayout.setBackgroundResource(R.drawable.twocode);
                    holder.onclick.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(IceActivity.this, QRcodeActivity.class));
                        }
                    });

                    break;
                default:
                    break;
            }
            holder.onclick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        //获取焦点view
                        FocusView = holder.onclick;


                    } else {

                        FocusView = null;
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            //修改
            return 4;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public TextView mTextViewletter;
            public RelativeLayout relativelayout;
            public TextView mTextViewdevice;
            public TextView mTextViewinformation;
            public RelativeLayout onclick;


            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.item_textview);
                mTextViewletter = view.findViewById(R.id.item_textview_letter);
                relativelayout = view.findViewById(R.id.item_rl_0);

                mTextViewdevice = view.findViewById(R.id.item_textview_device);
                mTextViewinformation = view.findViewById(R.id.item_textview_information);

                onclick = view.findViewById(R.id.item_rl_onclick);


            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //关闭当前activity


        }

        if (keyCode == KeyEvent.KEYCODE_POWER) {

            TextView textview = FocusView.findViewById(R.id.item_textview);
            if (textview.getText().equals("User")) {
                startActivity(new Intent(this, IceInfoActivity.class));

            }

            TextView device = FocusView.findViewById(R.id.item_textview_device);
            if (device.getText().equals("DECVICE")) {
                startActivity(new Intent(this, DevicesInfoActivity.class));

            }
            TextView Emergence = FocusView.findViewById(R.id.item_textview_device);
            if (Emergence.getText().equals("Emergence")) {
                startActivity(new Intent(this, EmergenceActivity.class));

            }
            TextView QRcode = FocusView.findViewById(R.id.item_textview);
            if (QRcode.getText().equals("QR code")) {
                startActivity(new Intent(this, QRcodeActivity.class));

            }


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
