package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.detail.AccountDetailActivity;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddAccount;
import com.adslinfotech.mobileaccounting.adapter.CursorRecyclerViewAdapter;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.database.FetchCursor;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.lang.ref.WeakReference;
import java.util.List;

public class AccountTableAdapter extends CursorRecyclerViewAdapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private WeakReference<Context> mContext;

  private class FooterViewHolder extends ViewHolder {
    private final TextView mTvName;

    public FooterViewHolder(View itemView) {
      super(itemView);
      this.mTvName = (TextView) itemView.findViewById(R.id.tv_title);
      itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) AccountTableAdapter.this.mContext.get();
          context.startActivity(new Intent(context, ActivityAddAccount.class));
        }
      });
    }
  }

  private class NormalViewHolder extends ViewHolder {
    private Account account;
    private final ImageView imgAccount;
    private final RelativeLayout relav;
    private final TextView tvCategory;
    private final TextView tvEmail;
    public final TextView tvName;

    public NormalViewHolder(View view) {
      super(view);
      this.tvName = (TextView) view.findViewById(R.id.tv_rmd_sub);
      this.tvEmail = (TextView) view.findViewById(R.id.tv_rmd_desc);
      this.tvCategory = (TextView) view.findViewById(R.id.tv_acc_category);
      this.imgAccount = (ImageView) view.findViewById(R.id.img_acc);
      ((TextView) view.findViewById(R.id.tv_divider)).setVisibility(View.VISIBLE);
      this.relav = (RelativeLayout) view.findViewById(R.id.rel);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) AccountTableAdapter.this.mContext.get();
          Intent intent = new Intent(context, AccountDetailActivity.class);
          intent.putExtra(EXTRA.SELECTED_ACCOUNT_NAME, NormalViewHolder.this.account.getName());
          context.startActivity(intent);
        }
      });
    }

    private void bindCursor(Cursor cursor) {
      this.account = FetchCursor.getAccount(cursor, true);
      this.tvEmail.setText(this.account.getEmail());
      this.tvCategory.setText(this.account.getCategory());
      if (AppUtils.setImage(this.imgAccount, this.account.getImage())) {
        this.imgAccount.setImageDrawable(((Context) AccountTableAdapter.this.mContext.get()).getResources().getDrawable(R.drawable.profile_icon));
      }
      if (this.account.getBalance().contains("Cr")) {
        this.tvName.setText(this.account.getName() + " (Cr)");
        this.relav.setBackgroundColor(Color.parseColor("#ff0000"));
      } else if (this.account.getBalance().contains("Db")) {
        this.tvName.setText(this.account.getName() + " (Db)");
        this.relav.setBackgroundColor(Color.parseColor("#551076"));
      } else {
        this.tvName.setText(this.account.getName() + " (=)");
        this.relav.setBackgroundColor(Color.parseColor("#3b4259"));
      }
    }
  }

  public interface OnAccountListListener {
    void onItemClicked(Account account);
  }

  public AccountTableAdapter(Context context, Cursor cursor) {
    super(cursor);
    this.mContext = new WeakReference(context);
    this.isShowFirstRow = true;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_account_first, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_account, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, Cursor cursor) {
    try {
      if (vh instanceof NormalViewHolder) {
        ((NormalViewHolder) vh).bindCursor(cursor);
      } else if (vh instanceof FooterViewHolder) {
        ((FooterViewHolder) vh).mTvName.setText(((Context) this.mContext.get()).getString(R.string.btn_Create_Account));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getItemCount() {
    int count = super.getItemCount();
    if (count == 0) {
      return 1;
    }
    return count + 1;
  }

  public int getItemViewType(int position) {
    if (position == 0) {
      return 1;
    }
    return super.getItemViewType(position);
  }

  public void filter(List<Account> list) {
    notifyDataSetChanged();
  }
}
