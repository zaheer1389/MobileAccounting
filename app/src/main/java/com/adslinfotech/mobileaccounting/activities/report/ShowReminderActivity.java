package com.adslinfotech.mobileaccounting.activities.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditReminder;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddReminder;
import com.adslinfotech.mobileaccounting.adapter.home.ReminderTableAdapter.OnReminderListListener;
import com.adslinfotech.mobileaccounting.adapter.report.ReminderAdapter;
import com.adslinfotech.mobileaccounting.calculator.Calculator;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.ui.PickerDateActivity;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppConstants.REQUEST_CODE;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ShowReminderActivity extends PickerDateActivity implements OnReminderListListener {
  private AdView mAdView;
  private ReminderAdapter mAdapter;
  private Date mDate;
  private SimpleDateFormat mFormat;
  private SimpleDateFormat mFormatSql;
  private RecyclerView mListTransactions;
  private ArrayList<Reminder> mReminders;
  private TextView mTvDate;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_reminders);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items)[14]);
    getViews();
    setAdapter();
    getData();
    initAds();
  }

  private void initAds() {
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  private void getViews() {
    this.mFormat = AppUtils.getDateFormat();
    this.mDate = new Date();
    this.mTvDate = (TextView) findViewById(R.id.tv_date);
    this.mTvDate.setText(this.mFormat.format(this.mDate));
    this.mFormatSql = new SimpleDateFormat(DateFormat.DB_DATE);
  }

  private void setAdapter() {
    this.mListTransactions = (RecyclerView) findViewById(R.id.list);
    this.mListTransactions.setLayoutManager(new GridLayoutManager(this, 1));
    this.mListTransactions.setItemAnimator(new DefaultItemAnimator());
    this.mListTransactions.setHasFixedSize(true);
  }

  private void getData() {
    this.mReminders = new FetchData().getReminder(this.mFormatSql.format(this.mDate));
    this.mAdapter = new ReminderAdapter(this, this.mReminders);
    this.mListTransactions.setAdapter(this.mAdapter);
    this.mAdapter.notifyDataSetChanged();
  }

  public void selectDateFrom(View view) {
    showDate(this.mDate);
  }

  protected void populateSetDate(int id, int year, int month, int day) {
    Date date;
    try {
      date = this.mFormatSql.parse(year + "-" + month + "-" + day);
    } catch (Exception e) {
      date = new Date();
    }
    this.mDate = date;
    this.mTvDate.setText(this.mFormat.format(this.mDate));
    getData();
  }

  private void exportPdf(int index) {
    ArrayList<String> mColumns = new ArrayList();
    mColumns.add(getString(R.string.txt_Date));
    mColumns.add(getString(R.string.txt_Description));
    mColumns.add(getString(R.string.txt_DayRemain));
    mColumns.add(getString(R.string.txt_Type));
    ArrayList<PdfDao> mValues = new ArrayList();
    Iterator it = this.mReminders.iterator();
    while (it.hasNext()) {
      Reminder dao = (Reminder) it.next();
      PdfDao pdf = new PdfDao();
      pdf.setFirst(dao.getDate());
      pdf.setSecond(dao.getDescription());
      pdf.setThird("" + dao.getBeforeDay());
      pdf.setFour(ActivityAddReminder.accType[dao.getRmdType()]);
      mValues.add(pdf);
    }
    PdfDao header = new PdfDao();
    header.setFirst(AppConstants.ACCOUNT_REPORT);
    switch (index) {
      case 0:
        new GeneratePdf(getApplicationContext(), header, mColumns, mValues, 8).pdf(this);
        return;
      case 1:
        new GenerateExcel(getApplicationContext(), header, mColumns, mValues, 8).excel(this);
        return;
      default:
        return;
    }
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE.SELECTED_REMINDER && resultCode == -1) {
      getData();
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_export, menu);
    menu.removeItem(R.id.menu_search);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case Menus.HOME /*16908332*/:
        finish();
        return false;
      case R.id.menu_export_exel:
        exportPdf(1);
        return false;
      case R.id.menu_export_pdf:
        exportPdf(0);
        return false;
      case R.id.menu_usecal:
        try {
          Intent i = new Intent();
          i.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
          startActivity(i);
          return false;
        } catch (Exception e) {
          startActivity(new Intent(getApplicationContext(), Calculator.class));
          return false;
        }
      default:
        return super.onOptionsItemSelected(item);
    }
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

  public void onItemClicked(Reminder item) {
    Intent intent = new Intent(this, ActivityEditReminder.class);
    intent.putExtra(AppConstants.ACCOUNT_SELECTED, item);
    startActivityForResult(intent, REQUEST_CODE.SELECTED_REMINDER);
  }
}
