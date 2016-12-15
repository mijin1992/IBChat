package com.brasco.simwechat.view;

/**
 * Created by Administrator on 12/15/2016.
 */

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.SimWeChatApplication;
import com.brasco.simwechat.utils.ResourceUtil;

@SuppressWarnings("deprecation")
public class CameraView extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback {
    public static final String TAG = "CameraView";

    private Context mContext;
    private CustomCameraCallback mCallback = null;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private String mPictureFileName;
    private boolean mPreviewing = false;
    private String mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
    private int mOpenCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    private MediaRecorder mRecorder = null;
    private String mFilename = null;

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Saving a photo to file");
        }
    };

    public CameraView(Context context, CustomCameraCallback callback) {
        super(context);
        Log.d(TAG, "CameraView construct");

        mOpenCamera = CameraInfo.CAMERA_FACING_BACK;
        //		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
        //			int total = Camera.getNumberOfCameras();
        //			if (total >= 2)
        //				mOpenCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        //		}

        mContext = context;
        mFlashMode = Parameters.FLASH_MODE_AUTO;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCallback = callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        Log.d(TAG, "surfaceCreated");

        mCamera = Camera.open(mOpenCamera);
        setCameraDisplayOrientation(mOpenCamera, mCamera);

        mbAutoFocusSupport = false;
        PackageManager pm = mContext.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            mbAutoFocusSupport = true;
        }

        Parameters params = mCamera.getParameters();
        if (mOpenCamera == Camera.CameraInfo.CAMERA_FACING_BACK)
            params.setFlashMode(mFlashMode);

        mCamera.setParameters(params);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mPreviewing = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");

        if (mPreviewing) {
            mCamera.stopPreview();
            mPreviewing = false;
        }

        mIsTakingPhoto = false;
        mbAutoFocusSuccess = false;
        if (mCamera != null)
            ;
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            mPreviewing = true;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void setFlash(String inFlash) {
        mFlashMode = inFlash;
    }

    public String getFlash() {
        return mFlashMode;
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public void setEffect(String effect) {
        setCameraDisplayOrientation(mOpenCamera, mCamera);
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public boolean setCameraResolution(int width, int height) {
        boolean isSuccess = false;
        try {
            Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);
            mCamera.setParameters(params);
            isSuccess = true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    private void setCameraDisplayOrientation(int cameraID2, Camera camera2) {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        }
    }

    public String getPhotoName() {
        return mPictureFileName;
    }

    public void cancel() {
        Log.d(TAG, "cancel");

        if (mCamera != null) {
            try {
                mCamera.cancelAutoFocus();

                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                mPreviewing = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCallback(CustomCameraCallback callback) {
        mCallback = callback;
    }

    public void takePicture(String fileName) {
        Log.d(TAG, "takePicture");

        mPictureFileName = fileName;

        try {
            mIsTakingPhoto = true;
            if (mbAutoFocusSupport) {
                mCamera.autoFocus(mAutoFocusCallback);
            } else {
                mCamera.takePicture(null, null, jpegCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retakePicture() {
        mIsTakingPhoto = false;
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
    }

    public boolean isSupportFrontCamera() {
        boolean bSupport = false;
        int total = Camera.getNumberOfCameras();
        if (total >= 2)
            bSupport = true;

        return bSupport;
    }

    public int getCamera() {
        return mOpenCamera;
    }

    public void startPreview() {
        if (!mPreviewing) {
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            mPreviewing = true;
        }
    }

    public void stopPreview() {
        if (mPreviewing) {
            mCamera.stopPreview();
            mPreviewing = false;
        }
    }

    public void changeFace(boolean bChange) {
        if (bChange) {
            if (mOpenCamera == CameraInfo.CAMERA_FACING_BACK)
                mOpenCamera = CameraInfo.CAMERA_FACING_FRONT;
            else
                mOpenCamera = CameraInfo.CAMERA_FACING_BACK;
        }

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
        mCamera = Camera.open(mOpenCamera);

        mbAutoFocusSupport = false;
        mIsTakingPhoto = false;
        mbAutoFocusSuccess = false;
        PackageManager pm = mContext.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            mbAutoFocusSupport = true;
        }

        Parameters params = mCamera.getParameters();
        if (mOpenCamera == CameraInfo.CAMERA_FACING_BACK)
            params.setFlashMode(mFlashMode);

        mCamera.setParameters(params);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            setCameraDisplayOrientation(mOpenCamera, mCamera);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            mPreviewing = true;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void zoomout() {
        Log.d(TAG, "zoomout");

        Camera.Parameters localParameters = mCamera.getParameters();
        if (localParameters.getZoom() >= 2) {
            localParameters.setZoom(localParameters.getZoom() - 2);
            mCamera.setParameters(localParameters);
        }
    }

    public void zoomin() {
        Log.d(TAG, "zoomin");

        Camera.Parameters localParameters = mCamera.getParameters();
        // int i = ;
        if (localParameters.getZoom() + 2 <= localParameters.getMaxZoom()) {
            localParameters.setZoom(localParameters.getZoom() + 2);
            mCamera.setParameters(localParameters);
        }
    }

    private boolean mIsTakingPhoto = false;
    private boolean mbAutoFocusSuccess = false;
    private boolean mbAutoFocusSupport = false;

    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {

        /**
         * function called when auto focus is completed
         * @param success tells if autofocus where successfully acomplished
         * @param camera android camera object
         */
        public void onAutoFocus(boolean success, Camera camera) {
            mbAutoFocusSuccess = true;
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mIsTakingPhoto && mbAutoFocusSuccess) {
            mIsTakingPhoto = false;

            switch (camera.getParameters().getPreviewFormat()) {
                case ImageFormat.NV21:
                case ImageFormat.NV16:
                    boolean success = false;
                    try {
                        mCamera.stopPreview();

                        Size previewSize = camera.getParameters().getPreviewSize();
                        Rect clipRect = new Rect(0, 0, previewSize.width, previewSize.height);

                        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(clipRect, 80, baos);

                        // Save to file.
                        FileOutputStream fos = new FileOutputStream(mPictureFileName);
                        fos.write(baos.toByteArray());
                        fos.flush();
                        fos.close();

                        success = true;
                        baos.close();

                        if (mCallback != null) {
                            mCallback.onPictureTaken(mPictureFileName);
                            mCamera.setPreviewCallback(null);
                        }
                    } catch (Exception e) {
                        Log.e("onPictureTaken()", "Error converting bitmap");
                        success = false;
                    }

                    // setting result of activity and finishing
                    if (success) {
                    } else {
                        Log.d("CAMERA", "Photo Damaged!!!!");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface CustomCameraCallback {
        public void onPictureTaken(String strFileName);
    }

    public void startVideoRecording() {
        Log.d(TAG, "startVideoRecording");

        mRecorder = new MediaRecorder();
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        mCamera.unlock();
        mRecorder.setCamera(mCamera);

        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        int mCurrentIndex = AppPreference.getInt(AppPreference.CAMERA_RESOLUTION_INDEX, 0);
        CamcorderProfile camcorderProfile = CamcorderProfile.get(AppGlobals.QUALITY_LEVELS.get(mCurrentIndex));
        camcorderProfile.videoBitRate = 4 * 1024 * 1024; // Mbps
        camcorderProfile.videoFrameRate = 30;

        mRecorder.setProfile(camcorderProfile);

        // This is all very sloppy
        try {
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                mRecorder.setOrientationHint(90);

                if (mOpenCamera == CameraInfo.CAMERA_FACING_FRONT)
                    mRecorder.setOrientationHint(270);

            } else {
                if (mOpenCamera == CameraInfo.CAMERA_FACING_FRONT)
                    mRecorder.setOrientationHint(180);
            }

            mFilename = ResourceUtil.getCaptureVideoFilePath(SimWeChatApplication.getContext());
            mRecorder.setOutputFile(mFilename);
            mRecorder.prepare();
            mRecorder.start();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void stopVideoRecording() {
        Log.d(TAG, "stopVideoRecording");
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;

            mCamera.lock();

            mCamera.reconnect();
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRecordedFilename(){
        return mFilename;
    }
}
