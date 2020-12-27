package com.netconnect.sitienergy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.model.BillItem;
import com.netconnect.sitienergy.model.BillListViewCache;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PaymentHistoryAdapter extends ArrayAdapter {
    private List<BillItem> paymentList = new ArrayList<BillItem>();
    private Context context;
    private int resource;
    private static LayoutInflater inflater = null;

    public PaymentHistoryAdapter(Context context,int resourceId,List<BillItem> paymentList) {
        super(context,resourceId, paymentList);
        this.context = context;
        this.resource = resourceId;
        this.paymentList = paymentList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return paymentList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        BillItem item = paymentList.get(position);
        BillListViewCache viewCache;
        if (v == null) {
            v= inflater.inflate(resource, null);
            viewCache = new BillListViewCache( v );
            v.setTag( viewCache );
        } else {
            viewCache = (BillListViewCache) v.getTag();
        }

        TextView tvPaymentDate=viewCache.getTvPayment();
        TextView tvAdvAmt=viewCache.getTvAdvAmt();
        TextView tvPaidAmt=viewCache.getTvPaidAmt();
        TextView tvBalanceAmt=viewCache.getTvBalanceAmt();
        TextView tvBillDay=viewCache.getTvBillDay();
        TextView tvBillMonth=viewCache.getTvBillMonth();
        TextView tvPayMode=viewCache.getTvPayMode();
        TextView tvAggName=viewCache.getTvAggName();

        DateFormat inputDF  = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = inputDF.parse(item.getBillDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);

        Format formatter = new SimpleDateFormat("MMM");
        String month = formatter.format(date1);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");

        tvBillDay.setText(day+"");
        tvBillMonth.setText(month);
        tvPaymentDate.setText(format2.format(date1));
        tvAdvAmt.setText("Advance Amount: ₹ " + item.getAdvAmt());
        tvPaidAmt.setText("Paid Amount: ₹ " + item.getPaidAmt());
        tvBalanceAmt.setText("Remaining Amount: ₹ "+ item.getRemainAmt());
        tvPayMode.setText("Payment Mode: " + item.getPayMode());
        tvAggName.setText("Aggregator's Name: " + item.getAggName());
        return v;
    }
}