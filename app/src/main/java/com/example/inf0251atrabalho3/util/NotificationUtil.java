package com.example.inf0251atrabalho3.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.inf0251atrabalho3.MainActivity;
import com.example.inf0251atrabalho3.R;

import java.util.Date;

// https://developer.android.com/training/notify-user/build-notification

public class NotificationUtil {

    public static void generateNotification(Context context) {

        String title = context.getString(R.string.notification_title);
        String message = context.getString(R.string.notification_message);

        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Seta as informações do canal padrão do app
        String androidChannelId = "com.example.inf0251atrabalho3";
        String androidChannelName = "ApiMoedas";
        String androidChannelDescription = "Canal de Notificacao";

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(androidChannelId, androidChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(androidChannelDescription);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //==

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            try {
                notificationManager.createNotificationChannel(channel);
            } catch (Exception e) {
                Log.e("NotificationUtil", e.getMessage());
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context, androidChannelId);

        Notification notification = mBuilder
                .setTicker(message)
                .setWhen(0)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(intent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText(message).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Seta a vibraçao para a notificaçao
        notification.defaults = Notification.DEFAULT_VIBRATE;

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        try {
            notificationManager.notify(m, notification);
        } catch (Exception e) {
            Log.e("Erro na notificaçao", e.getMessage(), e);
        }
    }
}
