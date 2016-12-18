package com.brasco.simwechat.quickblox.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brasco.simwechat.ChatActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.http.HttpDownloader;
import com.brasco.simwechat.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.brasco.simwechat.quickblox.core.ui.adapter.BaseListAdapter;
import com.brasco.simwechat.quickblox.utils.TimeUtils;
import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.PaginationHistoryListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.helper.CollectionsUtil;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ChatAdapter extends BaseListAdapter<QBChatMessage> implements StickyListHeadersAdapter {
    public static final String TAG = ChatAdapter.class.getSimpleName();
    private OnItemInfoExpandedListener onItemInfoExpandedListener;
    private final QBChatDialog chatDialog;
    private PaginationHistoryListener paginationListener;
    private int previousGetCount = 0;
    private ChatActivity mActivity;

    public ChatAdapter(Context context, QBChatDialog chatDialog, List<QBChatMessage> chatMessages) {
        super(context, chatMessages);
        mActivity = (ChatActivity) context;
        this.chatDialog = chatDialog;
        LogUtil.writeDebugLog(TAG, "ChatAdapter", "1");
    }

    public void setOnItemInfoExpandedListener(OnItemInfoExpandedListener onItemInfoExpandedListener) {
        this.onItemInfoExpandedListener = onItemInfoExpandedListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final QBChatMessage chatMessage = getItem(position);
        final ViewHolder holder;
        holder = new ViewHolder();

        boolean isIncoming = isIncoming(chatMessage);

        if (isIncoming){
            convertView = inflater.inflate(R.layout.item_message_receive, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.item_message_send, parent, false);
        }
        holder.userImage = (ImageView) convertView.findViewById(R.id.image_friend);
        holder.messageBodyTextView = (TextView) convertView.findViewById(R.id.txt_message);
        holder.audio = (ImageView) convertView.findViewById(R.id.ic_audio);
        holder.time = (TextView) convertView.findViewById(R.id.txt_time);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryDownloadFile(holder.audioUrl, holder.audioFilename);
            }
        });
        setMessageBody(holder, chatMessage);

        if (isIncoming(chatMessage) && !isRead(chatMessage)){
            readMessage(chatMessage);
        }

        downloadMore(position);
        return convertView;
    }

    private void TryDownloadFile(String url, String filename) {
        HttpDownloader httpDownloader = new HttpDownloader();
        httpDownloader.SetDownloaderListener(new HttpDownloader.HttpDownloaderListener() {
            @Override
            public void OnDownloaderResult(String a_strPath) {
                if (a_strPath == null)	{  return;  }
                if (a_strPath.length() == 0) { return;  }
                PlayAudio(a_strPath);
            }
        });

        httpDownloader.SetFileName(filename);
        httpDownloader.SetUrl(url);
        httpDownloader.execute();
    }

    private void PlayAudio(String filePath){
        MediaPlayer mpintro = MediaPlayer.create(mActivity, Uri.parse(filePath));
        mpintro.setLooping(false);
        mpintro.start();
    }

    private void downloadMore(int position) {
        LogUtil.writeDebugLog(TAG, "downloadMore", "1");
        if (position == 0) {
            if (getCount() != previousGetCount) {
                LogUtil.writeDebugLog(TAG, "downloadMore", "2");
                paginationListener.downloadMore();
                previousGetCount = getCount();
            }
        }
    }

    public void setPaginationHistoryListener(PaginationHistoryListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        LogUtil.writeDebugLog(TAG, "getHeaderView", "1");
        if (convertView == null) {
            holder = new HeaderViewHolder();
//            convertView = inflater.inflate(R.layout.view_chat_message_header, parent, false);
//            holder.dateTextView = (TextView) convertView.findViewById(R.id.header_date_textview);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        QBChatMessage chatMessage = getItem(position);
        holder.dateTextView.setText(TimeUtils.getDate(chatMessage.getDateSent() * 1000));

        LogUtil.writeDebugLog(TAG, "getHeaderView", "2");
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.dateTextView.getLayoutParams();
        if (position == 0) {
//            lp.topMargin = ResourceUtils.getDimen(R.dimen.chat_date_header_top_margin);
        } else {
            lp.topMargin = 0;
        }
        holder.dateTextView.setLayoutParams(lp);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        LogUtil.writeDebugLog(TAG, "getHeaderId", "1");
        QBChatMessage chatMessage = getItem(position);
        return TimeUtils.getDateAsHeaderId(chatMessage.getDateSent() * 1000);
    }

    private void setMessageBody(final ViewHolder holder, QBChatMessage chatMessage) {
        Boolean hasAttachments = hasAttachments(chatMessage);
        String type = "";
        if (hasAttachments)
            type = getAttachmentsType(chatMessage);

        long sentTime = chatMessage.getDateSent();
        sentTime = sentTime *1000;
        String strTime = "";
        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm:ss a");
        strTime = df.format(sentTime);
        holder.time.setText(strTime);
        boolean isIncoming = isIncoming(chatMessage);
        if (hasAttachments && type.equals(QBAttachment.AUDIO_TYPE)) {
            holder.messageBodyTextView.setVisibility(View.GONE);
            holder.audio.setVisibility(View.VISIBLE);
            Collection<QBAttachment> attachments = chatMessage.getAttachments();
            QBAttachment attachment = attachments.iterator().next();
            Integer id = Integer.parseInt(attachment.getId());
            holder.audioUrl = attachment.getUrl();
            holder.audioFilename = "audiomessage_" + chatMessage.getDateSent() + ".mp3";
        } else {
            holder.messageBodyTextView.setText(chatMessage.getBody());
            holder.messageBodyTextView.setVisibility(View.VISIBLE);
            holder.audio.setVisibility(View.GONE);
        }
        QBUser qbuser;
        if (isIncoming){
                qbuser = AppGlobals.curChattingUser.getQBUser();
        } else {
            qbuser = QBData.curQBUser;
        }
        String strUrl= qbuser.getCustomData();
        if(strUrl != null && !strUrl.isEmpty()) {
                LogUtil.writeDebugLog(TAG, "setMessageBody", "UserLogoURL:" + strUrl);
                Glide.with(mActivity)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                                holder.userImage.setImageBitmap(bm);
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
                        .into(holder.userImage);
        } else {
                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                holder.userImage.setImageBitmap(bm);
        }
    }

    private boolean hasAttachments(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        return attachments != null && !attachments.isEmpty();
    }
    private void readMessage(QBChatMessage chatMessage){
        try {
            chatDialog.readMessage(chatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
        }
    }
    private boolean isRead(QBChatMessage chatMessage){
        Integer currentUserId = ChatHelper.getCurrentUser().getId();
        return !CollectionsUtil.isEmpty(chatMessage.getReadIds()) && chatMessage.getReadIds().contains(currentUserId);
    }

    private String getAttachmentsType(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        QBAttachment attachment = attachments.iterator().next();
        String ret = attachment.getType();
        return ret;
    }

    private boolean isIncoming(QBChatMessage chatMessage) {
        QBUser currentUser = ChatHelper.getCurrentUser();
        return chatMessage.getSenderId() != null && !chatMessage.getSenderId().equals(currentUser.getId());
    }

    private static class HeaderViewHolder {
        public TextView dateTextView;
    }

    private static class ViewHolder {
        public TextView messageBodyTextView;
        public ImageView userImage;
        public ImageView audio;
        public TextView time;
        public String audioUrl;
        public String audioFilename;
    }

    public interface OnItemInfoExpandedListener {
        void onItemInfoExpanded(int position);
    }
}
