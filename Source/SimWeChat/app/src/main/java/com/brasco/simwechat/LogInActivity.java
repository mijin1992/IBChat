package com.brasco.simwechat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Ibrahima Chat";
    private Button m_btnLogin = null;
    private Button m_btnSignUp = null;
    private Button m_btnForgotPasswd = null;
    private ImageView m_ivUserImage = null;
    private TextView m_txtUserName = null;
    private EditText m_txtPassword = null;

    private boolean m_bCheckPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

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

        m_ivUserImage = (ImageView) findViewById(R.id.img_user_avatar);
        m_txtUserName = (TextView) findViewById(R.id.txt_user_name);
        m_txtPassword = (EditText) findViewById(R.id.txt_user_password);
        m_txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (m_txtPassword.getText().toString().trim().isEmpty()) {
                    m_btnLogin.setEnabled(false);
                } else {
                    m_btnLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

    private void signIn() {

    }

    private void performForgotPassword() {
    }

    private void signUp() {
        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
