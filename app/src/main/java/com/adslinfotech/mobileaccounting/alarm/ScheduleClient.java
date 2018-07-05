package com.adslinfotech.mobileaccounting.alarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.adslinfotech.mobileaccounting.alarm.ScheduleService.ServiceBinder;
import com.adslinfotech.mobileaccounting.dao.Reminder;

public class ScheduleClient {
  private ScheduleService mBoundService;
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      ScheduleClient.this.mBoundService = ((ServiceBinder) service).getService();
    }

    public void onServiceDisconnected(ComponentName className) {
      ScheduleClient.this.mBoundService = null;
    }
  };
  private Context mContext;
  private boolean mIsBound;

  public ScheduleClient(Context context) {
    this.mContext = context;
  }

  public void doBindService() {
    this.mContext.bindService(new Intent(this.mContext, ScheduleService.class), this.mConnection, 1);
    this.mIsBound = true;
  }

  public void setAlarmForNotification(Reminder reminder) {
    this.mBoundService.setAlarm(reminder);
  }

  public void doUnbindService() {
    if (this.mIsBound) {
      this.mContext.unbindService(this.mConnection);
      this.mIsBound = false;
    }
  }
}
