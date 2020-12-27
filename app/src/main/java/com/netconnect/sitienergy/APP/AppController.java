package com.netconnect.sitienergy.APP;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    //public static String SEL_URL = "http://10.182.0.154:8080/SELAndroid/";
    public static String SEL_URL = "http://45.114.143.56/TgmlWebService";
//    public static String SEL_URL = "http://103.233.79.76/SELAndroid/";
    //public static String SEL_URL = "http://192.168.1.89:8080/Sitienergy/";
public static String USER_IMAGE_URL = "http://45.114.143.56/tgml/document/";
    //public static String USER_IMAGE_URL = "http://10.182.0.154:8080/tgml/document/";
//    public static String USER_IMAGE_URL = "http://sitienergy.com/SEL/document/";
    //   /usr/local//tomcat7/webapps//SEL/readingDoc/
    public static final String MyPREFERENCES = "MyPrefs";

    private static AppController mInstance;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_NAME = "customer_name";
    public static final String KEY_PASS = "password";
    public static final String APP_VERSION = "appVersion";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBILE = "mobile_no";
    public static final String KEY_USER_TYPE = "user_type";
    public static String username = "";
    public static String name = "";
    public static String password = "";
    public static String email = "";
    public static String mobile = "";
    public static String userType = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // UNIVERSAL IMAGE LOADER SETUP
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AppController.password = password;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        AppController.name = name;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AppController.username = username;
    }

    public static String getMobile() {
        return mobile;
    }

    public static void setMobile(String mobile) {
        AppController.mobile = mobile;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        AppController.email = email;
    }

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String userType) {
        AppController.userType = userType;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
