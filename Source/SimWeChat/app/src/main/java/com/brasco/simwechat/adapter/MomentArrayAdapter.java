package com.brasco.simwechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brasco.simwechat.MomentActivity;
import com.brasco.simwechat.PostActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.model.FirePost;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.brasco.simwechat.quickblox.QBData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.users.model.QBUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 12/23/2016.
 */

public class MomentArrayAdapter extends ArrayAdapter<FirePost> {
private LayoutInflater mInflater;
private MomentActivity mActivity;

static class Holder {
    ImageView m_userImage;
    ImageView m_momentImage;
    TextView m_name;
    TextView m_txtComment;
    TextView m_txtTime;
}

    public MomentArrayAdapter(MomentActivity context, List<FirePost> posts) {
        super(context, R.layout.item_moment, posts);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivity = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        convertView = mInflater.inflate(R.layout.item_moment, parent, false);
        holder = new Holder();
        holder.m_userImage = (ImageView) convertView.findViewById(R.id.moment_avatar);
        holder.m_momentImage = (ImageView) convertView.findViewById(R.id.moment_image);
        holder.m_name = (TextView) convertView.findViewById(R.id.moment_name);
        holder.m_txtComment = (TextView) convertView.findViewById(R.id.moment_comment);
        holder.m_txtTime = (TextView) convertView.findViewById(R.id.moment_time);
        convertView.setTag(holder);

        holder.m_txtComment.setText(getItem(position).getComment());
        long postTime = Long.parseLong(getItem(position).getTime());
        Date date = new Date();
        long curTime = date.getTime();
        curTime = curTime / 1000;
        postTime = postTime /1000;
        int days = (int) ((curTime - postTime)/(60 * 60 * 24));
        int hours = (int) ((curTime - postTime)/(60 * 60));
        int minutes = (int) ((curTime - postTime)/(60));
        String strTime = "Just now";
        if (minutes >= 1)
            strTime = String.valueOf(minutes) + " minutes ago";
        if (hours >= 1)
            strTime = String.valueOf(hours) + " hours ago";
        if (days >= 1)
            strTime = String.valueOf(days) + " days ago";

        holder.m_txtTime.setText(strTime);
        String url = getItem(position).getImageUrl();
        Glide.with(mActivity)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
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
                .into(holder.m_momentImage);

        holder.m_name.setText(getItem(position).getAuthorName());

        QBUser qbUser = getQBUserFromUserId(getItem(position).getAuthorQbId());
        if (qbUser != null){
            String strUrl= qbUser.getCustomData();
            if(strUrl != null && !strUrl.isEmpty()) {
                Glide.with(mActivity)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<GlideDrawable> target, boolean isFirstResource) {
                                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                                holder.m_userImage.setImageBitmap(bm);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
                        .error(R.drawable.ic_error)
                        .into(holder.m_userImage);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
                holder.m_userImage.setImageBitmap(bm);
            }
        } else {
            Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
            holder.m_userImage.setImageBitmap(bm);
        }

        return convertView;
    }

    private QBUser getQBUserFromUserId(String userId){
        if (QBData.curQBUser.getLogin().equals(userId))
            return QBData.curQBUser;

        for (int i=0; i< AppGlobals.mAllUserData.size(); i++){
            UserData user = AppGlobals.mAllUserData.get(i);
            if (user.getQBUser().getLogin().equals(userId))
                return user.getQBUser();
        }
        return null;
    }
}
