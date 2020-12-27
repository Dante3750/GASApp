package com.netconnect.sitienergy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.activity.MainActivity;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BillHistoryAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> billList = new ArrayList<HashMap<String, String>>();
        Context context;
        private OnImgClickListener onImgClickListener;
        int[] imageId;
        private static LayoutInflater inflater = null;

        public BillHistoryAdapter(Context context, ArrayList<HashMap<String, String>> billList) {
            this.context = context;
            this.billList = billList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return billList.size();
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

        public class ViewHolder {
            TextView tvBill;
            TextView tvBillAmt;
            TextView tvBillDay;
            TextView tvBillMonth;
            TextView imgDownload;
            TextView tvAmtPayable;
            TextView tvInvoice;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder = new ViewHolder();
            if (v == null) {
                v = inflater.inflate(R.layout.row_bill_history, null);
                holder.tvBill = (TextView) v.findViewById(R.id.txtbillItem);
                holder.tvBillAmt = (TextView) v.findViewById(R.id.txtbillAmount);
                holder.tvAmtPayable = (TextView) v.findViewById(R.id.txtAmtPayable);
                holder.tvInvoice = (TextView) v.findViewById(R.id.txtInvoice);
                holder.imgDownload = (TextView) v.findViewById(R.id.imgdownload);
                holder.tvBillDay = (TextView) v.findViewById(R.id.txtbillDate);
                holder.tvBillMonth = (TextView) v.findViewById(R.id.txtMonth);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            DateFormat inputDF = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = null;

            final String billDate= billList.get(position).get("bill_date");
            final String totalAmountPayable = billList.get(position).get("bill_payable");
            final String invoiceAmount = billList.get(position).get("bill_amount");
            final String invoice= billList.get(position).get("invoice");
            final String billID= billList.get(position).get("bill_id");
            try {
                date1 = inputDF.parse(billDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();

            Format formatter = new SimpleDateFormat("MMM");
            String month = formatter.format(date1);
            cal.setTime(date1);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");

            holder.tvBillDay.setText(day + "");
            holder.tvBillMonth.setText(month);
            holder.tvBill.setText(format2.format(date1));
            holder.tvBillAmt.setText("Current Bill period charge: ₹ " + invoiceAmount);
            holder.tvAmtPayable.setText("Amount Payable: ₹ " + totalAmountPayable);
            holder.tvInvoice.setText("Invoice No.: " + invoice);
            final String id =  billList.get(position).get("bill_id");

            holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onImgClickListener.imgClicked("http://103.233.79.76/tgml/billing_data?id="+id,billID);
                    
                }
            });
            return v;
        }

    public interface OnImgClickListener {
        void imgClicked(String url, String fileName);
    }

    public void setOnImgClickListener(OnImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
    }

}
