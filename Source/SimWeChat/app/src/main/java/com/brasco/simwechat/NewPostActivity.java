package com.brasco.simwechat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.firebase.MyUploadService;
import com.brasco.simwechat.model.FirePost;
import com.brasco.simwechat.quickblox.QBData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends IBActivity {
    private static final String REQUIRED = "Required";

    private ImageView m_postImage;
    private EditText m_postText;
    private Button m_post;

    private DatabaseReference mPostReference;
    private BroadcastReceiver mBroadcastReceiver;
    private FirebaseAuth mAuth;

    private MyProgressDialog myProgressDialog;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    private String m_imagePath;

    public static void startForResult(Activity activity, int code, String imagePath) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra(Constant.EK_URL, imagePath);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        ActionBar("New Post");
        myProgressDialog = new MyProgressDialog(this, 0);

        m_imagePath = getIntent().getStringExtra(Constant.EK_URL);
        mPostReference = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_POSTS);
        mAuth = FirebaseAuth.getInstance();

        m_postImage = (ImageView) findViewById(R.id.img_post);
        m_postText = (EditText) findViewById(R.id.txt_post);
        m_post = (Button) findViewById(R.id.button_post);
        m_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(m_imagePath);
            }
        });
        if (m_imagePath != null && !m_imagePath.isEmpty()) {
            File image = new File(m_imagePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
            m_postImage.setImageBitmap(bitmap);
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();
                switch (intent.getAction()) {
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void onUploadResultIntent(Intent intent) {
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
        if (mDownloadUrl != null) {
            String url = mDownloadUrl.toString();
            writePost(url);
        }
    }
    private void uploadImage(String path){
        final String comment = m_postText.getText().toString();
        if (path == null || path.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(comment)) {
            m_postText.setError(REQUIRED);
            return;
        }
        showProgressDialog();
        mFileUri = Uri.fromFile(new File(path));
        mDownloadUrl = null;
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, mFileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));
    }

    private void writePost(String url){
        String name = QBData.curQBUser.getFullName();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long time = new Date().getTime();
        String comment = m_postText.getText().toString();
        FirePost post = new FirePost(uid, name, String.valueOf(time), comment, url, QBData.curQBUser.getLogin());

        String key = mPostReference.push().getKey();
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+ key, postValues);
        mPostReference.updateChildren(childUpdates);

//        mPostReference.setValue(post);

        finish();
    }

    private void showProgressDialog() {
        myProgressDialog.show();
    }

    private void hideProgressDialog() {
        myProgressDialog.hide();
    }
}
