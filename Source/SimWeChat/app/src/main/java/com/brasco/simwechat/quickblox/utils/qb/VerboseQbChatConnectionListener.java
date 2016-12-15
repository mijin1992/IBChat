package com.brasco.simwechat.quickblox.utils.qb;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.brasco.simwechat.utils.LogUtil;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class VerboseQbChatConnectionListener implements ConnectionListener {
    private static final String TAG = VerboseQbChatConnectionListener.class.getSimpleName();
    private View rootView;
    private Snackbar snackbar;

    public VerboseQbChatConnectionListener(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.i(TAG, "connected()");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean authenticated) {
        Log.i(TAG, "authenticated()");
        LogUtil.writeDebugLog(TAG, "authenticated", "authenticated()");
    }

    @Override
    public void connectionClosed() {
        Log.i(TAG, "connectionClosed()");
        LogUtil.writeDebugLog(TAG, "connectionClosed", "connectionClosed()");
    }

    @Override
    public void connectionClosedOnError(final Exception e) {
        Log.i(TAG, "connectionClosedOnError(): " + e.getLocalizedMessage());
        LogUtil.writeDebugLog(TAG, "connectionClosedOnError", "connectionClosedOnError(): " + e.getLocalizedMessage());
//        Toaster.longToast("connectionClosed");
//        snackbar = Snackbar.make(rootView, BomboApplication.getInstance().getString(R.string.connection_error), Snackbar.LENGTH_INDEFINITE);
//        snackbar.show();
    }

    @Override
    public void reconnectingIn(final int seconds) {
        if (seconds % 5 == 0 && seconds != 0) {
//            Toaster.longToast("reconnecting.");
            Log.i(TAG, "reconnectingIn(): " + seconds);
            LogUtil.writeDebugLog(TAG, "reconnectingIn", "reconnectingIn(): " + seconds);
//            snackbar = Snackbar.make(rootView, BomboApplication.getInstance().getString(R.string.reconnect_alert, seconds), Snackbar.LENGTH_INDEFINITE);
//            snackbar.show();
        }
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i(TAG, "reconnectionSuccessful()");
        LogUtil.writeDebugLog(TAG, "reconnectionSuccessful", "reconnectionSuccessful()");
//        Toaster.longToast("reconnectionSuccessful.");
//        snackbar.dismiss();
    }

    @Override
    public void reconnectionFailed(final Exception error) {
//        Toaster.longToast("reconnectionFailed.");
        Log.i(TAG, "reconnectionFailed(): " + error.getLocalizedMessage());
        LogUtil.writeDebugLog(TAG, "reconnectionFailed", "reconnectionFailed(): " + error.getLocalizedMessage());
    }
}
