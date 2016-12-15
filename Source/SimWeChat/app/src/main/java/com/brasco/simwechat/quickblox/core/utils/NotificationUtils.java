package com.brasco.simwechat.quickblox.core.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.quickblox.core.utils.constant.GcmConsts;

public class NotificationUtils {
    public static AppPreference mPrefs;
    public static final String TAG = NotificationUtils.class.getSimpleName();
    public static void showNotification(Context context, Class<? extends Activity> activityClass,
                                        String title, String message, @DrawableRes int icon,
                                        int notificationId) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(GcmConsts.EXTRA_GCM_MESSAGE, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mPrefs = new AppPreference(context);
        if(mPrefs.getNotifications()) {

            Uri defaultSoundUri = null;
            if (mPrefs.getNotificationRing()) {
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId, notificationBuilder.build());

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (mPrefs.getNotificationVibrate()) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500);
                }
            }
        }
    }

}
