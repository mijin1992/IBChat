package com.brasco.simwechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brasco.simwechat.ChatActivity;
import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.RecentMessageData;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.ui.adapter.BaseListAdapter;
import com.brasco.simwechat.quickblox.managers.QbDialogUtils;
import com.brasco.simwechat.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

/**
 * Created by Mikhail on 12/14/2016.
 */

public class ChatListAdapter extends BaseListAdapter<QBChatDialog> {
    private Vector<ViewHolder> m_viewHolder = new Vector<>();
    private LayoutInflater layoutInflater;
    private MainActivity mActivity;

    static class ViewHolder {
        ImageView _userImage;
        TextView _userName;
        TextView _message;
        TextView _date;
        TextView _unreadCount;
    }

    public ChatListAdapter(Context aContext, List<QBChatDialog> dialogs) {
        super(aContext, dialogs);
        layoutInflater = LayoutInflater.from(aContext);
        m_viewHolder.clear();
        mActivity = (MainActivity) aContext;
    }

    @Override
    public int getCount() {
        if (objectsList == null)
            return 0;
        return objectsList.size();
    }

    @Override
    public QBChatDialog getItem(int position) {
        return objectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_chat, null);
            holder = new ViewHolder();
            holder._userImage = (ImageView) convertView.findViewById(R.id.badge_chat);
            holder._userName = (TextView) convertView.findViewById(R.id.txt_contact);
            holder._message = (TextView) convertView.findViewById(R.id.txt_message);
            holder._date = (TextView) convertView.findViewById(R.id.txt_message_time);
            holder._unreadCount = (TextView)convertView.findViewById(R.id.text_dialog_unread_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBChatDialog dialog = getItem(position);
        String strUserNm = QbDialogUtils.getDialogName(dialog);
        holder._userName.setText(strUserNm);
        holder._message.setText(prepareTextLastMessage(dialog));
        long time = dialog.getLastMessageDateSent();
        time = time *1000;
        String strTime = "";
        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm:ss a");
        strTime = df.format(time);
        holder._date.setText(strTime);
        int unreadMessagesCount = dialog.getUnreadMessageCount();
        if (unreadMessagesCount == 0) {
            holder._unreadCount.setVisibility(View.GONE);
        } else {
            holder._unreadCount.setVisibility(View.VISIBLE);
            holder._unreadCount.setText(String.valueOf(unreadMessagesCount > 99 ? 99 : unreadMessagesCount));
        }
        Integer userId = QbDialogUtils.getOpponentIdForPrivateDialog(dialog);
        final UserData user = mActivity.getUserDataFromUserId(userId);
        if (user != null){
            QBUser qbuser = user.getQBUser();
            String strUrl= qbuser.getCustomData();
            if(strUrl != null && !strUrl.isEmpty()) {
                Glide.with(mActivity)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                                holder._userImage.setImageBitmap(bm);
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
                        .into(holder._userImage);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                holder._userImage.setImageBitmap(bm);
            }
        }
        m_viewHolder.add(holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    AppGlobals.curChattingUser = user;
                    ChatActivity.startForResult(mActivity, MainActivity.REQUEST_DIALOG_ID_FOR_UPDATE, dialog.getDialogId());
                }
            }
        });
        return convertView;
    }

    private String prepareTextLastMessage(QBChatDialog chatDialog){
        if (isLastMessageAttachment(chatDialog)){
            return "Attachment";
        } else {
            return chatDialog.getLastMessage();
        }
    }
    private boolean isLastMessageAttachment(QBChatDialog dialog) {
        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }
}
