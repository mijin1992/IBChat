package com.brasco.simwechat.quickblox.core.gcm;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.brasco.simwechat.utils.LogUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.brasco.simwechat.quickblox.core.CoreApp;
import com.brasco.simwechat.quickblox.core.utils.DeviceUtils;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.VersionUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;


import java.io.IOException;
import java.util.ArrayList;

public class GooglePlayServicesHelper {
    private static final String TAG = GooglePlayServicesHelper.class.getSimpleName();

    private static final String PREF_APP_VERSION = "appVersion";
    private static final String PREF_GCM_REG_ID = "registration_id";

    private static final int PLAY_SERVICES_REQUEST_CODE = 9000;

    public void registerForGcm(String senderId) {
        LogUtil.writeDebugLog(TAG, "registerForGcm", "1");
        String gcmRegId = getGcmRegIdFromPreferences();
        if (TextUtils.isEmpty(gcmRegId)) {
            LogUtil.writeDebugLog(TAG, "registerForGcm", "2");
            registerInGcmInBackground(senderId);
        }
    }

    public void unregisterFromGcm(String senderId) {
        LogUtil.writeDebugLog(TAG, "unregisterFromGcm", "1");
        String gcmRegId = getGcmRegIdFromPreferences();
        if (!TextUtils.isEmpty(gcmRegId)) {
            LogUtil.writeDebugLog(TAG, "unregisterFromGcm", "2");
            unregisterInGcmInBackground(senderId);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @param activity activity where you check Google Play Services availability
     */
    public boolean checkPlayServicesAvailable(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        LogUtil.writeDebugLog(TAG, "checkPlayServicesAvailable", "1");
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_REQUEST_CODE)
                        .show();
                LogUtil.writeDebugLog(TAG, "checkPlayServicesAvailable", "2");
            } else {
                Log.i(TAG, "This device is not supported.");
                LogUtil.writeDebugLog(TAG, "checkPlayServicesAvailable", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public boolean checkPlayServicesAvailable() {
        return getPlayServicesAvailabilityResultCode() == ConnectionResult.SUCCESS;
    }

    private int getPlayServicesAvailabilityResultCode() {
        LogUtil.writeDebugLog(TAG, "getPlayServicesAvailabilityResultCode", "1");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(CoreApp.getInstance());
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInGcmInBackground(String senderId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(CoreApp.getInstance());
                    LogUtil.writeDebugLog(TAG, "registerInGcmInBackground", "1");
                    return instanceID.getToken(params[0], GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    LogUtil.writeDebugLog(TAG, "registerInGcmInBackground", "error");
                    Log.w(TAG, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final String gcmRegId) {
                if (TextUtils.isEmpty(gcmRegId)) {
                    Log.w(TAG, "Device wasn't registered in GCM");
                    LogUtil.writeDebugLog(TAG, "onPostExecute", "Device wasn't registered in GCM");
                } else {
                    Log.i(TAG, "Device registered in GCM, regId=" + gcmRegId);
                    LogUtil.writeDebugLog(TAG, "onPostExecute", gcmRegId);

                    QBSubscription qbSubscription = new QBSubscription();
                    qbSubscription.setNotificationChannel(QBNotificationChannel.GCM);
                    qbSubscription.setDeviceUdid(DeviceUtils.getDeviceUid());
                    qbSubscription.setRegistrationID(gcmRegId);
                    qbSubscription.setEnvironment(QBEnvironment.DEVELOPMENT); // Don't forget to change QBEnvironment to PRODUCTION when releasing application

                    QBPushNotifications.createSubscription(qbSubscription).performAsync(
                            new QBEntityCallback<ArrayList<QBSubscription>>() {
                                @Override
                                public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {
                                    Log.i(TAG, "Successfully subscribed for QB push messages");
                                    LogUtil.writeDebugLog(TAG, "onPostExecute", "Successfully subscribed for QB push messages");
                                    saveGcmRegIdToPreferences(gcmRegId);
                                }

                                @Override
                                public void onError(QBResponseException error) {
                                    Log.w(TAG, "Unable to subscribe for QB push messages; " + error.toString());
                                    LogUtil.writeDebugLog(TAG, "onPostExecute", "Unable to subscribe for QB push messages; " + error.toString());
                                }
                            });
                }
            }
        }.execute(senderId);
    }

    private void unregisterInGcmInBackground(String senderId) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(CoreApp.getInstance());
                    instanceID.deleteToken(params[0], GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    LogUtil.writeDebugLog(TAG, "unregisterInGcmInBackground", "doInBackground");
                    return null;
                } catch (IOException e) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    Log.w(TAG, e);
                    LogUtil.writeDebugLog(TAG, "unregisterInGcmInBackground", e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void gcmRegId) {
                LogUtil.writeDebugLog(TAG, "unregisterInGcmInBackground", "onPostExecute");
                deleteGcmRegIdFromPreferences();
            }
        }.execute(senderId);
    }

    protected void saveGcmRegIdToPreferences(String gcmRegId) {
        int appVersion = VersionUtils.getAppVersion();
        Log.i(TAG, "Saving gcmRegId on app version " + appVersion);
        LogUtil.writeDebugLog(TAG, "saveGcmRegIdToPreferences", "Saving gcmRegId on app version " + appVersion);
        // We save both gcmRegId and current app version,
        // so we can check if app was updated next time we need to get gcmRegId
        SharedPrefsHelper.getInstance().save(PREF_GCM_REG_ID, gcmRegId);
        SharedPrefsHelper.getInstance().save(PREF_APP_VERSION, appVersion);
    }

    protected void deleteGcmRegIdFromPreferences() {
        LogUtil.writeDebugLog(TAG, "deleteGcmRegIdFromPreferences", "1");
        SharedPrefsHelper.getInstance().delete(PREF_GCM_REG_ID);
        SharedPrefsHelper.getInstance().delete(PREF_APP_VERSION);
    }

    protected String getGcmRegIdFromPreferences() {
        // Check if app was updated; if so, we must request new gcmRegId
        // since the existing gcmRegId is not guaranteed to work
        // with the new app version
        int registeredVersion = SharedPrefsHelper.getInstance().get(PREF_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = VersionUtils.getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            LogUtil.writeDebugLog(TAG, "getGcmRegIdFromPreferences", "App version changed.");
            return "";
        }

        return SharedPrefsHelper.getInstance().get(PREF_GCM_REG_ID, "");
    }
}