package com.adslinfotech.mobileaccounting.activities.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddNote;
import com.adslinfotech.mobileaccounting.adapter.home.NoteTableAdapter;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
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

public class NoteListFragment extends BaseFragment implements FragmentLifecycle {
  private AdView mAdView;
  private NoteTableAdapter mAdapter;
  private RecyclerView mListNotes;
  private ArrayList<NoteDao> mNotes;

  public static NoteListFragment newInstance() {
    return new NoteListFragment();
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
    this.mListNotes = (RecyclerView) view.findViewById(R.id.list);
    this.mListNotes.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    this.mListNotes.setHasFixedSize(true);
    this.mListNotes.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
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
          List<NoteDao> notes = new ArrayList();
          Iterator it = NoteListFragment.this.mNotes.iterator();
          while (it.hasNext()) {
            NoteDao account = (NoteDao) it.next();
            if ((account.getHeading().toLowerCase() + "." + account.getDescr().toLowerCase()).contains(newText)) {
              notes.add(account);
            }
          }
          NoteListFragment.this.mAdapter.filter(notes);
        }
        return false;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(true);
    menu.findItem(R.id.menu_search).setVisible(true);
  }

  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    Log.e("NoteListFragment", "NoteListFragment setUserVisibleHint");
  }

  private void setAdapter() {
    Log.e("NoteListFragment", "NoteListFragment data");
    this.mNotes = new FetchData().getAllNotes();
    this.mAdapter = new NoteTableAdapter(getActivity(), this.mNotes);
    this.mListNotes.setAdapter(this.mAdapter);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add:
        startActivity(new Intent(getActivity(), ActivityAddNote.class));
        break;
    }
    return true;
  }

  public void onClick(View v) {
  }

  public void onPauseFragment() {
    try {
      this.mAdapter.filter(this.mNotes);
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
