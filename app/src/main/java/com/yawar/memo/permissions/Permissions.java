package com.yawar.memo.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yawar.memo.constant.AllConstants;


public class Permissions {


    public boolean isStorageReadOk(Context context, String perm) {
//         String readImagePermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? perm : Manifest.permission.WRITE_EXTERNAL_STORAGE;

        return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean isStorageWriteOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean manageStoreage(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStorage(Activity activity) {
        String readImagePermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        String readAudioPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_AUDIO : Manifest.permission.READ_EXTERNAL_STORAGE;
        String readVideoPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_VIDEO : Manifest.permission.READ_EXTERNAL_STORAGE;



        ActivityCompat.requestPermissions(activity, new String[]{readImagePermission, readAudioPermission,readVideoPermission}, AllConstants.STORAGE_REQUEST_CODE);
//        ActivityCompat.requestPermissions(activity, new String[]{readImagePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE}, AllConstants.STORAGE_REQUEST_CODE);
    }

    public boolean isContactOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestContact(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, AllConstants.CONTACTS_REQUEST_CODE);
    }

    public boolean isRecordingOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestRecording(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, AllConstants.RECORDING_REQUEST_CODE);
    }
    public void requestAudioStorage(Activity activity) {
        String readImagePermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_AUDIO : Manifest.permission.READ_EXTERNAL_STORAGE;

        ActivityCompat.requestPermissions(activity, new String[]{readImagePermission}, AllConstants.RECORD_STORAGE_CODE);
    }

}
