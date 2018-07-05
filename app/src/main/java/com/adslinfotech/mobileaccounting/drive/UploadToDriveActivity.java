package com.adslinfotech.mobileaccounting.drive;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import com.adslinfotech.mobileaccounting.database.DataBaseHandler;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet.Builder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class UploadToDriveActivity extends BaseDemoActivity {
  private static final String TAG = "UploadToDriveActivity";

  protected void onDriveClientReady() {
    createFile();
  }

  private void createFile() {
    final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
    final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
    Tasks.whenAll(new Task[]{rootFolderTask, createContentsTask}).continueWithTask(new Continuation<Void, Task<DriveFile>>() {
      public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
        DriveFolder parent = (DriveFolder) rootFolderTask.getResult();
        DriveContents contents = (DriveContents) createContentsTask.getResult();
        OutputStream outputStream = contents.getOutputStream();
        try {
          InputStream in = new FileInputStream(new File(Environment.getDataDirectory() + "/data/" + "com.adslinfotech.mobileaccounting" + "/databases/" + DataBaseHandler.name));
          byte[] buf = new byte[1024];
          while (true) {
            int len = in.read(buf);
            if (len <= 0) {
              break;
            }
            outputStream.write(buf, 0, len);
          }
          in.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return UploadToDriveActivity.this.getDriveResourceClient().createFile(parent, new Builder().setTitle(FILE_NAME_START.SA + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP).setMimeType("application/x-sqlite3").setStarred(true).build(), contents);
      }
    }).addOnSuccessListener(this, new OnSuccessListener<DriveFile>() {
      public void onSuccess(DriveFile driveFile) {
        UploadToDriveActivity.this.showMessage("Backup uploaded successfully. " + driveFile.getDriveId().encodeToString());
        UploadToDriveActivity.this.finish();
      }
    }).addOnFailureListener(this, new OnFailureListener() {
      public void onFailure(@NonNull Exception e) {
        Log.e(UploadToDriveActivity.TAG, "Unable to create file", e);
        UploadToDriveActivity.this.showMessage("Error! Uploading backup failed.");
        UploadToDriveActivity.this.finish();
      }
    });
  }

  private void createFileInAppFolder() {
    final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
    final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
    Tasks.whenAll(new Task[]{appFolderTask, createContentsTask}).continueWithTask(new Continuation<Void, Task<DriveFile>>() {
      public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
        DriveFolder parent = (DriveFolder) appFolderTask.getResult();
        DriveContents contents = (DriveContents) createContentsTask.getResult();
        OutputStream outputStream = contents.getOutputStream();
        try {
          InputStream in = new FileInputStream(new File(Environment.getDataDirectory() + "/data/" + "com.adslinfotech.mobileaccounting" + "/databases/" + DataBaseHandler.name));
          byte[] buf = new byte[1024];
          while (true) {
            int len = in.read(buf);
            if (len <= 0) {
              break;
            }
            outputStream.write(buf, 0, len);
          }
          in.close();
        } catch (IOException e) {
          Log.e(UploadToDriveActivity.TAG, "Unable to write file contents.");
          e.printStackTrace();
        }
        String filename = FILE_NAME_START.SA + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP;
        return UploadToDriveActivity.this.getDriveResourceClient().createFile(parent, new Builder().setMimeType("text/plain").setTitle("SimpleAccounting111.db").setStarred(true).build(), contents);
      }
    }).addOnSuccessListener(this, new OnSuccessListener<DriveFile>() {
      public void onSuccess(DriveFile driveFile) {
        UploadToDriveActivity.this.showMessage("Backup uploaded successfully. " + driveFile.getDriveId().encodeToString());
        UploadToDriveActivity.this.finish();
      }
    }).addOnFailureListener(this, new OnFailureListener() {
      public void onFailure(@NonNull Exception e) {
        Log.e(UploadToDriveActivity.TAG, "Unable to create file", e);
        UploadToDriveActivity.this.showMessage("Error! Uploading backup failed.");
        UploadToDriveActivity.this.finish();
      }
    });
  }
}
