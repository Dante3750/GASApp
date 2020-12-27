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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IndustrialMeterUploadActivity extends AppCompatActivity {

    private static final String TAG = IndustrialMeterUploadActivity.class.getSimpleName();
    private final static int SELECT_FILE = 1;
    private final static int REQUEST_CAMERA = 2;
    private final static String UPLOAD_DATE = "upload_date";
    private ImageView img;
    private Toolbar mToolbar;
    private ProgressBar progressBar;
    private Uri mCapturedImageURI;
    private String encodedString;
    private static RequestParams params = new RequestParams();
    private boolean fileLoaded = false;
    private static IndustrialMeterUploadActivity mInstance;
    private Button btnImageUpload;
    private Button meterImageChoose;
    private SharedPreferences prefs;
    private String userChoosenTask;
    Spinner spinnerCustom;
    ArrayList<String> customerType;
    String itemTypeSelected;

    public static synchronized IndustrialMeterUploadActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrial_meter_upload);
        mInstance = this;
        prefs = getSharedPreferences(AppController.MyPREFERENCES, Context.MODE_PRIVATE);
        btnImageUpload = (Button) findViewById(R.id.btnUpload);
        meterImageChoose = (Button) findViewById(R.id.btnChoose);
        img = (ImageView) findViewById(R.id.img);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Upload Meter Image");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setIcon(R.drawable.ic_sel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar cal = Calendar.getInstance();
//        String date = df.format(cal.getTime());
//        System.out.print("abcd_Tod  "+date);
//        Log.v("abcd_Tod",date);
//        Date todayDate = null;
//        try {
//            todayDate = df.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        meterImageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileLoaded) {
                    progressBar.setVisibility(View.VISIBLE);
                    SendHttpRequestTask t = new SendHttpRequestTask();
                    String[] param = new String[]{""};
                    t.execute(param);
                } else
                    Toast.makeText(getApplicationContext(), "Fill required fields", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCustom= (Spinner) findViewById(R.id.spinnerCustom);
        // Spinner Drop down elements
        customerType = new ArrayList<String>();
        customerType.add("Commercial");
        customerType.add("Industrial");
        customerType.add("CNG");
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(IndustrialMeterUploadActivity.this,customerType);
        spinnerCustom.setAdapter(customSpinnerAdapter);
        spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                itemTypeSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(IndustrialMeterUploadActivity.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                IndustrialMeterUploadActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(IndustrialMeterUploadActivity.this);
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
        if (resultCode == RESULT_OK) {
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
        }
    }

    private void cameraIntent()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "" + ".png");
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
            Cursor cursor = getContentResolver().query(contentUri,proj, null, null, null);
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
            params.put("custType", itemTypeSelected);
            makeHTTPCall();
        }
    }

    public void makeHTTPCall() {
        Log.v("Vimal", "inside makeHTTPCALL");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20000);
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(AppController.SEL_URL+"nondom_img_upload",
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
                            else
//                                Toast.makeText(getA
// 3ctivity(), "Some problem occurred! Try again later",
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
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Internal Server Error! Try again later",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            try {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Timeout! Slow or No Internet Connection"
                                        , Toast.LENGTH_LONG)
                                        .show();
                            }catch (Exception e){}
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.chg_pass:
                redirectToDialog(new DialogResetPassword());
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void redirectToDialog(DialogFragment fragment) {
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        fragment.show(getSupportFragmentManager(), "");
    }

    public void logout() {

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AppController.KEY_NAME);
        editor.remove(AppController.KEY_PASS);
        editor.remove(AppController.KEY_USERNAME);
        editor.remove(AppController.KEY_EMAIL);
        editor.remove(AppController.KEY_MOBILE);
        editor.remove(AppController.KEY_USER_TYPE);
        editor.commit();

        AppController.setName("");
        AppController.setUsername("");
        AppController.setPassword("");
        AppController.setEmail("");
        AppController.setMobile("");
        AppController.setUserType("");
        if (MainActivity.getInstance() != null)
            MainActivity.getInstance().finish();
        if (IndustrialMeterUploadActivity.getInstance() != null)
            IndustrialMeterUploadActivity.getInstance().finish();
        GUIUtils.redirectToActivity(getApplicationContext(), LoginActivity.class, null);
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context,ArrayList<String> asr) {
            this.asr=asr;
            activity = context;
        }



        public int getCount()
        {
            return asr.size();
        }

        public Object getItem(int i)
        {
            return asr.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }



        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(IndustrialMeterUploadActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(IndustrialMeterUploadActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_dropdown, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

    }

}
