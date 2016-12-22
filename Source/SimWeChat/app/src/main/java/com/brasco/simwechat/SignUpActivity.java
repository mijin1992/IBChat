package com.brasco.simwechat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.bumptech.glide.Glide;
//import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
//import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
//import com.nguyenhoanglam.imagepicker.model.Image;

import com.brasco.simwechat.countrypicker.CountryPicker;
import com.brasco.simwechat.countrypicker.CountryPickerListener;
import com.brasco.simwechat.model.User;
import com.brasco.simwechat.utils.Utils;
import com.bumptech.glide.util.Util;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.DataHolder;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.ResourceUtil;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.services.CallService;
import com.brasco.simwechat.quickblox.utils.SharedPreferencesUtil;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.callback.QbEntityCallbackTwoTypeWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends IBActivity implements View.OnClickListener {
    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICKER = 100;
    private ImageButton m_btnSelectAvatar = null;
//    private ArrayList<Image> m_imgAvatarList = new ArrayList<>();
    private EditText m_txtFullName = null;
    private EditText m_txtId = null;
    private LinearLayout m_btnSelectCountry = null;
    private TextView m_txtCountry = null;
    private EditText m_txtDialCode = null;
    private EditText m_txtMobileNumber = null;
    private EditText m_txtPassword = null;
    private EditText m_txtEmail = null;
    private ToggleButton m_btnShowPassword = null;
    private Button m_btnSignUp = null;

    private boolean m_bShowPassword = false;
    private CountryPicker m_CountryPicker = null;
    private QBUser userForSave;
    private MyProgressDialog myProgressDialog;
    QBUser mQBUser;
    private String mLogoImagePath = "";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        myProgressDialog = new MyProgressDialog(this, 0);

        m_btnSelectAvatar = (ImageButton) findViewById(R.id.btn_select_avatar);
        m_txtFullName = (EditText) findViewById(R.id.txt_full_name);
        m_txtId = (EditText) findViewById(R.id.txt_id);
        m_btnSelectCountry = (LinearLayout) findViewById(R.id.btn_select_country);
        m_txtCountry = (TextView) findViewById(R.id.txt_country);
        m_txtDialCode = (EditText) findViewById(R.id.txt_dial_code);
        m_txtMobileNumber = (EditText) findViewById(R.id.txt_phone_number);
        m_txtPassword = (EditText) findViewById(R.id.txt_input_password);
        m_txtEmail = (EditText) findViewById(R.id.txt_input_email);
        m_btnShowPassword = (ToggleButton) findViewById(R.id.btn_show_password);
        m_btnSignUp = (Button) findViewById(R.id.button_sign_up);
        m_btnSelectAvatar.setOnClickListener(this);
        m_btnSelectCountry.setOnClickListener(this);
        m_btnShowPassword.setOnClickListener(this);
        m_btnSignUp.setOnClickListener(this);

        m_CountryPicker = CountryPicker.newInstance("Select Region");
        m_CountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code) {
                m_txtCountry.setText(name);
                String strDialCode = "999";
                if (Utils.Country2DialCode.containsKey(code))
                    strDialCode = Utils.Country2DialCode.get(code);
                m_txtDialCode.setText(strDialCode);
                m_CountryPicker.dismiss();
            }
        });

        ActionBar("Sign Up");
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQ_PHOTO_FILE) {
                LogUtil.writeDebugLog(TAG, "onActivityResult", "onActivityResult from CameraActivity");
                mLogoImagePath = data.getStringExtra(Constant.EK_URL);
                File image = new File(mLogoImagePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800,800, true);
                m_btnSelectAvatar.setImageBitmap(bitmap);
                ResourceUtil.setVideoExtension(FilenameUtils.getExtension(mLogoImagePath));
            }
        } else if (resultCode == Constant.EXTRA_LOGIN_RESULT_CODE) {
            LogUtil.writeDebugLog(TAG, "onActivityResult", "onActivityResult from callservice.");
            boolean isLoginSuccess = data.getBooleanExtra(Constant.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Constant.EXTRA_LOGIN_ERROR_MESSAGE);
            if (isLoginSuccess) {
                saveUserData(userForSave);
                setAvatar();
            } else {
                Toaster.longToast(getString(R.string.login_chat_login_error) + errorMessage);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_avatar:
                showImageFileChooser();
                break;
            case R.id.btn_select_country:
                m_CountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                break;
            case R.id.btn_show_password:
                m_bShowPassword = !m_bShowPassword;
                if (m_bShowPassword)
                    m_txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    m_txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case R.id.button_sign_up:
                firebaseSignUp();
                break;
        }
    }

    private void showImageFileChooser() {
        Intent intent = new Intent(SignUpActivity.this, CameraActivity.class);
        intent.putExtra(Constant.REQ_VIDEO_CAMERAACTIVITY_TYPE, Constant.REQ_IMAGE_TYPE);
        startActivityForResult(intent, Constant.REQ_PHOTO_FILE);
    }

    private void QBUserSignIn(QBUser user){
        LogUtil.writeDebugLog(TAG, "QBUserSignIn", "start");
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "QBUserSignIn", "onSuccess");
                myProgressDialog.hide();
                DataHolder.getInstance().addQbUser(mQBUser);
                DataHolder.getInstance().setSignInQbUser(mQBUser);
                SharedPreferencesUtil.saveQbUser(mQBUser);
                QBData.curQBUser = mQBUser;
                setResult(RESULT_OK, new Intent());

