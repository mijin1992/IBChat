package com.brasco.simwechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brasco.simwechat.PostActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.model.FirePost;
import com.brasco.simwechat.quickblox.QBConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 12/23/2016.
 */

public class PostArrayAdapter extends ArrayAdapter<FirePost> {
    private LayoutInflater mInflater;
    private PostActivity mActivity;

    static class Holder {
        ImageView m_image;
        TextView m_txtDay;
        TextView m_txtMonth;
        TextView m_txtComment;
    }

    public PostArrayAdapter(PostActivity context, List<FirePost> posts) {
        super(context, R.layout.item_post, posts);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivity = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        convertView = mInflater.inflate(R.layout.item_post, parent, false);
        holder = new Holder();
        holder.m_image = (ImageView) convertView.findViewById(R.id.post_image);
        holder.m_txtDay = (TextView) convertView.findViewById(R.id.post_time_date);
        holder.m_txtMonth = (TextView) convertView.findViewById(R.id.post_time_month);
        holder.m_txtComment = (TextView) convertView.findViewById(R.id.post_comment);
        convertView.setTag(holder);

        holder.m_txtComment.setText(getItem(position).getComment());
        long time = Long.parseLong(getItem(position).getTime());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        String strMonth = sdfMonth.format(time);
        String strDay = sdfDay.format(time);
        holder.m_txtMonth.setText(strMonth);
        holder.m_txtDay.setText(strDay);
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
                .into(holder.m_image);

        return convertView;
    }
}