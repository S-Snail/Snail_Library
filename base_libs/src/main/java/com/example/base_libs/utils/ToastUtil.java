package com.example.base_libs.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base_libs.R;
import com.example.base_libs.base.BaseLibsApplication;
import com.example.base_libs.bridge.AppBridge;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class ToastUtil {

    public static void show(String content) {
        showBgDark(content);
    }

    public static void showLong(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_LONG).show();
    }

    public static void showBgDark(String content) {
        showCusBg(content, R.layout.wgt_cus_toast_bg_dark);
    }

    public static void showDebug(String content) {
        if (AppBridge.Debug) {
            show(content);
        }
    }

    public static void showCusBg(String content, int layout) {
        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View rootView = LayoutInflater.from(getContext()).inflate(layout, null);
        TextView contentView = rootView.findViewById(R.id.content);
        contentView.setText(content);
        toast.setView(rootView);
        toast.show();
    }

    private static Context getContext() {
        return BaseLibsApplication.getApplication();
    }
}
