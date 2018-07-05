package com.adslinfotech.mobileaccounting.activities.db;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.db.ImportBackupAdapter;
import com.adslinfotech.mobileaccounting.adapter.db.ImportBackupAdapter.OnCategoryListListener;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.Backup;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportDbActivity extends SimpleAccountingActivity implements OnCategoryListListener, OnQueryTextListener {
  private static final String KEY_BACKUP_LIST = "KEY_BACKUP_LIST";
  private ImportBackupAdapter mAdapter;
  private ArrayList<Backup> mTransactions;

  public static void newInstance(Context context, ArrayList<Backup> transactions) {
    Intent intent = new Intent(context, ImportDbActivity.class);
    intent.putExtra(KEY_BACKUP_LIST, transactions);
    context.startActivity(intent);
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_account_ledger);
    this.mTransactions = (ArrayList) getIntent().getSerializableExtra(KEY_BACKUP_LIST);
    setAdapter();
  }

  private void setAdapter() {
    RecyclerView mListTransactions = (RecyclerView) findViewById(R.id.list);
    mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    mListTransactions.setItemAnimator(new DefaultItemAnimator());
    mListTransactions.setHasFixedSize(true);
    this.mAdapter = new ImportBackupAdapter(this, this.mTransactions);
    mListTransactions.setAdapter(this.mAdapter);
  }

  public void onItemClicked(Backup item) {
    Log.e("onItemClicked " + item.getName(), "path: " + item.getPath());
    setResult(-1);
    DatabaseExportImport.importDb(this, new File(item.getPath()));
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu3, menu);
    menu.removeItem(R.id.menu_delete);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(this);
    menuItem.setOnActionExpandListener(new OnActionExpandListener() {
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      public boolean onMenuItemActionCollapse(MenuItem item) {
        ImportDbActivity.this.searchComplete();
        return true;
      }
    });
    return true;
  }

  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  public boolean onQueryTextChange(String newText) {
    this.mAdapter.setFilter(filter(this.mTransactions, newText));
    return false;
  }

  private void searchComplete() {
    this.mAdapter.setFilter(this.mTransactions);
  }

  private List<Backup> filter(List<Backup> models, String newText) {
    newText = newText.toLowerCase();
    List<Backup> filteredModelList = new ArrayList();
    for (Backup model : models) {
      if (model.toString().toLowerCase().contains(newText)) {
        filteredModelList.add(model);
      }
    }
    return filteredModelList;
  }
}
