package com.adslinfotech.mobileaccounting.activities.entry;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
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
import java.util.ArrayList;
import java.util.Date;

public class ActivityAddCreditDebit extends PhotoPicker implements OnItemSelectedListener, OnClickListener {
  private Button ca;
  private EditText eremark;
  private AdView mAdView;
  private Date mDate;
  private EditText mEtAmt1;
  private EditText mEtAmt2;
  private EditText mEtAmt3;
  private EditText mEtAmt4;
  private EditText mEtDate;
  private EditText mEtNarration;
  private FetchData mFetchData;
  private SimpleDateFormat mFormat;
  private ImageView mImgAccount;
  private ImageView mImgRecipet;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private Spinner mSpAccount;
  private AccountSpAdapter mSpAccountAdapter;
  private SimpleDateFormat sdf;
  private Spinner sp1;
  private Spinner sp2;
  private Spinner sp3;
  private Spinner sp4;
  private TextView tvBalanceAmount;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_add_creditdebit);
    this.mFetchData = new FetchData();
    this.mResource = getResources();
    getSupportActionBar().setTitle(this.mResource.getStringArray(R.array.nav_menu_items)[3]);
    getViews();
    displayResultList();
    this.ca.setOnClickListener(this);
    this.mImgRecipet.setOnClickListener(this);
    setSelection();
  }

  private void setSelection() {
    try {
      this.mSpAccount.setSelection(this.mSpAccountAdapter.setSelection(getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED)));
    } catch (Exception e) {
    }
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.ca:
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
    String amt1 = this.mEtAmt1.getText().toString();
    String amt2 = this.mEtAmt2.getText().toString();
    String amt3 = this.mEtAmt3.getText().toString();
    String amt4 = this.mEtAmt4.getText().toString();
    String remark = this.eremark.getText().toString();
    String narration = this.mEtNarration.getText().toString();
    String date = this.mEtDate.getText().toString();
    int createdby = SessionManager.getLoginUserId();
    if (amt1.equals("")) {
      Toast.makeText(getApplicationContext(), R.string.validate_one_amount, Toast.LENGTH_SHORT).show();
    } else if (this.sp1.getSelectedItemPosition() == 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.validate_amount_type, new Object[]{"1"}), Toast.LENGTH_SHORT).show();
    } else if (!amt2.equals("") && this.sp2.getSelectedItemPosition() == 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.validate_amount_type, new Object[]{"2"}), Toast.LENGTH_SHORT).show();
    } else if (!amt3.equals("") && this.sp3.getSelectedItemPosition() == 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.validate_amount_type, new Object[]{"3"}), Toast.LENGTH_SHORT).show();
    } else if (!amt4.equals("") && this.sp4.getSelectedItemPosition() == 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.validate_amount_type, new Object[]{"4"}), Toast.LENGTH_SHORT).show();
    } else if (date.equals("")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterDate), Toast.LENGTH_SHORT).show();
    } else {
      double amount2;
      double amount3;
      double amount4;
      SessionManager.setRefreshAccountList(true);
      setResult(-1);
      double amount1 = Double.parseDouble(amt1);
      try {
        amount2 = Double.parseDouble(amt2);
      } catch (Exception e) {
        amount2 = 0.0d;
      }
      try {
        amount3 = Double.parseDouble(amt3);
      } catch (Exception e2) {
        amount3 = 0.0d;
      }
      try {
        amount4 = Double.parseDouble(amt4);
      } catch (Exception e3) {
        amount4 = 0.0d;
      }
      Account account = FetchCursor.getAccount((Cursor) this.mSpAccount.getSelectedItem(), false);
      Transaction transaction = new Transaction();
      transaction.setAId(account.getAccountId());
      transaction.setRemark(remark);
      transaction.setNarration(narration);
      transaction.setUserId(createdby);
      transaction.setDate(this.mFormat.format(this.mDate));
      transaction.setImage(this.mNewEncodedImage);
      if (this.sp1.getSelectedItemPosition() == 1) {
        transaction.setDr_cr(1);
        transaction.setCraditAmount(amount1);
        transaction.setDebitAmount(0.0d);
      } else {
        transaction.setDr_cr(0);
        transaction.setCraditAmount(0.0d);
        transaction.setDebitAmount(amount1);
      }
      this.mFetchData.insertTransactionDetail(transaction);
      if (amount2 != 0.0d) {
        if (this.sp2.getSelectedItemPosition() == 1) {
          transaction.setDr_cr(1);
          transaction.setCraditAmount(amount2);
          transaction.setDebitAmount(0.0d);
        } else {
          transaction.setDr_cr(0);
          transaction.setCraditAmount(0.0d);
          transaction.setDebitAmount(amount2);
        }
        this.mFetchData.insertTransactionDetail(transaction);
      }
      if (amount3 != 0.0d) {
        if (this.sp3.getSelectedItemPosition() == 1) {
          transaction.setDr_cr(1);
          transaction.setCraditAmount(amount3);
          transaction.setDebitAmount(0.0d);
        } else {
          transaction.setDr_cr(0);
          transaction.setCraditAmount(0.0d);
          transaction.setDebitAmount(amount3);
        }
        this.mFetchData.insertTransactionDetail(transaction);
      }
      if (amount4 != 0.0d) {
        if (this.sp4.getSelectedItemPosition() == 1) {
          transaction.setDr_cr(1);
          transaction.setCraditAmount(amount4);
          transaction.setDebitAmount(0.0d);
        } else {
          transaction.setDr_cr(0);
          transaction.setCraditAmount(0.0d);
          transaction.setDebitAmount(amount4);
        }
        this.mFetchData.insertTransactionDetail(transaction);
      }
      setResult(-1);
      Toast.makeText(getApplicationContext(), "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
      setResult(-1);
      setEmpty();
    }
  }

  private void setEmpty() {
    this.mEtAmt1.setText("");
    this.mEtAmt2.setText("");
    this.mEtAmt3.setText("");
    this.mEtAmt4.setText("");
    this.eremark.setText("");
    this.mEtNarration.setText("");
    this.sp1.setSelection(0);
    this.sp2.setSelection(0);
    this.sp3.setSelection(0);
    this.sp4.setSelection(0);
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

  public void setImage(Bitmap photo) {
    if (photo != null) {
      photo = Bitmap.createScaledBitmap(photo, 1000, 1000, true);
      this.mImgRecipet.setImageBitmap(photo);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      photo.compress(CompressFormat.JPEG, 90, baos);
      this.mNewEncodedImage = baos.toByteArray();
    }
  }

  private void getViews() {
    this.mImgAccount = (ImageView) findViewById(R.id.img_profile);
    this.mImgRecipet = (ImageView) findViewById(R.id.img_recipet);
    this.mEtDate = (EditText) findViewById(R.id.edate);
    this.mEtAmt1 = (EditText) findViewById(R.id.etAmount1);
    this.mEtAmt2 = (EditText) findViewById(R.id.etAmount2);
    this.mEtAmt3 = (EditText) findViewById(R.id.etAmount3);
    this.mEtAmt4 = (EditText) findViewById(R.id.etAmount4);
    this.sp1 = (Spinner) findViewById(R.id.sp1);
    this.sp2 = (Spinner) findViewById(R.id.sp2);
    this.sp3 = (Spinner) findViewById(R.id.sp3);
    this.sp4 = (Spinner) findViewById(R.id.sp4);
    this.eremark = (EditText) findViewById(R.id.eremark);
    this.ca = (Button) findViewById(R.id.ca);
    this.mEtNarration = (EditText) findViewById(R.id.enarration);
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
    ArrayList<String> data = new ArrayList();
    data.add("Select");
    data.add("Credit");
    data.add("Debit");
    ArrayAdapter<String> dataAdapter2 = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, data);
    this.sp1.setAdapter(dataAdapter2);
    this.sp2.setAdapter(dataAdapter2);
    this.sp3.setAdapter(dataAdapter2);
    this.sp4.setAdapter(dataAdapter2);
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
    }
    super.onActivityResult(rqCode, rsCode, intent);
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
