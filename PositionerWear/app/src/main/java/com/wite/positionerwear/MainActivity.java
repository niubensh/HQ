package com.wite.positionerwear;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.thinkrace.orderlibrary.LocationService;
import com.thinkrace.orderlibrary.OrderUtil;
import com.wite.positionerwear.DBHelper.DBHelper;
import com.wite.positionerwear.DBHelper.GuaDBHelper;
import com.wite.positionerwear.DBHelper.MessageDBHelper;
import com.wite.positionerwear.DBHelper.SOSDBHelper;
import com.wite.positionerwear.DBHelper.VoiceDBHelper;
import com.wite.positionerwear.model.GuardianModel;
import com.wite.positionerwear.model.MissCallInfo;
import com.wite.positionerwear.model.PhoneUser;
import com.wite.positionerwear.model.StationInfo;
import com.wite.positionerwear.model.UserModel;
import com.wite.positionerwear.service.BackgroundService;
import com.wite.positionerwear.utils.FileUtil;
import com.wite.positionerwear.utils.GPSFormatUtils;
import com.wite.positionerwear.utils.HttpClientUtils;
import com.wite.positionerwear.utils.LocationUtil;
import com.wite.positionerwear.utils.NetWorkUtils;
import com.wite.positionerwear.utils.SetSystem;
import com.wite.positionerwear.utils.TimeConverterUtil;
import com.wite.positionerwear.utils.UnicodeUtil;
import com.wite.positionerwear.utils.WifiUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//2017年9月15日 09:54:44
//富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_CONTACT = 1;
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
    //接收下发的联系人
    List<PhoneUser> phoneuser = new ArrayList<>();
    private static final String TAG = "TAG";
    //单例模式
    final OrderUtil orderUtil = OrderUtil.getInstance(this);
    private int xiangji = 3;
    //音量控制模式
    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    public static final int RINGER_MODE_NORMAL = 2;
    //音量控制
    private AudioManager audio;
    //设置系统
    private SetSystem setsystem = new SetSystem();
    private NetWorkUtils mNetWorkUtils = new NetWorkUtils();
    private TextView tv_time;
    private TextView tv_date;
    private TextView tv_month;
    private TextView tv_am;
    private boolean is24Hour;
    private String imei;
    //控制GPS上传频率
    private LocationUtil mLocationUtil;
    private long gpsuptime = 60 * 5 * 1000;
    private boolean issend = true;
    //用于长按事件
    private boolean shortPress = false;
    private AlertDialog.Builder builder;
    private Boolean iscall = true;
    private AlertDialog dialog;
    private AlarmManagerUtil mAlarmManagerUtil;
    //定位
    private LocationManager gpsManager;
    //格林尼治时间
    private String GreenwichTime = "061830";
    //格林尼治时间
    private String nowTime;
    String latitude_str = "0000.0000N";
    String longitude_str = "0000.0000E";
    String speed_str;
    String time_str;
    float bearing = 323.87f;
    private int level = 100;
    String betterlevel = "080";
    String Gsm = "060";
    Long minTime = 3000L;
    private SOSDBHelper mSosdbHelper;
    //定义数据库帮助类
    private DBHelper dbHelper;
    PhoneUser mPhoneUser;
    private String sn;
    private TelephonyManager telephonyMgr;
    private MyPhoneStateListener MyListener;
    private MyPhoneStateListener myListener;
    private StationInfo mStationInfo;
    private String lbs;
    private int result;
    private List<MissCallInfo> listcallinfo;
    private TextView mochattext;
    private TextView messagetext;
    private TextView misscalltext;
    private WifiUtil mWifiUtil;
    // 扫描结果列表
    private List<ScanResult> wifilist;
    private ScanResult mScanResult;
    private UnicodeUtil mUnicodeUtil = new UnicodeUtil();
    private GuaDBHelper mGuaDBHelper;
    public static int netWorkType;
    private ContentValues Guavalues;
    private Context mContext;
    private int worktype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent i = new Intent(MainActivity.this, BackgroundService.class);
        startService(i);


        mContext = MainActivity.this;
        //wifi帮助类
        mWifiUtil = new WifiUtil(this);
        //测试WIFI
        // test();
        //测试
        //    testzz();
        startGps();
        iscall = true;

        //桌面角标！
        messagetext = (TextView) findViewById(R.id.textview_message);


        MessageDBHelper mMessagecount = new MessageDBHelper(MainActivity.this);


        int messagecount = mMessagecount.queryforisread();
        if (messagecount > 0) {
            messagetext.setText(messagecount);
        } else {
            messagetext.setVisibility(View.GONE);
        }

        mochattext = (TextView) findViewById(R.id.textview_mochat);

        VoiceDBHelper mVoicecount = new VoiceDBHelper(MainActivity.this);


        int voicecount = mVoicecount.queryforisread();
        if (voicecount > 0) {
            mochattext.setText(voicecount);
        } else {
            mochattext.setVisibility(View.GONE);
        }
        misscalltext = (TextView) findViewById(R.id.textview_misscall);
        int misscall = readMissCall();
        if (misscall > 0) {
            misscalltext.setText(misscall + "");
        } else {
            misscalltext.setVisibility(View.GONE);
        }
        ////////////////-------------------------------测试
        if (misscall > 0 || voicecount > 0 || messagecount > 0) {
            Toast.makeText(mContext, "有未读消息", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "没有未读消息", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, Main2Activity.class));

        }


        if (level == 100) {
            betterlevel = level + "";
        } else if (level > 10 && level < 99) {
            betterlevel = "0" + level + "";
        } else {
            betterlevel = "00" + level + "";
        }
        //白名单
        mGuaDBHelper = new GuaDBHelper(MainActivity.this, "guardian", 1);
        mGuaDBHelper.celer();
//     DBHelper   dbphoneHelper = new DBHelper(MainActivity.this, "phone", 1);
////清空联系人数据库
//        dbphoneHelper.celer();

        myListener = new MyPhoneStateListener();
        telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyMgr.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Log.e(TAG, "GSMx信号是多少 " + Gsm);


        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // startActivity(new Intent(this,Main2Activity.class));
        mSosdbHelper = new SOSDBHelper(this);
        telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //nox 有iemi号
        imei = telephonyMgr.getDeviceId();
        Log.e(TAG, "++++++++++++++++++++++++onCreate:  设备IMEI号码" + imei);
        Log.e(TAG, "onCreate: Unicode----------------" + UnicodeUtil.UNstringToUnicode("5f204e09"));
        sn = telephonyMgr.getSimSerialNumber();
        Log.e(TAG, "++++++++++++++++++++++++onCreate:  设备SN号码" + sn);
        if (mNetWorkUtils.isNetworkConnected(this) || mNetWorkUtils.isWifiConnected(this)) {
            Log.e(TAG, "开始GPS");
            Toast.makeText(mContext, "网络连接正常", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "请确认网络连接！", Toast.LENGTH_SHORT).show();

        }
        findViewById(R.id.mochat).setOnClickListener(this);
        findViewById(R.id.message).setOnClickListener(this);

