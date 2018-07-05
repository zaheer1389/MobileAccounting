package com.adslinfotech.mobileaccounting.activities.edit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class ActivityEditPersonalNote extends ActivityEdit implements OnClickListener {
  private static final int DIALOG_DELETE = 234;
  private static final int DIALOG_UPDATE = 56;
  private EditText edescr;
  private EditText ename;
  private NoteDao mAccountDetailsDao;
  private AdView mAdView;
  private FetchData mFetchData;
  private Resources mResource;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_editnote);
    this.mResource = getResources();
    this.mFetchData = new FetchData();
    getViews();
    setData();
    hideKeyPad();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void setData() {
    this.mAccountDetailsDao = (NoteDao) getIntent().getSerializableExtra(AppConstants.ACCOUNT_SELECTED);
    this.ename.setText(this.mAccountDetailsDao.getHeading());
    this.edescr.setText(this.mAccountDetailsDao.getDescr());
  }

  private void getViews() {
    this.ename = (EditText) findViewById(R.id.ename);
    this.edescr = (EditText) findViewById(R.id.edescription);
    Button btDelete = (Button) findViewById(R.id.btn_delete);
    Button btCancel = (Button) findViewById(R.id.btn_cancel);
    ((Button) findViewById(R.id.btn_save)).setOnClickListener(this);
    btDelete.setOnClickListener(this);
    btCancel.setOnClickListener(this);
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
      case R.id.btn_cancel:
        finish();
        return;
      case R.id.btn_delete:
        checkPasswordRequired(DIALOG_DELETE);
        return;
      case R.id.btn_save:
        checkPasswordRequired(56);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void passwordConfirmed(int i) {
    if (i == 56) {
      validateFields();
    } else {
      deleteTransaction();
    }
  }

  protected void setImage(Bitmap thumbnail) {
  }

  private void validateFields() {
    String name = this.ename.getText().toString();
    String desc = this.edescr.getText().toString();
    if (name.equals("")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Entername), Toast.LENGTH_SHORT).show();
    } else if (desc.equals("")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterDes), Toast.LENGTH_SHORT).show();
    } else if (!false) {
      this.mAccountDetailsDao.setHeading(name);
      this.mAccountDetailsDao.setDescr(desc);
      if (this.mFetchData.updateNote(this.mAccountDetailsDao) == 0) {
        Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_DetailsNotUpdate), Toast.LENGTH_SHORT).show();
        return;
      }
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_DetailsUpdate), Toast.LENGTH_SHORT).show();
      Intent intent = new Intent();
      intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mAccountDetailsDao);
      setResult(1, intent);
      finish();
    }
  }

  private void deleteTransaction() {
    if (this.mFetchData.deleteNote(this.mAccountDetailsDao.getAccount_Id()) == 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_DetailsNotDelete), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_DetailsDelete), Toast.LENGTH_SHORT).show();
      setResult(0);
    }
    finish();
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
