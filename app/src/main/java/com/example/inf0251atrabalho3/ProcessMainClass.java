package com.example.inf0251atrabalho3;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.inf0251atrabalho3.backgroundservice.CurrencyService;

public class ProcessMainClass {
    public static final String TAG = "INF-ProcessMainClass";
    private static Intent serviceIntent = null;

    public ProcessMainClass() {
    }


    private void setServiceIntent(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, CurrencyService.class);
        }
    }
    /**
     * launching the service
     */
    public void launchService(Context context) {
        if (context == null) {
            return;
        }
        setServiceIntent(context);
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception starting service", e);
        }
        Log.d(TAG, "ProcessMainClass: start service go!!!!");
    }
}

