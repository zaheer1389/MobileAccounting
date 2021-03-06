package com.adslinfotech.mobileaccounting.adapter.report;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.List;

public class LastTransactionAdapter extends Adapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private Balance mBalance;
  private String mCategory;
  private Context mContext;
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
    public final TextView tvAccName;
    public final TextView tvCategory;
    public final TextView tvCity;
    public final TextView tvDate;
    public final TextView tvEmail;
    public final TextView tvMobile;
    public final TextView tvName;
    public final TextView tvProduct;
    public final TextView tvRemark;

    public NormalViewHolder(View view) {
      super(view);
      this.tvName = (TextView) view.findViewById(R.id.txt_sname);
      this.tvEmail = (TextView) view.findViewById(R.id.txt_email);
      this.tvCity = (TextView) view.findViewById(R.id.txt_city);
      this.tvMobile = (TextView) view.findViewById(R.id.txt_phone_no);
      this.tvRemark = (TextView) view.findViewById(R.id.txt_remark);
      this.tvDate = (TextView) view.findViewById(R.id.txt_date);
      this.tvProduct = (TextView) view.findViewById(R.id.txt_product);
      this.tvAccName = (TextView) view.findViewById(R.id.txt_name);
      this.tvCategory = (TextView) view.findViewById(R.id.txt_category);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          ((OnTransactionListListener) LastTransactionAdapter.this.mContext).onItemClicked(NormalViewHolder.this.transaction);
        }
      });
    }
  }

  public interface OnTransactionListListener {
    void onItemClicked(Transaction transaction);
  }

  public LastTransactionAdapter(Context context, String category, List<Transaction> transactions, Balance balance) {
    this.mCategory = category;
    this.mBalance = balance;
    this.mTransactions = transactions;
    this.mResource = context.getResources();
    this.mContext = context;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_balance, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_day, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    try {
      if (vh instanceof NormalViewHolder) {
        NormalViewHolder holder = (NormalViewHolder) vh;
        Transaction transaction = (Transaction) this.mTransactions.get(position - 1);
        holder.transaction = transaction;
        if (transaction.getDr_cr() == 1) {
          holder.tvName.setText("Credit");
          holder.tvEmail.setText("" + transaction.getCraditAmount());
        } else {
          holder.tvName.setText("Debit");
          holder.tvEmail.setText("" + transaction.getDebitAmount());
        }
        holder.tvCity.setText("100.00");
        holder.tvDate.setText(transaction.getDate());
        holder.tvProduct.setText(transaction.getNarration());
        holder.tvRemark.setText(transaction.getRemark());
        holder.tvAccName.setText(transaction.getAccName());
        holder.tvCategory.setText(transaction.getType());
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
    if (this.mTransactions.size() == 0) {
      return 1;
    }
    return this.mTransactions.size() + 1;
  }

  public int getItemViewType(int position) {
    if (position == 0) {
      return 1;
    }
    return super.getItemViewType(position);
  }

  private void setText(FooterViewHolder holder) {
    holder.mTvName.setText(Html.fromHtml(this.mResource.getString(R.string.txt_As_on_Date) + ": " + this.mCategory));
    holder.mTvCredit.setText(this.mBalance.getCredit());
    holder.mTvDebit.setText(this.mBalance.getDebit());
    holder.mTvBalance.setText(this.mBalance.getBalance());
  }
}
