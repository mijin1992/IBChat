package com.brasco.simwechat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.countrypicker.Country;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.DataHolder;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.services.CallService;
import com.brasco.simwechat.quickblox.utils.SharedPreferencesUtil;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends IBActivity implements View.OnClickListener {
    private static final String TAG = LogInActivity.class.getSimpleName();
    private Button m_btnLogin = null;
    private Button m_btnSignUp = null;
    private Button m_btnForgotPasswd = null;
    private EditText m_txtUserId = null;
    private EditText m_txtPassword = null;

    private boolean m_bCheckPermission = false;
    private AppPreference mPrefs;
    protected MyProgressDialog progressDialog;
    QBUser mQBUser;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mPrefs = new AppPreference(this);

        if (!m_bCheckPermission) {
            Log.d(TAG, "permission NOT checked");
            checkPermissions();
        }

        m_btnLogin = (Button) findViewById(R.id.btn_login);
        m_btnLogin.setEnabled(false);
        m_btnForgotPasswd = (Button) findViewById(R.id.btn_forgot_password);
        m_btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        m_btnLogin.setOnClickListener(this);
        m_btnForgotPasswd.setOnClickListener(this);
        m_btnSignUp.setOnClickListener(this);

        m_txtUserId = (EditText) findViewById(R.id.txt_user_id);
        m_txtUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        m_txtPassword = (EditText) findViewById(R.id.txt_user_password);
        m_txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String savedUsername = mPrefs.getQuickBloxUsername();
        String savedPassword = mPrefs.getQuickBloxUserPass();
        if (savedUsername != null && !savedUsername.isEmpty()){
            m_txtUserId.setText(savedUsername);
            m_txtPassword.setText(savedPassword);
        }
        progressDialog = new MyProgressDialog(this, 0);
        progressDialog.show();
        createSession();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                signIn();
                break;
            case R.id.btn_forgot_password:
                performForgotPassword();
                break;
            case R.id.btn_sign_up:
                signUp();
                break;
        }
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if  (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Request permission");
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO }, 800);
            } else {
                Log.d(TAG, "Permission granted");
                m_bCheckPermission = true;
            }
        }
        else {
            Log.d(TAG, "Android < 6.0");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    private void performForgotPassword() {
    }

    private void signUp() {
        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void textChanged() {
        if (m_txtUserId.getText().toString().trim().isEmpty() || m_txtPassword.getText().toString().trim().isEmpty()) {
            m_btnLogin.setEnabled(false);
        } else {
            m_btnLogin.setEnabled(true);
        }
    }

    public void signIn() {
        String login = m_txtUserId.getText().toString().trim();
        String password = m_txtPassword.getText().toString();

        if (!isValidData(login, password)) {
            return;
        }
        mQBUser = getQBUserFromUsername(m_txtUserId.getText().toString());
        if (mQBUser == null){
            Toaster.longToast("Invalid user....");
            return;
        }
        progressDialog.show();
        mQBUser.setPassword(m_txtPassword.getText().toString());
        LogUtil.writeDebugLog(TAG, "signIn", "startLoginService");
        startLoginService(mQBUser);
    }

    private void QBUserSignIn(QBUser user){
        LogUtil.writeDebugLog(TAG, "QBUserSignIn", "start");
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                LogUtil.writeDebugLog(TAG, "QBUserSignIn", "onSuccess");
                progressDialog.dismiss();
                for (int i = 0; i < QBData.qbUsers.size(); i++){
                    QBUser user = QBData.qbUsers.get(i);
                    if (user.getLogin().equals(mQBUser.getLogin())){
                        mQBUser = user;
                        DataHolder.getInstance().addQbUser(mQBUser);
                        DataHolder.getInstance().setSignInQbUser(mQBUser);
                        SharedPreferencesUtil.saveQbUser(mQBUser);

                        QBData.curQBUser = mQBUser;
                        setResult(RESULT_OK);

                        mPrefs.setQuickBloxUsername(mQBUser.getLogin());
                        mPrefs.setQuickBloxUserPass(mQBUser.getPassword());

                        firebaseSignIn();

                        break;
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtil.writeDebugLog(TAG, "QBUserSignIn", "onError");
                progressDialog.dismiss();
                Toaster.longToast(e.getMessage());
            }
        });
    }

    private boolean isValidData(String login, String password) {
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(login)) {
                m_txtUserId.setError("Please input user name.");
            }
            if (TextUtils.isEmpty(password)) {
                m_txtPassword.setError("Please input user password");
            }
            return false;
        }
        return true;
    }

    protected void startLoginService(QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "startLoginService", "start");
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Constant.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.EXTRA_LOGIN_RESULT_CODE) {
            LogUtil.writeDebugLog(TAG, "onActivityResult", "after startLoginService");
            boolean isLoginSuccess = data.getBooleanExtra(Constant.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Constant.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(mQBUser);
                QBUserSignIn(mQBUser);
            } else {
                LogUtil.writeDebugLog(TAG, "onActivityResult", "startLoginService error");
                Toaster.longToast(getString(R.string.login_chat_login_error) + errorMessage);
            }
        }
    }

    public QBUser getQBUserFromUsername(String username){
        LogUtil.writeDebugLog(TAG, "getQBUserFromUsername", username);
        if(QBData.qbUsers != null) {
            for (int i = 0; i < QBData.qbUsers.size(); i++) {
                QBUser user = QBData.qbUsers.get(i);
                if (user.getLogin().equals(username))
                    return user;
            }
        }
        return null;
    }
    private void saveUserData(QBUser qbUser) {
        LogUtil.writeDebugLog(TAG, "saveUserData", qbUser.getLogin());
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
//        sharedPrefsHelper.save(Constant.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private void createSession() {
        LogUtil.writeDebugLog(TAG, "createSession", "1");
        QBChatService chatService = QBChatService.getInstance();
        chatService.setDebugEnabled(true);

        QBChatService.ConfigurationBuilder chatserviceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatserviceConfigurationBuilder.setSocketTimeout(10000);
        QBChatService.setConfigurationBuilder(chatserviceConfigurationBuilder);

        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                LogUtil.writeDebugLog(TAG, "createSession", "onSuccess");
                List<String> tags = new ArrayList<>();
                tags.add(Constant.QB_USERS_TAG);
                QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                        progressDialog.hide();
                        LogUtil.writeDebugLog(TAG, "createSession", "getUsers_onSuccess");
                        for (int i=0; i< result.size();i ++){
                            LogUtil.writeDebugLog(TAG, "createSession", "getUsers_onSuccess: username : " + result.get(i).getLogin());
                        }
                        QBData.qbUsers = result;
                        int count = result.size();
                        String username = mPrefs.getQuickBloxUsername();
                        String userpass = mPrefs.getQuickBloxUserPass();
///////////////////////////////////// goto Login //////////////////////////////////////////////////
                        if (username != null && !username.isEmpty()){
                            m_txtUserId.setText(username);
                            m_txtPassword.setText(userpass);
                            signIn();
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        progressDialog.hide();
                        LogUtil.writeDebugLog(TAG, "createSession", "getUsers_onError");
                        Toaster.longToast("Loading users error!");
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
                progressDialog.hide();
                LogUtil.writeDebugLog(TAG, "createSession", "onError");
                Toaster.longToast("Creat Session error!");
            }
        });
    }

    private void firebaseSignIn() {
        Log.d(TAG, "signIn");

        progressDialog.show();
        String email = mQBUser.getEmail();
        String password = m_txtPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, Constant.FIREBASE_DEFAULT_PASS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LogInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void onAuthSuccess(FirebaseUser user) {
        Intent intent= new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
