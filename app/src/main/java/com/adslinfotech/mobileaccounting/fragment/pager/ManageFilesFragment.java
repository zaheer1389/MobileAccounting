package com.adslinfotech.mobileaccounting.fragment.pager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.liveo.adapter.ViewPagerAdapter;
import br.liveo.sliding.SamplePagerItem;
import br.liveo.sliding.SlidingTabLayout;
import br.liveo.sliding.SlidingTabLayout.TabColorizer;
import br.liveo.utils.Utils;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.fragment.manage.BackupListFragment;
import com.adslinfotech.mobileaccounting.fragment.manage.ExcelListFragment;
import com.adslinfotech.mobileaccounting.fragment.manage.PDFListFragment;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ManageFilesFragment extends BaseFragment {
  private ViewPagerAdapter adapter;
  private List<SamplePagerItem> mTabs = new ArrayList();
  private ViewPager mViewPager;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fragment[] listFragments = new Fragment[]{BackupListFragment.newInstance(), PDFListFragment.newInstance(), ExcelListFragment.newInstance()};
    this.mTabs.add(new SamplePagerItem(0, getResources().getString(R.string.txt_BackupFile), getResources().getColor(Utils.colors[0]), Color.GRAY, listFragments));
    this.mTabs.add(new SamplePagerItem(1, getResources().getString(R.string.txt_PdfFile), getResources().getColor(Utils.colors[2]), Color.GRAY, listFragments));
    this.mTabs.add(new SamplePagerItem(2, getResources().getString(R.string.txt_ExcelFile), getResources().getColor(Utils.colors[3]), Color.GRAY, listFragments));
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.viewpager_fragment, container, false);
  }

  public void onViewCreated(View view, Bundle savedInstanceState) {
    this.mViewPager = (ViewPager) view.findViewById(R.id.mPager);
    this.mViewPager.setOffscreenPageLimit(2);
    this.adapter = new ViewPagerAdapter(getChildFragmentManager(), this.mTabs);
    this.mViewPager.setAdapter(this.adapter);
    final SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.mTabs);
    mSlidingTabLayout.setBackgroundResource(R.color.frag_back);
    mSlidingTabLayout.setViewPager(this.mViewPager);
    mSlidingTabLayout.setCustomTabColorizer(new TabColorizer() {
      public int getIndicatorColor(int position) {
        return ((SamplePagerItem) ManageFilesFragment.this.mTabs.get(position)).getIndicatorColor();
      }

      public int getDividerColor(int position) {
        return ((SamplePagerItem) ManageFilesFragment.this.mTabs.get(position)).getDividerColor();
      }
    });
    this.mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
      int currentPosition = 0;

      public void onPageSelected(int newPosition) {
        SessionManager.incrementInteractionCount();
        mSlidingTabLayout.listen.onPageSelected(newPosition);
        ((FragmentLifecycle) ManageFilesFragment.this.adapter.getItem(newPosition)).onResumeFragment(newPosition);
        ((FragmentLifecycle) ManageFilesFragment.this.adapter.getItem(this.currentPosition)).onPauseFragment();
        this.currentPosition = newPosition;
      }

      public void onPageScrolled(int arg0, float arg1, int arg2) {
        mSlidingTabLayout.listen.onPageScrolled(arg0, arg1, arg2);
      }

      public void onPageScrollStateChanged(int arg0) {
        mSlidingTabLayout.listen.onPageScrollStateChanged(arg0);
      }
    });
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public void textViewClicked(int pos) {
    switch (this.mViewPager.getCurrentItem()) {
      case 0:
        ((BackupListFragment) this.adapter.getItem(0)).textViewClicked(pos);
        return;
      case 1:
        ((PDFListFragment) this.adapter.getItem(1)).textViewClicked(pos);
        return;
      case 2:
        ((ExcelListFragment) this.adapter.getItem(2)).textViewClicked(pos);
        return;
      default:
        return;
    }
  }

  public void onPositiveClick(int v) {
    switch (this.mViewPager.getCurrentItem()) {
      case 0:
        ((BackupListFragment) this.adapter.getItem(0)).onPositiveClick(v);
        return;
      case 1:
        ((PDFListFragment) this.adapter.getItem(1)).onPositiveClick(v);
        return;
      case 2:
        ((ExcelListFragment) this.adapter.getItem(2)).onPositiveClick(v);
        return;
      default:
        return;
    }
  }

  public void onClick(View v) {
  }
}
