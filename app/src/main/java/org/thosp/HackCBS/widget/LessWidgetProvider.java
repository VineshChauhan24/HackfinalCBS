package org.thosp.HackCBS.widget;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import org.thosp.HackCBS.R;
import org.thosp.HackCBS.model.CurrentWeatherDbHelper;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.model.WidgetSettingsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;
import org.thosp.HackCBS.utils.TemperatureUtil;
import org.thosp.HackCBS.utils.Utils;


public class LessWidgetProvider extends AbstractWidgetProvider {

    private static final String TAG = "WidgetLessInfo";

    private static final String WIDGET_NAME = "MORE_WIDGET";

    @Override
    protected void preLoadWeather(Context context, RemoteViews remoteViews, int appWidgetId) {
        final CurrentWeatherDbHelper currentWeatherDbHelper = CurrentWeatherDbHelper.getInstance(context);
        final LocationsDbHelper locationsDbHelper = LocationsDbHelper.getInstance(context);
        WidgetSettingsDbHelper widgetSettingsDbHelper = WidgetSettingsDbHelper.getInstance(context);

        Long locationId = widgetSettingsDbHelper.getParamLong(appWidgetId, "locationId");

        if (locationId == null) {
            currentLocation = locationsDbHelper.getLocationByOrderId(0);
            if (!currentLocation.isEnabled()) {
                currentLocation = locationsDbHelper.getLocationByOrderId(1);
            }
        } else {
            currentLocation = locationsDbHelper.getLocationById(locationId);
        }

        if (currentLocation == null) {
            return;
        }

        CurrentWeatherDbHelper.WeatherRecord weatherRecord = currentWeatherDbHelper.getWeather(currentLocation.getId());

        if (weatherRecord != null) {
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    weatherRecord.getWeather(),
                    currentLocation.getLatitude(),
                    weatherRecord.getLastUpdatedTime()));
            String secondTemperature = TemperatureUtil.getSecondTemperatureWithUnit(
                    context,
                    weatherRecord.getWeather(),
                    currentLocation.getLatitude(),
                    weatherRecord.getLastUpdatedTime());
            if (secondTemperature != null) {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.VISIBLE);
                remoteViews.setTextViewText(R.id.widget_second_temperature, secondTemperature);
            } else {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.GONE);
            }
            remoteViews.setTextViewText(R.id.widget_city, Utils.getCityAndCountry(context, currentLocation.getOrderId()));
            remoteViews.setTextViewText(R.id.widget_description, Utils.getWeatherDescription(context, weatherRecord.getWeather()));
            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            String lastUpdate = Utils.getLastUpdateTime(context, weatherRecord, currentLocation);
            remoteViews.setTextViewText(R.id.widget_last_update, lastUpdate);
        } else {
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    null,
                    currentLocation.getLatitude(),
                    0));
            remoteViews.setTextViewText(R.id.widget_second_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    null,
                    currentLocation.getLatitude(),
                    0));
            remoteViews.setTextViewText(R.id.widget_description, "");

            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            remoteViews.setTextViewText(R.id.widget_last_update, "");
        }
    }

    public static void setWidgetTheme(Context context, RemoteViews remoteViews) {

        int textColorId = AppPreference.getTextColor(context);
        int backgroundColorId = AppPreference.getBackgroundColor(context);
        int windowHeaderBackgroundColorId = AppPreference.getWindowHeaderBackgroundColorId(context);
        
        remoteViews.setInt(R.id.widget_root, "setBackgroundColor", backgroundColorId);
        remoteViews.setTextColor(R.id.widget_temperature, textColorId);
        remoteViews.setTextColor(R.id.widget_second_temperature, textColorId);
        remoteViews.setTextColor(R.id.widget_description, textColorId);
        remoteViews.setInt(R.id.header_layout, "setBackgroundColor", windowHeaderBackgroundColorId);
    }

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_less_3x1;
    }

    @Override
    protected Class<?> getWidgetClass() {
        return LessWidgetProvider.class;
    }

    @Override
    protected String getWidgetName() {
        return WIDGET_NAME;
    }
}
