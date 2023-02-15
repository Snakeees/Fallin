package com.example.project_v1;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class BootAccessibilityService extends AccessibilityService implements SensorEventListener {

    private static final String TAG = "BootAccessibilityService";
    private boolean fallDetected, freeFall = false;
    public static final String FALL_DETECTED_ACTION = "com.example.project_v1.FALL_DETECTED";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private Thread thread;


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double x = ((double)event.values[0])*1000.0;
            x = (((int)x)/1000.0);
            double y = ((double)event.values[1])*1000.0;
            y = (((int)y)/1000.0);
            double z = ((double)event.values[2])*1000.0;
            z = (((int)z)/1000.0);

            //Log.i(TAG, String.format("X: %.3f m/s, Y: %.3f m/s, Z: %.3f m/s", x, y, z));

            double rootSquare = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) / 9.81;
            //Log.i(TAG, String.valueOf(rootSquare));

            if (rootSquare < 0.1) {
                Log.i(TAG, "Person in freefall");
                Log.i(TAG, String.valueOf(rootSquare));
                freeFall = true;
            }

            if (freeFall && rootSquare > 2.82 && !fallDetected) {
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
                broadcastIntent.setComponent(new ComponentName("com.example.project_v1", "com.example.project_v1.BootReceiver"));
                sendBroadcast(broadcastIntent);

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
