package com.brasco.simwechat.quickblox.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.db.QbUsersDbManager;
import com.brasco.simwechat.quickblox.utils.CollectionsUtils;
import com.brasco.simwechat.quickblox.utils.RingtonePlayer;
import com.brasco.simwechat.quickblox.utils.UiUtils;
import com.brasco.simwechat.quickblox.utils.UsersUtils;
import com.brasco.simwechat.quickblox.utils.WebRtcSessionManager;
import com.brasco.simwechat.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * QuickBlox team
 */
public class IncomeCallFragment extends Fragment implements Serializable, View.OnClickListener {

    private static final String TAG = IncomeCallFragment.class.getSimpleName();
    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private TextView callTypeTextView;
    private ImageButton rejectButton;
    private ImageButton takeButton;

    private List<Integer> opponentsIds;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType conferenceType;
    private long lastClickTime = 0l;
    private RingtonePlayer ringtonePlayer;
    private IncomeCallFragmentCallbackListener incomeCallFragmentCallbackListener;
    private QBRTCSession currentSession;
    private QbUsersDbManager qbUserDbManager;
    private TextView alsoOnCallText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            incomeCallFragmentCallbackListener = (IncomeCallFragmentCallbackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCallEventsController");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        Log.d(TAG, "onCreate() from IncomeCallFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_call, container, false);

        initFields();
        hideToolBar();

        if (currentSession != null) {
            initUI(view);
            setDisplayedTypeCall(conferenceType);
            initButtonsListener();
        }

        ringtonePlayer = new RingtonePlayer(getActivity());
        return view;
    }

    private void initFields() {
        currentSession = WebRtcSessionManager.getInstance(getActivity()).getCurrentSession();
        qbUserDbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());

        if (currentSession != null) {
            opponentsIds = currentSession.getOpponents();
            conferenceType = currentSession.getConferenceType();
            Log.d(TAG, conferenceType.toString() + "From onCreateView()");
        }
    }

    public void hideToolBar() {
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    private void initButtonsListener() {
        rejectButton.setOnClickListener(this);
        takeButton.setOnClickListener(this);
    }

    private void initUI(View view) {
        callTypeTextView = (TextView) view.findViewById(R.id.call_type);

        final ImageView callerAvatarImageView = (ImageView) view.findViewById(R.id.image_caller_avatar);
        //callerAvatarImageView.setBackgroundDrawable(getBackgroundForCallerAvatar(currentSession.getCallerID()));

        TextView callerNameTextView = (TextView) view.findViewById(R.id.text_caller_name);

        QBUser callerUser = getUserDataFromUserId(currentSession.getCallerID()).getQBUser();
        if (callerUser == null){
            callerUser = qbUserDbManager.getUserById(currentSession.getCallerID());
        }
        callerNameTextView.setText(UsersUtils.getUserNameOrId(callerUser, currentSession.getCallerID()));

        if (callerUser != null) {
            String strUrl = callerUser.getCustomData();
            if (strUrl != null && !strUrl.isEmpty()) {
                Glide.with(getActivity())
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.button_select_avatar);
                                callerAvatarImageView.setImageBitmap(bm);
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
                        .into(callerAvatarImageView);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.button_select_avatar);
                callerAvatarImageView.setImageBitmap(bm);
            }
        }

        TextView otherIncUsersTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        otherIncUsersTextView.setText(getOtherIncUsersNames());

        alsoOnCallText = (TextView) view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        rejectButton = (ImageButton) view.findViewById(R.id.image_button_reject_call);
        takeButton = (ImageButton) view.findViewById(R.id.image_button_accept_call);
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponentsIds.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private Drawable getBackgroundForCallerAvatar(int callerId) {
        return UiUtils.getColorCircleDrawable(callerId);
    }

    public void startCallNotification() {
        Log.d(TAG, "startCallNotification()");

        ringtonePlayer.play(false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

    }

    private void stopCallNotification() {
        Log.d(TAG, "stopCallNotification()");

        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private String getOtherIncUsersNames() {
        ArrayList<QBUser> usersFromDb = qbUserDbManager.getUsersByIds(opponentsIds);
        ArrayList<QBUser> opponents = new ArrayList<>();
        opponents.addAll(UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds));

        opponents.remove(QBChatService.getInstance().getUser());
        Log.d(TAG, "opponentsIds = " + opponentsIds);
        return CollectionsUtils.makeStringFromUsersFullNames(opponents);
    }

    private void setDisplayedTypeCall(QBRTCTypes.QBConferenceType conferenceType) {
        boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        callTypeTextView.setText(isVideoCall ? R.string.text_incoming_video_call : R.string.text_incoming_audio_call);
        takeButton.setImageResource(isVideoCall ? R.drawable.ic_video_white : R.drawable.ic_call);
    }

    @Override
    public void onStop() {
        stopCallNotification();
        super.onStop();
        Log.d(TAG, "onStop() from IncomeCallFragment");
    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (v.getId()) {
            case R.id.image_button_reject_call:
                reject();
                break;

            case R.id.image_button_accept_call:
                accept();
                break;

            default:
                break;
        }
    }

    private void accept() {
        enableButtons(false);
        stopCallNotification();

        incomeCallFragmentCallbackListener.onAcceptCurrentSession();
        Log.d(TAG, "Call is started");
    }

    private void reject() {
        enableButtons(false);
        stopCallNotification();

        incomeCallFragmentCallbackListener.onRejectCurrentSession();
        Log.d(TAG, "Call is rejected");
    }

    private void enableButtons(boolean enable) {
        takeButton.setEnabled(enable);
        rejectButton.setEnabled(enable);
    }
    private UserData getUserDataFromUserId(Integer userId){
        for(int i = 0; i < AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            Integer id = user.getQBUser().getId();
            if (id.equals(userId)){
                return user;
            }
        }
        LogUtil.writeDebugLog(TAG, "getUserDataFromSenderId", "return value is null");
        return null;
    }
}
