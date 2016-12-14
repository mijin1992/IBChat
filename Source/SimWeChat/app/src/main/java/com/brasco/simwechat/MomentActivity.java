package com.brasco.simwechat;

import android.os.Bundle;

public class MomentActivity extends IBActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        ActionBar("Moments");
    }
}
