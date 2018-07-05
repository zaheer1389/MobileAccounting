package com.adslinfotech.mobileaccounting;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PictureStyleNotificationTask extends AsyncTask<String, Void, Bitmap> {
  private String goToUrl;
  private String imageUrl;
  private Context mContext;
  private String message;
  private String title;

  public PictureStyleNotificationTask(Context context, String title, String message, String imageUrl, String goToUrl) {
    this.mContext = context;
    this.title = title;
    this.message = message;
    this.imageUrl = imageUrl;
    this.goToUrl = goToUrl;
  }

  protected Bitmap doInBackground(String... params) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(this.imageUrl).openConnection();
      connection.setDoInput(true);
      connection.connect();
      return BitmapFactory.decodeStream(connection.getInputStream());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e2) {
      e2.printStackTrace();
      return null;
    }
  }

  @TargetApi(16)
  protected void onPostExecute(Bitmap result) {
    super.onPostExecute(result);
    NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(NOTIFICATION_SERVICE);
    Notification notif = new Builder(this.mContext)
            .setContentIntent(PendingIntent.getActivity(this.mContext, 100,
                    new Intent("android.intent.action.VIEW", Uri.parse(this.goToUrl)),
                    PendingIntent.FLAG_UPDATE_CURRENT)).setContentTitle(this.title)
            .setContentText(this.message).setSmallIcon(R.drawable.accounting_icon)
            .setLargeIcon(result).setStyle(new BigPictureStyle().bigPicture(result)).build();
    notif.flags |= 16;
    notificationManager.notify(1, notif);
  }
}
