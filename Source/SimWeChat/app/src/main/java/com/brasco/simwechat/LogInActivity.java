package com.brasco.simwechat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends IBActivity implements View.OnClickListener {
    private static final String TAG = "Ibrahima Chat";
    private Button m_btnLogin = null;
    private Button m_btnSignUp = null;
    private Button m_btnForgotPasswd = null;
    private EditText m_txtUserId = null;
    private EditText m_txtPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    private void signIn() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
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
}
