package com.netconnect.sitienergy.model;

import android.view.View;
import android.widget.TextView;

import com.netconnect.sitienergy.R;

/**
 * Created by Vimal on 11/18/2015.
 */
public class BillListViewCache {

    private View baseView;
    private TextView tvPayment;
    private TextView tvAdvAmt;
    private TextView tvPaidAmt;
    private TextView tvBalanceAmt;
    private TextView tvBillDay;
    private TextView tvBillMonth;
    private TextView tvPayMode;
    private TextView tvAggName;

    public BillListViewCache(View baseView) {
        this.baseView = baseView;
    }

    public View getBaseView() {
        return baseView;
    }

    public TextView getTvPayment() {
        if ( tvPayment == null ) {
            tvPayment = ( TextView ) baseView.findViewById(R.id.txtPaymentItem);
        }
        return tvPayment;
    }

    public TextView getTvAdvAmt() {
        if ( tvAdvAmt == null ) {
            tvAdvAmt = ( TextView ) baseView.findViewById(R.id.txtAdvanceAmount);
        }
        return tvAdvAmt;
    }

    public TextView getTvPaidAmt() {
        if ( tvPaidAmt == null ) {
            tvPaidAmt = ( TextView ) baseView.findViewById(R.id.txtPaidAmount);
        }
        return tvPaidAmt;
    }

    public TextView getTvBalanceAmt() {
        if ( tvBalanceAmt == null ) {
            tvBalanceAmt = ( TextView ) baseView.findViewById(R.id.txtBalanceAmount);
        }
        return tvBalanceAmt;
    }

    public TextView getTvBillDay() {
        if ( tvBillDay == null ) {
            tvBillDay = ( TextView ) baseView.findViewById(R.id.txtbillDate);
        }
        return tvBillDay;
    }

    public TextView getTvBillMonth() {
        if ( tvBillMonth == null ) {
            tvBillMonth = ( TextView ) baseView.findViewById(R.id.txtMonth);
        }
        return tvBillMonth;
    }

    public TextView getTvPayMode() {
        if ( tvPayMode == null ) {
            tvPayMode = ( TextView ) baseView.findViewById(R.id.txtPayMode);
        }
        return tvPayMode;
    }

    public TextView getTvAggName() {
        if ( tvAggName == null ) {
            tvAggName = ( TextView ) baseView.findViewById(R.id.txtAggName);
        }
        return tvAggName;
    }

}
