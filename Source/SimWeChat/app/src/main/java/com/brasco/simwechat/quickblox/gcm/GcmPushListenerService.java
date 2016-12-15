package com.brasco.simwechat.quickblox.gcm;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.core.gcm.CoreGcmPushListenerService;
import com.brasco.simwechat.quickblox.core.utils.NotificationUtils;
import com.brasco.simwechat.quickblox.core.utils.ResourceUtils;
import com.brasco.simwechat.quickblox.core.utils.constant.GcmConsts;

public class GcmPushListenerService extends CoreGcmPushListenerService {
    public static final String TAG = GcmPushListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void showNotification(String message) {
//        if (message != null && message.contains(Constant.SEND_VIDEO_VIEW_REQUEST)) {
//
//        } else if (message != null && message.contains(Constant.SEND_VIDEO_SHOW_START)){
//
//        } else if (message != null && message.contains(Constant.SEND_VIDEO_SHOW_END)){
//
//        } else
        {
            LogUtil.writeDebugLog(TAG, "showNotification", message);
            NotificationUtils.showNotification(this, MainActivity.class,
                    ResourceUtils.getString(R.string.app_name), message,
                    R.drawable.ic_launcher, NOTIFICATION_ID);
        }
    }

    @Override
    protected void sendPushMessageBroadcast(String message) {
        LogUtil.writeDebugLog(TAG, "sendPushMessageBroadcast", message);
        Intent gcmBroadcastIntent = new Intent(GcmConsts.ACTION_NEW_GCM_EVENT);
        gcmBroadcastIntent.putExtra(GcmConsts.EXTRA_GCM_MESSAGE, message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(gcmBroadcastIntent);
    }
}