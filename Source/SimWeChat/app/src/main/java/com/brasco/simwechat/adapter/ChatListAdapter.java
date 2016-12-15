package com.brasco.simwechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.message.Message;
import com.brasco.simwechat.utils.Utils;

import java.util.List;
import java.util.Vector;

/**
 * Created by Mikhail on 12/14/2016.
 */

public class ChatListAdapter extends BaseAdapter {
    private List<Message> listData;
    private Vector<ViewHolder> m_viewHolder = new Vector<>();
    private LayoutInflater layoutInflater;
    private MainActivity mActivity;

    static class ViewHolder {
        ImageView _userImage;
        TextView _userName;
        TextView _message;
        TextView _date;
    }

    public ChatListAdapter(Context aContext, List<Message> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        m_viewHolder.clear();
        mActivity = (MainActivity) aContext;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_chat, null);
            holder = new ViewHolder();
            holder._userImage = (ImageView) convertView.findViewById(R.id.badge_chat);
            holder._userName = (TextView) convertView.findViewById(R.id.txt_contact);
            holder._message = (TextView) convertView.findViewById(R.id.txt_message);
            holder._date = (TextView) convertView.findViewById(R.id.txt_message_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String strUserNm = Utils.getNameFromId(listData.get(position).getFrom());
        holder._userName.setText(strUserNm);
        holder._message.setText(listData.get(position).getText());
        String strTime = Long.toString(listData.get(position).getTime());
        holder._date.setText(strTime);
        m_viewHolder.add(holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startChatActivity(null);
            }
        });
        return convertView;
    }
}
