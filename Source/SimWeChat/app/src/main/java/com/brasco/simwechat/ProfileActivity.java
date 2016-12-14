package com.brasco.simwechat;

import android.os.Bundle;

public class ProfileActivity extends IBActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar("My Profile");
    }
}
