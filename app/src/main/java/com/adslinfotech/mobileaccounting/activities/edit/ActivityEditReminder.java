package com.adslinfotech.mobileaccounting.activities.edit;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DIALOG;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityEditReminder extends ActivityEdit implements OnClickListener {
  private static final String TAG = "ActivityEditReminder";
  private String[] accType = new String[]{"Appointment", "Monthly", "Yearly"};
  private String[] arrBeforeDays = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
  private CustomDateTimePicker custom;
  private AdView mAdView;
  private Date mDate;
  private EditText mEtDate;
  private EditText mEtDesc;
  private EditText mEtRemark;
  private FetchData mFetchData;
  private SimpleDateFormat mFormat;
  private ImageView mImgDate;
  private Reminder mReminder;
  private Resources mResource;
  private Spinner mSpBfrDays;
  private Spinner mSpType;
  private ScheduleClient scheduleClient;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_addreminder);
    this.mResource = getResources();
    this.mFetchData = new FetchData();
    getViews();
    setAdapterType();
    setAdapterDays();
    setData();
    this.scheduleClient = new ScheduleClient(this);
    this.scheduleClient.doBindService();
    this.custom = new CustomDateTimePicker(this, new ICustomDateTimeListener() {
      public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
        calendarSelected.set(Calendar.DAY_OF_MONTH, 0);
        ActivityEditReminder.this.mDate = calendarSelected.getTime();
        ActivityEditReminder.this.setRMDDate();
      }

      public void onCancel() {
      }
    });
    this.custom.set24HourFormat(false);
    this.custom.setDate(Calendar.getInstance());
    hideKeyPad();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  private void setData() {
    this.mReminder = (Reminder) getIntent().getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED);
    try {
      this.mDate = this.mFormat.parse(this.mReminder.getDate());
    } catch (Exception e) {
    }
    try {
      this.mSpType.setSelection(this.mReminder.getRmdType());
    } catch (Exception e2) {
    }
    this.mEtDesc.setText(this.mReminder.getDescription());
    try {
      this.mSpBfrDays.setSelection(this.mReminder.getBeforeDay());
    } catch (Exception e3) {
      try {
        this.mSpBfrDays.setSelection(this.arrBeforeDays.length - 1);
      } catch (Exception e4) {
      }
    }
    this.mEtRemark.setText(this.mReminder.getRemark());
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.btn_cancel:
        finish();
        return;
      case R.id.btn_delete:
        checkPasswordRequired(325);
        return;
      case R.id.btn_save:
        validateFields();
        return;
      case R.id.img_date:
        this.custom.showDialog();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void validateFields() {
    if (this.mEtDesc.getText().toString().equals("")) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_EnterDes), Toast.LENGTH_SHORT).show();
    } else {
      checkPasswordRequired(DIALOG.DIALOG_UPDATE);
    }
  }

  public void passwordConfirmed(int i) {
    if (i == DIALOG.DIALOG_UPDATE) {
      updateReminder();
    } else if (i == 325) {
      deleteReminder();
    }
    setResult(-1);
  }

  protected void setImage(Bitmap thumbnail) {
  }

  private void updateReminder() {
    Reminder dao = new Reminder();
    dao.setId(this.mReminder.getId());
    dao.setRmdType(this.mSpType.getSelectedItemPosition());
    dao.setDescription(this.mEtDesc.getText().toString());
    dao.setBeforeDay(this.mSpBfrDays.getSelectedItemPosition() + 1);
    dao.setRemark(this.mEtRemark.getText().toString());
    dao.setDate(this.mFormat.format(this.mDate));
    this.mFetchData.updateReminder(dao);
    scheduleAlarm(dao);
    setResult(-1);
    finish();
  }

  private void deleteReminder() {
    this.mFetchData.deleteReminder(this.mReminder.getId());
    setResult(-1);
    finish();
  }

  private void getViews() {
    this.mFormat = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
    this.mEtDate = (EditText) findViewById(R.id.et_date);
    this.mEtDesc = (EditText) findViewById(R.id.et_desc);
    this.mSpBfrDays = (Spinner) findViewById(R.id.sp_bfr_days);
    this.mSpType = (Spinner) findViewById(R.id.sp_not_type);
    this.mEtRemark = (EditText) findViewById(R.id.et_remark);
    this.mImgDate = (ImageView) findViewById(R.id.img_date);
    this.mImgDate.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void setAdapterType() {
    ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,  android.R.layout.simple_spinner_item, this.accType);
    this.mSpType.setAdapter(dataAdapter);
    dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    this.mSpType.setOnItemSelectedListener(new OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ActivityEditReminder.this.setRMDDate();
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
    if (this.mEtDate != null) {
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

  protected void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }

  private void scheduleAlarm(Reminder dao) {
    Log.e(TAG, "scheduleAlarm: " + AppUtils.getNextAlarmTime(this));
  }
}
