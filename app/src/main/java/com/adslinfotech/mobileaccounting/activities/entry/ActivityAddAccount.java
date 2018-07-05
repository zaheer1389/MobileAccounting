package com.adslinfotech.mobileaccounting.activities.entry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.contact.ContactBean;
import com.adslinfotech.mobileaccounting.contact.ContactListActivity;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.image.PhotoPicker;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ActivityAddAccount extends PhotoPicker implements OnClickListener {
  private AdView mAdView;
  private Button mContact;
  private EditText mEtEmail;
  private EditText mEtMobile;
  private EditText mEtName;
  private EditText mEtRemarks;
  private ImageView mImgProfile;
  private byte[] mNewEncodedImage;
  private Button mSubmit;
  private Spinner spType;

  public static void getInstance(Context context) {
    context.startActivity(new Intent(context, ActivityAddAccount.class));
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle(getString(R.string.btn_Create_Account));
    FetchData mFetchData = new FetchData();
    boolean status = mFetchData.countTotalAccount();
    if (SessionManager.isProUser() || status) {
      setContentView((int) R.layout.activity_addaccount);
      this.mEtName = (EditText) findViewById(R.id.ename);
      this.mEtEmail = (EditText) findViewById(R.id.ceemail);
      this.mEtMobile = (EditText) findViewById(R.id.rmono);
      this.mEtRemarks = (EditText) findViewById(R.id.remarks);
      this.mSubmit = (Button) findViewById(R.id.ca);
      this.mContact = (Button) findViewById(R.id.btn_import);
      this.mImgProfile = (ImageView) findViewById(R.id.img_profile);
      this.spType = (Spinner) findViewById(R.id.sp_type);
      this.mSubmit.setOnClickListener(this);
      this.mContact.setOnClickListener(this);
      setSpAdapter(mFetchData);
      hideKeyPad();
      boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
      if (!SessionManager.isProUser() && isInternetPresent) {
        this.mAdView = new AdView(getApplicationContext());
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.setVisibility(View.VISIBLE);
        this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
        return;
      }
      return;
    }
    showAlertExitApp(getString(R.string.txt_LimitedEntry), 0);
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != Menus.HOME) {
      return super.onOptionsItemSelected(item);
    }
    finish();
    return false;
  }

  private void setSpAdapter(FetchData mFetchData) {
    Category category;
    ArrayList<Category> type = mFetchData.getAllCategory();
    if (type.size() == 0) {
      int i = 1;
      for (String str : getResources().getStringArray(R.array.account_type)) {
        category = new Category();
        category.setId(i);
        category.setName(str);
        type.add(category);
        i++;
      }
    }
    category = new Category();
    category.setId(0);
    category.setName(getString(R.string.txt_ChooseCategory));
    type.add(0, category);
    ArrayAdapter<Category> dataAdapter = new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_spinner_item, type);
    this.spType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.btn_import:
        if (isContactPermissionGranted()) {
          openContacts();
          return;
        } else {
          Toast.makeText(getApplicationContext(), R.string.permission_contact, Toast.LENGTH_LONG).show();
          return;
        }
      case R.id.ca:
        String fname = this.mEtName.getText().toString();
        String mobno = this.mEtMobile.getText().toString();
        String pass = this.mEtRemarks.getText().toString();
        String email = this.mEtEmail.getText().toString();
        if (TextUtils.isEmpty(fname)) {
          Toast.makeText(getApplicationContext(), getString(R.string.txt_Entername), Toast.LENGTH_SHORT).show();
          return;
        } else {
          addEntry(fname, email, mobno, pass);
          return;
        }
      case R.id.img_profile:
        alertBoxForUploadImageOp(false);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void onPositiveClick(int from) {
    showProAppLink();
    finish();
  }

  public void onNegativeClick(int from) {
    finish();
  }

  private void openContacts() {
    Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
    intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
    startActivityForResult(intent, 78);
  }

  public boolean isContactPermissionGranted() {
    if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_CONTACTS") == 0) {
      return true;
    }
    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS"}, 1);
    return false;
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults[0] == 0) {
      openContacts();
    }
  }

  private void addEntry(String fname, String email, String mono, String remark) {
    SessionManager.setRefreshAccountList(true);
    setResult(-1);
    int uId = SessionManager.getLoginUserId();
    Account account = new Account();
    account.setUserId(uId);
    account.setName(fname);
    account.setEmail(email);
    account.setMobile(mono);
    account.setRemark(remark);
    account.setImage(this.mNewEncodedImage);
    if (this.spType.getSelectedItemPosition() == 0) {
      account.setCategory("Individual");
      account.setCategoryId(1);
    } else {
      Category category = (Category) this.spType.getSelectedItem();
      account.setCategory(category.getName());
      account.setCategoryId(category.getId());
    }
    int status = new FetchData().insertAccountDetail(account);
    if (-1 == status) {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_AlearyExists), Toast.LENGTH_SHORT).show();
      return;
    }
    if (27 == status) {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_EntryNotAdd), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_EntryAdd), Toast.LENGTH_SHORT).show();
    }
    finish();
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

  protected void onActivityResult(int rqCode, int rsCode, Intent intent) {
    if (rqCode == 78 && rsCode == -1) {
      ContactBean dao = (ContactBean) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED);
      String accname = dao.getName();
      String number = dao.getPhoneNo();
      this.mEtName.setText(accname);
      this.mEtMobile.setText(number);
      this.mEtEmail.setText(dao.getEmail());
    }
    super.onActivityResult(rqCode, rsCode, intent);
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
    this.mAdView = null;
    super.onDestroy();
  }
}
