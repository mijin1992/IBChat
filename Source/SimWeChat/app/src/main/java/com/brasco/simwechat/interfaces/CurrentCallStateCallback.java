package com.brasco.simwechat.interfaces;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by Administrator on 12/14/2016.
 */
public interface CurrentCallStateCallback {
    void onCallStarted();
    void onCallStopped();
    void onOpponentsListUpdated(ArrayList<QBUser> newUsers);
}

