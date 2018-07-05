package com.adslinfotech.mobileaccounting;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class WakeLocker
{
  private static WakeLock wakeLock;
  
  public static void acquire(Context paramContext)
  {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(805306394, "WakeLock");
    wakeLock.acquire();
  }
  
  public static void release()
  {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = null;
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/WakeLocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */