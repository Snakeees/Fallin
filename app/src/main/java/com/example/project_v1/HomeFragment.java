package com.example.project_v1;

import android.os.Bundle;

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



public class HomeFragment extends Fragment {

    private EditText contact1, contact2, contact3;
    private Button saveInfo;
    private ImageButton call1, call2, call3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        contact1 = view.findViewById(R.id.contact1id);
        contact2 = view.findViewById(R.id.contact2id);
        contact3 = view.findViewById(R.id.emergencyid);
        saveInfo = view.findViewById(R.id.saveinfoid);
        call1 = view.findViewById(R.id.call1id);
        call2 = view.findViewById(R.id.call2id);
        call3 = view.findViewById(R.id.calleid);

        try {
            String[] numbers = readFile().split(";");
            contact1.setText(numbers[0]);
            contact2.setText(numbers[1]);
            contact3.setText(numbers[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

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
                } catch (Exception e) {}

                Toast.makeText(getActivity(), "Contacts have been saved.", Toast.LENGTH_SHORT).show();
            }
        });

        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact1.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    Intent phone_intent = new Intent(Intent.ACTION_CALL);
                    phone_intent.setData(Uri.parse("tel:" + contact));
                    startActivity(phone_intent);
                }
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact2.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact, null, WarningFragment.FALL_DETECTED_MESSAGE, null, null);
                }
            }
        });
        call3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact3.getText().toString();
                if (contact.isEmpty()) {
                    Toast.makeText(getActivity(), "Contact is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact, null, WarningFragment.FALL_DETECTED_MESSAGE, null, null);
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
}
