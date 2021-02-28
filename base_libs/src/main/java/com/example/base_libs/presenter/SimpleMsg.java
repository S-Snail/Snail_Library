package com.example.base_libs.presenter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author Snail
 * @Since 2021/2/28
 * 业务处理失败，或者业务报错时的提示
 */
public class SimpleMsg implements Parcelable {

    private int code;
    private String content;
    private Object arg;

    protected SimpleMsg() {
    }

    public SimpleMsg(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
