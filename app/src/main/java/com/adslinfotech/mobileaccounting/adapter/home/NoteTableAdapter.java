package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.detail.NoteDetailScreen;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddNote;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NoteTableAdapter extends Adapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private WeakReference<Context> mContext;
  private List<NoteDao> mNotes;

  public class FooterViewHolder extends ViewHolder {
    private final TextView mTvName;

    public FooterViewHolder(View itemView) {
      super(itemView);
      this.mTvName = (TextView) itemView.findViewById(R.id.tv_title);
      itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) NoteTableAdapter.this.mContext.get();
          context.startActivity(new Intent(context, ActivityAddNote.class));
        }
      });
    }
  }

  public class NormalViewHolder extends ViewHolder {
    public NoteDao note;
    public final TextView tvEmail;
    public final TextView tvName;

    public NormalViewHolder(View view) {
      super(view);
      this.tvName = (TextView) view.findViewById(R.id.tv_rmd_sub);
      this.tvEmail = (TextView) view.findViewById(R.id.tv_rmd_desc);
      view.findViewById(R.id.tv_acc_category).setVisibility(View.GONE);
      view.findViewById(R.id.img_acc).setVisibility(View.GONE);
      ((TextView) view.findViewById(R.id.tv_divider)).setVisibility(View.VISIBLE);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) NoteTableAdapter.this.mContext.get();
          Intent intent = new Intent(context, NoteDetailScreen.class);
          intent.putExtra(AppConstants.ACCOUNT_SELECTED, NormalViewHolder.this.note);
          context.startActivity(intent);
        }
      });
    }
  }

  public NoteTableAdapter(Context context, List<NoteDao> accounts) {
    this.mNotes = accounts;
    this.mContext = new WeakReference(context);
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == 1) {
      return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_account_first, parent, false));
    }
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_account, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    try {
      if (vh instanceof NormalViewHolder) {
        NormalViewHolder holder = (NormalViewHolder) vh;
        NoteDao note = (NoteDao) this.mNotes.get(position - 1);
        holder.note = note;
        holder.tvName.setText(note.getHeading());
        holder.tvEmail.setText(note.getDescr());
      } else if (vh instanceof FooterViewHolder) {
        ((FooterViewHolder) vh).mTvName.setText(((Context) this.mContext.get()).getString(R.string.txt_addnote));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getItemCount() {
    if (this.mNotes == null) {
      return 0;
    }
    if (this.mNotes.size() == 0) {
      return 1;
    }
    return this.mNotes.size() + 1;
  }

  public int getItemViewType(int position) {
    if (position == 0) {
      return 1;
    }
    return super.getItemViewType(position);
  }

  public void filter(List<NoteDao> accounts) {
    this.mNotes = new ArrayList();
    this.mNotes.addAll(accounts);
    notifyDataSetChanged();
  }
}
