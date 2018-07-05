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
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditReminder;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddReminder;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReminderTableAdapter extends Adapter<ViewHolder> {
  private static final int FOOTER_VIEW = 1;
  private WeakReference<Context> mContext;
  private List<Reminder> mReminders;

  public interface OnReminderListListener {
    void onItemClicked(Reminder reminder);
  }

  public class FooterViewHolder extends ViewHolder {
    private final TextView mTvName;

    public FooterViewHolder(View itemView) {
      super(itemView);
      this.mTvName = (TextView) itemView.findViewById(R.id.tv_title);
      itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) ReminderTableAdapter.this.mContext.get();
          context.startActivity(new Intent(context, ActivityAddReminder.class));
        }
      });
    }
  }

  public class NormalViewHolder extends ViewHolder {
    public Reminder reminder;
    public final TextView tvDate;
    public final TextView tvDays;
    public final TextView tvDesc;

    public NormalViewHolder(View view) {
      super(view);
      view.findViewById(R.id.img_acc).setVisibility(View.GONE);
      this.tvDesc = (TextView) view.findViewById(R.id.tv_rmd_sub);
      this.tvDate = (TextView) view.findViewById(R.id.tv_rmd_desc);
      this.tvDays = (TextView) view.findViewById(R.id.tv_acc_category);
      this.itemView.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Context context = (Context) ReminderTableAdapter.this.mContext.get();
          Intent intent = new Intent(context, ActivityEditReminder.class);
          intent.putExtra(AppConstants.ACCOUNT_SELECTED, NormalViewHolder.this.reminder);
          context.startActivity(intent);
        }
      });
    }
  }

  public ReminderTableAdapter(Context context, List<Reminder> accounts) {
    this.mReminders = accounts;
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
        Reminder reminder = (Reminder) this.mReminders.get(position - 1);
        holder.reminder = reminder;
        holder.tvDesc.setText(reminder.getDescription());
        holder.tvDays.setText(ActivityAddReminder.accType[reminder.getRmdType()]);
        try {
          SimpleDateFormat format;
          Date date = new SimpleDateFormat(DateFormat.DB_DATE_TIME).parse(reminder.getDate());
          switch (reminder.getRmdType()) {
            case 1:
              format = new SimpleDateFormat(DateFormat.RMD_MONTHLY);
              break;
            case 2:
              format = new SimpleDateFormat(DateFormat.RMD_YEARLY);
              break;
            default:
              format = new SimpleDateFormat(DateFormat.RMD_APPOINTMENT);
              break;
          }
          holder.tvDate.setText(format.format(date));
        } catch (Exception e) {
          holder.tvDate.setText(reminder.getDate());
        }
      } else if (vh instanceof FooterViewHolder) {
        ((FooterViewHolder) vh).mTvName.setText(((Context) this.mContext.get()).getString(R.string.btn_AddReminder));
      }
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  public int getItemCount() {
    if (this.mReminders == null) {
      return 0;
    }
    if (this.mReminders.size() == 0) {
      return 1;
    }
    return this.mReminders.size() + 1;
  }

  public int getItemViewType(int position) {
    if (position == 0) {
      return 1;
    }
    return super.getItemViewType(position);
  }

  public void filter(List<Reminder> accounts) {
    this.mReminders = new ArrayList();
    this.mReminders.addAll(accounts);
    notifyDataSetChanged();
  }
}
