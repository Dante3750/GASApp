package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.JSONParser;
import com.netconnect.sitienergy.utils.NCOptionBox;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Vimal Kumar
 */
public class ForgotPasswordActivity extends Activity{

    private NCOptionBox userType;
    private Button btnResetPass;
    public static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private EditText userName;
    private EditText emailORMobile;
    private String url;
    private TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        btnResetPass = (Button) findViewById(R.id.resetPass);
        userName = (EditText) findViewById(R.id.username);
        emailORMobile = (EditText) findViewById(R.id.email_or_phone);
        back= (TextView) findViewById(R.id.back);
        userType = (NCOptionBox) findViewById(R.id.user_type);
        emailORMobile.requestFocus();

        Bundle bundle = getIntent().getExtras();
        String user = bundle.getString("userType");
        String userNameString = bundle.getString("userName");

        userType.init(new LinkedHashMap<Integer, String>() {{
            put(1, "Domestic");
            put(2, "Non-Domestic");
        }}, user, false, true);
        userName.setText(userNameString);

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void resetPassword() {

        // Store values at the time of the login attempt.
        String email = emailORMobile.getText().toString();
        String uName = userName.getText().toString();

        String uType = "1";
        if (userType.getSelectedKey() == 2)
            uType = "2";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            focusView = emailORMobile;
            cancel = true;
        }

        if (TextUtils.isEmpty(uName)) {
            focusView = userName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            showAlert("Please fill the details first");
        } else {
            Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "forget_password").buildUpon()
                    .appendQueryParameter("cust_id", uName)
                    .appendQueryParameter("detail", email)
                    .appendQueryParameter("cust_type",uType);
            url = uriBuilder.build().toString();
            email = "";
            uName = "";
            Log.v("forgot_password", url);
            new JSONParse().execute();
        }
    }

    private class JSONParse extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();


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
                if ("SUCCESS".equals(result)) {
                    showAlert("We will shortly be sending your password on your registered email account.");
                } else if ("FAIL".equals(result)) {
                    showAlertFail("The information that you have provided does not match our records. Please check your details and try again");
                    emailORMobile.requestFocus();
                } else {
                    Toast.makeText(getApplicationContext(), "Problem in connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertFail(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
