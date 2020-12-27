package com.netconnect.sitienergy.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.netconnect.sitienergy.APP.AppController;
import com.netconnect.sitienergy.R;
import com.netconnect.sitienergy.utils.GUIUtils;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private static MainActivity mInstance;
    private AlertDialog dialog;
    private Fragment mContent;
    Fragment currentFragment;
    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(AppController.MyPREFERENCES, Context.MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        Bundle b = getIntent().getExtras();
        String view = b.getString("FLAG", "");
        if (view.equals("TRUE")) {
            displayView(1);
        } else {
            displayView(0);
        }
      

//        val plainText = "this is my plain text"
//        val key = "your key"
//
//        val cryptLib = CryptLib()
//
//        val cipherText = cryptLib.encryptPlainTextWithRandomIV(plainText, key)
//        println("cipherText $cipherText")
//
//        val decryptedString = cryptLib.decryptCipherTextWithRandomIV(cipherText, key)
//        println("decryptedString $decryptedString")
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() > 0)
            manager.popBackStack();
        else {
            if(currentFragment instanceof HomeFragment) {
                try {
                    exitApp();
                }catch (Exception e){}
            }
            else
            displayView(0);
        }

    }

    public void exitApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Torrent GAS");
        // ic_dialog_alert
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("dataGotFromServerProfile");
                editor.commit();
                finish();
            }

        }).setNegativeButton("No", null);

        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment =new HomeFragment();
                title = getString(R.string.title_Profile);
                currentFragment =fragment;
                break;
            case 1:
                fragment =new BillHistoryFragment();
                title = getString(R.string.title_bill_history);
                currentFragment =fragment;
                break;
            case 2:
                fragment =new PaymentHistoryFragment();
                title = getString(R.string.title_payment_history);
                currentFragment =fragment;
                break;
            case 3:
                fragment =new ViewMeterReadingFrament();
                title = getString(R.string.title_view_meter_reading);
                currentFragment =fragment;
                break;
            case 4:
                fragment =new BillPaymentFragment();
                title = getString(R.string.title_bill_payment);
                currentFragment =fragment;
                //startNewActivity(getApplicationContext(),getString(R.string.package_name));
                break;
            case 5:
                fragment =new MeterUploadFragment();
                title = getString(R.string.title_upload_meter);
                currentFragment =fragment;
                break;
            case 6:
                fragment =new ContactUs();
                title = getString(R.string.title_contact_us);
                currentFragment =fragment;
                break;
            case 7:
                logout();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
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
        GUIUtils.redirectToActivity(getApplicationContext(), LoginActivity.class, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("dataGotFromServerProfile");
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("dataGotFromServerProfile");
        editor.commit();
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
