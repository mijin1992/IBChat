package com.brasco.simwechat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brasco.simwechat.LogInActivity;
import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.PostActivity;
import com.brasco.simwechat.ProfileActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.users.model.QBUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    LinearLayout m_btnProfile = null;
    LinearLayout m_btnPost = null;
    private ImageView m_userImage;
    private TextView m_userName;
    private TextView m_userId;

    private QBUser me;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        m_btnProfile = (LinearLayout) v.findViewById(R.id.button_profile);
        m_btnProfile.setOnClickListener(this);
        m_btnPost = (LinearLayout) v.findViewById(R.id.button_post);
        m_btnPost.setOnClickListener(this);
        m_userImage = (ImageView) v.findViewById(R.id.profile_image);
        m_userName = (TextView) v.findViewById(R.id.profile_name);
        m_userId = (TextView) v.findViewById(R.id.txt_id);

        initUI();

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_profile:
                Intent intent1 = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent1);
                break;
            case R.id.button_post:
                Intent intent2 = new Intent(getContext(), PostActivity.class);
                startActivity(intent2);
                break;
        }
    }
    private void initUI(){
        me = QBData.curQBUser;
        if (me != null) {
            m_userName.setText(me.getFullName());
            m_userId.setText(me.getLogin());
            String strUrl = me.getCustomData();
            if (strUrl != null && !strUrl.isEmpty()) {
                Glide.with(this)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.button_select_avatar);
                                m_userImage.setImageBitmap(bm);
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
                        .into(m_userImage);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_select_avatar);
                m_userImage.setImageBitmap(bm);
            }
        }
    }
}
