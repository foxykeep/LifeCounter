package com.foxykeep.lifecounter.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPrefsConfig {

    // No public constructor
    private SharedPrefsConfig() {}

    private static final String PREFS_FILE = "lifeCounter.sharedPrefs";

    public static final String BACKGROUND_COLOR_1 = "backgroundColor1";
    public static final String BACKGROUND_COLOR_2 = "backgroundColor2";
    public static final String FLIP_COUNTER = "flipCounter";
    public static final String SHOW_POISON_COUNTERS = "showPoisonCounters";
    public static final String STARTING_LIFE = "startingLife";
    public static final String KEEP_SCREEN_AWAKE = "keepScreenAwake";

    public static String getString(Context context, String key) {
        return getSharedPrefs(context).getString(key, null);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPrefs(context).getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        return getSharedPrefs(context).getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPrefs(context).getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.apply();
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrefs(context).edit();
    }
}
