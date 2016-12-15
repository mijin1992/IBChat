package com.brasco.simwechat.quickblox.utils;

import android.os.Bundle;
import android.util.Log;

import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 11/28/2016.
 */

public class QBResRequestExecutor {
    private String TAG = QBResRequestExecutor.class.getSimpleName();

    public void createSession(QBEntityCallback<QBSession> callback) {
        LogUtil.writeDebugLog(TAG, "createSession", "1");
        QBAuth.createSession().performAsync(callback);
    }

    public void createSessionWithUser(final QBUser qbUser, final QBEntityCallback<QBSession> callback) {
        LogUtil.writeDebugLog(TAG, "createSessionWithUser", "1");
        QBAuth.createSession(qbUser).performAsync(callback);
    }

    public void signUpNewUser(final QBUser newQbUser, final QBEntityCallback<QBUser> callback) {
        createSessionWithoutUser(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                QBUsers.signUp(newQbUser).performAsync(callback);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    public void signInUser(final QBUser currentQbUser, final QBEntityCallback<QBUser> callback) {
        QBUsers.signIn(currentQbUser).performAsync(callback);
    }

    public void deleteCurrentUser(int currentQbUserID, QBEntityCallback<Void> callback) {
        QBUsers.deleteUser(currentQbUserID).performAsync(callback);
    }

    public void loadUsersByTag(final String tag, final QBEntityCallback<ArrayList<QBUser>> callback) {
        restoreOrCreateSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                List<String> tags = new LinkedList<>();
                tags.add(tag);

                QBUsers.getUsersByTags(tags, requestBuilder).performAsync(callback);
            }
        });
    }

    public void loadUsersByIds(final Collection<Integer> usersIDs, final QBEntityCallback<ArrayList<QBUser>> callback) {
        restoreOrCreateSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                QBUsers.getUsersByIDs(usersIDs, null).performAsync(callback);
            }
        });
    }

    private void restoreOrCreateSession(final QBEntityCallbackImpl<QBSession> creatingSessionCallback) {
        LogUtil.writeDebugLog(TAG, "restoreOrCreateSession", "1");
        if (TokenUtils.isTokenValid()) {
            if (TokenUtils.restoreExistentQbSessionWithResult()) {
                LogUtil.writeDebugLog(TAG, "restoreOrCreateSession", "2");
                creatingSessionCallback.onSuccess(null, null);
            } else {
                LogUtil.writeDebugLog(TAG, "restoreOrCreateSession", "3");
                creatingSessionCallback.onError(null);
            }
        } else if (SharedPrefsHelper.getInstance().hasQbUser()) {
            LogUtil.writeDebugLog(TAG, "restoreOrCreateSession", "4");
            createSessionWithSavedUser(creatingSessionCallback);
        } else {
            LogUtil.writeDebugLog(TAG, "restoreOrCreateSession", "5");
            createSessionWithoutUser(creatingSessionCallback);
        }
    }

    private void createSessionWithSavedUser(final QBEntityCallback<QBSession> creatingSessionCallback) {
        LogUtil.writeDebugLog(TAG, "createSessionWithSavedUser", "1");
        createSessionWithUser(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                LogUtil.writeDebugLog(TAG, "createSessionWithSavedUser", "onSuccess");
                TokenUtils.saveTokenData();
                creatingSessionCallback.onSuccess(result, params);
            }

            @Override
            public void onError(QBResponseException responseException) {
                creatingSessionCallback.onError(responseException);
                Log.d(TAG, "Error creating session with user");
                LogUtil.writeDebugLog(TAG, "createSessionWithSavedUser", "Error creating session with user");
            }
        });
    }

    private void createSessionWithoutUser(final QBEntityCallback<QBSession> creatingSessionCallback) {
        LogUtil.writeDebugLog(TAG, "createSessionWithoutUser", "1");
        createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                LogUtil.writeDebugLog(TAG, "createSessionWithoutUser", "onSuccess");
                creatingSessionCallback.onSuccess(result, params);
            }

            @Override
            public void onError(QBResponseException responseException) {
                creatingSessionCallback.onError(responseException);
                LogUtil.writeDebugLog(TAG, "createSessionWithoutUser", "onError");
                Log.d(TAG, "Error creating session");
            }
        });
    }
}