//        Intent i = new Intent(MainActivity.this, BackgroundService.class);
//        startService(i);
/**----------------------------------------------------------------------**/
        IntentFilter filter = new IntentFilter();
        //电池电量广播
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        /**下行协议*/
        filter.addAction(LocationService.BP00);
        filter.addAction(LocationService.BPLN);
        filter.addAction(LocationService.BPLO);
        filter.addAction(LocationService.BPCM);
        filter.addAction(LocationService.BP02);
        filter.addAction(LocationService.BPFD);
        filter.addAction(LocationService.BP10);
        filter.addAction(LocationService.BP03);
        filter.addAction(LocationService.BP04);
        filter.addAction(LocationService.BP05);
        filter.addAction(LocationService.BP06);
        filter.addAction(LocationService.BP39);
        filter.addAction(LocationService.BP49);
        filter.addAction(LocationService.BP53);
        filter.addAction(LocationService.BP54);
        filter.addAction(LocationService.BP70);
        filter.addAction(LocationService.BP86);
        filter.addAction(LocationService.BPTM);
        filter.addAction(LocationService.BPHT);
        filter.addAction(LocationService.BPTQ);
        filter.addAction(LocationService.BP87);
        filter.addAction(LocationService.BP91);
        filter.addAction(LocationService.BP01);
        filter.addAction(LocationService.BP11);
        filter.addAction(LocationService.BP12);
        filter.addAction(LocationService.BP13);
        filter.addAction(LocationService.BP14);
        filter.addAction(LocationService.BP15);
        filter.addAction(LocationService.BP16);
        filter.addAction(LocationService.BP17);
        filter.addAction(LocationService.BP18);
        filter.addAction(LocationService.BP19);
        filter.addAction(LocationService.BP20);
        filter.addAction(LocationService.BP21);
        filter.addAction(LocationService.BP22);
        filter.addAction(LocationService.BP23);
        filter.addAction(LocationService.BP24);
        filter.addAction(LocationService.BP25);
        filter.addAction(LocationService.BP26);
        filter.addAction(LocationService.BP27);
        filter.addAction(LocationService.BP28);
        filter.addAction(LocationService.BP29);
        filter.addAction(LocationService.BP30);
        filter.addAction(LocationService.BP31);
        filter.addAction(LocationService.BP32);
        filter.addAction(LocationService.BP33);
        filter.addAction(LocationService.BP34);
        filter.addAction(LocationService.BP35);
        filter.addAction(LocationService.BP36);
        filter.addAction(LocationService.BP37);
        filter.addAction(LocationService.BP38);
        filter.addAction(LocationService.BP40);
        filter.addAction(LocationService.BP41);
        filter.addAction(LocationService.BP43);
        filter.addAction(LocationService.BP44);
        filter.addAction(LocationService.BP45);
        filter.addAction(LocationService.BP46);
        filter.addAction(LocationService.BP47);
        filter.addAction(LocationService.BP48);
        filter.addAction(LocationService.BP50);
        filter.addAction(LocationService.BP52);
        filter.addAction(LocationService.BP55);
        filter.addAction(LocationService.BP56);
        filter.addAction(LocationService.BP57);
        filter.addAction(LocationService.BP58);
        filter.addAction(LocationService.BP59);
        filter.addAction(LocationService.BP60);
        filter.addAction(LocationService.BP61);
        filter.addAction(LocationService.BP62);
        filter.addAction(LocationService.BP64);
        filter.addAction(LocationService.BP65);
        filter.addAction(LocationService.BP66);
        filter.addAction(LocationService.BP67);
        filter.addAction(LocationService.BP68);
        filter.addAction(LocationService.BP69);
        filter.addAction(LocationService.BP71);
        filter.addAction(LocationService.BP72);
        filter.addAction(LocationService.BP75);
        filter.addAction(LocationService.BP77);
        filter.addAction(LocationService.BP78);
        filter.addAction(LocationService.BP79);
        filter.addAction(LocationService.BPVL);
        filter.addAction(LocationService.BP80);
        filter.addAction(LocationService.BP81);
        filter.addAction(LocationService.BP82);
        filter.addAction(LocationService.BPXL);
        filter.addAction(LocationService.BPXY);
        filter.addAction(LocationService.BPJZ);
        filter.addAction(LocationService.BP83);
        filter.addAction(LocationService.BP84);
        filter.addAction(LocationService.BP85);
        filter.addAction(LocationService.BP88);
        filter.addAction(LocationService.BP89);
        filter.addAction(LocationService.BP90);
        filter.addAction(LocationService.BP92);
        filter.addAction(LocationService.BP93);
        filter.addAction(LocationService.BP94);
        filter.addAction(LocationService.BPDF);
        filter.addAction(LocationService.BP96);
        filter.addAction(LocationService.BP97);
        filter.addAction(LocationService.BPWL);
        /**上行协议*/
        filter.addAction(LocationService.AP11);
        filter.addAction(LocationService.AP12);
        filter.addAction(LocationService.AP13);
        filter.addAction(LocationService.AP14);
        filter.addAction(LocationService.AP15);
        filter.addAction(LocationService.AP16);
        filter.addAction(LocationService.AP17);
        filter.addAction(LocationService.AP18);
        filter.addAction(LocationService.AP19);
        filter.addAction(LocationService.AP20);
        filter.addAction(LocationService.AP21);
        filter.addAction(LocationService.AP22);
        filter.addAction(LocationService.AP23);
        filter.addAction(LocationService.AP24);
        filter.addAction(LocationService.AP25);
        filter.addAction(LocationService.AP26);
        filter.addAction(LocationService.AP27);
        filter.addAction(LocationService.AP28);
        filter.addAction(LocationService.AP29);
        filter.addAction(LocationService.AP30);
        filter.addAction(LocationService.AP31);
        filter.addAction(LocationService.AP32);
        filter.addAction(LocationService.AP33);
        filter.addAction(LocationService.AP34);
        filter.addAction(LocationService.AP35);
        filter.addAction(LocationService.AP36);
        filter.addAction(LocationService.AP37);
        filter.addAction(LocationService.AP38);
        filter.addAction(LocationService.AP40);
        filter.addAction(LocationService.AP41);
        filter.addAction(LocationService.AP43);
        filter.addAction(LocationService.AP44);
        filter.addAction(LocationService.AP45);
        filter.addAction(LocationService.AP46);
        filter.addAction(LocationService.AP47);
        filter.addAction(LocationService.AP48);
        filter.addAction(LocationService.AP50);
        filter.addAction(LocationService.AP51);
        filter.addAction(LocationService.AP52);
        filter.addAction(LocationService.AP55);
        filter.addAction(LocationService.AP56);
        filter.addAction(LocationService.AP57);
        filter.addAction(LocationService.AP58);
        filter.addAction(LocationService.AP59);
        filter.addAction(LocationService.AP60);
        filter.addAction(LocationService.AP61);
        filter.addAction(LocationService.AP62);
        filter.addAction(LocationService.AP64);
        filter.addAction(LocationService.AP65);
        filter.addAction(LocationService.AP66);
        filter.addAction(LocationService.AP67);
        filter.addAction(LocationService.AP68);
        filter.addAction(LocationService.AP69);
        filter.addAction(LocationService.AP71);
        filter.addAction(LocationService.AP72);
        filter.addAction(LocationService.AP75);
        filter.addAction(LocationService.AP77);
        filter.addAction(LocationService.AP78);
        filter.addAction(LocationService.AP79);
        filter.addAction(LocationService.APVL);
        filter.addAction(LocationService.AP80);
        filter.addAction(LocationService.AP81);
        filter.addAction(LocationService.AP82);
        filter.addAction(LocationService.APXL);
        filter.addAction(LocationService.APXY);
        filter.addAction(LocationService.APJZ);
        filter.addAction(LocationService.AP83);
        filter.addAction(LocationService.AP84);
        filter.addAction(LocationService.AP85);
        filter.addAction(LocationService.AP88);
        filter.addAction(LocationService.AP89);
        filter.addAction(LocationService.AP90);
        filter.addAction(LocationService.AP92);
        filter.addAction(LocationService.AP93);
        filter.addAction(LocationService.AP94);
        filter.addAction(LocationService.APDF);
        filter.addAction(LocationService.AP96);
        filter.addAction(LocationService.AP97);
        filter.addAction(LocationService.APWL);
        //2017年9月26日 16:35:53新加
        filter.addAction(LocationService.BPGL);
        filter.addAction(LocationService.BPGF);
        filter.addAction(LocationService.BP98);
        filter.addAction(LocationService.BPPF);
        filter.addAction(LocationService.BPVU);
        filter.addAction(LocationService.BPNS);
        filter.addAction(LocationService.BPWS);

        filter.addAction(LocationService.BPCD);


        registerReceiver(myReceiver, filter);


        orderUtil.setOnOrderListener(new OrderUtil.OrderListener() {

            @Override
            public void sendOrderSuccess(String s) {
                Log.e(TAG, "order发送指令成功: " + s);

            }

            @Override
            public void sendOrderFail(String s) {

                Log.e(TAG, "order发送指令失败: " + s);


                if (mNetWorkUtils.isMobileConnected(MainActivity.this) || mNetWorkUtils.isWifiConnected(MainActivity.this)) {


                    orderUtil.loginPkg(imei);
                    Log.e(TAG, "sendOrderFail:  再发一遍 好吧  谁怕谁啊 ");
                    Log.e(TAG, "开始GPS");


                }


            }

            @Override
            public void receiverOrder(String s) {
                Log.e(TAG, "order接收指令: " + s);
            }
        });


        //判断是12小时制还是24小时
        is24Hour = DateFormat.is24HourFormat(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                handler.sendMessage(Message.obtain());
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message ms = Message.obtain();
                if (issend) {
                    ms.what = 1;
                    handler.postDelayed(this, gpsuptime);
                    handler.sendMessage(ms);
                }
            }
        }).start();

        tv_time = (TextView) findViewById(R.id.time);
        tv_date = (TextView) findViewById(R.id.date);
        tv_month = (TextView) findViewById(R.id.month);
        tv_am = (TextView) findViewById(R.id.tv_am);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);

    }


    /**
     * 接收数据广播
     * 全套
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                   /* 定位数据包广播监听*/

            switch (intent.getAction()) {
                //网络状态
                case ConnectivityManager.CONNECTIVITY_ACTION:

                    Log.e(TAG, "监听到广播" + Context.CONNECTIVITY_SERVICE);
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                        Log.e(TAG, "网络不正常");
                    } else {

                        Toast.makeText(context, "网络正常", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "网络正常进入TRY");
                        try {
                            Log.e(TAG, "开始发送登陆指令");
                            orderUtil.loginPkg(imei);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.e(TAG, "TRY出现错误 捕获到" + e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                /**
                 *
                 * */
                /** 电池电量 */
                case Intent.ACTION_BATTERY_CHANGED:

                    final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN); /* boolean isCharging = false;*/
                    boolean mCharged = (status == BatteryManager.BATTERY_STATUS_FULL);
                    boolean isCharging = (mCharged || status == BatteryManager.BATTERY_STATUS_CHARGING);
                    level = (int) (100f * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
                    if (level < 15) issend = false;
                    else issend = true;

                    switch (level) {

                        case 75:
                            orderUtil.lowPowerAlarm(level + "");
                            break;
                        case 50:
                            orderUtil.lowPowerAlarm(level + "");
                            break;
                        case 25:
                            orderUtil.lowPowerAlarm(level + "");
                            break;
                        case 10:
                            orderUtil.lowPowerAlarm(level + "");
                            break;
                        default:
                            //   Log.e(TAG, "onReceive: 当前电量" + level);
                            break;
                    }
                    break;


                /***
                 * 下行协议
                 *
                 * */
                //登陆回执
                case LocationService.BP00:
                    Log.e(TAG, "指令名称++登陆回复包:BP00***LocationService====" + LocationService.BP00);
                    String testBP00 = intent.getStringExtra(LocationService.BP00);
                    if (testBP00 == null || testBP00 == "") {
                        Log.e(TAG, "onReceive: 如果没有拿到登陆数据");
                        orderUtil.baseStationTiming(mStationInfo.getMCC() + "", mStationInfo.getMNC() + "", mStationInfo.getLAC() + "", mStationInfo.getCID() + "");
                        Log.e(TAG, "-------MMC= " + mStationInfo.getMCC() + "MNC" + mStationInfo.getMNC() + "LAC=" + mStationInfo.getLAC() + "CID" + mStationInfo.getCID());

                    } else {
                        Log.e(TAG, "我全都知道了");
                        String[] BP00 = testBP00.split(",");
                        Log.e(TAG, "登陆 回执" + BP00[1]);
                        String bp00str = BP00[1];
                        String bp00_string;
                        if (bp00str.substring((bp00str.length() - 1), bp00str.length()).equals("#")) {
                            bp00_string = bp00str.substring(0, bp00str.length() - 1);
                            Log.e(TAG, "onReceive: 我的屁股有个#######");
                        } else bp00_string = bp00str;
                        TimeConverterUtil mTimeConverterUtil = new TimeConverterUtil();

                        Log.e(TAG, "++++++++++++++++++: " + bp00_string);

                        String BP00_login = mTimeConverterUtil.converTime(bp00_string, TimeZone.getDefault());
                        //   String BP00_login = mTimeConverterUtil.utc2Local(bp00_string, "yyyy-MM-ddHH:mm:ss:");

                        Log.e(TAG, "现在时间 " + BP00_login.trim());


//                        //需要加入系统权限
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
//                        Date date = new Date();
//                        try {
//                            date = sdf.parse(BP00_login);
//                            Log.e(TAG, "-------- " + date.toString());
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        Calendar c = Calendar.getInstance();
//                        c.setTime(date);
//                        int year = c.get(Calendar.YEAR);
//                        int mon  = c.get(Calendar.MONTH);
//                        int day  = c.get(Calendar.DATE);
//                        int hour = c.get(Calendar.HOUR);//小时
//                      int minute = c.get(Calendar.MINUTE);//分
                        //              setsystem.setDate(year, mon, day);
                        //               setsystem.setTime(hour, minute);


                        int year = Integer.valueOf(BP00_login.trim().substring(0, 4));
                        Log.e(TAG, "现在时间 " + year);


                        int mon = Integer.valueOf(BP00_login.trim().substring(5, 7));
                        Log.e(TAG, "现在时间 " + mon);

                        int day = Integer.valueOf(BP00_login.trim().substring(8, 10));
                        Log.e(TAG, "现在时间 " + day);

                        int hour = Integer.valueOf(BP00_login.trim().substring(11, 13));
                        Log.e(TAG, "现在时间 " + hour);

                        int minute = Integer.valueOf(BP00_login.trim().substring(14, 16));

                        Log.e(TAG, "现在时间 " + minute);


                        //设置时间的位置
                        //     setsystem.setDate(year,mon-1,day);
                        //       setsystem.setTime(hour,minute);


                        Log.e(TAG, "设备登录时间 " + year + "!" + mon + "!" + day + "//////" + hour + "" + minute);
                    }


                    break;

                case LocationService.BPLN:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPLN***LocationService====" + LocationService.BPLN, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPLN***LocationService====" + LocationService.BPLN);
                    break;
                case LocationService.BPLO:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPLO***LocationService====" + LocationService.BPLO, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPLO***LocationService====" + LocationService.BPLO);
                    break;
                case LocationService.BPCM:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPCM***LocationService====" + LocationService.BPCM, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPCM***LocationService====" + LocationService.BPCM);
                    break;
                case LocationService.BP02:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP02***LocationService====" + LocationService.BP02, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP02***LocationService====" + LocationService.BP02);
                    break;
                case LocationService.BPFD:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPFD***LocationService====" + LocationService.BPFD, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPFD***LocationService====" + LocationService.BPFD);
                    break;
                case LocationService.BP10:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP10***LocationService====" + LocationService.BP10, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP10***LocationService====" + LocationService.BP10);
                    break;
                case LocationService.BP03:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP03***LocationService====" + LocationService.BP03, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP03***LocationService====" + LocationService.BP03);
                    break;
                case LocationService.BP04:

                    Log.e(TAG, "指令名称:BP04***LocationService====" + LocationService.BP04);
                    break;
                case LocationService.BP05:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP05***LocationService====" + LocationService.BP05, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP05***LocationService====" + LocationService.BP05);
                    break;
                case LocationService.BP06:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP06***LocationService====" + LocationService.BP06, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP06***LocationService====" + LocationService.BP06);
                    break;
                case LocationService.BP39:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP39***LocationService====" + LocationService.BP39, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP39***LocationService====" + LocationService.BP39);
                    break;
                case LocationService.BP49:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP49***LocationService====" + LocationService.BP49, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP49***LocationService====" + LocationService.BP49);
                    break;

                //基站校时
                case LocationService.BP53:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP53***LocationService====" + LocationService.BP53, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP53***LocationService====" + LocationService.BP53);
                    String[] BP53 = intent.getStringExtra("BP53").split("，");


                    Log.e(TAG, "基站校时" + BP53);
                    Date sysdate = new Date(BP53[1]);
                    setsystem.setDate(sysdate.getYear(), sysdate.getMonth(), sysdate.getDay());
                    setsystem.setTime(sysdate.getDay(), sysdate.getMinutes());


                    break;
                case LocationService.BP54:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP54***LocationService====" + LocationService.BP54, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP54***LocationService====" + LocationService.BP54);
                    break;
                case LocationService.BP70:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP70***LocationService====" + LocationService.BP70, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP70***LocationService====" + LocationService.BP70);
                    break;
                case LocationService.BP86:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP86***LocationService====" + LocationService.BP86, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP86***LocationService====" + LocationService.BP86);
                    break;
                case LocationService.BPTM:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPTM***LocationService====" + LocationService.BPTM, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPTM***LocationService====" + LocationService.BPTM);
                    break;

                case LocationService.BPHT:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPHT***LocationService====" + LocationService.BPHT, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPHT***LocationService====" + LocationService.BPHT);
                    break;
                case LocationService.BPTQ:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPTQ***LocationService====" + LocationService.BPTQ, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPTQ***LocationService====" + LocationService.BPTQ);
                    break;
                case LocationService.BP87:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP87***LocationService====" + LocationService.BP87, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP87***LocationService====" + LocationService.BP87);
                    break;
                case LocationService.BP91:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP91***LocationService====" + LocationService.BP91, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP91***LocationService====" + LocationService.BP91);
                    break;
                case LocationService.BP01:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP01***LocationService====" + LocationService.BP01, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP01***LocationService====" + LocationService.BP01);
                    break;
                case LocationService.BP11:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP11***LocationService====" + LocationService.BP11, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP11***LocationService====" + LocationService.BP11);
                    break;
                /**接受sos号码并存储得到 soo号  getsos()*/
                case LocationService.BP12:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP12***LocationService====" + LocationService.BP12, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP12***LocationService====" + LocationService.BP12);
                    String[] BP12 = intent.getStringExtra(LocationService.BP12).substring(0, intent.getStringExtra(LocationService.BP12).length() - 1).split(",");
                    if (BP12.length > 3) {
                        String[] newBP12 = new String[]{BP12[3], BP12[4], BP12[5]};
                        com.wite.positionerwear.service.Item item = new com.wite.positionerwear.service.Item();
                        //存数数据
                        for (int i = 0; i < newBP12.length; i++) {
                            item.setName("sos" + i);
                            item.setNumber(newBP12[i]);
                            mSosdbHelper.addItem(item);
                            //
                            Log.e(TAG, "存放紧急联系人" + item.getName());
                        }

                        ArrayList<com.wite.positionerwear.service.Item> lists = mSosdbHelper.getItemList();
                        for (com.wite.positionerwear.service.Item ite : lists) {
                            Log.e(TAG, "紧急联系人" + ite.getName() + ite.getNumber());
                        }
                        orderUtil.send("IWAP12", BP12[2] + "," + lists.get(0).getNumber() + "," + lists.get(1).getNumber() + "," + lists.get(2).getNumber() + "#");

                    } else {
                        orderUtil.send("IWAP12", BP12[2] + "," + "," + "," + "#");

                    }

