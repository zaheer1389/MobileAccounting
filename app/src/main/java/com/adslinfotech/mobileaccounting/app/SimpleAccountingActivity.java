package com.adslinfotech.mobileaccounting.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DbActivity;
import com.adslinfotech.mobileaccounting.activities.utils.ActivityUserManual;
import com.adslinfotech.mobileaccounting.calculator.Calculator;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.gmail.SendEmailByNative;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.interfaces.AlertDialogListener;
import com.adslinfotech.mobileaccounting.ui.ActivityLogin;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.ui.UpgradeToPremium;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppConstants.HTML;
import com.adslinfotech.mobileaccounting.utils.AppConstants.PERMISSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.URI;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleAccountingActivity extends AppCompatActivity implements OnClickListener, AlertDialogListener {
  public static final int DIALOG_BACKUP = 42;
  public Dialog dialogAlertExitApp;
  public Dialog dialogAlertPositive;
  public AtomicBoolean isPositiveAlertShowing;
  public AtomicBoolean isProgressShowing;
  public String mBackupPath = null;
  public ProgressDialog mProgressDialog;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.isProgressShowing = new AtomicBoolean(false);
    this.isPositiveAlertShowing = new AtomicBoolean(false);
    try {
      ViewConfiguration config = ViewConfiguration.get(this);
      Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
      if (menuKeyField != null) {
        menuKeyField.setAccessible(true);
        menuKeyField.setBoolean(config, false);
      }
    } catch (Exception e) {
    }
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setDisplayHomeAsUpEnabled(true);
      bar.setHomeButtonEnabled(true);
    }
    SimpleAccountingApp.showAds();
  }

  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_ok_alert_positive:
        SessionManager.incrementInteractionCount();
        dismissPositiveAlertDialog();
        return;
      default:
        return;
    }
  }

  public void showProgressDailog(Context context) {
    Log.d("LedgerActivity", "progress dialog");
    if (!this.isProgressShowing.get()) {
      this.isProgressShowing.set(true);
      this.mProgressDialog = ProgressDialog.show(context, null, null);
      this.mProgressDialog.setContentView(R.layout.progress_dailog_layout);
      this.mProgressDialog.setCancelable(false);
      this.isPositiveAlertShowing = new AtomicBoolean(false);
    }
  }

  public void dismissDialog() {
    if (this.mProgressDialog != null) {
      this.mProgressDialog.dismiss();
      this.isProgressShowing.set(false);
    }
  }

  protected void sendBackToMail(String path) {
    Uri filePath = null;
    String body = "Dear Simple Accounting User,\nPlease Find Your DataBase backup file as attachment.\n\nIf you like to import data backup file in App from email then first download it from there to your mobile and copy it into sd card --> SimpleAccounting folder.\n\nThen you will be able to import this file from Recovery/Import option in App.";
    try {
      filePath = Uri.fromFile(new File(path));
      startActivity(new SendEmailByNative(getApplicationContext(), SessionManager.getEmail(), getString(R.string.app_name) + " BackUp File", body, filePath).sendEmailIntent());
    } catch (Exception e) {
      sendMailAction(body, filePath);
    }
  }

  protected void sendMailAction(String body, Uri file) {
    try {
      Intent emailIntent = new Intent("android.intent.action.SEND");
      emailIntent.setType("plain/text");
      emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{SessionManager.getEmail()});
      emailIntent.putExtra("android.intent.extra.STREAM", file);
      emailIntent.putExtra("android.intent.extra.TEXT", body);
      startActivity(Intent.createChooser(emailIntent, "Send email..."));
    } catch (Exception e) {
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  public void showPositiveAlert(String title, String alert) {
    if (!this.isPositiveAlertShowing.get()) {
      this.isPositiveAlertShowing.set(true);
      this.dialogAlertPositive = new Dialog(this, R.style.WindowTitleBackground);
      this.dialogAlertPositive.requestWindowFeature(1);
      this.dialogAlertPositive.setContentView(R.layout.dialog_positive_alerts);
      this.dialogAlertPositive.setCancelable(false);
      TextView mAlertTitle = (TextView) this.dialogAlertPositive.findViewById(R.id.title_alert);
      TextView mAlertText = (TextView) this.dialogAlertPositive.findViewById(R.id.txt_positive_alert);
      if (title == null) {
        mAlertTitle.setText(getString(R.string.app_name));
      } else {
        mAlertTitle.setText(title);
      }
      mAlertText.setText(alert);
      ((Button) this.dialogAlertPositive.findViewById(R.id.btn_ok_alert_positive)).setOnClickListener(this);
      this.dialogAlertPositive.show();
    }
  }

  private void dismissPositiveAlertDialog() {
    if (this.dialogAlertPositive != null) {
      this.dialogAlertPositive.dismiss();
    }
    this.isPositiveAlertShowing.set(false);
  }

  public void showAlertExitApp(String text, final int index) {
    this.dialogAlertExitApp = new Dialog(this, R.style.WindowTitleBackground);
    this.dialogAlertExitApp.requestWindowFeature(1);
    this.dialogAlertExitApp.setContentView(R.layout.dialog_confirmation);
    ((TextView) this.dialogAlertExitApp.findViewById(R.id.txt_confirmation)).setText(text);
    this.dialogAlertExitApp.setCancelable(false);
    ((Button) this.dialogAlertExitApp.findViewById(R.id.btn_yes_confirm1)).setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        SimpleAccountingActivity.this.closeExitAppDialog();
        SimpleAccountingActivity.this.onPositiveClick(index);
      }
    });
    ((Button) this.dialogAlertExitApp.findViewById(R.id.btn_no_confirm1)).setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        SimpleAccountingActivity.this.closeExitAppDialog();
        SimpleAccountingActivity.this.onNegativeClick(index);
      }
    });
    this.dialogAlertExitApp.show();
  }

  private void closeExitAppDialog() {
    if (this.dialogAlertExitApp != null) {
      this.dialogAlertExitApp.dismiss();
    }
  }

  public void onPositiveClick(int from) {
    if (DatabaseExportImport.isShow21AccountDailog) {
      DatabaseExportImport.isShow21AccountDailog = false;
      showProAppLink();
    } else if (from == 42) {
      sendBackToMail(this.mBackupPath);
    } else if (from != 64) {
    }
  }

  public void onNegativeClick(int from) {
  }

  public void hideKeyPad() {
    getWindow().setSoftInputMode(3);
  }

  public void logoutUser() {
    SessionManager.setRememberMe(false);
    Intent intent = new Intent(this, ActivityLogin.class);
    intent.addFlags(4194304);
    intent.addFlags(131072);
    intent.addFlags(16);
    startActivity(intent);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    SessionManager.incrementInteractionCount();
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        finish();
        return true;
      case R.id.menu_backup:
        backUpDb();
        return true;
      case R.id.menu_calculator:
        try {
          Intent i = new Intent();
          i.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
          startActivity(i);
          return true;
        } catch (Exception e) {
          startActivity(new Intent(getApplicationContext(), Calculator.class));
          return true;
        }
      case R.id.menu_help:
        Intent intent1 = new Intent(this, ActivityUserManual.class);
        intent1.putExtra(EXTRA.HTML_FILE, HTML.USER_MANUAL);
        startActivity(intent1);
        return true;
      case R.id.menu_logout:
        logoutUser();
        return true;
      case R.id.menu_moreapps:
        startActivity(new Intent("android.intent.action.VIEW", URI.PLAY_STORE_ADSL));
        return true;
      case R.id.menu_rate:
        startActivity(new Intent("android.intent.action.VIEW", URI.PLAY_STORE_APP));
        return true;
      case R.id.menu_refer:
        new ShareHelper(this, SessionManager.getName() + "Suggest you Simple Accounting App for Andoroid Mobile", AppConstants.SHARE_APP_MSG).share();
        return true;
      case R.id.menu_restore:
        Intent intent = new Intent(this, DbActivity.class);
        intent.putExtra(DbActivity.EXTRA_KEY_SELECTED_TAB, 1);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  protected void backUpDb() {
    if (isStoragePermissionGranted(PERMISSION.STORAGE_EXPORT_BACKUP_HOME)) {
      this.mBackupPath = DatabaseExportImport.exportDb();
      if (this.mBackupPath != null) {
        showAlertExitApp(getResources().getString(R.string.txt_backup_done) + " File at \n" + this.mBackupPath, 42);
        return;
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.txt_backup_not), Toast.LENGTH_SHORT).show();
        return;
      }
    }
    Toast.makeText(this, R.string.permission_storage, Toast.LENGTH_LONG).show();
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION.STORAGE_EXPORT_BACKUP_HOME && grantResults[0] == 0) {
      backUpDb();
    }
  }

  public boolean isStoragePermissionGranted(int requestCode) {
    if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
      return true;
    }
    ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, requestCode);
    return false;
  }

  public void showProAppLink() {
    startActivity(new Intent(this, UpgradeToPremium.class));
  }
}
