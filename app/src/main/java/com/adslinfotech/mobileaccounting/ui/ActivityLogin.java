package com.adslinfotech.mobileaccounting.ui;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DashboardActivity;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityRegistration;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.gmail.SendEmailByNative;
import com.adslinfotech.mobileaccounting.rest.ApiClient;
import com.adslinfotech.mobileaccounting.rest.ApiInterface;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import com.adslinfotech.mobileaccounting.utils.AppConstants.PERMISSION;
import com.adslinfotech.mobileaccounting.utils.AppUtils;

import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ActivityLogin extends SimpleAccountingActivity implements OnClickListener, ActivityEula.OnEulaAgreedTo {
  public AtomicBoolean isAlertMailShowing;
  private AdView mAdView;
  private CheckBox mCbRemember;
  private FetchData mFetchData;
  private EditText mPassword;
  private EditText mUserName;
  private File oldBackupFile;

  private static class FindOldBackupTask extends AsyncTask<String, Void, String> {
    private WeakReference<ActivityLogin> mContext;
    private File mFile = null;

    FindOldBackupTask(ActivityLogin context) {
      this.mContext = new WeakReference(context);
    }

    protected void onPreExecute() {
      SimpleAccountingActivity context = (SimpleAccountingActivity) this.mContext.get();
      context.showProgressDailog(context);
    }

    protected String doInBackground(String... args) {
      getBackUpFile(new File(args[0]));
      return null;
    }

    protected void onPostExecute(String result) {
      ActivityLogin context = (ActivityLogin) this.mContext.get();
      context.dismissDialog();
      if (this.mFile != null) {
        context.oldBackupFile = this.mFile;
        context.showAlertExitApp(context.getString(R.string.msg_import_old_backup), 423);
      }
    }

    private void getBackUpFile(File parentDir) {
      try {
        File[] files = parentDir.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            if (name.startsWith(FILE_NAME_START.SA_DB) && (name.endsWith(FILE_EXTENSION.BACKUP) || name.endsWith(FILE_EXTENSION.DB))) {
              return true;
            }
            return false;
          }
        });
        if (files != null) {
          for (File file : files) {
            if (file.isDirectory()) {
              getBackUpFile(file);
            } else if (this.mFile == null) {
              this.mFile = file;
            } else if (this.mFile.lastModified() < file.lastModified()) {
              this.mFile = file;
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_login);
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    SessionManager.setInteractionCount(0);
    this.mFetchData = new FetchData();
    this.isAlertMailShowing = new AtomicBoolean(false);
    getViews();
    hideKeyPad();
    autoBackup();
    if (AppUtils.isNetworkAvailable(getApplicationContext())) {
      registerGSM();
      if (SimpleAccountingApp.getPreference().getInt(SessionManager.REGISTER_ON_SERVER, 0) != -1 && this.mFetchData.isUserExists()) {
        registerUser();
      }
    }
    init();
    if (!ActivityEula.show(this) && !this.mFetchData.isDBExists()) {
      checkOldDbRequest();
    }
  }

  private void checkOldDbRequest() {
    if (isStoragePermissionGranted(PERMISSION.STORAGE_IMPORT_OLD_BACKUP)) {
      checkForOldDb();
    } else {
      Toast.makeText(this, R.string.permission_storage, Toast.LENGTH_LONG).show();
    }
  }

  private void checkForOldDb() {
    new FindOldBackupTask(this).execute(new String[]{Environment.getExternalStorageDirectory() + "/" + AppConstants.FOLDER});
  }

  private void init() {
    AppUtils.getNextAlarmTime(getApplicationContext());
    Locale locale = new Locale(SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LANGUAGE, "en"));
    Locale.setDefault(locale);
    Configuration config = new Configuration();
    config.locale = locale;
    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION.STORAGE_IMPORT_OLD_BACKUP) {
      checkForOldDb();
    }
  }

  protected void onStart() {
    super.onStart();
    SessionManager.createLoginSession(this.mFetchData.getProfileDetail());
    setLoginDetail();
  }

  private void autoBackup() {
    if (SimpleAccountingApp.getPreference().getBoolean(ActivityPreferences.PREF_DB_BACKUP, false)) {
      String backUp = SimpleAccountingApp.getPreference().getString(ActivityPreferences.DB_BACKUP_DAY, null);
      if (backUp != null) {
        int i = Integer.parseInt(backUp);
        if (i != 0) {
          long lastBackup = SessionManager.getBackUpDate();
          Calendar c = Calendar.getInstance();
          long todaydate = new Date().getTime();
          c.setTime(new Date(lastBackup));
          c.add(Calendar.DAY_OF_MONTH, i);
          if (c.getTimeInMillis() <= todaydate) {
            this.mFetchData.updateAutoBackupDate(todaydate);
            backUpDb();
          }
        }
      }
    }
  }

  private void getViews() {
    this.mUserName = (EditText) findViewById(R.id.txt_user_name_login);
    this.mPassword = (EditText) findViewById(R.id.txt_password_login);
    findViewById(R.id.btn_registration).setOnClickListener(this);
    findViewById(R.id.btn_login).setOnClickListener(this);
    findViewById(R.id.txt_forgot_pass).setOnClickListener(this);
    this.mCbRemember = (CheckBox) findViewById(R.id.chk_remember);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void setLoginDetail() {
    if (SimpleAccountingApp.getPreference().getBoolean(SessionManager.REMEMBER_ME, false)) {
      this.mUserName.setText(SessionManager.getUsername());
      this.mPassword.setText(SessionManager.getPassword());
      this.mCbRemember.setChecked(true);
    }
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_login:
        ckhLogin();
        return;
      case R.id.btn_registration:
        startActivity(new Intent(getBaseContext(), ActivityRegistration.class));
        return;
      case R.id.txt_forgot_pass:
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
          forgotPassword();
          return;
        } else {
          showPositiveAlert(getResources().getString(R.string.txt_NoNetwork), getResources().getString(R.string.txt_Netwok));
          return;
        }
      default:
        super.onClick(v);
        return;
    }
  }

  public void onPositiveClick(int from) {
    if (from == 39) {
      sendPasswordMail();
    } else if (from == 423) {
      DatabaseExportImport.importDb(this, this.oldBackupFile);
    } else {
      super.onPositiveClick(from);
    }
  }

  private void forgotPassword() {
    String email = SessionManager.getEmail();
    if (this.mFetchData.isUserExists()) {
      showAlertExitApp(getResources().getString(R.string.txt_Passsend) + email + getResources().getString(R.string.txt_backup_done_spam), 39);
    } else {
      showPositiveAlert(null, getResources().getString(R.string.txt_FirstRegister));
    }
  }

  private void ckhLogin() {
    String username = this.mUserName.getText().toString();
    String password = this.mPassword.getText().toString();
    if (TextUtils.isEmpty(username)) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_EnterUsername), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(password)) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_EnterPass), Toast.LENGTH_SHORT).show();
    } else if (this.mFetchData.checkSignInRequest(username, password)) {
      if (this.mCbRemember.isChecked()) {
        SessionManager.setRememberMe(true);
      } else {
        SessionManager.setRememberMe(false);
      }
      startActivityForResult(new Intent(getApplicationContext(), DashboardActivity.class), 1);
    } else if (this.mFetchData.isUserExists()) {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_UserNameError), Toast.LENGTH_LONG).show();
    } else {
      showPositiveAlert(null, getString(R.string.txt_FirstRegister));
    }
  }

  private void sendPasswordMail() {
    SessionManager.createLoginSession(this.mFetchData.getProfileDetail());
    Uri filePath = null;
    String body = "Dear Simple Accounting User,\nPlease Find Your Login detail file as attachment.\n\n Simple Accounting App Team";
    try {
      File myFile = new File(Environment.getExternalStorageDirectory() + "/mysdfile.txt");
      myFile.createNewFile();
      FileOutputStream fOut = new FileOutputStream(myFile);
      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
      UserDao user = this.mFetchData.getProfileDetail();
      myOutWriter.append("UserName = " + user.getUserName() + "\r\nPassword = " + user.getPassword());
      myOutWriter.close();
      fOut.close();
      myOutWriter.close();
      filePath = Uri.fromFile(myFile);
      startActivity(new SendEmailByNative(getApplicationContext(), SessionManager.getEmail(), "Simple Accounting Password Recovery File", body, filePath).sendEmailIntent());
    } catch (Exception e) {
      sendMailAction(body, filePath);
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_login, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        return true;
      case R.id.menu_logout:
        SessionManager.setRememberMe(false);
        this.mUserName.setText("");
        this.mPassword.setText("");
        this.mCbRemember.setChecked(false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  protected void onActivityResult(int reqCode, int resultCode, Intent data) {
    if (reqCode != 1) {
      return;
    }
    if (resultCode == AppConstants.ACTIVITY_FINISH) {
      finish();
    } else if (resultCode == AppConstants.ACTIVITY_LOGOUT) {
      this.mUserName.setText("");
      this.mPassword.setText("");
    }
  }

  private void registerGSM() {
    ApiInterface service = (ApiInterface) ApiClient.getClient(ApiClient.BASE_URL_ADSL).create(ApiInterface.class);
    JsonObject obj1 = new JsonObject();
    JsonObject params1 = new JsonObject();
    params1.addProperty("email", SessionManager.getEmail());
    params1.addProperty("model", Build.MODEL);
    params1.addProperty("imei", " ");
    params1.addProperty("gcmid", " ");
    params1.addProperty("appid", Integer.valueOf(6));
    params1.addProperty("type", "F");
    obj1.add("register", params1);
    service.registerGSM(obj1).enqueue(new Callback<JsonObject>() {
      public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
        try {
          if (((JsonObject) response.body()).get("RegisterADSLUserResult").getAsString().equalsIgnoreCase("success")) {
            Editor edit = SimpleAccountingApp.getPreference().edit();
            edit.putInt(SessionManager.REGISTER_ON_SERVER, -1);
            edit.apply();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      public void onFailure(Throwable t) {
        Log.e("ActivityLogin", "error: " + t.getMessage());
        Log.e("ActivityLogin", "error: " + t);
      }
    });
  }

  private void registerUser() {
    String mCity = SimpleAccountingApp.getPreference().getString("CITY", "");
    String mCode = SimpleAccountingApp.getPreference().getString("CODE", "");
    ApiInterface service = (ApiInterface) ApiClient.getClient(ApiClient.BASE_URL_ADSL).create(ApiInterface.class);
    JsonObject obj1 = new JsonObject();
    JsonObject params1 = new JsonObject();
    params1.addProperty("name", SessionManager.getName());
    params1.addProperty("email", SessionManager.getEmail());
    params1.addProperty("registeremail", SessionManager.getEmail());
    params1.addProperty("mobile", SessionManager.getMobile());
    params1.addProperty("remarks", "52");
    params1.addProperty("city", mCity);
    params1.addProperty("promocode", mCode);
    params1.addProperty("type", "F");
    obj1.add("register", params1);
    service.registerUser(obj1).enqueue(new Callback<JsonObject>() {
      public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
        try {
          if (((JsonObject) response.body()).get("RegisterEasyAccountingUserResult").getAsString().equalsIgnoreCase("success")) {
            SimpleAccountingApp.getPreference().edit().putInt(SessionManager.REGISTER_ON_SERVER, -1).apply();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      public void onFailure(Throwable t) {
        Log.e("ActivityLogin", "error: " + t.getMessage());
        Log.e("ActivityLogin", "error: " + t.getStackTrace());
      }
    });
  }

  public void onEulaAgreedTo() {
    checkOldDbRequest();
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (this.mAdView != null) {
      this.mAdView.resume();
    }
  }

  protected void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }
}
