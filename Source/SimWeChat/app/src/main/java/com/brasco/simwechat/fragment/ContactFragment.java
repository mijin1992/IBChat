package com.brasco.simwechat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.brasco.simwechat.R;
import com.brasco.simwechat.contact.Contact;
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
    private ArrayList<Contact> m_Contacts = new ArrayList<>();
    private ArrayList<Contact> m_Filters = new ArrayList<>();
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
        setSampleContactList();
        m_Adapter = new ContactListAdapter(getContext(), R.layout.item_contact, m_Contacts);
        m_lstContact.setFastScrollEnabled(true);
        m_lstContact.setAdapter(m_Adapter);
        m_lstContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                List<Contact> searchList = m_inSearchMode ? m_Filters : m_Contacts;
                float lastTouchX = m_lstContact.getScroller().getLastTouchDownEventX();
            }
        });

        return view;
    }

    // temporary
    private void setSampleContactList() {
        m_Contacts.add(new Contact("Lizbeth", "Lizbeth Crockett"));
        m_Contacts.add(new Contact("Zachery", "Zachery Loranger"));
        m_Contacts.add(new Contact("Vada", "Vada Winegar"));
        m_Contacts.add(new Contact("Essie", "Essie Pass"));
        m_Contacts.add(new Contact("Gracia", "Gracia Ringdahl"));
        m_Contacts.add(new Contact("Roselia", "Roselia Benjamin"));
        m_Contacts.add(new Contact("Venice", "Venice Facey"));
        m_Contacts.add(new Contact("Lanita", "Lanita Welcher"));
        m_Contacts.add(new Contact("Chana", "Chana Hollin"));
        m_Contacts.add(new Contact("Stella", "Stella Ketterer"));

        m_Contacts.add(new Contact("Pete", "Pete Ibrahim"));
        m_Contacts.add(new Contact("Dwain", "Dwain Cowher"));
        m_Contacts.add(new Contact("Terisa", "Terisa Griner") );
        m_Contacts.add(new Contact("Delisa", "Delisa Deak"));
        m_Contacts.add(new Contact("Zada", "Zada Buckingham"));
        m_Contacts.add(new Contact("Rosalie", "Rosalie Rohrer"));
        m_Contacts.add(new Contact("Gladis", "Gladis Milhorn"));
        m_Contacts.add(new Contact("Branda", "Branda Respass"));
        m_Contacts.add(new Contact("Tory", "Tory Stanislawski"));

    }
}
