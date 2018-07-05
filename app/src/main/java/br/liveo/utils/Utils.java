package br.liveo.utils;

public class Utils
{
  public static int[] colors = { 2131492891, 2131492891, 2131493019, 2131493020, 2131492968, 2131492970, 2131493006, 2131493007, 2131493016, 2131493017, 2131492891, 2131492891, 2131493019, 2131493020, 2131492968, 2131492970, 2131493006, 2131493007, 2131493016, 2131493017 };
  public static int[] iconNavigation1 = { 2130837659, 2130837638, 2130837646, 2130837637, 2130837579, 2130837579, 2130837684, 2130837685, 2130837644, 2130837643, 2130837643, 2130837674, 2130837673, 2130837698, 0, 0, 0 };
  public static int[] iconNavigation2 = { 2130837645, 2130837687, 2130837583, 2130837591, 2130837680, 2130837590, 2130837700, 2130837697, 2130837701, 2130837692, 2130837682, 2130837654, 2130837702, 2130837682, 0, 0 };
  
  public static int getIcon(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {
      return iconNavigation2[paramInt];
    }
    return iconNavigation1[paramInt];
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/br/liveo/utils/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */