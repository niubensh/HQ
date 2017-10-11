package com.wite.positionerwear.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.wite.positionerwear.DBHelper.SOSDBHelper;
import com.wite.positionerwear.R;

import java.util.ArrayList;
import java.util.HashMap;


public class BackgroundService extends Service {

	static final String TAG = "SOS/Service";

	static final String ACTION_SOS_START = "android.intent.action.SOS_LONG_PRESS";
	static final String ACTION_SOS_STOP = "ACTION_SOS_STOP";
	static final String ACTION_SEND_SOS_SMS = "ACTION_SEND_SOS_SMS";
	static final String ACTION_MAKE_SOS_CALL = "ACTION_MAKE_SOS_CALL";
	static final String CALL_ALIVE_ACTION = "ACTION_CALL_ALIVE";
	static final String SOS_CALL_WAITING_ACTION = "ACTION_SOS_CALL_WAITING";
	static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";
	static final String ACTION_SEND_SMS_DELIVER = "ACTION_SEND_SMS_DELIVER";

	static final String SOS_SOUND_ALERT = "SOS_SOUND_ALERT";

	private static final int DEFAULT_SMS_DELAY_TIME = 0 * 1000;// 5 second
	private static final int DEFAULT_CALL_DELAY_TIME = 0 * 1000;// 10 second
	private static final int DEFAULT_CALL_WAITING_DELAY_TIME = 60 * 1000;// 10
																			// second

	private SOSDBHelper mDBHelper = null;
	private ArrayList<Item> sosItemList = null;
	SoundPool soundPool;
	HashMap<String, Integer> soundMap = new HashMap<String, Integer>();

	private boolean bSosSoundPlaying = false;
	private boolean bSosMakeCall = false;

	private int mSteamId = -1;
	private AlarmManager mAM;
	private PendingIntent mPendingIntent;
	private SOSReceiver mSosReceiver;
	private PhoneListener mPhoneStateListener;
	private static int mSosListIndex = -1;
	private static String mSosCallNumber = null;
	private static boolean mSosCallDialing = false;

	static AudioManager mAudioManager;

	@Override
	public void onCreate() {
		super.onCreate();

		mAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mAudioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);

		Log.d(TAG, "@@@@@@@@@@@onCreate");

		// initSound();

