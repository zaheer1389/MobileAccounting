package com.adslinfotech.mobileaccounting.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.adapter.NavigationAdapter;
import br.liveo.navigationviewpagerliveo.NavigationList;
import br.liveo.sliding.SlidingTabLayout;
import br.liveo.sliding.SlidingTabLayout.TabColorizer;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityChangePassword;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditProfile;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCredit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCreditDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityInterAccount;
import com.adslinfotech.mobileaccounting.activities.report.AccountLedgerActivity;
import com.adslinfotech.mobileaccounting.activities.report.AccountListActivity;
import com.adslinfotech.mobileaccounting.activities.report.LastTransactionActivity;
import com.adslinfotech.mobileaccounting.activities.report.LastYearActivity;
import com.adslinfotech.mobileaccounting.activities.report.OverallCategoryActivity;
import com.adslinfotech.mobileaccounting.activities.report.ShowReminderActivity;
import com.adslinfotech.mobileaccounting.activities.utils.ActivityUserManual;
import com.adslinfotech.mobileaccounting.adapter.pager.SectionsPagerAdapter;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.fragment.ChooseDateActivity;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchCategory;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppConstants.HTML;
import com.adslinfotech.mobileaccounting.utils.AppConstants.PERMISSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.URI;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DashboardActivity extends SimpleAccountingActivity implements OnItemClickListener {
    private boolean isProfileMenu;
    private ListView listDrawer;
    private String mBackupPath = null;
    private DrawerLayout mDrawer;
    private ImageView mImgDrawerArrow;
    private ViewPager mViewPager;
    private NavigationAdapter navigationAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        init();
        sendBackup();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        setContentView((int) R.layout.activity_main);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        this.mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        getView();
        setTabs();
    }

    private void setTabs() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        this.mViewPager = (ViewPager) findViewById(R.id.container);
        this.mViewPager.setAdapter(mSectionsPagerAdapter);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setCustomTabColorizer(new TabColorizer() {
            public int getIndicatorColor(int position) {
                return DashboardActivity.this.getResources().getColor(R.color.tabsScrollColor);
            }

            public int getDividerColor(int position) {
                return 0;
            }
        });
        tabs.setViewPager(this.mViewPager);
    }

    private void sendBackup() {
        Date today = new Date();
        if (new Date(SimpleAccountingApp.getPreference().getLong(SessionManager.PREF_BACKUP_MAIL, today.getTime())).before(today)) {
            this.mBackupPath = DatabaseExportImport.exportDb();
            showPositiveAlert(null, getResources().getString(R.string.msg_backup));
            sendBackToMail(this.mBackupPath);
            SimpleAccountingApp.getPreference().edit().putLong(SessionManager.PREF_BACKUP_MAIL, today.getTime() + AppConstants.FIVE_DATE).apply();
        }
    }

    private void getView() {
        this.listDrawer = (ListView) findViewById(R.id.listDrawer);
        if (this.listDrawer != null) {
            this.navigationAdapter = NavigationList.getNavigationAdapter(this, R.array.nav_menu_items, this.isProfileMenu);
        }
        this.listDrawer.setAdapter(this.navigationAdapter);
        this.listDrawer.setOnItemClickListener(this);
        Button btnUpgrade = (Button) findViewById(R.id.btn_upgrade);
        if (SessionManager.isProUser()) {
            btnUpgrade.setVisibility(View.GONE);
        } else {
            btnUpgrade.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DashboardActivity.this.showProAppLink();
                }
            });
        }
        getUserDetailView();
        findViewById(R.id.header_nav).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardActivity.this.toggleList();
            }
        });
    }

    public void getUserDetailView() {
        ((TextView) findViewById(R.id.tv_name_nav)).setText(AppUtils.capitalWord(SessionManager.getName()));
        ((TextView) findViewById(R.id.tv_email_nav)).setText(SessionManager.getEmail());
        ImageView imgUser = (ImageView) findViewById(R.id.img_profile_nav);
        byte[] mUserImage = new FetchData().getProfileDetail().getImage();
        this.mImgDrawerArrow = (ImageView) findViewById(R.id.img_drawer_arrow);
        AppUtils.setImage(imgUser, mUserImage);
    }

    private void toggleList() {
        if (this.isProfileMenu) {
            this.isProfileMenu = false;
            if (this.listDrawer != null) {
                this.navigationAdapter = NavigationList.getNavigationAdapter(this, R.array.nav_menu_items, this.isProfileMenu);
            }
            this.listDrawer.setAdapter(this.navigationAdapter);
            this.navigationAdapter.notifyDataSetChanged();
            this.mImgDrawerArrow.setImageResource(R.drawable.left_panel_arrow_down);
            return;
        }
        this.isProfileMenu = true;
        if (this.listDrawer != null) {
            this.navigationAdapter = NavigationList.getNavigationAdapter(this, R.array.nav_menu_items_profile, this.isProfileMenu);
        }
        this.listDrawer.setAdapter(this.navigationAdapter);
        this.navigationAdapter.notifyDataSetChanged();
        this.mImgDrawerArrow.setImageResource(R.drawable.left_panel_arrow_up);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok_alert_positive:
                if (this.mBackupPath != null) {
                    sendBackToMail(this.mBackupPath);
                }
                super.onClick(v);
                return;
            default:
                super.onClick(v);
                return;
        }
    }

    public void onPositiveClick(int from) {
        if (from == 64) {
            super.onPositiveClick(from);
            setResult(AppConstants.ACTIVITY_FINISH);
            super.onBackPressed();
            return;
        }
        super.onPositiveClick(from);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        try {
            if (this.mDrawer.isDrawerOpen((int) GravityCompat.START)) {
                this.mDrawer.closeDrawer((int) GravityCompat.START);
            } else if (this.mViewPager.getCurrentItem() != 0) {
                this.mViewPager.setCurrentItem(0);
            } else {
                SimpleAccountingApp.showAds();
                DatabaseExportImport.storeFinalDb();
                showAlertExitApp(getResources().getString(R.string.txt_Exit), 64);
            }
        } catch (Exception e) {
            if (this.mDrawer.isDrawerOpen((int) GravityCompat.END)) {
                this.mDrawer.closeDrawer((int) GravityCompat.END);
            } else if (this.mViewPager.getCurrentItem() != 0) {
                this.mViewPager.setCurrentItem(0);
            } else {
                SimpleAccountingApp.showAds();
                DatabaseExportImport.storeFinalDb();
                showAlertExitApp(getResources().getString(R.string.txt_Exit), 64);
            }
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.e("onItemClick", "position: " + position);
        toggleDrawer();
        Intent intent;
        if (this.isProfileMenu) {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, DbActivity.class));
                    return;
                case 1:
                    startActivity(new Intent(this, ActivityEditProfile.class));
                    return;
                case 2:
                    startActivity(new Intent(this, AccountListActivity.class));
                    return;
                case 3:
                    startActivity(new Intent(this, ActivityChangePassword.class));
                    return;
                case 4:
                    if (isStoragePermissionGranted(PERMISSION.STORAGE_OPEN_MANAGE_FILES)) {
                        startActivity(new Intent(this, ManageFilesActivity.class));
                        return;
                    } else {
                        Toast.makeText(this, R.string.permission_storage, Toast.LENGTH_LONG).show();
                        return;
                    }
                case 5:
                    startActivityForResult(new Intent(getApplicationContext(), ActivityPreferences.class), 521);
                    return;
                case 6:
                    openCalculator();
                    return;
                case 7:
                    startActivityForResult(new Intent(getApplicationContext(), ActivityPreferences.class), 521);
                    return;
                case 8:
                    intent = new Intent(getApplicationContext(), ActivityUserManual.class);
                    intent.putExtra(EXTRA.HTML_FILE, HTML.USER_MANUAL);
                    startActivityForResult(intent, 1);
                    return;
                case 9:
                    new ShareHelper(this, SessionManager.getName() + "Suggest you Simple Accounting App for Andoroid Mobile", AppConstants.SHARE_APP_MSG).share();
                    return;
                case 10:
                    startActivity(new Intent("android.intent.action.VIEW", URI.PLAY_STORE_ADSL));
                    return;
                case 11:
                    intent = new Intent(getApplicationContext(), ActivityUserManual.class);
                    intent.putExtra(EXTRA.HTML_FILE, HTML.FAQ);
                    startActivityForResult(intent, 1);
                    return;
                case 12:
                    startActivity(new Intent("android.intent.action.VIEW", URI.YOUTUBE_VIDEO));
                    return;
                case 13:
                    startActivity(new Intent("android.intent.action.VIEW", URI.WEBSITE));
                    return;
                case 14:
                    startActivity(new Intent("android.intent.action.VIEW", URI.WEBSITE_DESKTOP));
                    return;
                case 15:
                    intent = new Intent(getApplicationContext(), ActivityUserManual.class);
                    intent.putExtra(EXTRA.HTML_FILE, HTML.POLICY);
                    startActivityForResult(intent, 1);
                    return;
                default:
                    Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
        switch (position) {
            case 0:
                return;
            case 1:
                startActivity(new Intent(getApplicationContext(), ActivityAddCredit.class));
                return;
            case 2:
                startActivity(new Intent(getApplicationContext(), ActivityAddDebit.class));
                return;
            case 3:
                startActivity(new Intent(getApplicationContext(), ActivityAddCreditDebit.class));
                return;
            case 4:
                startActivity(new Intent(getApplicationContext(), ActivityInterAccount.class));
                return;
            case 5:
                startActivity(new Intent(getApplicationContext(), AccountLedgerActivity.class));
                return;
            case 6:
                intent = new Intent(this, ChooseDateActivity.class);
                intent.putExtra("index", 7);
                startActivity(intent);
                return;
            case 7:
                intent = new Intent(getApplicationContext(), ActivitySearchCategory.class);
                intent.putExtra("index", 6);
                startActivity(intent);
                return;
            case 8:
                startActivity(new Intent(getApplicationContext(), OverallCategoryActivity.class));
                return;
            case 9:
                intent = new Intent(getApplicationContext(), ActivitySearchCategory.class);
                intent.putExtra("index", 2);
                startActivity(intent);
                return;
            case 10:
                intent = new Intent(this, ChooseDateActivity.class);
                intent.putExtra("index", 3);
                startActivity(intent);
                return;
            case 11:
                intent = new Intent(this, ChooseDateActivity.class);
                intent.putExtra("index", 8);
                startActivity(intent);
                return;
            case 12:
                startActivity(new Intent(this, LastTransactionActivity.class));
                return;
            case 13:
                startActivity(new Intent(this, LastYearActivity.class));
                return;
            case 14:
                startActivity(new Intent(getApplicationContext(), ShowReminderActivity.class));
                return;
            default:
                Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
                return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION.STORAGE_OPEN_MANAGE_FILES && grantResults[0] == 0) {
            startActivity(new Intent(this, ManageFilesActivity.class));
        }
    }

    private void openCalculator() {
        ArrayList<HashMap<String, Object>> items = new ArrayList();
        PackageManager pm = getPackageManager();
        for (PackageInfo pi : pm.getInstalledPackages(0)) {
            if (pi.packageName.toString().toLowerCase().contains("calcul")) {
                HashMap<String, Object> map = new HashMap();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }
        if (items.size() >= 1) {
            Intent i = pm.getLaunchIntentForPackage((String) ((HashMap) items.get(0)).get("packageName"));
            if (i != null) {
                startActivity(i);
                return;
            }
            return;
        }
        Toast.makeText(this, "Sorry! Calculator not found.", Toast.LENGTH_SHORT).show();
    }

    private void toggleDrawer() {
        try {
            if (this.mDrawer.isDrawerOpen(5)) {
                this.mDrawer.closeDrawer(5);
            } else {
                this.mDrawer.openDrawer(5);
            }
        } catch (Exception e) {
            if (this.mDrawer.isDrawerOpen(3)) {
                this.mDrawer.closeDrawer(3);
            } else {
                this.mDrawer.openDrawer(3);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 521 && resultCode == -1) {
            Locale locale = new Locale(SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LANGUAGE, "en"));
            Configuration newConfig = new Configuration();
            newConfig.locale = locale;
            onConfigurationChanged(newConfig);
        }
    }
}
