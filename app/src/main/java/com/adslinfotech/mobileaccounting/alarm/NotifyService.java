package com.adslinfotech.mobileaccounting.alarm;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.ShowReminderActivity;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.ActivityBalanceNotification;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotifyService extends Service {
  public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
  private static final int NOTIFICATION = 123;
  private static final String TAG = "NotifyService";
  private Reminder dao;
  protected DecimalFormat df = new DecimalFormat("#.##", this.otherSymbols);
  private final IBinder mBinder = new ServiceBinder();
  private NotificationManager mNM;
  public NumberFormat newFormat = new DecimalFormat(this.newPattern);
  String newPattern = this.pattern.replace("¤", "").trim();
  NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
  DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale("en"));
  String pattern = ((DecimalFormat) this.nf).toPattern();

  public class ServiceBinder extends Binder {
    NotifyService getService() {
      return NotifyService.this;
    }
  }

  public void onCreate() {
    Log.i(TAG, "onCreate()");
    this.mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
  }

  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("LocalService", "Received start id " + startId + ": " + intent);
    this.dao = (Reminder) intent.getExtras().get(AppConstants.DATA);
    if (intent.getBooleanExtra(INTENT_NOTIFY, false)) {
      if (VERSION.SDK_INT >= 16) {
        sendInboxStyleNotification();
      } else {
        showNotification();
      }
      AppUtils.getNextAlarmTime(getApplicationContext());
      stopSelf();
    }
    return Service.START_NOT_STICKY;
  }

  public IBinder onBind(Intent intent) {
    return this.mBinder;
  }

  private void showNotification() {
    String balance;
    CharSequence text;
    String strDate = new SimpleDateFormat(DateFormat.DB_DATE_TIME).format(new Date());
    Transaction mTransaction = new FetchData().getTodayTransactions(strDate, strDate);
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    double credit = mTransaction.getCraditAmount();
    double debit = mTransaction.getDebitAmount();
    if (debit > credit) {
      balance = this.newFormat.format(Double.valueOf(this.df.format(debit - credit)).doubleValue()) + "/-Db";
    } else if (debit == credit) {
      balance = "0.00";
    } else {
      balance = this.newFormat.format(Double.valueOf(this.df.format(credit - debit)).doubleValue()) + "/-Cr";
    }
    String Message = "Today Date: " + strDate + "\nTotal Credit: " + mRsSymbol + this.newFormat.format(credit) + "\nTotal Debit: " + mRsSymbol + this.newFormat.format(debit) + "\nTotal Balance: " + mRsSymbol + balance;
    int icon = getResources().getIdentifier("easyaccounting_icon", "drawable", getPackageName());
    String remark = this.dao.getRemark();
    long time = System.currentTimeMillis();
    if (TextUtils.isEmpty(remark)) {
      text = this.dao.getDescription();
    } else {
      text = this.dao.getDescription() + ": " + remark;
    }
    Notification notification;
    if (this.dao.getRmdType() == 3) {
      notification = new Notification(icon, Message, time);
    } else {
      notification = new Notification(icon, text, time);
    }
    Intent intent;
    if (this.dao.getRmdType() == 3) {
      intent = new Intent(getApplicationContext(), ActivityBalanceNotification.class);
      intent.putExtra("BALANCE", Message);
    } else {
      intent = new Intent(getApplicationContext(), ShowReminderActivity.class);
    }
    intent.setFlags(603979776);
    PendingIntent intent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    notification.flags |= 16;
    notification.defaults |= 1;
    notification.defaults |= 2;
    this.mNM.notify(123, notification);
  }

  @RequiresApi(api = 16)
  public void sendInboxStyleNotification() {
    String balance;
    PendingIntent pi;
    PendingIntent shareIntent;
    String notfyName;
    String type;
    String text;
    String notifyText;
    Log.e(TAG, "sendInboxStyleNotification");
    String strDate = new SimpleDateFormat(DateFormat.DB_DATE_TIME).format(new Date());
    Transaction mTransaction = new FetchData().getTodayTransactions(strDate, strDate);
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    double credit = mTransaction.getCraditAmount();
    double debit = mTransaction.getDebitAmount();
    if (debit > credit) {
      balance = this.newFormat.format(Double.valueOf(this.df.format(debit - credit)).doubleValue()) + "/-Db";
    } else if (debit == credit) {
      balance = "0.00";
    } else {
      balance = this.newFormat.format(Double.valueOf(this.df.format(credit - debit)).doubleValue()) + "/-Cr";
    }
    String Message = "Today Date: " + strDate + "\nTotal Credit: " + mRsSymbol + this.newFormat.format(credit) + "\nTotal Debit: " + mRsSymbol + this.newFormat.format(debit) + "\nTotal Balance: " + mRsSymbol + balance;
    Intent intent;
    if (this.dao.getRmdType() == 3) {
      intent = new Intent(getApplicationContext(), ActivityShare.class);
      Intent intent2 = new Intent(getApplicationContext(), ActivityBalanceNotification.class);
      intent2.putExtra("BALANCE", Message);
      pi = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
      shareIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    } else {
      intent = new Intent(getApplicationContext(), ActivityShare.class);
      pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ShowReminderActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
      shareIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    switch (this.dao.getRmdType()) {
      case 0:
        Log.e("RMD_APPOINTMENT", "RMD_MONTHLY");
        notfyName = "Appointment Reminder";
        type = this.dao.getDescription();
        break;
      case 1:
        Log.e("RMD_MONTHLY", "RMD_MONTHLY");
        notfyName = "Monthly Reminder";
        type = this.dao.getDescription();
        break;
      case 2:
        Log.e("RMD_YEARLY", "RMD_YEARLY");
        notfyName = "Yearly Reminder";
        type = this.dao.getDescription();
        break;
      case 3:
        Log.e("RMD_BALANCE", "RMD_BALANCE");
        notfyName = "Today Reminder";
        type = Message;
        break;
      default:
        notfyName = "Simple Accounting";
        type = "Reminder notification!!";
        break;
    }
    String desc = this.dao.getDescription();
    if (TextUtils.isEmpty(desc)) {
      text = this.dao.getRemark();
    } else {
      text = this.dao.getRemark() + ": " + desc;
    }
    if (this.dao.getRmdType() == 3) {
      notifyText = type;
    } else {
      notifyText = text;
    }
    Notification notification = new InboxStyle(new Builder(this).setContentTitle(notfyName).setContentText(type).setSmallIcon(R.drawable.accounting_icon).setContentIntent(pi).addAction(R.drawable.refer_app_to_friend, "Share App", shareIntent)).addLine(notifyText).build();
    notification.flags |= 16;
    notification.defaults |= 1;
    notification.defaults |= 2;
    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);
  }
}
