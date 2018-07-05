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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.List;

public class LastTransactionActivity extends ActivityMultipleSelection2 implements OnItemSelectedListener, OnTransactionListListener {
  private AdView mAdView;
  private DayTransactionAdapter mAdapter;
  private Balance mBalance;
  private RecyclerView mListTransactions;
  private Spinner mSpNumber;
  private List<Transaction> mTransactions;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[12]);
    getViews();
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

  private void getViews() {
    findViewById(R.id.img_search).setVisibility(View.GONE);
    findViewById(R.id.layout_spinner).setVisibility(View.VISIBLE);
    this.mSpNumber = (Spinner) findViewById(R.id.sp_account);
    this.mSpNumber.setOnItemSelectedListener(this);
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"});
    this.mSpNumber.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    this.mSpNumber.setSelection(4);
    this.mListTransactions = (RecyclerView) findViewById(R.id.list);
    this.mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    this.mListTransactions.setItemAnimator(new DefaultItemAnimator());
    this.mListTransactions.setHasFixedSize(true);
  }

  private void setAdapter() {
    this.mAdapter = new DayTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  private void getData() {
    this.mTransactions = getLastTransaction(this.mSpNumber.getSelectedItemPosition() + 1);
  }

  public ArrayList<Transaction> getLastTransaction(int limit) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat sdf1 = AppUtils.getDateFormat();
    String query = "SELECT Account.AID, Account.PersonName, Account.TypeName, Account.TypeName, Transection.TID, Transection.dr_cr, Transection.Narration, Transection.Credit_Amount, Transection.Debit_Amount, Transection.Narration, Transection.EntryDate, Transection.Remark, Transection.Image\nFROM Account INNER JOIN Transection ON Account.AID = Transection.AID\nORDER BY Transection.TID DESC LIMIT " + limit;
    ArrayList<Transaction> results = new ArrayList();
    double totalCredit = 0.0d;
    double totalDebit = 0.0d;
    Cursor c = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
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
          try {
            account.setDate(date);
          } catch (SQLiteException e2) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
          }
        }
        account.setNarration(c.getString(c.getColumnIndex("Narration")));
        account.setRemark(c.getString(c.getColumnIndex("Remark")));
        try {
          account.setCraditAmount(Double.parseDouble(c.getString(c.getColumnIndex("Credit_Amount"))));
        } catch (Exception e3) {
          account.setCraditAmount(0.0d);
        }
        try {
          account.setDebitAmount(Double.parseDouble(c.getString(c.getColumnIndex("Debit_Amount"))));
        } catch (Exception e4) {
          account.setDebitAmount(0.0d);
        }
        totalCredit += account.getCraditAmount();
        totalDebit += account.getDebitAmount();
        results.add(account);
      } while (c.moveToNext());
    }
    c.close();
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(this);
    this.mBalance = new Balance();
    this.mBalance.setCredit(mRsSymbol + format.format(totalCredit) + "/-");
    this.mBalance.setDebit(mRsSymbol + format.format(totalDebit) + "/-");
    if (totalCredit > totalDebit) {
      this.mBalance.setBalance(mRsSymbol + format.format(totalCredit - totalDebit) + "/-" + getResources().getString(R.string.txt_Credit));
    } else if (totalDebit > totalCredit) {
      this.mBalance.setBalance(mRsSymbol + format.format(totalDebit - totalCredit) + "/-" + getResources().getString(R.string.txt_Debit));
    } else {
      this.mBalance.setBalance(mRsSymbol + "0.00/-");
    }
    return results;
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getString(R.string.txt_Date));
    mColumns.add(getString(R.string.txt_Credit));
    mColumns.add(getString(R.string.txt_Debit));
    mColumns.add(getString(R.string.txt_AccName));
    mColumns.add(getString(R.string.txt_AccCat));
    mColumns.add(getString(R.string.txt_Narration));
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.LAST_TRAN_REPORT);
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
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 11).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 11).excel(this);
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

  protected List<Transaction> filter(List<Transaction> models, String newText) {
    newText = newText.toLowerCase();
    List<Transaction> filteredModelList = new ArrayList();
    for (Transaction model : models) {
      if (model.toString().toLowerCase().contains(newText)) {
        filteredModelList.add(model);
      }
    }
    return filteredModelList;
  }

  public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
    this.mTransactions = getLastTransaction(position + 1);
    this.mAdapter = new DayTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  public void onNothingSelected(AdapterView<?> adapterView) {
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == -1 && requestCode == 78) {
      setResult(-1);
      this.mTransactions = getLastTransaction(this.mSpNumber.getSelectedItemPosition() + 1);
      this.mAdapter = new DayTransactionAdapter(this, "", this.mTransactions, this.mBalance);
      this.mListTransactions.setAdapter(this.mAdapter);
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

  public void passwordConfirmed(int i) {
    SessionManager.setRefreshAccountList(true);
    FetchData fetchData = new FetchData();
    for (Transaction dao : this.multiselect_list) {
      fetchData.deleteTransaction(dao.getTransactionId());
    }
    this.mTransactions = getLastTransaction(this.mSpNumber.getSelectedItemPosition() + 1);
    this.mAdapter = new DayTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
  }
}
