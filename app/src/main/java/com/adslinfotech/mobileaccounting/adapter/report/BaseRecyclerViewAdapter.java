package com.adslinfotech.mobileaccounting.adapter.report;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter extends Adapter<ViewHolder> {
  private SparseBooleanArray selectedItems;

  public void toggleSelection(int pos) {
    if (this.selectedItems.get(pos, false)) {
      this.selectedItems.delete(pos);
    } else {
      this.selectedItems.put(pos, true);
    }
    notifyItemChanged(pos);
  }

  public void clearSelections() {
    this.selectedItems.clear();
    notifyDataSetChanged();
  }

  public int getSelectedItemCount() {
    return this.selectedItems.size();
  }

  public List<Integer> getSelectedItems() {
    List<Integer> items = new ArrayList(this.selectedItems.size());
    for (int i = 0; i < this.selectedItems.size(); i++) {
      items.add(Integer.valueOf(this.selectedItems.keyAt(i)));
    }
    return items;
  }
}
