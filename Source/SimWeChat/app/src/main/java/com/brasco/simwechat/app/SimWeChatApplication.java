package com.brasco.simwechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.brasco.simwechat.utils.MyImageLoader;
import com.brasco.simwechat.quickblox.core.CoreApp;
import com.brasco.simwechat.quickblox.core.utils.ActivityLifecycle;
import com.brasco.simwechat.quickblox.utils.QBResRequestExecutor;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by Administrator on 12/14/2016.
 */

public class SimWeChatApplication extends CoreApp {
    public static final String TAG = SimWeChatApplication.class.getSimpleName();
    public static Context mContext;
    public static String mPackageName;
    private QBResRequestExecutor qbResRequestExecutor;
    private static SimWeChatApplication instance;

    public static SimWeChatApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityLifecycle.init(this);

        instance = this;

        initCredentials(Constant.QB_APP_ID, Constant.QB_AUTH_KEY, Constant.QB_AUTH_SECRET, Constant.QB_ACCOUNT_KEY);

        mContext = this.getApplicationContext();

        // window size
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        AppGlobals.SCREEN_WIDTH = display.getWidth();
        AppGlobals.SCREEN_HEIGHT = display.getHeight();

        // preference
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        AppPreference.initialize(pref);

        // get package name
        mPackageName = mContext.getPackageName();

		/*
		 * Initialize Image loader
		 */
        initImageLoader(mContext);
        new MyImageLoader();
        MyImageLoader.init();

		/*
		 * app's initialize
		 */
        AppGlobals.init();
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getAppPackageName() {
        if (TextUtils.isEmpty(mPackageName))
            return "";

        return mPackageName;
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }
}

