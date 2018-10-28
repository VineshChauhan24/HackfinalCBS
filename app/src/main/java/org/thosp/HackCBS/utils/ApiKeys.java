package org.thosp.HackCBS.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import org.thosp.HackCBS.R;

public class ApiKeys {

    public static final int DEFAULT_AVAILABLE_LOCATIONS = 2;
    public static final int MAX_AVAILABLE_LOCATIONS = 20;

    public static final String DEFAULT_OPEN_WEATHER_MAP_API_KEY =
            "7664eb3c556fb862a325ec4af36872af";

    public static String getOpenweathermapApiKey(Context context) {
        String openweathermapApiKey = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(
                        Constants.KEY_PREF_OPEN_WEATHER_MAP_API_KEY,
                        ""
                );
        if ((openweathermapApiKey == null) || "".equals(openweathermapApiKey)) {
            openweathermapApiKey = DEFAULT_OPEN_WEATHER_MAP_API_KEY;
        }
        return openweathermapApiKey;
    }

    public static String getOpenweathermapApiKeyForPreferences(Context context) {
        String openweathermapApiKey = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(
                        Constants.KEY_PREF_OPEN_WEATHER_MAP_API_KEY,
                        ""
                );
        if ((openweathermapApiKey == null) || "".equals(openweathermapApiKey)) {
            openweathermapApiKey = context.getString(R.string.open_weather_map_api_default_key);
        }
        return openweathermapApiKey;
    }

    public static boolean isDefaultOpenweatherApiKey(Context context) {
        String openweathermapApiKey = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(
                        Constants.KEY_PREF_OPEN_WEATHER_MAP_API_KEY,
                        ""
                );
        return ((openweathermapApiKey == null) || "".equals(openweathermapApiKey));
    }

    public static int getAvailableLocations(Context context) {
        if (isDefaultOpenweatherApiKey(context)) {
            return DEFAULT_AVAILABLE_LOCATIONS;
        } else {
            return MAX_AVAILABLE_LOCATIONS;
        }
    }
}
