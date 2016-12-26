package com.brasco.simwechat;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.model.DataHolder;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.services.CallService;
import com.brasco.simwechat.quickblox.utils.SharedPreferencesUtil;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.utils.LogUtil;
import com.bumptech.glide.Glide;
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

public class SplashActivity extends AppCompatActivity {
    private AppPreference mPrefs;
    protected MyProgressDialog progressDialog;
    QBUser mQBUser;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView m_splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        m_splashImage = (ImageView) findViewById(R.id.splash_image);
        Uri uri = Uri.parse("android.resource://com.brasco.simwechat/" + R.drawable.splashscreen);
        Glide.with(this).load(uri).into(m_splashImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mPrefs = new AppPreference(this);

        progressDialog = new MyProgressDialog(this, 0);
        createSession();
    }

    private void createSession() {
        progressDialog.show();
        QBChatService chatService = QBChatService.getInstance();
        chatService.setDebugEnabled(true);

        QBChatService.ConfigurationBuilder chatserviceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatserviceConfigurationBuilder.setSocketTimeout(100000);
        QBChatService.setConfigurationBuilder(chatserviceConfigurationBuilder);

        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                List<String> tags = new ArrayList<>();
                tags.add(Constant.QB_USERS_TAG);
                QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                        QBData.qbUsers = result;
                        int count = result.size();
                        String username = mPrefs.getQuickBloxUsername();
                        String userpass = mPrefs.getQuickBloxUserPass();
///////////////////////////////////// Login //////////////////////////////////////////////////
                        if (username != null && !username.isEmpty()){
                            signIn(username, userpass);
                        } else {
                            progressDialog.hide();
                            gotoLoginActivity();
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        progressDialog.hide();
                        Toaster.longToast("Loading users error!");
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
                progressDialog.hide();
                Toaster.longToast(e.getErrors().get(0));
            }
        });
    }

    public void signIn(String userId, String pass) {
        String login = userId;
        String password = pass;

        mQBUser = getQBUserFromUsername(login);
        if (mQBUser == null){
            Toaster.longToast("Invalid user....");
            gotoLoginActivity();
            return;
        }
        mQBUser.setPassword(password);
        startLoginService(mQBUser);
    }

    private void QBUserSignIn(QBUser user){
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
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
                progressDialog.dismiss();
                Toaster.longToast(e.getMessage());

                gotoLoginActivity();
            }
        });
    }

    protected void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Constant.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.EXTRA_LOGIN_RESULT_CODE) {
            boolean isLoginSuccess = data.getBooleanExtra(Constant.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Constant.EXTRA_LOGIN_ERROR_MESSAGE);
            if (isLoginSuccess) {
                saveUserData(mQBUser);
                QBUserSignIn(mQBUser);
            } else {
                Toaster.longToast(getString(R.string.login_chat_login_error) + errorMessage);
                gotoLoginActivity();
            }
        }
    }

    public QBUser getQBUserFromUsername(String username){
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
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
//        sharedPrefsHelper.save(Constant.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private void firebaseSignIn() {
        progressDialog.show();
        String email = mQBUser.getEmail();
        mAuth.signInWithEmailAndPassword(email, Constant.FIREBASE_DEFAULT_PASS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SplashActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                            gotoLoginActivity();
                        }
                    }
                });
    }
    private void onAuthSuccess(FirebaseUser user) {
        Intent intent= new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoLoginActivity(){
        Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
}
