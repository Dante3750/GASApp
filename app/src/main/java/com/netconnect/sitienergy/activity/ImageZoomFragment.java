package com.netconnect.sitienergy.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.adapter.ViewMeterReadingAdapter;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.NCUtils;

/**
 * Created by Vimal Kumar on 8/19/2015.
 */
public class ImageZoomFragment extends DialogFragment {

    private ImageView expandedImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zoom_image, container, false);

        Bundle mArgs = getArguments();
        String meterString=mArgs.getString("image_string");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        expandedImageView = (ImageView) rootView.findViewById(R.id.expanded_image);
        expandedImageView.setImageBitmap(NCUtils.StringToBitMap(meterString));
        GUIUtils.getTransition(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


}
