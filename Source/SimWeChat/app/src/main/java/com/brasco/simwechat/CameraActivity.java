package com.brasco.simwechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brasco.simwechat.app.AppGlobals;
import com.brasco.simwechat.app.AppPreference;
import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.dialog.MyProgressDialog;
import com.brasco.simwechat.utils.MessageUtil;
import com.brasco.simwechat.utils.ResourceUtil;
import com.brasco.simwechat.view.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("deprecation")
public class CameraActivity extends BaseActionBarActivity implements View.OnClickListener, CameraView.CustomCameraCallback {
    public static final String TAG = "CameraActivity";
    public static CameraActivity instance = null;
    // UI
    private LinearLayout camera_preview;
    private CameraView mCameraView;
    private ImageView img_face_flip;
    private TextView txt_resolution;
    private TextView txt_time;
    private ImageView img_gallery;
    private ImageView img_capture;
    private ImageView img_video_photo;

    // Data
    private int capture_state = Constant.CAPTURE_PHOTO;
    private int record_state = Constant.CAPTURE_VIDEO_STOP;

    private int mRecordTime = 0;

    private MyProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        SetTitle(R.string.mode_picture, R.color.white);
        ShowActionBarIcons(true, R.id.action_back, R.id.action_done);
        action_done.setVisibility(View.INVISIBLE);
        setContentView(R.layout.activity_camera);

        progressDialog = new MyProgressDialog(this, 0);

        camera_preview = (LinearLayout) findViewById(R.id.camera_preview);
        txt_resolution = (TextView) findViewById(R.id.txt_resolution);
        txt_time = (TextView) findViewById(R.id.txt_time);
        img_face_flip = (ImageView) findViewById(R.id.img_face_flip);
        img_gallery = (ImageView) findViewById(R.id.img_gallery);
        img_capture = (ImageView) findViewById(R.id.img_capture);
        img_video_photo = (ImageView) findViewById(R.id.img_video_photo);

        txt_resolution.setOnClickListener(this);
        img_face_flip.setOnClickListener(this);
        img_gallery.setOnClickListener(this);
        img_capture.setOnClickListener(this);
        img_video_photo.setOnClickListener(this);

        // update hander
        mUpdateHander = new Handler();

        String reqStr = getIntent().getStringExtra(Constant.REQ_IMAGE_CAMERAACTIVITY_TYPE);
        //if (reqStr.equals(Constant.REQ_IMAGE_TYPE))
        {
            //    selectCategory();
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent intent = Intent.createChooser(getIntent, "Select Image");
            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
            startActivityForResult(intent, Constant.REQ_IMAGE_FROM_GALLERY);
        }

