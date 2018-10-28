package org.thosp.HackCBS.widget;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import org.thosp.HackCBS.R;
import org.thosp.HackCBS.model.CurrentWeatherDbHelper;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.model.Weather;
import org.thosp.HackCBS.model.WidgetSettingsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;
import org.thosp.HackCBS.utils.TemperatureUtil;
import org.thosp.HackCBS.utils.Utils;
import org.thosp.HackCBS.utils.WidgetUtils;

import java.util.Calendar;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

public class ExtLocationWidgetProvider extends AbstractWidgetProvider {

    private static final String TAG = "WidgetExtLocInfo";

    private static final String WIDGET_NAME = "EXT_LOC_WIDGET";

    @Override
    protected void preLoadWeather(Context context, RemoteViews remoteViews, int appWidgetId) {
        appendLog(context, TAG, "preLoadWeather:start");
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
            Weather weather = weatherRecord.getWeather();

            remoteViews.setTextViewText(R.id.widget_city, Utils.getCityAndCountry(context, currentLocation.getOrderId()));
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    weather,
                    currentLocation.getLatitude(),
                    weatherRecord.getLastUpdatedTime()));
            String secondTemperature = TemperatureUtil.getSecondTemperatureWithUnit(
                    context,
                    weather,
                    currentLocation.getLatitude(),
                    weatherRecord.getLastUpdatedTime());
            if (secondTemperature != null) {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.VISIBLE);
                remoteViews.setTextViewText(R.id.widget_second_temperature, secondTemperature);
            } else {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.GONE);
            }
            remoteViews.setTextViewText(R.id.widget_description, Utils.getWeatherDescription(context, weather));

            WidgetUtils.setWind(context, remoteViews, weather.getWindSpeed(), weather.getWindDirection());
            WidgetUtils.setHumidity(context, remoteViews, weather.getHumidity());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(1000 * weather.getSunrise());
            WidgetUtils.setSunrise(context, remoteViews, AppPreference.getLocalizedTime(context, calendar.getTime()));
            calendar.setTimeInMillis(1000 * weather.getSunset());
            WidgetUtils.setSunset(context, remoteViews, AppPreference.getLocalizedTime(context, calendar.getTime()));

            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            String lastUpdate = Utils.getLastUpdateTime(context, weatherRecord, currentLocation);
            remoteViews.setTextViewText(R.id.widget_last_update, lastUpdate);
        } else {
            remoteViews.setTextViewText(R.id.widget_city, context.getString(R.string.location_not_found));
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    null,
                    currentLocation.getLatitude(),
                    0));
            String secondTemperature = TemperatureUtil.getSecondTemperatureWithUnit(
                    context,
                    null,
                    currentLocation.getLatitude(),
                    0);
            if (secondTemperature != null) {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.VISIBLE);
                remoteViews.setTextViewText(R.id.widget_second_temperature, secondTemperature);
            } else {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.GONE);
            }
            remoteViews.setTextViewText(R.id.widget_description, "");

            WidgetUtils.setWind(context, remoteViews, 0, 0);
            WidgetUtils.setHumidity(context, remoteViews, 0);
            WidgetUtils.setSunrise(context, remoteViews, "");
            WidgetUtils.setSunset(context, remoteViews, "");

            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            remoteViews.setTextViewText(R.id.widget_last_update, "");
        }
        appendLog(context, TAG, "preLoadWeather:end");
    }

    public static void setWidgetTheme(Context context, RemoteViews remoteViews) {
        appendLog(context, TAG, "setWidgetTheme:start");
        int textColorId = AppPreference.getTextColor(context);
        int backgroundColorId = AppPreference.getBackgroundColor(context);
        int windowHeaderBackgroundColorId = AppPreference.getWindowHeaderBackgroundColorId(context);

        remoteViews.setInt(R.id.widget_root, "setBackgroundColor", backgroundColorId);
        remoteViews.setTextColor(R.id.widget_temperature, textColorId);
        remoteViews.setTextColor(R.id.widget_description, textColorId);
        remoteViews.setTextColor(R.id.widget_description, textColorId);
        remoteViews.setTextColor(R.id.widget_wind, textColorId);
        remoteViews.setTextColor(R.id.widget_humidity, textColorId);
        remoteViews.setTextColor(R.id.widget_sunrise, textColorId);
        remoteViews.setTextColor(R.id.widget_sunset, textColorId);
        remoteViews.setTextColor(R.id.widget_second_temperature, textColorId);
        remoteViews.setInt(R.id.header_layout, "setBackgroundColor", windowHeaderBackgroundColorId);
        appendLog(context, TAG, "setWidgetTheme:end");
    }

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_ext_loc_3x3;
    }

    @Override
    protected Class<?> getWidgetClass() {
        return ExtLocationWidgetProvider.class;
    }

    @Override
    protected String getWidgetName() {
        return WIDGET_NAME;
    }
}
