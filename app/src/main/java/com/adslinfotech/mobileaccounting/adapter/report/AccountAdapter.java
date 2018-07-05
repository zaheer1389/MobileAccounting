package com.adslinfotech.mobileaccounting.adapter.report;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends Adapter<ViewHolder> {
  public List<Account> mAccounts;
  private Context mContext;
  public List<Account> selected_usersList = new ArrayList();

  public interface OnAccountListListener {
    void onItemClicked(Account account);

    void onItemLongClicked(Account account);
  }

  public class NormalViewHolder extends ViewHolder {
    public Account account;
    public View mLayout;
    public final TextView tvDate;
    public final TextView tvDays;
    public final TextView tvDaysTitle;
    public final TextView tvDesc;
    public final TextView tvDescTitle;
    public final TextView tvTitle;
    public final TextView tvType;

    public NormalViewHolder(View view) {
      super(view);
      this.mLayout = view.findViewById(R.id.ll_top);
      this.tvTitle = (TextView) view.findViewById(R.id.txt_date);
      this.tvDate = (TextView) view.findViewById(R.id.txt_balance_title);
      this.tvType = (TextView) view.findViewById(R.id.txt_balance);
      this.tvDaysTitle = (TextView) view.findViewById(R.id.txt_credit_title);
      this.tvDays = (TextView) view.findViewById(R.id.txt_credit);
      this.tvDescTitle = (TextView) view.findViewById(R.id.txt_debit_title);
      this.tvDesc = (TextView) view.findViewById(R.id.txt_debit);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          ((OnAccountListListener) AccountAdapter.this.mContext).onItemClicked(NormalViewHolder.this.account);
        }
      });
      this.itemView.setOnLongClickListener(new OnLongClickListener() {
        public boolean onLongClick(View view) {
          ((OnAccountListListener) AccountAdapter.this.mContext).onItemLongClicked(NormalViewHolder.this.account);
          return true;
        }
      });
    }
  }

  public AccountAdapter(Context context, List<Account> accounts) {
    this.mContext = context;
    this.mAccounts = accounts;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_date, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    NormalViewHolder holder = (NormalViewHolder) vh;
    Account account = (Account) this.mAccounts.get(position);
    holder.account = account;
    holder.tvTitle.setText(account.getName());
    holder.tvDate.setText(account.getMobile());
    holder.tvType.setText("" + (position + 1));
    holder.tvDaysTitle.setText("Email");
    holder.tvDays.setText(account.getEmail());
    holder.tvDescTitle.setText("Category");
    holder.tvDesc.setText(account.getCategory());
    if (this.selected_usersList.contains(account)) {
      AppUtils.setDrawable(this.mContext, holder.mLayout, R.drawable.row_radius_selected);
    } else {
      AppUtils.setDrawable(this.mContext, holder.mLayout, R.drawable.row_radius);
    }
  }

  public int getItemCount() {
    if (this.mAccounts == null) {
      return 0;
    }
    return this.mAccounts.size();
  }

  public void setFilter(List<Account> countryModels) {
    this.mAccounts = countryModels;
    notifyDataSetChanged();
  }
}
