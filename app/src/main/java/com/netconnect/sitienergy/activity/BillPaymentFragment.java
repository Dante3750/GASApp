package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.billdesk.sdk.PaymentOptions;
import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.adapter.BillHistoryAdapter;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class BillPaymentFragment extends Fragment {

//    String strMsg = "AIRMTST|ARP1459915746514|NA|2.00|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|http://122.169.118.65/billdesk/pg_resp.php|1640343085";// pg_msg
//
//    String strEmail = "abc@def.ghi";
//    String strMobile = "9800000000";
    private static final String TAG = BillPaymentFragment.class.getSimpleName();
    private JSONParser jParser = new JSONParser();
    private EditText customerARN;
    private EditText mobileNo;
    private EditText emailID;
    private EditText paymentAmount;
    private String url = null;
    private Button btnPayNow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GUIUtils.getTransition(this);
        View rootView = inflater.inflate(R.layout.fragment_bill_payment, container, false);
        customerARN = (EditText) rootView.findViewById(R.id.arn_no);
        mobileNo = (EditText) rootView.findViewById(R.id.mobile);
        emailID = (EditText) rootView.findViewById(R.id.email);
        paymentAmount = (EditText) rootView.findViewById(R.id.bill_pay_amt);
        btnPayNow = (Button) rootView.findViewById(R.id.btnPayNow);

        customerARN.setText(AppController.getUsername());
        emailID.setText(AppController.getEmail());
        mobileNo.setText(AppController.getMobile());

        btnPayNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((emailID.getText().toString().length()!=0) && !(TextUtils.isEmpty(paymentAmount.getText().toString()))) {
                    Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "get_check_sum").buildUpon()
                            .appendQueryParameter("custID", EncryptionUtil.encode(AppController.getUsername()))
                            .appendQueryParameter("cust_email_id", EncryptionUtil.encode(emailID.getText().toString()))
                            .appendQueryParameter("bill_pay_amt", EncryptionUtil.encode(paymentAmount.getText().toString()))
                            .appendQueryParameter("type","android");
                    url = uriBuilder.build().toString();
                    loadUrl();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill required fields first", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    public void loadUrl() {
        try {
            if (jParser.isConnectingToInternet(getActivity().getApplicationContext())) {
                new JSONParse().execute();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class JSONParse extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

            final Handler h = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Internet connection problem", Toast.LENGTH_LONG).show();
                    }
                }
            };
            h.sendMessageDelayed(new Message(), 30000);
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
                JSONObject jsonobject = new JSONObject(jsonString);
                String result = jsonobject.optString("RESULT", "");
                String checkSumValue = jsonobject.optString("check_sum_value", "");

                if ("SUCCESS".equals(result) && !(TextUtils.isEmpty(checkSumValue))) {
                    SampleCallBack callbackObj = new SampleCallBack(); // callback instance
                    System.out.println("msg:- " + checkSumValue);

                    Intent intent = new Intent(getActivity(),
                            PaymentOptions.class);

                    intent.putExtra("msg", checkSumValue); // pg_msg
                    Log.v(TAG,checkSumValue);
   //				intent.putExtra("token", strToken);
                    intent.putExtra("user-email", emailID.getText().toString());
                    intent.putExtra("user-mobile", mobileNo.getText().toString());
                    intent.putExtra("callback", callbackObj);
                    intent.putExtra("orientation", Configuration.ORIENTATION_PORTRAIT);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
