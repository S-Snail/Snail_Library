package com.example.snail_library;

import android.os.Bundle;

import com.zixiu.base.TestRepositoryUtil;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestRepositoryUtil.log("test");
    }
}
