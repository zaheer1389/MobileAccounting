package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GlanceAdapter extends BaseAdapter {
  private WeakReference<Context> mContext;
  private final LayoutInflater mInflater;
  private ArrayList<Transaction> mList;

  private class ViewHolder {
    double balance;
    String balance_amount;
    TextView tvAmount;
    TextView tvName;

    private ViewHolder() {
    }

    private void setData(int pos) {
      String mRsSymbol = SessionManager.getCurrency((Context) GlanceAdapter.this.mContext.get());
      NumberFormat newFormat = new DecimalFormat(((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("en", "IN"))).toPattern().replace("¤", "").trim());
      Transaction dao = (Transaction) GlanceAdapter.this.mList.get(pos);
      this.tvName.setText(dao.getAccName());
      if (dao.getDebitAmount() > dao.getCraditAmount()) {
        this.balance = dao.getDebitAmount() - dao.getCraditAmount();
        this.balance_amount = mRsSymbol + "" + newFormat.format(this.balance) + "/-";
        this.tvAmount.setText(this.balance_amount + " Dr");
        return;
      }
      this.balance = dao.getCraditAmount() - dao.getDebitAmount();
      this.balance_amount = mRsSymbol + "" + newFormat.format(this.balance) + "/-";
      this.tvAmount.setText(this.balance_amount + " Cr");
    }
  }

  public GlanceAdapter(Context context, ArrayList<Transaction> list) {
    this.mContext = new WeakReference(context);
    this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.mList = list;
  }

  public int getCount() {
    return this.mList.size();
  }

  public Transaction getItem(int position) {
    return (Transaction) this.mList.get(position);
  }

  public long getItemId(int position) {
    return (long) position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    ViewHolder holder = new ViewHolder();
    if (convertView == null) {
      view = this.mInflater.inflate(R.layout.transaction_adapter, parent, false);
      holder = new ViewHolder();
      holder.tvName = (TextView) view.findViewById(R.id.item_accname);
      holder.tvAmount = (TextView) view.findViewById(R.id.item_amount);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.setData(position);
    return view;
  }
}
