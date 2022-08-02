package com.yawar.memo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.yawar.memo.R;

public class DialogProperties {

    public static void showPermissionDialog(String message, int RequestCode, Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(activity.getResources().getString(R.string.permission_necessary));
        alertBuilder.setMessage(activity.getResources().getString(R.string.contact_permission));
        alertBuilder.setMessage(message);

        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, RequestCode);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();


    }
}
