package com.netconnect.sitienergy.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeterUploadFragment extends Fragment {

    private static final String TAG = MeterUploadFragment.class.getSimpleName();
    private final static int SELECT_FILE = 1;
    private final static int REQUEST_CAMERA = 2;
    private final static String UPLOAD_DATE = "upload_date";
    private ImageView img;
    private ProgressBar progressBar;
    private Uri mCapturedImageURI;
    private String encodedString;
    private static RequestParams params = new RequestParams();
    private boolean fileLoaded = false;
    private static MeterUploadFragment mInstance;
    private Button btnImageUpload;
    private Button meterImageChoose;
    private SharedPreferences sharedpreferences;
    private EditText meterReading;
    private String userChoosenTask;
    //private RelativeLayout layout;

    public static synchronized MeterUploadFragment getInstance() {
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInstance = this;
        GUIUtils.getTransition(this);
        View rootView = inflater.inflate(R.layout.fragment_meter_upload, container, false);
        sharedpreferences = getActivity().getSharedPreferences(AppController.MyPREFERENCES, Context.MODE_PRIVATE);
        //layout = (RelativeLayout) rootView.findViewById(R.id.layout);
        btnImageUpload = (Button) rootView.findViewById(R.id.btnUpload);
        meterImageChoose = (Button) rootView.findViewById(R.id.btnChoose);
        meterReading = (EditText) rootView.findViewById(R.id.edtMeterReading);
        img = (ImageView) rootView.findViewById(R.id.img);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String date = df.format(cal.getTime());
        System.out.print("abcd_Tod  "+date);
        Log.v("abcd_Tod",date);
        Date todayDate = null;
        try {
            todayDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

/*        String testDate = sharedpreferences.getString(UPLOAD_DATE, "");
        System.out.print("abcd_test  "+testDate);
        Log.v("abcd_test", testDate +"  hello  ");
        Date endDate = null;
        if (TextUtils.isEmpty(testDate)) {
            testDate = df.format(new Date());
        }
        try {
            endDate = df.parse(testDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("abcd_test", endDate +"  hello  ");
        if (todayDate.before(endDate)) {
            showAlert();
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            layout.setVisibility(View.VISIBLE);
        }
        else{*/
            meterImageChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            btnImageUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String reading = meterReading.getText().toString();
                    if (fileLoaded && !TextUtils.isEmpty(reading)) {
                        progressBar.setVisibility(View.VISIBLE);
                        SendHttpRequestTask t = new SendHttpRequestTask();
                        String[] param = new String[]{""};
                        t.execute(param);
                    } else
                        Toast.makeText(getActivity(), "Fill required fields", Toast.LENGTH_SHORT).show();
                }
            });

        return rootView;
    }

    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.getInstance().displayView(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertOnReading(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        meterReading.setText("");
                        meterReading.setFocusableInTouchMode(true);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isWithinRange(Date testDate, Date endDate) {
        return (testDate.before(testDate) && testDate.after(endDate));
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)

                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        String selectedImagePath = "";
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                selectedImagePath = getRealPathFromURI(mCapturedImageURI);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);

                img.setImageBitmap(bm);

                fileLoaded = true;
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getRealPathFromURI(selectedImageUri);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                img.setImageBitmap(bm);

                fileLoaded = true;
            }
            if (fileLoaded) {
                img.setVisibility(View.VISIBLE);
            }
            params.put("filename", AppController.getUsername() + "_" + System.currentTimeMillis());
        }
    }

    private void cameraIntent()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "" + ".png");
        mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intentPicture, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }

     public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(contentUri,proj, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            int columnIndex = cursor.getColumnIndex(proj[0]);
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    public void getBitmapFromImageView() {
        Bitmap b = ((BitmapDrawable) img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byte_arr = baos.toByteArray();
        // Encode Image to String
        encodedString = Base64.encodeToString(byte_arr, 0);
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
                getBitmapFromImageView();
            return "";
        }

        @Override
        protected void onPostExecute(String data) {
            params.put("image", encodedString);
            params.put("custID", AppController.getUsername());
            params.put("meterReading", meterReading.getText().toString());
            android.util.Log.d ( TAG , "onPostExecute: "+ params );
            makeHTTPCall();
        }
    }

    public void makeHTTPCall() {
        Log.v("Vimal", "inside makeHTTPCALL");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20000);
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(AppController.SEL_URL+"upload_image",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject json = new JSONObject(response);
                            String result = json.optString("RESULT", "");
                            if("SUCCESS".equals(result)) {
                                //Toast.makeText(getActivity(), "Meter uploaded successfully",
                                 //       Toast.LENGTH_LONG).show();
                                showAlert("Meter Reading has been successfully uploaded");
                            }
                            else if("MAX_LIMIT_REACHED".equals(result)){
//                                Toast.makeText(getActivity(), "Meter image size exceeds limit 1MB",
//                                        Toast.LENGTH_LONG).show();
                                showAlert("Meter image size exceeds limit 1MB");
                            }
                            else if("ALREADY_DONE".equals(result)){
//                                Toast.makeText(getActivity(), "You already uploaded meter reading",
//                                        Toast.LENGTH_LONG).show();
                                showAlert("You have already uploaded the meter reading");
                            }
                            else if("LESSER READING".equals(result)){
//                                Toast.makeText(getActivity(), "You already uploaded meter reading",
//                                        Toast.LENGTH_LONG).show();
                                showAlertOnReading("New meter reading cannot be lesser than previous reading");
                            }
                            else
//                                Toast.makeText(getActivity(), "Some problem occurred! Try again later",
//                                        Toast.LENGTH_LONG).show();
                                showAlert("Some problem occurred! Try again later");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        progressBar.setVisibility(View.GONE);
                        // When Http response code is '404'

//                        Toast.makeText(getActivity(), statusCode  + "::"+error+"::"+content,
//                                Toast.LENGTH_LONG).show();
                        if (statusCode == 404) {
                            Toast.makeText(getActivity(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getActivity(),
                                    "Internal Server Error! Try again later",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            try {
                                Toast.makeText(
                                        getActivity(),
                                        "Timeout! Slow or No Internet Connection"
                                        , Toast.LENGTH_LONG)
                                        .show();
                            }catch (Exception e){}
                        }
                    }
                });
    }

    public void meterUploadTiming() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DATE, 60);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String output = sdf1.format(c.getTime());

        System.out.print("abcd  "+output);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(UPLOAD_DATE, output);
        editor.commit();
    }

}
