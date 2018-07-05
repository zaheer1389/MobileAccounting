package com.adslinfotech.mobileaccounting.activities.edit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.image.FullScreenImage;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchAccount;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ActivityEditTransaction extends ActivityEdit implements OnClickListener {
  private static final int DIALOG_DELETE = 765;
  private static final int DIALOG_UPDATE = 452;
  private List<Account> mAccounts = new ArrayList();
  private AdView mAdView;
  private Date mDate;
  private EditText mEtAmount;
  private EditText mEtDate;
  private EditText mEtNarration;
  private EditText mEtRemark;
  private FetchData mFetchData;
  private SimpleDateFormat mFormat;
  private ImageView mImgRecipet;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private Spinner mSpAccounts;
  private ArrayAdapter<Account> mSpAdapter;
  private Spinner mSpType;
  private Transaction mTransaction;
  private SimpleDateFormat sdf;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_edittransaction);
    this.mResource = getResources();
    this.mFetchData = new FetchData();
    getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_edit_transaction));
    getViews();
    openAndQueryDatabase();
    setData();
    hideKeyPad();
  }

  private void getViews() {
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    this.sdf = AppUtils.getDateFormat();
    this.mSpType = (Spinner) findViewById(R.id.sp_tran_type);
    this.mEtAmount = (EditText) findViewById(R.id.et_tran_amount);
    this.mImgRecipet = (ImageView) findViewById(R.id.img_recipet);
    this.mEtDate = (EditText) findViewById(R.id.et_tran_date);
    this.mEtRemark = (EditText) findViewById(R.id.et_tran_remark);
    this.mEtNarration = (EditText) findViewById(R.id.et_tran_narration);
    this.mSpAccounts = (Spinner) findViewById(R.id.spinner);
    Button btDelete = (Button) findViewById(R.id.btn_delete);
    Button btCancel = (Button) findViewById(R.id.btn_cancel);
    ((Button) findViewById(R.id.btn_save)).setOnClickListener(this);
    btDelete.setOnClickListener(this);
    btCancel.setOnClickListener(this);
    this.mImgRecipet.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  public void hideKeyPad() {
    getWindow().setSoftInputMode(3);
  }

  private void setData() {
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    this.mTransaction = (Transaction) getIntent().getSerializableExtra(AppConstants.EDIT_TRANSACTION);
    this.mSpAccounts.setSelection(getAccountIndex(this.mTransaction.getAccName()));
    if (1 == this.mTransaction.getDr_cr()) {
      this.mEtAmount.setText("" + this.mTransaction.getCraditAmount());
      this.mSpType.setSelection(1);
    } else {
      this.mEtAmount.setText("" + this.mTransaction.getDebitAmount());
      this.mSpType.setSelection(2);
    }
    try {
      this.mDate = this.sdf.parse(this.mTransaction.getDate());
    } catch (Exception e) {
      this.mDate = new Date();
      e.printStackTrace();
    }
    this.mEtDate.setText(this.sdf.format(this.mDate));
    this.mEtNarration.setText(this.mTransaction.getNarration());
    this.mEtRemark.setText(this.mTransaction.getRemark());
    this.mNewEncodedImage = this.mTransaction.getImage();
    setImage(this.mNewEncodedImage);
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
        if (this.mSpAccounts.getSelectedItemPosition() == 0) {
          Toast.makeText(this, this.mResource.getString(R.string.txt_SelectAccount), Toast.LENGTH_SHORT).show();
        }
        if (this.mSpType.getSelectedItemPosition() == 0) {
          Toast.makeText(getApplicationContext(), "Please Select Amount Type", Toast.LENGTH_SHORT).show();
          return;
        } else {
          checkPasswordRequired(452);
          return;
        }
      case R.id.img_recipet:
        alertBoxForUploadImageOp(true);
        return;
      case R.id.img_search:
        Intent intent = new Intent(this, ActivitySearchAccount.class);
        intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
        startActivityForResult(intent, 78);
        return;
      case R.id.txt_remove_image:
        this.mNewEncodedImage = null;
        this.mImgRecipet.setImageResource(R.drawable.add_receipt);
        this.dialogUploadPhoto.dismiss();
        return;
      case R.id.txt_zoom_image:
        Intent intent1 = new Intent(this, FullScreenImage.class);
        intent1.putExtra("image", this.mTransaction.getImage());
        startActivity(intent1);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void passwordConfirmed(int i) {
    if (i == 452) {
      validateFields();
    } else {
      deleteTransaction();
    }
  }

  private void displayResultList() {
    this.mSpAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, this.mAccounts);
    this.mSpAccounts.setAdapter(this.mSpAdapter);
    this.mSpAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    ArrayList<String> data = new ArrayList();
    data.add("Select");
    data.add("Credit");
    data.add("Debit");
    this.mSpType.setAdapter(new ArrayAdapter(this,  android.R.layout.simple_spinner_item, data));
  }

  private void openAndQueryDatabase() {
    this.mAccounts = this.mFetchData.getAllAccounts(false, false);
    Account account = new Account();
    account.setAccountId(0);
    account.setName(this.mResource.getString(R.string.spinner_title));
    this.mAccounts.add(0, account);
    displayResultList();
  }

  private void deleteTransaction() {
    SessionManager.setRefreshAccountList(true);
    setResult(-1);
    if (this.mFetchData.deleteTransaction(this.mTransaction.getTransactionId()) != 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Trans_Delete), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Trans_NotDelete), Toast.LENGTH_SHORT).show();
    }
    finish();
  }

  private void updateTransaction() {
    SessionManager.setRefreshAccountList(true);
    setResult(-1);
    Transaction transaction = new Transaction();
    transaction.setTransactionId(this.mTransaction.getTransactionId());
    switch (this.mSpType.getSelectedItemPosition()) {
      case 1:
        transaction.setCraditAmount(Double.parseDouble(this.mEtAmount.getText().toString().trim()));
        transaction.setDr_cr(1);
        break;
      case 2:
        transaction.setDebitAmount(Double.parseDouble(this.mEtAmount.getText().toString().trim()));
        transaction.setDr_cr(0);
        break;
    }
    transaction.setDate(this.mFormat.format(this.mDate));
    transaction.setRemark(this.mEtRemark.getText().toString().trim());
    transaction.setNarration(this.mEtNarration.getText().toString().trim());
    transaction.setAId(((Account) this.mSpAccounts.getSelectedItem()).getAccountId());
    transaction.setImage(this.mNewEncodedImage);
    if (this.mFetchData.updateTransaction(transaction) != 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Trans_Update), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Trans_NotUpdate), Toast.LENGTH_SHORT).show();
    }
    finish();
  }

  private void setImage(byte[] byteImage) {
    try {
      if (AppUtils.setImage(this.mImgRecipet, byteImage)) {
        this.mImgRecipet.setImageResource(R.drawable.profile_icon);
      }
    } catch (Exception e) {
    }
  }

  public void setImage(Bitmap photo) {
    if (photo != null) {
      photo = Bitmap.createScaledBitmap(photo, 1000, 1000, true);
      this.mImgRecipet.setImageBitmap(photo);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      photo.compress(CompressFormat.JPEG, 90, baos);
      this.mNewEncodedImage = baos.toByteArray();
    }
  }

  private void validateFields() {
    String amount = this.mEtAmount.getText().toString().trim();
    String date = this.mEtDate.getText().toString().trim();
    if (TextUtils.isEmpty(amount)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterAmount), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(date)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterDate), Toast.LENGTH_SHORT).show();
    } else {
      updateTransaction();
    }
  }

  public void openDatePicker(View view) {
    showDate(this.mDate);
  }

  public void populateSetDate(int id, int year, int month, int day) {
    String strDate = year + "-" + month + "-" + day;
    this.mDate = new Date();
    try {
      this.mDate = this.mFormat.parse(strDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.mEtDate.setText(this.sdf.format(this.mDate));
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
      this.mSpAccounts.setSelection(getAccountIndex(((Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED)).getName()));
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
