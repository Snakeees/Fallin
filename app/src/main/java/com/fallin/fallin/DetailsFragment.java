package com.fallin.fallin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.unhide();
            mainActivity.setSelected();
        }
        TextView textView = view.findViewById(R.id.p5);
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setText("• View our privacy policy at: https://fallin.co.in/privacy");

        return view;
    }

}
