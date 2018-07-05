package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.ParentAdapter;
import com.adslinfotech.mobileaccounting.dao.SearchDao;
import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends ParentAdapter {
  private int index;
  private final LayoutInflater mInflater;

  private class ViewHolder {
    TextView tvDesc;
    TextView tvSubject;

    private ViewHolder() {
    }

    public void setData(int position) {
      SearchDao dao = (SearchDao) SearchListAdapter.this.mFilteredFile.get(position);
      switch (position) {
        case 0:
          if (SearchListAdapter.this.index == 0) {
            this.tvSubject.setText(R.string.spinner_title);
            return;
          } else {
            this.tvSubject.setText(R.string.spinner_Acc_type);
            return;
          }
        default:
          this.tvSubject.setText(dao.getName());
          return;
      }
    }
  }

  public SearchListAdapter(Context context, ArrayList reminders, int i) {
    this.mInflater = LayoutInflater.from(context);
    this.mFileList = reminders;
    this.mFilteredFile = reminders;
    this.index = i;
  }

  public List<SearchDao> getList() {
    return this.mFileList;
  }

  public List<SearchDao> getFilteredResults() {
    return this.mFilteredFile;
  }

  public int getCount() {
    return this.mFilteredFile.size();
  }

  public Object getItem(int pos) {
    return ((SearchDao) this.mFilteredFile.get(pos)).getName();
  }

  public long getItemId(int position) {
    return (long) position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    View view = convertView;
    switch (position) {
      case 0:
        view = this.mInflater.inflate(R.layout.list_row_account_first, parent, false);
        holder = new ViewHolder();
        holder.tvSubject = (TextView) view.findViewById(R.id.tv_title);
        holder.tvDesc = (TextView) view.findViewById(R.id.tv_title);
        view.setTag(Integer.valueOf(0));
        break;
      default:
        if (convertView != null && !convertView.getTag().equals(Integer.valueOf(0))) {
          holder = (ViewHolder) view.getTag();
          break;
        }
        view = this.mInflater.inflate(R.layout.list_row_account, parent, false);
        holder = new ViewHolder();
        view.findViewById(R.id.img_acc).setVisibility(View.GONE);
        view.findViewById(R.id.tv_acc_category).setVisibility(View.GONE);
        holder.tvSubject = (TextView) view.findViewById(R.id.tv_rmd_sub);
        holder.tvDesc = (TextView) view.findViewById(R.id.tv_rmd_desc);
        view.setTag(holder);
        break;
    }
    holder.setData(position);
    return view;
  }

  public List<SearchDao> getFilteredResults(CharSequence mConstraint) {
    List<SearchDao> filteredComplains = new ArrayList();
    if (mConstraint != null) {
      int len = this.mFileList.size();
      for (int i = 0; i < len; i++) {
        SearchDao complain = (SearchDao) this.mFileList.get(i);
        if (i < 2) {
          filteredComplains.add(complain);
        } else if (complain.getName().toLowerCase().contains(mConstraint.toString().toLowerCase())) {
          filteredComplains.add(filteredComplains.size(), complain);
        }
      }
    }
    return filteredComplains;
  }
}
