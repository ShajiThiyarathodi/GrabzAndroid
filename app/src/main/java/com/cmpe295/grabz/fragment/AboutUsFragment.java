package com.cmpe295.grabz.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.cmpe295.grabz.R;


public class AboutUsFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container,
                false);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
        TextView text = (TextView) rootView.findViewById(R.id.aboutUs);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        getDialog().setTitle("About Grabz");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.logo1);
    }
}

