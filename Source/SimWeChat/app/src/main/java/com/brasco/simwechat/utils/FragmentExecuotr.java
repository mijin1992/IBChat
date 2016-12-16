package com.brasco.simwechat.utils;

/**
 * Created by Administrator on 12/17/2016.
 */
import android.app.Fragment;
import android.app.FragmentManager;

/**
 * QuickBlox team
 */
public class FragmentExecuotr {

    public static void addFragment(FragmentManager fragmentManager, int containerId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
    }

    public static void removeFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
}

