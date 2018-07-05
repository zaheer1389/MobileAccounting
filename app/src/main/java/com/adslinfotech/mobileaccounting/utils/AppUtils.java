package com.adslinfotech.mobileaccounting.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.adslinfotech.mobileaccounting.alarm.AlarmTask;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AppUtils {
  public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+");
  private static final String TAG = "AppUtils";

  public static boolean isEmailValid(String mEmail) {
    return EMAIL_ADDRESS_PATTERN.matcher(mEmail).matches();
  }

  public static SimpleDateFormat getDateFormat() {
    return new SimpleDateFormat(SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_DATE_FORMAT, "dd/MM/yyyy"));
  }

  public static boolean setImage(ImageView img, byte[] byteArray) {
    if (byteArray == null || byteArray.length == 0) {
      return true;
    }
    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    if (bmp == null) {
      return false;
    }
    img.setImageBitmap(bmp);
    return false;
  }

  public static final boolean isNetworkAvailable(Context context) {
    return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
  }

  public static String capitalWord(String source) {
    if (TextUtils.isEmpty(source)) {
      return "";
    }
    StringBuffer res = new StringBuffer();
    for (String str : source.split(" ")) {
      char[] stringArray = str.trim().toCharArray();
      stringArray[0] = Character.toUpperCase(stringArray[0]);
      res.append(new String(stringArray)).append(" ");
    }
    return res.toString();
  }

  public static NumberFormat getCurrencyFormatter() {
    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    return new DecimalFormat(((DecimalFormat) nf).toPattern().replace("¤", "").trim());
  }

  public static int getColor(Context context, int id) {
    if (VERSION.SDK_INT >= 23) {
      return context.getColor(id);
    }
    return context.getResources().getColor(id);
  }

  public static void setDrawable(Context context, View layout, int id) {
    if (VERSION.SDK_INT < 16) {
      layout.setBackgroundDrawable(context.getResources().getDrawable(id));
    } else if (VERSION.SDK_INT < 22) {
      layout.setBackground(context.getResources().getDrawable(id));
    } else {
      layout.setBackground(ContextCompat.getDrawable(context, id));
    }
  }

  public static String getValidFileName(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return "";
    }
    fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "");
    if (fileName.length() > 15) {
      return fileName.substring(0, 15);
    }
    return fileName;
  }

  public static String getUniqueFileName() {
    return new SimpleDateFormat("_yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
  }

  public static Date getNextAlarmTime(Context context) {
    Calendar calendar = Calendar.getInstance();
    Date today = new Date();
    Date alarmDate = null;
    Reminder alarmDao = null;
    calendar.setTime(today);
    int year = calendar.get(1);
    int month = calendar.get(2);
    int day = calendar.get(5);
    SimpleDateFormat mFormat = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
    Iterator it = new FetchData().getReminder(new SimpleDateFormat(DateFormat.DB_DATE).format(today)).iterator();
    while (it.hasNext()) {
      Reminder dao = (Reminder) it.next();
      try {
        Log.e(TAG, "--------------------------------------------");
        Date rmdDate = mFormat.parse(dao.getDate());
        switch (dao.getRmdType()) {
          case 0:
            Log.e(TAG, "APPOINTMENT: " + dao.getBeforeDay() + " , type: " + dao.getRmdType());
            calendar.setTime(rmdDate);
            break;
          case 1:
            Log.e(TAG, "MONTHLY: " + dao.getBeforeDay() + " , type: " + dao.getRmdType());
            calendar.setTime(rmdDate);
            calendar.set(1, year);
            calendar.set(2, month);
            break;
          case 2:
            Log.e(TAG, "YEARLY: " + dao.getBeforeDay() + " , type: " + dao.getRmdType());
            calendar.setTime(rmdDate);
            calendar.set(1, year);
            break;
        }
        calendar.set(5, day);
        Date showDate = calendar.getTime();
        Log.e(TAG, "Today Date: " + today);
        Log.e(TAG, "Reminder Date: " + rmdDate);
        Log.e(TAG, "ShowDate Date: " + showDate);
        if (today.before(showDate)) {
          Log.e(TAG, "RMD condition satisfied: today is before showDate and after beforeDate");
          if (alarmDate == null || alarmDate.after(showDate)) {
            alarmDate = showDate;
            alarmDao = dao;
          } else {
            Log.e(TAG, "reminder already set on alarmDate: " + alarmDate);
          }
        }
        Log.e(TAG, "alarmDate: " + alarmDate);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    calendar = Calendar.getInstance();
    if (calendar.get(11) < 22) {
      Log.e(TAG, "today below 10 PM");
      calendar.set(11, 22);
      calendar.set(12, 0);
      calendar.set(13, 0);
    } else {
      Log.e(TAG, "today above 10 PM");
      calendar.add(5, 1);
      calendar.set(11, 22);
      calendar.set(12, 0);
      calendar.set(13, 0);
    }
    Date balAlarm = calendar.getTime();
    if (alarmDate == null) {
      SessionManager.setAlarmDate(balAlarm.getTime());
      alarmDao = new Reminder();
      alarmDao.setRmdType(3);
      alarmDao.setAlarmDate(balAlarm);
      alarmDao.setDate(mFormat.format(balAlarm));
      new AlarmTask(context, alarmDao).run();
    } else {
      Log.e(TAG, "balAlarm: " + balAlarm + " before ");
      Log.e(TAG, "alarmDate: " + alarmDate + balAlarm.before(alarmDate));
      if (balAlarm.before(alarmDate)) {
        alarmDate = balAlarm;
        alarmDao = new Reminder();
        alarmDao.setRmdType(3);
        alarmDao.setDate(mFormat.format(balAlarm));
        alarmDao.setAlarmDate(balAlarm);
      }
      SessionManager.setAlarmDate(alarmDate.getTime());
      alarmDao.setAlarmDate(alarmDate);
      new AlarmTask(context, alarmDao).run();
    }
    return alarmDate;
  }
}
