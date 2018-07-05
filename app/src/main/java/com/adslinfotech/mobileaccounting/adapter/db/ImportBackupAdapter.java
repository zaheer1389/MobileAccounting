package com.adslinfotech.mobileaccounting.adapter.db;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Backup;
import java.util.List;

public class ImportBackupAdapter extends Adapter<ViewHolder> {
  private Context mContext;
  private List<Backup> mTransactions;

  public interface OnCategoryListListener {
    void onItemClicked(Backup backup);
  }

  public class NormalViewHolder extends ViewHolder {
    public Backup backup;
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
          ((OnCategoryListListener) ImportBackupAdapter.this.mContext).onItemClicked(NormalViewHolder.this.backup);
        }
      });
    }
  }

  public ImportBackupAdapter(Context context, List<Backup> transactions) {
    this.mContext = context;
    this.mTransactions = transactions;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_ledger, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    NormalViewHolder holder = (NormalViewHolder) vh;
    Backup backup = (Backup) this.mTransactions.get(position);
    holder.tvSno.setText("" + (position + 1));
    holder.backup = backup;
    holder.tvName.setText(backup.getName());
    holder.tvBalance.setText(backup.getDate());
  }

  public int getItemCount() {
    if (this.mTransactions == null) {
      return 0;
    }
    return this.mTransactions.size();
  }

  public void setFilter(List<Backup> countryModels) {
    this.mTransactions = countryModels;
    notifyDataSetChanged();
  }
}
