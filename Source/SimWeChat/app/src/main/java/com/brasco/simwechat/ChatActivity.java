package com.brasco.simwechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.activity.BaseActivity;
import com.brasco.simwechat.quickblox.adapter.ChatAdapter;
import com.brasco.simwechat.quickblox.adapter.ChatAttachmentPreviewAdapter;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.managers.QbChatDialogMessageListenerImp;
import com.brasco.simwechat.quickblox.managers.QbDialogHolder;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.PaginationHistoryListener;
import com.brasco.simwechat.quickblox.utils.qb.VerboseQbChatConnectionListener;
import com.brasco.simwechat.utils.AudioRecorder;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.ResourceUtil;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class ChatActivity extends BaseActivity implements View.OnClickListener
        , EmojiconGridFragment.OnEmojiconClickedListener
        , EmojiconsFragment.OnEmojiconBackspaceClickedListener
{
    public static final String TAG = "ReceivedActivity";
    public static final String EXTRA_DIALOG_ID = "dialogId";
    public static final String IS_DRAFT_MESSAGE = "is_draft_message";
    public static final String STRING_DRAFT_MESSAGE = "string_draft_message";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private static final String PROPERTY_NOT_SAVE_TO_HISTORY = "no_save_to_history";
    private ChatAdapter chatAdapter;
    private ChatAttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;
    private QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ChatMessageListener chatMessageListener;

    private EditText m_txtMessage = null;
    private ImageButton m_btnAudio = null;
    private ImageButton m_btnEmoji = null;
    private ImageButton m_btnSend = null;
    private FrameLayout m_emojiconLayout;
    private ListView m_chatListView;
    private TextView m_btnToTalk;

    private boolean m_bEmojiKeyboard = false;
    private String mReceivedImagePath = "";
    private String sendFilePath;
    private MyProgressDialog progressDialog;
    private AppPreference mPrefs;
    private ChatActivity instance;
    private boolean isAudioInputType;
    private AudioRecorder m_audioRecorder;


    public static void startForResult(Activity activity, int code, String dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);
        setContentView(R.layout.activity_chat);

        LogUtil.writeDebugLog(TAG, "onCreate", "start");
        instance = this;
        mPrefs = new AppPreference(this);
        progressDialog = new MyProgressDialog(this, 0);
        UserData user = AppGlobals.curChattingUser;

        m_txtMessage = (EditText) findViewById(R.id.txt_chat);
        m_emojiconLayout = (FrameLayout) findViewById(R.id.emojicons);
        m_btnAudio = (ImageButton) findViewById(R.id.button_record_audio);
        m_btnEmoji = (ImageButton) findViewById(R.id.button_emoji);
        m_btnSend = (ImageButton) findViewById(R.id.button_send);
        m_chatListView = (ListView) findViewById(R.id.list_chat_view);
        m_btnToTalk = (TextView) findViewById(R.id.btn_to_talk);
        m_btnToTalk.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    m_btnToTalk.setBackgroundResource(R.drawable.bg_hold_to_talk_pressed);
                    String fileName = ResourceUtil.getCaptureAudioFilePath(instance);
                    m_audioRecorder = new AudioRecorder();
                    try {
                        m_audioRecorder.startRecording(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    m_btnToTalk.setBackgroundResource(R.drawable.bg_hold_to_talk_default);
                    try {
                        m_audioRecorder.stop();
                        File audioMessage = new File(m_audioRecorder.getOutfilePath());
                        attachmentPreviewAdapter.add(audioMessage);
                        progressDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        m_btnToTalk.setOnClickListener(this);
        m_txtMessage.setOnClickListener(this);
        m_btnAudio.setOnClickListener(this);
        m_btnEmoji.setOnClickListener(this);
        m_btnSend.setOnClickListener(this);

        ActionBar("Message");

        qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(
                getIntent().getStringExtra(EXTRA_DIALOG_ID));
        chatMessageListener = new ChatMessageListener();

        qbChatDialog.addMessageListener(chatMessageListener);
        initChatConnectionListener();
        initViews();
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
    private void showEmojiconFragment(boolean isVisible) {
        if (isVisible) {
            getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, EmojiconsFragment.newInstance(false)).commit();
            m_emojiconLayout.setVisibility(View.VISIBLE);
        }
        else
            m_emojiconLayout.setVisibility(View.GONE);
    }
    private void sendDialogId() {
        LogUtil.writeDebugLog(TAG, "sendDialogId", "1");
        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_to_talk:
                break;
            case R.id.button_record_audio:
                isAudioInputType = !isAudioInputType;
                setAudioMessage();
                break;
            case R.id.txt_chat:
                showEmojiconFragment(false);
                break;
            case R.id.button_emoji:
                m_bEmojiKeyboard = !m_bEmojiKeyboard;
                showEmojiconFragment(m_bEmojiKeyboard);
                if (m_bEmojiKeyboard) {
                    m_txtMessage.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_txtMessage.getWindowToken(), 0);
                } else {
                    m_txtMessage.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(m_txtMessage, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case R.id.button_send:
                onSendChatClick();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        LogUtil.writeDebugLog(TAG, "onBackPressed", "1");
        releaseChat();
        sendDialogId();
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_right, R.anim.out_right);
    }
    public void onSendChatClick() {
        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(null, attachment);
                }
            } else {
                Toaster.shortToast(R.string.chat_wait_for_attachments_to_upload);
            }
        }

        String text = m_txtMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
        }
    }

    public void showMessage(QBChatMessage message) {
        if (chatAdapter != null) {
            chatAdapter.add(message);
            chatAdapter.notifyDataSetChanged();
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void initViews() {
        LogUtil.writeDebugLog(TAG, "initViews", "1");
        attachmentPreviewAdapter = new ChatAttachmentPreviewAdapter(this,
                new ChatAttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                    }
                },
                new ChatAttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                    }
                });

        isAudioInputType = true;
        setAudioMessage();
    }
    private void setAudioMessage(){
        if (isAudioInputType){
            m_btnAudio.setImageResource(R.drawable.ic_key);
            m_txtMessage.setEnabled(false);
            m_txtMessage.setVisibility(View.INVISIBLE);
            m_btnEmoji.setEnabled(false);
            m_btnEmoji.setVisibility(View.INVISIBLE);
            m_btnSend.setEnabled(false);
            m_btnSend.setVisibility(View.INVISIBLE);
            m_btnToTalk.setEnabled(true);
            m_btnToTalk.setVisibility(View.VISIBLE);
        } else {
            m_btnAudio.setImageResource(R.drawable.ic_audio_btn);
            m_txtMessage.setEnabled(true);
            m_txtMessage.setVisibility(View.VISIBLE);
            m_btnEmoji.setEnabled(true);
            m_btnEmoji.setVisibility(View.VISIBLE);
            m_btnSend.setEnabled(true);
            m_btnSend.setVisibility(View.VISIBLE);
            m_btnToTalk.setEnabled(false);
            m_btnToTalk.setVisibility(View.INVISIBLE);
        }
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        LogUtil.writeDebugLog(TAG, "sendChatMessage", "start");
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);
        } else {
            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()){
            Toaster.shortToast("You're still joining a group chat, please wait a bit");
            return;
        }

        try {
            LogUtil.writeDebugLog(TAG, "sendChatMessage", "qbChatDialog.sendMessage");
            if (qbChatDialog == null)
                Toaster.shortToast("Can't send a message, You are not connected to chat. Please login again.");
            qbChatDialog.sendMessage(chatMessage);

            if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                showMessage(chatMessage);
                inserRecentMessagesArray(chatMessage, qbChatDialog);
            }

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                m_txtMessage.setText("");
            }
        } catch (SmackException.NotConnectedException e) {
            Toaster.shortToast("Can't send a message, You are not connected to chat. Please login again.");
        }
    }

