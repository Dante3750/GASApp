package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.adapter.PaymentHistoryAdapter;
import com.netconnect.sitienergy.model.BillItem;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private JSONParser jParser = new JSONParser();
    private String url = null;
    private ListView billListView;
    private LinearLayout historyNotFound;
    private PaymentHistoryAdapter adapter;
    List<BillItem> arrayList = new ArrayList<BillItem>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private static PaymentHistoryFragment mInstance;

    public static synchronized PaymentHistoryFragment getInstance() {
        return mInstance;
    }

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInstance = this;
        GUIUtils.getTransition(this);
        View rootView = inflater.inflate(R.layout.fragment_bill_history, container, false);

        billListView = (ListView) rootView.findViewById(R.id.billListView);
        historyNotFound = (LinearLayout) rootView.findViewById(R.id.not_found);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        Uri.Builder uriBuilder = Uri.parse("http://45.114.143.56/tgml/" + "app_payment_history").buildUpon()
                .appendQueryParameter("customer_arn_no", AppController.getUsername());
        url = uriBuilder.build().toString();
        Log.v("Payment History", url);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        arrayList.clear();
                                        swipeRefreshLayout.setRefreshing(true);
                                        loadUrl();
                                    }
                                }
        );
        return rootView;
    }

    public void loadUrl() {
        try {
            if (jParser.isConnectingToInternet(getActivity().getApplicationContext())) {
                new JSONParse().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity().getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){

        }
    }

    @Override
    public void onRefresh() {
        arrayList.clear();
        loadUrl();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                JSONArray jsonarray = new JSONArray(jsonString);

                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String aggName  = jsonobject.optString("all_or_name", "");
                    String billDate = jsonobject.optString("pay_date", "");
                    String paidAmt = jsonobject.optString("paid_amt", "");
                    String remainAmt = jsonobject.optString("remain_amt", "");
                    String advAmt = jsonobject.optString("adv_amt", "");
                    String payMode = jsonobject.optString("pay_mode", "");

                    arrayList.add(new BillItem(aggName, billDate, paidAmt,remainAmt,advAmt,payMode));
                }
                billListView.setAdapter(null);
                adapter = new PaymentHistoryAdapter(getActivity(), R.layout.row_payment_history, arrayList);

                billListView.setAdapter(adapter);

                if (arrayList.size() == 0) {
                    historyNotFound.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                historyNotFound.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }
    }
}
