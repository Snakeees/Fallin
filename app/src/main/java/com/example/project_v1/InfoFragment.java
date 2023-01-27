package com.example.project_v1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class InfoFragment extends Fragment {


    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor accelerometerSensor;
    private TextView gyrotextX;
    private TextView gyrotextY;
    private TextView gyrotextZ;
    private TextView nogyro;
    private TextView accelTextX;
    private TextView accelTextY;
    private TextView accelTextZ;
    private TextView noaccel;
    private TextView smalldata;
    private TextView bigdata;
    private TextView freefall;
    private TextView falldetected;
    private TextView rootview;

    boolean fallDetected = false;
    private boolean freeFall = false;
    private boolean TFplot = true;


    private LineChart chart;
    private Thread thread;
    private Boolean plotData = true;

    private String data1 = "";
    private double bigdata1 = 0;
    private double smalldata1 = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        gyrotextX = view.findViewById(R.id.gyrotextX);
        gyrotextY =  view.findViewById(R.id.gyrotextY);
        gyrotextZ =  view.findViewById(R.id.gyrotextZ);
        nogyro =  view.findViewById(R.id.noGyroscope);

        accelTextX = view.findViewById(R.id.acc_textX);
        accelTextY =  view.findViewById(R.id.acc_textY);
        accelTextZ =  view.findViewById(R.id.acc_textZ);
        noaccel =  view.findViewById(R.id.noAccelerometer);


        smalldata =  view.findViewById(R.id.smallData);
        bigdata =  view.findViewById(R.id.bigData);

        freefall = view.findViewById(R.id.freefall);
        falldetected =  view.findViewById(R.id.falldetected);
        rootview =  view.findViewById(R.id.root);

        Button plot =  view.findViewById(R.id.plot);
        Button reset =  view.findViewById(R.id.reset);




        chart = view.findViewById(R.id.chart);
        // enable description text
        chart.getDescription().setEnabled(true);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        //xl.enableGridDashedLine(5f, 5f, 1f);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setLabelCount(10);

        YAxis leftAxis = chart.getAxisLeft();
        //leftAxis.enableGridDashedLine(5f, 5f, 1f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(3.5f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawTopYLabelEntry(false);
        leftAxis.setLabelCount(7);
        leftAxis.setDrawTopYLabelEntry(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getAxisLeft().setDrawGridLines(true);
        chart.getXAxis().setDrawGridLines(true);
        chart.setDrawBorders(true);
        Description description = chart.getDescription();
        description.setEnabled(false);

        feedMultiple();


        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        plot.setOnClickListener(v -> plot());
        reset.setOnClickListener(v -> reset());

        return view;

    }

    private void feedMultiple() {
        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(() -> {
            int x = 0;
            while (true) {
                if (TFplot) {
                    plotData = true;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (freeFall){
                    x+=1;
                    if (x>=50){
                        freeFall = false;
                        x = 0;
                        freefall.setText("FF");
                    }
                }
            }
        });

        thread.start();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(accelListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        if (accelerometerSensor == null) {
            noaccel.setVisibility(View.VISIBLE);
            accelTextX.setVisibility(View.INVISIBLE);
            accelTextY.setVisibility(View.INVISIBLE);
            accelTextZ.setVisibility(View.INVISIBLE);

        }

        if (gyroscopeSensor == null) {
            nogyro.setVisibility(View.VISIBLE);
            gyrotextX.setVisibility(View.INVISIBLE);
            gyrotextY.setVisibility(View.INVISIBLE);
            gyrotextX.setVisibility(View.INVISIBLE);
        }

    }


    private void addEntry( double accel) {

        LineData data = chart.getData();

        if (data != null) {


            ILineDataSet set = data.getDataSetByIndex(0);


            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) accel), 0);
            String var1 = Integer.toString(set.getEntryCount());
            String var2 = Double.toString(accel);
            String out = "("+var1+","+var2+")";
            data1 = data1+"\n"+out;
            //System.out.println(out);



            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(300);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Resultant Acceleration(g)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.CYAN);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    private final SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = ((double)event.values[0])*1000.0;
            x = (((int)x)/1000.0);
            double y = ((double)event.values[1])*1000.0;
            y = (((int)y)/1000.0);
            double z = ((double)event.values[2])*1000.0;
            z = (((int)z)/1000.0);
            //Log.i("gyroscope values", "X : " + x + " rad/s" + "Y : " + y + " rad/s" + "Z : " + z + " rad/s");
            gyrotextX.setText("X : " + x + " rad/s");
            gyrotextY.setText("Y : " + y + " rad/s");
            gyrotextZ.setText("Z : " + z + " rad/s");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

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
            accelTextX.setText("X : " + x + " m/s");
            accelTextY.setText("Y : " + y + " m/s");
            accelTextZ.setText("Z : " + z + " m/s");
            double rootSquare;
            rootSquare = (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)))/9.81;
            //Log.i("rootSquare", Double.toString(rootSquare));
            double rootround = rootSquare*1000.0;
            rootround = (((int)rootround)/1000.0);
            rootview.setText("Root: "+ rootround +"m/s");
            if(rootround>bigdata1){
                bigdata1 = rootround;
                bigdata.setText("Min: "+ rootround +"m/s");
            }
            if(rootround<smalldata1){
                smalldata1 = rootround;
                smalldata.setText("Min: "+ rootround +"m/s");
            }
            if(plotData){
                //Log.i("", "XYZ = " + rootround);
                addEntry((float)rootround);
                plotData = false;
            }

            if (rootSquare < 0.1) { //person free falling
                //System.out.println("in freefall");
                freeFall = true;
                freefall.setText("In freefall");
            }
            if (freeFall && rootSquare > 2.82 && !fallDetected) { //person hit the ground
                System.out.println("hit the ground");
                freeFall = false;
                fallDetected = true;
                falldetected.setText("Fall detected");
            }
            if (fallDetected) {
                fallDetected = false;
                //Log.i("fall dectected", "done");
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };


    public void reset(){
        if (!freefall.getText().toString().equals("FF")){
            freefall.setText("FF");
            falldetected.setText("FD");
        }
        else {
            chart.clearValues();
            bigdata1 = 0;
            smalldata1 = 1000;
        }
    }

    public void plot(){
        if (TFplot){
            TFplot = false;
            plotData = false;
        }
        else {
            TFplot = true;
            plotData = true;
        }
        if(chart.getData() != null) {
            chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
            chart.invalidate();
        }

    }




}