package com.brasco.simwechat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.countrypicker.CountryPicker;
import com.brasco.simwechat.countrypicker.CountryPickerListener;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.User;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.utils.qb.callback.QbEntityCallbackTwoTypeWrapper;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.ResourceUtil;
import com.brasco.simwechat.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

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

    private QBUser me;
    private DatabaseReference mUserReference;
    private String mFirebaseUserId;
    private User mFirebaseUser;
    private ValueEventListener mUsersListener;
    private MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUserId);

        myProgressDialog = new MyProgressDialog(this, 0);

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
                setUserRegion(name);
                m_CountryPicker.dismiss();
            }
        });

        ActionBar("My Profile");

        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQ_PHOTO_FILE) {
                LogUtil.writeDebugLog(TAG, "onActivityResult", "onActivityResult from CameraActivity");
                String path = data.getStringExtra(Constant.EK_URL);
                setAvatar(path);
                File image = new File(path);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800,800, true);
                m_ProfileImage.setImageBitmap(bitmap);
                ResourceUtil.setVideoExtension(FilenameUtils.getExtension(path));
            } else if (requestCode == Constant.REQ_INPUT_VALUE) {
                int type = data.getIntExtra(Constant.REQ_INPUT_TYPE, 0);
                String strValue = data.getStringExtra(Constant.REQ_INPUT_STRING);
                if (type == 0) {
                    m_txtName.setText(strValue);
                    setUserName(strValue);
                } else {
                    m_txtWhatsup.setText(strValue);
                    writeUserInfo("comment", strValue);
                }
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
            writeUserInfo("gender", "Male");
        } else {
            m_maleButton.setChecked(false);
            m_femaleButton.setChecked(true);
            m_genderType = Utils.Gender.FEMALE;
            m_txtGender.setText("Female");
            writeUserInfo("gender", "Female");
        }
        m_genderDialog.dismiss();
    }
    private void initUI(){
        me = QBData.curQBUser;
        if (me != null) {
            m_txtName.setText(me.getFullName());
            m_txtId.setText(me.getLogin());
            String country = me.getWebsite();
            if (country != null) {
                country = country.substring(country.indexOf("//") + 2);
                m_txtRegion.setText(country);
            }
            String strUrl = me.getCustomData();
            if (strUrl != null && !strUrl.isEmpty()) {
                setAvatarImage(strUrl);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_select_avatar);
                m_ProfileImage.setImageBitmap(bm);
            }
        }
    }
    private void setAvatarImage(String fileUrl) {
        Glide.with(this)
                .load(fileUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Bitmap bm = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.button_select_avatar);
                        m_ProfileImage.setImageBitmap(bm);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
                .error(R.drawable.ic_error)
                .into(m_ProfileImage);
    }
    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                m_txtGender.setText(user.getGender());
                m_txtWhatsup.setText(user.getWhatsUp());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(ProfileActivity.this, "Failed to load user.", Toast.LENGTH_SHORT).show();
            }
        };
        mUserReference.addValueEventListener(postListener);
        mUsersListener = postListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mUsersListener != null) {
            mUserReference.removeEventListener(mUsersListener);
        }
    }

    private void writeUserInfo(String field, String value) {
        mUserReference.child(field).setValue(value);
    }
    private void setAvatar(String filepath){
        File file  = new File(filepath);
        myProgressDialog.show();
        QBContent.uploadFileTask(file, true, null, null).performAsync(new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(null)
        {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "setAvatar", "onSuccess");
                Integer uploadedFileID = qbFile.getId();
                String pubUri = qbFile.getPublicUrl();
                QBData.curQBUser.setOldPassword(QBData.curQBUser.getPassword());
                QBData.curQBUser.setFileId(uploadedFileID);
                QBData.curQBUser.setCustomData(pubUri);
                QBUsers.updateUser(QBData.curQBUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        String url = qbUser.getCustomData();
                        myProgressDialog.hide();
                        //setAvatarImage(url);
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        myProgressDialog.hide();
//                        Toaster.longToast(e.getErrors().get(0));
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
//                Toaster.longToast(e.getErrors().get(0));
                myProgressDialog.hide();
            }
        });
    }
    private void setUserName(String name){
        myProgressDialog.show();
        QBData.curQBUser.setOldPassword(QBData.curQBUser.getPassword());
        QBData.curQBUser.setFullName(name);
        QBUsers.updateUser(QBData.curQBUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                String name = qbUser.getFullName();
                myProgressDialog.hide();
            }
            @Override
            public void onError(QBResponseException e) {
                myProgressDialog.hide();
//                Toaster.longToast(e.getErrors().get(0));
            }
        });
    }
    private void setUserRegion(String country){
        myProgressDialog.show();
        QBData.curQBUser.setOldPassword(QBData.curQBUser.getPassword());
        QBData.curQBUser.setWebsite(country);
        QBUsers.updateUser(QBData.curQBUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                String name = qbUser.getWebsite();
                myProgressDialog.hide();
            }
            @Override
            public void onError(QBResponseException e) {
                myProgressDialog.hide();
//                Toaster.longToast(e.getErrors().get(0));
            }
        });
    }
}
