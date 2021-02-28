package com.example.base_libs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.base_libs.base.BaseLibsApplication;

import java.util.concurrent.CopyOnWriteArrayList;

public class NetWorkStateManger {
    private static CusNetworkCallback networkCallback;
    private static CopyOnWriteArrayList<OnNetChangeListenner> msgListeners = new CopyOnWriteArrayList<>();

    public static void addOnNetChangeListenner(OnNetChangeListenner onNetChangeListenner) {
        if (onNetChangeListenner == null) {
            return;
        }
        init();
        msgListeners.add(onNetChangeListenner);
    }

    public static void removeNetChangeListenner(OnNetChangeListenner onNetChangeListenner) {
        if (onNetChangeListenner == null) {
            return;
        }
        msgListeners.remove(onNetChangeListenner);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void init() {
        if (networkCallback != null) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseLibsApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new CusNetworkCallback();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//API 大于26时
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API 大于21时
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class CusNetworkCallback extends ConnectivityManager.NetworkCallback {
        private int status = 0;

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                    if (status != 1) {
                        status = 1;
                        onNetChange(status);
                    }
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    if (status != 2) {
                        status = 2;
                        onNetChange(status);
                    }
                } else {
                    LogUtil.log("NetWorkStateManger:onCapabilitiesChanged()其他网络");
                }
            }
        }

        private void onNetChange(int type) {
            if (!msgListeners.isEmpty()) {
                try {
                    for (OnNetChangeListenner netChangeListenner : msgListeners) {
                        String name = "WIFI";
                        if (type == 2) {
                            name = "移动数据";
                        }
                        LogUtil.log("NetWorkStateManger_" + netChangeListenner.getClass().getName() + "onNetChange(" + name + ")");
                        netChangeListenner.onNetChange(type);
                    }
                } catch (Exception ex) {
                    LogUtil.logError("NetWorkStateManger", ex, true);
                }
            }
        }

    }

    public interface OnNetChangeListenner {
        /***
         * 网络切换
         * @param type 1：WIFI；2：移动网络
         */
        void onNetChange(int type);
    }
}
