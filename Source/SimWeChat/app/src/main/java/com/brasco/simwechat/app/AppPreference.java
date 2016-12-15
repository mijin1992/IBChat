package com.brasco.simwechat.app;

/**
 * Created by Administrator on 12/14/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.brasco.simwechat.utils.LogUtil;

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
        public static final String SEND_MESSAGES_USER_NAME =    "send_messages_user_name";
        public static final String SEND_MESSAGES_MESSAGE =      "send_messages_message";
        public static final String SEND_MESSAGES_TIME =         "send_messages_time";
        public static final String SEND_MESSAGES_VIDEO_FILE_PATH =         "send_messages_video_file_path";
        public static final String SEND_MESSAGES_ACCEPTED_VIEW_REQUEST =   "send_messages_accepted_view_request";
        public static final String RECEIVED_MESSAGES_USER_NAME =    "received_messages_user_name";
        public static final String RECEIVED_MESSAGES_MESSAGE =      "received_messages_message";
        public static final String RECEIVED_MESSAGES_TIME =         "received_messages_time";
        public static final String RECEIVED_MESSAGES_VIDEO_FILE_PATH =         "received_messages_video_file_path";
        //FavoriteUserData
        public static final String FAVORITE_USER_NAME = "favorite_user_name";
        public static final String FAVORITE_USER_SIZE = "favorite_user_size";
        //DraftMessageData
        public static final String DRAFT_MESSAGE_USER_NAME = "draft_message_user_name";
        public static final String DRAFT_MESSAGE_MESSAGE = "draft_message_message";
        public static final String DRAFT_MESSAGE_FILEPATH = "draft_message_filepath";
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

//    public void setSendMessagesArray( ArrayList<RecentMessageData> messageDatas)
//    {
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", "start");
//        int nCount = messageDatas.size();
//        String strUsername = "";
//        String strMessage = "";
//        String strTime = "";
//        String strVideoFilePath = "";
//        String strAcceptedViewRequest = "";
//
//        for(int i = 0; i < nCount; i++)
//        {
//            RecentMessageData data = messageDatas.get(i);
//            if (strUsername.length() == 0) {
//                strUsername = data.getUsername();
//                if (data.getMessage() == null || data.getMessage().isEmpty())
//                    strMessage = BLACK_STRING;
//                else
//                    strMessage = data.getMessage();
//
//                strTime = String.valueOf(data.getTime());
//
//                if (data.getLastImageFilePath() == null || data.getLastImageFilePath().isEmpty())
//                    strVideoFilePath = BLACK_STRING;
//                else
//                    strVideoFilePath = data.getLastImageFilePath();
//
//                strAcceptedViewRequest = data.getAcceptedViewRequest() ? "1":"0";
//            } else {
//                strUsername += (DIVKEY + (data.getUsername()));
//
//                if (data.getMessage() == null || data.getMessage().isEmpty())
//                    strMessage += (DIVKEY + BLACK_STRING);
//                else
//                    strMessage += (DIVKEY + (data.getMessage()));
//
//                strTime += (DIVKEY + (String.valueOf(data.getTime())));
//
//                if (data.getLastImageFilePath() == null || data.getLastImageFilePath().isEmpty())
//                    strVideoFilePath += (DIVKEY + BLACK_STRING);
//                else
//                    strVideoFilePath += (DIVKEY + (data.getLastImageFilePath()));
//
//                strAcceptedViewRequest += (DIVKEY + (data.getAcceptedViewRequest() ? "1":"0"));
//            }
//        }
//        SharedPreferences.Editor editor = mPrefs.edit();
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", strUsername);
//        editor.putString(SEND_MESSAGES_USER_NAME, strUsername);
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", strMessage);
//        editor.putString(SEND_MESSAGES_MESSAGE, strMessage);
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", strTime);
//        editor.putString(SEND_MESSAGES_TIME, strTime);
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", strVideoFilePath);
//        editor.putString(SEND_MESSAGES_VIDEO_FILE_PATH, strVideoFilePath);
//        LogUtil.writeDebugLog(TAG, "setSendMessagesArray", strAcceptedViewRequest);
//        editor.putString(SEND_MESSAGES_ACCEPTED_VIEW_REQUEST, strAcceptedViewRequest);
//        editor.commit();
//    }
//
//    public ArrayList<RecentMessageData> getSendMessagesArray( )
//    {
//        ArrayList<RecentMessageData> sendMessagesData = new ArrayList<RecentMessageData>();
//        SharedPreferences.Editor editor = mPrefs.edit();
//        String strUsername = mPrefs.getString(SEND_MESSAGES_USER_NAME, "");
//        String strMessage = mPrefs.getString(SEND_MESSAGES_MESSAGE, "");
//        String strTime = mPrefs.getString(SEND_MESSAGES_TIME, "");
//        String strVideoFilePath = mPrefs.getString(SEND_MESSAGES_VIDEO_FILE_PATH, "");
//        String strAcceptedViewRequest = mPrefs.getString(SEND_MESSAGES_ACCEPTED_VIEW_REQUEST, "");
//
//        LogUtil.writeDebugLog(TAG, "getSendMessagesArray", strUsername);
//        LogUtil.writeDebugLog(TAG, "getSendMessagesArray", strMessage);
//        LogUtil.writeDebugLog(TAG, "getSendMessagesArray", strTime);
//        LogUtil.writeDebugLog(TAG, "getSendMessagesArray", strVideoFilePath);
//        LogUtil.writeDebugLog(TAG, "getSendMessagesArray", strAcceptedViewRequest);
//        // --
//        String[] strUsernameIndexes = strUsername.split(DIVKEY);
//        String[] strMessageIndexes = strMessage.split(DIVKEY);
//        String[] strTimeIndexes = strTime.split(DIVKEY);
//        String[] strVideoFilePathIndexs = strVideoFilePath.split(DIVKEY);
//        String[] strAcceptedViewRequestIndexs = strAcceptedViewRequest.split(DIVKEY);
//        if (!strUsername.isEmpty()) {
//            for (int i = 0; i < strUsernameIndexes.length; i++) {
//                long time = Long.parseLong(strTimeIndexes[i]);
//                String message = "";
//                if (strMessageIndexes.length > i){
//                    message= strMessageIndexes[i];
//                    if (message.equals(BLACK_STRING)){
//                        message = "";
//                    }
//                }
//                RecentMessageData data = new RecentMessageData(strUsernameIndexes[i], message, time);
//                data.setAcceptedViewRequest(strAcceptedViewRequestIndexs[i].equals("1") ? true : false);
//                String filePath = "";
//                if (strVideoFilePathIndexs.length > i){
//                    filePath= strVideoFilePathIndexs[i];
//                    if (filePath.equals(BLACK_STRING)){
//                        filePath = "";
//                    }
//                }
//                data.setLastImageFilePath(filePath);
//                sendMessagesData.add(data);
//            }
//        }
//
//        return sendMessagesData;
//    }
//
//    public void setReceivedMessagesArray( ArrayList<RecentMessageData> messageDatas)
//    {
//        int nCount = messageDatas.size();
//        String strUsername = "";
//        String strMessage = "";
//        String strTime = "";
//        String strVideoFilePath = "";
//        for(int i = 0; i < nCount; i++)
//        {
//            RecentMessageData data = messageDatas.get(i);
//            if (strUsername.length() == 0) {
//                strUsername = data.getUsername();
//                if (data.getMessage() == null || data.getMessage().isEmpty())
//                    strMessage = BLACK_STRING;
//                else
//                    strMessage = data.getMessage();
//
//                strTime = String.valueOf(data.getTime());
//
//                if (data.getLastImageFilePath() == null || data.getLastImageFilePath().isEmpty())
//                    strVideoFilePath = BLACK_STRING;
//                else
//                    strVideoFilePath = data.getLastImageFilePath();
//
//            } else {
//                strUsername += (DIVKEY + (data.getUsername()));
//
//                if (data.getMessage() == null || data.getMessage().isEmpty())
//                    strMessage += (DIVKEY + BLACK_STRING);
//                else
//                    strMessage += (DIVKEY + (data.getMessage()));
//
//                strTime += (DIVKEY + (String.valueOf(data.getTime())));
//
//                if (data.getLastImageFilePath() == null || data.getLastImageFilePath().isEmpty())
//                    strVideoFilePath += (DIVKEY + BLACK_STRING);
//                else
//                    strVideoFilePath += (DIVKEY + (data.getLastImageFilePath()));
//            }
//        }
//        SharedPreferences.Editor editor = mPrefs.edit();
//        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strUsername);
//        editor.putString(RECEIVED_MESSAGES_USER_NAME, strUsername);
//        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strMessage);
//        editor.putString(RECEIVED_MESSAGES_MESSAGE, strMessage);
//        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strTime);
//        editor.putString(RECEIVED_MESSAGES_TIME, strTime);
//        LogUtil.writeDebugLog(TAG, "setReceivedMessagesArray", strVideoFilePath);
//        editor.putString(RECEIVED_MESSAGES_VIDEO_FILE_PATH, strVideoFilePath);
//        editor.commit();
//    }
//
//    public ArrayList<RecentMessageData> getReceivedMessagesArray( )
//    {
//        ArrayList<RecentMessageData> sendMessagesData = new ArrayList<RecentMessageData>();
//        SharedPreferences.Editor editor = mPrefs.edit();
//        String strUsername = mPrefs.getString(RECEIVED_MESSAGES_USER_NAME, "");
//        String strMessage = mPrefs.getString(RECEIVED_MESSAGES_MESSAGE, "");
//        String strTime = mPrefs.getString(RECEIVED_MESSAGES_TIME, "");
//        String strVideoFilePath = mPrefs.getString(RECEIVED_MESSAGES_VIDEO_FILE_PATH, "");
//
//        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strUsername);
//        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strMessage);
//        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strTime);
//        LogUtil.writeDebugLog(TAG, "getReceivedMessagesArray", strVideoFilePath);
//        // --
//        String[] strUsernameIndexes = strUsername.split(DIVKEY);
//        String[] strMessageIndexes = strMessage.split(DIVKEY);
//        String[] strTimeIndexes = strTime.split(DIVKEY);
//        String[] strVideoFilePathIndexs = strVideoFilePath.split(DIVKEY);
//        if (!strUsername.isEmpty()) {
//            for (int i = 0; i < strUsernameIndexes.length; i++) {
//                long time = Long.parseLong(strTimeIndexes[i]);
//                String message = "";
//                if (strMessageIndexes.length > i){
//                    message= strMessageIndexes[i];
//                    if (message.equals(BLACK_STRING)){
//                        message = "";
//                    }
//                }
//                RecentMessageData data = new RecentMessageData(strUsernameIndexes[i], message, time);
//                String filePath = "";
//                if (strVideoFilePathIndexs.length > i){
//                    filePath= strVideoFilePathIndexs[i];
//                    if (filePath.equals(BLACK_STRING)){
//                        filePath = "";
//                    }
//                }
//                data.setLastImageFilePath(filePath);
//                sendMessagesData.add(data);
//            }
//        }
//
//        return sendMessagesData;
//    }
//
//    public ArrayList<FavoriteUserData> getFavoriteUserData()
//    {
//        ArrayList<FavoriteUserData> userDatas = new ArrayList<FavoriteUserData>();
//        SharedPreferences.Editor editor = mPrefs.edit();
//        String strUsername = mPrefs.getString(FAVORITE_USER_NAME, "");
//        String strSize = mPrefs.getString(FAVORITE_USER_SIZE, "");
//
//        LogUtil.writeDebugLog(TAG, "getFavoriteUserData", strUsername);
//        LogUtil.writeDebugLog(TAG, "getFavoriteUserData", strSize);
//        // --
//        String[] strUsernameIndexes = strUsername.split(DIVKEY);
//        String[] strSizeIndexes = strSize.split(DIVKEY);
//        if (!strUsername.isEmpty()) {
//            for (int i = 0; i < strUsernameIndexes.length; i++) {
//                int size = Integer.parseInt(strSizeIndexes[i]);
//                FavoriteUserData data = new FavoriteUserData(strUsernameIndexes[i], size);
//                userDatas.add(data);
//            }
//        }
//
//        return userDatas;
//    }
//
//    public void setFavoriteUserData( ArrayList<FavoriteUserData> userDatas)
//    {
//        int nCount = userDatas.size();
//        String strUsername = "";
//        String strSize = "";
//        for(int i = 0; i < nCount; i++)
//        {
//            FavoriteUserData data = userDatas.get(i);
//            if (strUsername.length() == 0) {
//                strUsername = data.getUserName();
//                strSize = String.valueOf(data.getFavoriteSize());
//            } else {
//                strUsername += (DIVKEY + (data.getUserName()));
//                strSize += (DIVKEY + (String.valueOf(data.getFavoriteSize())));
//            }
//        }
//        SharedPreferences.Editor editor = mPrefs.edit();
//        LogUtil.writeDebugLog(TAG, "setFavoriteUserData", strUsername);
//        editor.putString(FAVORITE_USER_NAME, strUsername);
//        LogUtil.writeDebugLog(TAG, "setFavoriteUserData", strSize);
//        editor.putString(FAVORITE_USER_SIZE, strSize);
//        editor.commit();
//    }
//
//    public ArrayList<DraftMessageData> getDraftMessageData()
//    {
//        ArrayList<DraftMessageData> datas = new ArrayList<DraftMessageData>();
//        SharedPreferences.Editor editor = mPrefs.edit();
//        String strUsername = mPrefs.getString(DRAFT_MESSAGE_USER_NAME, "");
//        String strMessage = mPrefs.getString(DRAFT_MESSAGE_MESSAGE, "");
//        String strFilepath = mPrefs.getString(DRAFT_MESSAGE_FILEPATH, "");
//
//        LogUtil.writeDebugLog(TAG, "getDraftMessageData", strUsername);
//        LogUtil.writeDebugLog(TAG, "getDraftMessageData", strMessage);
//        LogUtil.writeDebugLog(TAG, "getDraftMessageData", strFilepath);
//        // --
//        String[] strUsernameIndexes = strUsername.split(DIVKEY);
//        String[] strMessageIndexes = strMessage.split(DIVKEY);
//        String[] strFilepathIndexes = strFilepath.split(DIVKEY);
//        if (!strUsername.isEmpty()) {
//            for (int i = 0; i < strUsernameIndexes.length; i++) {
//                DraftMessageData data = new DraftMessageData(strUsernameIndexes[i], strMessageIndexes[i], strFilepathIndexes[i]);
//                datas.add(data);
//            }
//        }
//
//        return datas;
//    }
//
//    public void setDraftMessageData( ArrayList<DraftMessageData> datas)
//    {
//        int nCount = datas.size();
//        String strUsername = "";
//        String strMessages = "";
//        String strFilepaths = "";
//        for(int i = 0; i < nCount; i++)
//        {
//            DraftMessageData data = datas.get(i);
//            if (strUsername.length() == 0) {
//                strUsername = data.getUsername();
//                strMessages = data.getMessage();
//                strFilepaths = data.getFilePath();
//            } else {
//                strUsername += (DIVKEY + data.getUsername());
//                strMessages += (DIVKEY + data.getMessage());
//                strFilepaths += (DIVKEY + data.getFilePath());
//            }
//        }
//        SharedPreferences.Editor editor = mPrefs.edit();
//        LogUtil.writeDebugLog(TAG, "setDraftMessageData", strUsername);
//        editor.putString(DRAFT_MESSAGE_USER_NAME, strUsername);
//        LogUtil.writeDebugLog(TAG, "setDraftMessageData", strMessages);
//        editor.putString(DRAFT_MESSAGE_MESSAGE, strMessages);
//        LogUtil.writeDebugLog(TAG, "setDraftMessageData", strFilepaths);
//        editor.putString(DRAFT_MESSAGE_FILEPATH, strFilepaths);
//        editor.commit();
//    }

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

