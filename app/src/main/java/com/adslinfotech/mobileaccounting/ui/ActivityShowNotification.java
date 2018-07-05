package com.adslinfotech.mobileaccounting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.adslinfotech.mobileaccounting.utils.VerticalMarqueeTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ActivityShowNotification
  extends AppCompatActivity
{
  public static String PUSH_MESSAGE = "PUSH_MESSAGE";
  private VerticalMarqueeTextView VMTV;
  private AdView mAdView;

  public void onBackPressed()
  {
    Intent localIntent = new Intent(this, ActivityLogin.class);
    localIntent.setFlags(67108864);
    startActivity(localIntent);
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_show_notification);
    this.VMTV = ((VerticalMarqueeTextView)findViewById(R.id.textView));
    String string = getIntent().getExtras().getString(PUSH_MESSAGE);
    this.VMTV.setText(string);
    this.VMTV.setMovementMethod(new ScrollingMovementMethod());
    this.VMTV.pauseMarquee();
    boolean bool = AppUtils.isNetworkAvailable(getApplicationContext());
    if ((!SessionManager.isProUser()) && (bool))
    {
      this.mAdView = new AdView(this);
      this.mAdView = ((AdView)findViewById(R.id.adView));
      this.mAdView.setVisibility(View.VISIBLE);
      AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("").build();
      this.mAdView.loadAd(adRequest);
    }
    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bar));
  }

  protected void onDestroy()
  {
    this.VMTV.stopMarquee();
    super.onDestroy();
  }

  protected void onPause()
  {
    this.VMTV.pauseMarquee();
    super.onPause();
  }

  protected void onResume()
  {
    if (this.VMTV.isPaused()) {
      this.VMTV.resumeMarquee();
    }
    super.onResume();
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/ui/ActivityShowNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */