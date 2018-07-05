package com.adslinfotech.mobileaccounting.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.fragment.pager.DBFragment;

public class DbActivity extends SimpleAccountingActivity {
  public static final String EXTRA_KEY_SELECTED_TAB = "EXTRA_KEY_SELECTED_TAB";

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[0]);
    DBFragment fragment = new DBFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(EXTRA_KEY_SELECTED_TAB, getIntent().getIntExtra(EXTRA_KEY_SELECTED_TAB, 0));
    fragment.setArguments(bundle);
    getSupportFragmentManager().beginTransaction().replace(16908290, fragment).commit();
  }

  public void onPositiveClick(int from) {
    Fragment fragment = getSupportFragmentManager().findFragmentById(16908290);
    if (fragment instanceof DBFragment) {
      ((DBFragment) fragment).onPositiveClick(from);
    }
  }
}
