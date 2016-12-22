package com.brasco.simwechat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PostActivity extends IBActivity {
    private TextView m_txtName;
    private ImageView m_imageUser;
    private ListView m_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar("My Posts");
    }
}
