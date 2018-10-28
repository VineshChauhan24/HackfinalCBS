package org.thosp.HackCBS;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import org.thosp.HackCBS.utils.Constants;
import org.thosp.HackCBS.utils.LanguageUtil;
import org.thosp.HackCBS.utils.PreferenceUtil;
import org.thosp.HackCBS.utils.PreferenceUtil.Theme;

import java.util.Locale;

public class YourLocalWeather extends Application {

    private static final String TAG = "YourLocalWeather";

    private static Theme sTheme = Theme.light;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(Constants.PREF_OS_LANGUAGE, Locale.getDefault().getLanguage())
                .apply();
        LanguageUtil.setLanguage(this, PreferenceUtil.getLanguage(this));

        sTheme = PreferenceUtil.getTheme(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(Constants.PREF_OS_LANGUAGE, Locale.getDefault().getLanguage())
                .apply();
        LanguageUtil.setLanguage(this, PreferenceUtil.getLanguage(this));
    }

    public void reloadTheme() {
        sTheme = PreferenceUtil.getTheme(this);
    }

    public void applyTheme(Activity activity) {
        activity.setTheme(getThemeResId());
    }

    public static int getThemeResId() {
        switch (sTheme) {
            case light:
                return R.style.AppThemeLight;
            case dark:
                return R.style.AppThemeDark;
            default:
                return R.style.AppThemeLight;
        }
    }
}
