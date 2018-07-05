package com.adslinfotech.mobileaccounting.activities.entry;

import android.content.Intent;
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
import java.util.Date;

public class ActivityInterAccount extends PhotoPicker implements OnItemSelectedListener, OnClickListener {
    private Button btnAddInter;
    private EditText etAmount;
    private EditText etRemark;
    private AdView mAdView;
    private Date mDate;
    private EditText mEtDate;
    private SimpleDateFormat mFormat;
    private ImageView mImgReceipt;
    private byte[] mNewEncodedImage;
    private AccountSpAdapter mSpAccountAdapter;
    private Spinner mSpAccountCr;
    private Spinner mSpAccountDr;
    private SimpleDateFormat sdf;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_inter_account);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[4]);
        getViews();
        displayResultList();
        this.btnAddInter.setOnClickListener(this);
        this.mImgReceipt.setOnClickListener(this);
    }

    public void onClick(View v) {
        SessionManager.incrementInteractionCount();
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_add_inter_account:
                insertDetails();
                return;
            case R.id.img_recipet:
                alertBoxForUploadImageOp(false);
                return;
            case R.id.img_search_credit_acc:
                intent = new Intent(this, ActivitySearchAccount.class);
                intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
                startActivityForResult(intent, 124);
                return;
            case R.id.img_search_debit_acc:
                intent = new Intent(this, ActivitySearchAccount.class);
                intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
                startActivityForResult(intent, 124);
                return;
            default:
                super.onClick(v);
                return;
        }
    }

    public void insertDetails() {
        if (this.mSpAccountCr.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please Select Credit Account!!", Toast.LENGTH_SHORT).show();
        } else if (this.mSpAccountDr.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please Select Debit Account!!", Toast.LENGTH_SHORT).show();
        } else {
            String amount = this.etAmount.getText().toString();
            String remark = this.etRemark.getText().toString();
            String date = this.mEtDate.getText().toString();
            int createdby = SessionManager.getLoginUserId();
            if (TextUtils.isEmpty(amount)) {
                Toast.makeText(getApplicationContext(), getString(R.string.txt_EnterAmount), Toast.LENGTH_SHORT).show();
            } else if (date.equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.txt_EnterDate), Toast.LENGTH_SHORT).show();
            } else {
                SessionManager.setRefreshAccountList(true);
                setResult(-1);
                double amt = Double.parseDouble(amount);
                if (amt == 0.0d) {
                    Toast.makeText(getApplicationContext(), getString(R.string.txt_EnterAmount), Toast.LENGTH_SHORT).show();
                    return;
                }
                Account accountCr = FetchCursor.getAccount((Cursor) this.mSpAccountCr.getSelectedItem(), false);
                Account accountDr = FetchCursor.getAccount((Cursor) this.mSpAccountDr.getSelectedItem(), false);
                Transaction transaction = new Transaction();
                transaction.setAId(accountCr.getAccountId());
                transaction.setCraditAmount(amt);
                transaction.setDebitAmount(0.0d);
                transaction.setDr_cr(1);
                transaction.setRemark(remark);
                transaction.setNarration("Credited via " + accountDr.getName());
                transaction.setUserId(createdby);
                transaction.setDate(this.mFormat.format(this.mDate));
                transaction.setImage(this.mNewEncodedImage);
                new FetchData().insertTransactionDetail(transaction);
                transaction.setAId(accountDr.getAccountId());
                transaction.setNarration("Debited via " + accountCr.getName());
                transaction.setCraditAmount(0.0d);
                transaction.setDebitAmount(amt);
                transaction.setDr_cr(0);
                new FetchData().insertTransactionDetail(transaction);
                Toast.makeText(getApplicationContext(), getString(R.string.txt_EntryAdd), Toast.LENGTH_SHORT).show();
                setResult(-1);
                finish();
            }
        }
    }

    public void setImage(Bitmap photo) {
        if (photo != null) {
            photo = Bitmap.createScaledBitmap(photo, 1000, 1000, true);
            this.mImgReceipt.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(CompressFormat.JPEG, 90, baos);
            this.mNewEncodedImage = baos.toByteArray();
        }
    }

    private void getViews() {
        this.mImgReceipt = (ImageView) findViewById(R.id.img_recipet);
        this.mEtDate = (EditText) findViewById(R.id.edate);
        this.etAmount = (EditText) findViewById(R.id.eamount);
        this.etRemark = (EditText) findViewById(R.id.eremark);
        this.btnAddInter = (Button) findViewById(R.id.btn_add_inter_account);
        this.mSpAccountCr = (Spinner) findViewById(R.id.sp_credit_acc);
        this.mSpAccountCr.setOnItemSelectedListener(this);
        this.mSpAccountDr = (Spinner) findViewById(R.id.sp_debit_acc);
        this.mSpAccountDr.setOnItemSelectedListener(this);
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
        this.mSpAccountCr.setAdapter(this.mSpAccountAdapter);
        this.mSpAccountDr.setAdapter(this.mSpAccountAdapter);
        this.mSpAccountAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    }

    protected void onActivityResult(int rqCode, int rsCode, Intent intent) {
        if (rqCode == 124 && rsCode == -1) {
            this.mSpAccountCr.setSelection(this.mSpAccountAdapter.setSelection(((Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED)).getName()));
        } else if (rqCode == 324 && rsCode == -1) {
            this.mSpAccountDr.setSelection(this.mSpAccountAdapter.setSelection(((Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED)).getName()));
        }
        super.onActivityResult(rqCode, rsCode, intent);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        if (pos != 0) {
        }
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
