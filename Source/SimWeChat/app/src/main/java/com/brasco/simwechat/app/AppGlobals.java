package com.brasco.simwechat.app;

/**
 * Created by Administrator on 12/14/2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.model.UserData;

import java.util.ArrayList;

public class AppGlobals {
    public static final String TAG = "AppGlobals";

    public static AppGlobals instance = null;
    public static UserData curChattingUser= null;

    public static MainActivity mainActivity;

    public static ArrayList<UserData> mAllUserData = new ArrayList<UserData>();
    public static ArrayList<RecentMessageData> mRecentessageArray = new ArrayList<RecentMessageData>();

    /*
     * Screen size
     */
    public static float SCREEN_WIDTH = 480;
    public static float SCREEN_HEIGHT = 800;

    /*
     * Camera Resolution
     */
    public static ArrayList<String> QUALITY_LABELS = new ArrayList<String>();
    public static ArrayList<Integer> QUALITY_LEVELS = new ArrayList<Integer>();
    public static ArrayList<String> RESOLUTION_LABELS = new ArrayList<String>();
    public static ArrayList<String> RESOLUTION_SIZES = new ArrayList<String>();

    @SuppressLint("InlinedApi")
    public static void init() {
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_2160P)) {
            QUALITY_LABELS.add("HQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_2160P);
            RESOLUTION_LABELS.add("2160p");
            RESOLUTION_SIZES.add("3840 x 2160");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
            QUALITY_LABELS.add("HQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_1080P);
            RESOLUTION_LABELS.add("1080p");
            RESOLUTION_SIZES.add("1920 x 1080");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            QUALITY_LABELS.add("HQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_720P);
            RESOLUTION_LABELS.add("720p");
            RESOLUTION_SIZES.add("1280 x 720");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            QUALITY_LABELS.add("MQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_480P);
            RESOLUTION_LABELS.add("480p");
            RESOLUTION_SIZES.add("720 x 480");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF)) {
            QUALITY_LABELS.add("LQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_CIF);
            RESOLUTION_LABELS.add("288p");
            RESOLUTION_SIZES.add("352 x 288");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
            QUALITY_LABELS.add("LQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_QVGA);
            RESOLUTION_LABELS.add("240p");
            RESOLUTION_SIZES.add("320 x 240");
        }
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QCIF)) {
            QUALITY_LABELS.add("LQ");
            QUALITY_LEVELS.add(CamcorderProfile.QUALITY_QCIF);
            RESOLUTION_LABELS.add("144p");
            RESOLUTION_SIZES.add("176 x 144");
        }
    }

    /*
     * Ringtone array
     */
    static String [] gRingToneNameArr;
    public static String[] getRingtoneList(Context context) {
        if (gRingToneNameArr != null)
            return gRingToneNameArr;

        RingtoneManager ringtoneMgr = new RingtoneManager(context);

        ringtoneMgr.setType(RingtoneManager.TYPE_NOTIFICATION);

        ArrayList<String> ringtoneNameList = new ArrayList<String>();

        Cursor cursor = ringtoneMgr.getCursor();
        while (cursor.moveToNext()) {
            ringtoneNameList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
        }

        gRingToneNameArr = new String[ringtoneNameList.size()];
        gRingToneNameArr = ringtoneNameList.toArray(gRingToneNameArr);

        return gRingToneNameArr;
    }

    private static Ringtone ringtone;
    public static void playNotificationRingtone(Context context, int index) {
        stopNotificationRingtone();

        RingtoneManager ringtoneMgr = new RingtoneManager(context);
        ringtoneMgr.getCursor();
        Uri ringtoneUri = ringtoneMgr.getRingtoneUri(index);
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        if (ringtone != null && ringtoneUri != null) {
            ringtone.play();

            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, ringtoneUri);
                mediaPlayer.prepare();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ringtone.stop();
                    }
                }, mediaPlayer.getDuration()+100);

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public static void stopNotificationRingtone() {
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
    }
}
