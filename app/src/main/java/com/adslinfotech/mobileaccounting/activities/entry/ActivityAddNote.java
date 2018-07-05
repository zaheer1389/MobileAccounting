package com.adslinfotech.mobileaccounting.activities.entry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class ActivityAddNote extends SimpleAccountingActivity implements OnClickListener {
  private Button ca;
  private EditText edescr;
  private EditText ename;
  private AdView mAdView;
  private FetchData mFetchData;

  public static void getInstance(Context context) {
    context.startActivity(new Intent(context, ActivityAddNote.class));
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_personalnote);
    getSupportActionBar().setTitle(getResources().getString(R.string.txt_addnote));
    this.mFetchData = new FetchData();
    getViews();
    this.ca.setOnClickListener(this);
  }

  private void getViews() {
    this.ename = (EditText) findViewById(R.id.ename);
    this.edescr = (EditText) findViewById(R.id.edescription);
    this.ca = (Button) findViewById(R.id.ca);
    this.ca.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.ca:
        String name = this.ename.getText().toString();
        String desc = this.edescr.getText().toString();
        if (name.equals("")) {
          Toast.makeText(getApplicationContext(), getString(R.string.txt_Entername), Toast.LENGTH_SHORT).show();
          return;
        } else if (desc.equals("")) {
          Toast.makeText(getApplicationContext(), getString(R.string.txt_EnterDes), Toast.LENGTH_SHORT).show();
          return;
        } else if (!false) {
          NoteDao accountdetail = new NoteDao();
          accountdetail.setHeading(name);
          accountdetail.setDescr(desc);
          this.mFetchData.insertNote(accountdetail);
          Toast.makeText(getApplicationContext(), getString(R.string.txt_DetailsAdd), Toast.LENGTH_SHORT).show();
          finish();
          return;
        } else {
          return;
        }
      default:
        return;
    }
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (this.mAdView != null) {
      this.mAdView.resume();
    }
  }

  public void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    super.onDestroy();
  }
}
