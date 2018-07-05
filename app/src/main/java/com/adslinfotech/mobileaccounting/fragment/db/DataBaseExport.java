package com.adslinfotech.mobileaccounting.fragment.db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DbActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.drive.UploadToDriveActivity;
import com.adslinfotech.mobileaccounting.dropbox.Constants;
import com.adslinfotech.mobileaccounting.dropbox.UploadFileTask;
import com.adslinfotech.mobileaccounting.dropbox.UploadFileTask.Callback;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.gmail.SendEmailByNative;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class DataBaseExport extends BaseFragment implements OnClickListener {
  DbActivity activity;
  private boolean isDropBoxLoginInitiated;
  private boolean isFirstTime;
  private AdView mAdView;
  private String mExportedFile;
  private int mIndex = 0;

  public static DataBaseExport newInstance() {
    return new DataBaseExport();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_settleall, container, false);
    init(view);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(getActivity());
      this.mAdView = (AdView) view.findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
    return view;
  }

  public boolean isStoragePermissionGranted() {
    if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
      return true;
    }
    Toast.makeText(getActivity(), R.string.permission_storage, Toast.LENGTH_LONG).show();
    ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    return false;
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults[0] == 0) {
      switch (this.mIndex) {
        case 0:
          checkSDCard();
          return;
        case 1:
          exportCsv();
          return;
        case 2:
          btnDropBoxClicked();
          return;
        case 3:
          startActivity(new Intent(getActivity(), UploadToDriveActivity.class));
          return;
        default:
          return;
      }
    }
  }

  private void init(View view) {
    Button btnDropBox = (Button) view.findViewById(R.id.backup_dropbox);
    btnDropBox.setVisibility(View.VISIBLE);
    btnDropBox.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        if (DataBaseExport.this.isStoragePermissionGranted()) {
          DataBaseExport.this.btnDropBoxClicked();
        } else {
          DataBaseExport.this.mIndex = 2;
        }
      }
    });
    Button btExport = (Button) view.findViewById(R.id.settleall);
    btExport.setText(R.string.txt_bac);
    Button btExpotcsv = (Button) view.findViewById(R.id.backup_csv);
    btExport.setText(getString(R.string.txt_Export));
    btExpotcsv.setVisibility(View.VISIBLE);
    btExport.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        if (DataBaseExport.this.isStoragePermissionGranted()) {
          DataBaseExport.this.checkSDCard();
        } else {
          DataBaseExport.this.mIndex = 0;
        }
      }
    });
    btExpotcsv.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        if (DataBaseExport.this.isStoragePermissionGranted()) {
          DataBaseExport.this.exportCsv();
        } else {
          DataBaseExport.this.mIndex = 1;
        }
      }
    });
    Button btnDrive = (Button) view.findViewById(R.id.backup_drive);
    btnDrive.setVisibility(View.VISIBLE);
    btnDrive.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        if (DataBaseExport.this.isStoragePermissionGranted()) {
          DataBaseExport.this.startActivity(new Intent(DataBaseExport.this.getActivity(), UploadToDriveActivity.class));
          return;
        }
        DataBaseExport.this.mIndex = 3;
      }
    });
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.backup_csv:
        exportCsv();
        return;
      case R.id.backup_drive:
        startActivity(new Intent(getActivity(), UploadToDriveActivity.class));
        return;
      case R.id.backup_dropbox:
        btnDropBoxClicked();
        return;
      case R.id.settleall:
        checkSDCard();
        return;
      default:
        return;
    }
  }

  public void onAttach(Context context) {
    super.onAttach(context);
    this.activity = (DbActivity) context;
  }

  public void onPositiveClick(int from) {
    Uri name = Uri.fromFile(new File(this.mExportedFile));
    String body = getString(R.string.backup_mail_body);
    try {
      startActivity(new SendEmailByNative(getActivity(), SessionManager.getEmail(), getString(R.string.backup_mail_subject), body, name).sendEmailIntent());
    } catch (Exception e) {
      e.printStackTrace();
      sendMailAction(body, name);
    }
  }

  private void sendMailAction(String body, Uri file) {
    try {
      Intent emailIntent = new Intent("android.intent.action.SEND");
      emailIntent.setType("plain/text");
      emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{SessionManager.getEmail()});
      emailIntent.putExtra("android.intent.extra.STREAM", file);
      emailIntent.putExtra("android.intent.extra.TEXT", body);
      startActivity(Intent.createChooser(emailIntent, "Send email..."));
    } catch (Exception e) {
      Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void exportCsv() {
    PdfDao header = new PdfDao();
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add("Sno.");
    mColumns.add("Account Name");
    mColumns.add("Date");
    mColumns.add("Debit");
    mColumns.add("Credit");
    mColumns.add("Narration");
    int i = 1;
    FetchData mFetchData = new FetchData();
    ArrayList<Account> mAccount = mFetchData.getAllAccounts(false, false);
    ArrayList<PdfDao> mValues = new ArrayList();
    Iterator it = mAccount.iterator();
    while (it.hasNext()) {
      Account dao = (Account) it.next();
      List<Transaction> list = (List<Transaction>)mFetchData.getAllTransactions(getActivity(), null, dao.getAccountId(), false).get(0);
      for (Transaction transaction : list) {
        PdfDao pdfDao = new PdfDao();
        pdfDao.setFirst("" + i);
        pdfDao.setSecond(dao.getName());
        pdfDao.setThird(transaction.getDate());
        pdfDao.setFour("" + transaction.getDebitAmount());
        pdfDao.setFive("" + transaction.getCraditAmount());
        pdfDao.setSix(transaction.getNarration());
        mValues.add(pdfDao);
        i++;
      }
    }
    header.setFirst("All Transactions List");
    new GenerateExcel(getActivity(), header, mColumns, mValues, 10).excel(getActivity());
  }

  private void checkSDCard() {
    String state = Environment.getExternalStorageState();
    if (!"mounted".equals(state)) {
      Toast.makeText(getActivity(), getResources().getString(R.string.txt_NoSdCard), Toast.LENGTH_LONG).show();
    } else if ("mounted_ro".equals(state)) {
      Toast.makeText(getActivity(), getResources().getString(R.string.txt_SdCard), Toast.LENGTH_LONG).show();
    } else {
      this.mExportedFile = DatabaseExportImport.exportDb();
      if (this.mExportedFile != null) {
        ((DbActivity) getActivity()).showAlertExitApp(getResources().getString(R.string.msg_backup_success, new Object[]{this.mExportedFile}), 0);
        return;
      }
      Toast.makeText(getActivity(), getResources().getString(R.string.msg_backup_fail), Toast.LENGTH_LONG).show();
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("DropBoxImportActivity", "onActivityResult: requestCode: " + requestCode + " resultCode: " + resultCode);
  }

  private void btnDropBoxClicked() {
    SharedPreferences prefs = SimpleAccountingApp.getPreference();
    String accessToken = prefs.getString(SessionManager.PREF_DROPBOX_TOKEN, null);
    if (accessToken == null) {
      accessToken = Auth.getOAuth2Token();
      if (accessToken != null) {
        prefs.edit().putString(SessionManager.PREF_DROPBOX_TOKEN, accessToken).apply();
        uploadFile(accessToken);
        return;
      }
      this.isDropBoxLoginInitiated = true;
      Auth.startOAuth2Authentication(getActivity(), Constants.DROPBOX_APP_KEY);
      return;
    }
    uploadFile(accessToken);
  }

  private void uploadFile(String accessToken) {
    DbxClientV2 client = new DbxClientV2(DbxRequestConfig.newBuilder(Constants.DROPBOX_APP_SECRET).build(), accessToken);
    final ProgressDialog dialog = new ProgressDialog(getActivity());
    dialog.setProgressStyle(0);
    dialog.setCancelable(false);
    dialog.setMessage("Uploading");
    dialog.show();
    new UploadFileTask(client, new Callback() {
      public void onUploadComplete(FileMetadata result) {
        dialog.dismiss();
        if (result != null) {
          Toast.makeText(DataBaseExport.this.getActivity(), "File uploaded successfully. " + result.getPathDisplay(), Toast.LENGTH_LONG).show();
        }
      }

      public void onError(Exception e) {
        dialog.dismiss();
        Log.e("DropBox uploadFile", "Failed to upload file.", e);
        Toast.makeText(DataBaseExport.this.getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
      }
    }).execute(new File[]{DatabaseExportImport.getDataBaseFile()});
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (!this.isFirstTime && this.isDropBoxLoginInitiated) {
      this.isDropBoxLoginInitiated = false;
      this.isFirstTime = true;
      btnDropBoxClicked();
    }
    if (this.mAdView != null) {
      this.mAdView.resume();
    }
  }

  public void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }
}
