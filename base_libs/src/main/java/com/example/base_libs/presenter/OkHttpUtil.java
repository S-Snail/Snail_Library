package com.example.base_libs.presenter;

import com.example.base_libs.bridge.AppBridge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {
    private static final int READ_TIMEOUT = 20;
    private static final int CONNECTION_TIMEOUT = 6;
    private static final int WRITE_TIMEOUT = 30;
    //构建拦截器
    static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient okHttpClient = null;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
            if (AppBridge.Debug) {
                builder.addInterceptor(loggingInterceptor);
            }
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }
}
