package com.brasco.simwechat.utils;

/**
 * Created by Administrator on 12/14/2016.
 */

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.brasco.simwechat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyImageLoader {
    public static final String TAG = "MyImageLoader";

    public static class IMG_TYPE {
        public static int NORMAL = 0;
    }

    private static ImageLoader instance = ImageLoader.getInstance();

    public static DisplayImageOptions normal_options;
    public static DisplayImageOptions profile_options;

    public static void init() {
        normal_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_bg) // resource or drawable
                .showImageForEmptyUri(R.drawable.default_image_bg) // resource or drawable
                .showImageOnFail(R.drawable.default_image_bg) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();

        profile_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.profile_bg) // resource or drawable
                .showImageForEmptyUri(R.drawable.profile_bg) // resource or drawable
                .showImageOnFail(R.drawable.profile_bg) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
    }

    @SuppressWarnings("deprecation")
    public static void clearCache() {
        if (instance != null) {
            instance.clearDiscCache();
            instance.clearMemoryCache();
        }
    }

    public static void stop() {
        if (instance != null) {
            instance.stop();
        }
    }

    public static void showImage(ImageView imgView, String url, SimpleImageLoadingListener listener) {
        if (TextUtils.isEmpty(url))
            return;

        DisplayImageOptions option = normal_options;
        try {
            ImageAware imageAware = new ImageViewAware(imgView, false);
            instance.displayImage(url, imageAware, option, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAvatar(ImageView imgView, String url, SimpleImageLoadingListener listener) {
        if (TextUtils.isEmpty(url))
            return;

        DisplayImageOptions option = profile_options;
        try {
            ImageAware imageAware = new ImageViewAware(imgView, false);
            instance.displayImage(url, imageAware, option, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancel(ImageView imgView) {
        if (imgView != null)
            instance.cancelDisplayTask(imgView);
    }
}
