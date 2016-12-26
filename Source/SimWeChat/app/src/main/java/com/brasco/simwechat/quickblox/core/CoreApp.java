package com.brasco.simwechat.quickblox.core;

import android.app.Application;

import com.quickblox.core.QBSettings;

public class CoreApp extends Application {
    public static final String TAG = CoreApp.class.getSimpleName();
    private static CoreApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized CoreApp getInstance() {
        return instance;
    }

    public void initCredentials(String APP_ID, String AUTH_KEY, String AUTH_SECRET, String ACCOUNT_KEY) {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().fastConfigInit("51013", "F62UjPafDSCmB-U", "aru4JB5XzZARfAz");
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}