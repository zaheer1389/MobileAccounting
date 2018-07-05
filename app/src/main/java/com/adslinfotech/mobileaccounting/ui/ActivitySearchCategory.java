package com.adslinfotech.mobileaccounting.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.CategoryLedgerActivity;
import com.adslinfotech.mobileaccounting.adapter.home.SearchListAdapter;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.ChooseDateActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ActivitySearchCategory extends SimpleAccountingActivity {
  private AdView mAdView;
  private SearchListAdapter mAdapter;
  private ArrayList<Category> mCategories;
  private ListView mlListReminders;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.frag_list);
    getSupportActionBar().setTitle(getResources().getString(R.string.txt_ChooseCategory));
    getViews();
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  public void onResume() {
    super.onResume();
    setAdapter();
  }

  private void setAdapter() {
    this.mCategories = new FetchData().getAllCategory();
    Category category = new Category();
    category.setName("All");
    this.mCategories.add(0, category);
    this.mCategories.add(0, new Category());
    this.mAdapter = new SearchListAdapter(this, this.mCategories, 1);
    this.mlListReminders.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
  }

  private void getViews() {
    this.mlListReminders = (ListView) findViewById(android.R.id.list);
    this.mlListReminders.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> view, View arg1, int pos, long arg3) {
        switch (pos) {
          case 0:
            return;
          default:
            ActivitySearchCategory.this.showReport((String) view.getItemAtPosition(pos));
            return;
        }
      }
    });
  }

  private void showReport(String category) {
    Intent intent;
    switch (getIntent().getExtras().getInt("index")) {
      case 2:
        intent = new Intent(this, ChooseDateActivity.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, category);
        intent.putExtra("index", 2);
        startActivity(intent);
        return;
      case 6:
        intent = new Intent(this, CategoryLedgerActivity.class);
        intent.putExtra(AppConstants.ACCOUNT_SELECTED, category);
        startActivity(intent);
        return;
      default:
        return;
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu2, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (newText != null) {
          ActivitySearchCategory.this.mAdapter.getFilter().filter(newText.toString().trim());
        }
        return false;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(false);
    menu.findItem(R.id.menu_search).setVisible(true);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        finish();
        break;
    }
    return true;
  }

  public void onClick(View v) {
  }
}
