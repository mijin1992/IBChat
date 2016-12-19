package com.brasco.simwechat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.countrypicker.CountryPicker;
import com.brasco.simwechat.countrypicker.CountryPickerListener;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.ResourceUtil;
import com.brasco.simwechat.utils.Utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ProfileActivity extends IBActivity implements View.OnClickListener {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    LinearLayout m_btnAvatar = null;
    LinearLayout m_btnName = null;
    LinearLayout m_btnGender = null;
    LinearLayout m_btnRegion = null;
    LinearLayout m_btnWhatsup = null;
    ImageView m_ProfileImage = null;
    TextView m_txtName = null;
    TextView m_txtId = null;
    TextView m_txtGender = null;
    TextView m_txtRegion = null;
    TextView m_txtWhatsup = null;
    CountryPicker m_CountryPicker = null;

    Dialog m_genderDialog = null;
    RadioGroup m_genderGroup = null;
    RadioButton m_maleButton = null;
    RadioButton m_femaleButton = null;
    LinearLayout m_btnGenderMale = null;
    LinearLayout m_btnGenderFemale = null;

    Utils.Gender m_genderType = Utils.Gender.MALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        m_btnAvatar = (LinearLayout) findViewById(R.id.button_profile_avatar);
        m_btnName = (LinearLayout) findViewById(R.id.button_profile_name);
        m_btnGender = (LinearLayout) findViewById(R.id.button_profile_gender);
        m_btnRegion = (LinearLayout) findViewById(R.id.button_profile_region);
        m_btnWhatsup = (LinearLayout) findViewById(R.id.button_profile_whatsup);
        m_btnAvatar.setOnClickListener(this);
        m_btnName.setOnClickListener(this);
        m_btnGender.setOnClickListener(this);
        m_btnRegion.setOnClickListener(this);
        m_btnWhatsup.setOnClickListener(this);

        m_ProfileImage = (ImageView) findViewById(R.id.img_profile);
        m_txtName = (TextView) findViewById(R.id.profile_name);
        m_txtId = (TextView) findViewById(R.id.profile_id);
        m_txtGender = (TextView) findViewById(R.id.profile_gender);
        m_txtRegion = (TextView) findViewById(R.id.profile_region);
        m_txtWhatsup = (TextView) findViewById(R.id.profile_what);

        m_genderDialog = new Dialog(this);
        m_genderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_genderDialog.setContentView(R.layout.dialog_gender_pick);

        m_btnGenderMale = (LinearLayout) m_genderDialog.findViewById(R.id.button_male);
        m_btnGenderFemale = (LinearLayout) m_genderDialog.findViewById(R.id.button_female);
        m_btnGenderMale.setOnClickListener(this);
        m_btnGenderFemale.setOnClickListener(this);

        m_genderGroup = (RadioGroup) m_genderDialog.findViewById(R.id.gender);
        m_maleButton = (RadioButton) m_genderDialog.findViewById(R.id.radio_male);
        m_femaleButton = (RadioButton) m_genderDialog.findViewById(R.id.radio_female);
        m_maleButton.setOnClickListener(this);
        m_femaleButton.setOnClickListener(this);

        m_CountryPicker = CountryPicker.newInstance("Select Region");
        m_CountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code) {
                m_txtRegion.setText(name);
                m_CountryPicker.dismiss();
            }
        });

        ActionBar("My Profile");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQ_PHOTO_FILE) {
                LogUtil.writeDebugLog(TAG, "onActivityResult", "onActivityResult from CameraActivity");
                String path = data.getStringExtra(Constant.EK_URL);
                File image = new File(path);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800,800, true);
                m_ProfileImage.setImageBitmap(bitmap);
                ResourceUtil.setVideoExtension(FilenameUtils.getExtension(path));
            } else if (requestCode == Constant.REQ_INPUT_VALUE) {
                int type = data.getIntExtra(Constant.REQ_INPUT_TYPE, 0);
                String strValue = data.getStringExtra(Constant.REQ_INPUT_STRING);
                if (type == 0)
                    m_txtName.setText(strValue);
                else
                    m_txtWhatsup.setText(strValue);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_profile_avatar:
                getPicture();
                break;
            case R.id.button_profile_name:
                getTextValue(0);
                break;
            case R.id.button_profile_gender:
                getGender();
                break;
            case R.id.button_profile_region:
                getRegion();
                break;
            case R.id.button_profile_whatsup:
                getTextValue(1);
                break;
            case R.id.button_male:
                checkGender(true);
                break;
            case R.id.button_female:
                checkGender(false);
                break;
            case R.id.radio_male:
                checkGender(true);
                break;
            case R.id.radio_female:
                checkGender(false);
                break;
        }
    }

    private void getPicture() {
        Intent intent = new Intent(ProfileActivity.this, CameraActivity.class);
        intent.putExtra(Constant.REQ_VIDEO_CAMERAACTIVITY_TYPE, Constant.REQ_IMAGE_TYPE);
        startActivityForResult(intent, Constant.REQ_PHOTO_FILE);
    }

    private void getTextValue(int type) {
        Intent intent = new Intent(ProfileActivity.this, InputActivity.class);
        intent.putExtra(Constant.REQ_INPUT_TYPE, type);
        if (type == 0)
            intent.putExtra(Constant.REQ_INPUT_STRING, m_txtName.getText().toString());
        else
            intent.putExtra(Constant.REQ_INPUT_STRING, m_txtWhatsup.getText().toString());
        startActivityForResult(intent, Constant.REQ_INPUT_VALUE);
    }

    private void getGender() {
        m_genderDialog.show();
    }

    private void getRegion() {
        m_CountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    private void checkGender(boolean checked) {
        if (checked) {
            m_maleButton.setChecked(true);
            m_femaleButton.setChecked(false);
            m_genderType = Utils.Gender.MALE;
            m_txtGender.setText("Male");
        } else {
            m_maleButton.setChecked(false);
            m_femaleButton.setChecked(true);
            m_genderType = Utils.Gender.FEMALE;
            m_txtGender.setText("Female");
        }
        m_genderDialog.dismiss();
    }
}
