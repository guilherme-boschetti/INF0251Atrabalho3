package com.example.inf0251atrabalho3.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.inf0251atrabalho3.Globals;
import com.example.inf0251atrabalho3.ProcessMainClass;
import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.apiservice.IApiService;
import com.example.inf0251atrabalho3.apiservice.RetrofitClient;
import com.example.inf0251atrabalho3.database.DBSQLiteHelper;
import com.example.inf0251atrabalho3.model.Moeda;
import com.example.inf0251atrabalho3.repository.Currency;
import com.example.inf0251atrabalho3.util.AndroidUtil;
import com.example.inf0251atrabalho3.util.Notification;
import com.example.inf0251atrabalho3.util.NotificationUtil;
import com.example.inf0251atrabalho3.util.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CurrencyService extends Service {

    public static final String TAG = "INF-CurrencyService";

    private DBSQLiteHelper db;
    private SharedPreferencesUtil preferences;
    private int time = -1;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int NOTIFICATION_ID = 1990;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service Strated");
        db = new DBSQLiteHelper(this);
        preferences = SharedPreferencesUtil.getInstance(this);
        time = preferences.getValue("time_background_consult", -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "INF0251A-Trabalho3", "O serviço de consulta a API de moedas está rodando em Background.", R.mipmap.ic_launcher));
                Log.i(TAG, "restarting foreground successful");
                startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        if (time > 0) {
            // restart the never ending service
            Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
            sendBroadcast(broadcastIntent);
        }
        stopTimerTask();
    }

    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*@Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "Service Strated");
        db = new DBSQLiteHelper(this);
        preferences = SharedPreferencesUtil.getInstance(this);
        time = preferences.getValue("time_background_consult", -1);
        if (time > 0) {
            startTimer();
        } else {
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service Stopped");
        stopTimerTask();
        RestartServiceBroadcastReceiver restartServiceBroadcastReceiver = new RestartServiceBroadcastReceiver();
        registerReceiver(restartServiceBroadcastReceiver, new IntentFilter("RestartServiceWhenStopped.RestartSensor"));
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        unregisterReceiver(restartServiceBroadcastReceiver);
    }*/

    // ==

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stopTimerTask();

        if (time <= 0)
            return;

        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every time
        timer.schedule(timerTask, time, time);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i(TAG, "in timer");
                if (AndroidUtil.isNetworkAvailable(CurrencyService.this)) {
                    callApi(); // Consulta a API de Moedas
                }
                //NotificationUtil.generateNotification(CurrencyService.this); // Para Teste
            }
        };
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // ==

    private void callApi() {
        try {
            final String currency = preferences.getValue("currency", "USD-BRL");

            IApiService apiService = RetrofitClient.createService(IApiService.class);

            Call<List<Currency>> call = apiService.getCurrency(currency);
            call.enqueue(new Callback<List<Currency>>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<List<Currency>> call, Response<List<Currency>> response) {
                    if (response.code() >= 200 && response.code() <= 299) {
                        List<Currency> currencyResponse = response.body();
                        if (currencyResponse != null && !currencyResponse.isEmpty()) {
                            float bidResponse = currencyResponse.get(0).getBid();
                            float askResponse = currencyResponse.get(0).getAsk();

                            // Guardar valor no banco caso valor da api esteja atualizado
                            // ou caso não exista no banco
                            // e notificar
                            Moeda lastMoeda = db.getLastIncludedByCurrency(currency);
                            if (lastMoeda == null ||
                                    lastMoeda.getBidValue() != bidResponse || lastMoeda.getAskValue() != askResponse) {

                                String pattern = "dd/MM/yyyy HH:mm:ss";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String dateHour = simpleDateFormat.format(new Date());

                                Moeda moeda = new Moeda();
                                moeda.setBidValue(bidResponse);
                                moeda.setAskValue(askResponse);
                                moeda.setCurrency(currency);
                                moeda.setDateHourInclusion(dateHour);
                                moeda.setDateMillis(new Date().getTime());
                                db.add(moeda);

                                // Notificar
                                NotificationUtil.generateNotification(CurrencyService.this);
                            }
                        }
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<List<Currency>> call, Throwable t) {
                    // do nothing
                }
            });
        } catch (Exception e) {
            // do nothing
        }
    }
}

// == Fontes de pesquisa e consulta:

// https://developer.android.com/guide/components/services
// https://developer.android.com/training/run-background-service/create-service
// https://developer.android.com/about/versions/oreo/background
// https://medium.com/pongploydev/why-start-the-service-on-android-o-and-encounter-a-problem-record-context-startforegroundservice-3c8ddd8969ef
// https://stackoverflow.com/questions/35578586/background-process-timer-on-android
// https://medium.com/@raziaranisandhu/create-services-never-stop-in-android-b5dcfc5fb4b2
// https://pt.stackoverflow.com/questions/16578/criando-aplicativo-que-rode-em-background
// https://robertohuertas.com/2019/06/29/android_foreground_services/
// https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android
// https://github.com/fabcira/neverEndingAndroidService
// -- Essa aqui foi tomada como base:
// https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android-gt-7
// https://github.com/fabcira/neverEndingProcessAndroid7-
