package com.brasco.simwechat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.brasco.simwechat.R;
import com.brasco.simwechat.adapter.ChatListAdapter;
import com.brasco.simwechat.message.Message;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private ListView m_lstChatView;
    private ArrayList<Message> m_ChatList = new ArrayList<>();
    private ChatListAdapter m_Adapter = null;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        m_lstChatView = (ListView) view.findViewById(R.id.list_chats);
        setSampleChatListData();
        m_Adapter = new ChatListAdapter(getContext(), m_ChatList);
        m_lstChatView.setAdapter(m_Adapter);

        return view;
    }

    private void setSampleChatListData() {
        Calendar calendar = Calendar.getInstance();
        m_ChatList.add(new Message("Zachery", "", "Sample Message1", calendar.getTimeInMillis()));
        m_ChatList.add(new Message("Terisa", "", "Sample Message2", calendar.getTimeInMillis()));
        m_ChatList.add(new Message("Chana", "", "Sample Message3", calendar.getTimeInMillis()));
        m_ChatList.add(new Message("Dwain", "", "Sample Message4", calendar.getTimeInMillis()));
        m_ChatList.add(new Message("Rosalie", "", "Sample Message5", calendar.getTimeInMillis()));
    }
}
