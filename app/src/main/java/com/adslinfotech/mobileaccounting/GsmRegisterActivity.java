package com.adslinfotech.mobileaccounting;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;

public class GsmRegisterActivity
{
  public static String mEmail;
  public static String mName;
  private Context mContext;
  AsyncTask<Void, Void, Void> mRegisterTask;
  
  public GsmRegisterActivity(Context paramContext, String paramString1, String paramString2)
  {
    this.mContext = paramContext;
    mEmail = paramString1;
    mName = paramString2;
  }
  
  public String register()
  {
    try
    {
      GCMRegistrar.checkDevice(this.mContext);
      GCMRegistrar.checkManifest(this.mContext);
      String str = GCMRegistrar.getRegistrationId(this.mContext);
      if (str.equals(""))
      {
        GCMRegistrar.register(this.mContext, new String[] { "849453436742" });
        return str;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      if (GCMRegistrar.isRegisteredOnServer(this.mContext))
      {
        Log.d("GsmRegisterActivity", "Device already registered.");
        return localException.getMessage();
      }

      Context localContext = this.mContext;
      this.mRegisterTask = new AsyncTask()
      {
        @Override
        protected Object doInBackground(Object[] params) {
          return null;
        }

        protected void onPostExecute(Void paramAnonymousVoid)
        {
          GsmRegisterActivity.this.mRegisterTask = null;
        }
      };

      this.mRegisterTask.execute(new Void[] { null, null, null });
      return localException.getMessage();
    }

    return  "";
  }
  
  public void removeListerner()
  {
    if (this.mRegisterTask != null) {
      this.mRegisterTask.cancel(true);
    }
    try
    {
      GCMRegistrar.onDestroy(this.mContext);
      return;
    }
    catch (Exception localException)
    {
      Log.e("UnRegisterReceiverError", "> " + localException.getMessage());
    }
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/GsmRegisterActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */