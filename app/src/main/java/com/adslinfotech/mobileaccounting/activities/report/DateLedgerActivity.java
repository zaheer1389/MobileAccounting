package com.adslinfotech.mobileaccounting.activities.report;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.base.ActivityMultipleSelection;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditTransaction;
import com.adslinfotech.mobileaccounting.adapter.report.tabullar.LedgerAdapterTabullar;
import com.adslinfotech.mobileaccounting.adapter.report.tabullar.LedgerAdapterTabullar.OnTransactionListListener;
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
import com.adslinfotech.mobileaccounting.fragment.ChooseDateActivity;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.image.FullScreenImage;
import com.adslinfotech.mobileaccounting.tabullar.FixedGridLayoutManager;
import com.adslinfotech.mobileaccounting.tabullar.InsetDecoration;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchAccount;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateLedgerActivity extends ActivityMultipleSelection implements OnItemSelectedListener, OnTransactionListListener, OnQueryTextListener {
  private ArrayList<Date> arrDate;
  private boolean isListTablular;
  private Account mAccount;
  private List<String> mAccountNames;
  private AdView mAdView;
  private LedgerAdapterTabullar mAdapter;
  private Balance mBalance;
  private RecyclerView mListTransactions;
  private Spinner mSpAccount;
  private List<Transaction> mTransactions;
  private ViewStub mViewStub;
  private String strOpeningBal;
  private String strOverallBal;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[6]);
    if (SimpleAccountingApp.getPreference().getBoolean(ActivityPreferences.PREF_LIST_TYPE, true)) {
      setContentView((int) R.layout.activity_account_ledger__);
      this.mViewStub = (ViewStub) findViewById(R.id.view_stub);
      this.mViewStub.setLayoutResource(R.layout.header_account_ledger);
      this.mViewStub.inflate();
      this.isListTablular = true;
      findViewById(R.id.view_separate).setVisibility(View.VISIBLE);
      findViewById(R.id.lout_overall).setVisibility(View.VISIBLE);
      findViewById(R.id.lout_opening_bal).setVisibility(View.VISIBLE);
    } else {
      setContentView((int) R.layout.activity_account_ledger);
    }
    getViews();
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
    } else {
      this.mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
      this.mListTransactions.setItemAnimator(new DefaultItemAnimator());
      this.mListTransactions.setHasFixedSize(true);
    }
    this.mAccountNames = new FetchData().getAllAccountsNames();
    this.mAccountNames.add(0, getResources().getString(R.string.spinner_title));
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, this.mAccountNames);
    this.mSpAccount.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  private void setListAdapter() {
    this.mAdapter = new LedgerAdapterTabullar(this, this.mAccount, this.mBalance, this.mTransactions, this.strOverallBal, this.strOpeningBal, this.isListTablular);
    this.mListTransactions.setAdapter(this.mAdapter);
  }

  private void loadData(String account) {
    FetchData fetchData = new FetchData();
    this.mAccount = fetchData.getAccount(account);
    this.arrDate = (ArrayList) getIntent().getExtras().getSerializable(AppConstants.DATA);
    getData(fetchData);
  }

  private void getData(FetchData fetchData) {
    if (fetchData == null) {
      fetchData = new FetchData();
    }
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE);
    ArrayList list = fetchData.getAllTransactions(this, "SELECT * FROM Transection where AID =? AND EntryDate BETWEEN Date('" + format.format((Date) this.arrDate.get(0)) + "') AND Date('" + format.format((Date) this.arrDate.get(1)) + "') order by EntryDate", this.mAccount.getAccountId(), true);
    this.strOverallBal = fetchData.getAccountBalance(this.mAccount.getAccountId(), this, format.format((Date) this.arrDate.get(1))).getBalance();
    this.strOpeningBal = fetchData.getAccountBalance(this.mAccount.getAccountId(), this, format.format((Date) this.arrDate.get(0))).getBalance();
    this.mBalance = (Balance) list.get(1);
    this.mTransactions = (ArrayList) list.get(0);
    setListAdapter();
    if (this.isListTablular) {
      setText();
    }
  }

  private void setText() {
    TextView mTvCredit = (TextView) findViewById(R.id.txt_total_credit);
    TextView mTvDebit = (TextView) findViewById(R.id.txt_total_debit);
    TextView mTvBalance = (TextView) findViewById(R.id.txt_total_balance);
    ((TextView) findViewById(R.id.tv_name)).setText(Html.fromHtml(getResources().getString(R.string.txt_As_on_Date) + ": Ledger of " + this.mAccount.getName()));
    mTvCredit.setText(this.mBalance.getCredit());
    mTvDebit.setText(this.mBalance.getDebit());
    mTvBalance.setText(this.mBalance.getBalance());
    ImageView mImgAccount = (ImageView) findViewById(R.id.img_profile);
    mImgAccount.setOnClickListener(this);
    byte[] img = this.mAccount.getImage();
    if (img != null && img.length > 0) {
      mImgAccount.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
    }
    TextView tvOpeningBal = (TextView) findViewById(R.id.txt_opening_balance);
    ((TextView) findViewById(R.id.txt_overall_balance)).setText(this.strOverallBal);
    tvOpeningBal.setText(this.strOpeningBal);
  }

  private void getViews() {
    findViewById(R.id.layout_spinner).setVisibility(View.VISIBLE);
    this.mListTransactions = (RecyclerView) findViewById(R.id.list);
    this.mSpAccount = (Spinner) findViewById(R.id.sp_account);
    this.mSpAccount.setOnItemSelectedListener(this);
    Date date = new Date();
    this.arrDate = new ArrayList();
    this.arrDate.add(date);
    this.arrDate.add(date);
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_profile:
        Intent intent1 = new Intent(this, FullScreenImage.class);
        intent1.putExtra("image", this.mAccount.getImage());
        startActivity(intent1);
        return;
      case R.id.img_search:
        Intent intent = new Intent(this, ActivitySearchAccount.class);
        intent.putExtra(AppConstants.ACTIVITY_NAME, 2);
        startActivityForResult(intent, 1);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    String name = parent.getItemAtPosition(pos).toString();
    if (name.equals(getResources().getString(R.string.spinner_title))) {
      removeText();
    } else {
      loadData(name);
    }
  }

  private void removeText() {
  }

  private void export(int index) {
    if (this.mSpAccount.getSelectedItemPosition() == 0) {
      Toast.makeText(this, getResources().getString(R.string.spinner_title), Toast.LENGTH_SHORT).show();
      return;
    }
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
    header.setSeven(this.strOverallBal);
    header.setEight(this.strOpeningBal);
    List<Transaction> trans = this.mTransactions;
    if (!SessionManager.getListOrder()) {
      Collections.reverse(trans);
    }
    ArrayList<PdfDao> mValues = new ArrayList();
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
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 0).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 0).excel(this);
        return;
      default:
        return;
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_account_ledger, menu);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(this);
    menuItem.setOnActionExpandListener(new OnActionExpandListener() {
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      public boolean onMenuItemActionCollapse(MenuItem item) {
        DateLedgerActivity.this.searchComplete();
        return true;
      }
    });
    return true;
  }

  public boolean onQueryTextChange(String newText) {
    if (this.mSpAccount.getSelectedItemPosition() != 0) {
      if (this.mViewStub != null) {
        this.mViewStub.setVisibility(View.GONE);
      }
      this.mAdapter.setFilter(filter(this.mTransactions, newText), true);
    }
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

  public boolean onOptionsItemSelected(MenuItem item) {
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    Intent i;
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        searchComplete();
        return super.onOptionsItemSelected(item);
      case R.id.menu_balshare:
        if (this.mSpAccount.getSelectedItemPosition() != 0) {
          new ShareHelper(this, "Your Transaction with MR. " + SessionManager.getName(), "Your Transaction with MR. " + SessionManager.getName() + "\n Total Credit =" + this.mBalance.getCredit() + "\n Total Debit =" + this.mBalance.getDebit() + "\n Total Balance =" + this.mBalance.getBalance() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "\n Total Credit =" + this.mBalance.getCredit() + "\n Total Debit =" + this.mBalance.getDebit() + "\n Total Balance =" + this.mBalance.getBalance() + "\nvia: Simple Accounting App\nhttp://bit.ly/24POGqP", "", "").share();
          break;
        }
        Toast.makeText(this, getResources().getString(R.string.txt_SelectAccount), Toast.LENGTH_SHORT).show();
        break;
      case R.id.menu_browse:
        if (this.mSpAccount.getSelectedItemPosition() != 0) {
          i = new Intent(getApplicationContext(), WebServerActivity.class);
          i.putExtra("TYPE", 1);
          i.putExtra(AppConstants.ACCOUNT_DAO, this.mAccount);
          startActivity(i);
          break;
        }
        Toast.makeText(this, getResources().getString(R.string.txt_SelectAccount), Toast.LENGTH_SHORT).show();
        break;
      case R.id.menu_date:
        ChooseDateActivity.newInstance(this, 7, 7, this.arrDate);
        break;
      case R.id.menu_detailshare:
        if (this.mSpAccount.getSelectedItemPosition() != 0) {
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
        }
        Toast.makeText(this, getResources().getString(R.string.txt_SelectAccount), Toast.LENGTH_SHORT).show();
        break;
      case R.id.menu_export_exel:
        export(1);
        break;
      case R.id.menu_export_pdf:
        export(0);
        break;
      case R.id.menu_usecal:
        try {
          i = new Intent();
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

  public void onNothingSelected(AdapterView<?> adapterView) {
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 78 && resultCode == -1) {
      setResult(-1);
      loadData(this.mSpAccount.getSelectedItem().toString());
    } else if (requestCode == 1 && resultCode == -1) {
      this.mSpAccount.setSelection(this.mAccountNames.indexOf(((Account) data.getSerializableExtra(AppConstants.ACCOUNT_SELECTED)).getName()));
    } else if (requestCode == 7 && resultCode == -1) {
      this.arrDate = (ArrayList) data.getExtras().getSerializable(AppConstants.DATA);
      if (this.mSpAccount.getSelectedItemPosition() != 0) {
        getData(null);
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

  public void passwordConfirmed(int i) {
    SessionManager.setRefreshAccountList(true);
    FetchData fetchData = new FetchData();
    for (Transaction dao : this.multiselect_list) {
      fetchData.deleteTransaction(dao.getTransactionId());
    }
    loadData(this.mSpAccount.getSelectedItem().toString());
    if (this.mActionMode != null) {
      this.mActionMode.finish();
    }
  }
}
