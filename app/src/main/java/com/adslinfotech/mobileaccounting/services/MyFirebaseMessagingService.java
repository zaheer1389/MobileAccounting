package com.adslinfotech.mobileaccounting.services;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.ui.NotificationActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage != null) {
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                handleNotification(remoteMessage.getNotification().getBody());
            }
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
                try {
                    handleDataMessage(new JSONObject(remoteMessage.getData().toString()));
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra(EXTRA.IS_APP_BACKGROUND, false);
            intent.putExtra("message", message);
            new NotificationUtils(getApplicationContext()).showNotificationMessage(getResources().getString(R.string.app_name), message, "" + System.currentTimeMillis(), intent);
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject(AppConstants.DATA);
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);
            if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                resultIntent.putExtra(EXTRA.IS_APP_BACKGROUND, true);
                resultIntent.putExtra("message", message);
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    return;
                } else {
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    return;
                }
            }
            Intent pushNotification = new Intent(getApplicationContext(), NotificationActivity.class);
            pushNotification.putExtra(EXTRA.IS_APP_BACKGROUND, false);
            pushNotification.putExtra("message", message);
            new NotificationUtils(getApplicationContext()).showNotificationMessage(getResources().getString(R.string.app_name), message, "" + System.currentTimeMillis(), pushNotification);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e2) {
            Log.e(TAG, "Exception: " + e2.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        this.notificationUtils = new NotificationUtils(context);
        intent.setFlags(268468224);
        this.notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        this.notificationUtils = new NotificationUtils(context);
        intent.setFlags(268468224);
        this.notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
