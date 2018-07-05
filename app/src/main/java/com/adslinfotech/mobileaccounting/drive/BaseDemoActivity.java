package com.adslinfotech.mobileaccounting.drive;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseDemoActivity extends SimpleAccountingActivity {
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    private static final String TAG = "BaseDriveActivity";
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    protected abstract void onDriveClientReady();

    protected void onStart() {
        super.onStart();
        signIn();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                    if (!getAccountTask.isSuccessful()) {
                        Log.e(TAG, "Sign-in failed.");
                        finish();
                        break;
                    }
                    initializeDriveClient((GoogleSignInAccount) getAccountTask.getResult());
                    break;
                }
                Log.e(TAG, "Sign-in failed.");
                finish();
                return;
            case 1:
                if (resultCode == -1) {
                    this.mOpenItemTaskSource.setResult((DriveId) data.getParcelableExtra("response_drive_id"));
                    break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount == null || !signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            startActivityForResult(GoogleSignIn.getClient(this, new Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(Drive.SCOPE_FILE, new Scope[0]).requestScopes(Drive.SCOPE_APPFOLDER, new Scope[0]).build()).getSignInIntent(), 0);
        } else {
            initializeDriveClient(signInAccount);
        }
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        this.mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        this.mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        onDriveClientReady();
    }

    protected Task<DriveId> pickTextFile() {
        return pickItem(new OpenFileActivityOptions.Builder().setSelectionFilter(Filters.or(Filters.contains(SearchableField.TITLE, FILE_EXTENSION.DB), new Filter[]{Filters.contains(SearchableField.TITLE, FILE_EXTENSION.BACKUP), Filters.contains(SearchableField.TITLE, ".sqlite")})).setActivityTitle("Select " + getString(R.string.app_name) + " backup file").build());
    }

    protected Task<DriveId> pickFolder() {
        return pickItem(new OpenFileActivityOptions.Builder().setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE)).setActivityTitle("select_folder").build());
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        this.mOpenItemTaskSource = new TaskCompletionSource();
        getDriveClient().newOpenFileActivityIntentSender(openOptions).continueWith(new Continuation<IntentSender, Void>() {
            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                BaseDemoActivity.this.startIntentSenderForResult((IntentSender) task.getResult(), 1, null, 0, 0, 0);
                return null;
            }
        });
        return this.mOpenItemTaskSource.getTask();
    }

    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected DriveClient getDriveClient() {
        return this.mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return this.mDriveResourceClient;
    }
}
