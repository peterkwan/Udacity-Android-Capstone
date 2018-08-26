package org.peterkwan.udacity.mysupermarket.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper {

    public static String getPreference(SharedPreferences sharedPreferences, String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static String getPreference(Context context, String key, String defaultValue) {
        return getPreference(getSharedPreference(context), key, defaultValue);
    }

    public static void savePreference(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