//                    SharedPreferences sos = getSharedPreferences("sos", 0);
//
//                    SharedPreferences.Editor editor = sos.edit();
//
//                    editor.putString("sos1", BP12[3]);
//                    editor.putString("sos2", BP12[4]);
//                    editor.putString("sos3", BP12[5]);
//                    editor.commit();
//                    String[] getsoss = getsos();
//                    for (String ss : getsos()) {
//
//                        Log.e(TAG, "sos号 --------------" + ss);
//                    }

                    break;
                case LocationService.BP13:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP13***LocationService====" + LocationService.BP13, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP13***LocationService====" + LocationService.BP13);
                    break;

                //接收联系人下发
                case LocationService.BP14:
                    dbHelper = new DBHelper(MainActivity.this, "phone", 1);
                    dbHelper.celer();

                    Log.e(TAG, "指令名称:BP14***LocationService====" + LocationService.BP14);
                    String[] BP14 = intent.getStringExtra(LocationService.BP14).split(",");

                    StringBuffer sb = new StringBuffer();
                    for (int i = 3; i < BP14.length; i++) {
                        if (BP14[i] == null || BP14[i] == "" || BP14[i].equals("#") || BP14[i].isEmpty() == true || BP14[i] == "#") {

                            if (BP14[3] == null || BP14[3] == "") {
                                dbHelper.celer();
                                orderUtil.send("IWAP14", BP14[2]);

                            }

                        } else {
                            Log.e(TAG, "onReceive: i=" + i);
                            mPhoneUser = new PhoneUser();
                            String[] test = BP14[i].split("\\|");
                            mPhoneUser.setName(UnicodeUtil.UNstringToUnicode(test[0].toString()));
                            mPhoneUser.setPhonenum(test[1]);

                            /* Log.e(TAG, "onReceive: 清空数据库"); dbHelper.celer();*/
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
                            List<PhoneUser> list_phoneuser = list;
                            Log.e(TAG, "onReceive: -------姓名：" + mPhoneUser.getName() + "电话号码：---------------" + mPhoneUser.getPhonenum() + "集合总共有" + list_phoneuser.size());

                            if (list_phoneuser.size() > 0) {
                                for (int z = 0; z < list_phoneuser.size(); z++) {
                                    //  for (PhoneUser phone : list_phoneuser) {
                                    PhoneUser phone = list_phoneuser.get(z);
                                    if (phone.getPhonenum().equals(mPhoneUser.getPhonenum()) && phone.getName().equals(mPhoneUser.getName())) {
                                        Log.e(TAG, "试图加入相同名字和相同手机号的联系人 " + phone.getName() + phone.getPhonenum());
                                        break;
                                    } else {
                                        //修改了条件
                                        ContentValues values = new ContentValues();
                                        values.put("name", phone.getName());
                                        values.put("Phonenum", phone.getPhonenum());
                                        dbHelper.insert(values);
                                    /*回复指令*/
                                    }

                                    // sb.append(BP14[z]);
                                    //  sb.append(",");

                                }
                            } else {

                                //修改！！！！！！！

                                //   Log.e(TAG, "ELSE又加了一次");
                                //  ContentValues values = new ContentValues();
                                //   istrue = true;
                                // values.put("name", mPhoneUser.getName());
                                // values.put("Phonenum", mPhoneUser.getPhonenum());
                                // dbHelper.insert(values);
                            }

                        }

                    }


                    if (sb.toString().length() > 0) {
                        orderUtil.send("IWAP14", BP14[2] + "," + BP14[3]);

                    } else {
                        orderUtil.send("IWAP14", BP14[2]);
                    }

                    //   Log.e(TAG, "回去拼接指令 IWAP14"+BP14[2]+","+ sb.toString().substring(0, sb.length() - 1)+"#" );
                    break;

                case LocationService.BP15:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP15***LocationService====" + LocationService.BP15, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP15***LocationService====" + LocationService.BP15);
                    break;

              /*立即定位*/
                //  333
                case LocationService.BP16:
                    Log.e(TAG, "指令名称:BP16***LocationService====" + LocationService.BP16);
                    String[] BP16 = intent.getStringExtra(LocationService.BP16).split(",");
                    lbs = "460,0,9520,3671";
                    if (mStationInfo != null) {
                        lbs = mStationInfo.getMCC() + "," + mStationInfo.getMNC() + "," + mStationInfo.getLAC() + "," + mStationInfo.getCID();
                    }
                    orderUtil.sendLocationOrder("IWAP01", nowTime, "A", latitude_str + "N", longitude_str + "E", "000.1", GreenwichTime, "323.87" + "", Gsm + "009" + betterlevel + "00102", lbs);
                    orderUtil.sendGPRSIntervalOrder("IWBP15", imei, "080835", "300");
                    Log.e(TAG, "上行定位信息 " + "IWAP01" + nowTime + "A" + latitude_str + "N" + longitude_str + "E" + speed_str + GreenwichTime + bearing + "" + Gsm + "009" + betterlevel + "00102" + lbs);
                    orderUtil.send("IWAP16", BP16[1]);
                    Log.e(TAG, "回复立即定位指令 ：" + "IWAP16" + BP16[1]);
                    break;

                case LocationService.BP17:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP17***LocationService====" + LocationService.BP17, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP17***LocationService====" + LocationService.BP17);


                    break;
                //重启设备
                case LocationService.BP18:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP18***LocationService====" + LocationService.BP18, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP18***LocationService====" + LocationService.BP18);
                    String[] BP18 = intent.getStringExtra(LocationService.BP18).substring(0, intent.getStringExtra(LocationService.BP18).length() - 1).split(",");
                    PowerManager pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    pManager.reboot("");
                    orderUtil.send("IWAP18", BP18[2]);
                    break;

                case LocationService.BP19:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP19***LocationService====" + LocationService.BP19, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP19***LocationService====" + LocationService.BP19);
                    break;
                case LocationService.BP20:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP20***LocationService====" + LocationService.BP20, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP20***LocationService====" + LocationService.BP20);
                    break;
                case LocationService.BP21:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP21***LocationService====" + LocationService.BP21, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP21***LocationService====" + LocationService.BP21);
                    break;
                case LocationService.BP22:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP22***LocationService====" + LocationService.BP22, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP22***LocationService====" + LocationService.BP22);
                    break;
                case LocationService.BP23:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP23***LocationService====" + LocationService.BP23, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP23***LocationService====" + LocationService.BP23);
                    break;
                //353919025680130
                case LocationService.BP24:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP24***LocationService====" + LocationService.BP24, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP24***LocationService====" + LocationService.BP24);
                    break;
                //设置闹钟
                case LocationService.BP25:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP25***LocationService====" + LocationService.BP25, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP25***LocationService====" + LocationService.BP25);
                    Log.e(TAG, intent.getStringExtra(LocationService.BP25));
                    String[] BP25 = intent.getStringExtra(LocationService.BP25).split(",");
                    if (0 == Integer.valueOf(BP25[4]) || BP25[4].equals("0")) {
                        if (Integer.valueOf(BP25[3]) == 1) {
                            Log.e(TAG, "onReceive: 闹钟总开关开启");
                            Log.e(TAG, "闹钟总数" + Integer.valueOf(BP25[4]));
                            String bp25 = intent.getStringExtra(LocationService.BP25);

                            String newbp25 = bp25.substring(34, bp25.length() - 1);
                            Log.e(TAG, "newbp25是+++++ " + newbp25);
                            String[] alarm = newbp25.split("@");
                            Log.e(TAG, "alarm的长度: " + alarm.length);
                            for (int j = 0; j < alarm.length; j++) {
                                String[] alarmagement = alarm[j].split(",");
                                Log.e(TAG, "alarmagement------------ " + alarmagement[0]);
                                Log.e(TAG, "星期------------ " + alarmagement[1]);
                                Log.e(TAG, "开关------------ " + alarmagement[2]);
                                String[] dateas;

                                if (alarmagement[1].length() == 1) {
                                    dateas = new String[]{alarmagement[1]};
                                } else {
                                    dateas = alarmagement[1].split("|");
                                }


                                for (int d = 0; d < dateas.length; d++) {
                                    if (dateas[d].equals("") == false) {
                                        Log.e(TAG, "设置的日期是" + dateas[d] + "|");

                                        int hour = Integer.parseInt(alarmagement[0].substring(0, 2));
                                        int minute = Integer.parseInt(alarmagement[0].substring(2, alarmagement[0].length()));
                                        Log.e(TAG, "时：" + hour);
                                        Log.e(TAG, "分：" + minute);
                                        AlarmManagerUtil.setAlarm(MainActivity.this, 1, hour, minute, j, Integer.valueOf(dateas[d]), "你在干什么", 2);
                                        Log.e(TAG, "onReceive: 闹钟id" + j);
                                        Log.e(TAG, "设置闹钟" + hour + minute + j + dateas[d]);
                                    } else {
                                        Log.e(TAG, "有一个空值" + dateas[d]);
                                    }
                                }


                            }
                        } else {
                            Log.e(TAG, "onReceive: --------------------------------------闹钟总开关关闭");
                        }
                    }

                    orderUtil.send("IWAP25", intent.getStringExtra(LocationService.BP25).substring(13, Integer.valueOf(intent.getStringExtra(LocationService.BP25).length())));

                    break;
                case LocationService.BP26:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP26***LocationService====" + LocationService.BP26, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP26***LocationService====" + LocationService.BP26);
                    break;

                //新语音提醒
                case LocationService.BP27:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP27***LocationService====" + LocationService.BP27, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP27***LocationService====" + LocationService.BP27);
                    String[] BP27 = intent.getStringExtra(LocationService.BP27).split(",");
                    Log.e(TAG, "onReceive: ");
                    break;
                case LocationService.BP28:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP28***LocationService====" + LocationService.BP28, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP28***LocationService====" + LocationService.BP28);
                    break;
                case LocationService.BP29:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP29***LocationService====" + LocationService.BP29, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP29***LocationService====" + LocationService.BP29);
                    break;
                case LocationService.BP30:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP30***LocationService====" + LocationService.BP30, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP30***LocationService====" + LocationService.BP30);
                    break;
                //远程关机
                case LocationService.BP31:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP31***LocationService====" + LocationService.BP31, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP31***LocationService====" + LocationService.BP31);
                    Intent BP31intent = new Intent("android.intent.action.REBOOT");
                    BP31intent.putExtra("nowait", 1);
                    BP31intent.putExtra("interval", 1);
                    BP31intent.putExtra("window", 0);
                    sendBroadcast(BP31intent);
                    String[] BP31 = intent.getStringExtra(LocationService.BP31).substring(0, intent.getStringExtra(LocationService.BP18).length() - 1).split(",");
                    orderUtil.send("IWAP31", BP31[2]);
                    break;

                //拨打电话
                case LocationService.BP32:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP32***LocationService====" + LocationService.BP32, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP32***LocationService====" + LocationService.BP32);
                    String[] BP32 = intent.getStringExtra(LocationService.BP32).substring(0, intent.getStringExtra(LocationService.BP32).length() - 1).split(",");
                    Intent bp32 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + BP32[3]));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(bp32);


                    break;
                case LocationService.BP33:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP33***LocationService====" + LocationService.BP33, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP33***LocationService====" + LocationService.BP33);
                    break;
                case LocationService.BP34:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP34***LocationService====" + LocationService.BP34, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP34***LocationService====" + LocationService.BP34);
                    break;
                case LocationService.BP35:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP35***LocationService====" + LocationService.BP35, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP35***LocationService====" + LocationService.BP35);
                    break;
                case LocationService.BP36:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP36***LocationService====" + LocationService.BP36, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP36***LocationService====" + LocationService.BP36);
                    break;
                case LocationService.BP37:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP37***LocationService====" + LocationService.BP37, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP37***LocationService====" + LocationService.BP37);
                    break;
                case LocationService.BP38:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP38***LocationService====" + LocationService.BP38, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP38***LocationService====" + LocationService.BP38);
                    break;

                //message 文字下发
                case LocationService.BP40:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP40***LocationService====" + LocationService.BP40, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP40***LocationService====" + LocationService.BP40);

                    //String[] BP40=intent.getStringExtra(LocationService.BP40).substring()


                    break;
                case LocationService.BP41:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP41***LocationService====" + LocationService.BP41, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP41***LocationService====" + LocationService.BP41);
                    break;
                case LocationService.BP43:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP43***LocationService====" + LocationService.BP43, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP43***LocationService====" + LocationService.BP43);
                    break;
                case LocationService.BP44:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP44***LocationService====" + LocationService.BP44, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP44***LocationService====" + LocationService.BP44);
                    break;
                //静音开关
                case LocationService.BP45:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP45***LocationService====" + LocationService.BP45, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP45***LocationService====" + LocationService.BP45);

                    String[] BP45 = intent.getStringExtra(LocationService.BP45).substring(0, intent.getStringExtra(LocationService.BP45).length() - 1).split(",");

                    if (Integer.valueOf(BP45[3]) == 1) {

                        Log.e(TAG, "1 标识开 0标识关  当前状态" + BP45[3]);

                        setsystem.toMute(audio);

                    } else {
                        setsystem.toNormal(audio);
                    }


                    break;
                case LocationService.BP46:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP46***LocationService====" + LocationService.BP46, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP46***LocationService====" + LocationService.BP46);


                case LocationService.BP47:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP47***LocationService====" + LocationService.BP47, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP47***LocationService====" + LocationService.BP47);
                    break;
                case LocationService.BP48:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP48***LocationService====" + LocationService.BP48, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP48***LocationService====" + LocationService.BP48);
                    break;
                case LocationService.BP50:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP50***LocationService====" + LocationService.BP50, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP50***LocationService====" + LocationService.BP50);
                    break;


                //删除联系人
                case LocationService.BP52:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP52***LocationService====" + LocationService.BP52, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP52***LocationService====" + LocationService.BP52);
                    String[] BP52 = intent.getStringExtra(LocationService.BP52).split(",");
                    dbHelper = new DBHelper(MainActivity.this, "phone", 1);
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
                    List<PhoneUser> list_phoneuser = list;
                    for (PhoneUser phone : list_phoneuser) {

                        Log.e(TAG, "Main+++++++++++++++++LocationService.BP52:数据库的数据******************" + phone.getName() + phone.getPhonenum());

                    }


                    String str = BP52[3];
                    String newstring;
                    if (str.substring((str.length() - 1), str.length()).equals("#"))
                        newstring = str.substring(0, str.length() - 1);
                    else newstring = str;
                    for (PhoneUser phone : list_phoneuser)
                        if (phone.getPhonenum() == newstring || phone.getPhonenum().equals(newstring)) {
                            dbHelper.delete(phone.get_id());
                            Log.e(TAG, "onReceive: 删除了" + phone.get_id());
                            Log.e(TAG, "已经被删除的_id" + phone.get_id() + "名字" + phone.getName() + "     手机号码" + phone.getPhonenum() + "          头像" + phone.getLetter() + "       时间" + phone.getIntime());
                        } else {
                            Log.e(TAG, "没有被删除的_id" + phone.get_id() + "名字" + phone.getName() + "     手机号码" + phone.getPhonenum() + "          头像" + phone.getLetter() + "       时间" + phone.getIntime());
                            Log.e(TAG, "onReceive: 没有找到");
                        }
                    orderUtil.send("IWAP52", BP52[2] + "," + "1");

                    break;


                case LocationService.BP55:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP55***LocationService====" + LocationService.BP55, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP55***LocationService====" + LocationService.BP55);
                    break;

                case LocationService.BP56:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP56***LocationService====" + LocationService.BP56, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP56***LocationService====" + LocationService.BP56);
                    break;
                case LocationService.BP57:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP57***LocationService====" + LocationService.BP57, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP57***LocationService====" + LocationService.BP57);
                    break;
                case LocationService.BP58:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP58***LocationService====" + LocationService.BP58, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP58***LocationService====" + LocationService.BP58);
                    break;
                case LocationService.BP59:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP59***LocationService====" + LocationService.BP59, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP59***LocationService====" + LocationService.BP59);
                    break;
                case LocationService.BP60:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP60***LocationService====" + LocationService.BP60, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP60***LocationService====" + LocationService.BP60);
                    break;
                case LocationService.BP61:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP61***LocationService====" + LocationService.BP61, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP61***LocationService====" + LocationService.BP61);
                    break;
                case LocationService.BP62:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP62***LocationService====" + LocationService.BP62, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP62***LocationService====" + LocationService.BP62);
                    break;
                case LocationService.BP64:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP64***LocationService====" + LocationService.BP64, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP64***LocationService====" + LocationService.BP64);
                    break;
                case LocationService.BP65:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP65***LocationService====" + LocationService.BP65, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP65***LocationService====" + LocationService.BP65);
                    break;
                case LocationService.BP66:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP66***LocationService====" + LocationService.BP66, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP66***LocationService====" + LocationService.BP66);
                    break;
                case LocationService.BP67:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP67***LocationService====" + LocationService.BP67, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP67***LocationService====" + LocationService.BP67);
                    break;
                case LocationService.BP68:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP68***LocationService====" + LocationService.BP68, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP68***LocationService====" + LocationService.BP68);
                    break;
                case LocationService.BP69:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP69***LocationService====" + LocationService.BP69, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP69***LocationService====" + LocationService.BP69);
                    break;
                case LocationService.BP71:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP71***LocationService====" + LocationService.BP71, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP71***LocationService====" + LocationService.BP71);
                    break;
                case LocationService.BP72:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP72***LocationService====" + LocationService.BP72, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP72***LocationService====" + LocationService.BP72);
                    break;

                //设置带名称的闹钟

                case LocationService.BP75:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP25***LocationService====" + LocationService.BP25, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP25***LocationService====" + LocationService.BP75);
                    Log.e(TAG, intent.getStringExtra(LocationService.BP75));
                    String[] BP75 = intent.getStringExtra(LocationService.BP75).split(",");
                    if (0 == Integer.valueOf(BP75[4]) || BP75[4].equals("0")) {
                        if (Integer.valueOf(BP75[3]) == 1) {
                            Log.e(TAG, "onReceive: 闹钟总开关开启");
                            Log.e(TAG, "闹钟总数" + Integer.valueOf(BP75[4]));
                            String bp25 = intent.getStringExtra(LocationService.BP25);

                            String newbp25 = bp25.substring(34, bp25.length() - 1);
                            Log.e(TAG, "newbp25是+++++ " + newbp25);
                            String[] alarm = newbp25.split("@");
                            Log.e(TAG, "alarm的长度: " + alarm.length);
                            for (int j = 0; j < alarm.length; j++) {
                                String[] alarmagement = alarm[j].split(",");

                                Log.e(TAG, "名称------------ " + alarmagement[0]);
                                Log.e(TAG, "alarmagement------------ " + alarmagement[1]);
                                Log.e(TAG, "星期------------ " + alarmagement[2]);
                                Log.e(TAG, "开关------------ " + alarmagement[3]);
                                String[] dateas;
                                if (alarmagement[2].length() == 1) {
                                    dateas = new String[]{alarmagement[2]};
                                } else {
                                    dateas = alarmagement[2].split("|");
                                }


                                for (int d = 0; d < dateas.length; d++) {
                                    if (dateas[d].equals("") == false) {
                                        Log.e(TAG, "设置的日期是" + dateas[d] + "|");

                                        int hour = Integer.parseInt(alarmagement[1].substring(0, 2));
                                        int minute = Integer.parseInt(alarmagement[1].substring(2, alarmagement[1].length()));
                                        Log.e(TAG, "时：" + hour);
                                        Log.e(TAG, "分：" + minute);
                                        AlarmManagerUtil.setAlarm(MainActivity.this, 1, hour, minute, j, Integer.valueOf(dateas[d]), UnicodeUtil.UNstringToUnicode(alarmagement[0].toString()), 2);
                                        Log.e(TAG, "onReceive: 闹钟id" + j);
                                        Log.e(TAG, "设置闹钟" + hour + minute + j + dateas[d]);
                                    } else {
                                        Log.e(TAG, "有一个空值" + dateas[d]);
                                    }
                                }


                            }
                        } else {
                            Log.e(TAG, "onReceive: --------------------------------------闹钟总开关关闭");
                        }
                    }

                    orderUtil.send("IWAP75", intent.getStringExtra(LocationService.BP25).substring(13, Integer.valueOf(intent.getStringExtra(LocationService.BP25).length())));

                    break;


                case LocationService.BP77:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP77***LocationService====" + LocationService.BP77, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP77***LocationService====" + LocationService.BP77);
                    break;
                case LocationService.BP78:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP78***LocationService====" + LocationService.BP78, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP78***LocationService====" + LocationService.BP78);
                    break;
                case LocationService.BP79:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP79***LocationService====" + LocationService.BP79, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP79***LocationService====" + LocationService.BP79);
                    break;
                case LocationService.BPVL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPVL***LocationService====" + LocationService.BPVL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPVL***LocationService====" + LocationService.BPVL);
                    break;
                case LocationService.BP80:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP80***LocationService====" + LocationService.BP80, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP80***LocationService====" + LocationService.BP80);
                    break;
                case LocationService.BP81:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP81***LocationService====" + LocationService.BP81, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP81***LocationService====" + LocationService.BP81);
                    break;
                case LocationService.BP82:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP82***LocationService====" + LocationService.BP82, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP82***LocationService====" + LocationService.BP82);
                    break;
                case LocationService.BPXL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPXL***LocationService====" + LocationService.BPXL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPXL***LocationService====" + LocationService.BPXL);
                    break;
                case LocationService.BPXY:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPXY***LocationService====" + LocationService.BPXY, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPXY***LocationService====" + LocationService.BPXY);
                    break;
                case LocationService.BPJZ:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPJZ***LocationService====" + LocationService.BPJZ, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPJZ***LocationService====" + LocationService.BPJZ);
                    break;
                case LocationService.BP83:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP83***LocationService====" + LocationService.BP83, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP83***LocationService====" + LocationService.BP83);
                    break;
                case LocationService.BP84:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP84***LocationService====" + LocationService.BP84, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP84***LocationService====" + LocationService.BP84);
                    break;
                case LocationService.BP85:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP85***LocationService====" + LocationService.BP85, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP85***LocationService====" + LocationService.BP85);
                    break;
                case LocationService.BP88:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP88***LocationService====" + LocationService.BP88, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP88***LocationService====" + LocationService.BP88);
                    break;
                case LocationService.BP89:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP89***LocationService====" + LocationService.BP89, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP89***LocationService====" + LocationService.BP89);
                    break;
                case LocationService.BP90:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP90***LocationService====" + LocationService.BP90, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP90***LocationService====" + LocationService.BP90);
                    break;
                case LocationService.BP92:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP92***LocationService====" + LocationService.BP92, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP92***LocationService====" + LocationService.BP92);
                    break;
                case LocationService.BP93:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP93***LocationService====" + LocationService.BP93, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP93***LocationService====" + LocationService.BP93);
                    break;
                case LocationService.BP94:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP94***LocationService====" + LocationService.BP94, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP94***LocationService====" + LocationService.BP94);
                    break;
                case LocationService.BPDF:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPDF***LocationService====" + LocationService.BPDF, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPDF***LocationService====" + LocationService.BPDF);
                    break;
                case LocationService.BP96:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP96***LocationService====" + LocationService.BP96, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP96***LocationService====" + LocationService.BP96);
                    break;
                case LocationService.BP97:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BP97***LocationService====" + LocationService.BP97, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BP97***LocationService====" + LocationService.BP97);
                    break;
                case LocationService.BPWL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPWL***LocationService====" + LocationService.BPWL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPWL***LocationService====" + LocationService.BPWL);
                    break;

                //下行监护人列表
                case LocationService.BPGL:

                    //   mGuaDBHelper.insert();

                    String test = "IWBPGL,353456789012345,080835,6654111|133xxxxxxxx|D3590D54|0,6654112|133xxxxxxxx|D3590D54|0,6654113|133xxxxxxxx|D3590D54|0,6654114|133xxxxxxxx|D3590D54|0,6654115|133xxxxxxxx|D3590D54|0,6654116|133xxxxxxxx|D3590D54|0,6654117|133xxxxxxxx|D3590D54|0,6654118|133xxxxxxxx|D3590D54|0,6654119|133xxxxxxxx|D3590D54|0,6654120|133xxxxxxxx|D3590D54|0#";

