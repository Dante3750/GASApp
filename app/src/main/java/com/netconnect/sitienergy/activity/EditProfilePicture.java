package com.netconnect.sitienergy.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.EncryptionUtil;
import com.netconnect.sitienergy.utils.GUIUtils;
import com.netconnect.sitienergy.utils.JSONParser;
import com.netconnect.sitienergy.utils.NCUtils;
import com.netconnect.sitienergy.utils.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class EditProfilePicture extends DialogFragment {

    private static final String TAG = EditProfilePicture.class.getSimpleName();
    private ImageView img;
    private final static int SELECT_FILE = 1;
    private final static int REQUEST_CAMERA = 2;
    private LinearLayout changePic;
    private ImageView btnCancel;
    private Button btnOK;
    private String userChoosenTask;
    private ProgressBar progressBar;
    private Uri mCapturedImageURI;
    private String encodedString;
    private static RequestParams params = new RequestParams();
    private boolean fileLoaded = false;
    private JSONParser jParser = new JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit_picture, container, false);

        GUIUtils.getTransition(this);
        img = (ImageView) rootView.findViewById(R.id.profile_img);
        changePic = (LinearLayout) rootView.findViewById(R.id.chg_photo);
        btnCancel = (ImageView) rootView.findViewById(R.id.btnCancel);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        btnOK = (Button) rootView.findViewById(R.id.btnOk);

        Bundle mArgs = getArguments();
        String imgString=mArgs.getString("image_string");
        img.setImageBitmap(NCUtils.StringToBitMap(imgString));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jParser.isConnectingToInternet(getActivity().getApplicationContext())) {
                    if (fileLoaded) {
                        progressBar.setVisibility(View.VISIBLE);
                        SendHttpRequestTask t = new SendHttpRequestTask();
                        String[] param = new String[]{""};
                        t.execute(param);
                    }else{
                        Toast.makeText(getActivity(),
                                "Change photo for updation",
                                Toast.LENGTH_LONG).show();
                    }
            }}
        });

        return rootView;
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
            android.util.Log.d ( TAG , "onPostExecute: "+encodedString );
            makeHTTPCall();
        }
    }


    public void makeHTTPCall() {
        Log.v("Vimal", "inside makeHTTPCALL");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20000);
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(AppController.SEL_URL+"upload_profile_image",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        progressBar.setVisibility(View.GONE);
                        android.util.Log.d ( TAG , "onSuccess: " + com.netconnect.sitienergy.APP.AppController.SEL_URL+"upload_profile_image"+params );


                        try {
                            JSONObject json = new JSONObject(response);
                            String result = json.optString("RESULT", "");
                            if("SUCCESS".equals(result)) {
                                //Toast.makeText(getActivity(), "Meter uploaded successfully",
                                //       Toast.LENGTH_LONG).show();
                                showAlert("Profile Photo Updated");
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

    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        MainActivity.getInstance().displayView(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
