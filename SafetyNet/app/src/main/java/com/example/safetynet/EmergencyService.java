package com.example.safetynet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class EmergencyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle background tasks
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}