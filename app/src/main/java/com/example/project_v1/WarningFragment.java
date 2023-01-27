package com.example.project_v1;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;


public class WarningFragment extends Fragment {

    private TextView timer;
    private Button exit;
    private Thread thread;
    private int x;
    public MediaPlayer fallSound;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_warning, container, false);
        timer = view.findViewById(R.id.timerid);
        exit =  view.findViewById(R.id.exit);




        countdown();
        exit.setOnClickListener(v -> exit());


        return view;
    }

    private void countdown() {
        if (thread != null){
            thread.interrupt();
        }
        fallSound = MediaPlayer.create(getActivity(), R.raw.beep);



        thread = new Thread(() -> {
            fallSound = MediaPlayer.create(getActivity(), R.raw.beep);
            for(int i=10;i>=0;i--){
                getActivity().runOnUiThread(new Runnable() {
                    public void run(){
                        timer.setText(Integer.toString(x));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent phone_intent = new Intent(Intent.ACTION_CALL);
            phone_intent.setData(Uri.parse("tel:9701172254"));
            startActivity(phone_intent);
        });

        thread.start();

    }
    public void exit(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
    }

}