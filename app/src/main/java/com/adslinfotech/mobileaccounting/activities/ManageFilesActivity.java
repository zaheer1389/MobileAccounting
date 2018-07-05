package com.adslinfotech.mobileaccounting.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.home.PlanetArrayAdapter.TextViewClickedLister;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.fragment.pager.ManageFilesFragment;

public class ManageFilesActivity extends SimpleAccountingActivity implements TextViewClickedLister {
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[4]);
    getSupportFragmentManager().beginTransaction().replace(16908290, new ManageFilesFragment()).commit();
  }

  public void textViewClicked(int position) {
    ((ManageFilesFragment) getSupportFragmentManager().findFragmentById(16908290)).textViewClicked(position);
  }

  public void onClick(View v) {
    super.onClick(v);
    ((ManageFilesFragment) getSupportFragmentManager().findFragmentById(16908290)).onClick(v);
  }

  public void onPositiveClick(int from) {
    Fragment fragment = getSupportFragmentManager().findFragmentById(16908290);
    if (fragment instanceof ManageFilesFragment) {
      ((ManageFilesFragment) fragment).onPositiveClick(from);
    }
  }
}
