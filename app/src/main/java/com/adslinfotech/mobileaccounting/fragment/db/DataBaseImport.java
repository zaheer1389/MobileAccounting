package com.adslinfotech.mobileaccounting.fragment.db;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DbActivity;
import com.adslinfotech.mobileaccounting.activities.db.ImportDbActivity;
import com.adslinfotech.mobileaccounting.dao.Backup;
import com.adslinfotech.mobileaccounting.drive.ImportFromDriveActivity;
import com.adslinfotech.mobileaccounting.dropbox.DropBoxImportActivity;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class DataBaseImport extends BaseFragment {
  public static boolean mRefreshImportList = true;
  private AdView mAdView;
  private int mIndex = 0;
  private ArrayList<Backup> mListBackUpFiles;

  private class Task extends AsyncTask<String, Void, String> {
    private ArrayList<Backup> mFiles;
    private SimpleDateFormat mFormat;

    private Task() {
      this.mFiles = new ArrayList();
    }

    protected void onPreExecute() {
      ((DbActivity) DataBaseImport.this.getActivity()).showProgressDailog(DataBaseImport.this.getActivity());
      this.mFormat = new SimpleDateFormat("yyyy/MM/dd");
    }

    protected String doInBackground(String... arg0) {
      getBackUpFile(new File(Environment.getExternalStorageDirectory().getPath()));
      try {
        Collections.sort(this.mFiles);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    protected void onPostExecute(String result) {
      ((DbActivity) DataBaseImport.this.getActivity()).dismissDialog();
      DataBaseImport.this.mListBackUpFiles = this.mFiles;
      if (this.mFiles.size() == 0) {
        ((DbActivity) DataBaseImport.this.getActivity()).showPositiveAlert(null, "There is no backup file available in your sd card.");
      } else {
        DataBaseImport.this.showBackupList();
      }
    }

    private File[] getBackUpFile(File parentDir) {
      try {
        File[] listFiles = parentDir.listFiles(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            if (name.startsWith(".")) {
              return false;
            }
            if (name.endsWith(FILE_EXTENSION.BACKUP) || name.endsWith(FILE_EXTENSION.DB)) {
              return true;
            }
            return new File(dir.getAbsolutePath() + "/" + name).isDirectory();
          }
        });
        if (listFiles == null) {
          return listFiles;
        }
        for (File file : listFiles) {
          if (file.isDirectory()) {
            getBackUpFile(file);
          } else {
            Backup dao = new Backup();
            dao.setName(file.getName());
            dao.setPath(file.getAbsolutePath());
            dao.setDate(this.mFormat.format(new Date(file.lastModified())));
            this.mFiles.add(dao);
          }
        }
        return listFiles;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  public static DataBaseImport newInstance() {
    return new DataBaseImport();
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
          startActivity(new Intent(getActivity(), DropBoxImportActivity.class));
          return;
        case 2:
          startActivity(new Intent(getActivity(), ImportFromDriveActivity.class));
          return;
        default:
          return;
      }
    }
  }

  private void init(View view) {
    Button btnImport = (Button) view.findViewById(R.id.settleall);
    btnImport.setText(R.string.txt_Import);
    Button btnDropBox = (Button) view.findViewById(R.id.backup_dropbox);
    Button btnDrive = (Button) view.findViewById(R.id.backup_drive);
    btnDrive.setVisibility(View.VISIBLE);
    btnDropBox.setVisibility(View.VISIBLE);
    btnDropBox.setText("Import From Dropbox");
    btnDrive.setText("Import From Drive");
    btnImport.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        SessionManager.incrementInteractionCount();
        if (DataBaseImport.this.isStoragePermissionGranted()) {
          DataBaseImport.this.checkSDCard();
        } else {
          DataBaseImport.this.mIndex = 0;
        }
      }
    });
    btnDropBox.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        SessionManager.incrementInteractionCount();
        if (DataBaseImport.this.isStoragePermissionGranted()) {
          DataBaseImport.this.startActivity(new Intent(DataBaseImport.this.getActivity(), DropBoxImportActivity.class));
          return;
        }
        DataBaseImport.this.mIndex = 1;
      }
    });
    btnDrive.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        SessionManager.incrementInteractionCount();
        if (DataBaseImport.this.isStoragePermissionGranted()) {
          DataBaseImport.this.startActivity(new Intent(DataBaseImport.this.getActivity(), ImportFromDriveActivity.class));
          return;
        }
        DataBaseImport.this.mIndex = 2;
      }
    });
  }

  private void checkSDCard() {
    String state = Environment.getExternalStorageState();
    if (!"mounted".equals(state)) {
      Toast.makeText(getActivity(), "Sorry! SD card not found.", Toast.LENGTH_LONG).show();
    } else if ("mounted_ro".equals(state)) {
      Toast.makeText(getActivity(), "Please! Insert sd card ", Toast.LENGTH_LONG).show();
    } else if (mRefreshImportList || this.mListBackUpFiles == null) {
      mRefreshImportList = false;
      new Task().execute(new String[0]);
    } else {
      showBackupList();
    }
  }

  public void onClick(View v) {
  }

  private void showBackupList() {
    ImportDbActivity.newInstance(getActivity(), this.mListBackUpFiles);
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

  public void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }
}