        img_video_photo.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mCameraView = new CameraView(this, this);
        camera_preview.removeAllViews();
        camera_preview.addView(mCameraView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));

        int cameraResolutionIndex = AppPreference.getInt(AppPreference.CAMERA_RESOLUTION_INDEX, 0);
        txt_resolution.setText(AppGlobals.QUALITY_LABELS.get(cameraResolutionIndex)
                + AppGlobals.RESOLUTION_LABELS.get(cameraResolutionIndex));

        enableControls(true, false);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();
        if (capture_state == Constant.CAPTURE_VIDEO) {
            if (record_state == Constant.CAPTURE_VIDEO_START) {
                record_state = Constant.CAPTURE_VIDEO_STOP;
                img_capture.setImageResource(R.drawable.camera_start);
                enableControls(true, false);
                mCameraView.stopVideoRecording();
            }
        }

        mCameraView.cancel();

        mUpdateHander.removeCallbacks(mUpdateRunnable);

        //
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mRecordTime = 0;
                txt_time.setText("00:00");
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        super.onDestroy();
        if (capture_state == Constant.CAPTURE_VIDEO) {
            if (record_state == Constant.CAPTURE_VIDEO_START) {
                record_state = Constant.CAPTURE_VIDEO_STOP;
                img_capture.setImageResource(R.drawable.camera_start);
                enableControls(true, false);
                mCameraView.stopVideoRecording();
            }
        }

        mCameraView.cancel();
    }

    public void enableControls(boolean bEnable, boolean bIncCaptureview) {
        if (bIncCaptureview) {
            img_capture.setClickable(bEnable);
        }
        img_capture.setImageResource(bEnable ? R.drawable.camera_start : R.drawable.camera_stop);
        img_face_flip.setClickable(bEnable);
        img_gallery.setClickable(bEnable);
        img_video_photo.setClickable(bEnable);
    }

    @SuppressLint("SimpleDateFormat")
    private void captureScreen() {
        Log.e(TAG, "clicked captureScreen");

        if (capture_state == Constant.CAPTURE_PHOTO) {
            //progressDialog.show();
            ResourceUtil.setPhotoExtension("jpg");
            String fileName = ResourceUtil.getCaptureImageFilePath(this);
            File tempFile = new File(fileName);

            if (tempFile.exists()) {
                tempFile.delete();
            }

            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCameraView.takePicture(fileName);

        } else if (capture_state == Constant.CAPTURE_VIDEO) {
            if (record_state == Constant.CAPTURE_VIDEO_STOP) {
                record_state = Constant.CAPTURE_VIDEO_START;
                img_capture.setImageResource(R.drawable.camera_stop);
                enableControls(false, false);
                mCameraView.startVideoRecording();
                MessageUtil.showToast(instance, "Start recording...");
                mUpdateRunnable.run();
            } else if (record_state == Constant.CAPTURE_VIDEO_START) {
                record_state = Constant.CAPTURE_VIDEO_STOP;
                img_capture.setImageResource(R.drawable.camera_start);
                enableControls(true, false);
                mCameraView.stopVideoRecording();
                MessageUtil.showToast(instance, "Stop recording...");

                Intent intent = new Intent();
                intent.putExtra(Constant.EK_URL, mCameraView.getRecordedFilename());
                setResult(RESULT_OK, intent);
                myBack();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_resolution: {
                String[] resoltionLabelArr = new String[AppGlobals.RESOLUTION_SIZES.size()];
                resoltionLabelArr = AppGlobals.RESOLUTION_SIZES.toArray(resoltionLabelArr);

                int cameraResolutionIndex = AppPreference.getInt(AppPreference.CAMERA_RESOLUTION_INDEX, 0);
                new AlertDialog.Builder(instance)
                        .setSingleChoiceItems(resoltionLabelArr, cameraResolutionIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();

                                AppPreference.setInt(AppPreference.CAMERA_RESOLUTION_INDEX, which);
                                txt_resolution.setText(AppGlobals.QUALITY_LABELS.get(which)
                                        + AppGlobals.RESOLUTION_LABELS.get(which));
                            }
                        })
                        .show();
            } break;

            case R.id.img_face_flip: {
                mCameraView.changeFace(true);
            } break;

            case R.id.img_gallery:
                if (capture_state == Constant.CAPTURE_PHOTO) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent intent = Intent.createChooser(getIntent, "Select Image");
                    intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(intent, Constant.REQ_IMAGE_FROM_GALLERY);

                } else {
                    Intent intent = new Intent(instance, SelectVideoActivity.class);
                    startActivityForResult(intent, Constant.REQ_VIDEO_FROM_GALLERY);
                }
                break;

            case R.id.img_capture:
                captureScreen();
                break;

            case R.id.img_video_photo:
                selectCategory();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK) {

            if(requestCode == Constant.REQ_IMAGE_FROM_GALLERY) {
                if (data == null)
                    return;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    String filename = ResourceUtil.getCaptureImageFilePath(instance);
                    File destFile = new File(filename);
                    FileOutputStream out = new FileOutputStream(destFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    inputStream.close();
                    out.close();
                    long length = destFile.length();
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EK_URL, filename);
                    setResult(RESULT_OK, intent);
                    myBack();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == Constant.REQ_VIDEO_FROM_GALLERY) {
                String origPath = data.getStringExtra(Constant.EK_URL);
                ResourceUtil.setVideoExtension(FilenameUtils.getExtension(origPath));
                Intent intent = new Intent();
                intent.putExtra(Constant.EK_URL, origPath);
                setResult(RESULT_OK, intent);
                myBack();
            }
        }
