package com.brasco.simwechat.interfaces;

/**
 * Created by Administrator on 12/14/2016.
 */

import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;

public interface ConversationFragmentCallbackListener {

    void addTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks);
    void removeRTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks);

    void addRTCSessionUserCallback(QBRTCSessionUserCallback sessionUserCallback);
    void removeRTCSessionUserCallback(QBRTCSessionUserCallback sessionUserCallback);

    void addCurrentCallStateCallback (CurrentCallStateCallback currentCallStateCallback);
    void removeCurrentCallStateCallback (CurrentCallStateCallback currentCallStateCallback);

    void addOnChangeDynamicToggle (OnChangeDynamicToggle onChangeDynamicCallback);
    void removeOnChangeDynamicToggle (OnChangeDynamicToggle onChangeDynamicCallback);

    void onSetAudioEnabled(boolean isAudioEnabled);

    void onSetVideoEnabled(boolean isNeedEnableCam);

    void onSwitchAudio();

    void onHangUpCurrentSession();

}
