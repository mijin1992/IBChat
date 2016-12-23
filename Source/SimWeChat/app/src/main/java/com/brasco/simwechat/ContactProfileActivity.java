package com.brasco.simwechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brasco.simwechat.adapter.PostArrayAdapter;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.model.FirePost;
import com.brasco.simwechat.model.FireUser;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.activity.BaseActivity;
import com.brasco.simwechat.quickblox.activity.CallActivity;
import com.brasco.simwechat.quickblox.core.utils.Toaster;
import com.brasco.simwechat.quickblox.db.QbUsersDbManager;
import com.brasco.simwechat.quickblox.services.CallService;
import com.brasco.simwechat.quickblox.utils.CollectionsUtils;
import com.brasco.simwechat.quickblox.utils.PermissionsChecker;
import com.brasco.simwechat.quickblox.utils.UsersUtils;
import com.brasco.simwechat.quickblox.utils.WebRtcSessionManager;
import com.brasco.simwechat.utils.LogUtil;
import com.brasco.simwechat.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.List;

public class ContactProfileActivity extends BaseActivity implements View.OnClickListener {

    private String m_ContactUserId = "";

    private ImageView m_ivProfileImage = null;
    private TextView m_txtProfileName = null;
    private ImageView m_ivProfileGender = null;
    private TextView m_txtProfileId = null;
    private TextView m_txtRegion = null;
    private LinearLayout m_btnAlbum = null;
    private TextView m_txtWhatsup = null;
    private Button m_btnMessage = null;
    private Button m_btnCall = null;
    private ImageView m_postImage1 = null;
    private ImageView m_postImage2 = null;
    private ImageView m_postImage3 = null;

    private UserData m_curUser = null;

    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;
    private PermissionsChecker checker;
    private QBUser currentUser;
    private ArrayList<QBUser> currentOpponentsList;
    private QbUsersDbManager dbManager;

    private DatabaseReference mPostReference;
    private DatabaseReference mUserReference;
    private ChildEventListener mPostChildListener;
    private ChildEventListener mUserChildListener;
    private ArrayList<FirePost> m_postArray = new ArrayList<FirePost>();

    public static void start(Context context, boolean isRunForCall, String userId) {
        Intent intent = new Intent(context, ContactProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Constant.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        intent.putExtra(Utils.KEY_USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);

        Intent intent = getIntent();
        m_ContactUserId = intent.getStringExtra(Utils.KEY_USER_ID);
        setContentView(R.layout.activity_contact_profile);

        mPostReference = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_POSTS);
        mUserReference = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_USERS);

        m_ivProfileImage = (ImageView) findViewById(R.id.profile_image);
        m_txtProfileName = (TextView) findViewById(R.id.profile_name);
        m_ivProfileGender = (ImageView) findViewById(R.id.profile_gender);
        m_txtProfileId = (TextView) findViewById(R.id.txt_id);
        m_txtRegion = (TextView) findViewById(R.id.profile_region);
        m_btnAlbum = (LinearLayout) findViewById(R.id.button_album);
        m_txtWhatsup = (TextView) findViewById(R.id.profile_what);
        m_btnMessage = (Button) findViewById(R.id.button_message);
        m_btnCall = (Button) findViewById(R.id.button_call);
        m_postImage1 = (ImageView) findViewById(R.id.image_post_1);
        m_postImage2 = (ImageView) findViewById(R.id.image_post_2);
        m_postImage3 = (ImageView) findViewById(R.id.image_post_3);
        m_postImage1.setOnClickListener(this);
        m_postImage2.setOnClickListener(this);
        m_postImage3.setOnClickListener(this);
        m_btnAlbum.setOnClickListener(this);
        m_btnMessage.setOnClickListener(this);
        m_btnCall.setOnClickListener(this);

        ActionBar("Profile");
        initUI();

