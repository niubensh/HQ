package com.wite.positionerwear.utils;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

/**
 * Created by Administrator on 2017/9/21.
 */

public class MyPhoneStateListener extends PhoneStateListener
{
  /* Get the Signal strength from the provider,
   * each tiome there is an update
   *从得到的信号强度,每个tiome供应商有更新
   */

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
        super.onSignalStrengthsChanged(signalStrength);
        //信号强度换算公式
        int astSignal = -113 + 2*signalStrength.getGsmSignalStrength();
       // gsm.setText("GSM 信号强度asu :" + signalStrength.getGsmSignalStrength() +"_dBm :"+astSignal);


    }

};
