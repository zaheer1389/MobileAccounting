package com.adslinfotech.mobileaccounting.activities.report;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditAccount;
import com.adslinfotech.mobileaccounting.adapter.report.AccountAdapter;
import com.adslinfotech.mobileaccounting.adapter.report.AccountAdapter.OnAccountListListener;
import com.adslinfotech.mobileaccounting.dao.Account;
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
import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends ReportSearchActivity implements OnAccountListListener {
  private static final int MULTIPLE_DELETE = 3;
  private boolean isAllSelected = false;
  private boolean isMultiSelect = false;
  private List<Account> mAccounts;
  private ActionMode mActionMode;
  @SuppressLint({"NewApi"})
  protected Callback mActionModeCallback = new Callback() {
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      mode.getMenuInflater().inflate(R.menu.menu_multiple_delete, menu);
      return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      boolean z = false;
      switch (item.getItemId()) {
        case R.id.menu_delete:
          if (AccountListActivity.this.multiselect_list.size() == 0) {
            AccountListActivity.this.showPositiveAlert(null, AccountListActivity.this.getResources().getString(R.string.msg_no_item_delete));
            return true;
          }
          AccountListActivity.this.showAlertExitApp(AccountListActivity.this.getResources().getString(R.string.alert_delete_multiple_account), 3);
          return true;
        case R.id.menu_select_all:
          if (AccountListActivity.this.isAllSelected) {
            AccountListActivity.this.multiselect_list.clear();
            AccountListActivity.this.refreshAdapterData();
            AccountListActivity.this.setActionTitle();
          } else {
            AccountListActivity.this.onSelectAllClick();
          }
          AccountListActivity accountListActivity = AccountListActivity.this;
          if (!AccountListActivity.this.isAllSelected) {
            z = true;
          }
          accountListActivity.isAllSelected = z;
          return true;
        default:
          return false;
      }
    }

    public void onDestroyActionMode(ActionMode mode) {
      AccountListActivity.this.mActionMode = null;
      AccountListActivity.this.isMultiSelect = false;
      AccountListActivity.this.isAllSelected = false;
      AccountListActivity.this.multiselect_list = new ArrayList();
      AccountListActivity.this.refreshAdapterData();
    }
  };
  private AdView mAdView;
  private AccountAdapter mAdapter;
  private List<Account> multiselect_list = new ArrayList();

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[2]);
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
    RecyclerView mListTransactions = (RecyclerView) findViewById(R.id.list);
    mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    mListTransactions.setItemAnimator(new DefaultItemAnimator());
    mListTransactions.setHasFixedSize(true);
    this.mAdapter = new AccountAdapter(this, this.mAccounts);
    mListTransactions.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
  }

  private void getData() {
    this.mAccounts = new FetchData().getAllAccounts(false, false);
  }

  protected void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getResources().getString(R.string.txt_Serialno));
    mColumns.add(getResources().getString(R.string.txt_AccName));
    mColumns.add(getResources().getString(R.string.txt_Mobile));
    mColumns.add(getResources().getString(R.string.txt_Email));
    mColumns.add(getResources().getString(R.string.txt_AccCat));
    int i = 1;
    ArrayList<PdfDao> mValues = new ArrayList();
    for (Account dao : this.mAccounts) {
      PdfDao pdf = new PdfDao();
      pdf.setFirst("" + i);
      pdf.setSecond(dao.getName());
      pdf.setThird(dao.getMobile());
      pdf.setFour(dao.getEmail());
      pdf.setFour(dao.getCategory());
      mValues.add(pdf);
      i++;
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.DATE_REPORT);
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
    this.mAdapter.setFilter(this.mAccounts);
  }

  protected void search(String newText) {
    this.mAdapter.setFilter(filterAccount(this.mAccounts, newText));
  }

  protected List<Transaction> filter(List<Transaction> list, String query) {
    return null;
  }

  private List<Account> filterAccount(List<Account> models, String query) {
    query = query.toLowerCase();
    List<Account> filteredModelList = new ArrayList();
    for (Account model : models) {
      if ((model.getName() + model.getEmail() + model.getMobile() + model.getCategory() + model.getRemark()).toLowerCase().contains(query)) {
        filteredModelList.add(model);
      }
    }
    return filteredModelList;
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != 78) {
      return;
    }
    if (resultCode == -1 || resultCode == 1) {
      setResult(-1);
      getData();
      setAdapter();
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

  public void onItemClicked(Account item) {
    if (this.isMultiSelect) {
      multi_select(item);
      return;
    }
    Intent intent = new Intent(this, ActivityEditAccount.class);
    intent.putExtra(AppConstants.ACCOUNT_SELECTED, item.getName());
    intent.putExtra("SCREEN", 1);
    startActivityForResult(intent, 78);
  }

  @SuppressLint({"NewApi"})
  public void onItemLongClicked(Account transaction) {
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
    this.multiselect_list.addAll(this.mAccounts);
    refreshAdapterData();
    setActionTitle();
  }

  public void refreshAdapterData() {
    this.mAdapter.selected_usersList = this.multiselect_list;
    this.mAdapter.mAccounts = this.mAccounts;
    this.mAdapter.notifyDataSetChanged();
  }

  @SuppressLint({"NewApi"})
  public void passwordConfirmed(int i) {
    SessionManager.setRefreshAccountList(true);
    setResult(-1);
    FetchData fetchData = new FetchData();
    for (Account dao : this.multiselect_list) {
      fetchData.settleAccount(dao.getAccountId());
      fetchData.deleteAccount(dao.getAccountId());
    }
    getData();
    setAdapter();
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
  }

  public void multi_select(Account item) {
    if (this.mActionMode != null) {
      if (this.multiselect_list.contains(item)) {
        this.multiselect_list.remove(item);
      } else {
        this.multiselect_list.add(item);
      }
      setActionTitle();
      refreshAdapterData();
    }
  }

  @SuppressLint({"NewApi"})
  protected void setActionTitle() {
    if (this.multiselect_list.size() > 0) {
      this.mActionMode.setTitle("" + this.multiselect_list.size());
    } else {
      this.mActionMode.setTitle("");
    }
  }

  public void onPositiveClick(int from) {
    checkPasswordRequired(from);
  }

  protected void setImage(Bitmap thumbnail) {
  }
}
