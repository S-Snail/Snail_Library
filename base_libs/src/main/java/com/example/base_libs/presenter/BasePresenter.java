package com.example.base_libs.presenter;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.base_libs.bridge.AppBridge;
import com.example.base_libs.utils.DateUtil;
import com.example.base_libs.utils.DeviceUtil;
import com.example.base_libs.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Iterator;

import javax.crypto.BadPaddingException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class BasePresenter {

    private static final String LOGTAG = "zx_api";
    /***
     * okhttp
     */
    private static MediaType formContentType = MediaType.parse("application/json;charset=utf-8");
    /***
     * 线程存储区，用户处理异常返回情况
     */
    private static final ThreadLocal<SimpleMsg> localMsg = new ThreadLocal<>();

    private static Gson gson = new Gson();

    protected void startAsynWork(final AsynWork asynWork, @Nullable ViewCallback viewCallback) {
        final CallbackHandler callbackHandler = new CallbackHandler(asynWork, viewCallback);
        callbackHandler.sendOnStart();
        AppThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Object data = asynWork.doWork();
                    //判断是否有需要处理的异常情况，如果有，直接实现onFailed()方法
                    if (localMsg.get() != null) {
                        callbackHandler.sendOnFailed(localMsg.get());
                        return;
                    }
                    //数据正常返回直接实现onSuccess()方法
                    callbackHandler.sendOnSuccess(data);
                } catch (Exception e) {
                    LogUtil.logError("BasePresenter.startAsynWork()", e, true);
                    callbackHandler.sendOnFailed(new SimpleMsg(ErrConfig.UI_LOGIC_CODE, ErrConfig.UI_LOGIC_MSG));
                } finally {
                    //清空线程存储区
                    localMsg.remove();
                }
            }
        });
    }

    /**
     * 调用此方法会在流程走完实现ui层的onfaild方法
     *
     * @param simpleMsg
     */
    protected void sendErrorMsgToUI(SimpleMsg simpleMsg) {
        localMsg.set(simpleMsg);
    }

    /***
     * 进行api请求，注意：框架层会封装业务错误场景，进行处理
     * @param mt 方法名，去ApiConfig里面配置
     * @param parameters 业务参数
     * @param typeToken 需要反射的类，如果data是JsonArray，传item的类
     */
    protected static ServerResponse doApiPostRequest(String mt, @Nullable BizParameters parameters, Type typeToken) {
        return doApiRequest(mt, parameters, typeToken, true);
    }

    protected static ServerResponse doApiGetRequest(String mt, @Nullable BizParameters parameters, Type typeToken) {
        return doApiRequest(mt, parameters, typeToken, false);
    }

    private static ServerResponse doApiRequest(String mt, @Nullable BizParameters parameters, Type typeToken, boolean isPost) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            String appkey = AppBridge.appkey;
            if (parameters != null && parameters.getParam("appkey") != null) {
                appkey = parameters.getParam("appkey").toString();
                parameters.remove("appkey");
            }
            String result = getHttpResponse(mt, parameters, appkey, isPost);
            serverResponse.setMainJson(result);
            JsonElement je = gson.fromJson(result, JsonElement.class);
            if (je.isJsonObject()) {
                JsonObject mainJson = je.getAsJsonObject();
                int code = 0;
                String msg = "";
                String prompt = "";
                boolean privacy = false;
                if (mainJson.has("code")) {
                    code = mainJson.get("code").getAsInt();
                }
                if (mainJson.has("message")) {
                    msg = mainJson.get("message").getAsString();
                }
                if (mainJson.has("prompt")) {
                    prompt = mainJson.get("prompt").getAsString();
                }
                if (mainJson.has("time")) {
                    serverResponse.setTime(mainJson.get("time").getAsString());
                    //校准时间
                    DateUtil.syncServerTimeMillis(serverResponse.getTime());
                }
                if (mainJson.has("privacy")) {
                    privacy = mainJson.get("privacy").getAsBoolean();
                }
                //处理数据
                if (code == 200) {
                    //服务返回正确数据的处理
                    serverResponse.setSuccess(true);
                    serverResponse.setSimpleMsg(new SimpleMsg(code, msg));
                    if (mainJson.has("data") && typeToken != null) {
                        JsonElement jsonData = mainJson.get("data");
                        if (privacy) {
                            String decryptStr = jsonData.getAsString();
                            try {
                                jsonData = gson.fromJson(decryptStr, JsonElement.class);
                            } catch (Exception ex) {
                                serverResponse.setData(decryptStr);
                                return serverResponse;
                            }
                        }
                        try {
                            serverResponse.setData(gson.fromJson(jsonData, typeToken));
                        } catch (Exception ex) {
                            serverResponse.setData(null);
                        }
                    }
                } else {
                    //服务返回业务错误，框架暂不处理，业务层自己处理各种业务code和返回消息，通过sendErrorMsgToUI()方法通知ui层更新
                    serverResponse.setSuccess(false);
                    serverResponse.setSimpleMsg(new SimpleMsg(code, msg));
                }
            }
        } catch (Exception ex) {
            serverResponse.setSuccess(false);
            if (ex instanceof HttpException) {
                serverResponse.setSimpleMsg(new SimpleMsg(((HttpException) ex).getErrorCode(), ex.getMessage()));
            } else if (ex instanceof BadPaddingException) {
                serverResponse.setSimpleMsg(new SimpleMsg(ErrConfig.UI_LOGIC_CODE, "安全验证失败"));
                LogUtil.logError("BasePresenter.doApiRequest()", ex, true);
            } else {
                LogUtil.logError("BasePresenter.doApiRequest()", ex, true);
                serverResponse.setSimpleMsg(new SimpleMsg(ErrConfig.UI_LOGIC_CODE, ErrConfig.UI_LOGIC_MSG));
            }
        }
        return serverResponse;
    }

    /**
     * 真正发起一个网络请求
     *
     * @param mt     api 方法名
     * @param params 请求参数
     * @return 网络请求返回的json
     */
    private static String getHttpResponse(String mt, @Nullable BizParameters params, String appkey, boolean isPost) throws Exception {
        Request okHttpRequest = null;
        try {
            //处理url
            StringBuilder stringBuilder = new StringBuilder(AppBridge.apiUrl + mt + "?");
            if (!TextUtils.isEmpty(AppBridge.url_appCode)) {
                stringBuilder.append("appCode=" + AppBridge.url_appCode + "&");
            }
            if (!TextUtils.isEmpty(AppBridge.url_token)) {
                stringBuilder.append("token=" + AppBridge.url_token + "&");
            }
            if (!TextUtils.isEmpty(AppBridge.url_authCode)) {
                stringBuilder.append("authCode=" + AppBridge.url_authCode + "&");
            }
            if (params != null && !params.urlParams.isEmpty()) {
                for (String key : params.urlParams.keySet()) {
                    stringBuilder.append(key.trim());
                    stringBuilder.append("=");
                    stringBuilder.append(params.urlParams.get(key).toString().trim());
                    stringBuilder.append("&");
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            String baseUrl = stringBuilder.toString();
            //封装body
            String bodyStr = params == null ? "" : params.toString();
            if (!TextUtils.isEmpty(bodyStr)) {
                bodyStr = encrypt(bodyStr, appkey);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("ciphertext", bodyStr);
                bodyStr = jsonObject.toString();
            }
            RequestBody requestBody = RequestBody.create(bodyStr, formContentType);
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(baseUrl);
            if (isPost) {
                requestBuilder.post(requestBody);
            }
            setCommonParams(requestBuilder, params);
            requestBuilder.addHeader("Accept-Encoding", "gzip");

            // ps: 由于我们的请求都是post，是无法走HTTP默认的缓存机制
            OkHttpClient client = OkHttpUtil.getOkHttpClient();
            okHttpRequest = requestBuilder.build();
            okhttp3.Response response = client.newCall(okHttpRequest).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("接口请求失败:code=" + response.code() + " | message=" + response.message() + "\n参数:" + params);
            }
            String respBody = null;
            if (response.header("Content-Encoding") != null && response.header("Content-Encoding").contains("gzip")) {
                GzipSource responseBody = new GzipSource(response.body().source());
                Headers strippedHeaders = response.headers().newBuilder()
                        .removeAll("Content-Encoding")
                        .removeAll("Content-Length")
                        .build();
                String contentType = strippedHeaders.get("Content-Type");
                if (contentType == null) {
                    contentType = "";
                }
                respBody = new RealResponseBody(contentType, contentType.length(), Okio.buffer(responseBody)).string();
            } else {
                ResponseBody body = response.body();
                if (body != null) {
                    respBody = body.string();
                } else {
                    respBody = "";
                }
            }
            logRequestInfo(mt, okHttpRequest, bodyStr, response, respBody, appkey);
            return respBody;
        } catch (Exception ex) {
            //此处发生的异常，全部都算到网络异常
            logRequestErr(mt, ex.getMessage());
            if (okHttpRequest != null) {
                LogUtil.logError("BasePresenter.doHttp()", new HttpException(okHttpRequest, ex), false);
            } else {
                LogUtil.logError("BasePresenter.doHttp()", ex, true);
            }
            if (ex instanceof SocketTimeoutException) {
                throw new HttpException(ErrConfig.NET_TIMEOUT_MSG, ex,
                        ErrConfig.NET_TIMEOUT_CODE, "");
            } else {
                throw new HttpException(ErrConfig.NET_OFF_MSG, ex,
                        ErrConfig.NET_OFF_CODE, "");
            }
        }
    }

    /***
     * 封装通用参数
     * @param requestBuilder
     */
    private static void setCommonParams(Request.Builder requestBuilder, @Nullable BizParameters params) {
        requestBuilder.addHeader("Content-Type", "application/json; charset=utf-8");
        requestBuilder.addHeader("Accept", "application/json");
        if (!TextUtils.isEmpty(AppBridge.tk)) {
            requestBuilder.addHeader("tk", AppBridge.tk);
        }
        requestBuilder.addHeader("appid", AppBridge.appId);
        requestBuilder.addHeader("ver", DeviceUtil.getAppVersionName());
        requestBuilder.addHeader("vercode", DeviceUtil.getAppVersionCode() + "");
        requestBuilder.addHeader("os", "Android " + DeviceUtil.getAndroidRelease());
        requestBuilder.addHeader("oscode", DeviceUtil.getAndroidVersion() + "");
        requestBuilder.addHeader("device", DeviceUtil.getMobileModel() + "");
        if (params != null) {
            //临时添加，业务参数应该放到body中，header只放通用参数
            if (params.getParam("TIMESTAMP") != null) {
                requestBuilder.addHeader("TIMESTAMP", params.getParam("TIMESTAMP") + "");
            }
            if (params.getParam("TOKEN") != null) {
                requestBuilder.addHeader("tk", params.getParam("TOKEN") + "");
            }
        }
    }

    /**
     * api请求日志
     *
     * @param mt
     * @param request
     * @param params
     * @param result
     */
    private static void logRequestInfo(String mt, Request request, String params, okhttp3.Response response, String result, String appkey) {
        if (AppBridge.Debug) {
            if (request != null) {
                try {
                    result = Uri.decode(result);
                    JsonObject requestJson = new JsonObject();
                    String url = request.url().toString();
                    requestJson.addProperty("url", url);
                    if (!TextUtils.isEmpty(params)) {
                        JsonObject jo_param = gson.fromJson(params, JsonObject.class);
                        //解密
                        if (jo_param.has("ciphertext")) {
                            jo_param = gson.fromJson(decryptResult(jo_param.get("ciphertext").getAsString(), appkey), JsonObject.class);
                        }
                        requestJson.remove("params");
                        requestJson.add("params", jo_param);
                    }
                    JsonObject jo_header = new JsonObject();
                    for (int i = 0; i < request.headers().size(); i++) {
                        Iterator iter = request.headers().names().iterator();
                        while (iter.hasNext()) {
                            String name = iter.next().toString();
                            jo_header.addProperty(name, request.headers().get(name));
                        }
                    }
                    requestJson.add("headers", jo_header);
                    requestJson.addProperty("method", request.method());
                    requestJson.addProperty("protocol", request.isHttps() ? "https" : "http");
                    //解密
                    JsonElement jsonMain = gson.fromJson(result, JsonElement.class);
                    boolean isError = true;
                    if (jsonMain.getAsJsonObject().has("code") && jsonMain.getAsJsonObject().get("code").getAsInt() == 200) {
                        isError = false;
                    }
                    if (jsonMain.getAsJsonObject().has("data") && jsonMain.getAsJsonObject().get("privacy").getAsBoolean()) {
                        String decryptResult = decryptResult(jsonMain.getAsJsonObject().get("data").getAsString(), appkey);
                        jsonMain.getAsJsonObject().remove("data");
                        try {
                            JsonElement jsonData = gson.fromJson(decryptResult, JsonElement.class);
                            jsonMain.getAsJsonObject().add("data", jsonData);
                        } catch (Exception ex) {
                            jsonMain.getAsJsonObject().addProperty("data", decryptResult);
                        }
                    }
                    LogUtil.log(LOGTAG, mt + "(耗时:" + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + "ms)" + "\nrequest:" + Uri.decode(requestJson.toString()) + "\nresponse:" + (jsonMain.toString().length() > 2000 ? jsonMain.toString().substring(0, 1800) + "..." : jsonMain.toString()) + "\n----------------------------------------------------------------", isError);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logRequestErr(mt + "|日志打印出现异常", ex.getMessage());
                }
            }
        }
    }

    /***
     * api报错日志
     * @param mt
     * @param content
     */
    private static void logRequestErr(String mt, String content) {
        if (AppBridge.Debug) {
            LogUtil.log(LOGTAG, mt + "\nerror:" + content + "\n----------------------------------------------------------------", true);
        }
    }

    /**
     * 加密
     *
     * @param requestStr
     * @return
     * @throws Exception
     */
    private static String encrypt(String requestStr, String aesKey) throws Exception {
        byte[] result = Base64.encrypt(requestStr.getBytes(), aesKey.getBytes());
        return BASE64Decod.encode(result);
    }

    /**
     * 解密
     *
     * @throws Exception
     */
    private static String decryptResult(String requestStr, String aesKey) throws Exception {
        byte[] str = Base64.decode(requestStr);
        byte[] res = Base64.decrypt(str, aesKey.getBytes());
        String result = new String(res);
        return result;
    }


    private static class CallbackHandler extends Handler {
        private static final int ACTION_START = 1;
        private static final int ACTION_SUCCESS = 2;
        private static final int ACTION_FAILED = 3;

        private AsynWork asynWork;
        private ViewCallback viewCallback;

        public CallbackHandler(AsynWork asynWork, ViewCallback viewCallback) {
            this.asynWork = asynWork;
            this.viewCallback = viewCallback;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == ACTION_START) {
                try {
                    viewCallback.onStart();
                } catch (Exception e) {
                    LogUtil.logError("BasePresenter.onStart()", e, true);
                    sendOnFailed(new SimpleMsg(ErrConfig.UI_LOGIC_CODE, ErrConfig.UI_LOGIC_MSG));
                }
            } else if (msg.what == ACTION_SUCCESS) {
                try {
                    viewCallback.onSuccess(msg.obj);
                    viewCallback.onFinish();
                } catch (Exception e) {
                    LogUtil.logError("BasePresenter.onSuccess()", e, true);
                    sendOnFailed(new SimpleMsg(ErrConfig.UI_LOGIC_CODE, ErrConfig.UI_LOGIC_MSG));
                }
            } else if (msg.what == ACTION_FAILED) {
                try {
                    SimpleMsg simpleMsg = (SimpleMsg) msg.obj;
                    viewCallback.onFailed(simpleMsg);
                    //如果接口中有特殊情况需要处理，可在此处修改
                    viewCallback.onFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendOnStart() {
            obtainMessage(ACTION_START).sendToTarget();
        }

        public void sendOnSuccess(Object data) {
            obtainMessage(ACTION_SUCCESS, data).sendToTarget();
        }

        public void sendOnFailed(SimpleMsg simpleMsg) {
            obtainMessage(ACTION_FAILED, simpleMsg).sendToTarget();
        }

    }

}
