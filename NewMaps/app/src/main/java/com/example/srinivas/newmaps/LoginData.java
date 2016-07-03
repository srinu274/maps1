package com.example.srinivas.newmaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LoginData {
    private static String  IS_CONNECTED="connnected";
    private static String USER_NAME="user_name";
    private static String USER_EMAIL="user_email";
    private static String USER_URL="user_url";

    public static void setConnected(Context context, boolean isConnected) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(IS_CONNECTED,isConnected).apply();
    }

    public static boolean isConnected(Context context) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(IS_CONNECTED,false);
    }

    public static void setUserName(Context context,String userName) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(USER_NAME,userName).apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(USER_NAME,"");
    }

    public static void setUserEmail(Context context,String userEmail) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(USER_EMAIL,userEmail).apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(USER_EMAIL,"");
    }

    public static void setUserUrl(Context context,String userUrl) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(USER_URL,userUrl).apply();
    }

    public static String getUserUrl(Context context) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(USER_URL,"");
    }
}
