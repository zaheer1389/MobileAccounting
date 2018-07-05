package com.adslinfotech.mobileaccounting.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import java.util.ArrayList;

public class SessionManager {
  public static final String FIRST_TIME = "isAppInstalled";
  private static final String IS_LOGIN = "IsLoggedIn";
  private static final String KEY_BACKUP = "backup";
  private static final String KEY_EMAIL = "email";
  private static final String KEY_IS_PRO = "KEY_IS_PRO";
  private static final String KEY_MOBILE = "mobile";
  private static final String KEY_NAME = "Hii";
  private static final String KEY_PASS = "pass";
  private static final String KEY_USER_NAME = "username";
  public static final String LAST_SEARCH = "LAST_SEARCH";
  private static final String PREF_ALARM_DATE = "PREF_ALARM_DATE";
  public static final String PREF_BACKUP_MAIL = "PREF_BACKUP_MAIL";
  public static final String PREF_DROPBOX_TOKEN = "DB_ACCESS_TOKEN";
  private static final String PREF_IS_REFRESH_ACCOUNT_LIST = "PREF_IS_REFRESH_ACCOUNT_LIST";
  public static final String PREF_STORED_DB_NAME = "PREF_STORED_DB_NAME";
  private static final String PREF_USER_INTERACTION_COUNT = "PREF_USER_INTERACTION_COUNT";
  public static final String REGISTER_ON_SERVER = "count";
  public static final String REMEMBER_ME = "rememberMe";
  private static final String U_ID = "u_id";

  public static void createLoginSession(UserDao user) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_USER_NAME, user.getUserName());
    editor.putString(KEY_NAME, user.getName());
    editor.putString(KEY_PASS, user.getPassword());
    editor.putString("email", user.getEmail());
    editor.putString(KEY_MOBILE, user.getMobile());
    editor.putInt(U_ID, user.getUserID());
    editor.apply();
  }

  public static boolean isProUser() {
    return SimpleAccountingApp.getPreference().getBoolean(KEY_IS_PRO, false);
  }

  public static void setProUser(boolean rememberStatus) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putBoolean(KEY_IS_PRO, rememberStatus);
    editor.apply();
  }

  public static String getCurrency(Context context) {
    int id = Integer.parseInt(SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_CURRENCY, "0"));
    if (id == 0) {
      return "";
    }
    return context.getString(AppConstants.ARR_CURRENCY[id - 1]);
  }

  public static boolean getListOrder() {
    if (SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LIST_ORDER, "asc").equalsIgnoreCase("asc")) {
      return true;
    }
    return false;
  }

  public static long getBackUpDate() {
    return SimpleAccountingApp.getPreference().getLong(KEY_BACKUP, 0);
  }

  public static void setBackUpDate(long date) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putLong(KEY_BACKUP, date);
    editor.apply();
  }

  public static long getAlarmDate() {
    return SimpleAccountingApp.getPreference().getLong(PREF_ALARM_DATE, 0);
  }

  public static void setAlarmDate(long date) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putLong(PREF_ALARM_DATE, date);
    editor.apply();
  }

  public static void editProfile(String name, String email, String mobile) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putString(KEY_NAME, name);
    editor.putString("email", email);
    editor.putString(KEY_MOBILE, mobile);
    editor.apply();
  }

  public static boolean isPasswordRequired() {
    return SimpleAccountingApp.getPreference().getBoolean(ActivityPreferences.PREF_PASSWORD, true);
  }

  public static void setRememberMe(boolean rememberStatus) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putBoolean(REMEMBER_ME, rememberStatus);
    editor.apply();
  }

  public static ArrayList<Category> getAccountType(Context context) {
    String[] types = context.getResources().getStringArray(R.array.account_type);
    ArrayList<Category> accTypes = new ArrayList();
    for (int i = 0; i < 7; i++) {
      Category dao = new Category();
      dao.setName(types[i]);
      dao.setName(types[i]);
      accTypes.add(dao);
    }
    Category account = new Category();
    account.setName("All");
    account.setName("All");
    accTypes.add(0, account);
    return accTypes;
  }

  public static UserDao getAdminDao() {
    SharedPreferences pref = SimpleAccountingApp.getPreference();
    UserDao user = new UserDao();
    user.setUserID(pref.getInt(U_ID, 1));
    user.setUserName(pref.getString(KEY_USER_NAME, null));
    user.setPassword(pref.getString(KEY_PASS, null));
    user.setName(pref.getString(KEY_NAME, null));
    user.setEmail(pref.getString("email", null));
    user.setMobile(SimpleAccountingApp.getPreference().getString(KEY_MOBILE, null));
    return user;
  }

  public static String getEmail() {
    return SimpleAccountingApp.getPreference().getString("email", "");
  }

  public static String getMobile() {
    return SimpleAccountingApp.getPreference().getString(KEY_MOBILE, "");
  }

  public static String getName() {
    return SimpleAccountingApp.getPreference().getString(KEY_NAME, null);
  }

  public static String getUsername() {
    return SimpleAccountingApp.getPreference().getString(KEY_USER_NAME, "");
  }

  public static int getLoginUserId() {
    return SimpleAccountingApp.getPreference().getInt(U_ID, 0);
  }

  public static String getPassword() {
    return SimpleAccountingApp.getPreference().getString(KEY_PASS, null);
  }

  public static void setPassword(String password) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putString(KEY_PASS, password);
    editor.apply();
  }

  public static int getInteractionCount() {
    return SimpleAccountingApp.getPreference().getInt(PREF_USER_INTERACTION_COUNT, 0);
  }

  public static void setInteractionCount(int count) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putInt(PREF_USER_INTERACTION_COUNT, count);
    editor.apply();
  }

  public static void incrementInteractionCount() {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putInt(PREF_USER_INTERACTION_COUNT, getInteractionCount() + 1);
    editor.apply();
  }

  public static boolean isRefreshAccountList() {
    return SimpleAccountingApp.getPreference().getBoolean(PREF_IS_REFRESH_ACCOUNT_LIST, false);
  }

  public static void setRefreshAccountList(boolean refresh) {
    Editor editor = SimpleAccountingApp.getPreference().edit();
    editor.putBoolean(PREF_IS_REFRESH_ACCOUNT_LIST, refresh);
    editor.apply();
  }
}