//    private void insertSentMessagesArray(QBChatMessage chatMessage, QBChatDialog dialog ){
//        if(chatMessage.getBody() != null) {
//
//            String otherUsername = AppGlobals.curChattingUser.getUserName();
//            for (int i = 0; i < AppGlobals.sentMessageData.size(); i++) {
//                RecentMessageData data = AppGlobals.sentMessageData.get(i);
//                if (data.getUsername().equals(otherUsername)) {
//                    AppGlobals.sentMessageData.remove(i);
//                    break;
//                }
//            }
//            String message = chatMessage.getBody();
//            Date date = new Date();
//            long time = date.getTime();
//            RecentMessageData sendMessage = new RecentMessageData("null", "null", otherUsername, message, time);
//            AppGlobals.sentMessageData.add(0, sendMessage);
//        }
//    }

    private void initChat() {
        LogUtil.writeDebugLog(TAG, "initChat", "1");
        switch (qbChatDialog.getType()) {
            case PRIVATE:
                loadDialogUsers();
                break;
            default:
                Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name()));
                finish();
                break;
        }
    }

    private void releaseChat() {
        LogUtil.writeDebugLog(TAG, "releaseChat", "1");
        qbChatDialog.removeMessageListrener(chatMessageListener);
        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
        }
    }
    private void updateDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().updateDialogUsers(qbChatDialog, selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        qbChatDialog = dialog;
                        loadDialogUsers();
                    }
                    @Override
                    public void onError(QBResponseException e) {  }
                }
        );
    }

    private void loadDialogUsers() {
        LogUtil.writeDebugLog(TAG, "loadDialogUsers", "start");
        ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "loadDialogUsers", "onSuccess");
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "loadDialogUsers", "onError");
            }
        });
    }

    private void loadChatHistory() {
        LogUtil.writeDebugLog(TAG, "loadChatHistory", "start");
        progressDialog.show();
        ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                LogUtil.writeDebugLog(TAG, "loadChatHistory", "onSuccess");
                progressDialog.hide();
                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(ChatActivity.this, qbChatDialog, messages);
                    chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                        @Override
                        public void downloadMore() {
                            loadChatHistory();
                        }
                    });
                    chatAdapter.setOnItemInfoExpandedListener(new ChatAdapter.OnItemInfoExpandedListener() {
                        @Override
                        public void onItemInfoExpanded(final int position) {
                            if (isLastItem(position)) {
                                // HACK need to allow info textview visibility change so posting it via handler
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mListViewChat.setSelection(position);
                                        m_chatListView.smoothScrollToPosition(position);
                                    }
                                });
                            } else {
                                m_chatListView.smoothScrollToPosition(position);
                            }
                        }
                        private boolean isLastItem(int position) {
                            return position == chatAdapter.getCount() - 1;
                        }
                    });
                    if (unShownMessages != null && !unShownMessages.isEmpty()) {
                        List<QBChatMessage> chatList = chatAdapter.getList();
                        for (QBChatMessage message : unShownMessages) {
                            if (!chatList.contains(message)) {
                                chatAdapter.add(message);
                            }
                        }
                    }
                    m_chatListView.setAdapter(chatAdapter);
                    m_chatListView.setDivider(null);
                } else {
                    chatAdapter.addList(messages);
                    m_chatListView.setSelection(messages.size());
                }
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "loadChatHistory", "onError");
                progressDialog.hide();
                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
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

    private void scrollMessageListDown() {
        m_chatListView.setSelection(m_chatListView.getCount() - 1);
    }

    private void deleteChat() {
        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(QBResponseException e) { }
        });
    }

    private void initChatConnectionListener() {
        LogUtil.writeDebugLog(TAG, "initChatConnectionListener", "1");
        progressDialog.show();
        chatConnectionListener = new VerboseQbChatConnectionListener(getSnackbarAnchorView()) {
            @Override
            public void reconnectionSuccessful() {
                LogUtil.writeDebugLog(TAG, "reconnectionSuccessful", "1");
                super.reconnectionSuccessful();
                skipPagination = 0;
                chatAdapter = null;
                switch (qbChatDialog.getType()) {
                    case PRIVATE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadChatHistory();
                            }
                        });
                        break;
                }
            }
        };
    }
    public void onAttachmentsClick() {
        //new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(Constant.REQ_VIDEO_CAMERAACTIVITY_TYPE, Constant.REQ_IMAGE_TYPE);
        startActivityForResult(intent, Constant.REQ_PHOTO_FILE);
    }
    public void hideProgressDialog(){
        if (progressDialog.isShowing())
            progressDialog.hide();
    }
    public void showProgressDialog(){
        progressDialog.show();
    }
    public void sendAttachedFile(){
        onSendChatClick();
    }
    private void inserRecentMessagesArray(QBChatMessage chatMessage, QBChatDialog dialog ){
        LogUtil.writeDebugLog(TAG, "insertSentMessagesArray", "1");
        String message = chatMessage.getBody();
        if (message != null && !message.isEmpty()){
            String otherUserId = AppGlobals.curChattingUser.getUserId();
            Boolean exist = false;
            for (int i = 0; i < AppGlobals.mRecentessageArray.size(); i++) {
                RecentMessageData data = AppGlobals.mRecentessageArray.get(i);
                if (data.getUserId().equals(otherUserId)) {
                    LogUtil.writeDebugLog(TAG, "insertSentMessagesArray", "2");
                    exist = true;
                    RecentMessageData recentMessageData = AppGlobals.mRecentessageArray.get(i);
                    recentMessageData.setMessage(message);
                    Date date = new Date();
                    long time = date.getTime();
                    recentMessageData.setTime(time);
                    AppGlobals.mRecentessageArray.remove(i);
                    AppGlobals.mRecentessageArray.add(0, recentMessageData);
                    break;
                }
            }
            if (exist == false) {
                LogUtil.writeDebugLog(TAG, "insertSentMessagesArray", "3");
                Date date = new Date();
                long time = date.getTime();
                RecentMessageData recentMessageData = new RecentMessageData(otherUserId
                        , AppGlobals.curChattingUser.getFullName(), message, time
                        , AppGlobals.curChattingUser.getQBUser().getCustomData());

                AppGlobals.mRecentessageArray.add(0, recentMessageData);
            }
            mPrefs.setRecentMessagesArray(AppGlobals.mRecentessageArray);
        }
    }
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(m_txtMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(m_txtMessage);
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(EXTRA_DIALOG_ID));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.writeDebugLog(TAG, "onResume", "1");
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.writeDebugLog(TAG, "onPause", "1");
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onSessionCreated(boolean success) {
        LogUtil.writeDebugLog(TAG, "onSessionCreated", "1");
        if (success) {
            initChat();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogUtil.writeDebugLog(TAG, "onActivityResult", "1");
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQ_PHOTO_FILE) {
                sendFilePath = data.getStringExtra(Constant.EK_URL);
                LogUtil.writeDebugLog(TAG, "onActivityResult", "ok");
                File file = new File(sendFilePath);
                attachmentPreviewAdapter.add(file);
                progressDialog.show();
            }
        }
    }

    public class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            if (qbChatMessage.getBody() != null && (qbChatMessage.getBody().equals(Constant.SEND_VIDEO_VIEW_REQUEST)
                    || qbChatMessage.getBody().equals(Constant.SEND_VIDEO_SHOW_END)
                    || qbChatMessage.getBody().equals(Constant.SEND_VIDEO_SHOW_START)
            ) ){

            } else {
                showMessage(qbChatMessage);
            }
        }
    }

}
