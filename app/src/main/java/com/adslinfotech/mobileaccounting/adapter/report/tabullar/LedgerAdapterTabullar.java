package com.adslinfotech.mobileaccounting.adapter.report.tabullar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;

public class LedgerAdapterTabullar extends Adapter<ViewHolder> {
  private static final int FIRST_ROW = 2;
  private static final int HEADER_VIEW = 1;
  private boolean isSearching;
  private boolean isTabullar;
  private Account mAccount;
  private Balance mBalance;
  private Context mContext;
  private Resources mResource;
  public List<Transaction> mTransactions;
  public List<Transaction> selected_usersList = new ArrayList();
  private String strOpeningBal;
  private String strOverallBal;

  public interface OnTransactionListListener {
    void onItemClicked(Transaction transaction);

    void onItemLongClicked(Transaction transaction);
  }

  public class FirstViewHolder extends ViewHolder {
    private final TextView mBalance = ((TextView) this.itemView.findViewById(R.id.text_balance));
    private final TextView mCredit = ((TextView) this.itemView.findViewById(R.id.text_credit));
    private final TextView mDate = ((TextView) this.itemView.findViewById(R.id.text_date));
    private final TextView mDebit = ((TextView) this.itemView.findViewById(R.id.text_debit));
    private final TextView mNarration = ((TextView) this.itemView.findViewById(R.id.text_narration));

    public FirstViewHolder(View view) {
      super(view);
    }
  }

  public class HeaderViewHolder extends ViewHolder {
    private final ImageView mImgAccount;
    private final TextView mTvBalance;
    private final TextView mTvCredit;
    private final TextView mTvDebit;
    private final TextView mTvName;
    private final TextView mTvOpening;
    private final TextView mTvOverall;

