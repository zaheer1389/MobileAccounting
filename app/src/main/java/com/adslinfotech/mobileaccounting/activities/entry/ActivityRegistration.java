package com.adslinfotech.mobileaccounting.activities.entry;

import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.image.PhotoPicker;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityRegistration extends PhotoPicker implements OnClickListener, OnItemSelectedListener {
  private Dialog dialogRegisterPositive;
  private AtomicBoolean isRegisterAlertShowing;
  private Button mCancel;
  private EditText mEtCity;
  private EditText mEtCode;
  private EditText mEtConfirmPass;
  private EditText mEtEmail;
  private EditText mEtMobile;
  private EditText mEtName;
  private EditText mEtPassword;
  private EditText mEtUsername;
  private FetchData mFetchData;
  private ImageView mImgProfile;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private Button mSubmit;
  private Spinner spCurrencyType;
  private Spinner spLanguageType;
  private UserDao user = new UserDao();

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_register);
    this.isRegisterAlertShowing = new AtomicBoolean(false);
    this.mResource = getResources();
    this.mFetchData = new FetchData();
    getSupportActionBar().setTitle(this.mResource.getString(R.string.title_activity_registration));
    getViews();
    hideKeyPad();
    setAdapterCurrencyType();
    setAdapterLanguageType();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void getViews() {
    this.mSubmit = (Button) findViewById(R.id.submit);
    this.mCancel = (Button) findViewById(R.id.cancel);
    this.mEtName = (EditText) findViewById(R.id.efname);
    this.mEtUsername = (EditText) findViewById(R.id.ename);
    this.mEtPassword = (EditText) findViewById(R.id.repass);
    this.mEtConfirmPass = (EditText) findViewById(R.id.conpass);
    this.mEtEmail = (EditText) findViewById(R.id.eemail);
    this.mEtMobile = (EditText) findViewById(R.id.rmono);
    this.mEtCity = (EditText) findViewById(R.id.city);
    this.mEtCode = (EditText) findViewById(R.id.code);
    this.mImgProfile = (ImageView) findViewById(R.id.img_profile);
    this.spCurrencyType = (Spinner) findViewById(R.id.currency_type);
    this.spLanguageType = (Spinner) findViewById(R.id.language_type);
    this.mSubmit.setOnClickListener(this);
    this.mCancel.setOnClickListener(this);
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_alert_ok_register_dialog:
        if (this.dialogRegisterPositive != null) {
          this.dialogRegisterPositive.dismiss();
        }
        this.isRegisterAlertShowing.set(false);
        finish();
        return;
      case R.id.cancel:
        finish();
        return;
      case R.id.img_profile:
        alertBoxForUploadImageOp(false);
        return;
      case R.id.submit:
        validateFields();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void onPositiveClick(int from) {
    finish();
  }

  public void onNegativeClick(int from) {
    super.onNegativeClick(from);
    finish();
  }

  private void setAdapterCurrencyType() {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.currency_options));
    this.spCurrencyType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    this.spCurrencyType.setOnItemSelectedListener(this);
  }

  private void setAdapterLanguageType() {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.language_options));
    this.spLanguageType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    this.spLanguageType.setOnItemSelectedListener(this);
  }

  public void setImage(Bitmap photo) {
    if (photo != null) {
      photo = Bitmap.createScaledBitmap(photo, 1000, 1000, true);
      this.mImgProfile.setImageBitmap(photo);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      photo.compress(CompressFormat.JPEG, 90, baos);
      this.mNewEncodedImage = baos.toByteArray();
    }
  }

  private void validateFields() {
    String name = this.mEtName.getText().toString().trim();
    String mobile = this.mEtMobile.getText().toString().trim();
    String pass = this.mEtPassword.getText().toString().trim();
    String cpass = this.mEtConfirmPass.getText().toString();
    String email = this.mEtEmail.getText().toString().trim();
    String usernam = this.mEtUsername.getText().toString();
    String city = this.mEtCity.getText().toString();
    String code = this.mEtCode.getText().toString();
    Editor edit = SimpleAccountingApp.getPreference().edit();
    edit.putString("CITY", city);
    edit.putString("CODE", code);
    edit.commit();
    if (TextUtils.isEmpty(name)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_FirstName), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(usernam)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterUser), Toast.LENGTH_SHORT).show();
    } else if (usernam.contains(" ")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_InvalidUser), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(email)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterEmail), Toast.LENGTH_SHORT).show();
    } else if (!AppUtils.isEmailValid(email)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_ValidEmail), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(mobile)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterMobile), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(pass) || pass.equalsIgnoreCase(" ")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_plzEnterPass), Toast.LENGTH_SHORT).show();
    } else if (!cpass.equals(pass)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Passnotmatched), Toast.LENGTH_SHORT).show();
    } else if (this.mFetchData.isUserExists()) {
      Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
      showPositiveAlert(this.mResource.getString(R.string.txt_AlreadyReg), this.mResource.getString(R.string.txt_AlrdyReg));
    } else {
      this.user.setName(name);
      this.user.setUserName(usernam.toLowerCase());
      this.user.setMobile(mobile);
      this.user.setEmail(email);
      this.user.setPassword(pass.toLowerCase());
      this.user.setImage(this.mNewEncodedImage);
      addEntry();
    }
  }

  private void addEntry() {
    this.user.setUserID(this.mFetchData.insertLoginDetail(this.user));
    SessionManager.createLoginSession(this.user);
    Editor edit = SimpleAccountingApp.getPreference().edit();
    edit.putString(ActivityPreferences.PREF_CURRENCY, "" + this.spCurrencyType.getSelectedItemPosition());
    String languageType = this.spLanguageType.getSelectedItem().toString();
    edit.putString(ActivityPreferences.PREF_LANGUAGE, languageType);
    edit.commit();
    Locale locale = new Locale(languageType);
    Locale.setDefault(locale);
    Configuration config = new Configuration();
    config.locale = locale;
    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    setResult(-1);
    showRegisterAlert("Simple Accounting", this.mResource.getString(R.string.txt_registersucc));
  }

  public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
  }

  public void onNothingSelected(AdapterView<?> adapterView) {
  }

  private void showRegisterAlert(String title, String alert) {
    if (!this.isRegisterAlertShowing.get()) {
      this.isRegisterAlertShowing.set(true);
      this.dialogRegisterPositive = new Dialog(this, R.style.WindowTitleBackground);
      this.dialogRegisterPositive.requestWindowFeature(1);
      this.dialogRegisterPositive.setContentView(R.layout.dialog_regstraion);
      this.dialogRegisterPositive.setCancelable(false);
      TextView mAlertText = (TextView) this.dialogRegisterPositive.findViewById(R.id.txt_positive_alert);
      ((TextView) this.dialogRegisterPositive.findViewById(R.id.title_alert)).setText(title);
      mAlertText.setText(alert);
      ((Button) this.dialogRegisterPositive.findViewById(R.id.btn_alert_ok_register_dialog)).setOnClickListener(this);
      this.dialogRegisterPositive.show();
    }
  }
}
