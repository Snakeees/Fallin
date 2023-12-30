package com.fallin.fallin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.*;
import java.util.*;


public class HomeFragment extends Fragment {

    private EditText contact1, contact2, contact3;
    private Button saveInfo, cancle;
    private ImageButton call, msg1, msg2;
    private String url = "";
    private final String TAG = "HomeFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.unhide();
            mainActivity.setSelected();
        }
        requestLocation();

        contact1 = view.findViewById(R.id.contact1id);
        contact2 = view.findViewById(R.id.contact2id);
        contact3 = view.findViewById(R.id.contact3id);
        saveInfo = view.findViewById(R.id.save_btn);
        cancle = view.findViewById(R.id.cancle_btn);
        call = view.findViewById(R.id.call);
        msg1 = view.findViewById(R.id.msg1);
        msg2 = view.findViewById(R.id.msg2);

        try {
            String[] numbers = readFile().split(";");
            contact1.setText(numbers[0]);
            contact2.setText(numbers[1]);
            contact3.setText(numbers[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact1.setText("");
                contact2.setText("");
                contact3.setText("");
            }
        });

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contact1S = contact1.getText().toString();
                String contact2S = contact2.getText().toString();
                String contact3S = contact3.getText().toString();

                if (contact1S.isEmpty() || contact2S.isEmpty() || contact3S.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(getActivity().getFilesDir(), "text");
                if (!file.exists()) {
                    file.mkdir();
                }
                try {
                    File gpxfile = new File(file, "numbers.txt");
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.write(contact1S + ";" + contact2S + ";" + contact3S);
                    writer.flush();
                    writer.close();
                    String output = readFile().replace(";", ", ");
                    Toast.makeText(getActivity(), output, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }

                Toast.makeText(getActivity(), "Contacts have been saved.", Toast.LENGTH_SHORT).show();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact1.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        Intent phone_intent = new Intent(Intent.ACTION_CALL);
                        phone_intent.setData(Uri.parse("tel:" + contact));
                        startActivity(phone_intent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error making call", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        msg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact2.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is empty", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contact, null, WarningFragment.FALL_DETECTED_MESSAGE + "\n" + url, null, null);
                        Toast.makeText(getActivity(), "Sent Message", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        msg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact3.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contact, null, WarningFragment.FALL_DETECTED_MESSAGE + "\n" + url, null, null);
                        Toast.makeText(getActivity(), "Sent Message", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private String readFile() {

        File file = new File(getActivity().getFilesDir(), "/text/numbers.txt");
        String firstLine = "";
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                firstLine = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("File Error", "File does not exist: " + file.getAbsolutePath());
        }
        return (firstLine != null) ? firstLine : ";;";
    }

    private void requestLocation() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(
                            provider,
                            null,
                            ContextCompat.getMainExecutor(getContext()),
                            location -> {
                                if (location != null) {
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();
                                    url = "http://maps.google.com/?q=" + latitude + "," + longitude;
                                    Log.i(TAG, url);
                                }
                            }
                    );
                }
            }
        }
    }

}
