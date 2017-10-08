package com.wite.positionerwear.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 * WIFI_STATE_DISABLING = 0	WIFI网卡正在关闭
 * WIFI_STATE_DISABLED = 1	WIFI网卡不可用
 * WIFI_STATE_ENABLING = 2	WIFI网正在打开 （WIFI启动需要一段时间）
 * WIFI_STATE_ENABLED = 3	WIFI网卡可用
 * WIFI_STATE_UNKNOWN = 4	未知网卡状态
 * WIFI_AP_STATE_DISABLING = 10	WIFI热点正在关闭
 * WIFI_AP_STATE_DISABLED = 11	WIFI热点不可用
 * WIFI_AP_STATE_ENABLING = 12	WIFI热点正在打开
 * WIFI_AP_STATE_ENABLED = 13	WIFI热点可用
 */

public class WifiUtil {
    //定义一个WifiManager对象
    private WifiManager mWifiManager;
    //定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    WifiManager.WifiLock mWifiLock;

    public WifiUtil(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭wifi
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    //锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    //解锁wifiLock
    public void releaseWifiLock() {
        //判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    //指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    //查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");

        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    //得到wifiInfo的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    //添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    //断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }


    /**
     *分为三种情况：1没有密码;2用wep加密;3用wpa加密
     *连接至指定WIFI
     *
     * */

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (Type) {
            case 1:
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
            case 2:
                configuration.hiddenSSID = false;
                configuration.wepKeys[0] = "\"" + Password +"\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                break;
            case 3:

                configuration.preSharedKey = "\"" + Password + "\"";
                configuration.hiddenSSID = false;
                // configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return  configuration;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig:
                existingConfigs) {
            if (existingConfig.SSID.equals("\"" +SSID+"\"")) {
                return  existingConfig;
            }

        }
        return null;
    }
}
