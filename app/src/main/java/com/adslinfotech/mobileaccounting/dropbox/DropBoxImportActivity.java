package com.adslinfotech.mobileaccounting.dropbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.dropbox.DownloadFileTask.Callback;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DIALOG;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DropBoxImportActivity extends SimpleAccountingActivity implements OnItemClickListener {
    private ListView lvDropboxDownloadFilesList;
    private DbxClientV2 mClient;
    private List<Metadata> mFiles;
    private FileMetadata mSelectedFile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.dropboxdownload);
        this.lvDropboxDownloadFilesList = (ListView) findViewById(R.id.lvDropboxDownloadFilesList);
        SharedPreferences prefs = SimpleAccountingApp.getPreference();
        String accessToken = prefs.getString(SessionManager.PREF_DROPBOX_TOKEN, null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                prefs.edit().putString(SessionManager.PREF_DROPBOX_TOKEN, accessToken).apply();
                loadFiles(accessToken);
            } else {
                Auth.startOAuth2Authentication(this, Constants.DROPBOX_APP_KEY);
            }
        } else {
            loadFiles(accessToken);
        }
        checkAppKeySetup();
        this.lvDropboxDownloadFilesList.setOnItemClickListener(this);
    }

    private void loadFiles(String accessToken) {
        this.mClient = new DbxClientV2(DbxRequestConfig.newBuilder(Constants.DROPBOX_APP_SECRET).build(), accessToken);
        getDataTask("");
    }

    private void getDataTask(final String path) {
        new AsyncTask<Void, Void, ListFolderResult>() {
            protected void onPreExecute() {
                super.onPreExecute();
                DropBoxImportActivity.this.showProgressDailog(DropBoxImportActivity.this);
            }

            protected ListFolderResult doInBackground(Void... voids) {
                try {
                    ListFolderResult result = DropBoxImportActivity.this.mClient.files().listFolder(path);
                    Log.e("DropBox loadFiles", "result " + result.getEntries().size());
                    while (true) {
                        for (Metadata metadata : result.getEntries()) {
                            Log.e("DropBox loadFiles", "getEntries " + metadata.getPathLower());
                        }
                        if (!result.getHasMore()) {
                            return result;
                        }
                        result = DropBoxImportActivity.this.mClient.files().listFolderContinue(result.getCursor());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(ListFolderResult result) {
                super.onPostExecute(result);
                System.out.println("result:: " + result.getEntries().size());
                DropBoxImportActivity.this.mFiles = new ArrayList();
                for (Metadata metadata : result.getEntries()) {
                    Log.e("DropBox loadFiles", "getEntries " + metadata.getPathLower());
                    DropBoxImportActivity.this.mFiles.add(metadata);
                }
                DropBoxImportActivity.this.lvDropboxDownloadFilesList.setAdapter(new DownloadFileAdapter(DropBoxImportActivity.this, DropBoxImportActivity.this.mFiles));
                DropBoxImportActivity.this.dismissDialog();
            }
        }.execute(new Void[0]);
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
        Metadata item = (Metadata) this.mFiles.get(pos);
        if (item instanceof FileMetadata) {
            this.mSelectedFile = (FileMetadata) item;
            showAlertExitApp(getString(R.string.msg_import_file, new Object[]{item.getName()}), DIALOG.DIALOG_IMPORT);
            return;
        }
        try {
            getDataTask(((FolderMetadata) item).getPathDisplay());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performWithPermissions() {
        if (isStoragePermissionGranted(521)) {
            downloadFile(this.mSelectedFile);
        } else {
            Toast.makeText(this, R.string.permission_storage, Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 521 && grantResults[0] == 0) {
            downloadFile(this.mSelectedFile);
        }
    }

    private void checkAppKeySetup() {
        if (Constants.DROPBOX_APP_KEY.startsWith("CHANGE") || Constants.DROPBOX_APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }
        Intent testIntent = new Intent("android.intent.action.VIEW");
        String scheme = "db-daa0nmzw7jgshqd";
        testIntent.setData(Uri.parse(scheme + "://" + 1 + "/test"));
        if (getPackageManager().queryIntentActivities(testIntent, 0).size() == 0) {
            showToast("URL scheme in your app's manifest is not set up correctly. You should have a com.dropbox.client2.android.AuthActivity with the scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onPositiveClick(int from) {
        if (from == DIALOG.DIALOG_IMPORT) {
            performWithPermissions();
        }
    }

    private void downloadFile(FileMetadata file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(0);
        dialog.setCancelable(false);
        dialog.setMessage("Downloading");
        dialog.show();
        new DownloadFileTask(this, this.mClient, new Callback() {
            public void onDownloadComplete(File result) {
                dialog.dismiss();
                if (result != null) {
                    DatabaseExportImport.importDb(DropBoxImportActivity.this, result);
                }
            }

            public void onError(Exception e) {
                dialog.dismiss();
                Log.e("downloadFile", "Failed to download file.", e);
                Toast.makeText(DropBoxImportActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        }).execute(new FileMetadata[]{file});
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("DropBoxImportActivity", "onActivityResult: requestCode: " + resultCode + " resultCode: " + resultCode);
    }
}
