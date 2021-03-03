package com.example.snail_library;

import android.os.Bundle;
import android.view.View;

import com.example.base_libs.widgets.loading.LoadingLayout;
import com.zixiu.base.TestRepositoryUtil;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingLayout = findViewById(R.id.loadingLayout);
    }

    public void showLoading(View view) {
        loadingLayout.showLoading();
    }

    public void showData(View view) {
        loadingLayout.showData();
    }

    public void showError(View view) {
        loadingLayout.showLoadError();
    }
}
