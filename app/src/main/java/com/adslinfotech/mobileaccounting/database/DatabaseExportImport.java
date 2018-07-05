package com.adslinfotech.mobileaccounting.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.fragment.db.DataBaseImport;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public class DatabaseExportImport {
  private static final File DATABASE_DIRECTORY = new File(Environment.getExternalStorageDirectory(), AppConstants.FOLDER);
  private static final String DATABASE_NAME = DataBaseHandler.name;
  private static final File DATA_DIRECTORY_DATABASE = new File(Environment.getDataDirectory() + "/data/" + "com.adslinfotech.mobileaccounting" + "/databases/" + DATABASE_NAME);
  private static final String[] DB_TABLE = new String[]{"Login", "Account", "Transection", "GeneralReminder", "AccountDetails", "AccountType"};
  private static File IMPORT_FILE;
  public static final String TAG = DatabaseExportImport.class.getName();
  public static boolean isShow21AccountDailog = false;

  private static class ImportTask extends AsyncTask<Void, Void, Void> {
    private File file;
    private int i;
    private WeakReference<SimpleAccountingActivity> mContext;

    ImportTask(SimpleAccountingActivity context, File file) {
      this.mContext = new WeakReference(context);
      this.file = file;
      DatabaseExportImport.isShow21AccountDailog = false;
    }

    protected void onPreExecute() {
      super.onPreExecute();
      try {
        SimpleAccountingActivity activity = (SimpleAccountingActivity) this.mContext.get();
        if (activity != null) {
          activity.showProgressDailog(activity);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    protected Void doInBackground(Void... params) {
      this.i = DatabaseExportImport.importTableFromFile(this.file);
      return null;
    }

    protected void onPostExecute(Void result) {
      SimpleAccountingActivity activity = (SimpleAccountingActivity) this.mContext.get();
      if (activity != null) {
        try {
          activity.dismissDialog();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      try {
        SessionManager.setRefreshAccountList(true);
        if (1 < this.i) {
          Toast.makeText(activity, activity.getString(R.string.backup_import_success), Toast.LENGTH_SHORT).show();
          SessionManager.createLoginSession(new FetchData().getProfileDetail());
        } else if (DatabaseExportImport.isShow21AccountDailog) {
          activity.showAlertExitApp(activity.getString(R.string.msg_21_account), 0);
        } else {
          Toast.makeText(activity, activity.getString(R.string.backup_import_fail), Toast.LENGTH_SHORT).show();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
      super.onPostExecute(result);
    }
  }

  public static File getDataBaseFile() {
    return DATA_DIRECTORY_DATABASE;
  }

  public static String exportDb() {
    DataBaseImport.mRefreshImportList = true;
    IMPORT_FILE = new File(DATABASE_DIRECTORY, FILE_NAME_START.SA + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP);
    File exportDir = DATABASE_DIRECTORY;
    if (!exportDir.exists()) {
      exportDir.mkdirs();
    }
    try {
      IMPORT_FILE.createNewFile();
      copyFile(DATA_DIRECTORY_DATABASE, IMPORT_FILE);
      String path = IMPORT_FILE.getPath();
      if (path == null) {
        return path;
      }
      SessionManager.setBackUpDate(new Date().getTime());
      return path;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void storeFinalDb() {
    final File exportDir = new File(Environment.getExternalStorageDirectory(), AppConstants.FOLDER);
    final File dbDir = new File(Environment.getDataDirectory() + "/data/" + "com.adslinfotech.mobileaccounting" + "/databases/" + DATABASE_NAME);
    String fileName = SimpleAccountingApp.getPreference().getString(SessionManager.PREF_STORED_DB_NAME, "");
    if (TextUtils.isEmpty(fileName)) {
      fileName = FILE_NAME_START.SA_DB + AppUtils.getUniqueFileName() + FILE_EXTENSION.BACKUP;
      SimpleAccountingApp.getPreference().edit().putString(SessionManager.PREF_STORED_DB_NAME, fileName).apply();
    }
    final String finalFileName = fileName;
    try {
      new Thread(new Runnable() {
        public void run() {
          try {
            File finalDb = new File(exportDir, finalFileName);
            if (!exportDir.exists()) {
              exportDir.mkdirs();
            }
            finalDb.createNewFile();
            DatabaseExportImport.copyFile(dbDir, finalDb);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void copyFile(File src, File dst) throws IOException {
    FileChannel inChannel = new FileInputStream(src).getChannel();
    FileChannel outChannel = new FileOutputStream(dst).getChannel();
    try {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      if (inChannel != null) {
        inChannel.close();
      }
      if (outChannel != null) {
        outChannel.close();
      }
    }
  }

  public static void importDb(SimpleAccountingActivity mContext, File file) {
    new ImportTask(mContext, file).execute(new Void[0]);
  }

  /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
  public static int importTableFromFile(File file) {
    int tableImported = -1;
    ArrayList<String> arrayList = new ArrayList(Arrays.asList(DB_TABLE));
    Cursor cursor = null;
    SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase(file.getPath(), null, 0);
    int importCount = 0;
    cursor = sqlDb.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
    Cursor cursorClm = null;
    while (cursor.moveToNext()) {

      String tableName = cursor.getString(0);
      if (arrayList.contains(tableName)) {
        int index = arrayList.indexOf(tableName);
        if (index == 1 && !SessionManager.isProUser()) {
          cursorClm = sqlDb.query(tableName, null, null, null, null, null, null, "25");
          if (cursorClm.getCount() > 21) {
            isShow21AccountDailog = true;
            cursorClm.close();
            break;
          }
        }
        try {
          cursorClm = sqlDb.query(tableName, null, null, null, null, null, null, "1");
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          try {
            cursor.close();
          } catch (Exception e2) {
          }
        }
        ArrayList<String> clmInFile = new ArrayList(Arrays.asList(cursorClm.getColumnNames()));
        ArrayList<String> clmInDb = DataBaseAdapter.getColumns(index);
        Log.e(TAG, "table columns: " + clmInFile);
        if (!clmInFile.containsAll(clmInDb)) {
          Log.e(TAG, "table not contain clm: " + clmInDb);
          cursorClm.close();
          break;
        }
        importCount++;
        cursorClm.close();
      }
    }
    if (importCount <= 5 || cursor.getCount() == 0) {
      Log.e(TAG, "Invalid database");
    } else {
      boolean isSecondAttempt;
      Log.e(TAG, "valid database");
      SQLiteDatabase db = SimpleAccountingApp.getDBHandler().getReadableDatabase();
      cursorClm = db.query("Login", null, null, null, null, null, null, null);
      if (cursorClm != null && cursorClm.getCount() > 0) {
        Log.e(TAG, "insertLogin to backup file: " + (!DataBaseAdapter.insertData(sqlDb, cursorClm, (String) arrayList.get(0), 0)));
      }
      Log.e(TAG, "first attampt");
      try {
        copyFile(file, DATA_DIRECTORY_DATABASE);
        db = SimpleAccountingApp.getDBHandler().getWritableDatabase();
        try {
          db.execSQL("ALTER TABLE Transection ADD COLUMN EntryDate Date");
        } catch (Exception e3) {
        }
        try {
          db.execSQL("ALTER TABLE GeneralReminder ADD COLUMN EntryDate Date");
        } catch (Exception e4) {
        }
        try {
          db.execSQL("ALTER TABLE Login ADD COLUMN BackupDate Date");
        } catch (Exception e5) {
        }
        try {
          cursorClm = db.rawQuery("SELECT * FROM Transection where Date is null or EntryDate is null", null);
          DataBaseAdapter.updateData(db, cursorClm, "Transection", 1);
        } catch (Exception e6) {
        }
        try {
          cursorClm = db.rawQuery("SELECT * FROM GeneralReminder where EntryDate is null", null);
          DataBaseAdapter.updateData(db, cursorClm, "GeneralReminder", 2);
        } catch (Exception e7) {
        }
        try {
          cursorClm.close();
        } catch (Exception e8) {
        }
        tableImported = 5;
        isSecondAttempt = false;
      } catch (Exception e9) {
        Log.e(TAG, "first attempt failed");
        e9.printStackTrace();
        isSecondAttempt = true;
        try {
          cursorClm = db.query("Login", null, null, null, null, null, null, null);
        } catch (Exception e10) {
        }
        try {
          DATA_DIRECTORY_DATABASE.delete();
          db = SimpleAccountingApp.getDBHandler().getWritableDatabase();
          if (cursorClm != null && cursorClm.getCount() > 0) {
            DataBaseAdapter.insertData(db, cursorClm, (String) arrayList.get(0), 0);
          }
          cursorClm.close();
        } catch (Exception e22) {
          e22.printStackTrace();
        }
      }
      if (isSecondAttempt) {
        int loginSkiped = 0;
        Log.e(TAG, "second attempt");
        db = SimpleAccountingApp.getDBHandler().getWritableDatabase();
        cursorClm = db.query("Login", null, null, null, null, null, null, null);
        if (cursorClm != null && cursorClm.getCount() > 0) {
          arrayList.remove(0);
          loginSkiped = 1;
          Log.e(TAG, "skip login table import from backup file in second attempt");
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
          String table = (String) it.next();
          Log.e(TAG, "table: " + table + " loginSkiped: " + loginSkiped);
          try {
            cursorClm = sqlDb.query(table, null, null, null, null, null, null, null);
            DataBaseAdapter.insertData(db, cursorClm, table, arrayList.indexOf(table) + loginSkiped);
            tableImported++;
          } catch (Exception e222) {
            e222.printStackTrace();
          }
          cursorClm.close();
        }
      }
    }
    cursor.close();
    try {
      cursor.close();
    } catch (Exception e11) {
    }
    return tableImported;
  }
}
