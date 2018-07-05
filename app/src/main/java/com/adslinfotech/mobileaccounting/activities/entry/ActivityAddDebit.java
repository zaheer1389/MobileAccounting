package com.adslinfotech.mobileaccounting.activities.entry;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.invoice.InvoiceActivity;
import com.adslinfotech.mobileaccounting.adapter.spinner.AccountSpAdapter;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchCursor;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.database.Query;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.image.PhotoPicker;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchAccount;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityAddDebit extends PhotoPicker implements OnItemSelectedListener, OnClickListener {
  private boolean Ismail = false;
  private Button ca;
  private Button cb;
  private EditText eamount;
  private EditText enarration;
  private EditText eremark;
  private AdView mAdView;
  private Date mDate;
  private EditText mEtDate;
  private SimpleDateFormat mFormat;
  private ImageView mImgAccount;
  private ImageView mImgRecipet;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private Spinner mSpAccount;
  private AccountSpAdapter mSpAccountAdapter;
  private SimpleDateFormat sdf;
  private TextView tvBalanceAmount;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_adddebit);
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    this.mResource = getResources();
    getSupportActionBar().setTitle(this.mResource.getStringArray(R.array.nav_menu_items)[2]);
    getViews();
    displayResultList();
    this.mImgRecipet.setOnClickListener(this);
    this.ca.setOnClickListener(this);
    this.cb.setOnClickListener(this);
    setSelection();
  }

  private void setSelection() {
    try {
      this.mSpAccount.setSelection(this.mSpAccountAdapter.setSelection(getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getViews() {
    this.mImgAccount = (ImageView) findViewById(R.id.img_profile);
    this.mImgRecipet = (ImageView) findViewById(R.id.img_recipet);
    this.mEtDate = (EditText) findViewById(R.id.edate);
    this.eamount = (EditText) findViewById(R.id.eamount);
    this.eremark = (EditText) findViewById(R.id.eremark);
    this.ca = (Button) findViewById(R.id.ca);
    this.cb = (Button) findViewById(R.id.cb);
    this.enarration = (EditText) findViewById(R.id.enarration);
    this.tvBalanceAmount = (TextView) findViewById(R.id.balanceamount);
    this.mSpAccount = (Spinner) findViewById(R.id.spinner);
    this.mSpAccount.setOnItemSelectedListener(this);
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    this.sdf = AppUtils.getDateFormat();
    this.mDate = new Date();
    this.mEtDate.setText(this.sdf.format(this.mDate));
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
        this.Ismail = false;
        insertDetails();
        return;
      case R.id.cb:
        this.Ismail = true;
        insertDetails();
        return;
      case R.id.img_recipet:
        alertBoxForUploadImageOp(false);
        return;
      case R.id.img_search:
        Intent intent = new Intent(this, ActivitySearchAccount.class);
        intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
        startActivityForResult(intent, 78);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void insertDetails() {
    if (this.mSpAccount.getSelectedItemPosition() == 0) {
      Toast.makeText(this, this.mResource.getString(R.string.txt_SelectAccount), Toast.LENGTH_SHORT).show();
      return;
    }
    String amount = this.eamount.getText().toString();
    String remark = this.eremark.getText().toString();
    String narration = this.enarration.getText().toString();
    String date = this.mEtDate.getText().toString();
    int createdby = SessionManager.getLoginUserId();
    if (TextUtils.isEmpty(amount)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterAmount), Toast.LENGTH_SHORT).show();
    } else if (date.equals("")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterDate), Toast.LENGTH_SHORT).show();
    } else {
      SessionManager.setRefreshAccountList(true);
      setResult(-1);
      double amt = Double.parseDouble(amount);
      if (amt == 0.0d) {
        Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterAmount), Toast.LENGTH_SHORT).show();
        return;
      }
      Account account = FetchCursor.getAccount((Cursor) this.mSpAccount.getSelectedItem(), false);
      Transaction transaction = new Transaction();
      transaction.setAId(account.getAccountId());
      transaction.setCraditAmount(0.0d);
      transaction.setDebitAmount(amt);
      transaction.setNarration(narration);
      transaction.setRemark(remark);
      transaction.setDr_cr(0);
      transaction.setDate(this.mFormat.format(this.mDate));
      transaction.setUserId(createdby);
      transaction.setImage(this.mNewEncodedImage);
      new FetchData().insertTransactionDetail(transaction);
      setResult(-1);
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_DebitAdded), Toast.LENGTH_SHORT).show();
      if (this.Ismail) {
        InvoiceActivity.newInstance(this, transaction, account);
      } else {
        finish();
      }
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

  public void selectDate(View view) {
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

  private void displayResultList() {
    this.mSpAccountAdapter = new AccountSpAdapter(this,  android.R.layout.simple_spinner_item, FetchCursor.getCursor(Query.ACCOUNT_LIST_HOME), new String[]{ACCOUNT.NAME}, new int[]{android.R.id.text1}, 0);
    this.mSpAccount.setAdapter(this.mSpAccountAdapter);
    this.mSpAccountAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  private void setImage(byte[] byteImage) {
    try {
      if (AppUtils.setImage(this.mImgAccount, byteImage)) {
        this.mImgAccount.setImageResource(R.drawable.profile_icon);
      }
    } catch (Exception e) {
    }
  }

  protected void onActivityResult(int rqCode, int rsCode, Intent intent) {
    if (rqCode == 78 && rsCode == -1) {
      this.mSpAccount.setSelection(this.mSpAccountAdapter.setSelection(((Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED)).getName()));
    } else if (rqCode == AppConstants.ACTIVITY_FINISH) {
      setEmpty();
    }
    super.onActivityResult(rqCode, rsCode, intent);
  }

  public void setEmpty() {
    this.eamount.setText("");
    this.enarration.setText("");
    this.eremark.setText("");
    this.mImgRecipet.setImageResource(R.drawable.add_receipt);
    this.mImgAccount.setImageResource(R.drawable.profile_icon);
    try {
      int pos = this.mSpAccount.getSelectedItemPosition();
      this.mSpAccountAdapter = new AccountSpAdapter(this,  android.R.layout.simple_spinner_item, FetchCursor.getCursor(Query.ACCOUNT_LIST_HOME), new String[]{ACCOUNT.NAME}, new int[]{android.R.id.text1}, 0);
      this.mSpAccount.setAdapter(this.mSpAccountAdapter);
      this.mSpAccountAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
      this.mSpAccountAdapter.notifyDataSetChanged();
      this.mSpAccount.setSelection(pos);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    if (pos == 0) {
      this.tvBalanceAmount.setText("");
      this.mImgAccount.setImageResource(R.drawable.profile_icon);
      return;
    }
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    Account account = FetchCursor.getAccount((Cursor) parent.getItemAtPosition(pos), true);
    this.tvBalanceAmount.setText(mRsSymbol + account.getBalance());
    setImage(account.getImage());
  }

  public void onNothingSelected(AdapterView<?> adapterView) {
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
