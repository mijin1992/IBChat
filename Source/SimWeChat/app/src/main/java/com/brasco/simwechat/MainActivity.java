package com.brasco.simwechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.brasco.simwechat.quickblox.core.utils.constant.GcmConsts;
import com.brasco.simwechat.quickblox.db.QbUsersDbManager;
import com.brasco.simwechat.quickblox.managers.DialogsManager;
import com.brasco.simwechat.quickblox.managers.QbChatDialogMessageListenerImp;
import com.brasco.simwechat.quickblox.managers.QbDialogHolder;
import com.brasco.simwechat.quickblox.utils.PermissionsChecker;
import com.brasco.simwechat.quickblox.utils.WebRtcSessionManager;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.utils.LogUtil;
import com.halzhang.android.library.BottomTabIndicator;
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

import java.util.ArrayList;
import java.util.Collection;

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
//                updateDialogsAdapter();
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
//        for(int i=0; i <mAllUserData.size(); i++){
//            UserData user = mAllUserData.get(i);
//            Integer id = user.getQBUser().getId();
//            if (id.equals(senderId)){
//                return user;
//            }
//        }
//        LogUtil.writeDebugLog(TAG, "getUserDataFromSenderId", "return value is null");
        return null;
    }

    public void startChatActivity(UserData user){
        ChatActivity.startForResult(MainActivity.this, REQUEST_DIALOG_ID_FOR_UPDATE, "");
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        super.onDestroy();
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    @Override
    public void onSessionCreated(boolean success) {

    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {

    }

    @Override
    public void onDialogUpdated(String chatDialog) {

    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {

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
//                addReceivedMessage(dialogId, qbChatMessage, senderId);

                Boolean hasAttachments = hasAttachments(qbChatMessage);
                String type = "";
                if (hasAttachments) {
                    LogUtil.writeDebugLog(TAG, "AllDialogsMessageListener", "processMessage:  has attachment");
                    type = getAttachmentsType(qbChatMessage);
                    if (type.equals(QBAttachment.PHOTO_TYPE)) {
                        Collection<QBAttachment> attachments = qbChatMessage.getAttachments();
                        QBAttachment attachment = attachments.iterator().next();
                        String url = attachment.getUrl();
                        UserData user = getUserDataFromSenderId(senderId);
                        if (user != null) {
//                            setImageFilePathForReceived(url, user);
//                            updateDialogsAdapter();
                        }
                    }
                }
            }
        }
    }
}
