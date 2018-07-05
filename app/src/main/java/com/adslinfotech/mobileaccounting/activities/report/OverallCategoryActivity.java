package com.adslinfotech.mobileaccounting.activities.report;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.report.CategoryLedgerAdapter;
import com.adslinfotech.mobileaccounting.adapter.report.CategoryLedgerAdapter.OnCategoryListListener;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OverallCategoryActivity extends ReportSearchActivity implements OnCategoryListListener {
  private AdView mAdView;
  private CategoryLedgerAdapter mAdapter;
  private List<Transaction> mTransactions;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[8]);
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
    this.mAdapter = new CategoryLedgerAdapter(this, "", this.mTransactions, null);
    mListTransactions.setAdapter(this.mAdapter);
  }

  private void getData() {
    this.mTransactions = getAllCategoryBal();
  }

  public ArrayList<Transaction> getAllCategoryBal() {
    String query = "SELECT Sum(Transection.Credit_Amount) AS SumOfCRAmount, Sum(Transection.Debit_Amount) AS SumOfDRAmount, Account.TypeName\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.TypeName";
    ArrayList<String> name = new ArrayList();
    ArrayList<Transaction> results = new ArrayList();
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(this);
    Cursor c = SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    Log.d("getAllCategoryBal", "size: " + c.getCount());
    if (c.moveToFirst()) {
      do {
        double credit;
        double debit;
        Transaction account = new Transaction();
        String strCr = c.getString(c.getColumnIndex("SumOfCRAmount"));
        String strDr = c.getString(c.getColumnIndex("SumOfDRAmount"));
        try {
          String type = c.getString(c.getColumnIndex("TypeName"));
          if (type == null || type.trim().equalsIgnoreCase("")) {
            type = "Individual";
          }
          name.add(type);
          account.setAccName(type);
        } catch (Exception e) {
        }
        try {
          credit = Double.parseDouble(strCr);
        } catch (Exception e2) {
          credit = 0.0d;
        }
        try {
          debit = Double.parseDouble(strDr);
        } catch (Exception e3) {
          debit = 0.0d;
        }
        if (credit > debit) {
          account.setBalance(mRsSymbol + format.format(credit - debit) + "/-" + getResources().getString(R.string.txt_Credit));
        } else if (debit > credit) {
          try {
            account.setBalance(mRsSymbol + format.format(debit - credit) + "/-" + getResources().getString(R.string.txt_Debit));
          } catch (SQLiteException e4) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
          }
        } else {
          account.setBalance("0.00");
        }
        results.add(account);
      } while (c.moveToNext());
    }
    c.close();
    ArrayList<Transaction> trans = new ArrayList();
    Iterator it = new FetchData().getAllCategory().iterator();
    while (it.hasNext()) {
      Category category = (Category) it.next();
      if (name.contains(category.getName())) {
        ArrayList<Transaction> arrayList = trans;
        arrayList.add((Transaction) results.get(name.indexOf(category.getName())));
      } else {
        Transaction dao = new Transaction();
        dao.setAccName(category.getName());
        dao.setBalance("0");
        trans.add(dao);
      }
    }
    return trans;
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Serialno));
    mColumns.add(getResources().getString(R.string.txt_catname));
    mColumns.add(getResources().getString(R.string.txt_Balance));
    int i = 0;
    ArrayList<PdfDao> mValues = new ArrayList();
    for (Transaction dao : this.mTransactions) {
      i++;
      PdfDao pdf = new PdfDao();
      pdf.setFirst("" + i);
      pdf.setSecond(dao.getAccName());
      pdf.setThird(dao.getBalance());
      mValues.add(pdf);
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.ACCOUNT_REPORT);
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 8).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 8).excel(this);
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

  public void onItemClicked(Transaction item) {
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