		mSosReceiver = new SOSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SOS_START);
		filter.addAction(ACTION_SOS_STOP);
		filter.addAction(ACTION_MAKE_SOS_CALL);
		filter.addAction(ACTION_SEND_SOS_SMS);
		filter.addAction(SOS_CALL_WAITING_ACTION);
		filter.addAction(CALL_ALIVE_ACTION);
		filter.addAction(ACTION_SEND_SMS);
		filter.addAction(ACTION_SEND_SMS_DELIVER);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mSosReceiver, filter);

		mDBHelper = new SOSDBHelper(this);

		// mPhoneStateListener=new PhoneListener();
		// mPhoneStateListener=new PhoneListener();
		// TelephonyManager tm =
		// (TelephonyManager)this.getSystemService(Service.TELEPHONY_SERVICE);
		// tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(mSosReceiver);

		stopSosSound();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		flags = START_STICKY; // auto restart flag
		return super.onStartCommand(intent, flags, startId);

	}

	private void registetPhoneListener() {

		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Service.TELEPHONY_SERVICE);

		mPhoneStateListener = new PhoneListener();
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void unRegistetPhoneListener() {
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Service.TELEPHONY_SERVICE);
		if (mPhoneStateListener != null) {
			tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
			mPhoneStateListener = null;
		}

	}

	private void initSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundMap.put(SOS_SOUND_ALERT, soundPool.load(this, R.raw.sos, 1));
	}

	private boolean isSpeakerOn() {
		boolean ret = mAudioManager.isSpeakerphoneOn();
		Log.d(TAG, "**************** Speaker state =" + ret);

		return ret;
	}

	private void turnOnSpeaker() {
		/*
		 * if(!isSpeakerOn()) { mAudioManager.setSpeakerphoneOn(true);
		 * mAudioManager.setMode(AudioManager.MODE_IN_CALL); Log.d(TAG,
		 * "****************turnOnSpeaker,on"); }
		 */
	}

	private void turnOffSpeaker() {
		/*
		 * if(isSpeakerOn()) { mAudioManager.setSpeakerphoneOn(false);
		 * 
		 * Log.d(TAG, "****************turnOnSpeaker,off"); }
		 */
	}

	private int getSoundId(String soundName) {
		if (soundMap != null && soundMap.containsKey(soundName)) {
			return soundMap.get(soundName);
		} else {
			return -1;
		}
	}

	private void playSosSound() {
		Log.d(TAG, "playSosSound,play state=" + bSosSoundPlaying);

		if (!bSosSoundPlaying) {
			if (soundPool != null && soundMap != null) {
				mSteamId = soundPool.play(getSoundId(SOS_SOUND_ALERT), 1, 1, 0,
						-1, 1);// loop

				Log.d(TAG, "playSosSound,play,ret=" + mSteamId);

				bSosSoundPlaying = true;
			}
		}
	}

	private void stopSosSound() {

		Log.d(TAG, "stopSosSound,play state=" + bSosSoundPlaying);

		if (bSosSoundPlaying) {
			if (soundPool != null && soundMap != null) {
				Log.d(TAG, "stopSosSound,stop");

				if (mSteamId != -1) {
					soundPool.stop(mSteamId);// loop
					mSteamId = -1;
				}
				bSosSoundPlaying = false;
			}
		}
	}

	private void startSosSendSmsTimer() {
		/*long currentTime = System.currentTimeMillis();
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_SEND_SOS_SMS), PendingIntent.FLAG_CANCEL_CURRENT);
		mAM.setExact(AlarmManager.RTC_WAKEUP, currentTime + DEFAULT_SMS_DELAY_TIME,
				mPendingIntent);*/
		
		sendBroadcast(new Intent(ACTION_SEND_SOS_SMS));
		Log.d(TAG, "start Sos Send Sms Timer");

	}

	private void startSosMakeCallTimer() {
		/*long currentTime = System.currentTimeMillis();
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_MAKE_SOS_CALL), PendingIntent.FLAG_CANCEL_CURRENT);
		mAM.setExact(AlarmManager.RTC_WAKEUP, currentTime + DEFAULT_CALL_DELAY_TIME,
				mPendingIntent);*/
		
		sendBroadcast(new Intent(ACTION_MAKE_SOS_CALL));
		Log.d(TAG, "start Sos Make Call Timer");

	}

	private void cancelTimer() {
		if (mPendingIntent != null) {
			Log.d(TAG,
					"Cancel Timer,PendingIntent=" + mPendingIntent.toString());

			mAM.cancel(mPendingIntent);
			mPendingIntent = null;

		}
	}

	private void StartSosCallWaitingTimer() {
		/*long currentTime = System.currentTimeMillis();
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				SOS_CALL_WAITING_ACTION), PendingIntent.FLAG_CANCEL_CURRENT);
		mAM.setExact(AlarmManager.RTC_WAKEUP, currentTime
				+ DEFAULT_CALL_WAITING_DELAY_TIME, mPendingIntent);*/

		sendBroadcast(new Intent(SOS_CALL_WAITING_ACTION));
		Log.d(TAG, "StartSosCallWaitingTimer");
		Log.d(TAG, "Start Sos Call Waiting Timer");
	}

	public class SOSReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context1, Intent intent1) {

			String action = intent1.getAction();

			Log.d(TAG, "**********************SOSReceiver,onReceive, action="
					+ action);

			if (action.equals(ACTION_SOS_START)) {
				// Log.d(TAG, "@@@@@@@@@@@SOSReceiver");

				if (!bSosMakeCall) {
					Log.d(TAG, "onReceive: 进入方法" );
					sosItemList = mDBHelper.getItemList();
					Log.d(TAG, "联系人长度"+sosItemList.size() );

					resetCallNumber();

					mSosCallDialing = false;

					if (mDBHelper.isSosEnable(context1)) {
						bSosMakeCall = true;
						playSosSound();
						startSosSendSmsTimer();
					}
				} else {
					Log.d(TAG, "@@@@@@@@@@@@@SOS working,not allow!!!");
				}

			} else if (action.equals(ACTION_SOS_STOP)) {
				if (bSosMakeCall) {
					cancelTimer();
					stopSosSound();

					bSosMakeCall = false;
					mSosCallDialing = false;

					unRegistetPhoneListener();
				}

			} else if (action.equals(ACTION_SEND_SOS_SMS)) {
				cancelTimer();
					
				startSosMakeCallTimer();
				
				sosSendSms();
					
			} else if (action.equals(ACTION_MAKE_SOS_CALL)) {

				stopSosSound();
				cancelTimer();

				if (sosItemList == null || sosItemList.size() == 0) {
					bSosMakeCall = false;

					mSosCallDialing = false;

					notificateUser(R.string.str_make_call_fail_hint);
				} else {

					if (sosMakeCall()) {
						// StartSosCallWaitingTimer();
					} else {
						bSosMakeCall = false;

						mSosCallDialing = false;

						turnOffSpeaker();

						unRegistetPhoneListener();

						Log.d(TAG, "SOSReceiver,stop call");

					}
				}
			}
			/*
			 * else if (action.equals(SOS_CALL_WAITING_ACTION)) { cancelTimer();
			 * 
			 * if(bSosMakeCall) { if(sosMakeCall()) {
			 * StartSosCallWaitingTimer(); } else { bSosMakeCall=false;
			 * 
			 * unRegistetPhoneListener();
			 * 
			 * Log.d(TAG, "SOSReceiver,stop call"); } } }
			 */
			else if (action.equals(CALL_ALIVE_ACTION)) {

				if (bSosMakeCall && mSosCallDialing) {
					Log.d(TAG, "call connected! need stop make any call... ");

					cancelTimer();
					stopSosSound();
					resetCallNumber();

					bSosMakeCall = false;
					mSosCallDialing = false;

					unRegistetPhoneListener();

					Log.d(TAG, "SOSReceiver,stop call");
				}
			} else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				if (bSosMakeCall) {
					registetPhoneListener();
				}
			} else if (action.equals(ACTION_SEND_SMS)) {
				Log.d(TAG, "send sms ok....");
				try {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						notificateUser(R.string.str_sms_sent_success);
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						notificateUser(R.string.str_sms_sent_failed);
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						break;
					}
				} catch (Exception e) {
					e.getStackTrace();
				}
			} else if (action.equals(ACTION_SEND_SMS_DELIVER)) {
				Log.d(TAG, "sos sms is received !");
				String numStr = intent1.getStringExtra("NAME");
				try {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						notificateUser(String.format(
								getResources().getString(
										R.string.str_sms_rec_success), numStr));
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						notificateUser(String.format(
								getResources().getString(
										R.string.str_sms_rec_failed), numStr));
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						break;
					}
				} catch (Exception e) {
					e.getStackTrace();
				}
			}

		}
	}

	private void notificateUser(String hintStr) {
		Toast.makeText(this, hintStr, Toast.LENGTH_LONG).show();
	}

	private void notificateUser(int resID) {
		Toast.makeText(this, getResources().getText(resID).toString(),
				Toast.LENGTH_LONG).show();
	}

	private void resetCallNumber() {
		mSosCallNumber = null;
		mSosListIndex = -1;
	}

	private void getCallnumber() {
		if (sosItemList != null && sosItemList.size() > 0) {
			mSosListIndex++;
			if (mSosListIndex < sosItemList.size()) {
				mSosCallNumber = sosItemList.get(mSosListIndex).getNumber();
			} else {
				resetCallNumber();
			}
		}
	}

	private void sosSendSms() {
		String addressStr = "";
		String nameStr = null;
		String contentStr = mDBHelper.getSMSContent("0");
			
		if (contentStr == null || "".equals(contentStr)) {
			notificateUser(R.string.str_set_sms_content);
			return;
		}

		if (sosItemList.size() > 0) {

			Log.d(TAG, "Start send sms ......");

			for (int i = 0; i < sosItemList.size(); i++) {
				addressStr = sosItemList.get(i).getNumber();
				nameStr = sosItemList.get(i).getName();

				SmsManager smsManager = SmsManager.getDefault();

				Intent itSend = new Intent(ACTION_SEND_SMS);
				PendingIntent mSendPI = PendingIntent.getBroadcast(this, 0,
						itSend, 0);

				Intent itDeliver = new Intent(ACTION_SEND_SMS_DELIVER);
				if (nameStr == null || "".equals(nameStr)) {
					itDeliver.putExtra("NAME", "<" + addressStr + ">");
				} else {
					itDeliver.putExtra("NAME", "<" + nameStr + ">");
				}

				PendingIntent mDeliverPI = PendingIntent.getBroadcast(this, 0,
						itDeliver, 0);

				smsManager.sendTextMessage(addressStr, null, contentStr,
						mSendPI, mDeliverPI);
				Log.d(TAG, "send sms num." + i + " ,dest addr=" + addressStr);
			}

			Log.d(TAG, "send sms complete.");
			// Toast.makeText(this, contentStr, Toast.LENGTH_LONG).show();
		} else {
			// Toast.makeText(this, contentStr, Toast.LENGTH_LONG).show();
		}

	}

	private boolean sosMakeCall() {
		boolean bSuccess = false;

		getCallnumber();

		Log.d(TAG, "start making call,number=" + mSosCallNumber);

		if (mSosCallNumber != null) {
			turnOnSpeaker();

			bSuccess = true;
			Intent myIntentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:" + mSosCallNumber));

            myIntentDial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(myIntentDial);
			Log.d(TAG, "making call successed!");
		}

		return bSuccess;
	}

	class PhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			Log.d(TAG, "11111111111111 onCallStateChanged,state=" + state);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				turnOffSpeaker();

				if (bSosMakeCall && mSosCallDialing) {
					mSosCallDialing = false;
					sendBroadcast(new Intent(ACTION_MAKE_SOS_CALL));
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				if (bSosMakeCall) {
					mSosCallDialing = true;
				}

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				break;
			}
		}
	}

	class PhoneListener2 extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			Log.d(TAG, "22222222222222222 onCallStateChanged,state=" + state);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				turnOffSpeaker();

				if (bSosMakeCall && mSosCallDialing) {
					mSosCallDialing = false;
					sendBroadcast(new Intent(ACTION_MAKE_SOS_CALL));
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				if (bSosMakeCall) {
					mSosCallDialing = true;

					turnOnSpeaker();
				}

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				break;
			}
		}
	}

}
