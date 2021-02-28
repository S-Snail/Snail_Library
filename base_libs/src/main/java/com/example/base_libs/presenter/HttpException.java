package com.example.base_libs.presenter;

import com.example.base_libs.utils.LogUtil;

import java.net.URL;

import okhttp3.Request;

public class HttpException extends RuntimeException {
    private String mt;
    private int errorCode;

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(Request request, Throwable cause) {
        super(formatMessage(request, cause));
    }

    private static String formatMessage(Request request, Throwable cause) {
        try {
            StringBuffer sf = new StringBuffer();
            URL url = request.url().url();
            sf.append("\n\n");
            sf.append("请求地址:");
            sf.append(url.getProtocol() + "://" + url.getHost() + url.getPath());
            sf.append("\n\n堆栈输出:\n");
            sf.append(LogUtil.getExceptionMessage(cause));
            return sf.toString();
        } catch (Exception ex) {
            return ex.getMessage();
        }

    }

    public HttpException(String message, Throwable cause, int errorCode, String mt) {
        super(message, cause);
        this.mt = mt;
        this.errorCode = errorCode;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
