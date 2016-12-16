package com.brasco.simwechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.Utils;
import com.quickblox.core.helper.StringifyArrayList;

import java.util.List;

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

    private UserData m_curUser = null;

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
         initUI();
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
        AppGlobals.mainActivity.startChatActivity(m_curUser);
    }

    private void freeCall() {

    }

    public UserData getUserDataFromUsername(String userId){
        for (int i=0; i< AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            if (user.getUserId().equals(userId))
                return user;
        }
        return null;
    }
    private void initUI(){
        m_curUser = getUserDataFromUsername(m_ContactUserId);
        if (m_curUser != null){
            m_txtProfileName.setText(m_curUser.getFullName());
            m_txtProfileId.setText(m_curUser.getUserId());
            String country = m_curUser.getQBUser().getWebsite();
            if (country != null) {
                country = country.substring(country.indexOf("//") + 2);
                m_txtRegion.setText(country);
            }
        }
    }
}
