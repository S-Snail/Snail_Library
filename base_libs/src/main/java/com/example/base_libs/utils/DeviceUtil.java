package com.example.base_libs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.base_libs.base.BaseLibsApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class DeviceUtil {
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static float density = 0;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = BaseLibsApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        float scale = BaseLibsApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5F);
    }

    public static int px2sp(float pxValue) {
        float scale = BaseLibsApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5F);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = BaseLibsApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth() {
        if (screenWidth != 0) return screenWidth;
        initWidHeight();
        return screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight != 0) return screenHeight;
        initWidHeight();
        return screenHeight;
    }

    private static void initWidHeight() {
        WindowManager manager = (WindowManager) BaseLibsApplication.getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;
    }

    public static int scaledSize(int size, float scale) {
        return (int) (size * scale + 0.5F);
    }

    public static int scaledSize(double scale) {
        return (int) (getScreenWidth() * scale + 0.5f);
    }

    public static float getDensity(Context context) {
        if (density != 0) return density;
        initWidHeight();
        return density;
    }

    public static void setWindowWidthPercent(Window window, float percent) {
        int width = getScreenWidth();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = (int) (width * percent);
        window.setAttributes(p);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Resources resources = BaseLibsApplication.getApplication().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static void closeKeyboard(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        if (imm.isActive()) {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) BaseLibsApplication.getApplication().getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null || runningProcesses.isEmpty()) return false;
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(BaseLibsApplication.getApplication().getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(BaseLibsApplication.getApplication().getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isActivityForeground(Activity context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }

    public static void setFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /***
     * 安卓版本号
     */
    public static String getAndroidRelease() {
        return Build.VERSION.RELEASE;
    }

    /***
     * 安卓sdk版本号
     */
    public static int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getMobileBrand() {
        return Build.BRAND;
    }

    public static String getMobileModel() {
        return Build.MODEL;
    }

    /**
     * 获取android版本名称
     */
    private static String versionName = "";

    public static String getAppVersionName() {
        if ("".equals(versionName)) {
            try {
                // ---get the package info---
                PackageManager pm = BaseLibsApplication.getApplication().getPackageManager();
                PackageInfo pi = pm.getPackageInfo(BaseLibsApplication.getApplication().getPackageName(), 0);
                versionName = pi.versionName;
                if (versionName == null || versionName.length() <= 0) {
                    return "";
                }
            } catch (Exception e) {
                Log.e("VersionInfo", "Exception", e);
            }
        }
        return versionName;
    }

    /**
     * 获取versionCode
     */
    private static int versionCode = 0;

    public static int getAppVersionCode() {
        if (versionCode == 0) {
            try {
                PackageManager pm = BaseLibsApplication.getApplication().getPackageManager();
                PackageInfo pi = pm.getPackageInfo(BaseLibsApplication.getApplication().getPackageName(), 0);
                versionCode = pi.versionCode;
            } catch (Exception e) {
                LogUtil.logError("AppUtils.getAppVersionCode", e);
            }
        }
        return versionCode;
    }

    public static String getAppChannel() {
        return "main";
    }

    /***
     * 获取当前进程名字
     */
    public static String getProcessName() {
        try {
            ActivityManager am = (ActivityManager) BaseLibsApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
                if (runningApps == null) {
                    return null;
                }
                for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                    if (procInfo.pid == android.os.Process.myPid()) {
                        return procInfo.processName;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /***
     * 判断是否在主进程
     * @param
     * @return
     */
    public static boolean isOnMainProccess() {
        try {
            return BaseLibsApplication.getApplication().getPackageName().equals(getProcessName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /***
     * 获取RandomUUID
     * @return
     */
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    /**
     * 判断cpu是否为电脑来判断 模拟器
     *
     * @return true 为模拟器
     */
    public static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /***
     * 判断是否在主线程
     * @return
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /***
     * 检查悬浮窗权限
     * @param
     * @return
     */
    public static boolean hasFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0动态申请悬浮窗权限
            if (!Settings.canDrawOverlays(BaseLibsApplication.getApplication())) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager manager = (AppOpsManager) BaseLibsApplication.getApplication().getSystemService(Context.APP_OPS_SERVICE);
                try {
                    Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                    return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, 24, Binder.getCallingUid(), BaseLibsApplication.getApplication().getPackageName());
                } catch (Exception e) {

                }
            }
        }
        return true;
    }

    /***
     * 申请悬浮窗权限
     * @param mActivity
     * @param resultCode
     */
    public static void startFloatWindowPermissionReq(Activity mActivity, int resultCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, resultCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /***
     * 高德地图创建key使用
     * @param
     * @return
     */
    public static void aMapSHA1() {
        try {
            PackageInfo info = BaseLibsApplication.getApplication().getPackageManager().getPackageInfo(
                    BaseLibsApplication.getApplication().getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            result = result.substring(0, result.length() - 1);
            LogUtil.log("aMapSHA1:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String CPUABI = "";

    public static String getCPUABI() {
        if (TextUtils.isEmpty(CPUABI)) {
            try {
                String os_cpuabi = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream())).readLine();
//                if (os_cpuabi.contains("x86")) {
//                    CPUABI = "x86";
//                } else if (os_cpuabi.contains("armeabi-v7a") || os_cpuabi.contains("arm64-v8a")) {
//                    CPUABI = "armeabi-v7a";
//                } else {
//                    CPUABI = "armeabi";
//                }
                CPUABI = os_cpuabi;
            } catch (Exception e) {
                CPUABI = "armeabi";
            }
        }
        return CPUABI;
    }
}
