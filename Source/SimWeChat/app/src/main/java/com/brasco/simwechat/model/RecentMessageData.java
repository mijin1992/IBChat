package com.brasco.simwechat.model;

import com.brasco.simwechat.utils.LogUtil;

/**
 * Created by Administrator on 12/14/2016.
 */
public class RecentMessageData {
    public static final String TAG = "RecentMessageData";

    private String mUserId;
    private String mLogoUrl;
    private String mUsername;
    private String mMessage;
    private long mSendReceiveTime;
    private int mUnreadMessages;

    public RecentMessageData(String userid, String username, String message, long time, String logoUrl){
        mUserId = userid;
        mUsername = username;
        mMessage = message;
        mSendReceiveTime = time;
        mUnreadMessages = 0;
        mLogoUrl = logoUrl;
        LogUtil.writeDebugLog(TAG, "RecentMessageData", "start");
    }

    public String getUserId(){return  mUserId;}
    public String getUsername(){
        return mUsername;
    }
    public String getMessage(){
        return mMessage;
    }
    public void setMessage(String message){
        mMessage = message;
    }
    public long getTime(){
        return mSendReceiveTime;
    }
    public void setTime(long time){
        mSendReceiveTime = time;
    }
    public void setUnreadMessages(int count){
        mUnreadMessages = count;
    }
    public int getUnreadMessages(){
        return mUnreadMessages;
    }
    public void setLogoUrl(String url){
        mLogoUrl = url;
    }
    public String getLogoUrl(){
        return mLogoUrl;
    }
}

