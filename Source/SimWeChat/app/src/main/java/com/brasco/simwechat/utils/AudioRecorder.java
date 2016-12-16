package com.brasco.simwechat.utils;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 12/16/2016.
 */

public class AudioRecorder {
    private MediaRecorder recorder = new MediaRecorder();

    private String outfilePath = null;

    public AudioRecorder(){}

    public void startRecording(String filePath) throws IOException {
        outfilePath = filePath;
//        ContentValues values = new ContentValues(3);
//        values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(outfilePath);
        try{
            recorder.prepare();
        }catch(IllegalStateException e){
            e.printStackTrace();
        }

        recorder.start();
    }

    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }

    public String getOutfilePath(){
        return outfilePath;
    }
}
