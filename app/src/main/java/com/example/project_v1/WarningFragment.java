package com.example.project_v1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class WarningFragment extends Fragment {

    private TextView timer;
    private Thread thread;
    private MediaPlayer fallSound;
    private boolean isCountdownRunning = false;
    public static final String FALL_DETECTED_MESSAGE = "A FALL HAS BEEN DETECTED BY THE DEVICE, PLEASE CONTACT IMMEDIATELY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        timer = view.findViewById(R.id.timerid);
        Button exit = view.findViewById(R.id.exit);
        countdown();
        exit.setOnClickListener(v -> exitFragment());
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isCountdownRunning) {
            stopCountdown();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isCountdownRunning) {
            stopCountdown();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isCountdownRunning) {
            stopCountdown();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isCountdownRunning) {
            stopCountdown();
        }
    }


    private void countdown() {
        if (thread != null) {
            thread.interrupt();
        }
        isCountdownRunning = true; // Countdown is running

        fallSound = MediaPlayer.create(getActivity(), R.raw.beep);

        thread = new Thread(() -> {
            fallSound.start();
            for (int i = 10; i >= 0; i--) {
                final int countdownValue = i;
                try {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            timer.setText(Integer.toString(countdownValue));
                        } catch (Exception e) {}
                    });
                } catch (Exception e) {}
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(fallSound.isPlaying()) {
                fallSound.stop();
            }
            try {
                callAndMessageEmergencyContact();
            } catch (Exception e) {}
            try {
                exitFragment();
            } catch (Exception e) {}
        });

        thread.start();
    }

    private void stopCountdown() {
        if (fallSound.isPlaying()) {
            fallSound.stop();
        }
        isCountdownRunning = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private void exitFragment() {
        stopCountdown();
        final FragmentActivity activity = getActivity();
        if (isAdded() && activity != null) {
            activity.runOnUiThread(() -> {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(WarningFragment.this);
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                fragmentTransaction.commit();
            });
        }
    }

    private void callAndMessageEmergencyContact() {
        try {
            String numbers = readFile();
            String[] contactArray = numbers.split(";");
            if (contactArray.length >= 1) {
                String contact1 = contactArray[0];
                String contact2 = contactArray[1];
                String contact3 = contactArray[2];

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(contact2, null, FALL_DETECTED_MESSAGE, null, null);
                smsManager.sendTextMessage(contact3, null, FALL_DETECTED_MESSAGE, null, null);

                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + contact1));
                startActivity(phoneIntent);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private String readFile() {
        File file = new File(requireActivity().getFilesDir(), "/text/numbers.txt");
        String firstLine = "";
        if (!file.exists()) {
            Objects.requireNonNull(file.getParentFile()).mkdirs();
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
        return firstLine;
    }

}

