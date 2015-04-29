package com.cmpe295.grabz.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmpe295.grabz.R;


public class AboutUsFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container,
                false);
        getDialog().setTitle("About Us");
        // Do something else
        return rootView;
    }
}

