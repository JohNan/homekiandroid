package com.homeki.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	public static void setServerUrl(Context context, String serverUrl) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putString("server_url", serverUrl).commit();
	}

	public static String getServerUrl(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("server_url", "");
	}

	public static String getServerPort(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("server_port", "5000");
	}

    public static String getRegisteredClientGuid(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("registered_client_guid", null);
    }

    public static void setRegisteredClientGuid(Context context, String clientGuid) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("registered_client_guid", clientGuid).commit();
    }
	
	public static boolean isClientRegisteringEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("client_registering", false);
    }

    public static long getAlarmStartTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("alarm_start_time", -1);
    }

    public static void setAlarmStartTime(Context context, long ms) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putLong("alarm_start_time", ms).commit();
    }
}