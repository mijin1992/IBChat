package com.brasco.simwechat.quickblox.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.brasco.simwechat.R;
import com.brasco.simwechat.app.SimWeChatApplication;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.gcm.GooglePlayServicesHelper;
import com.brasco.simwechat.quickblox.core.ui.activity.CoreBaseActivity;
import com.brasco.simwechat.quickblox.core.ui.dialog.ProgressDialogFragment;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.utils.QBResRequestExecutor;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.QbAuthUtils;
import com.brasco.simwechat.quickblox.utils.qb.QbSessionStateCallback;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

public abstract class BaseActivity extends CoreBaseActivity implements QbSessionStateCallback {
    public static final String TAG = BaseActivity.class.getSimpleName();
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    protected boolean isAppSessionActive;

    protected SharedPrefsHelper sharedPrefsHelper;
    private ProgressDialog progressDialog;
    protected GooglePlayServicesHelper googlePlayServicesHelper;
    protected QBResRequestExecutor requestExecutor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.writeDebugLog(TAG, "onCreate", "1");
        boolean wasAppRestored = savedInstanceState != null;
        boolean isQbSessionActive = QbAuthUtils.isSessionActive();
        final boolean needToRestoreSession = wasAppRestored || !isQbSessionActive;
        Log.v(TAG, "wasAppRestored = " + wasAppRestored);
        Log.v(TAG, "isQbSessionActive = " + isQbSessionActive);

        // Triggering callback via Handler#post() method
        // to let child's code in onCreate() to execute first
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (needToRestoreSession) {
                    recreateChatSession();
                    isAppSessionActive = false;
                } else {
                    onSessionCreated(true);
                    isAppSessionActive = true;
                }
            }
        });

        requestExecutor = SimWeChatApplication.getInstance().getQbResRequestExecutor();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        googlePlayServicesHelper = new GooglePlayServicesHelper();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("dummy_value", 0);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    protected abstract View getSnackbarAnchorView();

    private void recreateChatSession() {
        Log.d(TAG, "Need to recreate chat session");
        LogUtil.writeDebugLog(TAG, "recreateChatSession", "Need to recreate chat session");
        QBUser user = QBData.curQBUser;
        if (user == null) {
            LogUtil.writeDebugLog(TAG, "recreateChatSession", "User is null, can't restore session");
            Toaster.longToast("User is null, can't restore session");
            throw new RuntimeException("User is null, can't restore session");
        }

        reloginToChat(user);
    }

    private void reloginToChat(final QBUser user) {
        ProgressDialogFragment.show(getSupportFragmentManager(), R.string.dlg_restoring_chat_session);

        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                Log.v(TAG, "Chat login onSuccess()");
                LogUtil.writeDebugLog(TAG, "reloginToChat", "onSuccess");
                isAppSessionActive = true;
                onSessionCreated(true);

                ProgressDialogFragment.hide(getSupportFragmentManager());
            }

            @Override
            public void onError(QBResponseException e) {
                isAppSessionActive = false;
                LogUtil.writeDebugLog(TAG, "reloginToChat", "onError");
                ProgressDialogFragment.hide(getSupportFragmentManager());
                Log.w(TAG, "Chat login onError(): " + e);
                onSessionCreated(false);
            }
        });
    }
    protected void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
            progressDialog.setOnKeyListener(keyListener);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        LogUtil.writeDebugLog(TAG, "hideProgressDialog", "1");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}