package com.adslinfotech.mobileaccounting.activities.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddAccount;
import com.adslinfotech.mobileaccounting.adapter.home.AccountTableAdapter;
import com.adslinfotech.mobileaccounting.database.FetchCursor;
import com.adslinfotech.mobileaccounting.database.Query;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.adslinfotech.mobileaccounting.utils.SimpleDividerItemDecoration;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;


public class AccountListFragment extends BaseFragment implements FragmentLifecycle {
  private int firstVisiblePosition;
  private AdView mAdView;
  private AccountTableAdapter mAdapter;
  private RecyclerView mListAccounts;

  public static AccountListFragment newInstance() {
    return new AccountListFragment();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_account_ledger, container, false);
    rootView.setLayoutParams(new LayoutParams(-1, -1));
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(getActivity());
      this.mAdView = (AdView) rootView.findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
    return rootView;
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setAdapter(view);
    setAdapter();
  }

  public void onDestroyView() {
    super.onDestroyView();
  }

  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    Log.e("AccountListFragment", "AccountListFragment setUserVisibleHint");
  }

  private void setAdapter(View view) {
    this.mListAccounts = (RecyclerView) view.findViewById(R.id.list);
    this.mListAccounts.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    this.mListAccounts.setHasFixedSize(true);
    this.mListAccounts.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu2, menu);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(((SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getActivity().getComponentName()));
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
          AccountListFragment.this.mAdapter.swapCursor(FetchCursor.getCursor(Query.getAccountHomeSearch(newText.trim().toLowerCase())));
        }
        return false;
      }
    });
    menuItem.setOnActionExpandListener(new OnActionExpandListener() {
      public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      public boolean onMenuItemActionCollapse(MenuItem item) {
        AccountListFragment.this.mAdapter.swapCursor(FetchCursor.getCursor(Query.ACCOUNT_LIST_HOME));
        return true;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(true);
    menu.findItem(R.id.menu_search).setVisible(true);
  }

  private void setAdapter() {
    Log.e("AccountListFragment", "AccountListFragment data");
    this.mAdapter = new AccountTableAdapter(getActivity(), FetchCursor.getCursor(Query.ACCOUNT_LIST_HOME));
    this.mListAccounts.setAdapter(this.mAdapter);
    scrollToPosition();
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add:
        startActivity(new Intent(getActivity(), ActivityAddAccount.class));
        break;
    }
    return true;
  }

  public void onClick(View v) {
  }

  public void onPauseFragment() {
  }

  public void onResumeFragment(int newPosition) {
  }

  private void scrollToPosition() {
    this.mListAccounts.scrollToPosition(this.firstVisiblePosition);
    new Handler().postDelayed(new Runnable() {
      public void run() {
        AccountListFragment.this.mListAccounts.scrollBy(0, 0);
      }
    }, 500);
  }

  private void savePosition() {
    this.firstVisiblePosition = this.mListAccounts.getChildAdapterPosition(this.mListAccounts.getChildAt(0));
  }

  public void onPause() {
    savePosition();
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (SessionManager.isRefreshAccountList()) {
      SessionManager.setRefreshAccountList(false);
      setAdapter();
    }
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
