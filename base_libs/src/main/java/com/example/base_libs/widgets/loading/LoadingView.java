package com.example.base_libs.widgets.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.base_libs.R;

/**
 * @Author Snail
 * @Since 2021/3/3
 */
public class LoadingView extends LinearLayout {

    private LinearLayout llLoading;
    private LinearLayout llLoadError;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View loadingView = LayoutInflater.from(context).inflate(R.layout.loading_view, this);
        llLoading = loadingView.findViewById(R.id.ll_loading);
        llLoadError = loadingView.findViewById(R.id.ll_load_error);
    }

    //显示加载中
    public void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        llLoadError.setVisibility(View.GONE);
    }

    //显示加载错误
    public void showLoadingError() {
        llLoading.setVisibility(View.GONE);
        llLoadError.setVisibility(View.VISIBLE);
    }

}
