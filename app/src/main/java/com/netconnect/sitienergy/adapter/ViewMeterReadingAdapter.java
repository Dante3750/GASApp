package com.netconnect.sitienergy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.NCUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ViewMeterReadingAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> readingList = new ArrayList<HashMap<String, String>>();
    Context context;
    private OnImgClickListener onImgClickListener;
    int[] imageId;
    private static LayoutInflater inflater = null;

    public ViewMeterReadingAdapter(Context context, ArrayList<HashMap<String, String>> readingList) {
        this.context = context;
        this.readingList = readingList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return readingList.size();
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
        TextView tvUploadDate;
        TextView tvReading;
        TextView tvStatus;
        ImageView imgMeter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = new ViewHolder();
        if (v == null) {
            v = inflater.inflate(R.layout.row_view_meter_reading, null);
            holder.tvUploadDate = (TextView) v.findViewById(R.id.txtUploadDate);
            holder.tvReading = (TextView) v.findViewById(R.id.txtReading);
            holder.tvStatus = (TextView) v.findViewById(R.id.txtStatus);
            holder.imgMeter = (ImageView) v.findViewById(R.id.meter_image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        final String uploadReadingDate= readingList.get(position).get("upload_reading_date");
        if(!TextUtils.isEmpty(uploadReadingDate) || uploadReadingDate != null) {
            try {
                date = inputDF.parse(uploadReadingDate);
                SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy");
                holder.tvUploadDate.setText("Uploaded on "+format2.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.tvReading.setText("Reading: " + readingList.get(position).get("reading"));
        final String id = readingList.get(position).get("bill_id");

        if (readingList.get(position).get("admin_approval").equals("0")) {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF1060D"));
        }else{
            holder.tvStatus.setText("Approved");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FF3E8622"));
        }
        final String meterString = readingList.get(position).get("meter_image");
        Bitmap bm =  NCUtils.StringToBitMap(meterString);
        if(TextUtils.isEmpty(meterString) || meterString == null || meterString.length()==0){
            holder.imgMeter.setBackgroundResource(R.drawable.img_not_available);
        }else{
            holder.imgMeter.setImageBitmap(bm);
        }

        final ImageView img = holder.imgMeter;
        holder.imgMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(meterString)){
                    onImgClickListener.imgClicked(meterString,img);
                }
            }
        });
        return v;
    }

    public interface OnImgClickListener {
        public void imgClicked(String encodedString,ImageView imageView);
    }

    public void setOnImgClickListener(OnImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
    }
}
