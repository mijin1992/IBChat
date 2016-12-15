package com.brasco.simwechat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.brasco.simwechat.fragment.ChatFragment;
import com.brasco.simwechat.fragment.ContactFragment;
import com.brasco.simwechat.fragment.MomentFragment;
import com.brasco.simwechat.fragment.ProfileFragment;
import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.model.UserData;
import com.halzhang.android.library.BottomTabFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Mikhail on 12/9/2016.
 */

public class FragmentAdapter extends BottomTabFragmentPagerAdapter {
    private Context m_Context = null;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        m_Context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return ChatFragment.newInstance();
        else if (position == 1)
            return ContactFragment.newInstance();
        else if (position == 2)
            return MomentFragment.newInstance();
        else
            return ProfileFragment.newInstance();
    }

    @Override
    public int getPageIcon(int position) {
        if (position == 0)
            return R.drawable.tab_chat;
        else if (position == 1)
            return R.drawable.tab_contact;
        else if (position == 2)
            return R.drawable.tab_moment;
        else
            return R.drawable.tab_profile;
    }

    @Override
    public int[] getTabViewIds() {
        int[] ids = new int[4];
        ids[0] = R.id.tab0;
        ids[1] = R.id.tab1;
        ids[2] = R.id.tab2;
        ids[3] = R.id.tab3;
        return ids;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Chats";
        else if (position == 1)
            return "Contacts";
        else if (position == 2)
            return "Moments";
        else
            return "Profile";
    }
}
