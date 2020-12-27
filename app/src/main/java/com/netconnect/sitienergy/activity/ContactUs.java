package com.netconnect.sitienergy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.PermissionUtility;

public class ContactUs extends Fragment {

    private static final String TAG = ContactUs.class.getSimpleName();
    private static ContactUs mInstance;
    private ImageView imgCall;
    private ImageView imgEmail;

    public static synchronized ContactUs getInstance() {
        return mInstance;
    }

    public ContactUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInstance = this;
        GUIUtils.getTransition(this);
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        imgCall = (ImageView) rootView.findViewById(R.id.call_us);
        imgEmail = (ImageView) rootView.findViewById(R.id.mail_us);

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToCustomerCare();
            }
        });

        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"connect.mbd@torrentgas.com"});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }catch (SecurityException se){

                }
            }
        });
        return rootView;
    }

    private void callToCustomerCare() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            boolean result = PermissionUtility.checkCallPermission(getActivity());
                            if(result) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:1800123567890"));
                                startActivity(Intent.createChooser(callIntent, ""));
                            }
                        }catch (SecurityException se){

                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_phone).setTitle("Call Customer Care").setMessage("Are you sure? You want to call customer care centre.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+917830707087"));
                        startActivity(Intent.createChooser(callIntent, ""));
                }
                break;
        }
    }
}
