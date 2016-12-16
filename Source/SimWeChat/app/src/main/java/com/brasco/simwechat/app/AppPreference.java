package com.brasco.simwechat.app;

/**
 * Created by Administrator on 12/14/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.utils.LogUtil;

import java.util.ArrayList;

public class AppPreference {
    public static final String TAG = "AppPreference";
    private String DIVKEY = "#&#";
    private String BLACK_STRING = "&**&";

        // app
        public static final String AGREE_TERM = "AGREE_TERM";
        // sign in
        public static final String SIGN_IN_AUTO = "SIGN_IN_AUTO";
        public static final String SIGN_IN_USERNAME = "SIGN_IN_USERNAME";
        public static final String SIGN_IN_PASSWORD = "SIGN_IN_PASSWORD";
        // setting
        public static final String SETTING_DISTANCE_INDEX = "SETTING_DISTANCE_INDEX";
        public static final String SETTING_TIME_INDEX = "SETTING_TIME_INDEX";
        // dialog
        public static final String DONT_SHOW_AGAIN_FREE_USE_DIALOG = "DONT_SHOW_AGAIN_FREE_USE_DIALOG";
        // camera setting
        public static final String CAMERA_RESOLUTION_INDEX = "camera_resolution_index";
        //Send&Received Data
        public static final String RECENT_MESSAGES_USER_ID =    "recent_messages_user_id";
        public static final String RECENT_MESSAGES_USER_NAME =    "recent_messages_user_name";
        public static final String RECENT_MESSAGES_MESSAGE =      "recent_messages_message";
        public static final String RECENT_MESSAGES_TIME =         "recent_messages_time";
        public static final String RECENT_MESSAGES_UNREAD =         "recent_messages_unread";
        //User Name and Password
        public static final String QUICKBLOX_USER_NAME = "quickblox_user_name";
        public static final String QUICKBLOX_USER_PASS = "quickblox_user_pass";
        //Setting
        public static final String NOTIFICATIONS = "notifications";
        public static final String NOTIFICATION_MESSAGE_RECEIVED = "notification_message_received";
        public static final String NOTIFICATION_VIEW_REQUEST = "notification_view_request";
        public static final String NOTIFICATION_RING = "notification_ring";
        public static final String NOTIFICATION_VIBRATE = "notification_vibrate";

        public static final String RESPONSE_STREAM = "notification_vibrate";
        public static final String ALLOW_RECORDED_RESPONSE = "allow_recorded_response";

    private static String APP_SHARED_PREFS;
    private static SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    public AppPreference(Context context) {
        APP_SHARED_PREFS = context.getApplicationContext().getPackageName();
        mPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Activity.MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();
    }
    public static void initialize(SharedPreferences pref) {
        mPrefs = pref;
    }

    // check contain
    public static boolean contains(String key) {
        return mPrefs.contains(key);
    }

    // boolean
    public static boolean getBool(String key, boolean def) {
        return mPrefs.getBoolean(key, def);
    }
    public static void setBool(String key, boolean value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // int
    public static int getInt(String key, int def) {
        return mPrefs.getInt(key, def);
    }
    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // long
    public static long getLong(String key, long def) {
        return mPrefs.getLong(key, def);
    }
    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    // string
    public static String getStr(String key, String def) {
        return mPrefs.getString(key, def);
    }
    public static void setStr(String key, String value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // remove
    public static void removeKey(String key) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void setRecentMessagesArray( ArrayList<RecentMessageData> messageDatas)
    {
        int nCount = messageDatas.size();
        String strUserId = "";
        String strUsername = "";
        String strMessage = "";
        String strTime = "";
        String strUnRead = "";
        for(int i = 0; i < nCount; i++)
        {
            RecentMessageData data = messageDatas.get(i);
            if (strUsername.length() == 0) {
                strUserId = data.getUserId();
                strUsername = data.getUsername();
                if (data.getMessage() == null || data.getMessage().isEmpty())
                    strMessage = BLACK_STRING;
                else
                    strMessage = data.getMessage();

                strTime = String.valueOf(data.getTime());
                strUnRead = String.valueOf(data.getUnreadMessages());
            } else {
                strUserId += (DIVKEY + (data.getUserId()));
                strUsername += (DIVKEY + (data.getUsername()));

                if (data.getMessage() == null || data.getMessage().isEmpty())
                    strMessage += (DIVKEY + BLACK_STRING);
                else
                    strMessage += (DIVKEY + (data.getMessage()));

                strTime += (DIVKEY + (String.valueOf(data.getTime())));
                strUnRead += (DIVKEY + (String.valueOf(data.getUnreadMessages())));
            }
        }
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(RECENT_MESSAGES_USER_ID, strUserId);
        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strUsername);
        editor.putString(RECENT_MESSAGES_USER_NAME, strUsername);
        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strMessage);
        editor.putString(RECENT_MESSAGES_MESSAGE, strMessage);
        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strTime);
        editor.putString(RECENT_MESSAGES_TIME, strTime);
        editor.putString(RECENT_MESSAGES_UNREAD, strUnRead);
        editor.commit();
    }

    public ArrayList<RecentMessageData> getRecentMessagesArray( )
    {
        ArrayList<RecentMessageData> sendMessagesData = new ArrayList<RecentMessageData>();
        SharedPreferences.Editor editor = mPrefs.edit();
        String strUserId = mPrefs.getString(RECENT_MESSAGES_USER_ID, "");
        String strUsername = mPrefs.getString(RECENT_MESSAGES_USER_NAME, "");
        String strMessage = mPrefs.getString(RECENT_MESSAGES_MESSAGE, "");
        String strTime = mPrefs.getString(RECENT_MESSAGES_TIME, "");
        String strUnRead = mPrefs.getString(RECENT_MESSAGES_UNREAD, "");

        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strUsername);
        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strMessage);
        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strTime);
        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strUnRead);
        // --
        String[] strUserIdIndexes = strUserId.split(DIVKEY);
        String[] strUsernameIndexes = strUsername.split(DIVKEY);
        String[] strMessageIndexes = strMessage.split(DIVKEY);
        String[] strTimeIndexes = strTime.split(DIVKEY);
        String[] strUnReadIndexs = strUnRead.split(DIVKEY);
        if (!strUserId.isEmpty()) {
            for (int i = 0; i < strUserIdIndexes.length; i++) {
                long time = Long.parseLong(strTimeIndexes[i]);
                String message = "";
                if (strMessageIndexes.length > i){
                    message= strMessageIndexes[i];
                    if (message.equals(BLACK_STRING)){
                        message = "";
                    }
                }
                String username = "";
                if (strUsernameIndexes.length > i){
                    username= strUsernameIndexes[i];
                    if (username.equals(BLACK_STRING)){
                        username = "";
                    }
                }
                RecentMessageData data = new RecentMessageData(strUserIdIndexes[i], username, message, time, "");
                data.setUnreadMessages(Integer.getInteger(strUnReadIndexs[i]));
                sendMessagesData.add(data);
            }
        }

        return sendMessagesData;
    }

    public void setQuickBloxUsername(String username){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(QUICKBLOX_USER_NAME, username);
        LogUtil.writeDebugLog(TAG, "setQuickBloxUsername", username);
        editor.commit();
    }
    public String getQuickBloxUsername(){
        SharedPreferences.Editor editor = mPrefs.edit();
        String strUsername = mPrefs.getString(QUICKBLOX_USER_NAME, "");
        LogUtil.writeDebugLog(TAG, "getQuickBloxUsername", strUsername);
        return  strUsername;
    }

    public void setQuickBloxUserPass(String password){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(QUICKBLOX_USER_PASS, password);
        LogUtil.writeDebugLog(TAG, "setQuickBloxUserPass", password);
        editor.commit();
    }
    public String getQuickBloxUserPass(){
        SharedPreferences.Editor editor = mPrefs.edit();
        String password = mPrefs.getString(QUICKBLOX_USER_PASS, "");
        LogUtil.writeDebugLog(TAG, "getQuickBloxUserPass", password);
        return  password;
    }

    public void setNotifications(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NOTIFICATIONS, on);
        editor.commit();
    }
    public Boolean getNotifications(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(NOTIFICATIONS, true);
        return on;
    }
    public void setNotificationMessageReceived(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NOTIFICATION_MESSAGE_RECEIVED, on);
        editor.commit();
    }
    public Boolean getNotificationMessageReceived(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(NOTIFICATION_MESSAGE_RECEIVED, true);
        return on;
    }
    public void setNotificationViewRequest(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NOTIFICATION_VIEW_REQUEST, on);
        editor.commit();
    }
    public Boolean getNotificationViewrequest(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(NOTIFICATION_VIEW_REQUEST, true);
        return on;
    }
    public void setNotificationRing(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NOTIFICATION_RING, on);
        editor.commit();
    }
    public Boolean getNotificationRing(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(NOTIFICATION_RING, true);
        return on;
    }
    public void setNotificationVibrate(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NOTIFICATION_VIBRATE, on);
        editor.commit();
    }
    public Boolean getNotificationVibrate(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(NOTIFICATION_VIBRATE, true);
        return on;
    }
    public void setResponseStream(boolean on){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(RESPONSE_STREAM, on);
        editor.commit();
    }
    public Boolean getResponseStream(){
        SharedPreferences.Editor editor = mPrefs.edit();
        boolean on = mPrefs.getBoolean(RESPONSE_STREAM, true);
        return on;
    }
    public void setAllowRecordedResponse(int value){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(ALLOW_RECORDED_RESPONSE, value);
        editor.commit();
    }
    public int getAllowRecordedResponse(){
        SharedPreferences.Editor editor = mPrefs.edit();
        int value = mPrefs.getInt(ALLOW_RECORDED_RESPONSE, 5);
        return value;
    }
}

