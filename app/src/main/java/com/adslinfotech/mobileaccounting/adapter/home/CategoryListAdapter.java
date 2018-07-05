package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.ParentAdapter;
import com.adslinfotech.mobileaccounting.dao.Category;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends ParentAdapter {
  private WeakReference<Context> mContext;
  private final LayoutInflater mInflater;

  private class ViewHolder {
    TextView tvDesc;
    TextView tvSubject;

    private ViewHolder() {
    }

    public void setData(int position) {
      this.tvSubject.setText(((Category) CategoryListAdapter.this.mFilteredFile.get(position)).getName());
    }
  }

  public CategoryListAdapter(Context context, ArrayList<Category> reminders) {
    this.mContext = new WeakReference(context);
    this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.mFileList = reminders;
    this.mFilteredFile = reminders;
  }

  public List<Category> getList() {
    return this.mFileList;
  }

  public List<Category> getFilteredResults() {
    return this.mFilteredFile;
  }

  public int getCount() {
    return this.mFilteredFile.size();
  }

  public Object getItem(int pos) {
    return this.mFilteredFile.get(pos);
  }

  public long getItemId(int position) {
    return (long) position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    View view = convertView;
    if (convertView == null) {
      view = this.mInflater.inflate(R.layout.list_row_account, parent, false);
      holder = new ViewHolder();
      view.findViewById(R.id.img_acc).setVisibility(View.GONE);
      view.findViewById(R.id.tv_acc_category).setVisibility(View.GONE);
      holder.tvSubject = (TextView) view.findViewById(R.id.tv_rmd_sub);
      holder.tvDesc = (TextView) view.findViewById(R.id.tv_rmd_desc);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.setData(position);
    Animation animation = AnimationUtils.loadAnimation((Context) this.mContext.get(), R.anim.fade_in);
    animation.setDuration(500);
    view.startAnimation(animation);
    return view;
  }

  public List<Category> getFilteredResults(CharSequence mConstraint) {
    List<Category> filteredComplains = new ArrayList();
    if (mConstraint != null) {
      int len = this.mFileList.size();
      for (int i = 0; i < len; i++) {
        Category complain = (Category) this.mFileList.get(i);
        if (complain.getName().toLowerCase().contains(mConstraint.toString().toLowerCase())) {
          filteredComplains.add(filteredComplains.size(), complain);
        }
      }
    }
    return filteredComplains;
  }
}
