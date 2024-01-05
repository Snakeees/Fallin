package com.fallin.fallin;

import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
    String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.toString());
        String action = intent.getAction();

        if (action != null && action.equals(ForegroundService.FALL_DETECTED_ACTION)) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra("frag", 1);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            Log.i(TAG,"MainActivity Launched");
        } else {
            createNotificationChannel(context);
            Intent serviceIntent = new Intent(context, ForegroundService.class);
            ContextCompat.startForegroundService(context, serviceIntent);

        }
    }

    private void createNotificationChannel(Context context) {
        NotificationChannel serviceChannel = new NotificationChannel(
                MainActivity.NotificationChannelID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
