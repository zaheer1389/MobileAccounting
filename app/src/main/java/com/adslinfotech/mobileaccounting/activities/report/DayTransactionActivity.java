package com.adslinfotech.mobileaccounting.activities.report;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.base.ActivityMultipleSelection2;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditTransaction;
import com.adslinfotech.mobileaccounting.adapter.report.DayTransactionAdapter;
import com.adslinfotech.mobileaccounting.adapter.report.DayTransactionAdapter.OnTransactionListListener;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayTransactionActivity extends ActivityMultipleSelection2 implements OnTransactionListListener {
  private AdView mAdView;
  private DayTransactionAdapter mAdapter;
  private Balance mBalance;
  private String mCategorySelected;
  private ArrayList<Date> mDates;
  private RecyclerView mListTransactions;
  private List<Transaction> mTransactions;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[9]);
    getData();
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
    this.mListTransactions = (RecyclerView) findViewById(R.id.list);
    this.mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    this.mListTransactions.setItemAnimator(new DefaultItemAnimator());
    this.mListTransactions.setHasFixedSize(true);
    this.mAdapter = new DayTransactionAdapter(this, this.mCategorySelected, this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  private void getData() {
    this.mDates = (ArrayList) getIntent().getExtras().getSerializable(AppConstants.DATA);
    this.mCategorySelected = getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED);
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE);
    this.mTransactions = getDayTransaction(this.mCategorySelected, format.format((Date) this.mDates.get(0)), format.format((Date) this.mDates.get(1)));
  }

  /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
  public ArrayList<Transaction> getDayTransaction(String type, String fromDate, String toDate) {
    String query;
    SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat sdf1 = AppUtils.getDateFormat();
    if (type.equalsIgnoreCase("All")) {
      query = "SELECT Account.AID, Account.PersonName, Account.TypeName, Account.TypeName, Transection.TID, Transection.dr_cr, Transection.EntryDate, Transection.Narration, Transection.Credit_Amount, Transection.Debit_Amount, Transection.Narration, Transection.Remark, Transection.Image\nFROM Account INNER JOIN Transection ON Account.AID = Transection.AID\nWHERE Transection.EntryDate BETWEEN Date('" + fromDate + "') AND Date('" + toDate + "') ORDER BY Transection.EntryDate";
    } else {
      query = "SELECT Account.AID, Account.PersonName, Account.TypeName, Account.TypeName, Transection.TID, Transection.dr_cr, Transection.EntryDate, Transection.Narration, Transection.Credit_Amount, Transection.Debit_Amount, Transection.Narration, Transection.Remark, Transection.Image\nFROM Account INNER JOIN Transection ON Account.AID = Transection.AID\nWHERE Account.TypeName LIKE '" + type + "' AND Transection.EntryDate BETWEEN Date('" + fromDate + "') AND Date('" + toDate + "') ORDER BY Transection.EntryDate";
    }
    ArrayList<Transaction> results = new ArrayList();
    double d = 0.0d;
    double d2 = 0.0d;
    Cursor c = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    Log.d("getDayTransaction " + c.getCount(), "" + query);
    if (c.moveToFirst()) {
      do {
        Transaction account = new Transaction();
        account.setTransactionId(c.getInt(c.getColumnIndex("TID")));
        account.setAId(c.getInt(c.getColumnIndex("AID")));
        account.setImage(c.getBlob(c.getColumnIndex("Image")));
        account.setAccName(c.getString(c.getColumnIndex(ACCOUNT.NAME)));
        account.setDr_cr(c.getInt(c.getColumnIndex("dr_cr")));
        account.setType(c.getString(c.getColumnIndex("TypeName")));
        String date = c.getString(c.getColumnIndex("EntryDate"));
        try {
          account.setDate(sdf1.format(sdf.parse(date)));
        } catch (Exception e) {
          account.setDate(date);
        }
        account.setNarration(c.getString(c.getColumnIndex("Narration")));
        account.setRemark(c.getString(c.getColumnIndex("Remark")));
        try {
          account.setCraditAmount(Double.parseDouble(c.getString(c.getColumnIndex("Credit_Amount"))));
        } catch (Exception e2) {
          account.setCraditAmount(0.0d);
        }
        try {
          account.setDebitAmount(Double.parseDouble(c.getString(c.getColumnIndex("Debit_Amount"))));
          d += account.getCraditAmount();
          d2 += account.getDebitAmount();
          results.add(account);
        } catch (SQLiteException e3) {
          Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
      } while (c.moveToNext());
    }
    c.close();
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(this);
    this.mBalance = new Balance();
    this.mBalance.setCredit(mRsSymbol + format.format(d) + "/-");
    this.mBalance.setDebit(mRsSymbol + format.format(d2) + "/-");
    if (d > d2) {
      this.mBalance.setBalance(mRsSymbol + format.format(d - d2) + "/-" + getResources().getString(R.string.txt_Credit));
    } else if (d2 > d) {
      this.mBalance.setBalance(mRsSymbol + format.format(d2 - d) + "/-" + getResources().getString(R.string.txt_Debit));
    } else {
      this.mBalance.setBalance(mRsSymbol + "0.00/-");
    }
    return results;
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Date));
    mColumns.add(getResources().getString(R.string.txt_Credit));
    mColumns.add(getResources().getString(R.string.txt_Debit));
    mColumns.add(getResources().getString(R.string.txt_AccName));
    mColumns.add(getResources().getString(R.string.txt_AccCat));
    mColumns.add(getResources().getString(R.string.txt_Narration));
    ArrayList<PdfDao> mValues = new ArrayList();
    for (Transaction dao : this.mTransactions) {
      PdfDao pdf = new PdfDao();
      pdf.setFirst(dao.getDate());
      pdf.setSecond("" + dao.getCraditAmount());
      pdf.setThird("" + dao.getDebitAmount());
      pdf.setFour(dao.getAccName());
      pdf.setFive(dao.getType());
      pdf.setSix(dao.getNarration());
      mValues.add(pdf);
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.DAY_REPORT);
    header.setSecond(this.mBalance.getCredit());
    header.setThird(this.mBalance.getDebit());
    header.setFour(this.mBalance.getBalance());
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 3).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 3).excel(this);
        return;
      default:
        return;
    }
  }

  protected void searchComplete() {
    this.mAdapter.setFilter(this.mTransactions, false);
  }

  protected void search(String newText) {
    this.mAdapter.setFilter(filter(this.mTransactions, newText), true);
  }

  protected List<Transaction> filter(List<Transaction> models, String query) {
    query = query.toLowerCase();
    List<Transaction> filteredModelList = new ArrayList();
    for (Transaction model : models) {
      if (model.toString().toLowerCase().contains(query)) {
        filteredModelList.add(model);
      }
    }
    return filteredModelList;
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == -1 && requestCode == 78) {
      setResult(-1);
      loadData();
    }
  }

  private void loadData() {
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE);
    this.mTransactions = getDayTransaction(this.mCategorySelected, format.format((Date) this.mDates.get(0)), format.format((Date) this.mDates.get(1)));
    this.mAdapter = new DayTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
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

  public void passwordConfirmed(int i) {
    SessionManager.setRefreshAccountList(true);
    FetchData fetchData = new FetchData();
    for (Transaction dao : this.multiselect_list) {
      fetchData.deleteTransaction(dao.getTransactionId());
    }
    loadData();
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
  }
}
