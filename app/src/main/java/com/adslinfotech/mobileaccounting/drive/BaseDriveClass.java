package com.adslinfotech.mobileaccounting.drive;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

public abstract class BaseDriveClass
  extends SimpleAccountingActivity
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  public static final String EXISTING_FILE_ID = "0ByfSjdPVs9MZTHBmMVdSeWxaNTg";
  public static final String EXISTING_FOLDER_ID = "0B2EEtIjPUdX6MERsWlYxN3J6RU0";
  protected static final String EXTRA_ACCOUNT_NAME = "account_name";
  protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;
  protected static final int REQUEST_CODE_RESOLUTION = 1;
  private static final String TAG = "BaseDriveActivity";
  protected GoogleApiClient mGoogleApiClient;
  
  public GoogleApiClient getGoogleApiClient()
  {
    return this.mGoogleApiClient;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if ((paramInt1 == 1) && (paramInt2 == -1)) {
      this.mGoogleApiClient.connect();
    }
  }
  
  public void onConnected(Bundle paramBundle)
  {
    Log.e("BaseDriveActivity", "GoogleApiClient connected");
  }
  
  public void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    Log.e("BaseDriveActivity", "GoogleApiClient connection failed: " + paramConnectionResult.toString());
    if (!paramConnectionResult.hasResolution()) {
      try
      {
        GooglePlayServicesUtil.getErrorDialog(paramConnectionResult.getErrorCode(), this, Toast.LENGTH_SHORT).show();
        return;
      }
      catch (Exception localException)
      {
        showPositiveAlert("Simple Accounting", "" + paramConnectionResult.getErrorCode());
        return;
      }
    }
    try
    {
      paramConnectionResult.startResolutionForResult(this, 1);
      return;
    }
    catch (IntentSender.SendIntentException e)
    {
      Log.e("BaseDriveActivity", "Exception while starting resolution activity", e);
    }
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    Log.e("BaseDriveActivity", "GoogleApiClient connection suspended");
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (this.mGoogleApiClient == null) {
      this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }
    this.mGoogleApiClient.connect();
  }
  
  protected void onPause()
  {
    if (this.mGoogleApiClient != null) {
      this.mGoogleApiClient.disconnect();
    }
    super.onPause();
  }
  
  public void showMessage(String paramString)
  {
    Toast.makeText(this, paramString, Toast.LENGTH_LONG).show();
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/drive/BaseDriveClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */