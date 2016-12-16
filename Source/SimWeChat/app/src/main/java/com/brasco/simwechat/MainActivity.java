package com.brasco.simwechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.activity.BaseActivity;
import com.brasco.simwechat.quickblox.core.gcm.GooglePlayServicesHelper;
import com.brasco.simwechat.quickblox.core.utils.NotificationUtils;
import com.brasco.simwechat.quickblox.core.utils.ResourceUtils;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.core.utils.constant.GcmConsts;
import com.brasco.simwechat.quickblox.db.QbUsersDbManager;
import com.brasco.simwechat.quickblox.managers.DialogsManager;
import com.brasco.simwechat.quickblox.managers.QbChatDialogMessageListenerImp;
import com.brasco.simwechat.quickblox.managers.QbDialogHolder;
import com.brasco.simwechat.quickblox.services.CallService;
import com.brasco.simwechat.quickblox.utils.PermissionsChecker;
import com.brasco.simwechat.quickblox.utils.SharedPreferencesUtil;
import com.brasco.simwechat.quickblox.utils.WebRtcSessionManager;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.callback.QbEntityCallbackImpl;
import com.brasco.simwechat.utils.LogUtil;
import com.halzhang.android.library.BottomTabIndicator;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class MainActivity extends BaseActivity implements DialogsManager.ManagingDialogsCallbacks{
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_SELECT_PEOPLE = 174;
    public static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;

    private ViewPager m_ViewPager = null;
    private BottomTabIndicator m_TabView = null;
    private FragmentAdapter m_Adapter = null;

    private ActionMode currentActionMode;
    private QBRequestGetBuilder requestBuilder;
    private BroadcastReceiver pushBroadcastReceiver;
    private GooglePlayServicesHelper googlePlayServicesHelper;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBIncomingMessagesManager incomingMessagesManager;
    private DialogsManager dialogsManager;
    private Toast mToast;
    private MyProgressDialog progressDialog;
    private AppPreference mPrefs;
    private QBUser currentUser;
    private ArrayList<QBUser> currentOpponentsList;
    private QbUsersDbManager dbManager;
    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;
    private PermissionsChecker checker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);

        setContentView(R.layout.activity_main);
        m_Adapter = new FragmentAdapter(this, getSupportFragmentManager());
        m_ViewPager = (ViewPager) findViewById(R.id.viewPager);
        m_ViewPager.setAdapter(m_Adapter);
        m_TabView = (BottomTabIndicator) findViewById(R.id.tab_indicator);
        m_TabView.setViewPager(m_ViewPager);
        m_TabView.setCurrentItem(0);

        mPrefs = new AppPreference(this);
        progressDialog = new MyProgressDialog(this, 0);
        AppGlobals.mainActivity = this;

        getUsers();
        setupList();
        LogUtil.writeDebugLog(TAG, "onCreate", "googlePlayServicesHelper");
        googlePlayServicesHelper = new GooglePlayServicesHelper();
        if (googlePlayServicesHelper.checkPlayServicesAvailable(this)) {
            googlePlayServicesHelper.registerForGcm(Constant.GCM_SENDER_ID);
        }

        pushBroadcastReceiver = new PushBroadcastReceiver();
        if (isAppSessionActive) {
            allDialogsMessagesListener = new AllDialogsMessageListener();
            systemMessagesListener = new SystemMessagesListener();
        }
        dialogsManager = new DialogsManager();
        initUi();

        initFields();
        checker = new PermissionsChecker(getApplicationContext());
        LogUtil.writeDebugLog(TAG, "onCreate", "end");

    }
    private void initUi() {
        LogUtil.writeDebugLog(TAG, "initUi", "1");
        requestBuilder = new QBRequestGetBuilder();
    }
    private void initFields() {
        LogUtil.writeDebugLog(TAG, "initFields", "start");
        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        webRtcSessionManager = WebRtcSessionManager.getInstance(this);
    }
    public void ActionBar(String title) {
        ActionBar toolBar = getSupportActionBar();
        if (toolBar != null) {
            toolBar.setDisplayShowHomeEnabled(false);
            toolBar.setDisplayShowTitleEnabled(false);
            toolBar.setDisplayShowCustomEnabled(true);
            toolBar.setCustomView(R.layout.default_actionbar);
            Toolbar parent = (Toolbar) toolBar.getCustomView().getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
        TextView txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(title);
        TextView buttonBack = (TextView) findViewById(R.id.toolbar_back);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/materialicons.ttf");
        buttonBack.setTypeface(font);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupList() {
        LogUtil.writeDebugLog(TAG, "setupList", "start");
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
    }
    private void getUsers(){
        LogUtil.writeDebugLog(TAG, "getUsers", "1");
        if(QBData.qbUsers != null) {
            ArrayList<QBUser> qbUsers = QBData.qbUsers;
            AppGlobals.mAllUserData.clear();
            for (int i = 0; i < qbUsers.size(); i++) {
                if (!isUserMe(qbUsers.get(i))) {
                    QBUser user = qbUsers.get(i);
                    AppGlobals.mAllUserData.add(new UserData(user, user.getCustomData(), user.getLogin(), user.getFullName()));
                }
            }
        }
    }
    protected boolean isUserMe(QBUser user) {
        if (QBData.curQBUser != null) {
            QBUser currentUser = QBData.curQBUser;
            String userID = user.getLogin();
            //for administrator
            if (userID.equals("ibrahima")) {
                return true;
            }
            return currentUser != null && currentUser.getId().equals(user.getId());
        } else {
            return false;
        }
    }
    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
        LogUtil.writeDebugLog(TAG, "loadDialogsFromQb", "start");
        if (!silentUpdate) {
            progressDialog.show();
        }
        ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "loadDialogsFromQb", "onSuccess");
                progressDialog.hide();
                if (clearDialogHolder) {
                    QbDialogHolder.getInstance().clear();
                }
                QbDialogHolder.getInstance().addDialogs(dialogs);
                updateDialogsAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "loadDialogsFromQb", "onError");
                progressDialog.hide();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean hasAttachments(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        return attachments != null && !attachments.isEmpty();
    }
    private String getAttachmentsType(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        QBAttachment attachment = attachments.iterator().next();
        String ret = attachment.getType();
        return ret;
    }
    private UserData getUserDataFromSenderId(Integer senderId){
        for(int i=0; i <AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            Integer id = user.getQBUser().getId();
            if (id.equals(senderId)){
                return user;
            }
        }
        LogUtil.writeDebugLog(TAG, "getUserDataFromSenderId", "return value is null");
        return null;
    }

    public void startChatActivity(UserData user){
        if (user != null){
            AppGlobals.curChattingUser = user;
            ArrayList<QBUser> newDialog = new ArrayList<QBUser>();
            newDialog.add(AppGlobals.curChattingUser.getQBUser());
            newDialog.add(QBData.curQBUser);
            if (isPrivateDialogExist(newDialog)){
                LogUtil.writeDebugLog(TAG, "startReceivedActivity", "start ReceivedActivity");
                newDialog.remove(ChatHelper.getCurrentUser());
                QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(newDialog.get(0));
                ChatActivity.startForResult(MainActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog.getDialogId());
            } else {
                LogUtil.writeDebugLog(TAG, "startReceivedActivity", "create Dialog");
                createDialog(newDialog);
            }
        }
    }
    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers){
        LogUtil.writeDebugLog(TAG, "isPrivateDialogExist", "1");
        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }
    private void createDialog(final ArrayList<QBUser> selectedUsers) {
        LogUtil.writeDebugLog(TAG, "createDialog", "start");
        progressDialog.show();
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        LogUtil.writeDebugLog(TAG, "createDialog", "onSuccess");
                        progressDialog.hide();
                        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                        LogUtil.writeDebugLog(TAG, "createDialog", "start ReceivedActivity");
                        ChatActivity.startForResult(MainActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, dialog.getDialogId());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        progressDialog.hide();
                        LogUtil.writeDebugLog(TAG, "createDialog", "start onError");
                    }
                }
        );
    }
    public UserData getUserDataFromUserId(String userId){
        LogUtil.writeDebugLog(TAG, "getUserDataFromUsername", userId);
        for (int i=0; i< AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            if (user.getUserId().equals(userId))
                return user;
        }
        return null;
    }
    private void loadUpdatedDialog(String dialogId) {
        LogUtil.writeDebugLog(TAG, "loadUpdatedDialog", "start");
        ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "loadUpdatedDialog", "onSuccess");
                QbDialogHolder.getInstance().addDialog(result);
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "loadUpdatedDialog", "onError");
            }
        });
    }
    private void userLogout() {
        LogUtil.writeDebugLog(TAG, "userLogout", "start");
        ChatHelper.getInstance().logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "userLogout", "onSuccess");
                if (googlePlayServicesHelper.checkPlayServicesAvailable()) {
                    googlePlayServicesHelper.unregisterFromGcm(Constant.GCM_SENDER_ID);
                }
                SharedPreferencesUtil.removeQbUser();
                QbDialogHolder.getInstance().clear();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "userLogout", "onError");
                reconnectToChatLogout(SharedPreferencesUtil.getQbUser());
            }
        });
    }
    private void reconnectToChatLogout(final QBUser user) {
        LogUtil.writeDebugLog(TAG, "reconnectToChatLogout", "start");
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "reconnectToChatLogout", "onSuccess");
                userLogout();
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "reconnectToChatLogout", "onError");
                invalidateOptionsMenu();
                reconnectToChatLogout(SharedPreferencesUtil.getQbUser());
            }
        });
    }
    private void updateDialogsList() {
        LogUtil.writeDebugLog(TAG, "updateDialogsList", "1");
        if (isAppSessionActive) {
            loadDialogsFromQb(true, true);
        }
    }
    private void registerQbChatListeners() {
        LogUtil.writeDebugLog(TAG, "registerQbChatListeners", "1");
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : new AllDialogsMessageListener());
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : new SystemMessagesListener());
        }

        dialogsManager.addManagingDialogsCallbackListener(this);
    }
    private void unregisterQbChatListeners() {
        LogUtil.writeDebugLog(TAG, "unregisterQbChatListeners", "1");
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }
        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }
        dialogsManager.removeManagingDialogsCallbackListener(this);
    }
    public void updateDialogsAdapter() {
        LogUtil.writeDebugLog(TAG, "updateDialogsAdapter", "1");

    }
    private void deleteSelectedDialogs(Collection<QBChatDialog> selectedDialogs) {
        ChatHelper.getInstance().deleteDialogs(selectedDialogs, new QBEntityCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> dialogsIds, Bundle bundle) {
                QbDialogHolder.getInstance().deleteDialogs(dialogsIds);
                updateDialogsAdapter();
            }
            @Override
            public void onError(QBResponseException e) { }
        });
    }
    private void startLoadUsers() {
        String currentRoomName = SharedPrefsHelper.getInstance().get(Constant.PREF_CURREN_ROOM_NAME);
        requestExecutor.loadUsersByTag(currentRoomName, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                dbManager.saveAllUsers(result, true);
                initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
            }
        });
    }
    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        LogUtil.writeDebugLog(TAG, "isCurrentOpponentsListActual", "start");
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }
    private void addRecentMessage(String dialogId, QBChatMessage qbChatMessage, Integer senderId){
        if(qbChatMessage.getBody() != null && !qbChatMessage.getBody().isEmpty()) {
            String userId = null;
            String userName = null;
            String logoUrl = null;
            for (int i = 0; i < AppGlobals.mAllUserData.size(); i++){
                QBUser user = AppGlobals.mAllUserData.get(i).getQBUser();
                Integer id = user.getId();
                if (id.equals(senderId)){
                    userId = user.getLogin();
                    userName = user.getFullName();
                    logoUrl = user.getCustomData();
                    break;
                }
            }
            if (userId != null) {
                String message = qbChatMessage.getBody();
                Date date = new Date();
                long time = date.getTime();
                boolean isExist = false;
                String notificationMessage = "";
                for (int i = 0; i < AppGlobals.mRecentessageArray.size(); i++) {
                    RecentMessageData data = AppGlobals.mRecentessageArray.get(i);
                    if (data.getUserId().equals(userId)) {
                        data.setMessage(message);
                        data.setTime(time);
                        AppGlobals.mRecentessageArray.remove(i);
                        AppGlobals.mRecentessageArray.add(0, data);
                        notificationMessage = userName + ": " +message;
                        isExist = true;
                        break;
                    }
                }
                if (isExist == false){
                    LogUtil.writeDebugLog(TAG, "addReceivedMessage", "5");
                    RecentMessageData receivedMessage = new RecentMessageData(userId, userName, message, time, logoUrl);
                    AppGlobals.mRecentessageArray.add(0, receivedMessage);

                    notificationMessage = userName + " sent new message to you.";

                }
//                if (mPrefs.getNotifications() && mPrefs.getNotificationMessageReceived())
//                    NotificationUtils.showNotification(getApplicationContext(), MainActivity.class,
//                            ResourceUtils.getString(R.string.notification_title), notificationMessage,
//                            R.drawable.ic_launcher, 1);

                updateDialogsAdapter();
            }
        }
    }
    private void initUsersList() {
        LogUtil.writeDebugLog(TAG, "initUsersList", "1");
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
        proceedInitUsersList();
    }
    private void proceedInitUsersList() {
        LogUtil.writeDebugLog(TAG, "proceedInitUsersList", "1");
        currentOpponentsList = dbManager.getAllUsers();
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
    }
    private boolean isLoggedInChat() {
        LogUtil.writeDebugLog(TAG, "isLoggedInChat", "1");
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }
    private void tryReLoginToChat() {
        LogUtil.writeDebugLog(TAG, "tryReLoginToChat", "1");
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }
    public void startCall(boolean isVideoCall) {
        LogUtil.writeDebugLog(TAG, "startCall", "start");
        if (isLoggedInChat()) {
            LogUtil.writeDebugLog(TAG, "startCall", "ok");
            QBUser user = AppGlobals.curChattingUser.getQBUser();
            Integer userId = user.getId();
            ArrayList<Integer> opponentsList = new ArrayList<Integer>();
            opponentsList.add(userId);
            QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
            QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

//        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());
        }
    }
    @Override
    protected void onResume() {
        LogUtil.writeDebugLog(TAG, "onResume", "1");
        super.onResume();
        googlePlayServicesHelper.checkPlayServicesAvailable(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(GcmConsts.ACTION_NEW_GCM_EVENT));
        initUsersList();
    }
    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.writeDebugLog(TAG, "onPause", "1");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pushBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        super.onDestroy();
        if (isAppSessionActive) {
            unregisterQbChatListeners();
        }
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.layout_root);
    }

    @Override
    public void onSessionCreated(boolean success) {
        LogUtil.writeDebugLog(TAG, "onSessionCreated", "1");
        if (success) {
            QBUser currentUser = ChatHelper.getCurrentUser();
            if (currentUser != null) {

            }
            registerQbChatListeners();
            if (QbDialogHolder.getInstance().getDialogs().size() > 0) {
                loadDialogsFromQb(true, true);
            } else {
                loadDialogsFromQb(false, true);
            }
        }
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }
    private class PushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(GcmConsts.EXTRA_GCM_MESSAGE);
            loadDialogsFromQb(true, true);
            LogUtil.writeDebugLog(TAG, "PushBroadcastReceiver", "onReceive");
        }
    }
    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            LogUtil.writeDebugLog(TAG, "SystemMessagesListener", "processMessage");
            dialogsManager.onSystemMessageReceived(qbChatMessage);
//            NotificationUtils.showNotification(getApplicationContext(), SplashScreenActivity.class,
//                    ResourceUtils.getString(R.string.notification_title), "SystemMessagesListener",
//                    R.drawable.ic_launcher, 1);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {
            LogUtil.writeDebugLog(TAG, "SystemMessagesListener", "processError");
        }
    }
    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            LogUtil.writeDebugLog(TAG, "AllDialogsMessageListener", "processMessage");
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
                addRecentMessage(dialogId, qbChatMessage, senderId);
            }
        }
    }
}
