package com.adslinfotech.mobileaccounting.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.adslinfotech.mobileaccounting.utils.AppUtils;

public class RebootReceiver extends BroadcastReceiver {
  public void onReceive(Context context, Intent intent) {
    try {
      AppUtils.getNextAlarmTime(context);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
