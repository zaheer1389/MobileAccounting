package br.liveo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import br.liveo.sliding.SamplePagerItem;
import java.util.List;

public class ViewPagerAdapter
  extends FragmentPagerAdapter
{
  int currentPosition = 0;
  private List<SamplePagerItem> mTabs;
  
  public ViewPagerAdapter(FragmentManager paramFragmentManager, List<SamplePagerItem> paramList)
  {
    super(paramFragmentManager);
    this.mTabs = paramList;
  }
  
  public int getCount()
  {
    return this.mTabs.size();
  }
  
  public Fragment getItem(int paramInt)
  {
    return ((SamplePagerItem)this.mTabs.get(paramInt)).createFragment();
  }
  
  public CharSequence getPageTitle(int paramInt)
  {
    return ((SamplePagerItem)this.mTabs.get(paramInt)).getTitle();
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/br/liveo/adapter/ViewPagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */