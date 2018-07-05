package com.adslinfotech.mobileaccounting.activities.report;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.report.DateTransactionAdapter;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.fragment.chart.LineChartFragment;
import com.adslinfotech.mobileaccounting.fragment.chart.PieChartFragment;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LastYearActivity extends ReportSearchActivity {
  private static final int DATE_FROM = 124;
  private static final int DATE_TO = 325;
  private AdView mAdView;
  private DateTransactionAdapter mAdapter;
  private Balance mBalance;
  private Date mDateFrom;
  private Date mDateTo;
  private SimpleDateFormat mFormat;
  private LineChartFragment mFragLineChart;
  private PieChartFragment mFragPieChart;
  private RecyclerView mListTransactions;
  private NumberFormat mNumFormat;
  private String mRsSymbol;
  private ArrayList<Transaction> mTransactions;
  private TextView mTvFrom;
  private TextView mTvTo;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_yearly_report);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[13]);
    getViews();
    getData();
    setAdapter();
    initAds();
  }

  protected void setImage(Bitmap thumbnail) {
  }

  private void getViews() {
    this.mNumFormat = AppUtils.getCurrencyFormatter();
    this.mRsSymbol = SessionManager.getCurrency(this);
    findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
    this.mFormat = new SimpleDateFormat("MMM yyyy");
    this.mTvFrom = (TextView) findViewById(R.id.tv_from_date);
    this.mTvTo = (TextView) findViewById(R.id.tv_to_date);
    initDates();
  }

  private void initDates() {
    this.mDateTo = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(this.mDateTo);
    cal.add(2, -1);
    cal.add(1, -1);
    this.mDateFrom = cal.getTime();
    this.mTvFrom.setText(this.mFormat.format(this.mDateFrom));
    this.mTvTo.setText(this.mFormat.format(this.mDateTo));
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
    this.mAdapter = new DateTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  private void getData() {
    this.mTransactions = getDateTransactions();
  }

  public ArrayList<Transaction> getDateTransactions() {
    SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat f2 = new SimpleDateFormat("MMM yyyy");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy");
    Calendar fromCal = Calendar.getInstance();
    Calendar toCal = Calendar.getInstance();
    fromCal.setTime(this.mDateFrom);
    toCal.setTime(this.mDateTo);
    int yearFrom = fromCal.get(1);
    int yearTo = toCal.get(1);
    int monthTo = toCal.get(2) + 2;
    String query = "SELECT sum(Debit_Amount) AS SumOfDRAmount, sum(Credit_Amount) AS SumOfCRAmount, strftime('%Y-%m', EntryDate) as yr_mon, strftime('%Y', EntryDate) as _yr, strftime('%m', EntryDate) as _mon FROM Transection WHERE ((cast(_yr as INTEGER) > " + yearFrom + " AND cast(_yr as INTEGER) < " + yearTo + ") OR (_yr LIKE '" + yearFrom + "' AND cast(_mon as INTEGER) > " + fromCal.get(2) + ") OR (_yr LIKE '" + yearTo + "' AND cast(_mon as INTEGER) < " + monthTo + ")) GROUP BY yr_mon ORDER BY yr_mon desc";
    ArrayList<Transaction> transactions = new ArrayList();
    double totalCredit = 0.0d;
    double totalDebit = 0.0d;
    Cursor cursor = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    Log.d("getDayTransaction " + cursor.getCount(), "" + query);
    if (cursor.moveToFirst()) {
      int clmDate = cursor.getColumnIndex("yr_mon");
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
        if (data.getCraditAmount() > data.getDebitAmount()) {
          data.setBalance((data.getCraditAmount() - data.getDebitAmount()) + "/-Cr");
        } else if (data.getDebitAmount() > data.getCraditAmount()) {
          data.setBalance((data.getDebitAmount() - data.getCraditAmount()) + "/-Dr");
        } else {
          data.setBalance("0.00");
        }
        try {
          Date date = f1.parse(cursor.getString(clmDate));
          data.setDate(f2.format(date));
          data.setRemark(simpleDateFormat.format(date));
        } catch (Exception e3) {
          data.setDate(cursor.getString(clmDate));
        }
        try {
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
    this.mBalance = new Balance();
    this.mBalance.setCredit(this.mRsSymbol + this.mNumFormat.format(totalCredit) + "/-");
    this.mBalance.setDebit(this.mRsSymbol + this.mNumFormat.format(totalDebit) + "/-");
    if (totalCredit > totalDebit) {
      this.mBalance.setBalance(this.mRsSymbol + this.mNumFormat.format(totalCredit - totalDebit) + "/-" + getResources().getString(R.string.txt_Credit));
    } else if (totalDebit > totalCredit) {
      this.mBalance.setBalance(this.mRsSymbol + this.mNumFormat.format(totalDebit - totalCredit) + "/-" + getResources().getString(R.string.txt_Debit));
    } else {
      this.mBalance.setBalance(this.mRsSymbol + "0.00/-");
    }
    return transactions;
  }

  public String getFormatedBal(double amt) {
    return this.mRsSymbol + this.mNumFormat.format(amt) + "/-";
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    boolean b = super.onCreateOptionsMenu(menu);
    menu.add(0, 1, 1, R.string.txt_Pie);
    menu.add(0, 2, 2, R.string.txt_line);
    return b;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    switch (item.getItemId()) {
      case 1:
        this.mFragPieChart = PieChartFragment.newInstance(this.mTransactions);
        fragmentTransaction.add(R.id.content_frame, this.mFragPieChart, "PieChartFragment");
        fragmentTransaction.commit();
        return true;
      case 2:
        this.mFragLineChart = LineChartFragment.newInstance(this.mTransactions);
        fragmentTransaction.add(R.id.content_frame, this.mFragLineChart, "LineChartFragment");
        fragmentTransaction.commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Month));
    mColumns.add(getResources().getString(R.string.txt_Credit));
    mColumns.add(getResources().getString(R.string.txt_Debit));
    mColumns.add(getResources().getString(R.string.txt_Balance));
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.YEAR_REPORT);
    ArrayList<PdfDao> mValues = new ArrayList();
    Iterator it = this.mTransactions.iterator();
    while (it.hasNext()) {
      Transaction dao = (Transaction) it.next();
      PdfDao pdf = new PdfDao();
      pdf.setFirst(dao.getDate());
      pdf.setSecond("" + dao.getCraditAmount());
      pdf.setThird("" + dao.getDebitAmount());
      pdf.setFour(dao.getBalance());
      mValues.add(pdf);
    }
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 5).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 5).excel(this);
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

  public void selectDateFrom(View view) {
    showDate(124, this.mDateFrom, true);
  }

  public void selectDateTo(View view) {
    showDate(325, this.mDateTo, true);
  }

  protected void populateSetDate(int id, int year, int month, int day) {
    Date date;
    try {
      date = new SimpleDateFormat(DateFormat.DB_DATE).parse(year + "-" + month + "-" + day);
    } catch (Exception e) {
      date = new Date();
    }
    if (id == 124) {
      this.mDateFrom = date;
      this.mTvFrom.setText(this.mFormat.format(this.mDateFrom));
    } else {
      this.mDateTo = date;
      this.mTvTo.setText(this.mFormat.format(this.mDateTo));
    }
    getData();
    this.mAdapter = new DateTransactionAdapter(this, "", this.mTransactions, this.mBalance);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  public void onBackPressed() {
    if (this.mFragPieChart != null && this.mFragPieChart.isAdded()) {
      getSupportFragmentManager().beginTransaction().remove(this.mFragPieChart).commit();
    } else if (this.mFragLineChart == null || !this.mFragLineChart.isAdded()) {
      super.onBackPressed();
    } else {
      getSupportFragmentManager().beginTransaction().remove(this.mFragLineChart).commit();
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

  public void passwordConfirmed(int i) {
  }
}
