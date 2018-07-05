package br.liveo.adapter;

public class NavigationItemAdapter
{
  public int counter;
  public int icon;
  public boolean isHeader;
  public String title;
  
  public NavigationItemAdapter(String paramString, int paramInt)
  {
    this(paramString, paramInt, false);
  }
  
  public NavigationItemAdapter(String paramString, int paramInt, boolean paramBoolean)
  {
    this(paramString, paramInt, paramBoolean, 0);
  }
  
  public NavigationItemAdapter(String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    this.title = paramString;
    this.icon = paramInt1;
    this.isHeader = paramBoolean;
    this.counter = paramInt2;
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/br/liveo/adapter/NavigationItemAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */