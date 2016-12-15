package com.brasco.simwechat.quickblox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.brasco.simwechat.R;
import com.brasco.simwechat.utils.LogUtil;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCMediaConfig;

import java.util.List;

/**
 * Created by Administrator on 11/29/2016.
 */

public class SettingsUtil {
    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static void setSettingsForMultiCall(List<Integer> users) {
        LogUtil.writeDebugLog(TAG, "setSettingsForMultiCall", "1");
        if (users.size() <= 4) {
            int width = QBRTCMediaConfig.getVideoWidth();
            if (width > QBRTCMediaConfig.VideoQuality.VGA_VIDEO.width) {
                setDefaultVideoQuality();
            }
        } else {
            //set to minimum settings
            QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.width);
            QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.height);
            QBRTCMediaConfig.setVideoHWAcceleration(false);
            QBRTCMediaConfig.setVideoCodec(null);
        }
    }

    public static void setSettingsStrategy(List<Integer> users, SharedPreferences sharedPref, Context context) {
        LogUtil.writeDebugLog(TAG, "setSettingsStrategy", "1");
        setCommonSettings(sharedPref, context);
        if (users.size() == 1) {
            setSettingsFromPreferences(sharedPref, context);
        } else {
            setSettingsForMultiCall(users);
        }
    }

    private static void setCommonSettings(SharedPreferences sharedPref, Context context) {
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "1");
        String audioCodecDescription = getPreferenceString(sharedPref, context, R.string.pref_audiocodec_key,
                R.string.pref_audiocodec_def);
        QBRTCMediaConfig.AudioCodec audioCodec = QBRTCMediaConfig.AudioCodec.ISAC.getDescription()
                .equals(audioCodecDescription) ?
                QBRTCMediaConfig.AudioCodec.ISAC : QBRTCMediaConfig.AudioCodec.OPUS;
        Log.e(TAG, "audioCodec =: " + audioCodec.getDescription());
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "audioCodec =: " + audioCodec.getDescription());
        QBRTCMediaConfig.setAudioCodec(audioCodec);
        Log.v(TAG, "audioCodec = " + QBRTCMediaConfig.getAudioCodec());
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "audioCodec = " + QBRTCMediaConfig.getAudioCodec());
        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = getPreferenceBoolean(sharedPref, context,
                R.string.pref_disable_built_in_aec_key,
                R.string.pref_disable_built_in_aec_default);

        QBRTCMediaConfig.setUseBuildInAEC(!disableBuiltInAEC);
        Log.v(TAG, "setUseBuildInAEC = " + QBRTCMediaConfig.isUseBuildInAEC());
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "setUseBuildInAEC = " + QBRTCMediaConfig.isUseBuildInAEC());
        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = getPreferenceBoolean(sharedPref, context,
                R.string.pref_noaudioprocessing_key,
                R.string.pref_noaudioprocessing_default);
        QBRTCMediaConfig.setAudioProcessingEnabled(!noAudioProcessing);
        Log.v(TAG, "isAudioProcessingEnabled = " + QBRTCMediaConfig.isAudioProcessingEnabled());
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "isAudioProcessingEnabled = " + QBRTCMediaConfig.isAudioProcessingEnabled());
        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = getPreferenceBoolean(sharedPref, context,
                R.string.pref_opensles_key,
                R.string.pref_opensles_default);
        QBRTCMediaConfig.setUseOpenSLES(useOpenSLES);
        Log.v(TAG, "isUseOpenSLES = " + QBRTCMediaConfig.isUseOpenSLES());
        LogUtil.writeDebugLog(TAG, "setCommonSettings", "isUseOpenSLES = " + QBRTCMediaConfig.isUseOpenSLES());
    }

    private static void setSettingsFromPreferences(SharedPreferences sharedPref, Context context) {
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "1");
        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(context.getString(R.string.pref_hwcodec_key),
                Boolean.valueOf(context.getString(R.string.pref_hwcodec_default)));

        QBRTCMediaConfig.setVideoHWAcceleration(hwCodec);

        // Get video resolution from settings.
        int resolutionItem = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_resolution_key),
                "0"));
        Log.e(TAG, "resolutionItem =: " + resolutionItem);
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "resolutionItem =: " + resolutionItem);
        setVideoQuality(resolutionItem);
        Log.v(TAG, "resolution = " + QBRTCMediaConfig.getVideoHeight() + "x" + QBRTCMediaConfig.getVideoWidth());
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "resolution = " + QBRTCMediaConfig.getVideoHeight() + "x" + QBRTCMediaConfig.getVideoWidth());

        // Get start bitrate.
        int startBitrate = getPreferenceInt(sharedPref, context,
                R.string.pref_startbitratevalue_key,
                R.string.pref_startbitratevalue_default);
        Log.e(TAG, "videoStartBitrate =: " + startBitrate);
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "videoStartBitrate =: " + startBitrate);
        QBRTCMediaConfig.setVideoStartBitrate(startBitrate);
        Log.v(TAG, "videoStartBitrate = " + QBRTCMediaConfig.getVideoStartBitrate());
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "videoStartBitrate = " + QBRTCMediaConfig.getVideoStartBitrate());

        int videoCodecItem = Integer.parseInt(getPreferenceString(sharedPref, context, R.string.pref_videocodec_key, "0"));
        for (QBRTCMediaConfig.VideoCodec codec : QBRTCMediaConfig.VideoCodec.values()) {
            if (codec.ordinal() == videoCodecItem) {
                Log.e(TAG, "videoCodecItem =: " + codec.getDescription());
                LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "videoCodecItem =: " + codec.getDescription());
                QBRTCMediaConfig.setVideoCodec(codec);
                Log.v(TAG, "videoCodecItem = " + QBRTCMediaConfig.getVideoCodec());
                LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "videoCodecItem = " + QBRTCMediaConfig.getVideoCodec());
                break;
            }
        }
        // Get camera fps from settings.
        int cameraFps = getPreferenceInt(sharedPref, context, R.string.pref_frame_rate_key, R.string.pref_frame_rate_default);
        Log.e(TAG, "cameraFps = " + cameraFps);
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "cameraFps = " + cameraFps);
        QBRTCMediaConfig.setVideoFps(cameraFps);
        Log.v(TAG, "cameraFps = " + QBRTCMediaConfig.getVideoFps());
        LogUtil.writeDebugLog(TAG, "setSettingsFromPreferences", "cameraFps = " + QBRTCMediaConfig.getVideoFps());
    }

    public static void configRTCTimers(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        long answerTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_answer_time_interval_key,
                R.string.pref_answer_time_interval_default_value);
        QBRTCConfig.setAnswerTimeInterval(answerTimeInterval);
        Log.e(TAG, "answerTimeInterval = " + answerTimeInterval);
        LogUtil.writeDebugLog(TAG, "configRTCTimers", "answerTimeInterval = " + answerTimeInterval);
        int disconnectTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_disconnect_time_interval_key,
                R.string.pref_disconnect_time_interval_default_value);
        QBRTCConfig.setDisconnectTime(disconnectTimeInterval);
        Log.e(TAG, "disconnectTimeInterval = " + disconnectTimeInterval);
        LogUtil.writeDebugLog(TAG, "configRTCTimers", "disconnectTimeInterval = " + disconnectTimeInterval);

        long dialingTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_dialing_time_interval_key,
                R.string.pref_dialing_time_interval_default_value);
        QBRTCConfig.setDialingTimeInterval(dialingTimeInterval);
        Log.e(TAG, "dialingTimeInterval = " + dialingTimeInterval);
        LogUtil.writeDebugLog(TAG, "configRTCTimers", "dialingTimeInterval = " + dialingTimeInterval);
    }

    private static void setVideoQuality(int resolutionItem) {
        LogUtil.writeDebugLog(TAG, "setVideoQuality", "1");
        if (resolutionItem != -1) {
            LogUtil.writeDebugLog(TAG, "setVideoQuality", "2");
            setVideoFromLibraryPreferences(resolutionItem);
        } else {
            LogUtil.writeDebugLog(TAG, "setVideoQuality", "3");
            setDefaultVideoQuality();
        }
    }

    private static void setDefaultVideoQuality() {
        LogUtil.writeDebugLog(TAG, "setDefaultVideoQuality", "1");
//        QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.width);
//        QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.height);
        QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.width);
        QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.height);
    }

    private static void setVideoFromLibraryPreferences(int resolutionItem) {
        LogUtil.writeDebugLog(TAG, "setVideoFromLibraryPreferences", "1");
        for (QBRTCMediaConfig.VideoQuality quality : QBRTCMediaConfig.VideoQuality.values()) {
            if (quality.ordinal() == resolutionItem) {
                Log.e(TAG, "resolution =: " + quality.height + ":" + quality.width);
                LogUtil.writeDebugLog(TAG, "setVideoFromLibraryPreferences", "resolution =: " + quality.height + ":" + quality.width);
                QBRTCMediaConfig.setVideoHeight(quality.height);
                QBRTCMediaConfig.setVideoWidth(quality.width);
            }
        }
    }

    private static String getPreferenceString(SharedPreferences sharedPref, Context context, int strResKey, int strResDefValue) {
        return sharedPref.getString(context.getString(strResKey), context.getString(strResDefValue));
    }

    private static String getPreferenceString(SharedPreferences sharedPref, Context context, int strResKey, String strResDefValue) {
        return sharedPref.getString(context.getString(strResKey), strResDefValue);
    }

    public static int getPreferenceInt(SharedPreferences sharedPref, Context context, int strResKey, int strResDefValue) {
        return sharedPref.getInt(context.getString(strResKey), Integer.valueOf(context.getString(strResDefValue)));
    }
    private static boolean getPreferenceBoolean(SharedPreferences sharedPref, Context context, int StrRes, int strResDefValue) {
        return sharedPref.getBoolean(context.getString(StrRes), Boolean.valueOf(context.getString(strResDefValue)));
    }
}
