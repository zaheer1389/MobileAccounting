package com.adslinfotech.mobileaccounting.dropbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class DownloadFileTask extends AsyncTask<FileMetadata, Void, File> {
    private final Callback mCallback;
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private Exception mException;

    public interface Callback {
        void onDownloadComplete(File file);

        void onError(Exception exception);
    }

    DownloadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        this.mContext = context;
        this.mDbxClient = dbxClient;
        this.mCallback = callback;
    }

    protected void onPostExecute(File result) {
        super.onPostExecute(result);
        if (this.mException != null) {
            this.mCallback.onError(this.mException);
        } else {
            this.mCallback.onDownloadComplete(result);
        }
    }

    protected File doInBackground(FileMetadata... params) {
        FileMetadata metadata = params[0];
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, metadata.getName());
            if (path.exists()) {
                if (!path.isDirectory()) {
                    this.mException = new IllegalStateException("Download path is not a directory: " + path);
                    return null;
                }
            } else if (!path.mkdirs()) {
                this.mException = new RuntimeException("Unable to create directory: " + path);
            }
            try {
                this.mDbxClient.files().download(metadata.getPathLower(), metadata.getRev()).download(new FileOutputStream(file));
            } catch (IOException e) {
            }
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.fromFile(file));
            this.mContext.sendBroadcast(intent);
            return file;
        } catch (DbxException e2) {
            this.mException = e2;
            return null;
        }
    }
}
