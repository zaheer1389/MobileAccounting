package com.adslinfotech.mobileaccounting.activities.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.DashboardActivity;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeFragment extends BaseFragment implements FragmentLifecycle {
    public static boolean isProfileUpdate;
    private AdView mAdView;
    private ImageView mImgProfile;
    private Resources mResource;
    private TextView mTotalBalance;
    private TextView mTotalCredit;
    private TextView mTotalDebit;
    private TextView tvCreditAccount;
    private TextView tvDateBackup;
    private TextView tvDebitAccount;
    private TextView tvEmail;
    private TextView tvEqualAccount;
    private TextView tvName;
    private TextView tvReminder;
    private TextView tvTotalAccount;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_home, container, false);
        rootView.setLayoutParams(new LayoutParams(-1, -1));
        this.mResource = getResources();
        Log.e("HomeFragment", "HomeFragment data");
        init(rootView);
        setText();
        try {
            ViewConfiguration config = ViewConfiguration.get(getActivity());
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
        boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
        if (!SessionManager.isProUser() && isInternetPresent) {
            this.mAdView = new AdView(getActivity());
            this.mAdView = (AdView) rootView.findViewById(R.id.adView);
            this.mAdView.setVisibility(View.VISIBLE);
            this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
        }
        return rootView;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("HomeFragment", "HomeFragment setUserVisibleHint");
    }

    public void setText() {
        FetchData fetchData = new FetchData();
        String mRsSymbol = SessionManager.getCurrency(getActivity());
        long date = SessionManager.getBackUpDate();
        if (date != 0) {
            this.tvDateBackup.setText(new SimpleDateFormat("EEE dd/MM/yy HH:mm").format(Long.valueOf(date)));
        }
        date = SessionManager.getAlarmDate();
        if (date != 0) {
            this.tvReminder.setText(new SimpleDateFormat("EEE dd/MM/yy HH:mm").format(Long.valueOf(date)));
        }
        ArrayList<Double> transaction = fetchData.getTransactionTotal(0);
        double credit = ((Double) transaction.get(0)).doubleValue();
        double debit = ((Double) transaction.get(1)).doubleValue();
        this.mTotalCredit.setText(mRsSymbol + this.newFormat.format(credit));
        this.mTotalDebit.setText(mRsSymbol + this.newFormat.format(debit));
        if (credit > debit) {
            this.mTotalBalance.setText(mRsSymbol + "" + this.newFormat.format(Double.valueOf(credit - debit).doubleValue()) + "/-" + this.mResource.getString(R.string.txt_Credit));
        } else if (debit > credit) {
            this.mTotalBalance.setText(mRsSymbol + "" + this.newFormat.format(Double.valueOf(debit - credit).doubleValue()) + "/-" + this.mResource.getString(R.string.txt_Debit));
        } else {
            this.mTotalBalance.setText("0.00/-");
        }
        setAccountDetail(fetchData.countAccountByBal());
        updateUser();
    }

    private void setAccountDetail(ArrayList<Long> longs) {
        try {
            this.tvCreditAccount.setText("" + longs.get(0));
            this.tvDebitAccount.setText("" + longs.get(1));
            this.tvEqualAccount.setText("" + longs.get(2));
            this.tvTotalAccount.setText("" + longs.get(3));
        } catch (Exception e) {
        }
    }

    private void updateUser() {
        if (isProfileUpdate) {
            isProfileUpdate = false;
            this.tvName.setText(AppUtils.capitalWord(SessionManager.getName()));
            this.tvEmail.setText(SessionManager.getEmail());
            ((DashboardActivity) getActivity()).getUserDetailView();
        }
    }

    private void init(View view) {
        this.tvName = (TextView) view.findViewById(R.id.tv_name);
        this.tvEmail = (TextView) view.findViewById(R.id.tv_email);
        this.tvName.setText(AppUtils.capitalWord(SessionManager.getName()));
        this.tvEmail.setText(SessionManager.getEmail());
        this.tvReminder = (TextView) view.findViewById(R.id.tv_reminder);
        this.tvDateBackup = (TextView) view.findViewById(R.id.tv_backup_date);
        this.mTotalCredit = (TextView) view.findViewById(R.id.tv_main_credit);
        this.mTotalDebit = (TextView) view.findViewById(R.id.tv_main_debit);
        this.mTotalBalance = (TextView) view.findViewById(R.id.tv_main_balance);
        this.mImgProfile = (ImageView) view.findViewById(R.id.img_home_user);
        AppUtils.setImage(this.mImgProfile, new FetchData().getProfileDetail().getImage());
        this.tvTotalAccount = (TextView) view.findViewById(R.id.tv_total_acc_home);
        this.tvCreditAccount = (TextView) view.findViewById(R.id.tv_credit_accounts_home);
        this.tvDebitAccount = (TextView) view.findViewById(R.id.tv_debit_accounts_home);
        this.tvEqualAccount = (TextView) view.findViewById(R.id.tv_equal_accounts_home);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onClick(View v) {
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login, menu);
    }

    public void onPauseFragment() {
    }

    public void onResumeFragment(int newPosition) {
    }

    public void onPause() {
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        setText();
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