//切换成数据库更好
                    // String[] BPGL = intent.getStringExtra(LocationService.BPGL).substring(0, intent.getStringExtra(LocationService.BPGL).length() - 1).split(",");
                    // String[] newBPGL = intent.getStringExtra(LocationService.BPGL).substring(29, intent.getStringExtra(LocationService.BPGL).length() - 1).split(",");
                    String[] BPGL = test.substring(0, test.length() - 1).split(",");
                    String[] newBPGL = test.substring(29, test.length() - 1).split(",");

//                    SharedPreferences guardian=mContext.getSharedPreferences("Guardian",MODE_PRIVATE);
//
//
//                    SharedPreferences.Editor GuaEditor= guardian.edit();
                    String[] GuardianString;
                    for (int i = 0; i < newBPGL.length; i++) {
                        GuardianString = newBPGL[i].split("[|]");
                        Guavalues = new ContentValues();
                        Guavalues.put("id", GuardianString[0]);
                        Guavalues.put("phone", GuardianString[1]);
                        Guavalues.put("name", UnicodeUtil.UNstringToUnicode(GuardianString[2].toString()));
                        mGuaDBHelper.insert(Guavalues);
                    }

                    mGuaDBHelper.query();

                    List<GuardianModel> Gualist = new ArrayList<>(); /*调用query()获取Cursor*/
                    Cursor guac = dbHelper.query();
                    while (guac.moveToNext()) {
                        PhoneUser p = new PhoneUser();
                        GuardianModel g = new GuardianModel();
                        g.setId(guac.getInt(guac.getColumnIndex("id")));
                        g.setGuardianPhone(guac.getString(guac.getColumnIndex("GuardianPhone")));
                        g.setGuardianName(guac.getString(guac.getColumnIndex("GuardianName")));
                        Gualist.add(g);
                    }


                    for (GuardianModel ss : Gualist) {
                        Log.e(TAG, "监护人" + ss.getId() + ss.getGuardianName() + ss.getGuardianPhone());
                    }


                    break;


                case LocationService.BPGF:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPWL***LocationService====" + LocationService.BPWL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPWL***LocationService====" + LocationService.BPWL);
                    break;
                case LocationService.BP98:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+BPWL***LocationService====" + LocationService.BPWL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:BPWL***LocationService====" + LocationService.BPWL);
                    break;

                //手表使用者下发
                case LocationService.BPPF:

                    Log.e(TAG, "onReceive: 接受到用户信息");
                    String[] BPPF = intent.getStringExtra(LocationService.BPPF).substring(0, intent.getStringExtra(LocationService.BPPF).length() - 1).split(",");
                    String[] userinfo = BPPF[3].split("\\|");
                    UserModel mUserModel = new UserModel();
                    mUserModel.setName(userinfo[0]);
                    mUserModel.setBirthday(userinfo[1]);
                    mUserModel.setBlood(userinfo[2]);
                    mUserModel.setMedical(userinfo[3]);
                    mUserModel.setDrug(userinfo[4]);
                    mUserModel.setAnaphylaxis(userinfo[5]);
                    mUserModel.setEmergency(userinfo[6]);

                    //获取SharedPreferences对象

                    SharedPreferences mSharedPreferences = mContext.getSharedPreferences("User", MODE_PRIVATE);

                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putString("Name", mUserModel.getName());
                    mEditor.putString("Birthday", mUserModel.getBirthday());
                    mEditor.putString("Blood", mUserModel.getBlood());
                    mEditor.putString("Medical", mUserModel.getMedical());
                    mEditor.putString("Drug", mUserModel.getDrug());
                    mEditor.putString("Anaphylaxis", mUserModel.getAnaphylaxis());
                    mEditor.putString("Emergency", mUserModel.getEmergency());

                    mEditor.commit();


                    break;

                //91.语音聊天以URL方式下发文件连接（下行协议号：BPVU，响应：APVU）
                //IWBPVU,353456789012345,080835,123,爸爸,1,http://www.xxx.com/x.amr#
                case LocationService.BPVU:
                    String bpvu = intent.getStringExtra(LocationService.BPVU);
                    String[] BPVU = bpvu.substring(0, bpvu.length() - 1).split(",");

                    Date fil = new Date();
                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    //下载文件//
                    String urlPath = BPVU[6];
                    String outPath = null;
                    String fileName = mSimpleDateFormat.format(fil);
                    //获取文件路径
                    String filepath = getFilesDir().getAbsolutePath();
                    FileUtil mFileUtil = new FileUtil();
                    File voicefile = null;
                    if (mFileUtil.createFile(filepath, fileName)) {
                        voicefile = new File(filepath + File.separator + fileName);
                        Log.e(TAG, "文件创建成功！");
                    } else {
                        Log.e(TAG, "文件创建失败！！！！！");
                    }
                    HttpClientUtils mHttpClientUtils = new HttpClientUtils(urlPath, outPath, fileName);
                    mHttpClientUtils.downloadFile();


                    //存入数据库
                    VoiceDBHelper mVoiceDBHelper = new VoiceDBHelper(MainActivity.this);
                    ContentValues voice = new ContentValues();
                    voice.put("id", BPVU[3]);
                    voice.put("name", BPVU[4]);
                    voice.put("voicefile", voicefile.getPath());
                    voice.put("isread", 0);
                    mVoiceDBHelper.insert(voice);


                    break;


                //搜索附近wifi
                case LocationService.BPNS:
                    String[] BPNS = intent.getStringExtra(LocationService.BPNS).substring(0, intent.getStringExtra(LocationService.BPNS).length() - 1).split(",");
                    StringBuffer wifiString = new StringBuffer();
                    //打开wifi
                    mWifiUtil.openWifi();
                    // 每次点击扫描之前清空上一次的扫描结果
                    List<String> wifilists = new ArrayList<>();
                    if (wifilists != null) {
                        wifilists = new ArrayList<>();
                    }
                    //开始扫描网络
                    mWifiUtil.startScan();
                    wifilist = mWifiUtil.getWifiList();
                    if (wifilist != null) {
                        for (int i = 0; i < wifilist.size(); i++) {
                            //得到扫描结果
                            mScanResult = wifilist.get(i);
                            wifiString = wifiString.append(mUnicodeUtil.convert(mScanResult.SSID)).append("@").append(mScanResult.BSSID).append("|");

                        }
                    }
                    String wwwifi = wifiString.toString().substring(0, wifiString.toString().length() - 1);
                    Log.e(TAG, "WIFI是什么" + wwwifi);
                    StringBuffer mStringBuffer = new StringBuffer();
                    mStringBuffer.append(BPNS[2]);
                    mStringBuffer.append(",");
                    mStringBuffer.append(wwwifi);
                    mStringBuffer.append("#");
                    orderUtil.send("IWAPNS", mStringBuffer.toString());
                    break;

                //连接至指定WIFI
                case LocationService.BPWS:
                    String[] BPWS = intent.getStringExtra(LocationService.BPWS).substring(0, intent.getStringExtra(LocationService.BPWS).length() - 1).split(",");

                    mWifiUtil.addNetWork(mWifiUtil.createWifiInfo(BPWS[4], BPWS[5], Integer.valueOf(BPWS[5])));
                    //回复WIFI连接
                    orderUtil.send("IWAPWS", BPWS[2] + "#");
                    break;


                case LocationService.BPCD:
                    String bpcd = intent.getStringExtra(LocationService.BPCD);
                    String[] BPCD = bpcd.substring(0, bpcd.length() - 1).split(",");

                    if (BPCD[4].equals("3") || BPCD[4] == 3 + "") {

                        //IWBPCD,6654111,D3590D54,080835,3,XXXXXXXXXXXXXXXX#
                        MessageDBHelper messageDBHelper = new MessageDBHelper(MainActivity.this);
                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put("id", BPCD[1]);
                        mContentValues.put("name", UnicodeUtil.UNstringToUnicode(BPCD[2]));
                        Log.e(TAG, "当BPCD【4】等于3的时候代表发送文字信息" + UnicodeUtil.UNstringToUnicode(BPCD[2]) + "内容" + UnicodeUtil.UNstringToUnicode(BPCD[5]));
                        mContentValues.put("textMessage", UnicodeUtil.UNstringToUnicode(BPCD[5]));
                        Date inDate = new Date();
                        //注意时间格式
                        SimpleDateFormat intime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        mContentValues.put("messageintime", intime.format(inDate));
                        //设置读取状态
                        mContentValues.put("isreade", 0);


                        messageDBHelper.insert(mContentValues);

                        orderUtil.send("IWAPCD", BPCD[1] + "," + BPCD[3] + "," + BPCD[4] + ",1");

                    }

                    break;


                /***
                 * 上行协议
                 *
                 * */
                case LocationService.AP11:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP11***LocationService====" + LocationService.AP11, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP11***LocationService====" + LocationService.AP11);
                    break;
                case LocationService.AP12:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP12***LocationService====" + LocationService.AP12, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP12***LocationService====" + LocationService.AP12);
                    break;
                case LocationService.AP13:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP13***LocationService====" + LocationService.AP13, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP13***LocationService====" + LocationService.AP13);
                    break;
                case LocationService.AP14:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP14***LocationService====" + LocationService.AP14, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP14***LocationService====" + LocationService.AP14);
                    break;
                case LocationService.AP15:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP15***LocationService====" + LocationService.AP15, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP15***LocationService====" + LocationService.AP15);
                    break;
                case LocationService.AP16:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP16***LocationService====" + LocationService.AP16, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP16***LocationService====" + LocationService.AP16);
                    break;
                case LocationService.AP17:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP17***LocationService====" + LocationService.AP17, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP17***LocationService====" + LocationService.AP17);
                    break;
                case LocationService.AP18:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP18***LocationService====" + LocationService.AP18, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP18***LocationService====" + LocationService.AP18);
                    break;
                case LocationService.AP19:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP19***LocationService====" + LocationService.AP19, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP19***LocationService====" + LocationService.AP19);
                    break;
                case LocationService.AP20:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP20***LocationService====" + LocationService.AP20, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP20***LocationService====" + LocationService.AP20);
                    break;
                case LocationService.AP21:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP21***LocationService====" + LocationService.AP21, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP21***LocationService====" + LocationService.AP21);
                    break;
                case LocationService.AP22:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP22***LocationService====" + LocationService.AP22, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP22***LocationService====" + LocationService.AP22);
                    break;
                case LocationService.AP23:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP23***LocationService====" + LocationService.AP23, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP23***LocationService====" + LocationService.AP23);
                    break;
                case LocationService.AP24:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP24***LocationService====" + LocationService.AP24, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP24***LocationService====" + LocationService.AP24);
                    break;
                case LocationService.AP25:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP25***LocationService====" + LocationService.AP25, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP25***LocationService====" + LocationService.AP25);
                    break;
                case LocationService.AP26:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP26***LocationService====" + LocationService.AP26, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP26***LocationService====" + LocationService.AP26);
                    break;
                case LocationService.AP27:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP27***LocationService====" + LocationService.AP27, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP27***LocationService====" + LocationService.AP27);
                    break;
                case LocationService.AP28:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP28***LocationService====" + LocationService.AP28, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP28***LocationService====" + LocationService.AP28);
                    break;
                case LocationService.AP29:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP29***LocationService====" + LocationService.AP29, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP29***LocationService====" + LocationService.AP29);
                    break;
                case LocationService.AP30:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP30***LocationService====" + LocationService.AP30, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP30***LocationService====" + LocationService.AP30);
                    break;
                case LocationService.AP31:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP31***LocationService====" + LocationService.AP31, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP31***LocationService====" + LocationService.AP31);
                    break;
                case LocationService.AP32:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP32***LocationService====" + LocationService.AP32, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP32***LocationService====" + LocationService.AP32);
                    break;
                case LocationService.AP33:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP33***LocationService====" + LocationService.AP33, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP33***LocationService====" + LocationService.AP33);
                    break;
                case LocationService.AP34:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP34***LocationService====" + LocationService.AP34, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP34***LocationService====" + LocationService.AP34);
                    break;
                case LocationService.AP35:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP35***LocationService====" + LocationService.AP35, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP35***LocationService====" + LocationService.AP35);
                    break;
                case LocationService.AP36:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP36***LocationService====" + LocationService.AP36, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP36***LocationService====" + LocationService.AP36);
                    break;
                case LocationService.AP37:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP37***LocationService====" + LocationService.AP37, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP37***LocationService====" + LocationService.AP37);
                    break;
                case LocationService.AP38:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP38***LocationService====" + LocationService.AP38, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP38***LocationService====" + LocationService.AP38);
                    break;
                case LocationService.AP40:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP40***LocationService====" + LocationService.AP40, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP40***LocationService====" + LocationService.AP40);
                    break;
                case LocationService.AP41:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP41***LocationService====" + LocationService.AP41, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP41***LocationService====" + LocationService.AP41);
                    break;
                case LocationService.AP43:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP43***LocationService====" + LocationService.AP43, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP43***LocationService====" + LocationService.AP43);
                    break;
                case LocationService.AP44:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP44***LocationService====" + LocationService.AP44, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP44***LocationService====" + LocationService.AP44);
                    break;
                case LocationService.AP45:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP45***LocationService====" + LocationService.AP45, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP45***LocationService====" + LocationService.AP45);
                    break;
                case LocationService.AP46:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP46***LocationService====" + LocationService.AP46, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP46***LocationService====" + LocationService.AP46);
                    break;
                case LocationService.AP47:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP47***LocationService====" + LocationService.AP47, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP47***LocationService====" + LocationService.AP47);
                    break;
                case LocationService.AP48:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP48***LocationService====" + LocationService.AP48, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP48***LocationService====" + LocationService.AP48);
                    break;
                case LocationService.AP50:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP50***LocationService====" + LocationService.AP50, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP50***LocationService====" + LocationService.AP50);
                    break;

                case LocationService.AP51:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP51***LocationService====" + LocationService.AP51, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP51***LocationService====" + LocationService.AP51);
                    break;

                case LocationService.AP52:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP52***LocationService====" + LocationService.AP52, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP52***LocationService====" + LocationService.AP52);
                    break;
                case LocationService.AP55:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP55***LocationService====" + LocationService.AP55, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP55***LocationService====" + LocationService.AP55);
                    break;
                case LocationService.AP56:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP56***LocationService====" + LocationService.AP56, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP56***LocationService====" + LocationService.AP56);
                    break;
                case LocationService.AP57:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP57***LocationService====" + LocationService.AP57, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP57***LocationService====" + LocationService.AP57);
                    break;
                case LocationService.AP58:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP58***LocationService====" + LocationService.AP58, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP58***LocationService====" + LocationService.AP58);
                    break;
                case LocationService.AP59:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP59***LocationService====" + LocationService.AP59, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP59***LocationService====" + LocationService.AP59);
                    break;
                case LocationService.AP60:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP60***LocationService====" + LocationService.AP60, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP60***LocationService====" + LocationService.AP60);
                    break;
                case LocationService.AP61:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP61***LocationService====" + LocationService.AP61, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP61***LocationService====" + LocationService.AP61);
                    break;
                case LocationService.AP62:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP62***LocationService====" + LocationService.AP62, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP62***LocationService====" + LocationService.AP62);
                    break;
                case LocationService.AP64:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP64***LocationService====" + LocationService.AP64, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP64***LocationService====" + LocationService.AP64);
                    break;
                case LocationService.AP65:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP65***LocationService====" + LocationService.AP65, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP65***LocationService====" + LocationService.AP65);
                    break;
                case LocationService.AP66:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP66***LocationService====" + LocationService.AP66, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP66***LocationService====" + LocationService.AP66);
                    break;
                case LocationService.AP67:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP67***LocationService====" + LocationService.AP67, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP67***LocationService====" + LocationService.AP67);
                    break;
                case LocationService.AP68:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP68***LocationService====" + LocationService.AP68, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP68***LocationService====" + LocationService.AP68);
                    break;
                case LocationService.AP69:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP69***LocationService====" + LocationService.AP69, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP69***LocationService====" + LocationService.AP69);
                    break;
                case LocationService.AP71:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP71***LocationService====" + LocationService.AP71, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP71***LocationService====" + LocationService.AP71);
                    break;
                case LocationService.AP72:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP72***LocationService====" + LocationService.AP72, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP72***LocationService====" + LocationService.AP72);
                    break;
                case LocationService.AP75:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP75***LocationService====" + LocationService.AP75, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP75***LocationService====" + LocationService.AP75);
                    break;
                case LocationService.AP77:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP77***LocationService====" + LocationService.AP77, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP77***LocationService====" + LocationService.AP77);
                    break;
                case LocationService.AP78:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP78***LocationService====" + LocationService.AP78, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP78***LocationService====" + LocationService.AP78);
                    break;
                case LocationService.AP79:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP79***LocationService====" + LocationService.AP79, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP79***LocationService====" + LocationService.AP79);
                    break;
                case LocationService.APVL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APVL***LocationService====" + LocationService.APVL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APVL***LocationService====" + LocationService.APVL);
                    break;
                case LocationService.AP80:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP80***LocationService====" + LocationService.AP80, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP80***LocationService====" + LocationService.AP80);
                    break;
                case LocationService.AP81:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP81***LocationService====" + LocationService.AP81, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP81***LocationService====" + LocationService.AP81);
                    break;
                case LocationService.AP82:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP82***LocationService====" + LocationService.AP82, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP82***LocationService====" + LocationService.AP82);
                    break;
                case LocationService.APXL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APXL***LocationService====" + LocationService.APXL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APXL***LocationService====" + LocationService.APXL);
                    break;
                case LocationService.APXY:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APXY***LocationService====" + LocationService.APXY, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APXY***LocationService====" + LocationService.APXY);
                    break;
                case LocationService.APJZ:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APJZ***LocationService====" + LocationService.APJZ, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APJZ***LocationService====" + LocationService.APJZ);
                    break;
                case LocationService.AP83:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP83***LocationService====" + LocationService.AP83, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP83***LocationService====" + LocationService.AP83);
                    break;
                case LocationService.AP84:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP84***LocationService====" + LocationService.AP84, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP84***LocationService====" + LocationService.AP84);
                    break;
                case LocationService.AP85:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP85***LocationService====" + LocationService.AP85, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP85***LocationService====" + LocationService.AP85);
                    break;
                case LocationService.AP88:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP88***LocationService====" + LocationService.AP88, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP88***LocationService====" + LocationService.AP88);
                    break;
                case LocationService.AP89:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP89***LocationService====" + LocationService.AP89, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP89***LocationService====" + LocationService.AP89);
                    break;
                case LocationService.AP90:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP90***LocationService====" + LocationService.AP90, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP90***LocationService====" + LocationService.AP90);
                    break;
                case LocationService.AP92:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP92***LocationService====" + LocationService.AP92, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP92***LocationService====" + LocationService.AP92);
                    break;
                case LocationService.AP93:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP93***LocationService====" + LocationService.AP93, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP93***LocationService====" + LocationService.AP93);
                    break;
                case LocationService.AP94:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP94***LocationService====" + LocationService.AP94, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP94***LocationService====" + LocationService.AP94);
                    break;
                case LocationService.APDF:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APDF***LocationService====" + LocationService.APDF, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APDF***LocationService====" + LocationService.APDF);
                    break;
                case LocationService.AP96:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP96***LocationService====" + LocationService.AP96, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP96***LocationService====" + LocationService.AP96);
                    break;
                case LocationService.AP97:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+AP97***LocationService====" + LocationService.AP97, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:AP97***LocationService====" + LocationService.AP97);
                    break;
                case LocationService.APWL:
                    Toast.makeText(MainActivity.this, "onReceive服务监听-指令名称+APWL***LocationService====" + LocationService.APWL, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "指令名称:APWL***LocationService====" + LocationService.APWL);
                    break;


                default:

                    Toast.makeText(context, "我是谁intent.getAction()" + intent.getAction(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "我是谁intent.getAction()" + intent.getAction());
                    break;

            }

        }

    };


    //点击事件
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.message:
                startActivity(new Intent(this, MenuActivity.class));
                break;

            case R.id.mochat:
                // Intent intent = new Intent(MainActivity.this, BackgroundService.class);

                //没有参数 你个智障
                Intent test = new Intent();
                test.setAction("android.intent.action.SOS_LONG_PRESS");
                this.sendBroadcast(test);
                Toast.makeText(mContext, "开始你的表演", Toast.LENGTH_SHORT).show();


                break;
        }


    }

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
            if (cc <= 9) {
                str4 = "0" + cc;
            } else {
                str4 = "" + cc;
            }
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

    //程序一加载，直接在主线程中创建Handler
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
            if (msg.what == 1) {
                lbs = "460,0,9520,3671";
                if (mStationInfo != null) {
                    lbs = mStationInfo.getMCC() + "," + mStationInfo.getMNC() + "," + mStationInfo.getLAC() + "," + mStationInfo.getCID();
                }

                orderUtil.sendLocationOrder("IWAP01", nowTime, "A", latitude_str + "N", longitude_str + "E", "000.1", GreenwichTime, "323.87" + "", Gsm + "009" + betterlevel + "00102", lbs);


                Log.e(TAG, "-----------------每" + gpsuptime + "毫秒发送一次定位信息----------------");
            }

        }
    };

    private void startGps() {
        // 获取到LocationManager对象
        gpsManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //provider可为gps定位，也可为为基站和WIFI定位
        String provider = gpsManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        //3000ms为定位的间隔时间，10m为距离变化阀值，gpsListener为回调接口
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        /**
         *  minTime	long：位置更新之间的最小时间间隔，以毫秒为单位
         *  minDistance	float：位置更新之间的最小距离，以米为单位
         *  criteria	Criteria：包含位置管理器参数选择合适的供应商和参数来计算位置
         *  intent	PendingIntent：一个PendingIntent为每个位置更新被发送
         * */
        gpsManager.requestLocationUpdates(getProvider(), minTime, 10, gpsListener);


    }


    private void stopGps() {
        gpsManager.removeUpdates(gpsListener);
    }

    private String getProvider() {
        // TODO Auto-generated method stub
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(true);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(true);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
        // provider
        return gpsManager.getBestProvider(criteria, true);
    }


    private LocationListener gpsListener = new LocationListener() {

        // 位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            Log.e("Location", "onLocationChanged");
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            float speed = location.getSpeed();
            long time = location.getTime();

            //得到方向角
            bearing = location.getBearing();
            Log.e(TAG, "转换前---经度---latitude " + latitude);
            Log.e(TAG, "转换前---纬度---longitude " + longitude);
            GPSFormatUtils convertdd = new GPSFormatUtils();
            latitude_str = convertdd.DDtoDMS_photo(latitude);
            longitude_str = convertdd.DDtoDMS_long(longitude);

            Log.e(TAG, "onLocationChanged: 转换后的经度是" + latitude_str);
            Log.e(TAG, "转换后---纬度---longitude " + longitude_str);
            Date date = new Date();
            date.setTime(Long.valueOf(time));
            Log.e(TAG, "speed是什么: " + speed);
            //速度
            speed_str = speed + "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
            GreenwichTime = simpleDateFormat.format(date);

            SimpleDateFormat simpleDateFormatyy = new SimpleDateFormat("yyyyMMdd");
            Date yyyyMMdd = new Date();
            nowTime = simpleDateFormatyy.format(yyyyMMdd);
            nowTime = nowTime.substring(2, nowTime.length());


            mStationInfo = LocationUtil.getInstance(MainActivity.this).getCellInfo();

        }

        // provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {
            Log.e("Location", "onProviderDisabled-------provider失效时调用");
        }

        // provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {
            Log.e("Location", "onProviderEnabled--------provider启用时调用");

        }

        // 状态改变时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("Location", "onStatusChanged--------状态改变时调用");
        }
    };


    public ArrayList<com.wite.positionerwear.service.Item> getsos() {
        ArrayList<com.wite.positionerwear.service.Item> lists = mSosdbHelper.getItemList();
        return lists;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            System.out.println("requestCode"+requestCode);
//
//            switch (requestCode){
//
//                case 2:
//                    Uri tuku_uri = data.getData();
//                    System.out.println(tuku_uri.getPath());
//                    ContentResolver tuku_cr = this.getContentResolver();
//                    try {
//                        bmp = BitmapFactory.decodeStream(tuku_cr.openInputStream(tuku_uri));
//                        MCShareLaunchShareHelper.shareContentWithBitmap("测试分享本地图片", bmp, "your share url", "", MoxunActivity.this);
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    break;
//                case 3:
//                    try {
//                        Uri xiangji_uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), sdcardTempFile.getAbsolutePath(), null, null));
//                        System.out.println(xiangji_uri.getPath());
//                        ContentResolver xiangji_cr = this.getContentResolver();
//
//                        bmp = BitmapFactory.decodeStream(xiangji_cr.openInputStream(xiangji_uri));
//                        MCShareLaunchShareHelper.shareContentWithBitmap("测试照相机图片", bmp, "your share url", "", MoxunActivity.this);
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
////方法二
////if (requestCode == MyApp.CAMERA_RECODE) {
////              try {
////                  bmp=BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());
////                  img.setImageBitmap(bmp);
////                  picCount++;
////              } catch (Exception e) {
////                  e.printStackTrace();
////              }
////
////              break;
////          }
////    }
    }


    //信号监测
    private class MyPhoneStateListener extends PhoneStateListener {
  /* Get the Signal strength from the provider,
   * each tiome there is an update
   *从得到的信号强度,每个tiome供应商有更新
   */

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //信号强度换算公式
            int astSignal = -113 + 2 * signalStrength.getGsmSignalStrength();
            // gsm.setText("GSM 信号强度asu :" + signalStrength.getGsmSignalStrength() +"_dBm :"+astSignal);
            Gsm = astSignal + "";


        }

    }


    private int readMissCall() {
        result = 0;

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


    private void test() {
        StringBuffer wifiString = new StringBuffer();
        //打开wifi
        mWifiUtil.openWifi();
        // 每次点击扫描之前清空上一次的扫描结果

        List<String> wifilists = new ArrayList<>();
        if (wifiString != null) {
            wifiString = new StringBuffer();
        }
        //开始扫描网络
        mWifiUtil.startScan();
        wifilist = mWifiUtil.getWifiList();
        if (wifilist != null) {
            for (int i = 0; i < wifilist.size(); i++) {
                //得到扫描结果
                mScanResult = wifilist.get(i);
                wifiString = wifiString.append(mUnicodeUtil.convert(mScanResult.SSID)).append("@").append(mScanResult.BSSID).append("|");
            }
        }
        String wwwifi = wifiString.toString().substring(0, wifiString.toString().length() - 1);
        Log.e(TAG, "WIFI是什么" + wwwifi);
        StringBuffer mStringBuffer = new StringBuffer();
        mStringBuffer.append("080835");
        mStringBuffer.append(",");
        mStringBuffer.append(wwwifi);
        mStringBuffer.append("#");
        orderUtil.send("IWAPNS", mStringBuffer.toString());

    }


    private void testzz() {

        String test = "IWBPGL,353456789012345,080835,6654111|133xxxxxxxx|D3590D54|0,6654112|133xxxxxxxx|D3590D54|0,6654113|133xxxxxxxx|D3590D54|0,6654114|133xxxxxxxx|D3590D54|0,6654115|133xxxxxxxx|D3590D54|0,6654116|133xxxxxxxx|D3590D54|0,6654117|133xxxxxxxx|D3590D54|0,6654118|133xxxxxxxx|D3590D54|0,6654119|133xxxxxxxx|D3590D54|0,6654120|133xxxxxxxx|D3590D54|0#";

//切换成数据库更好
        // String[] BPGL = intent.getStringExtra(LocationService.BPGL).substring(0, intent.getStringExtra(LocationService.BPGL).length() - 1).split(",");
        // String[] newBPGL = intent.getStringExtra(LocationService.BPGL).substring(29, intent.getStringExtra(LocationService.BPGL).length() - 1).split(",");
        String[] BPGL = test.substring(0, test.length() - 1).split(",");
        String[] newBPGL = test.substring(30, test.length() - 1).split(",");
        Log.e(TAG, "new的长度" + newBPGL.length + "-----" + newBPGL[0]);

//                    SharedPreferences guardian=mContext.getSharedPreferences("Guardian",MODE_PRIVATE);
//
//
//                    SharedPreferences.Editor GuaEditor= guardian.edit();

        mGuaDBHelper = new GuaDBHelper(MainActivity.this, "guardian", 1);
        String[] GuardianString;
        for (int i = 0; i < newBPGL.length; i++) {
            GuardianString = newBPGL[i].split("\\|");

            Log.e(TAG, "长度" + GuardianString.length + "兼职" + GuardianString[0]);
            Guavalues = new ContentValues();
            Guavalues.put("id", GuardianString[0]);
            Guavalues.put("GuardianPhone", GuardianString[1]);
            //UnicodeUtil.UNstringToUnicode(GuardianString[2].toString())
            Guavalues.put("GuardianName", "张三");
            mGuaDBHelper.insert(Guavalues);
        }

        mGuaDBHelper.query();

        List<GuardianModel> Gualist = new ArrayList<>(); /*调用query()获取Cursor*/
        Cursor guac = mGuaDBHelper.query();
        while (guac.moveToNext()) {

            GuardianModel g = new GuardianModel();
            g.setId(guac.getInt(guac.getColumnIndex("id")));
            g.setGuardianPhone(guac.getString(guac.getColumnIndex("GuardianPhone")));
            g.setGuardianName(guac.getString(guac.getColumnIndex("GuardianName")));
            Gualist.add(g);
        }


        for (GuardianModel ss : Gualist) {
            Log.e(TAG, "监护人" + ss.getId() + ss.getGuardianName() + ss.getGuardianPhone());
        }


    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "长按BACK键", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "长按BACK键-----------------------------");
            shortPress = false;
            //长按要执行的代码
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("紧急呼叫");
            builder.setMessage("即将进入紧急呼叫流程！按BACK键退出！");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "取消紧急呼叫！", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "开始紧急呼叫！", Toast.LENGTH_SHORT).show();

                }
            });
            dialog = builder.create();
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    if (iscall) {
                        Log.e(TAG, "开始紧急呼叫--" + iscall);
                        dialog.dismiss();
                        ArrayList<com.wite.positionerwear.service.Item> sss = mSosdbHelper.getItemList();
                        if (sss.size() > 0) {
                            Toast.makeText(mContext, "开始呼叫", Toast.LENGTH_SHORT).show();
                            //没有参数 你个智障
                            Intent test = new Intent();
                            test.setAction("android.intent.action.SOS_LONG_PRESS");
                            sendBroadcast(test);


                        } else {
                            Toast.makeText(mContext, "请设置紧急联系人", Toast.LENGTH_SHORT).show();
                        }


                        iscall = true;
                    } else {
                        iscall = true
                        ;
                    }
                }
            }, 5000);//3秒后执行Runnable中的run方法

            return true;
        }

        return false;
    }


    //监听按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_0) {
            startActivity(new Intent(this, Main2Activity.class));
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    iscall = false;
                    //    Toast.makeText(this, "取消紧急呼叫", Toast.LENGTH_SHORT).show();

                }
            }


            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                event.startTracking();
                if (event.getRepeatCount() == 0) {
                    shortPress = true;
                }
                return true;
            }

        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        }
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            if (iscall) {

                iscall = false;
            }
            startActivity(new Intent(MainActivity.this, MenuActivity.class));


        }
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//
//            startActivity(new Intent(this, AlarmClockActivity.class));
//        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

            startActivity(new Intent(this, MenuActivity.class));


        }


        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            startActivity(new Intent(this, MenuActivity.class));
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (shortPress) {
            } else {
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}



