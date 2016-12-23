package com.brasco.simwechat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brasco.simwechat.adapter.MomentArrayAdapter;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.model.FirePost;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class PostUserActivity extends IBActivity {
    public static final int NEW_POST = 165;
    public static final String EXTRA_QBUSER_ID = "qbuserId";
    private TextView m_txtName;
    private ImageView m_imageUser;
    private ListView m_listview;
    private ImageView m_camera;

    private ArrayList<FirePost> m_momentArray = new ArrayList<FirePost>();
    private MomentArrayAdapter m_momentArrayAdapter;
    private DatabaseReference mPostReference;
    private String mFirebaseUserId;
    private ChildEventListener mPostChildListener;
    private QBUser me;
    private String mQbUserId;

    public static void startPostUserActivity(Activity activity, String qbUserId) {
        Intent intent = new Intent(activity, PostUserActivity.class);
        intent.putExtra(PostUserActivity.EXTRA_QBUSER_ID, qbUserId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_user);

        mQbUserId  = getIntent().getStringExtra(EXTRA_QBUSER_ID);
        me = getQBUserFromUserId(mQbUserId);
        ActionBar(me.getFullName() + "'s Posts");

        mFirebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPostReference = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_POSTS);

        m_txtName = (TextView) findViewById(R.id.my_name);
        m_imageUser = (ImageView) findViewById(R.id.my_image);
        m_listview = (ListView) findViewById(R.id.list_moments);
        m_camera = (ImageView) findViewById(R.id.img_camera);
        m_camera.setVisibility(View.GONE);

        initUI();
        m_momentArray.clear();
        m_momentArrayAdapter = new MomentArrayAdapter(this, m_momentArray);
        m_listview.setAdapter(m_momentArrayAdapter);
    }
    private void initUI(){
        if (me != null) {
            m_txtName.setText(me.getFullName());
            String strUrl = me.getCustomData();
            if (strUrl != null && !strUrl.isEmpty()) {
                setAvatarImage(strUrl);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_select_avatar);
                m_imageUser.setImageBitmap(bm);
            }
        }
    }
    private void setAvatarImage(String fileUrl) {
        Glide.with(this)
                .load(fileUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Bitmap bm = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.button_select_avatar);
                        m_imageUser.setImageBitmap(bm);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
                .error(R.drawable.ic_error)
                .into(m_imageUser);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQ_PHOTO_FILE) {
                String path = data.getStringExtra(Constant.EK_URL);
                NewPostActivity.startForResult(this, NEW_POST, path);
            }
        }
    }
    private void getPicture() {
        Intent intent = new Intent(PostUserActivity.this, CameraActivity.class);
        intent.putExtra(Constant.REQ_VIDEO_CAMERAACTIVITY_TYPE, Constant.REQ_IMAGE_TYPE);
        startActivityForResult(intent, Constant.REQ_PHOTO_FILE);
    }

    @Override
    public void onStart() {
        super.onStart();

        mPostChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot d1 = dataSnapshot.child("authorName");
                DataSnapshot d2 = dataSnapshot.child("authorUid");
                DataSnapshot d3 = dataSnapshot.child("comment");
                DataSnapshot d4 = dataSnapshot.child("time");
                DataSnapshot d5 = dataSnapshot.child("imageUrl");
                DataSnapshot d6 = dataSnapshot.child("authorQbId");
                String auName = d1.getValue(String.class);
                String auId = d2.getValue(String.class);
                String time = d4.getValue(String.class);
                String comment = d3.getValue(String.class);
                String url = d5.getValue(String.class);
                String qbId = d6.getValue(String.class);

                if (qbId.equals(me.getLogin())) {
                    FirePost post = new FirePost(auId, auName, time, comment, url, qbId);
                    m_momentArray.add(post);
                    m_momentArrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addChildEventListener(mPostChildListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPostChildListener != null) {
            mPostReference.removeEventListener(mPostChildListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        m_momentArray.clear();
        m_momentArrayAdapter.clear();
    }
    private QBUser getQBUserFromUserId(String userId){
        if (QBData.curQBUser.getLogin().equals(userId))
            return QBData.curQBUser;

        for (int i = 0; i< AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            if (user.getQBUser().getLogin().equals(userId))
                return user.getQBUser();
        }
        return null;
    }
}
