package com.wite.positionerwear.utils;

/**
 * Created by Administrator on 2017/9/18.
 */

/**
 * HttpURLConnection网络请求返回监听器
 */
public interface HttpCallbackStringListener {
    // 网络请求成功
    void onFinish(String response);

    // 网络请求失败
    void onError(Exception e);
}