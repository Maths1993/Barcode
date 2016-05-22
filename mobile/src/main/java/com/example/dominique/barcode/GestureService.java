package com.example.dominique.barcode;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public final class GestureService extends Service {
    private final static String TAG = "Main Service";
    private final IBinder binder = new MainServiceBinder();

    @Override
    public final void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created!!!!!!");
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public final int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }


    @Override
    public final void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service created");
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public final IBinder onBind(final Intent intent) {
        return this.binder;
    }

    public final class MainServiceBinder extends Binder {
        final GestureService getService() {
            return GestureService.this;
        }
    }
}
