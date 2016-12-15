package com.brasco.simwechat.model;

import com.brasco.simwechat.utils.LogUtil;
import com.quickblox.users.model.QBUser;

/**
 * Created by Administrator on 12/14/2016.
 */
public class UserData {
    public static final String TAG = "UserData";

    private String mLogoUrl;
    private String mFullName;
    private String mUserId;
    private QBUser mQBUser;

    public UserData(QBUser user, String logo, String userId, String fullname) {
        mQBUser = user;
        mLogoUrl = logo;
        mUserId = userId;
        mFullName = fullname;

        LogUtil.writeDebugLog(TAG, "UserData", "start");
    }

    public String getLogo() {
        LogUtil.writeDebugLog(TAG, "getLogo", mLogoUrl);
        return mLogoUrl;
    }
    public String getFullName() {
        if (mFullName != null) {
            LogUtil.writeDebugLog(TAG, "getFullName", mFullName);
        }
        return mFullName;
    }
    public String getUserId() {
        return mUserId;
    }
    public QBUser getQBUser() {
        LogUtil.writeDebugLog(TAG, "getQBUser", mQBUser.getLogin());
        return mQBUser;
    }
    public void setLogo(String logo) {
        LogUtil.writeDebugLog(TAG, "setLogo", logo);
        mLogoUrl = logo;
    }
    public void setFullName(String fullname) {
        mFullName = fullname;
    }
    public void setUserId(String id) {
        mUserId = id;
    }
    public void setQBUser(QBUser user) {
        mQBUser = user;
    }
}
