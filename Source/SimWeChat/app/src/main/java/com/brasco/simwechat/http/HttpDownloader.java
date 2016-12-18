package com.brasco.simwechat.http;

/**
 * Created by Administrator on 12/19/2016.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Environment;

public class HttpDownloader extends AsyncTask<Object, Object, String> {

    public interface HttpDownloaderListener {
        public void OnDownloaderResult(String a_strPath);
    }
    private String						m_strUrl;
    private String						m_strFilePath;
    private String						m_strFileName;
    private boolean						m_bForceRenew;
    private HttpDownloaderListener		m_httpDownloaderListener;

    //------------------------------------------------------------------------------
    public HttpDownloader() {
        m_strUrl					= null;
        m_strFilePath				= null;
        m_strFileName				= null;
        m_bForceRenew				= false;
        m_httpDownloaderListener	= null;
    }

    //------------------------------------------------------------------------------
    public void SetUrl(String a_strUrl) {
        m_strUrl = a_strUrl;
    }

    //------------------------------------------------------------------------------
    public void SetFilePath(String a_strFilePath) {
        m_strFilePath = a_strFilePath;
    }

    //------------------------------------------------------------------------------
    public void SetFileName(String a_strFileName) {
        m_strFileName = a_strFileName;
    }

    //------------------------------------------------------------------------------
    public void SetForceRenew(boolean a_bForceRenew) {
        m_bForceRenew = a_bForceRenew;
    }

    //------------------------------------------------------------------------------
    public void SetDownloaderListener(HttpDownloaderListener a_httpDownloaderListener) {
        m_httpDownloaderListener = a_httpDownloaderListener;
    }

    //------------------------------------------------------------------------------
    @Override
    protected String doInBackground(Object... params) {
        URL					url = null;
        HttpURLConnection	httpUrlConnection = null;
        int					iLenRead		= 0;
        byte[]				pcBuffer	= new byte[1024 * 8];
        InputStream			inputStream;
        String extStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_strFilePath = extStoragePath + "/SimWeChat/audio";
        File myDir = new File(m_strFilePath);
        myDir.mkdirs();
        File				fileOutput = new File(m_strFilePath, m_strFileName);
        FileOutputStream	fileOutputStream;

        if (fileOutput.exists()) {
            if (m_bForceRenew)
                fileOutput.delete();
            else
                return fileOutput.getAbsolutePath();
        }

        try {
            fileOutput.createNewFile();
        } catch (Exception e) {
            return null;
        }

        try {
            fileOutputStream = new FileOutputStream(fileOutput);
        }catch (Exception e) {
            return null;
        }

        try { url = new URL(m_strUrl); }
        catch (Exception e) {
            try { fileOutputStream.close(); } catch (Exception ex) { return null; }
            return null;
        }

        try {
            httpUrlConnection = (HttpURLConnection)url.openConnection();
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.connect();

            inputStream = httpUrlConnection.getInputStream();

            while((iLenRead = inputStream.read(pcBuffer)) > 0) {
                if (iLenRead <= 0) 		break;
                try {
                    fileOutputStream.write(pcBuffer, 0, iLenRead);
                }
                catch (IOException e) {
                    break;
                };
            }
            inputStream.close();
            fileOutputStream.close();

            httpUrlConnection.disconnect();
            httpUrlConnection = null;
            return fileOutput.getAbsolutePath();
        }
        catch (Exception e) {
            if (httpUrlConnection != null)
                httpUrlConnection.disconnect();
        }

        return null;
    }

    //------------------------------------------------------------------------------
    @Override
    protected void onPostExecute(String result) {
        m_httpDownloaderListener.OnDownloaderResult(result);
    }

    //------------------------------------------------------------------------------
}

