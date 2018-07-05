package com.adslinfotech.mobileaccounting.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import java.util.Calendar;

public class AlarmTask implements Runnable {
  private final Context context;
  private final Reminder dao;
  private final Calendar date = Calendar.getInstance();

  public AlarmTask(Context context, Reminder reminder) {
    this.context = context;
    this.date.setTime(reminder.getAlarmDate());
    this.dao = reminder;
  }

  public void run() {
    AlarmManager am = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pi = PendingIntent.getBroadcast(this.context, 1, new Intent(this.context, NotifyService.class), PendingIntent.FLAG_NO_CREATE);
    if (pi != null) {
      am.cancel(pi);
    }
    Intent i = new Intent(this.context, NotifyService.class);
    i.putExtra(AppConstants.DATA, this.dao);
    i.putExtra(NotifyService.INTENT_NOTIFY, true);
    am.set(0, this.date.getTimeInMillis(), PendingIntent.getService(this.context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT));
  }
}
