package com.adslinfotech.mobileaccounting.services;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.adslinfotech.mobileaccounting.app.Config;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
    }

    private void sendRegistrationToServer(String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        Editor editor = SimpleAccountingApp.getPreference().edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
