package org.thosp.HackCBS.widget;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import org.thosp.HackCBS.R;
import org.thosp.HackCBS.model.CurrentWeatherDbHelper;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.model.Weather;
import org.thosp.HackCBS.model.WeatherForecastDbHelper;
import org.thosp.HackCBS.model.WidgetSettingsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;
import org.thosp.HackCBS.utils.TemperatureUtil;
import org.thosp.HackCBS.utils.Utils;
import org.thosp.HackCBS.utils.WidgetUtils;

import java.util.Calendar;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

public class ExtLocationWithForecastWidgetProvider extends AbstractWidgetProvider {

    private static final String TAG = "ExtLocationWithForecastWidgetProvider";

    private static final String WIDGET_NAME = "EXT_LOC_WITH_FORECAST_WIDGET";

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
        }
        WeatherForecastDbHelper.WeatherForecastRecord weatherForecastRecord = null;
        try {
            weatherForecastRecord = WidgetUtils.updateWeatherForecast(context, currentLocation.getId(), remoteViews);
        } catch (Exception e) {
            appendLog(context, TAG, "preLoadWeather:error updating weather forecast", e);
        }
        String lastUpdate = Utils.getLastUpdateTime(context, weatherRecord, weatherForecastRecord, currentLocation);
        remoteViews.setTextViewText(R.id.widget_last_update, lastUpdate);
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
        remoteViews.setTextColor(R.id.forecast_1_widget_day, textColorId);
        remoteViews.setTextColor(R.id.forecast_1_widget_temperatures, textColorId);
        remoteViews.setTextColor(R.id.forecast_2_widget_day, textColorId);
        remoteViews.setTextColor(R.id.forecast_2_widget_temperatures, textColorId);
        remoteViews.setTextColor(R.id.forecast_3_widget_day, textColorId);
        remoteViews.setTextColor(R.id.forecast_3_widget_temperatures, textColorId);
        remoteViews.setTextColor(R.id.forecast_4_widget_day, textColorId);
        remoteViews.setTextColor(R.id.forecast_4_widget_temperatures, textColorId);
        remoteViews.setTextColor(R.id.forecast_5_widget_day, textColorId);
        remoteViews.setTextColor(R.id.forecast_5_widget_temperatures, textColorId);
        remoteViews.setInt(R.id.header_layout, "setBackgroundColor", windowHeaderBackgroundColorId);
        appendLog(context, TAG, "setWidgetTheme:end");
    }

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_ext_loc_forecast_3x3;
    }

    @Override
    protected Class<?> getWidgetClass() {
        return ExtLocationWithForecastWidgetProvider.class;
    }

    @Override
    protected String getWidgetName() {
        return WIDGET_NAME;
    }
}
