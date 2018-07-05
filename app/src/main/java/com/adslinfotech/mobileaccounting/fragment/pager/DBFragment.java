package com.adslinfotech.mobileaccounting.fragment.pager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.liveo.adapter.ViewPagerAdapter;
import br.liveo.sliding.SamplePagerItem;
import br.liveo.sliding.SlidingTabLayout;
import br.liveo.sliding.SlidingTabLayout.TabColorizer;
import br.liveo.utils.Utils;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DbActivity;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.db.DataBaseExport;
import com.adslinfotech.mobileaccounting.fragment.db.DataBaseImport;
import java.util.ArrayList;
import java.util.List;

public class DBFragment extends BaseFragment {
  private ViewPagerAdapter mAdapter;
  private int mSelection = 0;
  private List<SamplePagerItem> mTabs = new ArrayList();
  private ViewPager mViewPager;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fragment[] listFragments = new Fragment[]{DataBaseExport.newInstance(), DataBaseImport.newInstance()};
    this.mTabs.add(new SamplePagerItem(0, getResources().getString(R.string.txt_bac), getResources().getColor(Utils.colors[0]), Color.GRAY, listFragments));
    this.mTabs.add(new SamplePagerItem(1, getResources().getString(R.string.txt_restore), getResources().getColor(Utils.colors[2]), Color.GRAY, listFragments));
    Bundle bundle = getArguments();
    if (bundle != null) {
      this.mSelection = bundle.getInt(DbActivity.EXTRA_KEY_SELECTED_TAB, 0);
    }
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.viewpager_fragment, container, false);
  }

  public void onViewCreated(View view, Bundle savedInstanceState) {
    this.mViewPager = (ViewPager) view.findViewById(R.id.mPager);
    this.mViewPager.setOffscreenPageLimit(3);
    this.mAdapter = new ViewPagerAdapter(getChildFragmentManager(), this.mTabs);
    this.mViewPager.setAdapter(this.mAdapter);
    SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mTabs);
    mSlidingTabLayout.setBackgroundResource(R.color.frag_back);
    mSlidingTabLayout.setViewPager(this.mViewPager);
    mSlidingTabLayout.setCustomTabColorizer(new TabColorizer() {
      public int getIndicatorColor(int position) {
        return ((SamplePagerItem) DBFragment.this.mTabs.get(position)).getIndicatorColor();
      }

      public int getDividerColor(int position) {
        return ((SamplePagerItem) DBFragment.this.mTabs.get(position)).getDividerColor();
      }
    });
    this.mViewPager.setCurrentItem(this.mSelection);
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(false);
  }

  public void onClick(View v) {
    switch (this.mViewPager.getCurrentItem()) {
      case 0:
        ((DataBaseExport) this.mAdapter.getItem(0)).onClick(v);
        return;
      case 1:
        ((DataBaseImport) this.mAdapter.getItem(1)).onClick(v);
        return;
      default:
        return;
    }
  }

  public void onPositiveClick(int from) {
    ((DataBaseExport) this.mAdapter.getItem(0)).onPositiveClick(from);
  }
}
