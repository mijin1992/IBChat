package com.brasco.simwechat;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.halzhang.android.library.BottomTabIndicator;

public class MainActivity extends IBActivity {

    private ViewPager m_ViewPager = null;
    private BottomTabIndicator m_TabView = null;
    private FragmentAdapter m_Adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_Adapter = new FragmentAdapter(this, getSupportFragmentManager());
        m_ViewPager = (ViewPager) findViewById(R.id.viewPager);
        m_ViewPager.setAdapter(m_Adapter);
        m_TabView = (BottomTabIndicator) findViewById(R.id.tab_indicator);
        m_TabView.setViewPager(m_ViewPager);
        m_TabView.setCurrentItem(0);
    }
}
