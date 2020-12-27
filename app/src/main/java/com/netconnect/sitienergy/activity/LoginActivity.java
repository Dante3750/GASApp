package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;
import com.netconnect.sitienergy.utils.NCOptionBox;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class LoginActivity extends Activity {
    private Button btnLogin;
    public static final String TAG = LoginActivity.class.getSimpleName();
    private EditText userNameView;
    private EditText passwordView;
    private TextView forgotPassword;
    private RelativeLayout layout;
    private SharedPreferences sharedpreferences;
    private String url;
    private NCOptionBox userType;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login);
        sharedpreferences = getSharedPreferences(AppController.MyPREFERENCES, Context.MODE_PRIVATE);

        layout = (RelativeLayout) findViewById(R.id.login_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        userNameView = (EditText) findViewById(R.id.email_or_phone);
        passwordView = (EditText) findViewById(R.id.password);
        forgotPassword = (TextView) findViewById(R.id.forgot_pass);

        if (sharedpreferences.contains(AppController.KEY_USERNAME)) {
            AppController.setUsername(sharedpreferences.getString(AppController.KEY_USERNAME, ""));
            AppController.setPassword(sharedpreferences.getString(AppController.KEY_PASS, ""));
            AppController.setName(sharedpreferences.getString(AppController.KEY_NAME, ""));
            AppController.setUserType(sharedpreferences.getString(AppController.KEY_USER_TYPE, ""));

            if (AppController.getUserType().equals("2")) {
                GUIUtils.redirectToActivity(getApplicationContext(), IndustrialMeterUploadActivity.class, new Bundle());
            } else {
                GUIUtils.redirectToActivity(getApplicationContext(), MainActivity.class, new Bundle());
            }
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            finish();

            return;
        }

        userType = (NCOptionBox) findViewById(R.id.user_type);
        userType.init(new LinkedHashMap<Integer, String>() {{
            put(1, "Domestic");
            put(2, "Non-Domestic");
        }}, "1", false, true);

        userType.setOnValueChangeListener(new NCOptionBox.OnValueChangeListener() {
            @Override
            public void onChange(NCOptionBox v) {

                userNameView.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });

        userNameView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                userNameView.requestLayout();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

                return false;
            }
        });

        passwordView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                passwordView.requestLayout();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

                return false;
            }
        });

        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                GUIUtils.hideKeyboard(LoginActivity.this);
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String uType = "1";
                if (userType.getSelectedKey() == 2)
                    uType = "2";
                String user = userNameView.getText().toString();
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                i.putExtra("userType", uType);
                i.putExtra("userName", user);
                startActivity(i);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void signIn() {
        // Reset errors.
        userNameView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = userNameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        }

        String loginType = "1";
        if (userType.getSelectedKey() == 2)
            loginType = "2";


        if (cancel) {
            focusView.requestFocus();
        } else {
            Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "login").buildUpon()
                    .appendQueryParameter("userID", EncryptionUtil.encode(email))
                    .appendQueryParameter("password", EncryptionUtil.encode(password))
                    .appendQueryParameter("type", "android")
                    .appendQueryParameter("login", loginType);
            url = uriBuilder.build().toString();

            android.util.Log.d ( TAG , "url: "+url );
            email = "";
            password = "";
            EncryptionUtil.encode(email);
            new JSONParse().execute();
        }
    }

    private class JSONParse extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
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
                String name = json.optString("Name", "");
                if ("SUCCESS".equals(result)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String u = userNameView.getText().toString();
                    String p = passwordView.getText().toString();
                    String loginType = "1";
                    if (userType.getSelectedKey() == 2)
                        loginType = "2";

                    editor.putString(AppController.KEY_USERNAME, u);
                    editor.putString(AppController.KEY_PASS, p);
                    editor.putString(AppController.KEY_NAME, name);
                    editor.putString(AppController.KEY_USER_TYPE, loginType);
                    editor.commit();

                    AppController.setUserType(loginType);
                    AppController.setUsername(u);
                    AppController.setPassword(p);
                    AppController.setName(name);

                    if (userType.getSelectedKey() == 2) {
                        GUIUtils.redirectToActivity(getApplicationContext(), IndustrialMeterUploadActivity.class, new Bundle());
                    } else {
                        GUIUtils.redirectToActivity(getApplicationContext(), MainActivity.class, new Bundle());
                    }
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                    finish();
                } else if ("FAIL".equals(result)) {
                    Toast.makeText(getApplicationContext(), "Either username or Password is incorrect", Toast.LENGTH_LONG).show();
                    passwordView.setError(getString(R.string.error_incorrect_password));
                    passwordView.requestFocus();
                } else {
                    Toast.makeText(getApplicationContext(), "Problem in connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG,
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }
}
