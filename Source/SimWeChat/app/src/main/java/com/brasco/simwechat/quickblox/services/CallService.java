package com.brasco.simwechat.quickblox.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.utils.ChatPingAlarmManager;
import com.brasco.simwechat.quickblox.utils.SettingsUtil;
import com.brasco.simwechat.quickblox.utils.WebRtcSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;

import org.jivesoftware.smackx.ping.PingFailedListener;

/**
 * Created by Administrator on 11/29/2016.
 */

public class CallService  extends Service {
    private static final String TAG = CallService.class.getSimpleName();
    private QBChatService chatService;
    private QBRTCClient rtcClient;
    private PendingIntent pendingIntent;
    private int currentCommand;
    private QBUser currentUser;

    public static void start(Context context, QBUser qbUser, PendingIntent pendingIntent) {
        Intent intent = new Intent(context, CallService.class);

        intent.putExtra(Constant.EXTRA_COMMAND_TO_SERVICE, Constant.COMMAND_LOGIN);
        intent.putExtra(Constant.EXTRA_QB_USER, qbUser);
        intent.putExtra(Constant.EXTRA_PENDING_INTENT, pendingIntent);

        context.startService(intent);
        LogUtil.writeDebugLog(TAG, "start", "start");
    }

    public static void start(Context context, QBUser qbUser) {
        start(context, qbUser, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createChatService();

        Log.d(TAG, "Service onCreate()");
        LogUtil.writeDebugLog(TAG, "onCreate", "1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        LogUtil.writeDebugLog(TAG, "onStartCommand", "Service started");

        parseIntentExtras(intent);

        startSuitableActions();

        return START_REDELIVER_INTENT;
    }

    private void parseIntentExtras(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            currentCommand = intent.getIntExtra(Constant.EXTRA_COMMAND_TO_SERVICE, Constant.COMMAND_NOT_FOUND);
            pendingIntent = intent.getParcelableExtra(Constant.EXTRA_PENDING_INTENT);
            currentUser = (QBUser) intent.getSerializableExtra(Constant.EXTRA_QB_USER);
        }
    }

    private void startSuitableActions() {
        LogUtil.writeDebugLog(TAG, "startSuitableActions", "1");
        if (currentCommand == Constant.COMMAND_LOGIN) {
            startLoginToChat();
        } else if (currentCommand == Constant.COMMAND_LOGOUT) {
            logout();
        }
    }

    private void createChatService() {
        LogUtil.writeDebugLog(TAG, "createChatService", "1");
        if (chatService == null) {
            LogUtil.writeDebugLog(TAG, "createChatService", "2");
            QBChatService.setDebugEnabled(true);
            QBChatService.setDefaultAutoSendPresenceInterval(60);
            chatService = QBChatService.getInstance();
        }
    }

    private void startLoginToChat() {
        if (!chatService.isLoggedIn()) {
            loginToChat(currentUser);
        } else {
            sendResultToActivity(true, null);
        }
    }

    private void loginToChat(QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "loginToChat", "start");
        chatService.login(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Log.d(TAG, "login onSuccess");
                LogUtil.writeDebugLog(TAG, "loginToChat", "onSuccess");
                startActionsOnSuccessLogin();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d(TAG, "login onError " + e.getMessage());
                LogUtil.writeDebugLog(TAG, "loginToChat", "onError");
                sendResultToActivity(false, e.getMessage() != null
                        ? e.getMessage()
                        : "Login error");
            }
        });
    }

    private void startActionsOnSuccessLogin() {
        LogUtil.writeDebugLog(TAG, "startActionsOnSuccessLogin", "1");
        initPingListener();
        initQBRTCClient();
        sendResultToActivity(true, null);
    }

    private void initPingListener() {
        LogUtil.writeDebugLog(TAG, "initPingListener", "1");
        ChatPingAlarmManager.onCreate(this);
        ChatPingAlarmManager.getInstanceFor().addPingListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                Log.d(TAG, "Ping chat server failed");
                LogUtil.writeDebugLog(TAG, "initPingListener", "Ping chat server failed");
            }
        });
    }

    private void initQBRTCClient() {
        LogUtil.writeDebugLog(TAG, "initQBRTCClient", "1");
        rtcClient = QBRTCClient.getInstance(getApplicationContext());
        // Add signalling manager
        chatService.getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                    LogUtil.writeDebugLog(TAG, "initQBRTCClient", "addSignalingManagerListener");
                }
            }
        });

        // Configure
        QBRTCConfig.setDebugEnabled(true);
        SettingsUtil.configRTCTimers(CallService.this);

        // Add service as callback to RTCClient
        rtcClient.addSessionCallbacksListener(WebRtcSessionManager.getInstance(this));
        rtcClient.prepareToProcessCalls();
    }

    private void sendResultToActivity(boolean isSuccess, String errorMessage) {
        if (pendingIntent != null) {
            Log.d(TAG, "sendResultToActivity()");
            LogUtil.writeDebugLog(TAG, "sendResultToActivity", "1");
            try {
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA_LOGIN_RESULT, isSuccess);
                intent.putExtra(Constant.EXTRA_LOGIN_ERROR_MESSAGE, errorMessage);

                pendingIntent.send(CallService.this, Constant.EXTRA_LOGIN_RESULT_CODE, intent);
            } catch (PendingIntent.CanceledException e) {
                String errorMessageSendingResult = e.getMessage();
                LogUtil.writeDebugLog(TAG, "sendResultToActivity", errorMessageSendingResult);
                Log.d(TAG, errorMessageSendingResult != null
                        ? errorMessageSendingResult
                        : "Error sending result to activity");
            }
        }
    }

    public static void logout(Context context) {
        Intent intent = new Intent(context, CallService.class);
        intent.putExtra(Constant.EXTRA_COMMAND_TO_SERVICE, Constant.COMMAND_LOGOUT);
        context.startService(intent);
    }

    private void logout() {
        destroyRtcClientAndChat();
        LogUtil.writeDebugLog(TAG, "logout", "1");
    }

    private void destroyRtcClientAndChat() {
        LogUtil.writeDebugLog(TAG, "destroyRtcClientAndChat", "1");
        if (rtcClient != null) {
            LogUtil.writeDebugLog(TAG, "destroyRtcClientAndChat", "2");
            rtcClient.destroy();
        }
        ChatPingAlarmManager.onDestroy();
        if (chatService != null) {
            LogUtil.writeDebugLog(TAG, "destroyRtcClientAndChat", "3");
            chatService.logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    LogUtil.writeDebugLog(TAG, "destroyRtcClientAndChat", "onSuccess");
                    chatService.destroy();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "logout onError " + e.getMessage());
                    LogUtil.writeDebugLog(TAG, "destroyRtcClientAndChat", "onError");
                    chatService.destroy();
                }
            });
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy()");
        LogUtil.writeDebugLog(TAG, "onDestroy", "Service onDestroy()");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind()");
        LogUtil.writeDebugLog(TAG, "onBind", "Service onBind()");
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Service onTaskRemoved()");
        LogUtil.writeDebugLog(TAG, "onTaskRemoved", "Service onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
        destroyRtcClientAndChat();
    }
}
