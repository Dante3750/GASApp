package com.netconnect.sitienergy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netconnect.sitienergy.R;

public class StatusActivity extends Activity {
	String mStatus;
	LinearLayout success;
	LinearLayout error;
	Button ok,tryAgain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_status);
		Bundle bundle = this.getIntent().getExtras();
		mStatus = bundle.getString("status").toString();
		success =(LinearLayout)findViewById(R.id.success);
		error = (LinearLayout)findViewById(R.id.error);
		ok= (Button)findViewById(R.id.buttonOk);
		tryAgain = (Button)findViewById(R.id.try_again);
		Log.v("Status_String",mStatus);

		try {
			String str2 = "|0300|";
			boolean bb = mStatus.toLowerCase().contains(str2.toLowerCase());
			System.out.println("bb :: "
					+ mStatus.toLowerCase().contains(str2.toLowerCase()));

			if (bb) {
				error.setVisibility(View.GONE);
				success.setVisibility(View.VISIBLE);
				Log.v("Status","Success");

			} else {
				success.setVisibility(View.GONE);
				error.setVisibility(View.VISIBLE);
				Log.v("Status","Failed");
			}

			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			tryAgain.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

		} catch (Exception e2) {
			e2.printStackTrace();
			Log.v("Status","Failed");
		}
	}
}
