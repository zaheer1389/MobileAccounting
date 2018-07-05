package com.adslinfotech.mobileaccounting.fragment.manage;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.ManageFilesActivity;
import com.adslinfotech.mobileaccounting.adapter.home.PlanetArrayAdapter;
import com.adslinfotech.mobileaccounting.dao.Planet;
import com.adslinfotech.mobileaccounting.files.ActivityFileManager;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.drive.DriveFile;

import com.itextpdf.text.xml.xmp.PdfSchema;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PDFListFragment extends ListFragment implements FragmentLifecycle {
  private boolean isFirstTime = true;
  private AdView mAdView;
  private PlanetArrayAdapter mAdapter;
  private String mFileType = PdfSchema.DEFAULT_XPATH_ID;
  private Planet planet;
  List<Planet> songNameList = new ArrayList();
  private ArrayList<HashMap<String, String>> songsList = new ArrayList();
  private View view;

  public static PDFListFragment newInstance() {
    return new PDFListFragment();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_file_list, container, false);
    this.view = rootView;
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(getActivity());
      this.mAdView = (AdView) rootView.findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
    return rootView;
  }

  private void init() {
    this.mAdapter = new PlanetArrayAdapter(getActivity(), this.songNameList);
    getListView().setAdapter(this.mAdapter);
    getListView().setClickable(false);
    OnClickListener clickListener = new OnClickListener() {
      public void onClick(View v) {
        boolean isCheck = ((CheckBox) v).isChecked();
        int itemCount = PDFListFragment.this.getListView().getCount();
        for (int i = 0; i < itemCount; i++) {
          ((Planet) PDFListFragment.this.mAdapter.getItem(i)).setChecked(isCheck);
        }
        PDFListFragment.this.mAdapter.notifyDataSetChanged();
      }
    };
    OnItemClickListener itemClickListener = new OnItemClickListener() {
      public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
        CheckBox chk = (CheckBox) PDFListFragment.this.view.findViewById(R.id.chkAll);
        if (PDFListFragment.this.getListView().getCount() == PDFListFragment.this.getCheckedItemCount()) {
          chk.setChecked(true);
        } else {
          chk.setChecked(false);
        }
      }
    };
    ((CheckBox) this.view.findViewById(R.id.chkAll)).setOnClickListener(clickListener);
    getListView().setOnItemClickListener(itemClickListener);
  }

  private static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (String file : children) {
        if (!deleteDir(new File(dir, file))) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  private int getCheckedItemCount() {
    int cnt = 0;
    SparseBooleanArray positions = getListView().getCheckedItemPositions();
    int itemCount = getListView().getCount();
    for (int i = 0; i < itemCount; i++) {
      if (positions.get(i)) {
        cnt++;
      }
    }
    return cnt;
  }

  private void getSongName() {
    this.songNameList.clear();
    Iterator it = this.songsList.iterator();
    while (it.hasNext()) {
      HashMap<String, String> song = (HashMap) it.next();
      Planet planet = new Planet();
      planet.setName((String) song.get("songTitle"));
      planet.setPath((String) song.get("songPath"));
      this.songNameList.add(planet);
    }
    this.mAdapter.notifyDataSetChanged();
  }

  public void onPause() {
    super.onPause();
  }

  public void onDestroy() {
    super.onDestroy();
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu3, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setSearchableInfo(((SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getActivity().getComponentName()));
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (newText != null) {
          PDFListFragment.this.mAdapter.getFilter().filter(newText.toString().trim());
        }
        return false;
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_delete:
        ((ManageFilesActivity) getActivity()).showAlertExitApp(getString(R.string.msg_delete_files), 0);
        break;
    }
    return true;
  }

  public void onPositiveClick(int from) {
    deleteBackup();
  }

  private void deleteBackup() {
    boolean refresh = false;
    int itemCount = getListView().getCount();
    for (int i = 0; i < itemCount; i++) {
      Planet planet = (Planet) this.mAdapter.getItem(i);
      if (planet.isChecked()) {
        refresh = true;
        try {
          new File(planet.getPath()).delete();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (refresh) {
      this.songsList = new ActivityFileManager(this.mFileType).getPlayList();
      init();
      getSongName();
    }
  }

  public void onPauseFragment() {
  }

  public void onResume() {
    super.onResume();
    if (this.isFirstTime) {
      this.songsList = new ActivityFileManager(this.mFileType).getPlayList();
      init();
      getSongName();
    }
  }

  public Object onRetainNonConfigurationInstance() {
    return this.songNameList;
  }

  public void onResumeFragment(int pos) {
    if (this.isFirstTime && pos == 1) {
      this.isFirstTime = false;
      if (this.songNameList.size() == 0) {
        ((ManageFilesActivity) getActivity()).showPositiveAlert("Simple Accounting", getString(R.string.txt_NoFile));
      }
    }
  }

  public void textViewClicked(int pos) {
    this.planet = (Planet) this.mAdapter.getItem(pos);
    openPDF();
  }

  private void openPDF() {
    File file = new File(this.planet.getPath());
    Intent target = new Intent("android.intent.action.VIEW");
    target.setDataAndType(Uri.fromFile(file), "application/pdf");
    Intent intent = Intent.createChooser(target, "Open File");
    try {
      intent.addFlags(DriveFile.MODE_READ_ONLY);
      startActivity(intent);
    } catch (ActivityNotFoundException e) {
    }
  }
}
