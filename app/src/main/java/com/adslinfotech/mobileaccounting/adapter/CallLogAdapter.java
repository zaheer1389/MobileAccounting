package com.adslinfotech.mobileaccounting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallLogAdapter extends ParentAdapter {
  private Context mContext;
  private final LayoutInflater mInflater;

  private class ViewHolder {
    ImageView imgAccount;
    TextView tvCategory;
    TextView tvDesc;
    TextView tvSubject;

    private ViewHolder() {
    }

    public void setData(int position) {
      Account dao = (Account) CallLogAdapter.this.mFilteredFile.get(position);
      this.tvDesc.setText(dao.getEmail());
      this.tvCategory.setText(dao.getCategory());
      this.tvSubject.setText(dao.getName());
      if (AppUtils.setImage(this.imgAccount, dao.getImage())) {
        this.imgAccount.setImageDrawable(CallLogAdapter.this.mContext.getResources().getDrawable(R.drawable.profile_icon));
      }
    }
  }

  public CallLogAdapter(Context context, ArrayList<Account> fileList) {
    this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.mFileList = fileList;
    this.mFilteredFile = fileList;
    this.mContext = context;
  }

  public List<Account> getList() {
    return this.mFileList;
  }

  public List<Account> getFilteredResults() {
    return this.mFilteredFile;
  }

  public int getCount() {
    return this.mFilteredFile.size();
  }

  public Object getItem(int pos) {
    return this.mFilteredFile.get(pos);
  }

  public long getItemId(int id) {
    return (long) id;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    ViewHolder holder = new ViewHolder();
    if (convertView == null) {
      view = this.mInflater.inflate(R.layout.list_row_account, parent, false);
      holder = new ViewHolder();
      holder.tvSubject = (TextView) view.findViewById(R.id.tv_rmd_sub);
      holder.tvDesc = (TextView) view.findViewById(R.id.tv_rmd_desc);
      holder.tvCategory = (TextView) view.findViewById(R.id.tv_acc_category);
      holder.imgAccount = (ImageView) view.findViewById(R.id.img_acc);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.setData(position);
    return view;
  }

  public List<Account> getFilteredResults(CharSequence mConstraint) {
    List<Account> filteredComplains = new ArrayList();
    if (mConstraint != null) {
      Iterator it = this.mFileList.iterator();
      while (it.hasNext()) {
        Account complain = (Account) it.next();
        if (complain.getName().toLowerCase().contains(mConstraint) || complain.getEmail().toLowerCase().contains(mConstraint) || complain.getCategory().toLowerCase().contains(mConstraint)) {
          filteredComplains.add(filteredComplains.size(), complain);
        }
      }
    }
    return filteredComplains;
  }
}
