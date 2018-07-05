package com.adslinfotech.mobileaccounting.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;

public class Permissions {
  public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

  @TargetApi(16)
  public static boolean checkPermission(final Context context) {
    if (VERSION.SDK_INT < 23) {
      return true;
    }
    if (ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
      return true;
    }
    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, "android.permission.READ_EXTERNAL_STORAGE")) {
      Builder alertBuilder = new Builder(context);
      alertBuilder.setCancelable(true);
      alertBuilder.setTitle((CharSequence) "Permission necessary");
      alertBuilder.setMessage((CharSequence) "External storage permission is necessary");
      alertBuilder.setPositiveButton(android.R.string.yes, new OnClickListener() {
        @TargetApi(16)
        public void onClick(DialogInterface dialog, int which) {
          ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, Permissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
      });
      alertBuilder.create().show();
    } else {
      ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }
    return false;
  }
}
