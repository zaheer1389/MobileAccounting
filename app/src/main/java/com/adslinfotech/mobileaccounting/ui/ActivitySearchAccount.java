package com.adslinfotech.mobileaccounting.ui;

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.AccountLedgerActivity;
import com.adslinfotech.mobileaccounting.activities.report.OverallCategoryActivity;
import com.adslinfotech.mobileaccounting.adapter.CallLogAdapter;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.ChooseDateActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.util.ArrayList;

public class ActivitySearchAccount extends SimpleAccountingActivity implements TextWatcher, OnItemClickListener {
  private ArrayList<Account> accounts = new ArrayList();
  private AdView mAdView;
  private CallLogAdapter mAdapter;
  private EditText mEtSearch;
  private ListView mListAccount;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (openAndQueryDatabase()) {
      setContentView((int) R.layout.activity_searchaccount);
      getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_ledger_list));
      this.mListAccount = (ListView) findViewById(R.id.list_account);
      this.mEtSearch = (EditText) findViewById(R.id.et_search_complain);
      this.mEtSearch.addTextChangedListener(this);
      this.mListAccount.setOnItemClickListener(this);
      displayResultList();
      this.mEtSearch.getBackground().setColorFilter(getResources().getColor(R.color.white), Mode.SRC_ATOP);
      boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
      if (!SessionManager.isProUser() && isInternetPresent) {
        this.mAdView = new AdView(this);
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.setVisibility(View.VISIBLE);
        this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
      }
    }
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok_alert_positive:
        super.onClick(v);
        finish();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void displayResultList() {
    this.mAdapter = new CallLogAdapter(getApplicationContext(), this.accounts);
    this.mListAccount.setAdapter(this.mAdapter);
    this.mEtSearch.setText(SimpleAccountingApp.getPreference().getString(SessionManager.LAST_SEARCH, ""));
  }

  private boolean openAndQueryDatabase() {
    this.accounts = new FetchData().getAllAccounts(true, false);
    if (this.accounts.size() != 0) {
      return true;
    }
    showPositiveAlert(getResources().getString(R.string.txt_error), getResources().getString(R.string.NO_Account_Error));
    return false;
  }

  public void afterTextChanged(Editable s) {
    if (s != null) {
      String str = s.toString().trim();
      SimpleAccountingApp.getPreference().edit().putString(SessionManager.LAST_SEARCH, str).apply();
      this.mAdapter.getFilter().filter(str.toLowerCase());
    }
  }

  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  public void onItemClick(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
    Intent intent = null;
    Account dao = (Account) this.mAdapter.getItem(pos);
    switch (getIntent().getExtras().getInt(AppConstants.ACTIVITY_NAME, 0)) {
      case 1:
        if (getIntent().getExtras().getBoolean(AppConstants.ACCOUNT_REPORT)) {
          switch (getIntent().getExtras().getInt("index")) {
            case 0:
              intent = new Intent(this, ChooseDateActivity.class);
              intent.putExtra("index", 0);
              break;
            case 1:
              intent = new Intent(this, OverallCategoryActivity.class);
              break;
          }
          intent.putExtra(AppConstants.ACCOUNT_SELECTED, dao);
          startActivity(intent);
          finish();
          return;
        }
        intent = new Intent(this, AccountLedgerActivity.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, dao);
        startActivity(intent);
        finish();
        return;
      case 2:
        intent = new Intent();
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, dao);
        setResult(-1, intent);
        finish();
        return;
      default:
        return;
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
    super.onDestroy();
  }
}
