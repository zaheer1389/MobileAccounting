package com.adslinfotech.mobileaccounting.activities.report;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.report.DateTransactionAdapter;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
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

public class DateTransactionActivity extends ReportSearchActivity {
  private AdView mAdView;
  private DateTransactionAdapter mAdapter;
  private Balance mBalance;
  private List<Transaction> mTransactions;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[10]);
    getData();
    setAdapter();
    initAds();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  protected void setImage(Bitmap thumbnail) {
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
    RecyclerView mListTransactions = (RecyclerView) findViewById(R.id.list);
    mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    mListTransactions.setItemAnimator(new DefaultItemAnimator());
    mListTransactions.setHasFixedSize(true);
    this.mAdapter = new DateTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    mListTransactions.setAdapter(this.mAdapter);
  }

  private void getData() {
    ArrayList<Date> data = (ArrayList) getIntent().getExtras().getSerializable(AppConstants.DATA);
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE);
    this.mTransactions = getDateTransactions(format.format((Date) data.get(0)), format.format((Date) data.get(1)));
  }

  public ArrayList<Transaction> getDateTransactions(String fromDate, String toDate) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat sdf1 = AppUtils.getDateFormat();
    String query = "SELECT sum(Debit_Amount) AS SumOfDRAmount, sum(Credit_Amount) AS SumOfCRAmount, EntryDate FROM Transection\nWHERE (AID != 0) AND EntryDate BETWEEN Date('" + fromDate + "') AND Date('" + toDate + "') GROUP BY EntryDate";
    ArrayList<Transaction> transactions = new ArrayList();
    double totalCredit = 0.0d;
    double totalDebit = 0.0d;
    Cursor cursor = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    Log.d("getDayTransaction " + cursor.getCount(), "" + query);
    if (cursor.moveToFirst()) {
      int clmDate = cursor.getColumnIndex("EntryDate");
      int damt = cursor.getColumnIndex("SumOfDRAmount");
      int camt = cursor.getColumnIndex("SumOfCRAmount");
      do {
        Transaction data = new Transaction();
        String debit = cursor.getString(damt);
        try {
          data.setCraditAmount(Double.parseDouble(cursor.getString(camt)));
        } catch (Exception e) {
          data.setCraditAmount(0.0d);
        }
        try {
          data.setDebitAmount(Double.parseDouble(debit));
        } catch (Exception e2) {
          data.setDebitAmount(0.0d);
        }
        try {
          if (data.getCraditAmount() > data.getDebitAmount()) {
            data.setBalance((data.getCraditAmount() - data.getDebitAmount()) + "/-Cr");
          } else if (data.getDebitAmount() > data.getCraditAmount()) {
            data.setBalance((data.getDebitAmount() - data.getCraditAmount()) + "/-Dr");
          } else {
            data.setBalance("0.00");
          }
          String date = cursor.getString(clmDate);
          try {
            data.setDate(sdf1.format(simpleDateFormat.parse(date)));
          } catch (Exception e3) {
            data.setDate(date);
          }
          totalCredit += data.getCraditAmount();
          totalDebit += data.getDebitAmount();
          transactions.add(data);
        } catch (Exception e4) {
          e4.printStackTrace();
          Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
      } while (cursor.moveToNext());
      cursor.close();
    }
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
    return transactions;
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Date));
    mColumns.add(getResources().getString(R.string.txt_Credit));
    mColumns.add(getResources().getString(R.string.txt_Debit));
    mColumns.add(getResources().getString(R.string.txt_Balance));
    ArrayList<PdfDao> mValues = new ArrayList();
    for (Transaction dao : this.mTransactions) {
      PdfDao pdf = new PdfDao();
      pdf.setFirst(dao.getDate());
      pdf.setSecond("" + dao.getCraditAmount());
      pdf.setThird("" + dao.getDebitAmount());
      pdf.setFour(dao.getBalance());
      mValues.add(pdf);
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.DATE_REPORT);
    header.setSecond(this.mBalance.getCredit());
    header.setThird(this.mBalance.getDebit());
    header.setFour(this.mBalance.getBalance());
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 4).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 4).excel(this);
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

  public void passwordConfirmed(int i) {
  }
}
