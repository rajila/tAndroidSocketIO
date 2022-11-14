package com.rdajila.tandroidsocketio.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;


public final class DeviceSocketIO extends Service {
    private static int counter = 0;
    private static final String TAG = DeviceSocketIO.class.getSimpleName();
    TimerTask timerTask;

    public DeviceSocketIO() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Servicio creado...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado...");

        final String ACTION_RUN_SERVICE = "com.rdajila.tandroidsocketio.services.action.RUN_SERVICE";
        final String EXTRA_MEMORY = "com.rdajila.tandroidsocketio.services.extra.MEMORY";
        final String COUNTER = "com.rdajila.tandroidsocketio.services.extra.COUNT";

        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        Timer timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                activityManager.getMemoryInfo(memoryInfo);
                String availMem = memoryInfo.availMem / 1048576 + "MB";

                Log.d(TAG, availMem);

                Intent localIntent = new Intent(ACTION_RUN_SERVICE)
                        .putExtra(EXTRA_MEMORY, availMem)
                        .putExtra(COUNTER, String.valueOf(DeviceSocketIO.counter++));

                // Emitir el intent a la actividad
                LocalBroadcastManager.getInstance(DeviceSocketIO.this).sendBroadcast(localIntent);
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 5000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        final String ACTION_MEMORY_EXIT = "com.rdajila.tandroidsocketio.services.action.MEMORY_EXIT";
        Intent localIntent = new Intent(ACTION_MEMORY_EXIT);

        // Emitir el intent a la actividad
        LocalBroadcastManager.getInstance(DeviceSocketIO.this).sendBroadcast(localIntent);

        Log.d(TAG, "Servicio destruido...");
    }
}