package com.fallin.fallin;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.app.Notification;
import android.util.Log;


public class ForegroundService extends Service implements SensorEventListener{

    private static final String TAG = "ForegroundService";
    final int FOREGROUND_ID = 6969;

    private boolean fallDetected, freeFall = false;
    public static final String FALL_DETECTED_ACTION = "com.fallin.fallin.FALL_DETECTED";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private Thread thread;


    private void feedMultiple() {
        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(() -> {
            int x = 0;
            while (true) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (freeFall){
                    x+=1;
                    if (x>=50){
                        freeFall = false;
                        x = 0;
                    }
                } else {x = 0;}
            }
        });
        thread.start();

    }


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        feedMultiple();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_ID, getNotification());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        feedMultiple();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //Log.i(TAG, String.format("X: %.3f m/s, Y: %.3f m/s, Z: %.3f m/s", x, y, z));

            float rootSquare = (float) (Math.sqrt(x*x + y*y + z*z) / 9.81);
            //Log.i(TAG, String.valueOf(rootSquare));

            if (0.15 < rootSquare && rootSquare < 0.6) {
                Log.i(TAG, "Person in freefall");
                Log.i(TAG, String.valueOf(rootSquare));
                freeFall = true;
            }

            if (freeFall && rootSquare < 0.15) {
                freeFall = false;
            }

            if (freeFall && rootSquare > 8 && !fallDetected) {//2.82
                Log.i(TAG, "Person hit the ground");
                Log.i(TAG, String.valueOf(rootSquare));
                freeFall = false;
                fallDetected = true;
            }

            if (fallDetected) {
                fallDetected = false;
                Log.i(TAG, String.valueOf(rootSquare));
                Log.i(TAG, "Fall Detected - Launching WarningFragment");
                Intent broadcastIntent = new Intent(FALL_DETECTED_ACTION);
                broadcastIntent.setComponent(new ComponentName("com.fallin.fallin", "com.fallin.fallin.BootReceiver"));
                sendBroadcast(broadcastIntent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this, MainActivity.NotificationChannelID)
                .setContentTitle("Foreground Service")
                .setContentText("This is running in foreground")
                .setSmallIcon(R.drawable.fallin_shield_500_no_bg);
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