//        if(requestCode == Constant.REQ_PHOTO_POST && resultCode == Activity.RESULT_FIRST_USER) {
//            myBack();
//        }
    }

    private void selectCategory() {
        Log.e(TAG, "clicked selectCategory");

        if (capture_state == Constant.CAPTURE_PHOTO) {
            capture_state = Constant.CAPTURE_VIDEO;
            txt_time.setVisibility(View.VISIBLE);
            img_video_photo.setImageResource(R.drawable.video);
            SetTitle(R.string.mode_video, R.color.white);

        } else if (capture_state == Constant.CAPTURE_VIDEO) {
            capture_state = Constant.CAPTURE_PHOTO;
            txt_time.setVisibility(View.GONE);
            img_video_photo.setImageResource(R.drawable.photo);
            SetTitle(R.string.mode_picture, R.color.white);
        }
    }

    @Override
    public void onPictureTaken(String strFileName) {
        int cameraID = mCameraView.getCamera();
        int rotation = ResourceUtil.getRotationAngle(this, cameraID);
        if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT)
            rotation = (360 - rotation) % 360; // compensate the mirror

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(strFileName);
        }
        catch(Exception e) {
            Log.d(TAG, "--- saveToCameraFile 21");
            e.printStackTrace();
            bm = null;
        }
        Log.d(TAG, "--- saveToCameraFile 3");
        if (bm == null) {
            Log.d(TAG, "--- saveToCameraFile 31");
            mCameraView.startPreview();
            MessageUtil.showToast(instance, "Cannot get camera capture image");
            return;
        }

        Log.d(TAG, "--- saveToCameraFile 4");
        // make rotate image
        Bitmap rotatedBitmap = null;
        if (rotation > 0) {
            Log.d(TAG, "--- saveToCameraFile 41");
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            try {
                Log.d(TAG, "--- saveToCameraFile 42");
                rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }
            catch(Exception e) {
                Log.d(TAG, "--- saveToCameraFile 43");
                e.printStackTrace();
                rotatedBitmap = null;
            }
        }
        else
            rotatedBitmap = bm;

        if (rotatedBitmap == null) {
            Log.d(TAG, "--- saveToCameraFile 44");
            mCameraView.startPreview();
            MessageUtil.showToast(instance, "Cannot make rotate image");
            bm.recycle();
            return;
        }
        if (bm != rotatedBitmap)
            bm.recycle();

        Log.d(TAG, "--- write to " + strFileName);
        File file = new File(strFileName);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(strFileName);
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //progressDialog.hide();
        long length = file.length();
        Intent intent = new Intent();
        intent.putExtra(Constant.EK_URL, strFileName);
        setResult(RESULT_OK, intent);
        myBack();
    }

    // Task
    Handler mUpdateHander;
    Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();
            msg.what = MSG_UPDATE;
            mUIHandler.sendMessage(msg);

            mUpdateHander.postDelayed(mUpdateRunnable, 1000);
        }
    };

    private static final int MSG_UPDATE = 3423;
    @SuppressLint("HandlerLeak")
    Handler mUIHandler = new Handler() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            switch (msg.what) {
                case MSG_UPDATE: {
                    mRecordTime++;
                    int sec = mRecordTime % 60;
                    int min = mRecordTime / 60;
                    txt_time.setText(String.format("%02d:%02d", min, sec));
                } break;
            }
        }
    };
}
