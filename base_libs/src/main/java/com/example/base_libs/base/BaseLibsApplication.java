package com.example.base_libs.base;

import android.app.Application;
import android.text.TextUtils;

import com.example.base_libs.bridge.AppBridge;
import com.example.base_libs.utils.AppFrontBackHelper;
import com.example.base_libs.utils.DeviceUtil;
import com.tencent.bugly.crashreport.CrashReport;

public abstract class BaseLibsApplication extends Application {
    private static BaseLibsApplication mApplication;
    private AppFrontBackHelper appFrontBackHelper;

    public static BaseLibsApplication getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        appFrontBackHelper = new AppFrontBackHelper();
        initBaseLibsConfig();
        initExternalModules();
    }

    public abstract void initBaseLibsConfig();

    public void setOnAppStatusListener(AppFrontBackHelper.OnAppStatusListener onAppStatusListener) {
        appFrontBackHelper.register(onAppStatusListener);
    }

    /***
     * 要在所有进程调用
     */
    private static void initExternalModules() {
        //bugly
        if (!TextUtils.isEmpty(AppBridge.buglyAppId)) {
            // 设置是否为上报进程
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplication());
            strategy.setAppVersion(DeviceUtil.getAppVersionName());
            strategy.setAppChannel(DeviceUtil.getAppChannel());
            strategy.setUploadProcess(DeviceUtil.isOnMainProccess());
            CrashReport.initCrashReport(getApplication(), AppBridge.buglyAppId, AppBridge.Debug, strategy);
            if (!TextUtils.isEmpty(AppBridge.userId)) {
                CrashReport.setUserId(AppBridge.userId);
            }
        }
    }

    public boolean isAppIsInBackground() {
        return DeviceUtil.isAppIsInBackground();
    }
}
