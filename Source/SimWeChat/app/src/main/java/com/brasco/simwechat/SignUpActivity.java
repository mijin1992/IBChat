package com.brasco.simwechat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

//import com.bumptech.glide.Glide;
//import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
//import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
//import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_PICKER = 100;
    private ImageButton m_btnSelectAvatar = null;
//    private ArrayList<Image> m_imgAvatarList = new ArrayList<>();
    private EditText m_txtFullName = null;
    private LinearLayout m_btnSelectCountry = null;
    private EditText m_txtDialCode = null;
    private EditText m_txtMobileNumber = null;
    private EditText m_txtPassword = null;
    private ToggleButton m_btnShowPassword = null;
    private Button m_btnSignUp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        m_btnSelectAvatar = (ImageButton) findViewById(R.id.btn_select_avatar);
        m_txtFullName = (EditText) findViewById(R.id.txt_full_name);
        m_btnSelectCountry = (LinearLayout) findViewById(R.id.btn_select_country);
        m_txtDialCode = (EditText) findViewById(R.id.txt_dial_code);
        m_txtMobileNumber = (EditText) findViewById(R.id.txt_phone_number);
        m_txtPassword = (EditText) findViewById(R.id.txt_input_password);
        m_btnShowPassword = (ToggleButton) findViewById(R.id.btn_show_password);
        m_btnSignUp = (Button) findViewById(R.id.button_sign_up);
        m_btnSelectAvatar.setOnClickListener(this);
        m_btnSelectCountry.setOnClickListener(this);
        m_btnShowPassword.setOnClickListener(this);
        m_btnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
//            m_imgAvatarList = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
//            if (m_imgAvatarList.size() > 0) {
//                Image imgAvatar = m_imgAvatarList.get(0);
//                String imgPath = imgAvatar.getPath();
//                Glide.with(this).load(Uri.fromFile(new File(imgPath))).into(m_btnSelectAvatar);
//            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_avatar:
                showImageFileChooser();
                break;
            case R.id.btn_select_country:
                break;
            case R.id.btn_show_password:
                break;
            case R.id.button_sign_up:
                break;
        }
    }

    private void showImageFileChooser() {
//        ImagePicker.create(this)
//                .folderMode(true) // folder mode (false by default)
//                .folderTitle("Folder") // folder selection title
//                .imageTitle("Select Avatar") // image selection title
//                .single() // single mode
//                .multi() // multi mode (default mode)
//                .limit(1) // max images can be selected (99 by default)
//                .showCamera(true) // show camera or not (true by default)
//                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .origin(m_imgAvatarList) // original selected images, used in multi mode
//                .start(REQUEST_CODE_PICKER); // start image picker activity with request code

    }
}
