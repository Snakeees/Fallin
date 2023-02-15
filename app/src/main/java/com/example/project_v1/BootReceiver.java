package com.example.project_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.toString());
        String action = intent.getAction();

        if (action != null && action.equals(BootAccessibilityService.FALL_DETECTED_ACTION)) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra("frag", 1);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            Log.i(TAG,"MainActivity Launched");
        }
    }
}
