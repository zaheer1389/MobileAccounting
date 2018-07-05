package com.adslinfotech.mobileaccounting.activities.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditPersonalNote;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class NoteDetailScreen extends SimpleAccountingActivity {
  private AdView mAdView;
  private NoteDao mDao;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_note_detail);
    init();
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void init() {
    this.mDao = (NoteDao) getIntent().getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED);
    TextView tvDetail = (TextView) findViewById(R.id.tv_note_detail);
    ((TextView) findViewById(R.id.tv_note_heading)).setText(this.mDao.getHeading());
    tvDetail.setText(this.mDao.getDescr());
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_category_edit:
      case R.id.lout_note_detail:
        Intent intent = new Intent(this, ActivityEditPersonalNote.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mDao);
        startActivityForResult(intent, 1);
        return;
      case R.id.btn_category_share:
        shareDetail();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != Menus.HOME) {
      return super.onOptionsItemSelected(item);
    }
    finish();
    return false;
  }

  private void shareDetail() {
    new ShareHelper(this, this.mDao.getHeading() + " Details:", this.mDao.getHeading() + "\n" + this.mDao.getDescr() + "\n Via: Simple Accounting Android App\ndownload this app\nhttp://bit.ly/1LGUjOE", this.mDao.getDescr() + "\n Via: Simple Accounting Android App\ndownload this app\nhttp://bit.ly/1LGUjOE", "http://bit.ly/1LGUjOE", "http://bit.ly/1LGUjOE").share();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == 0) {
      finish();
      return;
    }
    try {
      this.mDao = (NoteDao) data.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED);
      TextView tvDetail = (TextView) findViewById(R.id.tv_note_detail);
      ((TextView) findViewById(R.id.tv_note_heading)).setText(this.mDao.getHeading());
      tvDetail.setText(this.mDao.getDescr());
    } catch (Exception e) {
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
    this.mAdView = null;
    super.onDestroy();
  }
}
