package com.example.base_libs.widgets.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author Snail
 * @Since 2021/3/3
 */
public class LoadingLayout extends FrameLayout {

    private LoadingView loadingView;

    public LoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadingView = new LoadingView(getContext());
        addView(loadingView);
        showLoading();
    }

    public void showData() {
        setLoadingViewVisibility(View.GONE);
    }

    public void showLoading() {
        setLoadingViewVisibility(View.VISIBLE);
        loadingView.showLoading();
    }

    public void showLoadError() {
        setLoadingViewVisibility(View.VISIBLE);
        loadingView.showLoadingError();
    }

    private void setLoadingViewVisibility(int visibility) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof LoadingView) {
                child.setVisibility(visibility);
            } else {
                if (visibility == View.VISIBLE) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
