package com.brasco.simwechat.quickblox.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.activity.CallActivity;
import com.brasco.simwechat.quickblox.utils.CollectionsUtils;
import com.brasco.simwechat.quickblox.utils.UiUtils;
import com.brasco.simwechat.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by tereha on 25.05.16.
 */
public class AudioConversationFragment extends BaseConversationFragment implements CallActivity.OnChangeDynamicToggle {
    private static final String TAG = AudioConversationFragment.class.getSimpleName();

    private ToggleButton audioSwitchToggleButton;
    private TextView alsoOnCallText;
    private TextView firstOpponentNameTextView;
    private TextView otherOpponentsTextView;
    private boolean headsetPlugged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationFragmentCallbackListener.addOnChangeDynamicToggle(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void configureOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_outgoing_opponents_names_audio_call));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_call_type));
    }

    @Override
    protected void configureToolbar() {
//        toolbar.setVisibility(View.VISIBLE);
//        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
//        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.toolbar_title_color));
//        toolbar.setSubtitleTextColor(ContextCompat.getColor(getActivity(), R.color.toolbar_subtitle_color));
    }

    @Override
    protected void configureActionBar() {
//        actionBar.setTitle(currentUser.getTags().get(0));
//        actionBar.setSubtitle(String.format(getString(R.string.subtitle_text_logged_in_as), currentUser.getFullName()));
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        timerChronometer = (Chronometer) view.findViewById(R.id.chronometer_timer_audio_call);

        final ImageView firstOpponentAvatarImageView = (ImageView) view.findViewById(R.id.image_caller_avatar);

        alsoOnCallText = (TextView) view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        firstOpponentNameTextView = (TextView) view.findViewById(R.id.text_caller_name);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());

        otherOpponentsTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        otherOpponentsTextView.setText(getOtherOpponentsNames());

        audioSwitchToggleButton = (ToggleButton) view.findViewById(R.id.toggle_speaker);
        audioSwitchToggleButton.setVisibility(View.VISIBLE);

        actionButtonsEnabled(false);

        Integer userId = opponents.get(0).getId();
        QBUser user  = getUserDataFromUserId(userId).getQBUser();
        if (user!= null) {
            String strUrl = user.getCustomData();
            if (strUrl != null && !strUrl.isEmpty()) {
                Glide.with(getActivity())
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.button_select_avatar);
                                firstOpponentAvatarImageView.setImageBitmap(bm);
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
                        .into(firstOpponentAvatarImageView);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.button_select_avatar);
                firstOpponentAvatarImageView.setImageBitmap(bm);
            }
            firstOpponentNameTextView.setText(user.getFullName());
            otherOpponentsTextView.setText(user.getFullName());
        }
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponents.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private String getOtherOpponentsNames() {
        ArrayList<QBUser> otherOpponents = new ArrayList<>();
        otherOpponents.addAll(opponents);
        otherOpponents.remove(0);

        return CollectionsUtils.makeStringFromUsersFullNames(otherOpponents);
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationFragmentCallbackListener.removeOnChangeDynamicToggle(this);
    }

    @Override
    protected void initButtonsListener() {
        super.initButtonsListener();

        audioSwitchToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationFragmentCallbackListener.onSwitchAudio();
            }
        });
    }

    @Override
    protected void actionButtonsEnabled(boolean inability) {
        super.actionButtonsEnabled(inability);
        if (!headsetPlugged) {
            audioSwitchToggleButton.setEnabled(inability);
        }
        audioSwitchToggleButton.setActivated(inability);
    }

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_audio_conversation;
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());
        otherOpponentsTextView.setText(getOtherOpponentsNames());
    }

    @Override
    public void enableDynamicToggle(boolean plugged, boolean previousDeviceEarPiece) {
        headsetPlugged = plugged;

        if (isStarted) {
            audioSwitchToggleButton.setEnabled(!plugged);

            if (plugged) {
                audioSwitchToggleButton.setChecked(true);
            }else if(previousDeviceEarPiece){
                audioSwitchToggleButton.setChecked(true);
            } else {
                audioSwitchToggleButton.setChecked(false);
            }
        }
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
