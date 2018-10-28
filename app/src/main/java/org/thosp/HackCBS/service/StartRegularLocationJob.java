package org.thosp.HackCBS.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.os.Build;

import org.thosp.HackCBS.model.Location;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;

import java.util.List;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

@TargetApi(Build.VERSION_CODES.M)
public class StartRegularLocationJob extends AbstractAppJob {
    private static final String TAG = "StartRegularLocationJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        performUpdateOfWeather();
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unbindAllServices();
        return true;
    }

    private void performUpdateOfWeather() {
        LocationsDbHelper locationsDbHelper = LocationsDbHelper.getInstance(getBaseContext());
        String updatePeriodStr = AppPreference.getLocationUpdatePeriod(getBaseContext());
        if (!"0".equals(updatePeriodStr) && (locationsDbHelper.getAllRows().size() > 1)) {
            reScheduleNextAlarm(2, updatePeriodStr, StartRegularLocationJob.class);
        }
        List<Location> locations = locationsDbHelper.getAllRows();
        for (Location location: locations) {
            if (location.getOrderId() == 0) {
                continue;
            } else {
                sendMessageToCurrentWeatherService(location, AppWakeUpManager.SOURCE_CURRENT_WEATHER);
                sendMessageToWeatherForecastService(location.getId());
            }
        }
    }
}