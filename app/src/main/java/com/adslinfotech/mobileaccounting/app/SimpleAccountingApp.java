package com.adslinfotech.mobileaccounting.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.database.DataBaseHandler;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.InterstitialAd;
import java.lang.ref.WeakReference;

public class SimpleAccountingApp extends MultiDexApplication {
  private static SimpleAccountingApp instance = null;
  private static InterstitialAd interstitial = null;
  private static boolean isActivityVisible = false;
  private static Handler mHandler = null;
  private static final int mInterval = 99000;
  private static SharedPreferences mPreferences;
  private static DataBaseHandler sDBHadler;
  Runnable mStatusChecker = new Runnable() {
    public void run() {
      try {
        SimpleAccountingApp.mHandler.sendEmptyMessage(0);
      } finally {
        SimpleAccountingApp.mHandler.postDelayed(SimpleAccountingApp.this.mStatusChecker, 99000);
      }
    }
  };

  private static class MyHandler extends Handler {
    private WeakReference<InterstitialAd> _interstitial;

    MyHandler(InterstitialAd interstitial) {
      this._interstitial = new WeakReference(interstitial);
    }

    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (!((InterstitialAd) this._interstitial.get()).isLoaded()) {
        SimpleAccountingApp.loadAds((InterstitialAd) this._interstitial.get());
      }
    }
  }

  public void onCreate() {
    super.onCreate();
    initialize();
    if (!SessionManager.isProUser()) {
      interstitial = new InterstitialAd(getApplicationContext());
      interstitial.setAdUnitId(getResources().getString(R.string.ad_id_interstitial));
      mHandler = new MyHandler(interstitial);
      startRepeatingTask();
    }
  }

  public static SimpleAccountingApp getInstance() {
    return instance;
  }

  private void initialize() {
    instance = this;
    sDBHadler = new DataBaseHandler(getApplicationContext());
    mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }

  public static DataBaseHandler getDBHandler() {
    return sDBHadler;
  }

  public static SharedPreferences getPreference() {
    return mPreferences;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public void onTerminate() {
    super.onTerminate();
    stopRepeatingTask();
    try {
      if (this.mStatusChecker != null) {
        this.mStatusChecker = null;
      }
      if (sDBHadler != null) {
        sDBHadler.close();
      }
      interstitial = null;
      sDBHadler = null;
    } catch (Exception e) {
    }
  }

  private void startRepeatingTask() {
    this.mStatusChecker.run();
  }

  private void stopRepeatingTask() {
    mHandler.removeCallbacks(this.mStatusChecker);
  }

  private static void loadAds(InterstitialAd interstitial) {
    try {
      interstitial.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void displayInterstitial() {
    try {
      if (SessionManager.getInteractionCount() > 2) {
        SessionManager.setInteractionCount(0);
        if (interstitial.isLoaded()) {
          interstitial.show();
          return;
        }
        return;
      }
      SessionManager.incrementInteractionCount();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showAds() {
    if (isActivityVisible) {
      if (!SessionManager.isProUser()) {
        displayInterstitial();
        loadAds(interstitial);
      } else {
        return;
      }
    }
    isActivityVisible = false;
  }
}
