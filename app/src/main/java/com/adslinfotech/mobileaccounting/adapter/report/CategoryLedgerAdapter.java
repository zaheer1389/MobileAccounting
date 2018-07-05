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

public class CategoryLedgerAdapter extends Adapter<ViewHolder> {
  private static final int FIRST_ROW = 2;
  private static final int FOOTER_VIEW = 1;
  private boolean isFooterRequire = true;
  private boolean isSearching;
  private Balance mBalance;
  private String mCategory;
  private Context mContext;
  private Resources mResource;
  private List<Transaction> mTransactions;

  public interface OnCategoryListListener {
    void onItemClicked(Transaction transaction);
  }

  public class FirstViewHolder extends ViewHolder {
    public Transaction transaction;
    public final TextView tvBalance;
    public final TextView tvName;
    public final TextView tvSno;

    public FirstViewHolder(View view) {
      super(view);
      this.tvSno = (TextView) view.findViewById(R.id.txt_sno);
      this.tvName = (TextView) view.findViewById(R.id.txt_name);
      this.tvBalance = (TextView) view.findViewById(R.id.txt_balance);
    }
  }

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
    public final TextView tvName;
    public final TextView tvSno;

    public NormalViewHolder(View view) {
      super(view);
      this.tvSno = (TextView) view.findViewById(R.id.txt_sno);
      this.tvName = (TextView) view.findViewById(R.id.txt_name);
      this.tvBalance = (TextView) view.findViewById(R.id.txt_balance);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          ((OnCategoryListListener) CategoryLedgerAdapter.this.mContext).onItemClicked(NormalViewHolder.this.transaction);
        }
      });
    }
  }

  public CategoryLedgerAdapter(Context context, String category, List<Transaction> transactions, Balance balance) {
    this.mContext = context;
    this.mCategory = category;
    this.mBalance = balance;
    this.mTransactions = transactions;
    this.mResource = context.getResources();
    if (balance == null) {
      this.isFooterRequire = false;
    }
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_balance, parent, false));
    }
    if (viewType == 2) {
      return new FirstViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_ledger, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_ledger, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    int first = 0;
    try {
      if (this.isFooterRequire) {
        first = 1;
      }
      if (vh instanceof NormalViewHolder) {
        Transaction transaction;
        NormalViewHolder holder = (NormalViewHolder) vh;
        if (this.isSearching) {
          transaction = (Transaction) this.mTransactions.get(position);
          holder.tvSno.setText("" + (position + 1));
        } else {
          transaction = (Transaction) this.mTransactions.get(position - (first + 1));
          holder.tvSno.setText("" + (position - first));
        }
        holder.transaction = transaction;
        holder.tvName.setText(transaction.getAccName());
        holder.tvBalance.setText(transaction.getBalance());
      } else if (vh instanceof FooterViewHolder) {
        setText((FooterViewHolder) vh);
      } else {
        ((NormalViewHolder) vh).tvName.setText("Category Name");
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
      if (this.isFooterRequire) {
        return 2;
      }
      return 1;
    } else if (this.isFooterRequire) {
      return this.mTransactions.size() + 2;
    } else {
      return this.mTransactions.size() + 1;
    }
  }

  public int getItemViewType(int position) {
    if (!this.isSearching) {
      if (this.isFooterRequire) {
        if (position == 0) {
          return 1;
        }
        if (position == 1) {
          return 2;
        }
      } else if (position == 0) {
        return 2;
      }
    }
    return super.getItemViewType(position);
  }

  private void setText(FooterViewHolder holder) {
    if (this.isFooterRequire) {
      holder.mTvName.setText(Html.fromHtml(this.mResource.getString(R.string.txt_As_on_Date) + ": " + this.mCategory));
    }
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
