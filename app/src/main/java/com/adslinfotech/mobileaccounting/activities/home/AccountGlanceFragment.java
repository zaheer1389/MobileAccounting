package com.adslinfotech.mobileaccounting.activities.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.adapter.home.GlanceAdapter;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.desktop.WebServerActivity;
import com.adslinfotech.mobileaccounting.export.GenerateExcel;
import com.adslinfotech.mobileaccounting.export.GeneratePdf;
import com.adslinfotech.mobileaccounting.fragment.BaseFragment;
import com.adslinfotech.mobileaccounting.fragment.FragmentLifecycle;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;

import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class AccountGlanceFragment extends BaseFragment implements FragmentLifecycle {
    private ListView Transaction_listviewCredit;
    private ListView Transaction_listviewDebit;
    private boolean isSortByName = true;
    private AdView mAdView;
    private ArrayList<String> mColumns = new ArrayList();
    private Resources mResource;
    private ArrayList<PdfDao> mValues = new ArrayList();
    private String strCredit;
    private String strDebit;
    private TextView txtNoCredit;
    private TextView txtNoDebit;
    private TextView txtTotalCreditAmount;
    private TextView txtTotalDebitAmount;

    public static AccountGlanceFragment newInstance() {
        return new AccountGlanceFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_acc_glance, container, false);
        rootView.setLayoutParams(new LayoutParams(-1, -1));
        getViews(rootView);
        return rootView;
    }

    private void getViews(View view) {
        this.mResource = getResources();
        this.Transaction_listviewCredit = (ListView) view.findViewById(R.id.TransactionListCredit);
        this.Transaction_listviewDebit = (ListView) view.findViewById(R.id.TransactionListDebit);
        this.txtTotalDebitAmount = (TextView) view.findViewById(R.id.txtDebit);
        this.txtTotalCreditAmount = (TextView) view.findViewById(R.id.txtCredit);
        this.txtNoDebit = (TextView) view.findViewById(R.id.txtNoDebitFound);
        this.txtNoCredit = (TextView) view.findViewById(R.id.txtNoCreditFound);
        boolean isInternetPresent = AppUtils.isNetworkAvailable(getActivity());
        if (!SessionManager.isProUser() && isInternetPresent) {
            this.mAdView = new AdView(getActivity());
            this.mAdView = (AdView) view.findViewById(R.id.adView);
            this.mAdView.setVisibility(View.VISIBLE);
            this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("AccountGlanceFragment", "AccountGlanceFragment setUserVisibleHint");
    }

    public void Set_Referash_Data(boolean isSortByName) {
        LayoutParams params;
        Log.e("AccountGlanceFragment", "AccountGlanceFragment setUserVisibleHint");
        String mRsSymbol = SessionManager.getCurrency(getActivity());
        ArrayList<ArrayList<Transaction>> data = new FetchData().getAccountGlance(isSortByName);
        ArrayList<Transaction> listCr = (ArrayList) data.get(0);
        ArrayList<Transaction> listDr = (ArrayList) data.get(1);
        Transaction total = (Transaction) ((ArrayList) data.get(2)).get(0);
        this.strCredit = mRsSymbol + "" + this.newFormat.format(total.getCraditAmount()) + "/-";
        this.strDebit = mRsSymbol + "" + this.newFormat.format(total.getDebitAmount()) + "/-";
        this.txtTotalCreditAmount.setText(this.mResource.getString(R.string.txt_Credit) + "\n(" + this.strCredit + ")");
        this.txtTotalDebitAmount.setText(this.mResource.getString(R.string.txt_Debit) + "\n(" + this.strDebit + ")");
        GlanceAdapter TAdapterCredit;
        if (listCr.size() == 0) {
            this.txtNoCredit.setVisibility(View.VISIBLE);
            params = this.txtNoCredit.getLayoutParams();
            params.height = 100;
            this.txtNoCredit.setLayoutParams(params);
            TAdapterCredit = new GlanceAdapter(getActivity().getApplicationContext(), listCr);
            this.Transaction_listviewCredit.setAdapter(TAdapterCredit);
            TAdapterCredit.notifyDataSetChanged();
        } else {
            this.txtNoCredit.setVisibility(View.INVISIBLE);
            params = this.txtNoCredit.getLayoutParams();
            params.height = 0;
            this.txtNoCredit.setLayoutParams(params);
            TAdapterCredit = new GlanceAdapter(getActivity().getApplicationContext(), listCr);
            this.Transaction_listviewCredit.setAdapter(TAdapterCredit);
            TAdapterCredit.notifyDataSetChanged();
        }
        if (listDr.size() == 0) {
            params = this.txtNoDebit.getLayoutParams();
            params.height = 100;
            this.txtNoDebit.setVisibility(View.VISIBLE);
            this.txtNoDebit.setLayoutParams(params);
            return;
        }
        this.txtNoDebit.setVisibility(View.INVISIBLE);
        params = this.txtNoDebit.getLayoutParams();
        params.height = 0;
        this.txtNoDebit.setLayoutParams(params);
        GlanceAdapter TAdapterDebit = new GlanceAdapter(getActivity().getApplicationContext(), listDr);
        this.Transaction_listviewDebit.setAdapter(TAdapterDebit);
        TAdapterDebit.notifyDataSetChanged();
    }

    public void onPauseFragment() {
    }

    public void onResumeFragment(int newPosition) {
    }

    public void onClick(View v) {
    }

    private void export(int index) {
        this.mColumns.clear();
        this.mValues.clear();
        this.mColumns.add(" " + this.mResource.getString(R.string.txt_Serialno));
        this.mColumns.add(" " + this.mResource.getString(R.string.txt_Credit));
        this.mColumns.add(" " + this.mResource.getString(R.string.txt_Debit));
        PdfDao header = new PdfDao();
        header.setFirst(AppConstants.ACCOUNT_GLANCE_REPORT);
        header.setSecond(this.strCredit);
        header.setThird(this.strDebit);
        String mRsSymbol = SessionManager.getCurrency(getActivity());
        NumberFormat newFormat = new DecimalFormat(((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("en", "IN"))).toPattern().replace("¤", "").trim());
        int i = 1;
        Iterator it = new FetchData().getCategoryBalance(null).iterator();
        while (it.hasNext()) {
            Transaction dao = (Transaction) it.next();
            PdfDao row = new PdfDao();
            row.setFirst("" + i);
            if (dao.getDebitAmount() > dao.getCraditAmount()) {
                String balance_amount = mRsSymbol + "" + newFormat.format(dao.getDebitAmount() - dao.getCraditAmount());
                row.setSecond("");
                row.setThird("    " + dao.getAccName() + "\n   " + balance_amount + " Dr");
            } else {
                PdfDao pdfDao = row;
                pdfDao.setSecond("    " + dao.getAccName() + "\n   " + (mRsSymbol + "" + newFormat.format(dao.getCraditAmount() - dao.getDebitAmount()) + "/-") + " Cr");
                row.setThird("");
            }
            i++;
            this.mValues.add(row);
        }
        switch (index) {
            case 0:
                new GeneratePdf(getActivity(), header, this.mColumns, this.mValues, 14).pdf(getActivity());
                return;
            case 1:
                new GenerateExcel(getActivity(), header, this.mColumns, this.mValues, 12).excel(getActivity());
                return;
            default:
                return;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_export_browse, menu);
        menu.add(1, 1, 1, "Sort by Balance");
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(1);
        if (this.isSortByName) {
            item.setTitle("Sort by Balance");
        } else {
            item.setTitle("Sort by Name");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = true;
        Intent i;
        switch (item.getItemId()) {
            case 1:
                if (this.isSortByName) {
                    z = false;
                }
                this.isSortByName = z;
                Set_Referash_Data(this.isSortByName);
                return false;
            case R.id.menu_browse:
                i = new Intent(getActivity(), WebServerActivity.class);
                i.putExtra("TYPE", 4);
                startActivity(i);
                return false;
            case R.id.menu_export_exel:
                export(1);
                return false;
            case R.id.menu_export_pdf:
                export(0);
                return false;
            case R.id.menu_usecal:
                try {
                    i = new Intent();
                    i.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
                    startActivity(i);
                    return false;
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Calculator not Found", Toast.LENGTH_SHORT).show();
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onPause() {
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        Set_Referash_Data(this.isSortByName);
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
