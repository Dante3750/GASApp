package com.netconnect.sitienergy.activity;

import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;

import org.json.JSONObject;

public class DialogResetPassword extends DialogFragment {

    private static final String TAG = DialogResetPassword.class.getSimpleName();
    private String url;
    private String changedPass;
    private EditText editOldPass;
    private EditText editNewPass;
    private EditText editConfirmPass;
    private TextView btnCancel;
    private TextView btnOK;
    private SharedPreferences sharedpreferences;
    private JSONParser jParser = new JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_reset_password, container, false);

        sharedpreferences = getActivity().getSharedPreferences(AppController.MyPREFERENCES, Context.MODE_PRIVATE);
        editOldPass = (EditText) rootView.findViewById(R.id.edt_old_pass);
        editNewPass = (EditText) rootView.findViewById(R.id.edt_new_pass);
        editConfirmPass = (EditText) rootView.findViewById(R.id.edt_confirm);
        btnCancel = (TextView) rootView.findViewById(R.id.btnCancel);
        btnOK = (TextView) rootView.findViewById(R.id.btnOk);
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
                GUIUtils.hideKeyboard(getActivity());
                if (jParser.isConnectingToInternet(getActivity().getApplicationContext())) {
                    // Reset errors.
                    editOldPass.setError(null);
                    editNewPass.setError(null);
                    editConfirmPass.setError(null);

                    // Store values at the time of the login attempt.
                    String oldPass = editOldPass.getText().toString();
                    String newPass = editNewPass.getText().toString();
                    String confirmPass = editConfirmPass.getText().toString();

                    boolean cancel = false;
                    View focusView = null;

                    if (TextUtils.isEmpty(oldPass)) {
                        editOldPass.setError(getString(R.string.error_field_required));
                        focusView = editOldPass;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(newPass)) {
                        editNewPass.setError(getString(R.string.error_field_required));
                        focusView = editNewPass;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(confirmPass)) {
                        editConfirmPass.setError(getString(R.string.error_field_required));
                        focusView = editConfirmPass;
                        cancel = true;
                    }

                    if (!(newPass.equals(confirmPass))) {
                        editConfirmPass.setError(getString(R.string.error_password_does_not_match));
                        focusView = editNewPass;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        changedPass=editNewPass.getText().toString();
                        Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "reset_password").buildUpon()
                                .appendQueryParameter("old_pass", editOldPass.getText().toString())
                                .appendQueryParameter("new_pass",changedPass)
                                .appendQueryParameter("user_type",AppController.getUserType())
                                .appendQueryParameter("cust_id", AppController.getUsername());
                        url = uriBuilder.build().toString();
                        Log.v("reset", url);
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

            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String jsonString) {

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                JSONObject json = new JSONObject(jsonString);
                String result = json.optString("RESULT", "");
                Log.v(TAG, result + "");
                if ("SUCCESS".equals(result)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove(AppController.KEY_PASS);
                    editor.putString(AppController.KEY_PASS, changedPass);
                    editor.commit();
                    AppController.setPassword(changedPass);
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Password do not match", Toast.LENGTH_LONG).show();
                }
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

