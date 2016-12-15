package com.brasco.simwechat.quickblox.core.gcm;

import com.brasco.simwechat.utils.LogUtil;
import com.google.android.gms.iid.InstanceIDListenerService;

public abstract class CoreGcmPushInstanceIDService extends InstanceIDListenerService {
    public static final String TAG = CoreGcmPushInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        GooglePlayServicesHelper playServicesHelper = new GooglePlayServicesHelper();
        LogUtil.writeDebugLog(TAG, "onTokenRefresh", "1");
        if (playServicesHelper.checkPlayServicesAvailable()) {
            LogUtil.writeDebugLog(TAG, "onTokenRefresh", "2");
            playServicesHelper.registerForGcm(getSenderId());
        }
    }

    protected abstract String getSenderId();
}
