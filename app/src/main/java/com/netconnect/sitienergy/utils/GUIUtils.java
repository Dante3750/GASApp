package com.netconnect.sitienergy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.netconnect.sitienergy.R;

public class GUIUtils {
    public static ProgressDialog pDialog;

    public static void redirectToActivity(Context context, Class cl, Bundle b) {

        Intent i = new Intent(context, cl);
        if (b != null)
            i.putExtras(b);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static ProgressDialog createDialog(Context context, String message) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        return pDialog;
    }

    public static void dismissDialog() {

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public static void hideKeyboard(Activity activity) {

        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static void getTransition(Fragment fragment){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Inflate transitions to apply
            Transition changeTransform = TransitionInflater.from(fragment.getActivity()).
                    inflateTransition(R.transition.change_image_transform);
            Transition explodeTransform = TransitionInflater.from(fragment.getActivity()).
                    inflateTransition(android.R.transition.explode);
            // Setup exit transition on first fragment
            fragment.setEnterTransition(changeTransform);
            fragment.setExitTransition(explodeTransform);
        }

    }

}
