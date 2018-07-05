package com.adslinfotech.mobileaccounting.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.DateLedgerActivity;
import com.adslinfotech.mobileaccounting.activities.report.DateTransactionActivity;
import com.adslinfotech.mobileaccounting.activities.report.DayCummAccountActivity;
import com.adslinfotech.mobileaccounting.activities.report.DayTransactionActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.ui.PickerDateActivity;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChooseDateActivity extends PickerDateActivity {
    private static final int DATE_FROM = 124;
    private static final int DATE_TO = 325;
    private SimpleDateFormat format;
    private int index;
    private AdView mAdView;
    private Date mDateFrom;
    private Date mDateTo;
    private EditText mEtFrom;
    private EditText mEtTo;
    private Resources mResource;

    public static void newInstance(SimpleAccountingActivity context, int requestCode, int index, ArrayList<Date> arrDates) {
        Intent intent = new Intent(context, ChooseDateActivity.class);
        intent.putExtra("index", index);
        intent.putExtra(AppConstants.DATA, arrDates);
        context.startActivityForResult(intent, requestCode);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_date_wise);
        this.mResource = getResources();
        getSupportActionBar().setTitle((CharSequence) "Choose Dates");
        this.index = getIntent().getExtras().getInt("index");
        this.format = AppUtils.getDateFormat();
        getViews();
        boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
        if (!SessionManager.isProUser() && isInternetPresent) {
            this.mAdView = new AdView(this);
            this.mAdView = (AdView) findViewById(R.id.adView);
            this.mAdView.setVisibility(View.VISIBLE);
            this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
        }
        init();
    }

    private void getViews() {
        ((Button) findViewById(R.id.bt_go)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.d("SelectDateFragment", "start date = " + ChooseDateActivity.this.mDateFrom);
                    Log.d("SelectDateFragment", "end date = " + ChooseDateActivity.this.mDateTo);
                    if (ChooseDateActivity.this.mDateTo.before(ChooseDateActivity.this.mDateFrom)) {
                        Toast.makeText(ChooseDateActivity.this.getApplicationContext(), ChooseDateActivity.this.mResource.getString(R.string.txt_ToDate), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<Date> data = new ArrayList();
                    data.add(ChooseDateActivity.this.mDateFrom);
                    data.add(ChooseDateActivity.this.mDateTo);
                    Intent intent;
                    switch (ChooseDateActivity.this.index) {
                        case 2:
                            String category = ChooseDateActivity.this.getIntent().getExtras().getString(AppConstants.ACCOUNT_SELECTED);
                            intent = new Intent(ChooseDateActivity.this, DayTransactionActivity.class);
                            intent.putExtra(AppConstants.DATA, data);
                            intent.putExtra(AppConstants.ACCOUNT_SELECTED, category);
                            ChooseDateActivity.this.startActivity(intent);
                            return;
                        case 3:
                            intent = new Intent(ChooseDateActivity.this, DateTransactionActivity.class);
                            intent.putExtra(AppConstants.DATA, data);
                            ChooseDateActivity.this.startActivity(intent);
                            return;
                        case 7:
                            if (ChooseDateActivity.this.getIntent().getExtras().containsKey(AppConstants.DATA)) {
                                Log.e("ChooseDateActivity", "setResult");
                                intent = new Intent();
                                intent.putExtra(AppConstants.DATA, data);
                                ChooseDateActivity.this.setResult(-1, intent);
                                ChooseDateActivity.this.finish();
                                return;
                            }
                            Log.e("ChooseDateActivity", "start new");
                            intent = new Intent(ChooseDateActivity.this, DateLedgerActivity.class);
                            intent.putExtra(AppConstants.DATA, data);
                            ChooseDateActivity.this.startActivity(intent);
                            return;
                        case 8:
                            intent = new Intent(ChooseDateActivity.this, DayCummAccountActivity.class);
                            intent.putExtra(AppConstants.DATA, data);
                            ChooseDateActivity.this.startActivity(intent);
                            return;
                        default:
                            return;
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void init() {
        this.mEtFrom = (EditText) findViewById(R.id.et_date_from);
        this.mEtTo = (EditText) findViewById(R.id.et_date_to);
        switch (this.index) {
            case 2:
                Date date = new Date();
                this.mDateTo = date;
                this.mDateFrom = date;
                break;
            case 7:
                if (!getIntent().getExtras().containsKey(AppConstants.DATA)) {
                    setMonthDate();
                    break;
                }
                ArrayList<Date> arrDates = (ArrayList) getIntent().getExtras().getSerializable(AppConstants.DATA);
                this.mDateFrom = (Date) arrDates.get(0);
                this.mDateTo = (Date) arrDates.get(1);
                break;
            default:
                setMonthDate();
                break;
        }
        this.mEtTo.setText(this.format.format(this.mDateTo));
        this.mEtFrom.setText(this.format.format(this.mDateFrom));
    }

    private void setMonthDate() {
        this.mDateTo = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(this.mDateTo);
        c.add(Calendar.DAY_OF_MONTH, -(c.get(Calendar.DAY_OF_MONTH) - 1));
        this.mDateFrom = new Date(c.getTimeInMillis());
    }

    protected void onActivityResult(int rqCode, int rsCode, Intent arg2) {
        if (rqCode == 2) {
            onCreate(null);
        }
        super.onActivityResult(rqCode, rsCode, arg2);
    }

    public void selectDate(View view) {
        showDate(124, this.mDateFrom, false);
    }

    public void selectDate2(View view) {
        showDate(325, this.mDateTo, false);
    }

    protected void populateSetDate(int id, int year, int month, int day) {
        Date date;
        try {
            date = new SimpleDateFormat(DateFormat.DB_DATE).parse(year + "-" + month + "-" + day);
        } catch (Exception e) {
            date = new Date();
        }
        if (id == 124) {
            this.mDateFrom = date;
            this.mEtFrom.setText(this.format.format(date));
            return;
        }
        this.mDateTo = date;
        this.mEtTo.setText(this.format.format(date));
    }
}
