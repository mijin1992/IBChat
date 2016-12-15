package com.brasco.simwechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brasco.simwechat.utils.Utils;

public class ContactProfileActivity extends IBActivity implements View.OnClickListener {

    private String m_ContactUserId = "";

    private ImageView m_ivProfileImage = null;
    private TextView m_txtProfileName = null;
    private ImageView m_ivProfileGender = null;
    private TextView m_txtProfileId = null;
    private TextView m_txtRegion = null;
    private LinearLayout m_btnAlbum = null;
    private TextView m_txtWhatsup = null;
    private Button m_btnMessage = null;
    private Button m_btnCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        m_ContactUserId = intent.getStringExtra(Utils.KEY_USER_ID);
        setContentView(R.layout.activity_contact_profile);

        m_ivProfileImage = (ImageView) findViewById(R.id.profile_image);
        m_txtProfileName = (TextView) findViewById(R.id.profile_name);
        m_ivProfileGender = (ImageView) findViewById(R.id.profile_gender);
        m_txtProfileId = (TextView) findViewById(R.id.txt_id);
        m_txtRegion = (TextView) findViewById(R.id.profile_region);
        m_btnAlbum = (LinearLayout) findViewById(R.id.button_album);
        m_txtWhatsup = (TextView) findViewById(R.id.profile_what);
        m_btnMessage = (Button) findViewById(R.id.button_message);
        m_btnCall = (Button) findViewById(R.id.button_call);

        m_btnAlbum.setOnClickListener(this);
        m_btnMessage.setOnClickListener(this);
        m_btnCall.setOnClickListener(this);

        ActionBar("Profile");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_album:
                break;
            case R.id.button_message:
                sendMessage();
                break;
            case R.id.button_call:
                freeCall();
                break;
        }
    }

    private void sendMessage() {
        Intent intent = new Intent(ContactProfileActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    private void freeCall() {

    }
}
