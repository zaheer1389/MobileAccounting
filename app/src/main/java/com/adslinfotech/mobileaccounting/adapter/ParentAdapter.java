package com.adslinfotech.mobileaccounting.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.List;

public class ParentAdapter extends BaseAdapter implements Filterable {
  protected List mFileList;
  protected List mFilteredFile;

  private class CustomFilter extends Filter {
    private CustomFilter() {
    }

    protected void publishResults(CharSequence constraint, FilterResults results) {
      ParentAdapter.this.mFilteredFile = (List) results.values;
      ParentAdapter.this.notifyDataSetChanged();
    }

    protected FilterResults performFiltering(CharSequence constraint) {
      List filteredResults = ParentAdapter.this.getFilteredResults(constraint);
      FilterResults results = new FilterResults();
      results.values = filteredResults;
      return results;
    }
  }

  public int getCount() {
    return 0;
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    return null;
  }

  public Filter getFilter() {
    return new CustomFilter();
  }

  public List getFilteredResults(CharSequence constraint) {
    return this.mFilteredFile;
  }
}
