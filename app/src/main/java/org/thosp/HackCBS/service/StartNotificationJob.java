package org.thosp.HackCBS.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.os.Build;

import org.thosp.HackCBS.model.Location;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

@TargetApi(Build.VERSION_CODES.M)
public class StartNotificationJob extends AbstractAppJob {
    private static final String TAG = "StartNotificationJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        performNotification();
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unbindAllServices();
        return true;
    }

    private void performNotification() {
        boolean isNotificationEnabled = AppPreference.isNotificationEnabled(getBaseContext());
        if (!isNotificationEnabled) {
            return;
        }
        Location currentLocation = getLocationForNotification();
        if (currentLocation == null) {
            return;
        }
        sendMessageToCurrentWeatherService(currentLocation, "NOTIFICATION", AppWakeUpManager.SOURCE_NOTIFICATION);
        reScheduleNextAlarm(3, AppPreference.getInterval(getBaseContext()), StartNotificationJob.class);
    }

    private Location getLocationForNotification() {
        final LocationsDbHelper locationsDbHelper = LocationsDbHelper.getInstance(this);
        Location currentLocation = locationsDbHelper.getLocationByOrderId(0);
        if (!currentLocation.isEnabled()) {
            currentLocation = locationsDbHelper.getLocationByOrderId(1);
        }
        return currentLocation;
    }
}