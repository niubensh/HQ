package com.wite.positionerwear.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
 	static final String TAG="SOS/BootBroadcastReceiver";
 
 	@Override
 	public void onReceive(Context context, Intent intent)
 	{
  		if (intent.getAction().equals(ACTION))
  		{
			Intent in = new Intent(context,BackgroundService.class);
			context.startService(in);
		}
 	}
}

