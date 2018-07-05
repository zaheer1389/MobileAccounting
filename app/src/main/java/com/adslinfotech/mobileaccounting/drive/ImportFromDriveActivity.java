package com.adslinfotech.mobileaccounting.drive;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImportFromDriveActivity extends BaseDemoActivity {
    private static final String TAG = "RetrieveContents";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onDriveClientReady() {
        pickTextFile().addOnSuccessListener(this, new OnSuccessListener<DriveId>() {
            public void onSuccess(DriveId driveId) {
                ImportFromDriveActivity.this.dismissDialog();
                ImportFromDriveActivity.this.retrieveContents(driveId.asDriveFile());
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
                ImportFromDriveActivity.this.dismissDialog();
                Log.e(ImportFromDriveActivity.TAG, "No file selected", e);
                ImportFromDriveActivity.this.showMessage("file_not_selected");
                ImportFromDriveActivity.this.finish();
            }
        });
    }

    private void retrieveContents(DriveFile file) {
        getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY, new OpenFileCallback() {
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                int progress = (int) ((100 * bytesDownloaded) / bytesExpected);
                Log.d(ImportFromDriveActivity.TAG, String.format("Loading progress: %d percent", new Object[]{Integer.valueOf(progress)}));
                ImportFromDriveActivity.this.showProgressDailog(ImportFromDriveActivity.this);
            }

            public void onContents(@NonNull DriveContents driveContents) {
                ImportFromDriveActivity.this.dismissDialog();
                try {
                    File folder = new File(Environment.getExternalStorageDirectory(), "/SimpleAccounting/backup/drive");
                    String filename = FILE_NAME_START.SA + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP;
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File outPutFile = new File(folder, filename);
                    ImportFromDriveActivity.this.copy(driveContents.getInputStream(), outPutFile);
                    DatabaseExportImport.importDb(ImportFromDriveActivity.this, outPutFile);
                    Log.e(ImportFromDriveActivity.TAG, "content_loaded");
                    ImportFromDriveActivity.this.finish();
                } catch (IOException e) {
                    onError(e);
                }
            }

            public void onError(@NonNull Exception e) {
                ImportFromDriveActivity.this.dismissDialog();
                Log.e(ImportFromDriveActivity.TAG, "Unable to read contents", e);
                ImportFromDriveActivity.this.showMessage("Unable to read contents");
                ImportFromDriveActivity.this.finish();
            }
        });
    }

    private void copy(InputStream in, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        while (true) {
            int len = in.read(buf);
            if (len > 0) {
                out.write(buf, 0, len);
            } else {
                out.close();
                return;
            }
        }
    }
}
