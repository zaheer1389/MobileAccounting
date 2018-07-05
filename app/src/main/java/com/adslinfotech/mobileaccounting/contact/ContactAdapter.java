package com.adslinfotech.mobileaccounting.contact;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.ParentAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactAdapter extends ParentAdapter implements Filterable {
    private final LayoutInflater mInflater;

    public class ViewHolder {
        public TextView tvPhoneNo;
        public TextView tvname;

        public void setData(int position) {
            ContactBean fileDao = (ContactBean) ContactAdapter.this.mFilteredFile.get(position);
            this.tvname.setText(Html.fromHtml(fileDao.getName()));
            this.tvPhoneNo.setText(Html.fromHtml(fileDao.getPhoneNo()));
        }
    }

    public ContactAdapter(Context context, ArrayList<ContactBean> fileList) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mFileList = fileList;
        this.mFilteredFile = fileList;
    }

    public List<ContactBean> getList() {
        return this.mFileList;
    }

    public List<ContactBean> getFilteredResults() {
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
            view = this.mInflater.inflate(R.layout.contact_row, parent, false);
            holder = new ViewHolder();
            holder.tvname = (TextView) view.findViewById(R.id.tvname);
            holder.tvPhoneNo = (TextView) view.findViewById(R.id.tvphone);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.setData(position);
        return view;
    }

    public List<ContactBean> getFilteredResults(CharSequence mConstraint) {
        if (mConstraint == null && TextUtils.isEmpty(mConstraint)) {
            return this.mFileList;
        }
        List<ContactBean> filteredComplains = new ArrayList();
        Iterator it = ((ArrayList) this.mFileList).iterator();
        while (it.hasNext()) {
            ContactBean bean = (ContactBean) it.next();
            if (bean.getName().toLowerCase().contains(mConstraint.toString().toLowerCase()) || bean.getPhoneNo().toLowerCase().contains(mConstraint.toString().toLowerCase())) {
                filteredComplains.add(filteredComplains.size(), bean);
            }
        }
        return filteredComplains;
    }
}
