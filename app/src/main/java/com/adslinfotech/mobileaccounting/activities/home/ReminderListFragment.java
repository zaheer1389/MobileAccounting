package com.adslinfotech.mobileaccounting.activities.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddReminder;
import com.adslinfotech.mobileaccounting.adapter.home.ReminderTableAdapter;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.adslinfotech.mobileaccounting.utils.SimpleDividerItemDecoration;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReminderListFragment extends BaseFragment implements FragmentLifecycle {
  private AdView mAdView;
  private ReminderTableAdapter mAdapter;
  private RecyclerView mListReminders;
  private ArrayList<Reminder> mReminders;

  public static ReminderListFragment newInstance() {
    return new ReminderListFragment();
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
  }

  private void setAdapter(View view) {
    this.mListReminders = (RecyclerView) view.findViewById(R.id.list);
    this.mListReminders.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    this.mListReminders.setItemAnimator(new DefaultItemAnimator());
    this.mListReminders.setHasFixedSize(true);
    this.mListReminders.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
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
          newText = newText.trim().toLowerCase();
          List<Reminder> accounts = new ArrayList();
          Iterator it = ReminderListFragment.this.mReminders.iterator();
          while (it.hasNext()) {
            Reminder reminder = (Reminder) it.next();
            if ((reminder.getDescription().toLowerCase() + "." + ActivityAddReminder.accType[reminder.getRmdType()].toLowerCase() + "." + reminder.getDate().toLowerCase()).contains(newText)) {
              accounts.add(reminder);
            }
          }
          ReminderListFragment.this.mAdapter.filter(accounts);
        }
        return false;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(true);
    menu.findItem(R.id.menu_search).setVisible(true);
  }

  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    Log.e("ReminderListFragment", "ReminderListFragment setUserVisibleHint");
  }

  private void setAdapter() {
    Log.e("ReminderListFragment", "ReminderListFragment data");
    this.mReminders = new FetchData().getReminder(null);
    this.mAdapter = new ReminderTableAdapter(getActivity(), this.mReminders);
    this.mListReminders.setAdapter(this.mAdapter);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add:
        startActivity(new Intent(getActivity(), ActivityAddReminder.class));
        break;
    }
    return true;
  }

  public void onClick(View v) {
  }

  public void onPauseFragment() {
    try {
      this.mAdapter.filter(this.mReminders);
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
