package com.adslinfotech.mobileaccounting.dropbox;

import android.os.AsyncTask;
import android.util.Log;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class UploadFileTask extends AsyncTask<File, Void, FileMetadata> {
    private DbxClientV2 dbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onError(Exception exception);

        void onUploadComplete(FileMetadata fileMetadata);
    }

    public UploadFileTask(DbxClientV2 dbxClient, Callback callback) {
        this.dbxClient = dbxClient;
        this.mCallback = callback;
    }

    protected FileMetadata doInBackground(File... strings) {
        Exception e;
        try {
            FileMetadata fileMetadata = (FileMetadata) this.dbxClient.files().uploadBuilder("/" + (FILE_NAME_START.SA + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP)).withMode(WriteMode.OVERWRITE).uploadAndFinish(new FileInputStream(strings[0]));
            Log.d("Upload Status", "Success");
            return fileMetadata;
        } catch (DbxException e2) {
            e = e2;
        } catch (IOException e3) {
            e = e3;
        } catch (Exception e4) {
            e4.printStackTrace();
            this.mException = e4;
            return null;
        }
        //e4.printStackTrace();
        //this.mException = e4;
        return null;
    }

    protected void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
        if (this.mException != null) {
            this.mCallback.onError(this.mException);
        } else if (result == null) {
            this.mCallback.onError(null);
        } else {
            this.mCallback.onUploadComplete(result);
        }
    }
}
