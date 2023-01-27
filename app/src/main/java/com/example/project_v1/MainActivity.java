package com.example.project_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.example.project_v1.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    boolean fallDetected = false;
    private boolean freeFall = false;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        relpaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home_button:
                    relpaceFragment(new HomeFragment());
                    break;
                case R.id.info_button:
                    relpaceFragment(new InfoFragment());
                    break;
            }

            return true;
        });

        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "sample");
            FileWriter writer = new FileWriter(gpxfile);
            String cont1 = "123";
            String cont2 = "456";
            String cont3 = "789";

            writer.write(cont1 + "\n" + cont2 + "\n" + cont3);
            writer.flush();
            writer.close();
            String output = readFile();
            //Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }


        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accelListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);



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
                }
            }
        });

        thread.start();

    }



    private final SensorEventListener accelListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = ((double)event.values[0])*1000.0;
            x = (((int)x)/1000.0);
            double y = ((double)event.values[1])*1000.0;
            y = (((int)y)/1000.0);
            double z = ((double)event.values[2])*1000.0;
            z = (((int)z)/1000.0);
            //Log.i("accelorometer values 1 ", "X : " + x + " m/s" + "Y : " + y + " m/s" + "Z : " + z + " m/s");
            double rootSquare;
            rootSquare = (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)))/9.81;
            //Log.i("rootSquare", Double.toString(rootSquare));

            if (rootSquare < 0.1) { //person free falling
                //System.out.println("in freefall");
                freeFall = true;
            }
            if (freeFall && rootSquare > 2.82 && !fallDetected) { //person hit the ground
                System.out.println("hit the ground");
                freeFall = false;
                fallDetected = true;
            }
            if (fallDetected) {
                fallDetected = false;
                //Log.i("fall dectected", "done");
                relpaceFragment(new WarningFragment());

            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };



    private void relpaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private String readFile() {
        File fileEvents = new File(MainActivity.this.getFilesDir() + "/text/sample");
        String text = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            text = String.valueOf(br.read());
            //System.out.println(br.read());
            int i;
            while ((i = br.read()) != -1) {
                //System.out.print((char)i);
            }
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            System.out.println("content: " + content.toString());


            br.close();
        } catch (IOException e) {
        }
        return text;
    }

    public String readAllLines(BufferedReader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }

        return content.toString();
    }


}
