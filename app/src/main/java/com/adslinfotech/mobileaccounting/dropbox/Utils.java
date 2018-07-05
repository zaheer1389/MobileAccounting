package com.adslinfotech.mobileaccounting.dropbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import java.io.File;

public class Utils
{
  public static String getPath()
  {
    String str;
    if (Environment.getExternalStorageState().equals("mounted")) {
      str = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    if (new File("/mnt/emmc").exists()) {
      str = "/mnt/emmc";
    } else {
      str = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    return str + "/SimpleAccounting";
  }
  
  public static boolean isOnline(Context paramContext)
  {
    NetworkInfo networkInfo = ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    return (paramContext != null) && (networkInfo.isConnectedOrConnecting());
  }
  
  public static void showNetworkAlert(Context paramContext)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(paramContext);
    builder.setTitle("Network Alert")
            .setMessage("Please check your network connection and try again")
            .setNeutralButton("OK", new OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                paramAnonymousDialogInterface.dismiss();
              }
            });

    AlertDialog dialog = builder.create();
    dialog.show();

  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/dropbox/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */