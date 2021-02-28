package com.example.base_libs.presenter;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class ServerResponse<T> {
    private String time;
    private boolean isSuccess;
    private SimpleMsg simpleMsg;
    private T data;
    private String mainJson = "";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public SimpleMsg getSimpleMsg() {
        return simpleMsg;
    }

    public void setSimpleMsg(SimpleMsg simpleMsg) {
        this.simpleMsg = simpleMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMainJson() {
        return mainJson;
    }

    public void setMainJson(String mainJson) {
        this.mainJson = mainJson;
    }
}
