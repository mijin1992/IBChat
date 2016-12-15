package com.brasco.simwechat;

/**
 * Created by Administrator on 12/15/2016.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.utils.DeviceUtil;

@SuppressWarnings("deprecation")
public class BaseActionBarActivity extends ActionBarActivity implements OnClickListener {
    public static final String TAG = "BaseActionBarActivity";
    // UI
    public ActionBar actionBar;

    // left icon
    public View action_home;
    public View action_back;

    public TextView action_txt_title;

    // right icon
    public View action_done;
    public View action_download;
    public View action_refresh;
    public View action_setting;

    public MyProgressDialog dlg_progress;

    // Data
    public boolean isErrorOccured = false;

    @SuppressLint({"InflateParams", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(getResources().getColor(R.color.orange));
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange)));

            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflator = LayoutInflater.from(this);
            View v = inflator.inflate(R.layout.actionbar_title, null);
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            action_home = v.findViewById(R.id.action_home);
            action_home.setVisibility(View.GONE);
            action_back = v.findViewById(R.id.action_back);
            action_back.setVisibility(View.GONE);

            action_txt_title = (TextView) v.findViewById(R.id.action_txt_title);
            action_txt_title.setText(this.getTitle());
            action_txt_title.setVisibility(View.GONE);

            action_done = v.findViewById(R.id.action_done);
            action_done.setVisibility(View.GONE);
            action_download = v.findViewById(R.id.action_download);
            action_download.setVisibility(View.GONE);
            action_refresh = v.findViewById(R.id.action_refresh);
            action_refresh.setVisibility(View.GONE);
            action_setting = v.findViewById(R.id.action_setting);
            action_setting.setVisibility(View.GONE);


            action_home.setOnClickListener(this);
            action_back.setOnClickListener(this);
            action_done.setOnClickListener(this);
            action_download.setOnClickListener(this);
            action_refresh.setOnClickListener(this);
            action_setting.setOnClickListener(this);

            //assign the view to the actionbar
            actionBar.setCustomView(v);
            //			Toolbar parent = (Toolbar) v.getParent();
            //			if (parent != null)
            //				parent.setContentInsetsAbsolute(0,0);

            dlg_progress = new MyProgressDialog(this);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        // TODO Auto-generated method stub
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);
    }

    public void SetTitle(int titleResId, int colorResId) {
        if (titleResId > 0) {
            SetTitle(getString(titleResId), colorResId);

        } else {
            SetTitle(null, colorResId);
        }
    }

    public void SetTitle(String title, int colorResId) {
        if (actionBar != null) {
            if (title != null) {
                action_txt_title.setVisibility(View.VISIBLE);
                action_txt_title.setText(title);
            } else {
                action_txt_title.setVisibility(View.GONE);
            }

            if (colorResId > 0)
                action_txt_title.setTextColor(getResources().getColor(colorResId));
        }
    }

    public void SetTitleGravity(int gravity) {
        if (actionBar != null)
            action_txt_title.setGravity(gravity);
    }

    public void SetActionBarBackground(int colorResId) {
        if (actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(colorResId)));
    }

    public void ShowActionBarIcons(boolean showActionBar, int... res_id_arr) {
        if (actionBar != null) {
            if (showActionBar)
                actionBar.show();
            else
                actionBar.hide();

            action_home.setVisibility(View.GONE);
            action_back.setVisibility(View.GONE);

            action_done.setVisibility(View.GONE);
            action_download.setVisibility(View.GONE);
            action_refresh.setVisibility(View.GONE);

            if (res_id_arr != null) {
                for (int i = 0; i < res_id_arr.length; i++) {
                    switch (res_id_arr[i]) {
                        case R.id.action_home:
                            action_home.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_back:
                            action_back.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_done:
                            action_done.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_download:
                            action_download.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_refresh:
                            action_refresh.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_setting:
                            action_setting.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.action_home:
                break;

            case R.id.action_back:
                myBack();
                break;

            case R.id.action_done: {
            }
            break;

            case R.id.action_download: {
            }
            break;

            case R.id.action_refresh: {
            }
            break;

            case R.id.action_setting:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (dlg_progress != null)
            dlg_progress.dismiss();
    }

    public void myBack() {
        finish();
        overridePendingTransition(R.anim.in_right, R.anim.out_right);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        myBack();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (!DeviceUtil.isNetworkAvailable(this)) {
            isErrorOccured = true;
            //ErrorNetworkActivity.OpenMe();

        } else {
            if (!DeviceUtil.isLocationServiceAvailable(this)) {
                isErrorOccured = true;
                //ErrorGPSActivity.OpenMe();
            } else {
                isErrorOccured = false;
//                ErrorNetworkActivity.CloseMe();
//                ErrorGPSActivity.CloseMe();
            }
        }
    }
}