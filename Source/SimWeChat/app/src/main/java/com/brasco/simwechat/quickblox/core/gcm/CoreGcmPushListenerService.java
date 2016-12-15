package com.brasco.simwechat.quickblox.core.gcm;;

import android.os.Bundle;
import android.util.Log;

import com.brasco.simwechat.utils.LogUtil;
import com.google.android.gms.gcm.GcmListenerService;
import com.brasco.simwechat.quickblox.core.utils.ActivityLifecycle;
import com.brasco.simwechat.quickblox.core.utils.constant.GcmConsts;

public abstract class CoreGcmPushListenerService extends GcmListenerService {
    private static final String TAG = CoreGcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString(GcmConsts.EXTRA_GCM_MESSAGE);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);

        LogUtil.writeDebugLog(TAG, "onMessageReceived", "From: " + from + ", Message: " + message);
        if (ActivityLifecycle.getInstance().isBackground()) {
            showNotification(message);
        }

        sendPushMessageBroadcast(message);
    }

    protected abstract void showNotification(String message);

    protected abstract void sendPushMessageBroadcast(String message);
}