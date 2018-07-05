package com.adslinfotech.mobileaccounting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.adslinfotech.mobileaccounting.utils.VerticalMarqueeTextView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class NotificationActivity extends SimpleAccountingActivity {
    private VerticalMarqueeTextView VMTV;
    private boolean isAppInBackground;
    private AdView mAdView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_show_notification);
        this.VMTV = (VerticalMarqueeTextView) findViewById(R.id.textView);
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");
        this.isAppInBackground = bundle.getBoolean(EXTRA.IS_APP_BACKGROUND);
        this.VMTV.setText(message);
        this.VMTV.setMovementMethod(new ScrollingMovementMethod());
        this.VMTV.pauseMarquee();
        boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
        if (!SessionManager.isProUser() && isInternetPresent) {
            this.mAdView = new AdView(this);
            this.mAdView = (AdView) findViewById(R.id.adView);
            this.mAdView.setVisibility(View.INVISIBLE);
            this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
        }
    }

    public void onBackPressed() {
        if (this.isAppInBackground) {
            Intent intent = new Intent(this, ActivityLogin.class);
            intent.addFlags(4194304);
            intent.addFlags(131072);
            intent.addFlags(16);
            startActivity(intent);
        }
        finish();
    }

    public void onPause() {
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
        this.VMTV.pauseMarquee();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
        if (this.VMTV.isPaused()) {
            this.VMTV.resumeMarquee();
        }
    }

    protected void onDestroy() {
        this.VMTV.stopMarquee();
        if (this.mAdView != null) {
            this.mAdView.destroy();
        }
        this.mAdView = null;
        super.onDestroy();
    }
}
