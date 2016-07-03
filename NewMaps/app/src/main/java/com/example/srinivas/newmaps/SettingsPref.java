package com.example.srinivas.newmaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class SettingsPref {

    private static final String LOCATION_UPDATE_STARTED = "locationupdateStarted";
    private static final String IS_LOCATION_STORED ="locationStored";

    public static boolean isLocationStored(Context context) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(IS_LOCATION_STORED,true);
    }

    public static void setLocationStored(Context context, boolean started) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(IS_LOCATION_STORED,started).apply();
    }

    public static void setLocationUpdateStarted(Context context, boolean b) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(LOCATION_UPDATE_STARTED,b).apply();
    }

    public static boolean isLocationUpdateStarted(Context context) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(LOCATION_UPDATE_STARTED,false);
    }
}
