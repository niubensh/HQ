package com.wite.positionerwear;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.wite.positionerwear.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.wite.positionerwear.service.BackgroundService;
import com.wite.positionerwear.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.wite.positionerwear.utils.Utils.setupItem;

/**
 * 2017年9月25日 10:34:37
 * 需要出第一个版本
 * 测试加入了摄像头
 *Lin100 加入小圆点
 *Lin168 加入cardview
 * Lin226加入点击时间
 *Lin361加入按键监听事件
 *
 *
 *
 *
 *
 * */

public class MenuActivity extends AppCompatActivity {
    private int ItemPosion;
    private List<ImageView> imagelist;
    private HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager;
    private ImageView touyin;
    //用于长按事件
    private boolean shortPress = false;
    private AlertDialog.Builder builder;
    private Boolean iscall = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        horizontalInfiniteCycleViewPager = (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        horizontalInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(this, false));
        imagelist = new ArrayList<>();
        imagelist.add((ImageView) findViewById(R.id.iv_0));
        imagelist.add((ImageView) findViewById(R.id.iv_1));
        imagelist.add((ImageView) findViewById(R.id.iv_2));
        imagelist.add((ImageView) findViewById(R.id.iv_3));
        imagelist.add((ImageView) findViewById(R.id.iv_4));
        imagelist.add((ImageView) findViewById(R.id.iv_5));

        touyin = (ImageView) findViewById(R.id.imageView_touyin);
        horizontalInfiniteCycleViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //设置
                switch (position % 6) {
                    case 0:
                        init();
                        imagelist.get(0).setImageResource(R.drawable.yes);
                        ItemPosion = 0;
                        touyin.setImageResource(R.drawable.phonebook1);
                        break;
                    case 1:
                        init();
                        imagelist.get(1).setImageResource(R.drawable.yes);
                        ItemPosion = 1;
                        touyin.setImageResource(R.drawable.message2);
                        break;
                    case 2:
                        init();
                        imagelist.get(2).setImageResource(R.drawable.yes);
                        ItemPosion = 2;
                        touyin.setImageResource(R.drawable.missedcall3);
                        break;

                    case 3:
                        init();
                        imagelist.get(3).setImageResource(R.drawable.yes);
                        ItemPosion = 3;
                        touyin.setImageResource(R.drawable.mochat4);
                        break;
                    case 4:
                        init();
                        imagelist.get(4).setImageResource(R.drawable.yes);
                        ItemPosion = 4;
                        touyin.setImageResource(R.drawable.ice5);
                        break;

                    //测试摄像头
                    case 5:
                        init();
                        imagelist.get(5).setImageResource(R.drawable.yes);
                        ItemPosion = 5;
                        touyin.setImageResource(R.drawable.ice5);
                        break;

                    default:
                        ItemPosion = -1;
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void init() {
        for (int i = 0; i < imagelist.size(); i++) {
            imagelist.get(i).setImageResource(R.drawable.no);
        }


    }

    public class HorizontalPagerAdapter extends PagerAdapter {

        private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
                new Utils.LibraryObject(

                        R.drawable.phonebook,
                        "PhoneBook"

                ),
                new Utils.LibraryObject(
                        R.drawable.message,
                        "Message"
                ),
                new Utils.LibraryObject(
                        R.drawable.missedcall,
                        "Missedcall"


                ),
                new Utils.LibraryObject(
                        R.drawable.mochat,
                        "Mochat"
                ),
                new Utils.LibraryObject(
                        R.drawable.ice,
                        "Ice"
                ),
                new Utils.LibraryObject(
                        R.drawable.phonebook,
                        "摄像头"
                )



        };

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        private boolean mIsTwoWay;

        public HorizontalPagerAdapter(final Context context, final boolean isTwoWay) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            mIsTwoWay = isTwoWay;
        }

        @Override
        public int getCount() {
            return mIsTwoWay ? 6 : LIBRARIES.length;
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view;

            view = mLayoutInflater.inflate(R.layout.item, container, false);

            setupItem(view, LIBRARIES[position], position);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (position) {
                        case 0:
                            startActivity(new Intent(MenuActivity.this, PhoneBookActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MenuActivity.this, MessageActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MenuActivity.this, MissedcallActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(MenuActivity.this, MochatActivity.class));
                            break;
                        case 4:
                            startActivity(new Intent(MenuActivity.this, IceActivity.class));
                            break;
                        case 5:
                            startActivity(new Intent(MenuActivity.this, TestActivity.class));
                            break;

                        default:
                            break;
                    }

                }
            });

            container.addView(view);
            return view;
        }


        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }
        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            iscall = true;
            shortPress = false;
            //长按要执行的代码
            builder = new AlertDialog.Builder(MenuActivity.this);
            builder.setTitle("紧急呼叫");
            builder.setMessage("即将进入紧急呼叫流程！按BACK键退出！");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MenuActivity.this, "取消紧急呼叫！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MenuActivity.this, "开始紧急呼叫！", Toast.LENGTH_SHORT).show();

                }
            });
            dialog = builder.create();
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(3000);
                    if (iscall) {
//                sosCall();
//                sosSendSms();
                        Intent intent = new Intent(MenuActivity.this, BackgroundService.class);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //关闭当前activity
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_4) {
            finish();
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
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            switch (ItemPosion) {
                case 0:
                    startActivity(new Intent(MenuActivity.this, PhoneBookActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MenuActivity.this, MessageActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MenuActivity.this, MissedcallActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MenuActivity.this, MochatActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MenuActivity.this, IceActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(MenuActivity.this, TestActivity.class));
                    break;
                default:
                    break;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            ItemPosion--;
            horizontalInfiniteCycleViewPager.setCurrentItem(ItemPosion);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            ItemPosion++;
            horizontalInfiniteCycleViewPager.setCurrentItem(ItemPosion);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (shortPress) {
                Toast.makeText(this, "shortPress", Toast.LENGTH_LONG).show();
            } else {
                //Don't handle longpress here, because the user will have to get his finger back up first
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
