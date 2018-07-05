package com.adslinfotech.mobileaccounting.drive;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import java.io.File;

public class DriveUtils {
  public static String getPath() {
    String path = "";
    if (Environment.getExternalStorageState().equals("mounted")) {
      path = Environment.getExternalStorageDirectory().getAbsolutePath();
    } else if (new File("/mnt/emmc").exists()) {
      path = "/mnt/emmc";
    } else {
      path = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    return path + AppConstants.FOLDER;
  }

  public static boolean isOnline(Context context) {
    NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
      return false;
    }
    return true;
  }

  public static void showNetworkAlert(Context context) {
    Builder builder = new Builder(context);
    builder.setTitle("Network Alert");
    builder.setMessage("Please check your network connection and try again");
    builder.setNeutralButton("OK", new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.show();
  }
}
