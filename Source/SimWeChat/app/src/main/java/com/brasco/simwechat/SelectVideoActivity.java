package com.brasco.simwechat;


import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.model.MediaModel;
import com.brasco.simwechat.utils.MessageUtil;
import com.brasco.simwechat.utils.ResourceUtil;

import java.util.ArrayList;

public class SelectVideoActivity extends BaseActionBarActivity implements OnClickListener {
    public static final String TAG = "SelectVideoActivity";

    public static SelectVideoActivity instance = null;

    ListView lst_media;
    View layout_nodata;
    VideoAdapter videoAdapter;

    // data
    ArrayList<MediaModel> mVideoList = new ArrayList<MediaModel>();
    public static MediaModel mSelectedVideoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        instance = this;
        ShowActionBarIcons(true, R.id.action_back, R.id.action_done);
        action_done.setVisibility(View.INVISIBLE);
        SetTitle("Select Video", R.color.white);
        setContentView(R.layout.activity_select_video);

        //
        mVideoList = ResourceUtil.getVideoList();
        if (mVideoList != null && mVideoList.size() > 0) {
            // select first item as default
            mVideoList.get(0).selected = true;
        }

        lst_media = (ListView) findViewById(R.id.lst_media);
        videoAdapter = new VideoAdapter(instance);
        lst_media.setAdapter(videoAdapter);
        layout_nodata = findViewById(R.id.layout_nodata);

        // visible
        if (mVideoList != null && mVideoList.size() > 0) {
            lst_media.setVisibility(View.VISIBLE);
            layout_nodata.setVisibility(View.GONE);
        } else {
            lst_media.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        }

        //
        findViewById(R.id.btn_import).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.btn_import: {
                if (mVideoList == null || mVideoList.size() == 0) {
                    MessageUtil.showToast(instance, "No video file");
                    return;
                }

                for (MediaModel model : mVideoList) {
                    if (model.selected) {
                        mSelectedVideoModel = model;
                        break;
                    }
                }
                if (mSelectedVideoModel != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EK_URL, mSelectedVideoModel.data);
                    setResult(RESULT_OK, intent);
                }
                myBack();
            }
            break;
        }
    }

    private class VideoAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public VideoAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mVideoList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            View layout_container;
            ImageView img_play;
            TextView txt_name;
        }

        @SuppressWarnings("deprecation")
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cell_select_media, null);
                holder = new ViewHolder();
                holder.layout_container = convertView.findViewById(R.id.layout_container);
                holder.img_play = (ImageView) convertView.findViewById(R.id.img_play);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (mVideoList.get(position).selected) {
                holder.layout_container.setBackgroundResource(R.color.orange_light);
                holder.img_play.setImageResource(R.drawable.ic_action_play_grey);
                holder.txt_name.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.layout_container.setBackgroundResource(R.color.transparent);
                holder.img_play.setImageResource(R.drawable.ic_action_play_orange);
                holder.txt_name.setTextColor(getResources().getColor(R.color.black));
            }

            holder.txt_name.setText(mVideoList.get(position).display_name);

            holder.layout_container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    for (MediaModel model : mVideoList) {
                        if (model.selected) {
                            model.selected = false;
                            break;
                        }
                    }
                    mVideoList.get(position).selected = true;

                    videoAdapter.notifyDataSetChanged();
                }
            });
            holder.img_play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
//                    VitamioStreamViewActivity.mVideoPath = mVideoList.get(position).data;
//                    VitamioStreamViewActivity.mDownloadPath = null;
//                    startActivity(new Intent(instance, VitamioStreamViewActivity.class));
                }
            });

            return convertView;
        }
    }
}
