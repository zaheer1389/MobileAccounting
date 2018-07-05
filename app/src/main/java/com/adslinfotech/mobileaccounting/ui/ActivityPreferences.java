package com.adslinfotech.mobileaccounting.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DashboardActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.DataBaseHandler;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


@TargetApi(11)
public class ActivityPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
  public static final String DB_BACKUP_DAY = "DB_BACKUP_DAY";
  public static final String PREF_CURRENCY = "PREF_CURRENCY";
  public static final String PREF_DATE_FORMAT = "PREF_DATE_FORMAT";
  public static final String PREF_DB_BACKUP = "PREF_DB_BACKUP";
  public static final String PREF_LANGUAGE = "PREF_LANGUAGE";
  public static final String PREF_LIST_ORDER = "PREF_LIST_ORDER";
  public static final String PREF_LIST_TYPE = "PREF_LIST_TYPE";
  public static final String PREF_PASSWORD = "PREF_PASSWORD";
  private static final String PREF_SETTLE = "PREF_SETTLE";
  private Dialog dialogConfirmPass;
  private boolean isWithBalance;
  private AdView mAdView;
  private EditTextPreference mEtBackUpDay;
  private EditText mEtPass;
  private ListPreference mListFormat;

  @SuppressLint({"NewApi"})
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.userpreferences);
    setContentView(R.layout.pref_layout);
    setTheme(R.style.MyPreferenceTheme);
    this.mEtBackUpDay = (EditTextPreference) getPreferenceScreen().findPreference(DB_BACKUP_DAY);
    this.mListFormat = (ListPreference) getPreferenceScreen().findPreference(PREF_DATE_FORMAT);
    int year = new Date().getYear() + 1900;
    this.mListFormat.setEntries(new String[]{"Select Date Format", year + "-12-31", "12/31/" + year, "31/12/" + year, year + "/12/31", "Sat, Dec 31, " + year, "Sat, 31 Dec " + year, year + " Dec 31, Sat"});
    this.mListFormat.setEntryValues(new String[]{DateFormat.DB_DATE, DateFormat.DB_DATE, "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/dd/MM", "EEE, MMM dd, yyyy", "EEE, dd MMM yyyy", "yyyy MMM dd, EEE"});
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  protected void onResume() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    SimpleAccountingApp.getPreference().edit().putString(PREF_SETTLE, "3").apply();
  }

  protected void onPause() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }

  public void showPositiveAlert(String title, String alert) {
    final Dialog dialogAlertPositive = new Dialog(this, R.style.WindowTitleBackground);
    dialogAlertPositive.requestWindowFeature(1);
    dialogAlertPositive.setContentView(R.layout.dialog_positive_alerts);
    dialogAlertPositive.setCancelable(false);
    TextView mAlertText = (TextView) dialogAlertPositive.findViewById(R.id.txt_positive_alert);
    ((TextView) dialogAlertPositive.findViewById(R.id.title_alert)).setText(title);
    mAlertText.setText(alert);
    ((Button) dialogAlertPositive.findViewById(R.id.btn_ok_alert_positive)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        dialogAlertPositive.dismiss();
      }
    });
    dialogAlertPositive.show();
  }

  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(PREF_DB_BACKUP)) {
      this.mEtBackUpDay.setEnabled(sharedPreferences.getBoolean(key, false));
    } else if (key.equals(PREF_LANGUAGE)) {
      setResult(-1);
      Locale locale = new Locale(sharedPreferences.getString(PREF_LANGUAGE, "en"));
      Configuration config = new Configuration();
      config.locale = locale;
      getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
      setResult(-1);
      onCreate(null);
    } else if (key.equals(PREF_SETTLE) && !sharedPreferences.getString(PREF_SETTLE, "3").equals("3")) {
      if (sharedPreferences.getString(PREF_SETTLE, "0").equalsIgnoreCase("0")) {
        this.isWithBalance = false;
      } else {
        this.isWithBalance = true;
      }
      if (SessionManager.isPasswordRequired()) {
        showAlertConPass();
      } else {
        settle();
      }
      sharedPreferences.edit().putString(PREF_SETTLE, "3").apply();
    }
  }

  private void showAlertConPass() {
    this.dialogConfirmPass = new Dialog(this, R.style.WindowTitleBackground);
    this.dialogConfirmPass.requestWindowFeature(1);
    this.dialogConfirmPass.setContentView(R.layout.dialog_with_edittext);
    this.dialogConfirmPass.setCancelable(false);
    this.mEtPass = (EditText) this.dialogConfirmPass.findViewById(R.id.et_dialong_name);
    Button mButtonOk = (Button) this.dialogConfirmPass.findViewById(R.id.btn_dialog_OK);
    ((Button) this.dialogConfirmPass.findViewById(R.id.btn_dialog_Cancel)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        ActivityPreferences.this.dialogConfirmPass.dismiss();
      }
    });
    mButtonOk.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        ActivityPreferences.this.confermPassword();
      }
    });
    this.dialogConfirmPass.show();
  }

  private void confermPassword() {
    if (this.mEtPass.getText().toString().equalsIgnoreCase(SessionManager.getPassword())) {
      this.dialogConfirmPass.dismiss();
      settle();
      return;
    }
    this.mEtPass.setError(getResources().getString(R.string.txt_Incorrect_pass));
  }

  private void settle() {
    backUpDb();
    if (SimpleAccountingApp.getPreference().getString(PREF_SETTLE, "0").equalsIgnoreCase("0")) {
      settleAllAccount();
    } else {
      settleAllAccount();
    }
    SimpleAccountingApp.getPreference().edit().remove(PREF_SETTLE).apply();
  }

  protected void backUpDb() {
    try {
      String path = DatabaseExportImport.exportDb();
      if (path != null) {
        Toast.makeText(getApplicationContext(), "Backup Done Succesfully at path " + path + ".Are you want to send this backup file to your email id?", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.txt_backup_not), Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void settleAllAccount() {
    DataBaseHandler handler = SimpleAccountingApp.getDBHandler();
    if (this.isWithBalance) {
      Transaction transaction;
      SQLiteDatabase db = handler.getWritableDatabase();
      Cursor c = db.rawQuery("SELECT AID, (select (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) FROM Transection where Transection.AID = Account.AID) AS bal FROM Account", null);
      ArrayList<Transaction> results = new ArrayList();
      while (c.moveToNext()) {
        double bal;
        transaction = new Transaction();
        transaction.setAId(c.getInt(0));
        try {
          bal = Double.parseDouble(c.getString(1));
        } catch (Exception e) {
          bal = 0.0d;
        }
        if (bal > 0.0d) {
          transaction.setCraditAmount(bal);
          transaction.setDebitAmount(0.0d);
          transaction.setDr_cr(1);
        } else if (0.0d > bal) {
          try {
            transaction.setDebitAmount(-1.0d * bal);
            transaction.setCraditAmount(0.0d);
            transaction.setDr_cr(0);
          } catch (Exception e2) {
            e2.printStackTrace();
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
            return;
          }
        } else {
          continue;
        }
        results.add(transaction);
      }
      c.close();
      db.delete("Transection", null, null);
      SimpleDateFormat format1 = new SimpleDateFormat(DateFormat.DB_DATE);
      SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
      Date today = new Date();
      int userId = SessionManager.getLoginUserId();
      String narr = "As on " + new SimpleDateFormat("dd MMM yyyy").format(today);
      String strDate = new SimpleDateFormat(DateFormat.DB_DATE).format(today);
      Iterator it = results.iterator();
      while (it.hasNext()) {
        transaction = (Transaction) it.next();
        ContentValues values = new ContentValues();
        values.put("AID", Integer.valueOf(transaction.getAccountId()));
        values.put("UserID", Integer.valueOf(userId));
        values.put("Credit_Amount", Double.valueOf(transaction.getCraditAmount()));
        values.put("Debit_Amount", Double.valueOf(transaction.getDebitAmount()));
        values.put("dr_cr", Integer.valueOf(transaction.getDr_cr()));
        values.put("Narration", narr);
        values.put("EntryDate", strDate);
        try {
          Date date = format1.parse(strDate);
          values.put("Date", format2.format(date));
          values.put("LongDate", Long.valueOf(date.getTime()));
        } catch (Exception e3) {
        }
        db.insert("Transection", null, values);
      }
      return;
    }
    handler.getWritableDatabase().delete("Transection", null, null);
  }

  protected void onPostCreate(Bundle savedInstanceState) {
    Toolbar bar;
    super.onPostCreate(savedInstanceState);
    if (VERSION.SDK_INT >= 14) {
      LinearLayout root = (LinearLayout) findViewById(R.id.llout);
      bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
      root.addView(bar, 0);
    } else {
      int height;
      ViewGroup root2 = (ViewGroup) findViewById(android.R.id.content);
      ListView content = (ListView) root2.getChildAt(0);
      root2.removeAllViews();
      bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root2, false);
      TypedValue tv = new TypedValue();
      if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
        height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
      } else {
        height = bar.getHeight();
      }
      content.setPadding(0, height, 0, 0);
      root2.addView(content);
      root2.addView(bar);
    }
    bar.setNavigationOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        ActivityPreferences.this.onBack();
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        onBack();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void onBackPressed() {
    onBack();
  }

  public void onBack() {
    super.onBackPressed();
    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
    intent.addFlags(4194304);
    intent.addFlags(131072);
    intent.addFlags(16);
    startActivity(intent);
    finish();
  }
}
