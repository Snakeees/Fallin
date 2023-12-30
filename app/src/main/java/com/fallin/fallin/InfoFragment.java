package com.fallin.fallin;

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
    private Sensor accelerometerSensor;
    private TextView accelTextX, accelTextY, accelTextZ, noaccel, smalldata, bigdata, freefall, falldetected, rootview, timediffview;

    private boolean fallDetected = false, freeFall = false, TFplot = true;


    private LineChart chart;
    private Thread thread;
    private Boolean plotData = true;

    private String data1 = "";
    private double min = 1000, max = -1;
    private int timediff, minTime = 0, maxTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.unhide();
            mainActivity.setSelected();
        }

        accelTextX = view.findViewById(R.id.acc_textX);
        accelTextY =  view.findViewById(R.id.acc_textY);
        accelTextZ =  view.findViewById(R.id.acc_textZ);
        noaccel =  view.findViewById(R.id.noAccelerometer);


        smalldata =  view.findViewById(R.id.smallData);
        bigdata =  view.findViewById(R.id.bigData);

        freefall = view.findViewById(R.id.freefall);
        falldetected =  view.findViewById(R.id.falldetected);
        rootview =  view.findViewById(R.id.root);
        timediffview =  view.findViewById(R.id.timediffview);

        Button plot =  view.findViewById(R.id.plot);
        Button reset =  view.findViewById(R.id.reset);


        chart = view.findViewById(R.id.chart);
        chart.getDescription().setEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);
        Legend l = chart.getLegend();
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
        leftAxis.setAxisMaximum(8f);
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
                        try {
                            requireActivity().runOnUiThread(() -> {
                                try {
                                    freefall.setText("FF");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {}
                    }
                }
            }
        });

        thread.start();

    }

    /*@Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }

    }*/


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(accelListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        if (accelerometerSensor == null) {
            noaccel.setVisibility(View.VISIBLE);
            accelTextX.setVisibility(View.INVISIBLE);
            accelTextY.setVisibility(View.INVISIBLE);
            accelTextZ.setVisibility(View.INVISIBLE);

        }

    }


    private int addEntry( double accel) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            int var1 = set.getEntryCount();
            String var2 = Double.toString(accel);
            String out = "("+Integer.toString(var1)+","+var2+")";
            data1 = data1+"\n"+out;

            data.addEntry(new Entry(var1, (float) accel), 0);
            //System.out.println(out);

            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(300);
            chart.moveViewToX(data.getEntryCount());

            return var1;
        }
        return 0;
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Resultant Acceleration(g)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.rgb(22,162,237));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
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
            accelTextX.setText("X : " + x + " m/s");
            accelTextY.setText("Y : " + y + " m/s");
            accelTextZ.setText("Z : " + z + " m/s");
            double rootSquare;
            rootSquare = (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)))/9.81;
            //Log.i("rootSquare", Double.toString(rootSquare));
            double rootround = rootSquare*1000.0;
            rootround = (((int)rootround)/1000.0);
            rootview.setText("Root: "+ rootround +" g");
            if(plotData){
                //Log.i("", "XYZ = " + rootround);
                int timeInt = addEntry((float)rootround);
                plotData = false;
                if(rootround>max){
                    max = rootround;
                    bigdata.setText("Max: "+ rootround +" g");
                }
                if(rootround<min){
                    min = rootround;
                    smalldata.setText("Min: "+ rootround +" g");
                }
                if (0.15 < rootSquare && rootSquare < 0.6) {
                    //System.out.println("in freefall");
                    freeFall = true;
                    freefall.setText("In freefall");
                    maxTime = timeInt;
                }

                if (freeFall && rootSquare < 0.15) {
                    freeFall = false;
                    freefall.setText("FF");
                }

                if (freeFall && rootSquare > 8 && !fallDetected) {//2.82
                    System.out.println("hit the ground");
                    freeFall = false;
                    fallDetected = true;
                    falldetected.setText("Fall detected");
                    minTime = timeInt;
                    timediff = minTime-maxTime;
                    timediffview.setText(timediff+" U");
                }
                if (fallDetected) {
                    fallDetected = false;
                    //Log.i("fall dectected", "done");
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };


    public void reset(){
        if (!freefall.getText().toString().equals("FF") || !falldetected.getText().toString().equals("FD")){
            freefall.setText("FF");
            falldetected.setText("FD");
            max = -1;
            min = 1000;
            minTime = 0;
            maxTime = 0;
            timediff = maxTime-minTime;
            timediffview.setText(timediff+" U");

        }
        else {
            chart.clearValues();
            freefall.setText("FF");
            falldetected.setText("FD");
            min = 1000;
            max = -1;
            minTime = 0;
            maxTime = 0;
            timediff = maxTime-minTime;
            timediffview.setText(timediff+" U");
            bigdata.setText("Root High");
            smalldata.setText("Root Low");
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