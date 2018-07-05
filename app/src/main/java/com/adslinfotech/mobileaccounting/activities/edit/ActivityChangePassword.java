package com.adslinfotech.mobileaccounting.activities.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class ActivityChangePassword extends SimpleAccountingActivity implements OnClickListener {
  private AdView mAdView;
  private EditText mEtConfirmPass;
  private EditText mEtNeWPass;
  private EditText mEtOldPass;
  private FetchData mFetchData;
  private int mUserID;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mFetchData = new FetchData();
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[3]);
    setContentView((int) R.layout.activity_changepassword);
    this.mEtOldPass = (EditText) findViewById(R.id.oldpass);
    this.mEtNeWPass = (EditText) findViewById(R.id.enewpass);
    this.mEtConfirmPass = (EditText) findViewById(R.id.econpass);
    ((Button) findViewById(R.id.bt_submit)).setOnClickListener(this);
    hideKeyPad();
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void validateChangePass() {
    String oldPass = this.mEtOldPass.getText().toString();
    String newPass = this.mEtNeWPass.getText().toString();
    String confirmPass = this.mEtConfirmPass.getText().toString();
    this.mUserID = SessionManager.getLoginUserId();
    String pass = SessionManager.getPassword();
    Log.d("ActivityChangePassword", "current pass and uname is =" + pass + "=uid" + this.mUserID);
    if (TextUtils.isEmpty(oldPass)) {
      this.mEtOldPass.setError(getString(R.string.txt_EnterOldpass));
    } else if (!oldPass.equals(pass)) {
      this.mEtOldPass.setError(getString(R.string.txt_EnterCurrpass));
    } else if (TextUtils.isEmpty(newPass)) {
      this.mEtNeWPass.setError(getString(R.string.txt_EnterNewpass));
    } else if (TextUtils.isEmpty(confirmPass)) {
      this.mEtConfirmPass.setError(getString(R.string.txt_EnterRepass));
    } else if (newPass.equals(confirmPass)) {
      changePassword(newPass);
    } else {
      this.mEtConfirmPass.setText("");
      this.mEtConfirmPass.setError(getString(R.string.txt_PassNotMatch));
    }
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.bt_submit:
        validateChangePass();
        break;
    }
    super.onClick(v);
  }

  private void changePassword(String newPass) {
    if (this.mFetchData.changepassword(this.mUserID, newPass) != 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_PasswordChange), Toast.LENGTH_SHORT).show();
      SessionManager.setRememberMe(false);
      SessionManager.setPassword(this.mFetchData.getProfileDetail().getPassword());
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
