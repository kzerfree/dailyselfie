package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.dailyselfie.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    // Notification action elements
    private Intent mNotificationIntent;
    private PendingIntent mPendingIntent;

    // Notification sound and vibration on arrival
    private final Uri soundURI = Uri
            .parse("android.resource://ttcntt.sgu.exalarm/" + R.raw.alarm_rooster);
    //private final long[] mVibrationPattern = { 0, 200, 200, 300 };

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mNotificationIntent = new Intent(context, MainActivity.class);
            mPendingIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            // Build notification
            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setTicker("Ping pong")
                    .setSmallIcon(R.drawable.ic_camera)
                    .setAutoCancel(true)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Đã tới giờ chụp ảnh")
                    .setContentIntent(mPendingIntent);
            //.setSound(soundURI);

            String channelId = "ALARM";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Your alarm is here",
                    NotificationManager.IMPORTANCE_HIGH);
            // Get NotificationManager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            Toast.makeText(context, "Notification", Toast.LENGTH_LONG).show();
        }
        catch (Exception exception) {
            Log.d("NOTIFICATION", exception.getMessage().toString());
        }
    }
}

