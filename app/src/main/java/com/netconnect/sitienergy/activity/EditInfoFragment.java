package com.netconnect.sitienergy.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;

import org.json.JSONObject;

/**
 * Created by Vimal Kumar on 8/19/2015.
 */
public class EditInfoFragment extends DialogFragment {

    private static final String TAG = EditInfoFragment.class.getSimpleName();
    private String url;
    private EditText editEmail;
    private EditText editMobile;
    private TextView btnCancel;
    private TextView btnOK;
    private JSONParser jParser = new JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit_info, container, false);

        GUIUtils.getTransition(this);
        editEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        editMobile = (EditText) rootView.findViewById(R.id.edtMobile);
        btnCancel = (TextView) rootView.findViewById(R.id.btnCancel);
        btnOK = (TextView) rootView.findViewById(R.id.btnOk);

        Bundle mArgs = getArguments();
        editEmail.setText(mArgs.getString("email"));
        editMobile.setText(mArgs.getString("mobile"));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jParser.isConnectingToInternet(getActivity().getApplicationContext())) {
                    // Reset errors.
                    editEmail.setError(null);
                    editMobile.setError(null);

                    // Store values at the time of the login attempt.
                    String emailID = editEmail.getText().toString();
                    String mobileNo = editMobile.getText().toString();

                    boolean cancel = false;
                    View focusView = null;

                    if (TextUtils.isEmpty(emailID)) {
                        editEmail.setError(getString(R.string.error_field_required));
                        focusView = editEmail;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(mobileNo)) {
                        editMobile.setError(getString(R.string.error_field_required));
                        focusView = editMobile;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "edit").buildUpon()
                                .appendQueryParameter("email", EncryptionUtil.encode(editEmail.getText().toString()))
                        .appendQueryParameter("mobile", EncryptionUtil.encode(editMobile.getText().toString()))
                                .appendQueryParameter("custID", EncryptionUtil.encode(AppController.getUsername()))
                                .appendQueryParameter("type","android");
                        url = uriBuilder.build().toString();
                        Log.v("edit", url);
                        new JSONParse().execute(url);
                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    private class JSONParse extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String jsonString) {

            try {
                JSONObject json = new JSONObject(jsonString);
                String result = json.optString("RESULT", "");
                Log.v(TAG, result + "");
                if ("SUCCESS".equals(result)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Contact Information updated", Toast.LENGTH_LONG).show();
                    HomeFragment.getInstance().refresh();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Problem occured", Toast.LENGTH_LONG).show();
                }
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
