package com.example.base_libs.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.base_libs.bridge.AppBridge;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LogUtil {
    private static final String TAG_MSG = "tag_msg";
    private static final String TAG_ERROR = "tag_error";

    /***
     * 打印错误
     * @param identify 报错标示 可写类名+方法名；
     * @param ex
     */
    public static void logError(String identify, Throwable ex) {
        logError(identify, ex, false);
    }

    /***
     * 默认用TAG_MSG标签来输出
     * @param content
     */
    public static void log(String content) {
        log(TAG_MSG, content);
    }

    public static void log(String tag, String content) {
        log(tag, content, false);
    }

    /***
     * 打印方法
     * @param tag
     * @param content
     */
    public static void log(String tag, String content, boolean isError) {
        if (AppBridge.Debug) {
            if (isError) {
                Log.e(tag, content);
            } else {
                Log.i(tag, content);
            }
        }
    }

    /***
     * 打印错误详细信息
     * @param ex
     * @return
     */
    public static String getExceptionMessage(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return info;
    }

    public interface OnSeverityProblemListenner {
        void onSeverityProblem(Throwable ex);
    }

    /***
     *
     * @param identify 报错标示 可写类名+方法名；
     * @param ex
     * @param isUpload 是否上传 只要阻碍了正常业务流程的异常都要上传；
     */
    public static void logError(String identify, Throwable ex, boolean isUpload) {
        logError(identify, "", ex, isUpload);
    }

    public static void logError(String identify, String message, Throwable ex, boolean isUpload) {
        if (isUpload) {
            String rstr = "错误严重级别较高！！！请立刻修复！！！全局搜索（" + identify + "）找到捕获位置";
            if (!TextUtils.isEmpty(message)) {
                rstr += "\n" + message;
            }
            RuntimeException runtimeException = new RuntimeException(rstr, ex);
            log(TAG_ERROR, getExceptionMessage(runtimeException), true);
            if (AppBridge.onSeverityProblemListenner != null) {
                try {
                    AppBridge.onSeverityProblemListenner.onSeverityProblem(ex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CrashReport.postCatchedException(runtimeException);
            final String finalRstr = rstr;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(finalRstr);
                }
            });
        } else {
            log(TAG_ERROR, identify + "\n" + getExceptionMessage(ex), true);
        }
    }
}
