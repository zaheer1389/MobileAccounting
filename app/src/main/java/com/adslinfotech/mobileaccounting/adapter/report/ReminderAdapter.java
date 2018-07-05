package com.adslinfotech.mobileaccounting.adapter.report;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReminderAdapter extends Adapter<ViewHolder> {
  private Context mContext;
  private List<Reminder> mReminders;

  public class NormalViewHolder extends ViewHolder {
    public Reminder reminder;
    public final TextView tvDate;
    public final TextView tvDays;
    public final TextView tvDaysTitle;
    public final TextView tvDesc;
    public final TextView tvDescTitle;
    public final TextView tvTitle;
    public final TextView tvType;

    public NormalViewHolder(View view) {
      super(view);
      this.tvTitle = (TextView) view.findViewById(R.id.txt_date);
      this.tvDate = (TextView) view.findViewById(R.id.txt_balance_title);
      this.tvType = (TextView) view.findViewById(R.id.txt_balance);
      this.tvDaysTitle = (TextView) view.findViewById(R.id.txt_credit_title);
      this.tvDays = (TextView) view.findViewById(R.id.txt_credit);
      this.tvDescTitle = (TextView) view.findViewById(R.id.txt_debit_title);
      this.tvDesc = (TextView) view.findViewById(R.id.txt_debit);
      view.setOnClickListener(new OnClickListener() {
        public void onClick(View view) {
          Intent intent = new Intent(ReminderAdapter.this.mContext, ActivityEditReminder.class);
          intent.putExtra(AppConstants.ACCOUNT_SELECTED, NormalViewHolder.this.reminder);
          ReminderAdapter.this.mContext.startActivity(intent);
        }
      });
    }
  }

  public ReminderAdapter(Context context, List<Reminder> reminders) {
    this.mContext = context;
    this.mReminders = reminders;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_date, parent, false));
  }

  public void onBindViewHolder(ViewHolder vh, int position) {
    NormalViewHolder holder = (NormalViewHolder) vh;
    Reminder reminder = (Reminder) this.mReminders.get(position);
    holder.tvTitle.setText(reminder.getDescription());
    holder.tvType.setText(ActivityAddReminder.accType[reminder.getRmdType()]);
    holder.tvDaysTitle.setText("Before Days");
    holder.tvDays.setText("" + reminder.getBeforeDay());
    holder.tvDescTitle.setText("Remark");
    holder.tvDesc.setText(reminder.getRemark());
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
    holder.reminder = reminder;
  }

  public int getItemCount() {
    if (this.mReminders == null) {
      return 0;
    }
    return this.mReminders.size();
  }
}
