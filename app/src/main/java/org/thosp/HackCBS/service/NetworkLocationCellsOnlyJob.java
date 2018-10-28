package org.thosp.HackCBS.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import static org.thosp.HackCBS.utils.LogToFile.appendLog;

@TargetApi(Build.VERSION_CODES.M)
public class NetworkLocationCellsOnlyJob extends JobService {
    private static final String TAG = "NetworkLocationCellsOnlyJob";

    NetworkLocationProvider networkLocationProvider;
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        appendLog(this, TAG, "starting cells only location lookup");
        Intent intent = new Intent(this, NetworkLocationProvider.class);
        bindService(intent, networkLocationProviderConnection, Context.BIND_AUTO_CREATE);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (networkLocationProvider != null) {
            unbindService(networkLocationProviderConnection);
        }
        return true;
    }

    private ServiceConnection networkLocationProviderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            NetworkLocationProvider.NetworkLocationProviderBinder binder =
                    (NetworkLocationProvider.NetworkLocationProviderBinder) service;
            networkLocationProvider = binder.getService();
            networkLocationProvider.startLocationUpdateCellsOnly();
            jobFinished(params, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            networkLocationProvider = null;
        }
    };
}
