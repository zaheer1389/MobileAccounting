package br.liveo.sliding;

import android.support.v4.app.Fragment;

public class SamplePagerItem
{
  private Fragment[] listFragments;
  private final int mDividerColor;
  private final int mIndicatorColor;
  private final CharSequence mTitle;
  private final int position;
  
  public SamplePagerItem(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3, Fragment[] paramArrayOfFragment)
  {
    this.mTitle = paramCharSequence;
    this.position = paramInt1;
    this.mIndicatorColor = paramInt2;
    this.mDividerColor = paramInt3;
    this.listFragments = paramArrayOfFragment;
  }
  
  public Fragment createFragment()
  {
    return this.listFragments[this.position];
  }
  
  public int getDividerColor()
  {
    return this.mDividerColor;
  }
  
  public int getIndicatorColor()
  {
    return this.mIndicatorColor;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/br/liveo/sliding/SamplePagerItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */