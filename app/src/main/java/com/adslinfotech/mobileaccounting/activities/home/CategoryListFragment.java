package com.adslinfotech.mobileaccounting.activities.home;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.home.CategoryListAdapter;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class CategoryListFragment extends BaseFragment implements FragmentLifecycle {
  private AdView mAdView;
  private CategoryListAdapter mAdapter;
  private ArrayList<Category> mCategories;
  private ListView mlListReminders;

  public static CategoryListFragment newInstance() {
    return new CategoryListFragment();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.frag_list, container, false);
    rootView.setLayoutParams(new LayoutParams(-1, -1));
    getViews(rootView);
    View header = inflater.inflate(R.layout.list_row_account_first, null, false);
    this.mlListReminders.addHeaderView(header);
    header.setOnClickListener(new OnClickListener() {
      public void onClick(View arg0) {
        CategoryListFragment.this.rename(new Category(), false);
      }
    });
    ((TextView) header.findViewById(R.id.tv_title)).setText(getString(R.string.txt_addcategory));
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(getActivity());
      this.mAdView = (AdView) rootView.findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
    return rootView;
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu2, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setSearchableInfo(((SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getActivity().getComponentName()));
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (newText != null) {
          CategoryListFragment.this.mAdapter.getFilter().filter(newText.toString().trim());
        }
        return false;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(true);
    menu.findItem(R.id.menu_search).setVisible(true);
  }

  private void setAdapter() {
    this.mCategories = new FetchData().getAllCategory();
    Log.e("CategoryListFrag", "mCategories: " + this.mCategories.size());
    if (this.mCategories.size() < 1) {
      View header = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_row_account_first, null, false);
      this.mlListReminders.addHeaderView(header);
      header.setOnClickListener(new OnClickListener() {
        public void onClick(View arg0) {
        }
      });
      ((TextView) header.findViewById(R.id.tv_title)).setText(getString(R.string.txt_addcategory));
    }
    this.mAdapter = new CategoryListAdapter(getActivity(), this.mCategories);
    this.mlListReminders.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
  }

  private void getViews(View view) {
    this.mlListReminders = (ListView) view.findViewById(android.R.id.list);
    this.mlListReminders.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> view, View arg1, int pos, long arg3) {
        CategoryListFragment.this.rename((Category) view.getItemAtPosition(pos), true);
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add:
        rename(new Category(), false);
        break;
    }
    return true;
  }

  public void btnAddCategory(String rename) {
    addCategory(rename);
    setAdapter();
  }

  public void btnDeleteCategory(Category category) {
    deleteCategory(category);
    setAdapter();
  }

  public void btUpdateCategory(Category category, String rename) {
    updateCategory(category, rename);
    setAdapter();
  }

  public void rename(final Category category, final boolean isDelete) {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    final EditText input = new EditText(getActivity());
    if (isDelete) {
      alert.setTitle("Edit Category");
      input.setText("" + category.getName());
    } else {
      alert.setTitle("Add Category");
    }
    alert.setMessage(getString(R.string.txt_EnterNewName));
    alert.setView(input);
    if (isDelete) {
      alert.setPositiveButton(getString(R.string.txt_Delete), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          CategoryListFragment.this.btnDeleteCategory(category);
        }
      });
    }
    alert.setNegativeButton(getString(R.string.btn_Cancel), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });
    alert.setNeutralButton(getString(R.string.btn_Ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        String rename = input.getText().toString();
        if (isDelete) {
          CategoryListFragment.this.btUpdateCategory(category, rename);
        } else if (TextUtils.isEmpty(rename)) {
          Toast.makeText(CategoryListFragment.this.getActivity(), CategoryListFragment.this.getString(R.string.txt_EnterCategory), Toast.LENGTH_SHORT).show();
        } else {
          CategoryListFragment.this.btnAddCategory(rename);
        }
      }
    });
    alert.show();
  }

  private void addCategory(String category) {
    new FetchData().insertCategory(category);
  }

  private void deleteCategory(Category category) {
    if (category.getId() < 8) {
      Toast.makeText(getActivity(), getString(R.string.txt_CategoryCantDelete), Toast.LENGTH_SHORT).show();
    } else if (new FetchData().deleteCategory(category) == 0) {
      Toast.makeText(getActivity(), getString(R.string.txt_CantDelete), Toast.LENGTH_SHORT).show();
    } else {
      setAdapter();
    }
  }

  private void updateCategory(Category category, String rename) {
    if (category.getId() < 8) {
      Toast.makeText(getActivity(), getString(R.string.txt_CategoryCantUpdate), Toast.LENGTH_SHORT).show();
      return;
    }
    category.setName(rename);
    new FetchData().updateCategory(category);
    setAdapter();
  }

  public void onClick(View v) {
  }

  public void onPauseFragment() {
    try {
      this.mAdapter.getFilter().filter("");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onResumeFragment(int newPosition) {
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    setAdapter();
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
