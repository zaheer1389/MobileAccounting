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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.report.CategoryLedgerAdapter;
import com.adslinfotech.mobileaccounting.adapter.report.CategoryLedgerAdapter.OnCategoryListListener;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.calculator.Calculator;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.desktop.WebServerActivity;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.gmail.SendBalanceHelper;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CategoryLedgerActivity extends SimpleAccountingActivity implements OnCategoryListListener {
  private static final int REQUEST_READ_PHONE_STORAGE = 21;
  private boolean isShareBalanceCalled;
  private AdView mAdView;
  private Balance mBalance;
  private SendBalanceHelper mBalanceHelper;
  private String mCategorySelected;
  private List<Transaction> mTransactions;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[7]);
    getData();
    setAdapter();
    initAds();
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
    mListTransactions.setAdapter(new CategoryLedgerAdapter(this, this.mCategorySelected, this.mTransactions, this.mBalance));
  }

  private void getData() {
    this.mCategorySelected = getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED);
    if (this.mCategorySelected.equalsIgnoreCase("All")) {
      this.mTransactions = getCategoryBalance(null);
    } else {
      this.mTransactions = getCategoryBalance(this.mCategorySelected);
    }
  }

  public ArrayList<Transaction> getCategoryBalance(String category) {
    String query;
    if (category == null) {
      query = "SELECT Account.PersonName, Account.PersonEmail, Account.PersonMobile, Sum(Transection.Credit_Amount) AS SumOfCRAmount, Sum(Transection.Debit_Amount) AS SumOfDRAmount\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.AID, Account.TypeName\nORDER BY PersonName COLLATE NOCASE;";
    } else {
      query = "SELECT Account.PersonName, Account.PersonEmail, Account.PersonMobile, Sum(Transection.Credit_Amount) AS SumOfCRAmount, Sum(Transection.Debit_Amount) AS SumOfDRAmount\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.AID, Account.TypeName\nHAVING (((Account.TypeName)=\"" + category + "\")) ORDER BY PersonName COLLATE NOCASE;";
    }
    ArrayList<Transaction> results = new ArrayList();
    double d = 0.0d;
    double d2 = 0.0d;
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(this);
    Cursor c = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    if (c.moveToFirst()) {
      do {
        double credit;
        double debit;
        Transaction dao = new Transaction();
        String name = c.getString(c.getColumnIndex(ACCOUNT.NAME));
        String strCr = c.getString(c.getColumnIndex("SumOfCRAmount"));
        String strDr = c.getString(c.getColumnIndex("SumOfDRAmount"));
        dao.setAccName(name);
        dao.setNarration(c.getString(c.getColumnIndex("PersonEmail")));
        dao.setRemark(c.getString(c.getColumnIndex("PersonMobile")));
        try {
          credit = Double.parseDouble(strCr);
        } catch (Exception e) {
          credit = 0.0d;
        }
        try {
          debit = Double.parseDouble(strDr);
        } catch (Exception e2) {
          debit = 0.0d;
        }
        if (credit > debit) {
          dao.setBalance(mRsSymbol + format.format(credit - debit) + "/-Cr");
        } else if (debit > credit) {
          try {
            dao.setBalance(mRsSymbol + format.format(debit - credit) + "/-Dr");
          } catch (SQLiteException e3) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
          }
        }
        d += credit;
        d2 += debit;
        results.add(dao);
      } while (c.moveToNext());
    }
    c.close();
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

  private void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Serialno));
    mColumns.add(getResources().getString(R.string.txt_AccName));
    mColumns.add(getResources().getString(R.string.txt_Balance));
    mColumns.add(getResources().getString(R.string.txt_Mobile));
    mColumns.add(getResources().getString(R.string.txt_Email));
    int i = 1;
    ArrayList<PdfDao> mValues = new ArrayList();
    for (Transaction dao : this.mTransactions) {
      PdfDao pdf = new PdfDao();
      pdf.setFirst("" + i);
      pdf.setSecond(dao.getAccName());
      pdf.setThird(dao.getBalance());
      pdf.setFour(dao.getRemark());
      pdf.setFive(dao.getNarration());
      mValues.add(pdf);
      i++;
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.OVERALL_LEDGER);
    header.setSecond(this.mBalance.getCredit());
    header.setThird(this.mBalance.getDebit());
    header.setFour(this.mBalance.getBalance());
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 2).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 2).excel(this);
        return;
      default:
        return;
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_export_browse, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i;
    switch (item.getItemId()) {
      case R.id.menu_browse:
        i = new Intent(getApplicationContext(), WebServerActivity.class);
        i.putExtra("TYPE", 2);
        i.putExtra(AppConstants.ACCOUNT_SELECTED, this.mCategorySelected);
        startActivity(i);
        return false;
      case R.id.menu_export_exel:
        exportPdf(1);
        return false;
      case R.id.menu_export_pdf:
        exportPdf(0);
        return false;
      case R.id.menu_usecal:
        try {
          i = new Intent();
          i.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
          startActivity(i);
          return false;
        } catch (Exception e) {
          startActivity(new Intent(getApplicationContext(), Calculator.class));
          return false;
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void onItemClicked(Transaction item) {
    this.isShareBalanceCalled = true;
    ArrayList<Transaction> transactions = new ArrayList();
    transactions.add(item);
    this.mBalanceHelper = new SendBalanceHelper(this, transactions);
    this.mBalanceHelper.share("Share Balance Detail");
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (this.isShareBalanceCalled && requestCode == 21 && grantResults[0] == 0) {
      this.mBalanceHelper.sendBySMS();
    }
    this.isShareBalanceCalled = false;
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
