package com.adslinfotech.mobileaccounting.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewSwitcher;
import com.itextpdf.text.pdf.BaseField;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomDateTimePicker implements OnClickListener {
  private final int CANCEL = 103;
  private final int SET = 102;
  private final int SET_DATE = 100;
  private final int SET_TIME = 101;
  private Activity activity;
  private Button btn_cancel;
  private Button btn_set;
  private Button btn_setDate;
  private Button btn_setTime;
  private Calendar calendar_date = null;
  private DatePicker datePicker;
  private Dialog dialog;
  private ICustomDateTimeListener iCustomDateTimeListener = null;
  private boolean is24HourView = true;
  private boolean isAutoDismiss = true;
  private int selectedHour;
  private int selectedMinute;
  private TimePicker timePicker;
  private ViewSwitcher viewSwitcher;

  public interface ICustomDateTimeListener {
    void onCancel();

    void onSet(Dialog dialog, Calendar calendar, Date date, int i, String str, String str2, int i2, int i3, String str3, String str4, int i4, int i5, int i6, int i7, String str5);
  }

  public CustomDateTimePicker(Activity a, ICustomDateTimeListener customDateTimeListener) {
    this.activity = a;
    this.iCustomDateTimeListener = customDateTimeListener;
    this.dialog = new Dialog(this.activity);
    this.dialog.setOnDismissListener(new OnDismissListener() {
      public void onDismiss(DialogInterface dialog) {
        CustomDateTimePicker.this.resetData();
      }
    });
    this.dialog.requestWindowFeature(1);
    this.dialog.setContentView(getDateTimePickerLayout());
  }

  public View getDateTimePickerLayout() {
    LayoutParams linear_match_wrap = new LayoutParams(-1, -1);
    LayoutParams linear_wrap_wrap = new LayoutParams(-2, -2);
    FrameLayout.LayoutParams frame_match_wrap = new FrameLayout.LayoutParams(-1, -2);
    LayoutParams button_params = new LayoutParams(0, -2, BaseField.BORDER_WIDTH_THIN);
    LinearLayout linear_main = new LinearLayout(this.activity);
    linear_main.setLayoutParams(linear_match_wrap);
    linear_main.setOrientation(LinearLayout.VERTICAL);
    linear_main.setGravity(17);
    LinearLayout linear_child = new LinearLayout(this.activity);
    linear_child.setLayoutParams(linear_wrap_wrap);
    linear_child.setOrientation(LinearLayout.VERTICAL);
    LinearLayout linear_top = new LinearLayout(this.activity);
    linear_top.setLayoutParams(linear_match_wrap);
    this.btn_setDate = new Button(this.activity);
    this.btn_setDate.setLayoutParams(button_params);
    this.btn_setDate.setText("Set Date");
    this.btn_setDate.setId(SET_DATE);
    this.btn_setDate.setOnClickListener(this);
    this.btn_setTime = new Button(this.activity);
    this.btn_setTime.setLayoutParams(button_params);
    this.btn_setTime.setText("Set Time");
    this.btn_setTime.setId(SET_DATE);
    this.btn_setTime.setOnClickListener(this);
    linear_top.addView(this.btn_setDate);
    linear_top.addView(this.btn_setTime);
    this.viewSwitcher = new ViewSwitcher(this.activity);
    this.viewSwitcher.setLayoutParams(frame_match_wrap);
    this.datePicker = new DatePicker(this.activity);
    this.timePicker = new TimePicker(this.activity);
    this.timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
      public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        CustomDateTimePicker.this.selectedHour = hourOfDay;
        CustomDateTimePicker.this.selectedMinute = minute;
      }
    });
    this.viewSwitcher.addView(this.timePicker);
    this.viewSwitcher.addView(this.datePicker);
    LinearLayout linear_bottom = new LinearLayout(this.activity);
    linear_match_wrap.topMargin = 8;
    linear_bottom.setLayoutParams(linear_match_wrap);
    this.btn_set = new Button(this.activity);
    this.btn_set.setLayoutParams(button_params);
    this.btn_set.setText("Set");
    this.btn_set.setId(102);
    this.btn_set.setOnClickListener(this);
    this.btn_cancel = new Button(this.activity);
    this.btn_cancel.setLayoutParams(button_params);
    this.btn_cancel.setText("Cancel");
    this.btn_cancel.setId(103);
    this.btn_cancel.setOnClickListener(this);
    linear_bottom.addView(this.btn_set);
    linear_bottom.addView(this.btn_cancel);
    linear_child.addView(linear_top);
    linear_child.addView(this.viewSwitcher);
    linear_child.addView(linear_bottom);
    linear_main.addView(linear_child);
    return linear_main;
  }

  public void showDialog() {
    if (!this.dialog.isShowing()) {
      if (this.calendar_date == null) {
        this.calendar_date = Calendar.getInstance();
      }
      this.selectedHour = this.calendar_date.get(11);
      this.selectedMinute = this.calendar_date.get(12);
      this.timePicker.setIs24HourView(Boolean.valueOf(this.is24HourView));
      if (VERSION.SDK_INT >= 23) {
        this.timePicker.setHour(this.selectedHour);
        this.timePicker.setMinute(this.selectedMinute);
      } else {
        this.timePicker.setCurrentHour(Integer.valueOf(this.selectedHour));
        this.timePicker.setCurrentMinute(Integer.valueOf(this.selectedMinute));
      }
      setTimeIn24HourFormat(this.selectedHour, this.selectedMinute);
      this.datePicker.updateDate(this.calendar_date.get(1), this.calendar_date.get(2), this.calendar_date.get(5));
      this.dialog.show();
      this.btn_setDate.performClick();
    }
  }

  public void setAutoDismiss(boolean isAutoDismiss) {
    this.isAutoDismiss = isAutoDismiss;
  }

  public void dismissDialog() {
    if (!this.dialog.isShowing()) {
      this.dialog.dismiss();
    }
  }

  public void setDate(Calendar calendar) {
    if (calendar != null) {
      this.calendar_date = calendar;
    }
  }

  public void setDate(Date date) {
    if (date != null) {
      this.calendar_date = Calendar.getInstance();
      this.calendar_date.setTime(date);
    }
  }

  public void setDate(int year, int month, int day) {
    if (month < 12 && month >= 0 && day < 32 && day >= 0 && year > 100 && year < 3000) {
      this.calendar_date = Calendar.getInstance();
      this.calendar_date.set(year, month, day);
    }
  }

  public void setTimeIn24HourFormat(int hourIn24Format, int minute) {
    if (hourIn24Format < 24 && hourIn24Format >= 0 && minute >= 0 && minute < 60) {
      if (this.calendar_date == null) {
        this.calendar_date = Calendar.getInstance();
      }
      this.calendar_date.set(this.calendar_date.get(1), this.calendar_date.get(2), this.calendar_date.get(5), hourIn24Format, minute);
      this.is24HourView = true;
    }
  }

  public void setTimeIn12HourFormat(int hourIn12Format, int minute, boolean isAM) {
    if (hourIn12Format < 13 && hourIn12Format > 0 && minute >= 0 && minute < 60) {
      if (hourIn12Format == 12) {
        hourIn12Format = 0;
      }
      int hourIn24Format = hourIn12Format;
      if (!isAM) {
        hourIn24Format += 12;
      }
      if (this.calendar_date == null) {
        this.calendar_date = Calendar.getInstance();
      }
      this.calendar_date.set(this.calendar_date.get(1), this.calendar_date.get(2), this.calendar_date.get(5), hourIn24Format, minute);
      this.is24HourView = false;
    }
  }

  public void set24HourFormat(boolean is24HourFormat) {
    this.is24HourView = is24HourFormat;
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case 100:
        this.btn_setTime.setEnabled(true);
        this.btn_setDate.setEnabled(false);
        this.viewSwitcher.showNext();
        return;
      case 101:
        this.btn_setTime.setEnabled(false);
        this.btn_setDate.setEnabled(true);
        this.viewSwitcher.showPrevious();
        return;
      case 102:
        if (this.iCustomDateTimeListener != null) {
          int month = this.datePicker.getMonth();
          this.calendar_date.set(this.datePicker.getYear(), month, this.datePicker.getDayOfMonth(), this.selectedHour, this.selectedMinute);
          this.iCustomDateTimeListener.onSet(this.dialog, this.calendar_date, this.calendar_date.getTime(), this.calendar_date.get(1), getMonthFullName(this.calendar_date.get(2)), getMonthShortName(this.calendar_date.get(2)), this.calendar_date.get(2), this.calendar_date.get(5), getWeekDayFullName(this.calendar_date.get(7)), getWeekDayShortName(this.calendar_date.get(7)), this.calendar_date.get(11), getHourIn12Format(this.calendar_date.get(11)), this.calendar_date.get(12), this.calendar_date.get(13), getAMPM(this.calendar_date));
        }
        if (this.dialog.isShowing() && this.isAutoDismiss) {
          this.dialog.dismiss();
          return;
        }
        return;
      case 103:
        if (this.iCustomDateTimeListener != null) {
          this.iCustomDateTimeListener.onCancel();
        }
        if (this.dialog.isShowing()) {
          this.dialog.dismiss();
          return;
        }
        return;
      default:
        return;
    }
  }

  public static String convertDate(String date, String fromFormat, String toFormat) {
    try {
      Date d = new SimpleDateFormat(fromFormat).parse(date);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(d);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(toFormat);
      simpleDateFormat.setCalendar(calendar);
      date = simpleDateFormat.format(calendar.getTime());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return date;
  }

  private String getMonthFullName(int monthNumber) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2, monthNumber);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
    simpleDateFormat.setCalendar(calendar);
    return simpleDateFormat.format(calendar.getTime());
  }

  private String getMonthShortName(int monthNumber) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2, monthNumber);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
    simpleDateFormat.setCalendar(calendar);
    return simpleDateFormat.format(calendar.getTime());
  }

  private String getWeekDayFullName(int weekDayNumber) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(7, weekDayNumber);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
    simpleDateFormat.setCalendar(calendar);
    return simpleDateFormat.format(calendar.getTime());
  }

  private String getWeekDayShortName(int weekDayNumber) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(7, weekDayNumber);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
    simpleDateFormat.setCalendar(calendar);
    return simpleDateFormat.format(calendar.getTime());
  }

  private int getHourIn12Format(int hour24) {
    if (hour24 == 0) {
      return 12;
    }
    if (hour24 <= 12) {
      return hour24;
    }
    return hour24 - 12;
  }

  private String getAMPM(Calendar calendar) {
    return calendar.get(9) == 0 ? "AM" : "PM";
  }

  private void resetData() {
    this.calendar_date = null;
    this.is24HourView = true;
  }

  public static String pad(int integerToPad) {
    if (integerToPad >= 10 || integerToPad < 0) {
      return String.valueOf(integerToPad);
    }
    return "0" + String.valueOf(integerToPad);
  }
}
