package com.example.base_libs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.base_libs.bridge.AppBridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.ACTIVITY_SERVICE;

@SuppressWarnings("all")
public class DeviceUtil {
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int scaledSize(int size, float scale) {
        return (int) (size * scale + 0.5F);
    }

    public static int scaledSize(Context context, double scale) {
        return (int) (DeviceUtil.getScreenWidth(context) * scale + 0.5f);
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

    public static void setWindowWidthPercent(Context context, Window window, float percent) {
        int width = getScreenWidth(context);
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = (int) (width * percent);
        window.setAttributes(p);
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null || runningProcesses.isEmpty()) return false;
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isActivityForeground(Context context, String className) {
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

    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
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
                PackageManager pm = AppBridge.mApp.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(AppBridge.mApp.getPackageName(), 0);
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
                PackageManager pm = AppBridge.mApp.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(AppBridge.mApp.getPackageName(), 0);
                versionCode = pi.versionCode;
            } catch (Exception e) {
//                LogUtil.logError("AppUtils.getAppVersionCode", e);
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
    public static String getProcessName(Context cxt) {
        try {
            ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
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
     * @param cxt
     * @return
     */
    public static boolean isOnMainProccess(Context cxt) {
        try {
            return cxt.getPackageName().equals(getProcessName(cxt));
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
     * @param mContext
     * @return
     */
    public static boolean hasFloatWindowPermission(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0动态申请悬浮窗权限
            if (!Settings.canDrawOverlays(mContext)) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager manager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                try {
                    Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                    return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, 24, Binder.getCallingUid(), mContext.getPackageName());
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
     * @param context
     * @return
     */
    public static void aMapSHA1() {
        try {
            PackageInfo info = AppBridge.mApp.getPackageManager().getPackageInfo(
                    AppBridge.mApp.getPackageName(), PackageManager.GET_SIGNATURES);
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
//            LogUtil.log("aMapSHA1:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
