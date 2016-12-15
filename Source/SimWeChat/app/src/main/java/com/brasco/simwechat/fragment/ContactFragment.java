package com.brasco.simwechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.brasco.simwechat.ContactProfileActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.contact.Contact;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.utils.Utils;
import com.brasco.simwechat.widget.ContactItemInterface;
import com.brasco.simwechat.widget.ContactListAdapter;
import com.brasco.simwechat.widget.ContactListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    private ContactListView m_lstContact = null;
    private ArrayList<UserData> m_Filters = new ArrayList<>();
    private ArrayList<UserData> m_Contacts = new ArrayList<UserData>();
    private ContactListAdapter m_Adapter = null;

    private boolean m_inSearchMode = false;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
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
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        m_lstContact = (ContactListView) view.findViewById(R.id.list_contact);
        setContactList();
        m_Adapter = new ContactListAdapter(getContext(), R.layout.item_contact, m_Contacts);
        m_lstContact.setFastScrollEnabled(true);
        m_lstContact.setAdapter(m_Adapter);
        m_lstContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                List<UserData> searchList = m_inSearchMode ? m_Filters : m_Contacts;
                float lastTouchX = m_lstContact.getScroller().getLastTouchDownEventX();
                UserData contact = searchList.get(position);
                Intent intent = new Intent(getContext(), ContactProfileActivity.class);
                intent.putExtra(Utils.KEY_USER_ID, contact.getUserId());
                startActivity(intent);
            }
        });

        return view;
    }

    // temporary
    private void setContactList() {
        m_Contacts.clear();
        for (int i=0; i < AppGlobals.mAllUserData.size(); i++) {
            m_Contacts.add(AppGlobals.mAllUserData.get(i));
        }
    }
}
