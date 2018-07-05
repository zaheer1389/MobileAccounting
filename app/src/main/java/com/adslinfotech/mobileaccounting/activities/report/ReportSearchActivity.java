package com.adslinfotech.mobileaccounting.activities.report;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEdit;
import com.adslinfotech.mobileaccounting.calculator.Calculator;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.ui.SessionManager;

import java.util.List;

public abstract class ReportSearchActivity extends ActivityEdit implements OnQueryTextListener {
  protected abstract void exportPdf(int i);

  protected abstract List<Transaction> filter(List<Transaction> list, String str);

  protected abstract void search(String str);

  protected abstract void searchComplete();

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_export, menu);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(this);
    menuItem.setOnActionExpandListener(new OnActionExpandListener() {
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      public boolean onMenuItemActionCollapse(MenuItem item) {
        ReportSearchActivity.this.searchComplete();
        return true;
      }
    });
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    SessionManager.incrementInteractionCount();
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        searchComplete();
        return super.onOptionsItemSelected(item);
      case R.id.menu_export_exel:
        exportPdf(1);
        return false;
      case R.id.menu_export_pdf:
        exportPdf(0);
        return false;
      case R.id.menu_usecal:
        try {
          Intent i = new Intent();
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

  public boolean onQueryTextChange(String newText) {
    search(newText);
    return true;
  }

  public boolean onQueryTextSubmit(String query) {
    Log.e("onQueryTextSubmit", "query: " + query);
    return true;
  }
}
