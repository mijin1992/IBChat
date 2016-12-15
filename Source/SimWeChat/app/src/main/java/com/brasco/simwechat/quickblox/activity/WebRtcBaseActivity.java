package com.brasco.simwechat.quickblox.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.app.SimWeChatApplication;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.quickblox.core.gcm.GooglePlayServicesHelper;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.brasco.simwechat.quickblox.utils.QBResRequestExecutor;

/**
 * * QuickBlox team
 * * */
public abstract class WebRtcBaseActivity extends CoreBaseActivity {
    public static final String TAG = "WebRtcBaseActivity";
        SharedPrefsHelper sharedPrefsHelper;
        private ProgressDialog progressDialog;
        protected GooglePlayServicesHelper googlePlayServicesHelper;
        protected QBResRequestExecutor requestExecutor;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LogUtil.writeDebugLog(TAG, "onCreate", "1");
            requestExecutor = SimWeChatApplication.getInstance().getQbResRequestExecutor();
            sharedPrefsHelper = SharedPrefsHelper.getInstance();
            googlePlayServicesHelper = new GooglePlayServicesHelper();
        }

        public void initDefaultActionBar() {
            String currentUserFullName = "";
            String currentRoomName = sharedPrefsHelper.get(Constant.PREF_CURREN_ROOM_NAME, "");

            if (sharedPrefsHelper.getQbUser() != null) {
                currentUserFullName = sharedPrefsHelper.getQbUser().getFullName();
            }
        }

        public void setActionbarSubTitle(String subTitle) {
        }

        public void removeActionbarSubTitle() {
        }

        void showProgressDialog(@StringRes int messageId) {
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
            progressDialog.setMessage(getString(messageId));
            progressDialog.show();
        }

        void hideProgressDialog() {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        protected void showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
            if (getSnackbarAnchorView() != null) {
            }
        }

        protected abstract View getSnackbarAnchorView();
    }