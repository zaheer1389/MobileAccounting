package com.adslinfotech.mobileaccounting.activities.detail;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.base.ActivityMultipleSelection;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditAccount;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditTransaction;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCredit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCreditDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddDebit;
import com.adslinfotech.mobileaccounting.adapter.detail.AccountDetailAdapter;
import com.adslinfotech.mobileaccounting.adapter.detail.AccountDetailAdapter.OnTransactionListListener;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.calculator.Calculator;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.desktop.WebServerActivity;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.image.FullScreenImage;
import com.adslinfotech.mobileaccounting.tabullar.FixedGridLayoutManager;
import com.adslinfotech.mobileaccounting.tabullar.InsetDecoration;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressLint({"NewApi"})
public class AccountDetailActivity extends ActivityMultipleSelection implements OnTransactionListListener, OnQueryTextListener {
  private static int REQUEST_EDIT_ACCOUNT = 5;
  private static final int SETTLE_ACCOUNT = 456;
  private static final int SETTLE_ACCOUNT_BALANCE = 423;
  private boolean isListTablular;
  private Account mAccount;
  private AdView mAdView;
  private AccountDetailAdapter mAdapter;
  private Balance mBalance;
  private RecyclerView mListTransactions;
  private List<Transaction> mTransactions;
  private ViewStub mViewStub;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (SimpleAccountingApp.getPreference().getBoolean(ActivityPreferences.PREF_LIST_TYPE, true)) {
      setContentView((int) R.layout.activity_account_ledger__);
      this.mViewStub = (ViewStub) findViewById(R.id.view_stub);
      this.mViewStub.setLayoutResource(R.layout.item_header_account_detail);
      this.mViewStub.inflate();
      this.isListTablular = true;
      findViewById(R.id.lout_account_detail).setOnClickListener(this);
    } else {
      setContentView((int) R.layout.activity_account_ledger);
    }
    getViews();
    loadData();
    setListAdapter();
    setAdapter();
    initAds();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void initAds() {
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void setAdapter() {
    if (this.isListTablular) {
      this.mListTransactions.setLayoutManager(FixedGridLayoutManager.newInstance());
      this.mListTransactions.addItemDecoration(new InsetDecoration(this));
      this.mListTransactions.getItemAnimator().setAddDuration(1000);
      this.mListTransactions.getItemAnimator().setChangeDuration(1000);
      this.mListTransactions.getItemAnimator().setMoveDuration(1000);
      this.mListTransactions.getItemAnimator().setRemoveDuration(1000);
      return;
    }
    this.mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    this.mListTransactions.setItemAnimator(new DefaultItemAnimator());
    this.mListTransactions.setHasFixedSize(true);
  }

  private void setListAdapter() {
    this.mAdapter = new AccountDetailAdapter(this, this.mAccount, this.mBalance, this.mTransactions, this.isListTablular);
    this.mListTransactions.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
  }

  private void loadData() {
    FetchData fetchData = new FetchData();
    this.mAccount = fetchData.getAccount(getIntent().getStringExtra(EXTRA.SELECTED_ACCOUNT_NAME));
    ArrayList list = fetchData.getAllTransactions(this, null, this.mAccount.getAccountId(), true);
    this.mBalance = (Balance) list.get(1);
    this.mTransactions = (ArrayList) list.get(0);
    setListAdapter();
    if (this.isListTablular) {
      setText();
    }
  }

  private void setText() {
    TextView mTvDebit = (TextView) findViewById(R.id.txt_total_debit);
    TextView mTvBalance = (TextView) findViewById(R.id.txt_total_balance);
    ((TextView) findViewById(R.id.txt_total_credit)).setText(this.mBalance.getCredit());
    mTvDebit.setText(this.mBalance.getDebit());
    mTvBalance.setText(this.mBalance.getBalance());
    setAccountDetail();
  }

  private void setAccountDetail() {
    TextView txtName = (TextView) findViewById(R.id.tv_account_name);
    TextView txtEmail = (TextView) findViewById(R.id.tv_account_email);
    TextView txtNumber = (TextView) findViewById(R.id.tv_account_number);
    TextView txtCategory = (TextView) findViewById(R.id.tv_account_category);
    ImageView mImgAccount = (ImageView) findViewById(R.id.img_account);
    mImgAccount.setOnClickListener(this);
    try {
      if (AppUtils.setImage(mImgAccount, this.mAccount.getImage())) {
        mImgAccount.setImageResource(R.drawable.profile_icon);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    txtName.setText(this.mAccount.getName());
    txtEmail.setText(this.mAccount.getEmail());
    txtNumber.setText(this.mAccount.getMobile());
    txtCategory.setText(this.mAccount.getCategory());
  }

  private void getViews() {
    this.mListTransactions = (RecyclerView) findViewById(R.id.list);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    switch (item.getItemId()) {
      case R.id.menu_balshare:
        new ShareHelper(this, "Your Transaction with MR. " + SessionManager.getName(), "Your Transaction with MR. " + SessionManager.getName() + "\n Total Credit =" + this.mBalance.getCredit() + "\n Total Debit =" + this.mBalance.getDebit() + "\n Total Balance =" + this.mBalance.getBalance() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "\n Total Credit =" + this.mBalance.getCredit() + "\n Total Debit =" + this.mBalance.getDebit() + "\n Total Balance =" + this.mBalance.getBalance() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "", "").share();
        break;
      case R.id.menu_browse:
        Intent intent = new Intent(getApplicationContext(), WebServerActivity.class);
        intent.putExtra("TYPE", 1);
        intent.putExtra(AppConstants.ACCOUNT_DAO, this.mAccount);
        startActivity(intent);
        break;
      case R.id.menu_detailshare:
        NumberFormat newFormat = AppUtils.getCurrencyFormatter();
        StringBuilder sb = new StringBuilder();
        for (Transaction dao : this.mTransactions) {
          if (dao.getCraditAmount() == 0.0d) {
            sb.append(dao.getDate() + "  " + mRsSymbol + newFormat.format(dao.getDebitAmount()) + " Db\n");
          } else if (dao.getDebitAmount() == 0.0d) {
            sb.append(dao.getDate() + "  " + mRsSymbol + newFormat.format(dao.getCraditAmount()) + " Cr\n");
          }
        }
        new ShareHelper(this, "Your Transaction with MR. " + SessionManager.getName(), "Your Transaction with MR. " + SessionManager.getName() + "\nAccount: " + this.mAccount.getName() + " (" + this.mBalance.getBalance() + ")\n" + sb.toString() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "\nAccount: " + this.mAccount.getName() + " (" + this.mBalance.getBalance() + ")\n" + sb.toString() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "", "").share();
        break;
      case R.id.menu_export_exel:
        export(1);
        break;
      case R.id.menu_export_pdf:
        export(0);
        break;
      case R.id.menu_usecal:
        try {
          Intent i = new Intent();
          i.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
          startActivity(i);
          break;
        } catch (Exception e) {
          startActivity(new Intent(getApplicationContext(), Calculator.class));
          break;
        }
      default:
        return super.onOptionsItemSelected(item);
    }
    return false;
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    Intent intent;
    switch (v.getId()) {
      case R.id.btn_call:
        String num = this.mAccount.getMobile().trim();
        if (num == null || num.equalsIgnoreCase("")) {
          Toast.makeText(getApplicationContext(), "Mobile number not exists of this account.", Toast.LENGTH_SHORT).show();
          return;
        }
        intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + Uri.encode(num)));
        intent.setFlags(268435456);
        startActivity(intent);
        return;
      case R.id.btn_email:
        sendEmail();
        return;
      case R.id.btn_settle_bls:
        showAlertExitApp(getString(R.string.confirm_settle_balance), 423);
        return;
      case R.id.btn_settle_zero:
        showAlertExitApp(getString(R.string.confirm_settle_zero), SETTLE_ACCOUNT);
        return;
      case R.id.btn_sms:
        if (isStoragePermissionGranted()) {
          sendSms();
          return;
        } else {
          Toast.makeText(this, R.string.permission_storage, Toast.LENGTH_LONG).show();
          return;
        }
      case R.id.img_account:
        Intent intent1 = new Intent(this, FullScreenImage.class);
        intent1.putExtra("image", this.mAccount.getImage());
        startActivity(intent1);
        return;
      case R.id.lout_account_detail:
      case R.id.view_stub:
        intent = new Intent(this, ActivityEditAccount.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mAccount.getName());
        intent.putExtra("SCREEN", 1);
        startActivityForResult(intent, REQUEST_EDIT_ACCOUNT);
        return;
      case R.id.tv_add_credit:
        intent = new Intent(this, ActivityAddCredit.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mAccount.getName());
        startActivityForResult(intent, 78);
        return;
      case R.id.tv_add_debit:
        intent = new Intent(this, ActivityAddDebit.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mAccount.getName());
        startActivityForResult(intent, 78);
        return;
      case R.id.tv_add_multiple:
        intent = new Intent(this, ActivityAddCreditDebit.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, this.mAccount.getName());
        startActivityForResult(intent, 78);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void sendEmail() {
    String email = this.mAccount.getEmail().trim();
    if (email == null || email.equalsIgnoreCase("")) {
      Toast.makeText(getApplicationContext(), "Email Id not exists of this account.", Toast.LENGTH_SHORT).show();
      return;
    }
    try {
      Intent emailIntent = new Intent("android.intent.action.SEND");
      emailIntent.setFlags(268435456);
      emailIntent.setType("plain/text");
      emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
      emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{email});
      emailIntent.putExtra("android.intent.extra.SUBJECT", "");
      emailIntent.putExtra("android.intent.extra.TEXT", "");
      startActivity(emailIntent);
    } catch (ActivityNotFoundException e) {
      Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
    }
  }

  private void sendSms() {
    String mobile = this.mAccount.getMobile().trim();
    if (mobile == null || mobile.equalsIgnoreCase("")) {
      Toast.makeText(getApplicationContext(), "Mobile Number not exists of this account.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent smsVIntent = new Intent("android.intent.action.VIEW");
    smsVIntent.setType("vnd.android-dir/mms-sms");
    smsVIntent.putExtra("address", mobile);
    smsVIntent.putExtra("sms_body", " \nVia:Simple Accounting Android App \ndownload this app \nhttp://bit.ly/1LGUjOE");
    try {
      startActivity(smsVIntent);
    } catch (Exception ex) {
      SmsManager.getDefault().sendTextMessage("PhoneNumber-example:" + mobile, null, " \nVia:Simple Accounting Android App \ndownload this app \nhttp://bit.ly/1LGUjOE", null, null);
      Toast.makeText(this, "Your sms has failed...", Toast.LENGTH_LONG).show();
      ex.printStackTrace();
    }
  }

  public boolean isStoragePermissionGranted() {
    if (ContextCompat.checkSelfPermission(this, "android.permission.SEND_SMS") == 0) {
      return true;
    }
    ActivityCompat.requestPermissions(this, new String[]{"android.permission.SEND_SMS"}, 1);
    return false;
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults[0] == 0) {
      sendSms();
    }
  }

  private void settleAccount(int index) {
    SessionManager.setRefreshAccountList(true);
    setResult(-1);
    FetchData mFetchData = new FetchData();
    int aId = this.mAccount.getAccountId();
    if (index == 423) {
      Balance balance = mFetchData.getAccountBalance(this.mAccount.getAccountId(), null, null);
      mFetchData.settleAccount(aId);
      double credit = Double.parseDouble(balance.getCredit());
      double debit = Double.parseDouble(balance.getDebit());
      if (credit != debit) {
        Date today = new Date();
        Transaction transaction = new Transaction();
        transaction.setAId(aId);
        if (credit > debit) {
          transaction.setCraditAmount(credit - debit);
          transaction.setDebitAmount(0.0d);
          transaction.setDr_cr(1);
        } else {
          transaction.setDebitAmount(debit - credit);
          transaction.setCraditAmount(0.0d);
          transaction.setDr_cr(0);
        }
        transaction.setNarration("As on " + new SimpleDateFormat("dd MMM yyyy").format(today));
        transaction.setUserId(SessionManager.getLoginUserId());
        transaction.setDate(new SimpleDateFormat(DateFormat.DB_DATE).format(today));
        transaction.setRemark("");
        mFetchData.insertTransactionDetail(transaction);
      } else if (credit != 0.0d) {
        Toast.makeText(this, getResources().getString(R.string.err_settle_equal_amount), Toast.LENGTH_SHORT).show();
        return;
      } else {
        return;
      }
    }
    mFetchData.settleAccount(aId);
    Toast.makeText(this, getString(R.string.txt_Account_Settled), Toast.LENGTH_SHORT).show();
    loadData();
  }

  protected void setImage(Bitmap thumbnail) {
  }

  private void export(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Date));
    mColumns.add(getResources().getString(R.string.txt_Credit));
    mColumns.add(getResources().getString(R.string.txt_Debit));
    mColumns.add(getResources().getString(R.string.txt_balance_amount));
    mColumns.add(getResources().getString(R.string.txt_Narration));
    PdfDao header = new PdfDao();
    header.setFirst(this.mAccount.getName());
    header.setSecond(this.mBalance.getCredit());
    header.setThird(this.mBalance.getDebit());
    header.setFour(this.mBalance.getBalance());
    header.setFive(this.mAccount.getMobile());
    header.setSix(this.mAccount.getEmail());
    List<Transaction> trans = this.mTransactions;
    ArrayList<PdfDao> mValues = new ArrayList();
    if (!SessionManager.getListOrder()) {
      Collections.reverse(trans);
    }
    for (Transaction dao : trans) {
      PdfDao pdf = new PdfDao();
      pdf.setFirst(dao.getDate());
      pdf.setSecond("" + dao.getCraditAmount());
      pdf.setThird("" + dao.getDebitAmount());
      pdf.setFour(dao.getBalance());
      pdf.setFive(dao.getNarration());
      mValues.add(pdf);
    }
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 1).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 1).excel(this);
        return;
      default:
        return;
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_account_ledger, menu);
    menu.removeItem(R.id.menu_date);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(this);
    menuItem.setOnActionExpandListener(new OnActionExpandListener() {
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      public boolean onMenuItemActionCollapse(MenuItem item) {
        AccountDetailActivity.this.searchComplete();
        return true;
      }
    });
    return true;
  }

  public boolean onQueryTextChange(String newText) {
    if (this.mViewStub != null) {
      this.mViewStub.setVisibility(View.GONE);
    }
    this.mAdapter.setFilter(filter(this.mTransactions, newText), true);
    return false;
  }

  private List<Transaction> filter(List<Transaction> models, String newText) {
    newText = newText.toLowerCase();
    List<Transaction> filteredModelList = new ArrayList();
    for (Transaction model : models) {
      if (model.toString().toLowerCase().contains(newText)) {
        filteredModelList.add(model);
      }
    }
    return filteredModelList;
  }

  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  private void searchComplete() {
    this.mAdapter.setFilter(this.mTransactions, false);
    if (this.mViewStub != null) {
      this.mViewStub.setVisibility(View.VISIBLE);
    }
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 78 && resultCode == -1) {
      loadData();
    } else if (requestCode != REQUEST_EDIT_ACCOUNT) {
    } else {
      if (resultCode == -1) {
        this.mAccount = (Account) data.getSerializableExtra(AppConstants.ACCOUNT_SELECTED);
        if (this.isListTablular) {
          setAccountDetail();
          return;
        }
        this.mAdapter = new AccountDetailAdapter(this, this.mAccount, this.mBalance, this.mTransactions, this.isListTablular);
        this.mListTransactions.setAdapter(this.mAdapter);
        this.mAdapter.notifyDataSetChanged();
        setResult(REQUEST_EDIT_ACCOUNT);
      } else if (resultCode == 1) {
        finish();
      }
    }
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

  public void onItemClicked(Transaction transaction) {
    if (this.isMultiSelect) {
      multi_select(transaction);
      return;
    }
    Intent intent = new Intent(this, ActivityEditTransaction.class);
    intent.putExtra(AppConstants.EDIT_TRANSACTION, transaction);
    startActivityForResult(intent, 78);
  }

  public void onItemLongClicked(Transaction transaction) {
    if (!this.isMultiSelect) {
      this.multiselect_list = new ArrayList();
      this.isMultiSelect = true;
      if (this.mActionMode == null) {
        this.mActionMode = startActionMode(this.mActionModeCallback);
      }
    }
    multi_select(transaction);
  }

  protected void onSelectAllClick() {
    this.multiselect_list.clear();
    this.multiselect_list.addAll(this.mTransactions);
    refreshAdapterData();
    setActionTitle();
  }

  public void refreshAdapterData() {
    this.mAdapter.selected_usersList = this.multiselect_list;
    this.mAdapter.mTransactions = this.mTransactions;
    this.mAdapter.notifyDataSetChanged();
  }

  public void passwordConfirmed(int index) {
    if (index == 3) {
      SessionManager.setRefreshAccountList(true);
      FetchData fetchData = new FetchData();
      for (Transaction dao : this.multiselect_list) {
        fetchData.deleteTransaction(dao.getTransactionId());
      }
      loadData();
      if (this.mActionMode != null) {
        this.mActionMode.finish();
        return;
      }
      return;
    }
    settleAccount(index);
  }
}
