package com.brasco.simwechat.utils;

/**
 * Created by Administrator on 12/14/2016.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Surface;

import com.brasco.simwechat.app.SimWeChatApplication;
import com.brasco.simwechat.model.MediaModel;

@SuppressWarnings("deprecation")
public class ResourceUtil {
    public static final String TAG = "ResourceUtil";

    public static String RES_DIRECTORY = Environment.getExternalStorageDirectory() + "/Bombo/";
    public static String FILE_EXTENSION = ".veew";

    /*
     * Resource directory
     */
    public static String getResourceDirectory() {
        String tempDirPath = RES_DIRECTORY;
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists())
            tempDir.mkdirs();

        return tempDirPath;
    }

    public static String getDownloadDirectory() {
        String tempDirPath = RES_DIRECTORY + "Donwload/";
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists())
            tempDir.mkdirs();

        return tempDirPath;
    }

    public static String getDownloadFilePath(String fileName) {
        return getDownloadDirectory() + fileName + FILE_EXTENSION;
    }

    /*
     * File
     */
    public static String getCapturedImageFilePath() {
        String tempDirPath = RES_DIRECTORY;
        String tempFileName = "camera.jpg";

        File tempDir = new File(tempDirPath);
        if (!tempDir.exists())
            tempDir.mkdirs();
        File tempFile = new File(tempDirPath + tempFileName);
        if (!tempFile.exists())
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return tempDirPath + tempFileName;
    }
    public static String getAvatarFilePath() {
        String tempDirPath = RES_DIRECTORY;
        String tempFileName = "avatar.jpg";

        File tempDir = new File(tempDirPath);
        if (!tempDir.exists())
            tempDir.mkdirs();
        File tempFile = new File(tempDirPath + tempFileName);
        if (!tempFile.exists())
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return tempDirPath + tempFileName;
    }

    /*
     * Post
     */
    // photo
    private static String mPhotoFileExtension = "jpg";
    public static void setPhotoExtension(String fileExtension) {
        mPhotoFileExtension = fileExtension;
    }
    public static String getCaptureImageFilePath(Context context) {
        Date date = new Date();
        String partname = String.valueOf(date.getTime());
        return getResourceDirectory() + "post_image_" + partname + "."+mPhotoFileExtension;
    }

    // video
    private static String mVideoFileExtension = "mp4";
    public static void setVideoExtension(String fileExtension) {
        mVideoFileExtension = fileExtension;
    }

    public static String getVideoExtension() {
        return mVideoFileExtension;
    }

    public static String getCaptureVideoFilePath(Context context) {
        Date date = new Date();
        String partname = String.valueOf(date.getTime());
        return getResourceDirectory() + "video_" + partname + "." + mVideoFileExtension;
    }

    public static String getTrimedVideoFilePath(Context context) {
        return getResourceDirectory() + "post_trimed_video." + mVideoFileExtension;
    }

    public static String getVideoThumbnailFilePath(Context context) {
        return getResourceDirectory() + "video_thumbnail.bmp";
    }

    /*
     * Import video
     */
    public static ArrayList<MediaModel> getVideoList() {
        ArrayList<MediaModel> songList = new ArrayList<MediaModel>();
        if (songList.size() == 0) {
            ContentResolver cr = SimWeChatApplication.getContext().getContentResolver();

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            Cursor cur = cr.query(uri, null, null, null, null);
            int count = 0;

            if (cur != null) {
                count = cur.getCount();
                if (count > 0) {
                    while(cur.moveToNext()) {
                        MediaModel media = new MediaModel();

                        media.id = cur.getString(cur.getColumnIndex(MediaStore.Video.Media._ID));
                        media.artist = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.ARTIST));
                        media.title = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.TITLE));
                        media.data = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DATA));
                        media.display_name = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        media.duration = cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DURATION));

                        songList.add(media);
                    }
                }
                cur.close();
            }
        }

        return songList;
    }

    /*
     * Device orientation
     */
    public static int getRotationAngle(Activity mContext, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
