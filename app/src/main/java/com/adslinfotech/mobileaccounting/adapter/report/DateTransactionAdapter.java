package com.adslinfotech.mobileaccounting.adapter.report;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.List;

public class DateTransactionAdapter extends Adapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private boolean isSearching;
  private Balance mBalance;
  private String mCategory;
  private Resources mResource;
  private List<Transaction> mTransactions;

  public class FooterViewHolder extends ViewHolder {
    private final TextView mTvBalance;
    private final TextView mTvCredit;
    private final TextView mTvDebit;
    private final TextView mTvName;

    public FooterViewHolder(View itemView) {
      super(itemView);
      this.mTvName = (TextView) itemView.findViewById(R.id.tv_name);
      this.mTvCredit = (TextView) itemView.findViewById(R.id.txt_total_credit);
      this.mTvDebit = (TextView) itemView.findViewById(R.id.txt_total_debit);
      this.mTvBalance = (TextView) itemView.findViewById(R.id.txt_total_balance);
    }
  }

  public class NormalViewHolder extends ViewHolder {
    public Transaction transaction;
    public final TextView tvBalance;
    public final TextView tvCredit;
    public final TextView tvDate;
    public final TextView tvDebit;

    public NormalViewHolder(View view) {
      super(view);
      this.tvDate = (TextView) view.findViewById(R.id.txt_date);
      this.tvCredit = (TextView) view.findViewById(R.id.txt_credit);
      this.tvDebit = (TextView) view.findViewById(R.id.txt_debit);
      this.tvBalance = (TextView) view.findViewById(R.id.txt_balance);
    }
  }

  public DateTransactionAdapter(Context context, String category, List<Transaction> transactions, Balance balance) {
    this.mCategory = category;
    this.mBalance = balance;
    this.mTransactions = transactions;
    this.mResource = context.getResources();
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_balance, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_date, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    try {
      if (vh instanceof NormalViewHolder) {
        Transaction transaction;
        NormalViewHolder holder = (NormalViewHolder) vh;
        if (this.isSearching) {
          transaction = (Transaction) this.mTransactions.get(position);
        } else {
          transaction = (Transaction) this.mTransactions.get(position - 1);
        }
        holder.transaction = transaction;
        holder.tvDate.setText(transaction.getDate());
        holder.tvCredit.setText("" + transaction.getCraditAmount());
        holder.tvDebit.setText("" + transaction.getDebitAmount());
        holder.tvBalance.setText(transaction.getBalance());
      } else if (vh instanceof FooterViewHolder) {
        setText((FooterViewHolder) vh);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getItemCount() {
    if (this.mTransactions == null) {
      return 0;
    }
    if (this.isSearching) {
      return this.mTransactions.size();
    }
    if (this.mTransactions.size() == 0) {
      return 1;
    }
    return this.mTransactions.size() + 1;
  }

  public int getItemViewType(int position) {
    if (this.isSearching || position != 0) {
      return super.getItemViewType(position);
    }
    return 1;
  }

  private void setText(FooterViewHolder holder) {
    holder.mTvName.setText(Html.fromHtml(this.mResource.getString(R.string.txt_As_on_Date) + ": " + this.mCategory));
    holder.mTvCredit.setText(this.mBalance.getCredit());
    holder.mTvDebit.setText(this.mBalance.getDebit());
    holder.mTvBalance.setText(this.mBalance.getBalance());
  }

  public void setFilter(List<Transaction> countryModels, boolean isDone) {
    this.isSearching = isDone;
    this.mTransactions = countryModels;
    notifyDataSetChanged();
  }
}
