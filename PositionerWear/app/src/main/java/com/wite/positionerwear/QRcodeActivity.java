package com.wite.positionerwear;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ImageView;

import com.wite.positionerwear.utils.ZXingUtils;

public class QRcodeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imageView = (ImageView) findViewById(R.id.two_code);
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //nox 有iemi号
      String  imei = TelephonyMgr.getDeviceId();

        Bitmap bitmap = ZXingUtils.createQRImage(imei,600, 600);
        imageView.setImageBitmap(bitmap);
    }
}
