package com.example.base_libs.presenter;

import com.example.base_libs.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

/**
 * @Author Snail
 * @Since 2021/2/28
 */
public class BizParameters {
    private HashMap<String, Object> params = new HashMap<>();
    public HashMap<String, Object> urlParams = new HashMap<>();

    public BizParameters() {
    }

    public BizParameters(HashMap<String, Object> parameters) {
        if (parameters != null) {
            this.params = parameters;
        }
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public void putUrlParam(String key, Object value) {
        urlParams.put(key, value);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public String toString() {
        try {
            if (params.size() > 0) {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                return gson.toJson(params);
            }
        } catch (Exception ex) {
            LogUtil.logError("BizParameters.toString()", ex, true);
        }
        return "";
    }

    public void remove(String key) {
        params.remove(key);
    }
}
