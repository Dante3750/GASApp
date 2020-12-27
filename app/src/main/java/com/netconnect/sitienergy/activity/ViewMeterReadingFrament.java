package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.adapter.ViewMeterReadingAdapter;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewMeterReadingFrament extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ViewMeterReadingAdapter.OnImgClickListener {

    private static final String TAG = BillHistoryFragment.class.getSimpleName();
    private JSONParser jParser = new JSONParser();
    private String url = null;
    private ListView readingListView;
    private LinearLayout historyNotFound;
    private ViewMeterReadingAdapter adapter;
    private ImageZoomFragment fragment;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private static ViewMeterReadingFrament mInstance;

    public static synchronized ViewMeterReadingFrament getInstance() {
        return mInstance;
    }

    public ViewMeterReadingFrament() {
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
        View rootView = inflater.inflate(R.layout.fragment_view_readings, container, false);

        readingListView = (ListView) rootView.findViewById(R.id.readingListView);
        historyNotFound = (LinearLayout) rootView.findViewById(R.id.not_found);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        Uri.Builder uriBuilder = Uri.parse(AppController.SEL_URL + "view_meter_reading").buildUpon()
                .appendQueryParameter("custID", EncryptionUtil.encode(AppController.getUsername()))
                .appendQueryParameter("type","android");
        url = uriBuilder.build().toString();
        Log.v("viewMeterReading", url);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    arrayList.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    loadUrl();
                } catch (Exception e) {
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
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity().getApplicationContext(), "Internet connection not available", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){}
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

    @Override
    public void onRefresh() {

    }

    @Override
    public void imgClicked(String encodedString, ImageView img) {
        Bundle args = new Bundle();
        args.putString("image_string", encodedString);
        fragment = new ImageZoomFragment();
        fragment.setArguments(args);
        FragmentTransaction ft=getFragmentManager().beginTransaction().addSharedElement(img, "profile");
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        fragment.show(ft, "");
    }

    private class JSONParse extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
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
                JSONObject json = jsonarray.getJSONObject(0);
                String result = json.optString("RESULT", "");
                if ("FAIL".equals(result)) {
                    historyNotFound.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < jsonarray.length(); i++) {

                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                      
                        String adminApproval = jsonobject.optString("admin_approval", "");
                        String reading = jsonobject.optString("reading", "");
                        String uploadReadingDate = jsonobject.optString("upload_reading_date", "");
                        String meterImage = jsonobject.optString("meter_image", "");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("admin_approval", adminApproval);
                        map.put("reading", reading);
                        map.put("upload_reading_date", uploadReadingDate);
                        map.put("meter_image", meterImage);

                        arrayList.add(map);
                    }

                    readingListView.setAdapter(null);
                    adapter = new ViewMeterReadingAdapter(getActivity(), arrayList);
                    readingListView.setAdapter(adapter);
                    adapter.setOnImgClickListener(getInstance());
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                e.printStackTrace();
            }
        }
    }
}
