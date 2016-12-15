package com.brasco.simwechat.interfaces;

import com.quickblox.videochat.webrtc.QBRTCSession;

import java.util.Map;

/**
 * Created by Administrator on 12/14/2016.
 */

public interface QBRTCSessionUserCallback {
    void onUserNotAnswer(QBRTCSession session, Integer userId);
    void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);
    void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);
    void onReceiveHangUpFromUser(QBRTCSession session, Integer userId);
}
