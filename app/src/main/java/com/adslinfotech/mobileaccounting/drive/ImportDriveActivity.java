package com.adslinfotech.mobileaccounting.drive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImportDriveActivity
  extends BaseDriveClass
{
  protected static final File DATABASE_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "/SimpleAccounting/backup/drive");
  private static final int REQUEST_CODE_OPENER = 1;
  private static final String TAG = "ImportDriveActivity";
  private ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback()
  {
    @Override
    public void onResult(@NonNull Result result) {
      if (!result.getStatus().isSuccess())
      {
        ImportDriveActivity.this.showMessage("Error while opening the file contents");
        ImportDriveActivity.this.pd.dismiss();
        return;
      }

    }
  };
  private DriveId mSelectedFileDriveId;
  private ProgressDialog pd;
  
  private static String getCurrentTime()
  {
    Object localObject = Calendar.getInstance();
    System.out.println("Current time => " + ((Calendar)localObject).getTime());
    localObject = new SimpleDateFormat("_yyyyMMdd_HHmmss").format(((Calendar)localObject).getTime());
    Log.d("current date", "=" + (String)localObject);
    return (String)localObject;
  }
  
  private void open()
  {
    this.pd = ProgressDialog.show(this, "Downloading...", "Please wait...");
    new Thread(new Runnable()
    {
      public void run()
      {
        DriveFile.DownloadProgressListener local1 = new DriveFile.DownloadProgressListener()
        {
          public void onProgress(long paramAnonymous2Long1, long paramAnonymous2Long2)
          {
            Log.d("ImportDriveActivity", String.format("Loading progress: %d percent", new Object[] { Integer.valueOf((int)(100L * paramAnonymous2Long1 / paramAnonymous2Long2)) }));
          }
        };
       // Drive.DriveApi.getFile(ImportDriveActivity.this.getGoogleApiClient(), ImportDriveActivity.this.mSelectedFileDriveId).open(ImportDriveActivity.this.getGoogleApiClient(), 268435456, local1).setResultCallback(ImportDriveActivity.this.contentsCallback);
        Log.d("Selected File:", "" + ImportDriveActivity.this.mSelectedFileDriveId);
        //ImportDriveActivity.access$102(ImportDriveActivity.this, null);
      }
    }).start();
  }
  
  public void copy(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    FileOutputStream fos = new FileOutputStream(paramFile);
    byte[] arrayOfByte = new byte['Ѐ'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0) {
        break;
      }
      fos.write(arrayOfByte, 0, i);
    }
    fos.close();
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Log.e("ImportDriveActivity", "requestCode: " + paramInt1 + " resultCode: " + paramInt2);
    if (paramInt1 == 1)
    {
      this.pd.dismiss();
      if (paramInt2 == -1)
      {
        this.pd.dismiss();
        this.mSelectedFileDriveId = ((DriveId)paramIntent.getParcelableExtra("response_drive_id"));
        Log.e("ImportDriveActivity", "mSelectedFileDriveId: " + this.mSelectedFileDriveId);
        if (this.mGoogleApiClient == null) {
          this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }
        this.mGoogleApiClient.connect();
      }
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onConnected(Bundle paramBundle)
  {
    super.onConnected(paramBundle);
    if (this.mSelectedFileDriveId != null)
    {
      open();
      return;
    }
    this.pd = ProgressDialog.show(this, null, "Retrieving data...");
    new Thread(new Runnable()
    {
      public void run()
      {
        IntentSender localIntentSender = Drive.DriveApi.newOpenFileActivityBuilder().setMimeType(new String[] { "text/html" }).build(ImportDriveActivity.this.getGoogleApiClient());
        try
        {
          ImportDriveActivity.this.startIntentSenderForResult(localIntentSender, 1, null, 0, 0, 0);
          return;
        }
        catch (SendIntentException localSendIntentException)
        {
          Log.w("ImportDriveActivity", "Unable to send intent", localSendIntentException);
        }
      }
    }).start();
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/drive/ImportDriveActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */