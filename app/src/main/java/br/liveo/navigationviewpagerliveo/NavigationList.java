package br.liveo.navigationviewpagerliveo;

import android.content.Context;
import br.liveo.adapter.NavigationAdapter;
import br.liveo.adapter.NavigationItemAdapter;
import br.liveo.utils.Utils;

public class NavigationList {
  public static NavigationAdapter getNavigationAdapter(Context context, int rId, boolean isProfile) {
    NavigationAdapter navigationAdapter = new NavigationAdapter(context);
    String[] menuItems = context.getResources().getStringArray(rId);
    for (int i = 0; i < menuItems.length; i++) {
      navigationAdapter.addItem(new NavigationItemAdapter(menuItems[i], Utils.getIcon(isProfile, i)));
    }
    return navigationAdapter;
  }
}
