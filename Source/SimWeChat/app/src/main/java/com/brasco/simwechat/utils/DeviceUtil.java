package com.brasco.simwechat.utils;

/**
 * Created by Administrator on 12/15/2016.
 */
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.brasco.simwechat.app.SimWeChatApplication;

@SuppressWarnings("deprecation")
public class DeviceUtil {
    /*
     * network connection
     */
    public static final String TAG = "DeviceUtil";

    public static boolean isNetworkAvailable(Context context) {
        boolean isConnected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return isConnected;
    }

    /*
     * Location service
     */
    public static boolean isLocationServiceAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //return isGPSEnabled || isNetworkEnabled;
        return isGPSEnabled;
    }

    public static String getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(SimWeChatApplication.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address == null)
                    return String.format(Locale.getDefault(), "*Lon:%.3f, Lat:%.3f", longitude, latitude);

                String country = address.getCountryName();
                if (country == null) {
                    country = "";
                }
                String adminArea = address.getAdminArea();
                if (adminArea == null) {
                    adminArea = "";
                }
                String locality = address.getLocality();
                if (locality == null) {
                    locality = "";
                }
                String thoroghfare = address.getThoroughfare();
                if (thoroghfare == null) {
                    thoroghfare = "";
                }
                String subthoroghfare = address.getSubThoroughfare();
                if (subthoroghfare == null) {
                    subthoroghfare = "";
                }
                String area = address.getSubLocality();
                if (area == null) {
                    area = "";
                }

                String locationName = subthoroghfare + " " + thoroghfare + " " + locality + " " + adminArea + " " + country;
                locationName = locationName.replace("  ", " ");
                locationName = locationName.replace("  ", " ");

                return locationName;

            } else {
                return String.format(Locale.getDefault(), "*Lon:%.3f, Lat:%.3f", longitude, latitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean IsAbleToFlashCamera() {
        return SimWeChatApplication.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private static Camera flashCamera;
    public static boolean IsFlashOn = false;
    public static void FlashLEDOn() {
        if (IsAbleToFlashCamera()) {
            try {
                flashCamera = Camera.open();
                flashCamera.setPreviewTexture(new SurfaceTexture(0));

                Parameters params = flashCamera.getParameters();
                List<String> supportedFlashModes = params.getSupportedFlashModes();
                if (supportedFlashModes.contains(Parameters.FLASH_MODE_TORCH)) {
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                } else if (supportedFlashModes.contains(Parameters.FLASH_MODE_ON)) {
                    params.setFlashMode(Parameters.FLASH_MODE_ON);
                }
                flashCamera.setParameters(params);
                flashCamera.startPreview();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            IsFlashOn = true;
        }
    }

    public static void FlashLEDOff() {
        if (flashCamera != null) {
            flashCamera.stopPreview();
            flashCamera.release();

            IsFlashOn = false;
        }
    }

    private static Ringtone ringtone = null;
    private static int oldAudioVolumeSize = -1;
    public static void PlayRingPhoneInMaxSound() {
        AudioManager audio = (AudioManager) SimWeChatApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        oldAudioVolumeSize = audio.getStreamVolume(AudioManager.STREAM_RING);
        audio.setStreamVolume(AudioManager.STREAM_RING,
                audio.getStreamMaxVolume(AudioManager.STREAM_RING),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(SimWeChatApplication.getContext(), ringUri);
        ringtone.play();
    }

    public static void StopRingPhoneInMaxSound() {
        if (ringtone != null) {
            ringtone.stop();
        }

        if (oldAudioVolumeSize > 0) {
            AudioManager audio = (AudioManager) SimWeChatApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamVolume(AudioManager.STREAM_RING,	oldAudioVolumeSize,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }
}

