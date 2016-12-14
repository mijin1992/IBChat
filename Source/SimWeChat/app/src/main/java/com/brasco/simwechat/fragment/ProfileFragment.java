package com.brasco.simwechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brasco.simwechat.LogInActivity;
import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.PostActivity;
import com.brasco.simwechat.ProfileActivity;
import com.brasco.simwechat.R;

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
}
