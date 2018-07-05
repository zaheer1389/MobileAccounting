package com.adslinfotech.mobileaccounting.activities.edit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.image.FullScreenImage;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchAccount;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ActivityEditAccount extends ActivityEdit implements OnItemSelectedListener, OnClickListener {
  private static final int DIALOG_DELETE = 621;
  private static final int DIALOG_UPDATE = 125;
  private ArrayList<Account> mAccounts = new ArrayList();
  private AdView mAdView;
  private Button mBtCancel;
  private Button mBtDelete;
  private Button mBtSave;
  private ArrayList<String> mCategoryNames = new ArrayList();
  private EditText mEtEmail;
  private EditText mEtMobile;
  private EditText mEtName;
  private EditText mEtRemark;
  private FetchData mFetchData;
  private ImageView mImgProfile;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private int mSelectScreen;
  private Spinner mSpAccount;
  private Spinner spType;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mResource = getResources();
    getSupportActionBar().setTitle(this.mResource.getString(R.string.title_activity_edit_account));
    this.mFetchData = new FetchData();
    this.mSelectScreen = getIntent().getExtras().getInt("SCREEN");
    if (openAndQueryDatabase()) {
      setContentView((int) R.layout.activity_edit_account);
      getViews();
      setAdapterOnSpinner();
      hideKeyPad();
      if (this.mSelectScreen == 1) {
        setSelection();
      }
    }
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void setSelection() {
    try {
      this.mSpAccount.setSelection(getAccountIndex(getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getViews() {
    this.mEtName = (EditText) findViewById(R.id.et_name);
    this.mEtEmail = (EditText) findViewById(R.id.et_email);
    this.mEtMobile = (EditText) findViewById(R.id.et_mobile);
    this.mEtRemark = (EditText) findViewById(R.id.et_remark);
    this.mImgProfile = (ImageView) findViewById(R.id.img_profile);
    this.spType = (Spinner) findViewById(R.id.sp_type);
    this.mSpAccount = (Spinner) findViewById(R.id.sp_select_account);
    this.mBtSave = (Button) findViewById(R.id.btn_save);
    this.mBtDelete = (Button) findViewById(R.id.btn_delete);
    this.mBtCancel = (Button) findViewById(R.id.btn_cancel);
    this.mSpAccount.setOnItemSelectedListener(this);
    this.mBtSave.setOnClickListener(this);
    this.mBtDelete.setOnClickListener(this);
    this.mBtCancel.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void setSpAdapter() {
    Category category;
    ArrayList<Category> mCategories = this.mFetchData.getAllCategory();
    if (mCategories.size() == 0) {
      int i = 1;
      for (String str : getResources().getStringArray(R.array.account_type)) {
        category = new Category();
        category.setId(i);
        category.setName(str);
        mCategories.add(category);
        this.mCategoryNames.add(str);
        i++;
      }
    } else {
      Iterator it = mCategories.iterator();
      while (it.hasNext()) {
        this.mCategoryNames.add(((Category) it.next()).getName());
      }
    }
    category = new Category();
    category.setId(0);
    category.setName(getString(R.string.txt_ChooseCategory));
    mCategories.add(0, category);
    ArrayAdapter<Category> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, mCategories);
    this.spType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  private void setAdapterOnSpinner() {
    ArrayAdapter<Account> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, this.mAccounts);
    this.mSpAccount.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  private boolean openAndQueryDatabase() {
    this.mAccounts = this.mFetchData.getAllAccounts(true, false);
    if (this.mAccounts.size() == 0) {
      showPositiveAlert(this.mResource.getString(R.string.txt_error), this.mResource.getString(R.string.NO_Account_Error));
      return false;
    }
    Account first = new Account();
    first.setAccountId(0);
    first.setName(getResources().getString(R.string.spinner_title));
    this.mAccounts.add(0, first);
    return true;
  }

  public void onItemSelected(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
    this.mImgProfile.setImageResource(R.drawable.add_profile_pic);
    this.mNewEncodedImage = null;
    if (pos == 0) {
      this.mNewEncodedImage = null;
      this.mEtName.setText("");
      this.mEtEmail.setText("");
      this.mEtMobile.setText("");
      this.mEtRemark.setText("");
      return;
    }
    Account account = (Account) this.mSpAccount.getSelectedItem();
    setSpAdapter();
    this.mEtName.setText(account.getName());
    this.mEtEmail.setText(account.getEmail());
    this.mEtMobile.setText(account.getMobile());
    this.mEtRemark.setText(account.getRemark());
    try {
      this.spType.setSelection(this.mCategoryNames.indexOf(account.getCategory()) + 1);
    } catch (Exception e) {
    }
    this.mNewEncodedImage = account.getImage();
    setImage(this.mNewEncodedImage);
  }

  private void setImage(byte[] byteImage) {
    try {
      if (AppUtils.setImage(this.mImgProfile, byteImage)) {
        this.mImgProfile.setImageResource(R.drawable.profile_icon);
      }
    } catch (Exception e) {
    }
  }

  public void onNothingSelected(AdapterView<?> adapterView) {
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    int pos = this.mSpAccount.getSelectedItemPosition();
    switch (v.getId()) {
      case R.id.btn_cancel:
        finish();
        return;
      case R.id.btn_delete:
        if (pos == 0) {
          Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Select_Account_First), Toast.LENGTH_SHORT).show();
          return;
        } else {
          checkPasswordRequired(621);
          return;
        }
      case R.id.btn_ok_alert_positive:
        super.onClick(v);
        finish();
        return;
      case R.id.btn_save:
        if (pos == 0) {
          Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Select_Account_First), Toast.LENGTH_SHORT).show();
          return;
        } else {
          checkPasswordRequired(DIALOG_UPDATE);
          return;
        }
      case R.id.img_profile:
        alertBoxForUploadImageOp(true);
        return;
      case R.id.img_search:
        Intent intent = new Intent(this, ActivitySearchAccount.class);
        intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
        startActivityForResult(intent, 78);
        return;
      case R.id.txt_remove_image:
        this.mNewEncodedImage = null;
        this.mImgProfile.setImageResource(R.drawable.add_profile_pic);
        this.dialogUploadPhoto.dismiss();
        return;
      case R.id.txt_zoom_image:
        Intent intent1 = new Intent(this, FullScreenImage.class);
        intent1.putExtra("image", this.mNewEncodedImage);
        startActivity(intent1);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void onPositiveClick(int from) {
    super.onPositiveClick(from);
  }

  private void showAlertSettel() {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setMessage(this.mResource.getString(R.string.txt_Settle_Msg)).setCancelable(false).setPositiveButton(this.mResource.getString(R.string.txt_Yes), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        ActivityEditAccount.this.deleteAccount();
      }
    }).setNegativeButton(this.mResource.getString(R.string.txt_No), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    dialog.show();
  }

  private void deleteAccount() {
    SessionManager.setRefreshAccountList(true);
    int aId = ((Account) this.mSpAccount.getSelectedItem()).getAccountId();
    int deleteStatus = this.mFetchData.deleteAccount(aId);
    this.mFetchData.settleAccount(aId);
    setResult(1);
    if (deleteStatus != 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Account_Delete), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Account_NotDelete), Toast.LENGTH_SHORT).show();
    }
    finish();
  }

  private void updateAccount() {
    SessionManager.setRefreshAccountList(true);
    Account account = (Account) this.mSpAccount.getSelectedItem();
    account.setName(this.mEtName.getText().toString().trim());
    account.setEmail(this.mEtEmail.getText().toString().trim());
    account.setMobile(this.mEtMobile.getText().toString().trim());
    account.setRemark(this.mEtRemark.getText().toString().trim());
    account.setImage(this.mNewEncodedImage);
    Category category = (Category) this.spType.getSelectedItem();
    account.setCategoryId(category.getId());
    account.setCategory(category.getName());
    int updateStatus = this.mFetchData.updateAccount(account);
    Intent intent = new Intent();
    intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mFetchData.getAccount(account.getName()));
    setResult(-1, intent);
    if (updateStatus != 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Account_Update), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Account_NotUpdate), Toast.LENGTH_SHORT).show();
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

  public void passwordConfirmed(int i) {
    if (i == DIALOG_UPDATE) {
      updateAccount();
    } else {
      showAlertSettel();
    }
  }

  private int getAccountIndex(String name) {
    int i = 0;
    Iterator it = this.mAccounts.iterator();
    while (it.hasNext() && !((Account) it.next()).getName().equals(name)) {
      i++;
    }
    return i;
  }

  protected void onActivityResult(int rqCode, int rsCode, Intent intent) {
    if (rqCode == 78 && rsCode == -1) {
      this.mSpAccount.setSelection(getAccountIndex(((Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED)).getName()));
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
    super.onDestroy();
  }
}
