package com.example.project_v1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment {

    private EditText contact1, contact2, contact3;
    private Button saveInfo;
    private ImageButton call1, call2, call3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // initializing all our variables.
        contact1 = view.findViewById(R.id.contact1id);
        contact2 = view.findViewById(R.id.contact2id);
        contact3 = view.findViewById(R.id.emergencyid);
        saveInfo = view.findViewById(R.id.saveinfoid);
        call1 = view.findViewById(R.id.call1id);
        call2 = view.findViewById(R.id.call2id);
        call3 = view.findViewById(R.id.calleid);

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String contact1S = contact1.getText().toString();
                String contact2S = contact2.getText().toString();
                String contact3S = contact3.getText().toString();

                // validating if the text fields are empty or not.
                if (contact1S.isEmpty() || contact2S.isEmpty() || contact3S.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // after adding the data we are displaying a toast message.
                Toast.makeText(getActivity(), "Data has been saved.", Toast.LENGTH_SHORT).show();
            }
        });

        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact1.getText().toString();
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + contact));
                startActivity(phone_intent);
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact2.getText().toString();
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + contact));
                startActivity(phone_intent);
            }
        });
        call3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = contact3.getText().toString();
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + contact));
                startActivity(phone_intent);
            }
        });




        return view;
    }
}
