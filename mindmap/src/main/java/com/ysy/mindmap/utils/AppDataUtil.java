package com.ysy.mindmap.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ysy.mindmap.bases.GlobalConstant;

public class AppDataUtil {

    private Context context;
    private static final String NAME = GlobalConstant.SP_TOTAL_NAME;

    public AppDataUtil(Context context) {
        this.context = context;
    }

    public void saveData(String key, String data) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (data == null)
            editor.putString(key, "");
        else
            editor.putString(key, data);
        editor.apply();
    }

    public void saveData(String key, int data) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, data);
        editor.apply();
    }

    public void saveData(String key, long data) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, data);
        editor.apply();
    }

    public String readStringData(String key) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public String readStringData(String key, String defaultValue) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public int readIntData(String key) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, -1);
    }

    public long readLongData(String key) {
        SharedPreferences sp = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, -1L);
    }

    public void clearAllDatas() {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
