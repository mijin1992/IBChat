package com.brasco.simwechat.quickblox.utils;

import android.content.Context;
import android.util.Log;

import com.brasco.simwechat.ContactProfileActivity;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.quickblox.activity.CallActivity;
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
        LogUtil.writeDebugLog(TAG, "onReceiveNewSession", "onReceiveNewSession to WebRtcSessionManager");
        if (currentSession == null)
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
                CallActivity.start(AppGlobals.mainActivity, true);
                //ContactProfileActivity.start(context, true, otherUsername);
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

