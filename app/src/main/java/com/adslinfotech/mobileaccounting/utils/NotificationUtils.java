package com.adslinfotech.mobileaccounting.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v7.app.NotificationCompat.Builder;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent, String imageUrl) {
        if (!TextUtils.isEmpty(message)) {
            intent.setFlags(603979776);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this.mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Builder mBuilder = new Builder(this.mContext);
            Uri alarmSound = Uri.parse("android.resource://" + this.mContext.getPackageName() + "/raw/notification");
            if (TextUtils.isEmpty(imageUrl)) {
                showSmallNotification(mBuilder, R.drawable.accounting_icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                playNotificationSound();
            } else if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, R.drawable.accounting_icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, R.drawable.accounting_icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        }
    }

    private void showSmallNotification(Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        InboxStyle inboxStyle = new InboxStyle();
        inboxStyle.addLine(message);
        ((NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(100, mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0).setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent).setSound(alarmSound).setStyle(inboxStyle).setWhen(getTimeMilliSec(timeStamp)).setSmallIcon(R.drawable.accounting_icon).setLargeIcon(BitmapFactory.decodeResource(this.mContext.getResources(), icon)).setContentText(message).build());
    }

    private void showBigNotification(Bitmap bitmap, Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        BigPictureStyle bigPictureStyle = new BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        ((NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(101, mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0).setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent).setSound(alarmSound).setStyle(bigPictureStyle).setWhen(getTimeMilliSec(timeStamp)).setSmallIcon(R.drawable.accounting_icon).setLargeIcon(BitmapFactory.decodeResource(this.mContext.getResources(), icon)).setContentText(message).build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(strURL).openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playNotificationSound() {
        try {
            RingtoneManager.getRingtone(this.mContext, Uri.parse("android.resource://" + this.mContext.getPackageName() + "/raw/notification")).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (VERSION.SDK_INT > 20) {
            for (RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo.importance == 100) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
            return isInBackground;
        } else if (((RunningTaskInfo) am.getRunningTasks(1).get(0)).topActivity.getPackageName().equals(context.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    public static void clearNotifications(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        try {
            return new SimpleDateFormat(DateFormat.DB_DATE_TIME).parse(timeStamp).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
