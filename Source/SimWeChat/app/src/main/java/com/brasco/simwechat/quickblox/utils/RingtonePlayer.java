package com.brasco.simwechat.quickblox.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.brasco.simwechat.utils.LogUtil;

/**
 * Created by Administrator on 11/29/2016.
 */

public class RingtonePlayer {
    private static final String TAG = RingtonePlayer.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private Context context;

    public RingtonePlayer(Context context, int resource){
        this.context = context;
        mediaPlayer = android.media.MediaPlayer.create(context, resource);
        LogUtil.writeDebugLog(TAG, "RingtonePlayer", "1");
    }

    public RingtonePlayer(Context context){
        this.context = context;
        Uri notification = getNotification();
        if (notification != null) {
            mediaPlayer = android.media.MediaPlayer.create(context, notification);
        }
    }

    private Uri getNotification() {
        LogUtil.writeDebugLog(TAG, "getNotification", "1");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if (notification == null) {
            // notification is null, using backup
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (notification == null) {
                // notification backup is null, using 2nd backup
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }
        return notification;
    }

    public void play(boolean looping) {
        Log.i(TAG, "play");
        LogUtil.writeDebugLog(TAG, "play", "1");
        if (mediaPlayer == null) {
            Log.i(TAG, "mediaPlayer isn't created ");
            LogUtil.writeDebugLog(TAG, "play", "mediaPlayer isn't created");
            return;
        }
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        //mediaPlayer.setVolume(100, 100);
        mediaPlayer.setLooping(looping);
        mediaPlayer.start();
    }

    public synchronized void stop() {
        LogUtil.writeDebugLog(TAG, "stop", "1");
        if (mediaPlayer != null) {
            try {
                LogUtil.writeDebugLog(TAG, "stop", "2");
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                LogUtil.writeDebugLog(TAG, "stop", "3");
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
