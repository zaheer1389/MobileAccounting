package com.adslinfotech.mobileaccounting;

import android.content.Context;
import android.util.Log;

public final class CommonUtilities
{
  public static final String EXTRA_MESSAGE = "message";
  public static final String SENDER_ID = "849453436742";
  static final String TAG = "AndroidHive GCM";
  
  static void displayMessage(Context paramContext, String paramString)
  {
    Log.d("CommonUtilities", "" + paramString);
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/CommonUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */