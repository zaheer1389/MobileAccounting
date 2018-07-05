package com.adslinfotech.mobileaccounting.adapter.report;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.List;

public class LedgerAdapter extends Adapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private Account mAccount;
  private Balance mBalance;
  private Context mContext;
  private Resources mResource;
  private List<Transaction> mTransactions;

  public class FooterViewHolder extends ViewHolder {
    private final ImageView mImgAccount;
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
      this.mImgAccount = (ImageView) itemView.findViewById(R.id.img_profile);
    }
  }

  public class NormalViewHolder extends ViewHolder {
    public Transaction transaction;
    public final TextView tvAmount;
    public final TextView tvBal;
    public final TextView tvDate;
    public final TextView tvMobile;
    public final TextView tvNarration;
    public final TextView tvRemark;
    public final TextView tvType;

    public NormalViewHolder(View view) {
      super(view);
      this.tvType = (TextView) view.findViewById(R.id.txt_sname);
      this.tvAmount = (TextView) view.findViewById(R.id.txt_email);
      this.tvBal = (TextView) view.findViewById(R.id.txt_city);
      this.tvMobile = (TextView) view.findViewById(R.id.txt_phone_no);
      this.tvRemark = (TextView) view.findViewById(R.id.txt_remark);
      this.tvDate = (TextView) view.findViewById(R.id.txt_date);
      this.tvNarration = (TextView) view.findViewById(R.id.txt_product);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          NormalViewHolder.this.transaction.setAccName(LedgerAdapter.this.mAccount.getName());
          ((OnTransactionListListener) LedgerAdapter.this.mContext).onItemClicked(NormalViewHolder.this.transaction);
        }
      });
    }
  }

  public interface OnTransactionListListener {
    void onItemClicked(Transaction transaction);
  }

  public LedgerAdapter(Context context, Account account, Balance balance, List<Transaction> transactions) {
    this.mAccount = account;
    this.mBalance = balance;
    this.mTransactions = transactions;
    this.mResource = context.getResources();
    this.mContext = context;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_transaction, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    try {
      if (vh instanceof NormalViewHolder) {
        NormalViewHolder holder = (NormalViewHolder) vh;
        Transaction transaction = (Transaction) this.mTransactions.get(position - 1);
        holder.transaction = transaction;
        if (transaction.getDr_cr() == 1) {
          holder.tvType.setText("Credit");
          holder.tvType.setTextColor(this.mContext.getResources().getColor(R.color.red_dark));
          holder.tvAmount.setText("" + transaction.getCraditAmount());
        } else {
          holder.tvType.setText("Debit");
          holder.tvType.setTextColor(this.mContext.getResources().getColor(R.color.s_name_color));
          holder.tvAmount.setText("" + transaction.getDebitAmount());
        }
        holder.tvBal.setText(transaction.getBalance());
        holder.tvDate.setText(" :" + transaction.getDate());
        holder.tvNarration.setText(" :" + transaction.getNarration());
        holder.tvRemark.setText(" :" + transaction.getRemark());
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
    holder.mTvName.setText(Html.fromHtml(this.mResource.getString(R.string.txt_As_on_Date) + ": Ledger of " + this.mAccount.getName()));
    holder.mTvCredit.setText(this.mBalance.getCredit());
    holder.mTvDebit.setText(this.mBalance.getDebit());
    holder.mTvBalance.setText(this.mBalance.getBalance());
    byte[] img = this.mAccount.getImage();
    if (img != null && img.length > 0) {
      holder.mImgAccount.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
    }
  }
}
