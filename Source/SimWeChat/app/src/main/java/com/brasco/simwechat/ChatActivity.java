package com.brasco.simwechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class ChatActivity extends IBActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener  {
public static final String EXTRA_DIALOG_ID = "dialogId";
    public static final String IS_DRAFT_MESSAGE = "is_draft_message";
    public static final String STRING_DRAFT_MESSAGE = "string_draft_message";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private EditText m_txtMessage = null;
    private ImageButton m_btnAudio = null;
    private ImageButton m_btnEmoji = null;
    private ImageButton m_btnSend = null;
    private FrameLayout m_emojiconLayout;

    private boolean m_bEmojiKeyboard = false;

public static void startForResult(Activity activity, int code, String dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        //intent.putExtra(Constant.EK_URL, videoPath);
        //intent.putExtra(ChatActivity.IS_DRAFT_MESSAGE, isDraftMessage);
//        if (isDraftMessage)
//            intent.putExtra(ChatActivity.STRING_DRAFT_MESSAGE, draftMessage);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        m_txtMessage = (EditText) findViewById(R.id.txt_chat);
        m_emojiconLayout = (FrameLayout) findViewById(R.id.emojicons);
        m_btnAudio = (ImageButton) findViewById(R.id.button_record_audio);
        m_btnEmoji = (ImageButton) findViewById(R.id.button_emoji);
        m_btnSend = (ImageButton) findViewById(R.id.button_send);
        m_txtMessage.setOnClickListener(this);
        m_btnAudio.setOnClickListener(this);
        m_btnEmoji.setOnClickListener(this);
        m_btnSend.setOnClickListener(this);

        ActionBar("Message");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_record_audio:
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
                break;
        }
    }

    private void showEmojiconFragment(boolean isVisible) {
        if (isVisible) {
            getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, EmojiconsFragment.newInstance(false)).commit();
            m_emojiconLayout.setVisibility(View.VISIBLE);
        }
        else
            m_emojiconLayout.setVisibility(View.GONE);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(m_txtMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(m_txtMessage);
    }
}
