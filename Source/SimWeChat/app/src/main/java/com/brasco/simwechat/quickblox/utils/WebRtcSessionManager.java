package com.brasco.simwechat.quickblox.utils;

import android.content.Context;
import android.util.Log;

import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.QBData;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacksImpl;

/**
 * Created by Administrator on 11/28/2016.
 */

public class WebRtcSessionManager extends QBRTCClientSessionCallbacksImpl {
    private static final String TAG = WebRtcSessionManager.class.getSimpleName();

    private static WebRtcSessionManager instance;
    private Context context;

    private static QBRTCSession currentSession;

    private WebRtcSessionManager(Context context) {
        this.context = context;
        LogUtil.writeDebugLog(TAG, "WebRtcSessionManager", "1");
    }

    public static WebRtcSessionManager getInstance(Context context){
        if (instance == null){
            instance = new WebRtcSessionManager(context);
        }

        LogUtil.writeDebugLog(TAG, "WebRtcSessionManager", "getInstance");

        return instance;
    }

    public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(QBRTCSession qbCurrentSession) {
        LogUtil.writeDebugLog(TAG, "setCurrentSession", "1");
        currentSession = qbCurrentSession;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession session) {
        Log.d(TAG, "onReceiveNewSession to WebRtcSessionManager");
        LogUtil.writeDebugLog(TAG, "onReceiveNewSession", "onReceiveNewSession to WebRtcSessionManager");
        //if (currentSession == null)
        {
            setCurrentSession(session);
            Integer senderId = session.getCallerID();
            String otherUsername = null;
            for (int i = 0; i < QBData.qbUsers.size(); i++){
                QBUser user = QBData.qbUsers.get(i);
                Integer userId = user.getId();
                if (userId.equals(senderId)){
                    otherUsername = user.getLogin();
                    LogUtil.writeDebugLog(TAG, "onReceiveNewSession", "1");
                    break;
                }
            }
            if (otherUsername != null) {
//                for (int i = 0; i < AppGlobals.receivedMessageData.size(); i++) {
//                    RecentMessageData data = AppGlobals.receivedMessageData.get(i);
//                    if (data.getUsername().equals(otherUsername)) {
//                        String imageFilePath = data.getLastImageFilePath();
//                        LogUtil.writeDebugLog(TAG, "onReceiveNewSession", imageFilePath);
//                        if (imageFilePath != null && !imageFilePath.isEmpty()){
//                            LogUtil.writeDebugLog(TAG, "onReceiveNewSession", "2");
//                            VideoViewActivity.startForResult(AppGlobals.mainActivity, MainActivity.REQUEST_DIALOG_ID_FOR_UPDATE, imageFilePath);
//                        }
//                        break;
//                    }
//                }
            }
        }
    }

    @Override
    public void onSessionClosed(QBRTCSession session) {
        Log.d(TAG, "onSessionClosed WebRtcSessionManager");
        LogUtil.writeDebugLog(TAG, "onSessionClosed", "onSessionClosed WebRtcSessionManager");
        if (session.equals(getCurrentSession())){
            LogUtil.writeDebugLog(TAG, "onSessionClosed", "2");
            setCurrentSession(null);
        }
    }
}

