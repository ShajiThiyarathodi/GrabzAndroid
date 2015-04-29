package com.cmpe295.grabz.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmpe295.grabz.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */

public class AboutUsFragment extends DialogFragment {
    public static final String CLASS_NAME = AboutUsFragment.class.getSimpleName();
    private TextView aboutUs;
    public AboutUsFragment(){
        //Empty constructor required
    }
    public static AboutUsFragment newInstance() {
        AboutUsFragment frag = new AboutUsFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container);
        aboutUs = (TextView) view.findViewById(R.id.aboutUs);
        aboutUs.setText("Grabz project developed for CMPE295B");

        return view;

    }
}
