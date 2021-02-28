package com.example.base_libs.bridge;

import android.app.Application;

import com.example.base_libs.utils.LogUtil;

public class AppBridge {
    /***
     * 上下文
     */
    public static Application mApp;
    /***
     * debug模式
     */
    public static boolean Debug;
    /***
     * api host
     */
    public static String apiUrl;
    /***
     * 图片 host
     */
    public static String imgUrl = "https://test.images.com/";
    /***
     * 客户端ID
     */
    public static String appId;
    /***
     * 用户id
     */
    public static String userId;

    public static String appkey;
    public static String url_appCode;
    public static String url_token;
    public static String url_authCode;

    /***
     * 用户token，接口请求中的通用参数
     */
    public static String tk;

    /***
     * 基础支持库的初始化
     * @param _mApplication
     * @param _debug
     * @param _apiUrl
     * @param _appId
     */
    public static void init(Application _mApplication, boolean _debug, String _apiUrl, String _appId) {
        mApp = _mApplication;
        Debug = _debug;
        apiUrl = _apiUrl;
        appId = _appId;
    }

    //bugly
    public static String buglyAppId = "";

    /***
     * 框架中捕获严重级别较高的异常回调
     */
    public static LogUtil.OnSeverityProblemListenner onSeverityProblemListenner;
}