//                Toaster.longToast("You was successfully sign in!");
                Intent intent= new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "QBUserSignIn", "onError");
                myProgressDialog.hide();
                Toaster.longToast(e.getErrors().get(0));
            }
        });
    }

    private void QBUserSingUp(final QBUser user){
        LogUtil.writeDebugLog(TAG, "QBUserSingUp", "start");
        QBUsers.signUpSignInTask(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "QBUserSingUp", "onSuccess");
                mQBUser = qbUser;
                user.setPassword(m_txtPassword.getText().toString().trim());
                loginToChat(qbUser);
            }

            @Override
            public void onError(QBResponseException error) {
                LogUtil.writeDebugLog(TAG, "QBUserSingUp", "onError");
                myProgressDialog.hide();
                Toaster.longToast(error.getErrors().get(0));
            }
        });
    }
    private void loginToChat(final QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "loginToChat", "start");
        qbUser.setPassword(m_txtPassword.getText().toString().trim());
        userForSave = qbUser;
        startLoginService(qbUser);
    }

    public void quickbloxSignUp() {
        LogUtil.writeDebugLog(TAG, "signUp", "start");
        String login = m_txtId.getText().toString().trim();
        String name = m_txtFullName.getText().toString().trim();
        String password = m_txtPassword.getText().toString();
        String email = m_txtEmail.getText().toString();
        String phoneNumber = m_txtMobileNumber.getText().toString();
        String country = m_txtCountry.getText().toString();

        myProgressDialog.show();

        mQBUser = new QBUser();
        mQBUser.setLogin(login);
        mQBUser.setPassword(password);
        mQBUser.setFullName(name);
        mQBUser.setPhone(phoneNumber);
        mQBUser.setEmail(email);
        mQBUser.setWebsite(country);
        QBUserSingUp(mQBUser);
    }

    private void setAvatar(){
        LogUtil.writeDebugLog(TAG, "setAvatar", "start");
        File file  = new File(mLogoImagePath);
        QBContent.uploadFileTask(file, true, null, null).performAsync(new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(null)
        {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "setAvatar", "onSuccess");
                String login = m_txtId.getText().toString().trim();
                String password = m_txtPassword.getText().toString();
                String name = m_txtFullName.getText().toString();
                String phoneNumber = m_txtMobileNumber.getText().toString();
                QBUser user = new QBUser();
                user.setLogin(login);
                Integer id = mQBUser.getId();
                user.setId(id);
                user.setFullName(name);
                Integer uploadedFileID = qbFile.getId();
                String pubUri = qbFile.getPublicUrl();
                user.setFileId(uploadedFileID);
                user.setCustomData(pubUri);
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        LogUtil.writeDebugLog(TAG, "setAvatar", "onSuccess_1");
                        qbUser.setPassword(m_txtPassword.getText().toString().trim());
                        QBUserSignIn(qbUser);
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.writeDebugLog(TAG, "setAvatar", "onError_1");
                        Toaster.longToast(e.getErrors().get(0));
                        myProgressDialog.hide();
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
                Toaster.longToast(e.getErrors().get(0));
                myProgressDialog.hide();
            }
        });
    }
    private boolean isValidData(String login, String password, String name, String phonenumber, String email) {
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(email)) {
            if (TextUtils.isEmpty(login)) {
                m_txtId.setError("Please input user name.");
            }
            if (TextUtils.isEmpty(password)) {
                m_txtPassword.setError("Please input user password");
            }
            if (TextUtils.isEmpty(name)) {
                m_txtFullName.setError("Please input user email");
            }
            if (TextUtils.isEmpty(phonenumber)) {
                m_txtMobileNumber.setError("Please input phone number");
            }
            if (TextUtils.isEmpty(email)) {
                m_txtEmail.setError("Please input email address");
            }
            return false;
        }
        if (mLogoImagePath.isEmpty()){
            Toaster.shortToast("Please set logo image.");
            return false;
        }
        return true;
    }

    private void startLoginService(QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "startLoginService", "start");
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Constant.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
    private void saveUserData(QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "saveUserData", "start");
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
//        sharedPrefsHelper.save(Constant.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private void firebaseSignUp() {
        Log.d(TAG, "signUp");
        String login = m_txtId.getText().toString().trim();
        String name = m_txtFullName.getText().toString().trim();
        String password = m_txtPassword.getText().toString();
        String email = m_txtEmail.getText().toString();
        String phoneNumber = m_txtMobileNumber.getText().toString();
        if (!isValidData(login, password, name, phoneNumber, email)) {
            return;
        }

        myProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        myProgressDialog.hide();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void onAuthSuccess(FirebaseUser user) {
        String username = m_txtId.getText().toString();
        String country = m_txtCountry.getText().toString();
        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(), "Male", country);
        // SignUp to Quickblox
        quickbloxSignUp();
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email, String gender, String country) {
        User user = new User(name, email, gender, country);
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

}
