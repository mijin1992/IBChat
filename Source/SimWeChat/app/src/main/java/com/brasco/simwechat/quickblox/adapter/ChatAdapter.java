package com.brasco.simwechat.quickblox.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brasco.simwechat.ChatActivity;
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
        Boolean isAttachedFile = hasAttachments(chatMessage);
        String type = "";
        if (isAttachedFile)
            type = getAttachmentsType(chatMessage);
        final ViewHolder holder;
        holder = new ViewHolder();

        if (isAttachedFile && type.equals(QBAttachment.PHOTO_TYPE)){
//            convertView = inflater.inflate(R.layout.cell_chat_list_video, parent, false);
//            holder.videoTime = (TextView) convertView.findViewById(R.id.lbl_time_ago);
//            holder.attachmentImageView = (ImageView) convertView.findViewById(R.id.image_attached_file);
//            holder.attachmentProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_attachment_file);
//            holder.emptyLayout = (LinearLayout) convertView.findViewById(R.id.layout_empty_Image_view);
        } else {
            LogUtil.writeDebugLog(TAG, "getView", "3");
//            convertView = inflater.inflate(R.layout.cell_chat_list_left, parent, false);
//            holder.messageBodyTextView = (TextView) convertView.findViewById(R.id.txt_message);
//            holder.userImage = (CircularImageView) convertView.findViewById(R.id.img_message_user_logo);
//            holder.attachmentImageView = (ImageView) convertView.findViewById(R.id.image_attached_file);
//            holder.attachmentImageView.setVisibility(View.GONE);
//            holder.attachmentProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_attachment_file);
//            holder.attachmentProgressBar.setVisibility(View.GONE);
//            holder.imgviewUserLogo = (ImageView) convertView.findViewById(R.id.imgview_user_logo);
//            holder.mUserLogoProgressbar = (ProgressBar) convertView.findViewById(R.id.progress_user_logo);
        }

        setMessageBody(holder, chatMessage);

        if (isIncoming(chatMessage) && !isRead(chatMessage)){
            readMessage(chatMessage);
        }

        downloadMore(position);
        return convertView;
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
        LogUtil.writeDebugLog(TAG, "setMessageBody", "1");
        Boolean hasAttachments = hasAttachments(chatMessage);
        String type = "";
        if (hasAttachments)
            type = getAttachmentsType(chatMessage);

        boolean isIncoming = isIncoming(chatMessage);
        if (hasAttachments && type.equals(QBAttachment.AUDIO_TYPE)) {
            Collection<QBAttachment> attachments = chatMessage.getAttachments();
            QBAttachment attachment = attachments.iterator().next();
            Integer id = Integer.parseInt(attachment.getId());
            String url = attachment.getUrl();
            LogUtil.writeDebugLog(TAG, "setMessageBody", "2");
            //TryDownloadFile(url, id + ".mp4");
            if (isIncoming) {
                //mActivity.setImageFilePathForReceived(url);
                holder.attachmentProgressBar.setVisibility(View.GONE);
                //mActivity.showViewButton();
            }else{
                holder.emptyLayout.setVisibility(View.GONE);
                holder.attachmentImageView.setVisibility(View.VISIBLE);
                holder.attachmentProgressBar.setVisibility(View.VISIBLE);
                LogUtil.writeDebugLog(TAG, "setMessageBody", "3_"+attachment.getUrl());
                Glide.with(context)
                        .load(attachment.getUrl())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<GlideDrawable> target, boolean isFirstResource) {
                                e.printStackTrace();
                                holder.attachmentImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                holder.attachmentProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.attachmentImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                holder.attachmentProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
//                        .dontTransform()
//                        .error(R.drawable.ic_error)
                        .into(holder.attachmentImageView);
            }
            Date date = new Date();
            long curTime = date.getTime();
            curTime = curTime / 1000;
            long sentTime = chatMessage.getDateSent();
            int hours = (int) ((curTime - sentTime)/(60 * 60));
            int minutes = (int) ((curTime - sentTime)/(60)) - hours * 60;
            holder.videoTime.setText(String.valueOf(hours) + " hours " + String.valueOf(minutes) + " mins ago");
        } else {
            LogUtil.writeDebugLog(TAG, "setMessageBody", "4");
            holder.messageBodyTextView.setText(chatMessage.getBody());
            holder.messageBodyTextView.setVisibility(View.VISIBLE);
            holder.attachmentImageView.setVisibility(View.GONE);
            holder.attachmentProgressBar.setVisibility(View.GONE);

            QBUser qbuser;
            if (isIncoming){
//                qbuser = AppGlobals.curChattingUser.getQBUser();
            } else {
                qbuser = QBData.curQBUser;
            }
//            Integer fileid = qbuser.getFileId();
//            String strUrl= qbuser.getCustomData();
//            if(strUrl != null && !strUrl.isEmpty()) {
//                LogUtil.writeDebugLog(TAG, "setMessageBody", "5");
//                Glide.with(mActivity)
//                        .load(strUrl)
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model,
//                                                       Target<GlideDrawable> target, boolean isFirstResource) {
//                                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.empty_user_logo);
//                                holder.userImage.setImageBitmap(bm);
//                                holder.mUserLogoProgressbar.setVisibility(View.GONE);
//                                holder.imgviewUserLogo.setVisibility(View.GONE);
//                                return false;
//                            }
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model,
//                                                           Target<GlideDrawable> target,
//                                                           boolean isFromMemoryCache, boolean isFirstResource) {
//                                Bitmap bmp =  ((GlideBitmapDrawable)resource.getCurrent()).getBitmap();
//                                holder.userImage.setImageBitmap(bmp);
//                                holder.mUserLogoProgressbar.setVisibility(View.GONE);
//                                holder.imgviewUserLogo.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
//                        .override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
//                        .error(R.drawable.ic_error)
//                        .into(holder.imgviewUserLogo);
//            } else {
//                holder.mUserLogoProgressbar.setVisibility(View.GONE);
//                holder.imgviewUserLogo.setVisibility(View.GONE);
//                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.empty_user_logo);
//                holder.userImage.setImageBitmap(bm);
//            }
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
        public CircularImageView userImage;
        public ImageView attachmentImageView;
        public ProgressBar attachmentProgressBar;
        public TextView videoTime;
        public LinearLayout emptyLayout;
        public ImageView imgviewUserLogo;
        public ProgressBar mUserLogoProgressbar;
    }

    public interface OnItemInfoExpandedListener {
        void onItemInfoExpanded(int position);
    }
}
