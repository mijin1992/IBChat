package com.brasco.simwechat;

import android.os.Bundle;

public class CallActivity extends IBActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ActionBar("Free Call");
    }
}
