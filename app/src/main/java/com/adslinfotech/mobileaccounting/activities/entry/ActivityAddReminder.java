package com.adslinfotech.mobileaccounting.activities.entry;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.alarm.ScheduleClient;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.ui.CustomDateTimePicker;
import com.adslinfotech.mobileaccounting.ui.CustomDateTimePicker.ICustomDateTimeListener;
import com.adslinfotech.mobileaccounting.ui.PickerDateActivity;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityAddReminder extends PickerDateActivity implements OnClickListener {
  private static final String TAG = "ActivityAddReminder";
  public static String[] accType = new String[]{"Appointment", "Monthly", "Yearly"};
  private String[] arrBeforeDays = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
  private CustomDateTimePicker custom;
  private AdView mAdView;
  private Button mBtNotify;
  private Date mDate;
  private EditText mEtDate;
  private EditText mEtDesc;
  private EditText mEtRemark;
  private FetchData mFetchData;
  private SimpleDateFormat mFormat;
  private ImageView mImgDate;
  private Spinner mSpBfrDays;
  private Spinner mSpType;
  private ScheduleClient scheduleClient;

  public static void getInstance(Context context) {
    context.startActivity(new Intent(context, ActivityAddReminder.class));
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_addreminder);
    getSupportActionBar().setTitle(getResources().getString(R.string.btn_AddReminder));
    this.mFetchData = new FetchData();
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
    getViews();
    setAdapterType();
    setAdapterDays();
    this.scheduleClient = new ScheduleClient(this);
    this.scheduleClient.doBindService();
    hideKeyPad();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void getViews() {
    Button btnCancel = (Button) findViewById(R.id.btn_cancel);
    ((Button) findViewById(R.id.btn_delete)).setVisibility(View.GONE);
    btnCancel.setVisibility(View.GONE);
    this.mEtDate = (EditText) findViewById(R.id.et_date);
    this.mEtDesc = (EditText) findViewById(R.id.et_desc);
    this.mBtNotify = (Button) findViewById(R.id.btn_save);
    this.mBtNotify.setText(getString(R.string.btn_AddReminder));
    this.mSpBfrDays = (Spinner) findViewById(R.id.sp_bfr_days);
    this.mSpType = (Spinner) findViewById(R.id.sp_not_type);
    this.mEtRemark = (EditText) findViewById(R.id.et_remark);
    this.mImgDate = (ImageView) findViewById(R.id.img_date);
    this.mImgDate.setOnClickListener(this);
    this.mBtNotify.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(12, 1);
    calendar.set(13, 0);
    this.mDate = calendar.getTime();
    this.mEtDate.setText(this.mFormat.format(this.mDate));
    this.custom = new CustomDateTimePicker(this, new ICustomDateTimeListener() {
      public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
        calendarSelected.set(13, 0);
        ActivityAddReminder.this.mDate = calendarSelected.getTime();
        ActivityAddReminder.this.setRMDDate();
      }

      public void onCancel() {
      }
    });
    this.custom.set24HourFormat(false);
    this.custom.setDate(Calendar.getInstance());
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.btn_save:
        validateFields();
        return;
      case R.id.img_date:
        this.custom.showDialog();
        return;
      default:
        return;
    }
  }

  private void validateFields() {
    String desc = this.mEtDesc.getText().toString();
    int type = this.mSpType.getSelectedItemPosition();
    if (desc.equals("")) {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_EnterDes), Toast.LENGTH_SHORT).show();
    } else if (1 == type || this.mDate != null) {
      saveReminder();
    } else {
      Toast.makeText(getApplicationContext(), getString(R.string.txt_SelectDate), Toast.LENGTH_SHORT).show();
    }
  }

  private void saveReminder() {
    Reminder dao = new Reminder();
    dao.setRmdType(this.mSpType.getSelectedItemPosition());
    dao.setDescription(this.mEtDesc.getText().toString());
    dao.setBeforeDay(this.mSpBfrDays.getSelectedItemPosition());
    dao.setRemark("" + this.mEtRemark.getText().toString());
    dao.setDate(this.mFormat.format(this.mDate));
    dao.setId(this.mFetchData.insertReminderDetail(dao));
    scheduleAlarm(dao);
    Toast.makeText(getApplicationContext(), getString(R.string.txt_ReminderAdd), Toast.LENGTH_SHORT).show();
    finish();
  }

  private void setAdapterType() {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, accType);
    this.mSpType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    this.mSpType.setOnItemSelectedListener(new OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ActivityAddReminder.this.setRMDDate();
      }

      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });
  }

  private void setAdapterDays() {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, this.arrBeforeDays);
    this.mSpBfrDays.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
  }

  private void setRMDDate() {
    SimpleDateFormat format;
    switch (this.mSpType.getSelectedItemPosition()) {
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
    this.mEtDate.setText(format.format(this.mDate));
  }

  private void scheduleAlarm(Reminder dao) {
    Log.e(TAG, "scheduleAlarm: " + AppUtils.getNextAlarmTime(this));
  }

  private void stopService() {
    if (this.scheduleClient != null) {
      this.scheduleClient.doUnbindService();
    }
  }

  protected void onStop() {
    stopService();
    super.onStop();
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (this.mAdView != null) {
      this.mAdView.resume();
    }
  }

  public void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }
}
