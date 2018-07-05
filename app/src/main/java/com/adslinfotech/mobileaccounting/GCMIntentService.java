package com.adslinfotech.mobileaccounting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.util.Log;
import android.widget.RemoteViews;
import com.adslinfotech.mobileaccounting.ui.NotificationActivity;
import com.google.android.gcm.GCMBaseIntentService;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class GCMIntentService extends GCMBaseIntentService {
  private static final String TAG = "GCMIntentService";

  public GCMIntentService() {
    super(CommonUtilities.SENDER_ID);
  }

  protected void onRegistered(Context context, String registrationId) {
    Log.i(TAG, "Device registered: regId = " + registrationId);
    CommonUtilities.displayMessage(context, "Your device registred with GCM");
    Log.d("NAME", "GCMIntentService onRegistered");
  }

  protected void onUnregistered(Context context, String registrationId) {
    Log.i(TAG, "Device unregistered");
    CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
  }

  protected void onMessage(Context context, Intent intent) {
    Log.i(TAG, "Received message");
    String message = intent.getExtras().getString("price");
    String imgUrl = intent.getExtras().getString("imageurl");
    String redirectUrl = intent.getExtras().getString("redirecturl");
    CommonUtilities.displayMessage(context, message);
  }

  protected void onDeletedMessages(Context context, int total) {
    Log.i(TAG, "Received deleted messages notification");
    CommonUtilities.displayMessage(context, getString(R.string.gcm_deleted, new Object[]{Integer.valueOf(total)}));
  }

  public void onError(Context context, String errorId) {
    Log.i(TAG, "Received error: " + errorId);
    CommonUtilities.displayMessage(context, getString(R.string.gcm_error, new Object[]{errorId}));
  }

  protected boolean onRecoverableError(Context context, String errorId) {
    Log.i(TAG, "Received recoverable error: " + errorId);
    CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error, new Object[]{errorId}));
    return super.onRecoverableError(context, errorId);
  }

  public static void generateNotification(Context context, String title, String message) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    Notification notification = new Notification(R.drawable.accounting_icon, message, System.currentTimeMillis());
    Intent notificationIntent = new Intent(context, NotificationActivity.class);
    notificationIntent.setFlags(16);
    notificationIntent.setFlags(536870912);
    notificationIntent.setFlags(131072);
    notificationIntent.setFlags(603979776);
    notificationIntent.putExtra("message", message);
    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT);
    notification.flags |= 16;
    notification.defaults |= 1;
    RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
    contentView.setImageViewResource(R.id.image, R.drawable.accounting_icon);
    contentView.setTextViewText(R.id.title, title);
    contentView.setTextViewText(R.id.text, message);
    notification.contentView = contentView;
    if (VERSION.SDK_INT >= 16) {
      notification.bigContentView = contentView;
    }
    notification.contentIntent = intent;
    notification.defaults |= 2;
    notificationManager.notify(0, notification);
  }
}
