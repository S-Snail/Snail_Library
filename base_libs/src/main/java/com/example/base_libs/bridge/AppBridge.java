package com.example.base_libs.bridge;

import android.app.Application;

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
    public static String imgUrl = "https://img.51zixiu.com/";
    /***
     * 客户端ID
     */
    public static String appId;

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
}
