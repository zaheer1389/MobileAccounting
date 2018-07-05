package com.adslinfotech.mobileaccounting.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.UpgradeResponse;
import com.adslinfotech.mobileaccounting.rest.ApiClient;
import com.adslinfotech.mobileaccounting.rest.ApiInterface;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.adslinfotech.mobileaccounting.utils.Permissions;
import com.adslinfotech.mobileaccounting.utils.UserEmailFetcher;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UpgradeToPremium extends SimpleAccountingActivity {
  private static int REQUEST_READ_PHONE_STATE = Permissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
  private static String TAG = "UpgradeToPremium";
  private static String mImeiNumber = "111";
  private Dialog dialogAlertPassword;
  private AtomicBoolean isPositiveAlertPassword;
  private AdView mAdView;
  private ImageView mCallus;
  private ImageView mEmaius;
  private EditText mEtPass;
  private ImageView mPlayStore;
  private ImageView mVisit;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_premium);
    this.isPositiveAlertPassword = new AtomicBoolean(false);
    this.mCallus = (ImageView) findViewById(R.id.callus);
    this.mEmaius = (ImageView) findViewById(R.id.emailus);
    this.mVisit = (ImageView) findViewById(R.id.visit);
    this.mPlayStore = (ImageView) findViewById(R.id.play_store);
    this.mCallus.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        UpgradeToPremium.this.startActivity(new Intent("android.intent.action.DIAL", Uri.fromParts("tel", "+917747004445", null)));
      }
    });
    this.mEmaius.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        UpgradeToPremium.this.sendEmail();
      }
    });
    this.mVisit.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent("android.intent.action.INSERT_OR_EDIT");
        i.setType("vnd.android.cursor.item/contact");
        i.putExtra("name", UpgradeToPremium.this.getString(R.string.app_name));
        i.putExtra("phone", "+919329479596");
        UpgradeToPremium.this.startActivity(i);
      }
    });
    this.mPlayStore.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        UpgradeToPremium.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.adslinfotech.simpleaccountingpro&hl=en")));
      }
    });
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  protected void sendEmail() {
    Log.i("Send email", "");
    String[] TO = new String[]{"adslinfosoft99@gmail.com"};
    String[] CC = new String[]{""};
    Intent emailIntent = new Intent("android.intent.action.SEND");
    emailIntent.setData(Uri.parse("mailto:"));
    emailIntent.setType("text/plain");
    emailIntent.putExtra("android.intent.extra.EMAIL", TO);
    emailIntent.putExtra("android.intent.extra.CC", CC);
    emailIntent.putExtra("android.intent.extra.SUBJECT", "");
    emailIntent.putExtra("android.intent.extra.TEXT", "");
    try {
      startActivity(Intent.createChooser(emailIntent, "Send mail..."));
      Log.i("UpgradeToPre", "Finished sending email...");
    } catch (ActivityNotFoundException e) {
      Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
    }
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_dialog_OK:
        this.dialogAlertPassword.dismiss();
        this.isPositiveAlertPassword.set(false);
        btnChkRenew();
        return;
      case R.id.btn_renew:
        getImei();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void getImei() {
    if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0) {
      ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, REQUEST_READ_PHONE_STATE);
      return;
    }
    setDeviceImei();
    showPinDialog();
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_READ_PHONE_STATE && grantResults[0] == 0) {
      setDeviceImei();
    }
    showPinDialog();
  }

  private void setDeviceImei() {
    mImeiNumber = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
  }

  private void showPinDialog() {
    if (AppUtils.isNetworkAvailable(getApplicationContext())) {
      this.mEtPass = showForgotPass(getResources().getString(R.string.app_name), "Please enter your subscription code for renew your account '" + UserEmailFetcher.getEmail(this) + "'.", "Enter Subscription Code");
      this.mEtPass.setTextColor(getResources().getColor(R.color.black));
      return;
    }
    showPositiveAlert(getString(R.string.txt_NoNetwork), getString(R.string.txt_Netwok));
  }

  private void btnChkRenew() {
    ApiInterface apiService = (ApiInterface) ApiClient.getClient("").create(ApiInterface.class);
    String email = "";
    try {
      email = UserEmailFetcher.getEmail(this);
    } catch (Exception e) {
      showPositiveAlert("Email Error", "Email! " + e.getMessage());
      Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
    String pin = "";
    try {
      pin = this.mEtPass.getText().toString();
    } catch (Exception e2) {
      showPositiveAlert("Pin Error", "Pin! " + e2.getMessage());
      Toast.makeText(this, e2.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
    try {
      showProgressDailog(this);
      apiService.validatePin(email, pin, mImeiNumber).enqueue(new Callback<UpgradeResponse>() {
        public void onResponse(Response<UpgradeResponse> response, Retrofit retrofit) {
          UpgradeToPremium.this.dismissDialog();
          try {
            Log.e(UpgradeToPremium.TAG, "statusCode: " + response.code());
            UpgradeToPremium.this.showPositiveAlert(UpgradeToPremium.this.getResources().getString(R.string.app_name), "" + ((UpgradeResponse) response.body()).getMessage());
            if (((UpgradeResponse) response.body()).isStatus()) {
              SessionManager.setProUser(true);
              UpgradeToPremium.this.mAdView = (AdView) UpgradeToPremium.this.findViewById(R.id.adView);
              UpgradeToPremium.this.mAdView.removeAllViews();
              return;
            }
            SessionManager.setProUser(false);
          } catch (Exception e) {
            Toast.makeText(UpgradeToPremium.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            UpgradeToPremium.this.showPositiveAlert(UpgradeToPremium.this.getString(R.string.txt_NoNetwork), UpgradeToPremium.this.getString(R.string.txt_Netwok));
          }
        }

        public void onFailure(Throwable t) {
          Log.e(UpgradeToPremium.TAG, t.toString());
          UpgradeToPremium.this.dismissDialog();
          Toast.makeText(UpgradeToPremium.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
      });
    } catch (Exception e22) {
      dismissDialog();
      showPositiveAlert("Pin Error", "Pin! " + e22.getMessage());
      Toast.makeText(this, e22.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
  }

  public EditText showForgotPass(String title, String Alert, String hint) {
    if (this.isPositiveAlertPassword.get()) {
      return null;
    }
    this.isPositiveAlertPassword.set(true);
    this.dialogAlertPassword = new Dialog(this, R.style.WindowTitleBackground);
    this.dialogAlertPassword.requestWindowFeature(1);
    this.dialogAlertPassword.setContentView(R.layout.dialog_forgot_pass);
    this.dialogAlertPassword.setCancelable(false);
    TextView mAlertText = (TextView) this.dialogAlertPassword.findViewById(R.id.txt_positive_alert);
    EditText mEtPass = (EditText) this.dialogAlertPassword.findViewById(R.id.et_Password);
    ((TextView) this.dialogAlertPassword.findViewById(R.id.title_alert)).setText(title);
    mAlertText.setText(Alert);
    mEtPass.setHint(hint);
    Button mButtonOk = (Button) this.dialogAlertPassword.findViewById(R.id.btn_dialog_OK);
    ((Button) this.dialogAlertPassword.findViewById(R.id.btn_dialog_Cancel)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        UpgradeToPremium.this.dialogAlertPassword.dismiss();
        UpgradeToPremium.this.isPositiveAlertPassword.set(false);
      }
    });
    mButtonOk.setOnClickListener(this);
    this.dialogAlertPassword.show();
    return mEtPass;
  }
}
