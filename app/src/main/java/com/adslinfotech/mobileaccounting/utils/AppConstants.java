package com.adslinfotech.mobileaccounting.utils;

import android.net.Uri;
import com.adslinfotech.mobileaccounting.R;

public class AppConstants {
  public static final String ACCOUNT_DAO = "ACCOUNT_DAO";
  public static final String ACCOUNT_GLANCE_REPORT = "ACCOUNT_GLANCE_REPORT";
  public static final String ACCOUNT_REPORT = "Account_List";
  public static final String ACCOUNT_SELECTED = "accountId";
  public static final int ACTIVITY_FINISH = 1554;
  public static final int ACTIVITY_LOGOUT = 5323;
  public static final String ACTIVITY_NAME = "ActivityName";
  public static final int[] ARR_CURRENCY = new int[]{R.string.Rs, R.string.dollar, R.string.Dinar, R.string.euro, R.string.pound, R.string.Riyal, R.string.Ruble, R.string.Rupee, R.string.Yen, R.string.Yen, R.string.Malaysian_Ringgit};
  public static final int CHART_CATEGORY_LEDGER = 6;
  public static final int CHART_DATE_LEDGER = 7;
  public static final int CHART_DAY_COMMU_ACCOUT = 8;
  public static final int CHART_DAY_WISE_COMMU = 3;
  public static final int CHART_DAY_WISE_TRANS = 2;
  public static final int CREDIT = 1;
  public static final String DATA = "data";
  public static final String DATE_REPORT = "Date_Wise_Report";
  public static final String DAY_REPORT = "Day_Report";
  public static final int DEBIT = 0;
  public static final int DUBLICATE_ENTRY = 27;
  public static final String EDIT_TRANSACTION = "editTransaction";
  public static final String EMAIL = "email";
  public static final int EXEL = 1;
  public static final long FIVE_DATE = 1080000000;
  public static final String FOLDER = "/SimpleAccounting";
  public static final String INDEX = "index";
  public static final String LAST_TRAN_REPORT = "Last_Transactions_Report";
  public static final int MIN_INTERACTION_COUNT = 2;
  public static final String OVERALL_LEDGER = "Overall Ledger";
  public static final String PACKAGE_NAME = "com.adslinfotech.mobileaccounting";
  public static final String PASSWORD = "password";
  public static final int PDF = 0;
  public static final String PROMOTIONAL_APP_LINK = "\nFor more updates. please like our facebook page\nhttp://bit.ly/1TEjbYt\nFor downlaod More Apps. please visit:\nhttp://bit.ly/1LGUjOE";
  public static final String PROMOTIONAL_APP_LINK_FULL = "\nFor more updates. please like our facebook page\nhttps://www.facebook.com/ShreshthaEasyAccounting/?fref=ts\nFor downlaod More Apps. please visit:\nhttps://play.google.com/store/apps/developer?id=ADSL+Infotech";
  public static final int REQUEST_CODE_EDIT_TRAN = 78;
  public static final int RMD_APPOINTMENT = 0;
  public static final int RMD_BALANCE = 3;
  public static final int RMD_MONTHLY = 1;
  public static final int RMD_YEARLY = 2;
  public static final String SHARE_APP_MSG = "🌿🌹🌿🌹🌿🌹🌿🌹\nSimple Accounting APP\n(Android / iOS / Desktop)\nNo. 1 Personal Accounting APP\n\nSome of the features are highlighted below:\n\n* Very Simple Design & easy to understand \n* Show individual Account Balances (Ledger)\n* Multilingual Support available \n* Database is included within the application (No online storage)\n* Backup & Restore Facility (Database backup via email, dropbox and Google Drive also)\n\nADSL Infotech\nPlay Store Link\nhttps://play.google.com/store/apps/details?id=com.adslinfotech.mobileaccounting\nVisit our Website For Desktop Setup\nwww.indianandroidstore.com\n🍁🍁🍁🍁🍁🍁🍁🍁";
  public static final String TEST_DEVICE = "F1FF10EC6FDD42006E6484B61B5CF62E";
  public static final String YEAR_REPORT = "Last_12_MONTH_REPORT";

  public interface DIALOG {
    public static final int DIALOG_DELETE = 325;
    public static final int DIALOG_IMPORT = 621;
    public static final int DIALOG_UPDATE = 223;
    public static final int EXIT_APP = 64;
    public static final int FORGET_PASS = 39;
    public static final int OLD_BACKUP_IMPORT = 423;
  }

  public interface DateFormat {
    public static final String DB_DATE = "yyyy-MM-dd";
    public static final String DB_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String RMD_APPOINTMENT = "yyyy-MM-dd HH:mm";
    public static final String RMD_MONTHLY = "dd EEE  |  HH:mm";
    public static final String RMD_YEARLY = "dd MMM  |  HH:mm";
  }

  public interface EXTRA {
    public static final String HTML_FILE = "html_file";
    public static final String IS_APP_BACKGROUND = "IS_APP_BACKGROUND";
    public static final String PUSH_MESSAGE = "message";
    public static final String SELECTED_ACCOUNT_NAME = "SELECTED_ACCOUNT_NAME";
  }

  public interface FILE_EXTENSION {
    public static final String BACKUP = ".backup";
    public static final String DB = ".db";
  }

  public interface FILE_NAME_START {
    public static final String SA = "SA";
    public static final String SA_DB = "SA_DB";
  }

  public interface HTML {
    public static final String FAQ = "file:///android_asset/User_Manual_FAQ.htm";
    public static final String POLICY = "file:///android_asset/Policy.htm";
    public static final String USER_MANUAL = "file:///android_asset/User_Manual.htm";
  }

  public interface PERMISSION {
    public static final int SMS_CATEGORY_LEDGER = 283;
    public static final int STORAGE_EXPORT_BACKUP_HOME = 654;
    public static final int STORAGE_IMPORT_DROPBOX = 521;
    public static final int STORAGE_IMPORT_OLD_BACKUP = 451;
    public static final int STORAGE_OPEN_MANAGE_FILES = 139;
  }

  public interface REQUEST_CODE {
    public static final int REQUEST_LANG_CHANGE = 521;
    public static final int SEARCH_ACCOUNT = 124;
    public static final int SEARCH_ACCOUNT_DEBIT = 324;
    public static final int SELECTED_REMINDER = 452;
  }

  public interface URI {
    public static final Uri PLAY_STORE_ADSL = Uri.parse("https://play.google.com/store/apps/developer?id=ADSL+Infotech");
    public static final Uri PLAY_STORE_APP = Uri.parse("market://details?id=com.adslinfotech.mobileaccounting");
    public static final Uri WEBSITE = Uri.parse("http://indianandroidstore.com");
    public static final Uri WEBSITE_DESKTOP = Uri.parse("http://indianandroidstore.com/download.html");
    public static final Uri YOUTUBE_VIDEO = Uri.parse("https://www.youtube.com/watch?v=_0hN-ITl-wA");
  }
}
