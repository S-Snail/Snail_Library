package com.example.base_libs.presenter;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class ErrConfig {
    /**
     * 网络超时
     */
    public static final int NET_TIMEOUT_CODE = 1000;
    public static final String NET_TIMEOUT_MSG = "网络超时";

    /**
     * 网络连接失败
     */
    public static final int NET_OFF_CODE = 1001;
    public static final String NET_OFF_MSG = "网络连接失败";

    /**
     * UI层逻辑代码错误，重要级别：极高
     */
    public static final int UI_LOGIC_CODE = 1003;
    public static final String UI_LOGIC_MSG = "发生位置异常";

}
