package com.brasco.simwechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.brasco.simwechat.R;
import com.brasco.simwechat.utils.LogUtil;

/**
 * Created by Administrator on 12/14/2016.
 */
public class UploadProgress extends Dialog {
    public static final String TAG = "UploadProgress";

    private ProgressBar progressBar;

    public UploadProgress(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public UploadProgress(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }
    public void updateProgress(int value){
        LogUtil.writeDebugLog(TAG, "updateProgress", "1");
        progressBar.setProgress(value);
    }

    private void init(Context context) {
        LogUtil.writeDebugLog(TAG, "init", "1");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.file_upload_progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2) ;
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0f;
        getWindow().setAttributes(lp);

        setCancelable(false);
    }

    @Override
    public void show() {
        // we are using try - catch in order to prevent crashing issue
        // when the activity is finished but the AsyncTask is still processing
        try {
            LogUtil.writeDebugLog(TAG, "show", "1");
            super.show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}


