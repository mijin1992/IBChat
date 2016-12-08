package com.brasco.simwechat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_PICKER = 100;
    private ImageButton m_btnSelectAvatar = null;
    private ArrayList<Image> m_imgAvatarList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        m_btnSelectAvatar = (ImageButton) findViewById(R.id.btn_select_avatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            m_imgAvatarList = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            if (m_imgAvatarList.size() > 0) {
                Image imgAvatar = m_imgAvatarList.get(0);
                String imgPath = imgAvatar.getPath();
                Glide.with(this).load(Uri.fromFile(new File(imgPath))).into(m_btnSelectAvatar);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_avatar:
                showImageFileChooser();
                break;
        }
    }

    private void showImageFileChooser() {
        ImagePicker.create(this)
                .folderMode(true) // folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Select Avatar") // image selection title
                .single() // single mode
                .multi() // multi mode (default mode)
                .limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .origin(m_imgAvatarList) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code

    }
}