    public HeaderViewHolder(View itemView) {
      super(itemView);
      this.mTvName = (TextView) itemView.findViewById(R.id.tv_name);
      this.mTvCredit = (TextView) itemView.findViewById(R.id.txt_total_credit);
      this.mTvDebit = (TextView) itemView.findViewById(R.id.txt_total_debit);
      this.mTvBalance = (TextView) itemView.findViewById(R.id.txt_total_balance);
      this.mImgAccount = (ImageView) itemView.findViewById(R.id.img_profile);
      if (LedgerAdapterTabullar.this.strOverallBal != null) {
        itemView.findViewById(R.id.view_separate).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.lout_overall).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.lout_opening_bal).setVisibility(View.VISIBLE);
        this.mTvOverall = (TextView) itemView.findViewById(R.id.txt_overall_balance);
        this.mTvOpening = (TextView) itemView.findViewById(R.id.txt_opening_balance);
        return;
      }
      this.mTvOverall = null;
      this.mTvOpening = null;
    }
  }

  public class NormalViewHolder extends ViewHolder {
    private View mLayout;
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
      this.mLayout = view.findViewById(R.id.ll_top);
      this.tvType = (TextView) view.findViewById(R.id.txt_sname);
      this.tvAmount = (TextView) view.findViewById(R.id.txt_email);
      this.tvBal = (TextView) view.findViewById(R.id.txt_city);
      this.tvMobile = (TextView) view.findViewById(R.id.txt_phone_no);
      this.tvRemark = (TextView) view.findViewById(R.id.txt_remark);
      this.tvDate = (TextView) view.findViewById(R.id.txt_date);
      this.tvNarration = (TextView) view.findViewById(R.id.txt_product);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          NormalViewHolder.this.transaction.setAccName(LedgerAdapterTabullar.this.mAccount.getName());
          ((OnTransactionListListener) LedgerAdapterTabullar.this.mContext).onItemClicked(NormalViewHolder.this.transaction);
        }
      });
      this.itemView.setOnLongClickListener(new OnLongClickListener() {
        public boolean onLongClick(View view) {
          ((OnTransactionListListener) LedgerAdapterTabullar.this.mContext).onItemLongClicked(NormalViewHolder.this.transaction);
          return true;
        }
      });
    }
  }

  public class VerticalItemHolder extends ViewHolder {
    private TextView mBalance;
    private TextView mCredit;
    private TextView mDate;
    private TextView mDebit;
    private View mLayout;
    private TextView mNarration;
    public Transaction transaction;

    public VerticalItemHolder(View itemView) {
      super(itemView);
      this.mDate = (TextView) itemView.findViewById(R.id.text_date);
      this.mCredit = (TextView) itemView.findViewById(R.id.text_credit);
      this.mDebit = (TextView) itemView.findViewById(R.id.text_debit);
      this.mBalance = (TextView) itemView.findViewById(R.id.text_balance);
      this.mNarration = (TextView) itemView.findViewById(R.id.text_narration);
      this.mLayout = itemView.findViewById(R.id.layout_row);
      itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          VerticalItemHolder.this.transaction.setAccName(LedgerAdapterTabullar.this.mAccount.getName());
          ((OnTransactionListListener) LedgerAdapterTabullar.this.mContext).onItemClicked(VerticalItemHolder.this.transaction);
        }
      });
      itemView.setOnLongClickListener(new OnLongClickListener() {
        public boolean onLongClick(View view) {
          ((OnTransactionListListener) LedgerAdapterTabullar.this.mContext).onItemLongClicked(VerticalItemHolder.this.transaction);
          return true;
        }
      });
    }
  }

  public LedgerAdapterTabullar(Context context, Account account, Balance balance, List<Transaction> transactions, String overall, String opening, boolean isTabullar) {
    this.mAccount = account;
    this.mBalance = balance;
    this.mTransactions = transactions;
    this.mResource = context.getResources();
    this.mContext = context;
    this.isTabullar = isTabullar;
    this.strOverallBal = overall;
    this.strOpeningBal = opening;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
    }
    if (viewType == 2) {
      return new FirstViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_match_item, parent, false));
    }
    if (this.isTabullar) {
      return new VerticalItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_match_item, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_transaction, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    try {
      if (vh instanceof HeaderViewHolder) {
        setText((HeaderViewHolder) vh);
      } else if (vh instanceof FirstViewHolder) {
        FirstViewHolder holder = (FirstViewHolder) vh;
        holder.mDate.setText(this.mContext.getResources().getString(R.string.txt_Date));
        holder.mCredit.setText(this.mContext.getResources().getString(R.string.txt_Credit));
        holder.mDebit.setText(this.mContext.getResources().getString(R.string.txt_Debit));
        holder.mBalance.setText(this.mContext.getResources().getString(R.string.txt_balance_amount));
        holder.mNarration.setText(this.mContext.getResources().getString(R.string.txt_Narration));
      } else {
        Transaction transaction;
        if (this.isSearching) {
          transaction = (Transaction) this.mTransactions.get(position);
        } else {
          transaction = (Transaction) this.mTransactions.get(position - 1);
        }
        if (this.isTabullar) {
          VerticalItemHolder holder2 = (VerticalItemHolder) vh;
          holder2.transaction = transaction;
          holder2.mDate.setText(transaction.getDate());
          holder2.mCredit.setText("" + transaction.getCraditAmount());
          holder2.mDebit.setText("" + transaction.getDebitAmount());
          holder2.mBalance.setText(transaction.getBalance());
          holder2.mNarration.setText(transaction.getNarration());
          if (this.selected_usersList.contains(transaction)) {
            holder2.mLayout.setBackgroundColor(AppUtils.getColor(this.mContext, R.color.green_google));
            return;
          } else if (transaction.getDr_cr() == 1) {
            holder2.mLayout.setBackgroundColor(AppUtils.getColor(this.mContext, R.color.red));
            return;
          } else {
            holder2.mLayout.setBackgroundColor(AppUtils.getColor(this.mContext, R.color.dark_blue));
            return;
          }
        }
        NormalViewHolder holder3 = (NormalViewHolder) vh;
        holder3.transaction = transaction;
        if (transaction.getDr_cr() == 1) {
          holder3.tvType.setText("Credit");
          holder3.tvType.setTextColor(this.mContext.getResources().getColor(R.color.red_dark));
          holder3.tvAmount.setText("" + transaction.getCraditAmount());
        } else {
          holder3.tvType.setText("Debit");
          holder3.tvType.setTextColor(this.mContext.getResources().getColor(R.color.s_name_color));
          holder3.tvAmount.setText("" + transaction.getDebitAmount());
        }
        holder3.tvBal.setText(transaction.getBalance());
        holder3.tvDate.setText(" :" + transaction.getDate());
        holder3.tvNarration.setText(" :" + transaction.getNarration());
        holder3.tvRemark.setText(" :" + transaction.getRemark());
        if (this.selected_usersList.contains(transaction)) {
          AppUtils.setDrawable(this.mContext, holder3.mLayout, R.drawable.row_radius_selected);
        } else {
          AppUtils.setDrawable(this.mContext, holder3.mLayout, R.drawable.row_radius);
        }
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
    if (this.isTabullar) {
      return 2;
    }
    return 1;
  }

  private void setText(HeaderViewHolder holder) {
    holder.mTvName.setText(Html.fromHtml(this.mResource.getString(R.string.txt_As_on_Date) + ": Ledger of " + this.mAccount.getName()));
    holder.mTvCredit.setText(this.mBalance.getCredit());
    holder.mTvDebit.setText(this.mBalance.getDebit());
    holder.mTvBalance.setText(this.mBalance.getBalance());
    byte[] img = this.mAccount.getImage();
    if (img != null && img.length > 0) {
      holder.mImgAccount.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
    }
    if (this.strOverallBal != null) {
      holder.mTvOverall.setText(this.strOverallBal);
      holder.mTvOpening.setText(this.strOpeningBal);
    }
  }

  public void setFilter(List<Transaction> countryModels, boolean isDone) {
    this.isSearching = isDone;
    this.mTransactions = countryModels;
    notifyDataSetChanged();
  }
}