        initFields();
        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(ContactProfileActivity.this, true);
        }
        checker = new PermissionsChecker(getApplicationContext());

        getFirebaseData();
    }

    public void ActionBar(String title) {
        ActionBar toolBar = getSupportActionBar();
        if (toolBar != null) {
            toolBar.setDisplayShowHomeEnabled(false);
            toolBar.setDisplayShowTitleEnabled(false);
            toolBar.setDisplayShowCustomEnabled(true);
            toolBar.setCustomView(R.layout.default_actionbar);
            Toolbar parent = (Toolbar) toolBar.getCustomView().getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
        TextView txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(title);
        TextView buttonBack = (TextView) findViewById(R.id.toolbar_back);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/materialicons.ttf");
        buttonBack.setTypeface(font);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_album:
                break;
            case R.id.button_message:
                sendMessage();
                break;
            case R.id.button_call:
                freeCall();
                break;
            case R.id.image_post_1:
            case R.id.image_post_2:
            case R.id.image_post_3:
                PostUserActivity.startPostUserActivity(this, m_curUser.getQBUser().getLogin());
                break;
        }
    }

    private void sendMessage() {
        AppGlobals.mainActivity.startChatActivity(m_curUser);
    }

    private void freeCall() {
        if (isLoggedInChat()) {
            startCall(false);
        }
    }
    public UserData getUserDataFromUsername(String userId){
        for (int i=0; i< AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            if (user.getUserId().equals(userId))
                return user;
        }
        return null;
    }
    private void initUI(){
        m_curUser = getUserDataFromUsername(m_ContactUserId);
        if (m_curUser != null){
            m_txtProfileName.setText(m_curUser.getFullName());
            m_txtProfileId.setText(m_curUser.getUserId());
            String country = m_curUser.getQBUser().getWebsite();
            if (country != null) {
                country = country.substring(country.indexOf("//") + 2);
                m_txtRegion.setText(country);
            }
            QBUser qbuser = m_curUser.getQBUser();
            String strUrl= qbuser.getCustomData();
            if(strUrl != null && !strUrl.isEmpty()) {
                Glide.with(this)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.button_select_avatar);
                                m_ivProfileImage.setImageBitmap(bm);
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
                        .into(m_ivProfileImage);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_select_avatar);
                m_ivProfileImage.setImageBitmap(bm);
            }
        }
    }
    private void initFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Constant.EXTRA_IS_STARTED_FOR_CALL);
        }

        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }
    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void initUsersList() {
//      checking whether currentOpponentsList is actual, if yes - return
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
        proceedInitUsersList();
    }

    private void proceedInitUsersList() {
        LogUtil.writeDebugLog(TAG, "proceedInitUsersList", "1");
        currentOpponentsList = dbManager.getAllUsers();
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
    }
    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }

    private void startCall(boolean isVideoCall) {
        Log.d(TAG, "startCall()");
        QBUser user = m_curUser.getQBUser();
        Integer userId = user.getId();
        ArrayList<Integer> opponentsList = new ArrayList<Integer>();
        opponentsList.add(userId);
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

//        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());

        CallActivity.start(this, false);
        Log.d(TAG, "conferenceType = " + conferenceType);
    }

    private void initActionBarWithSelectedUsers(int countSelectedUsers) {
        setActionBarTitle(String.format(getString(
                countSelectedUsers > 1
                        ? R.string.tile_many_users_selected
                        : R.string.title_one_user_selected),
                countSelectedUsers));
    }

    private void updateActionBar(int countSelectedUsers) {
        invalidateOptionsMenu();
    }

    private void logOut() {
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();
    }

    private void startLogoutCommand() {
        CallService.logout(this);
    }

    private void unsubscribeFromPushes() {
        if (googlePlayServicesHelper.checkPlayServicesAvailable(this)) {
            Log.d(TAG, "unsubscribeFromPushes()");
            googlePlayServicesHelper.unregisterFromGcm(Constant.GCM_SENDER_ID);
        }
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d(TAG, "Current user was deleted from QB");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "Current user wasn't deleted from QB " + e);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_right, R.anim.out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUsersList();
    }
    public void onNewSession(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Constant.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(ContactProfileActivity.this, true);
            }
        }
    }
    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.button_call);
    }

    @Override
    public void onSessionCreated(boolean success) {

    }

    //Firebase
    private void getFirebaseData() {
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
                if (qbId.equals(m_curUser.getQBUser().getLogin())) {
                    FirePost post = new FirePost(auId, auName, time, comment, url, qbId);
                    m_postArray.add(post);
                    setPostImage();
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

        mUserChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FireUser user = dataSnapshot.getValue(FireUser.class);
                if (user.username.equals(m_curUser.getQBUser().getLogin())) {
                    m_txtWhatsup.setText(user.getWhatsUp());
                    if (user.gender.equals(Constant.FIREBASE_GENDER_FEMALE)){
                        m_ivProfileGender.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.gender_female));
                    }
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
        mUserReference.addChildEventListener(mUserChildListener);
    }
    private void setPostImage(){
        if (m_postArray.size() > 0 ){
            setImageFromUrl(m_postImage1, m_postArray.get(0).getImageUrl());
        }
        if (m_postArray.size() > 1 ){
            setImageFromUrl(m_postImage2, m_postArray.get(1).getImageUrl());
        }
        if (m_postArray.size() > 2 ){
            setImageFromUrl(m_postImage3, m_postArray.get(2).getImageUrl());
        }
    }
    private void setImageFromUrl(final ImageView imageView, String url){
        if(url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
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
                    .into(imageView);
        }
    }
}
