package com.example.inf0251atrabalho3.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesUtil(Context context) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesUtil getInstance(Context context) {
        if (SharedPreferencesUtil.instance == null) {
            SharedPreferencesUtil.instance = new SharedPreferencesUtil(context);
        }
        return SharedPreferencesUtil.instance;
    }

    /**
     * Return String value of map
     *
     * @param key the key
     * @param returnOnNull value returned in case the not exist in map
     * @return string value
     */
    public String getValue(String key, String returnOnNull) {
        return preferences.getString(key, returnOnNull);
    }

    /**
     * Return  boolean value of map
     *
     * @param key the key
     * @param returnOnNull value returned in case the not exist in map
     * @return boolean value
     */
    public boolean getValue(String key, boolean returnOnNull) {
        return preferences.getBoolean(key, returnOnNull);
    }

    /**
     * Return int value of map
     *
     * @param key the key
     * @param returnOnNull value returned in case the not exist in map
     * @return int value
     */
    public int getValue(String key, int returnOnNull) {
        return preferences.getInt(key, returnOnNull);
    }

    /**
     * Set String value in map
     *
     * @param key  the key
     * @param value the string value
     */
    public void setValue(String key, String value) {
        editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Set boolean value in map
     *
     * @param key  the key
     * @param value the boolean value
     */
    public void setValue(String key, boolean value) {
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Set int value in map
     *
     * @param key  the key
     * @param value the int value
     */
    public void setValue(String key, int value) {
        editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Remove a key of map
     *
     * @param key the key
     */
    public void remove(String key) {
        editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
