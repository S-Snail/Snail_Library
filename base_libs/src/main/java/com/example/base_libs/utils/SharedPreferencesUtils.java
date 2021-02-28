package com.example.base_libs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.base_libs.base.BaseLibsApplication;
import com.google.gson.Gson;

public class SharedPreferencesUtils {
    private final static String FILE_NAME = "data";
    private volatile static SharedPreferencesUtils preferencesUtils;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesUtils() {
        sharedPreferences = BaseLibsApplication.getApplication().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesUtils getInstance() {
        if (null == preferencesUtils) {
            synchronized (SharedPreferencesUtils.class) {
                if (null == preferencesUtils) {
                    preferencesUtils = new SharedPreferencesUtils();
                }
            }
        }
        return preferencesUtils;
    }


    public void saveData(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) return;
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            String json = new Gson().toJson(value);
            editor.putString(key, json);
        }
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String valueString = getString(key);
        if (null == valueString || TextUtils.isEmpty(valueString.trim())
                || "null".equals(valueString.trim()) || "[]".equals(valueString.trim())
                || "".equals(valueString.trim())) {
            return defaultValue;
        }
        return valueString;
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, false);
    }

    public <T> T getObject(String key, Class<T> classOfT) {
        String json = sharedPreferences.getString(key, "");
        if (json == null || json.trim().equals("")) {
            return null;
        }
        return new Gson().fromJson(json, classOfT);
    }

    public boolean clearAll() {
        return sharedPreferences.edit().clear().commit();
    }

    public boolean remove(String key) {
        return sharedPreferences.edit().remove(key).commit();
    }


}
