package org.thosp.HackCBS.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import org.thosp.HackCBS.model.Location;
import org.thosp.HackCBS.model.LocationsDbHelper;
import org.thosp.HackCBS.utils.AppPreference;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

@TargetApi(Build.VERSION_CODES.M)
public class StartAutoLocationJob extends AbstractAppJob {
    private static final String TAG = "StartAutoLocationJob";

    LocationUpdateService locationUpdateService;
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        appendLog(this, TAG, "sending intent to get location update");
        Intent intent = new Intent(this, LocationUpdateService.class);
        bindService(intent, locationUpdateServiceConnection, Context.BIND_AUTO_CREATE);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (locationUpdateService != null) {
            unbindService(locationUpdateServiceConnection);
        }
        unbindService(sensorLocationUpdateServiceConnection);
        unbindAllServices();
        return true;
    }

    private void performUpdateOfLocation() {
        LocationsDbHelper locationsDbHelper = LocationsDbHelper.getInstance(getBaseContext());
        Location location = locationsDbHelper.getLocationByOrderId(0);
        if (location.isEnabled()) {
            locationUpdateService.startLocationAndWeatherUpdate();
        }
        Intent intent = new Intent(this, SensorLocationUpdateService.class);
        bindService(intent, sensorLocationUpdateServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection locationUpdateServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationUpdateService.LocationUpdateServiceBinder binder =
                    (LocationUpdateService.LocationUpdateServiceBinder) service;
            locationUpdateService = binder.getService();
            appendLog(getBaseContext(), TAG, "got locationUpdateServiceConnection");
            performUpdateOfLocation();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            locationUpdateService = null;
        }
    };

    private ServiceConnection sensorLocationUpdateServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SensorLocationUpdateService.SensorLocationUpdateServiceBinder binder =
                    (SensorLocationUpdateService.SensorLocationUpdateServiceBinder) service;
            SensorLocationUpdateService sensorLocationUpdateService = binder.getService();
            String updateAutoPeriodStr = AppPreference.getLocationAutoUpdatePeriod(getBaseContext());
            appendLog(getBaseContext(),
                    TAG,
                    "updateAutoPeriodStr:" + updateAutoPeriodStr);
            if ("0".equals(updateAutoPeriodStr)) {
                sensorLocationUpdateService.startSensorBasedUpdates(0);
                reScheduleNextAlarm(1, AppAlarmService.START_SENSORS_CHECK_PERIOD, StartAutoLocationJob.class);
            } else if ("OFF".equals(updateAutoPeriodStr)) {
                sensorLocationUpdateService.stopSensorBasedUpdates();
            } else {
                reScheduleNextAlarm(1, updateAutoPeriodStr, StartAutoLocationJob.class);
            }
            jobFinished(params, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}