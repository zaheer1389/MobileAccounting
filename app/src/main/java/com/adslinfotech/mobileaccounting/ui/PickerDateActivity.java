package com.adslinfotech.mobileaccounting.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import java.util.Calendar;
import java.util.Date;

public abstract class PickerDateActivity extends SimpleAccountingActivity {
  private OnDateSetListener mDateSetListener = new OnDateSetListener() {
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      PickerDateActivity.this.mYear = year;
      PickerDateActivity.this.mMonth = monthOfYear;
      PickerDateActivity.this.mDay = dayOfMonth;
      PickerDateActivity.this.populateSetDate(PickerDateActivity.this.mId, PickerDateActivity.this.mYear, PickerDateActivity.this.mMonth + 1, PickerDateActivity.this.mDay);
    }
  };
  private int mDay;
  private int mId;
  private int mMonth;
  private int mYear;

  protected abstract void populateSetDate(int i, int i2, int i3, int i4);

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  protected void showDate(Date date) {
    showDate(0, date, false);
  }

  protected void showDate(int id, Date date, boolean isYearlyDialog) {
    this.mId = id;
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    this.mYear = c.get(1);
    this.mMonth = c.get(2);
    this.mDay = c.get(5);
    final boolean z = isYearlyDialog;
    new DatePickerDialog(this, this.mDateSetListener, this.mYear, this.mMonth, this.mDay) {
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (z) {
          int year = getContext().getResources().getIdentifier("android:id/day", null, null);
          if (year != 0) {
            View yearPicker = findViewById(year);
            if (yearPicker != null) {
              yearPicker.setVisibility(View.GONE);
            }
          }
        }
      }
    }.show();
  }
}
