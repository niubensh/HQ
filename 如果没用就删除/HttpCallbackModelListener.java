package com.wite.positionerwear.utils;

/**
 * Created by Administrator on 2017/9/18.
 */

/**
 * HttpURLConnection网络请求返回监听器
 */
public interface  HttpCallbackModelListener<T> {
    // 网络请求成功
    void onFinish(T response);

    // 网络请求失败
    void onError(Exception e);
}