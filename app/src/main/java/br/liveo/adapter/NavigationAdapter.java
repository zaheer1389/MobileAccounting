package br.liveo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import java.util.HashSet;

public class NavigationAdapter extends ArrayAdapter<NavigationItemAdapter> {
  private HashSet<Integer> checkedItems = new HashSet();
  private ViewHolder holder;

  public static class ViewHolder {
    public final TextView counter;
    public final ImageView icon;
    public final TextView title;
    public final View viewNavigation;

    public ViewHolder(TextView title, TextView counter, ImageView icon, View viewNavigation) {
      this.title = title;
      this.counter = counter;
      this.icon = icon;
      this.viewNavigation = viewNavigation;
    }
  }

  public NavigationAdapter(Context context) {
    super(context, 0);
  }

  public void addItem(NavigationItemAdapter itemModel) {
    add(itemModel);
  }

  public int getViewTypeCount() {
    return 2;
  }

  public int getItemViewType(int position) {
    return ((NavigationItemAdapter) getItem(position)).isHeader ? 0 : 1;
  }

  public boolean isEnabled(int position) {
    return !((NavigationItemAdapter) getItem(position)).isHeader;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    this.holder = null;
    View view = convertView;
    NavigationItemAdapter item = (NavigationItemAdapter) getItem(position);
    if (view == null) {
      int layout = R.layout.navigation_item_counter;
      if (item.isHeader) {
        layout = R.layout.navigation_header_title;
      }
      view = LayoutInflater.from(getContext()).inflate(layout, null);
      view.setTag(new ViewHolder((TextView) view.findViewById(R.id.title), (TextView) view.findViewById(R.id.counter), (ImageView) view.findViewById(R.id.icon), view.findViewById(R.id.viewNavigation)));
    }
    if (this.holder == null && view != null) {
      Object tag = view.getTag();
      if (tag instanceof ViewHolder) {
        this.holder = (ViewHolder) tag;
      }
    }
    if (!(item == null || this.holder == null)) {
      if (this.holder.title != null) {
        this.holder.title.setText(item.title);
        this.holder.title.setTextSize(20.0f);
      }
      if (this.holder.counter != null) {
        if (item.counter > 0) {
          this.holder.counter.setVisibility(View.VISIBLE);
        } else {
          this.holder.counter.setVisibility(View.GONE);
        }
      }
      if (this.holder.icon != null) {
        if (item.icon != 0) {
          this.holder.title.setTextSize(14.0f);
          this.holder.icon.setVisibility(View.VISIBLE);
          this.holder.icon.setImageResource(item.icon);
        } else {
          this.holder.title.setTextSize(16.0f);
          this.holder.icon.setVisibility(View.GONE);
        }
      }
    }
    this.holder.viewNavigation.setVisibility(View.GONE);
    if (!item.isHeader && item.icon == 0) {
      if (this.checkedItems.contains(Integer.valueOf(position))) {
        this.holder.title.setTypeface(null, 1);
        this.holder.viewNavigation.setVisibility(View.VISIBLE);
      } else {
        this.holder.title.setTypeface(null, 0);
      }
    }
    view.setBackgroundResource(R.drawable.seletor_item_navigation);
    return view;
  }
}
