package com.adslinfotech.mobileaccounting.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.adslinfotech.mobileaccounting.dao.Reminder;

public class ScheduleService extends Service {
  private final IBinder mBinder = new ServiceBinder();

  public class ServiceBinder extends Binder {
    ScheduleService getService() {
      return ScheduleService.this;
    }
  }

  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
    return 1;
  }

  public IBinder onBind(Intent intent) {
    return this.mBinder;
  }

  public void setAlarm(Reminder reminder) {
    new AlarmTask(this, reminder).run();
  }
}
