package com.brasco.simwechat.utils;

/**
 * Created by Administrator on 12/14/2016.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

public class MessageUtil {
    public static final String TAG = "MessageUtil";

    public static final int TYPE_ERROR = 6020;
    public static final int TYPE_WARNING = 6021;
    public static final int TYPE_SUCCESS = 6022;

    /*
     * Alert dialog
     */
    public static void showError(Context context, int messageId) {
        showAlertDialog(context, TYPE_ERROR, messageId);
    }

    public static void showError(Context context, String message) {
        showAlertDialog(context, TYPE_ERROR, message);
    }

    public static void showAlertDialog(Context context, int type, int messageId) {
        if (messageId == 0)
            showAlertDialog(context, type, null);
        else
            showAlertDialog(context, type, context.getString(messageId));
    }

    public static void showAlertDialog(Context context, int type, String message) {
        showAlertDialog(context, type, message, null);
    }

    public static void showAlertDialog(Context context, int type, int messageId, DialogInterface.OnClickListener listener) {
        if (messageId == 0)
            showAlertDialog(context, type, null, listener);
        else
            showAlertDialog(context, type, context.getString(messageId), listener);
    }

    public static void showAlertDialog(Context context, int type, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (type) {
            case TYPE_ERROR:
                builder.setTitle("Error");
                break;
            case TYPE_WARNING:
                builder.setTitle("Warning");
                break;
            case TYPE_SUCCESS:
                builder.setTitle("Success");
                break;
        }
        if (!TextUtils.isEmpty(message))
            builder.setMessage(message);
        builder.setPositiveButton("OK", listener);
        builder.show();
    }

    /*
     * Toast
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int messageId) {
        showToast(context, context.getString(messageId));
    }

    public static void showToast(Context context, String message, boolean isLong) {
        if (isLong) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            showToast(context, message);
        }
    }

    public static void showToast(Context context, int messageId, boolean isLong) {
        showToast(context, context.getString(messageId), isLong);
    }
}