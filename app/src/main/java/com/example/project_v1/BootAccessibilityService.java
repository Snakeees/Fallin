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

import java.util.*;

public class BootAccessibilityService extends AccessibilityService implements SensorEventListener {

    private static final String TAG = "BootAccessibilityService";
    private boolean fallDetected, freeFall = false;
    public static final String FALL_DETECTED_ACTION = "com.example.project_v1.FALL_DETECTED";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private Thread thread;

    private static final int MAX_SIZE = 200;
    private ArrayList<Float> values = new ArrayList<>();

    public void addValue(float x) {
        values.add(x);
        if (values.size() > MAX_SIZE) {
            values.remove(0);
        }
    }

    public Float[] getValues() {
        return values.toArray(new Float[0]);
    }

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
    protected void onServiceConnected() {
        super.onServiceConnected();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        feedMultiple();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //Log.i(TAG, String.format("X: %.3f m/s, Y: %.3f m/s, Z: %.3f m/s", x, y, z));

            float rootSquare = (float) (Math.sqrt(x*x + y*y + z*z) / 9.81);
            addValue(rootSquare);
            //Log.i(TAG, String.valueOf(rootSquare));

            if (0.05 < rootSquare && rootSquare < 0.5) {
                Log.i(TAG, "Person in freefall");
                Log.i(TAG, String.valueOf(rootSquare));
                freeFall = true;
            }

            if (freeFall && rootSquare < 0.05) {
                freeFall = false;
            }

            if (freeFall && rootSquare > 6 && !fallDetected) {//2.82
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
