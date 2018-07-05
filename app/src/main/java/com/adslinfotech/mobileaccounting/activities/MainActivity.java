package com.adslinfotech.mobileaccounting.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
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

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityChangePassword;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditProfile;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCredit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCreditDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddDebit;
import com.adslinfotech.mobileaccounting.activities.home.AccountGlanceFragment;
import com.adslinfotech.mobileaccounting.activities.home.AccountListFragment;
import com.adslinfotech.mobileaccounting.activities.home.CategoryListFragment;
import com.adslinfotech.mobileaccounting.activities.home.HomeFragment;
import com.adslinfotech.mobileaccounting.activities.home.NoteListFragment;
import com.adslinfotech.mobileaccounting.activities.home.ReminderListFragment;
import com.adslinfotech.mobileaccounting.activities.report.AccountLedgerActivity;
import com.adslinfotech.mobileaccounting.activities.report.AccountListActivity;
import com.adslinfotech.mobileaccounting.activities.report.LastTransactionActivity;
import com.adslinfotech.mobileaccounting.activities.report.LastYearActivity;
import com.adslinfotech.mobileaccounting.activities.report.OverallCategoryActivity;
import com.adslinfotech.mobileaccounting.activities.report.ShowReminderActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.ui.ActivityFAQ;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.ActivitySearchCategory;
import com.adslinfotech.mobileaccounting.ui.ActivityUserManual;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends SimpleAccountingActivity implements OnItemClickListener
{
  private static final long FIVE_DATE = 1080000000L;
  private boolean isCloseApp;
  private boolean isProfileMenu;
  private ListView listDrawer;
  private DrawerLayout mDrawer;
  private ImageView mImgDrawerArrow;
  private SectionsPagerAdapter mSectionsPagerAdapter;
  private ViewPager mViewPager;
  private NavigationAdapter navigationAdapter;
  private SlidingTabLayout tabs;
  
  private void getView()
  {
    this.listDrawer = ((ListView)findViewById(R.id.listDrawer));
    if (this.listDrawer != null) {
      this.navigationAdapter = NavigationList.getNavigationAdapter(this, 2131165186, this.isProfileMenu);
    }
    this.listDrawer.setAdapter(this.navigationAdapter);
    this.listDrawer.setOnItemClickListener(this);
    Button localButton = (Button)findViewById(R.id.btn_upgrade);
    if (SessionManager.isProUser()) {
      localButton.setVisibility(View.GONE);
    }
    getUserDetailView();
    findViewById(R.id.header_nav).setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MainActivity.this.toggleList();
      }
    });
    localButton.setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MainActivity.this.showProAppLink();
      }
    });
    return;
  }
  
  private void init()
  {
    Object localObject = (Toolbar)findViewById(R.id.toolbar);
    setSupportActionBar((Toolbar)localObject);
    this.mDrawer = ((DrawerLayout)findViewById(R.id.drawer_layout));
    localObject = new ActionBarDrawerToggle(this, this.mDrawer, (Toolbar)localObject, R.string.drawer_open, R.string.drawer_close);
    this.mDrawer.setDrawerListener((DrawerListener)localObject);
    ((ActionBarDrawerToggle)localObject).syncState();
    getView();
    setTabs();
  }
  
  private void openCalculator()
  {
    Object localObject = new ArrayList();
    PackageManager localPackageManager = getPackageManager();
    Iterator localIterator = localPackageManager.getInstalledPackages(0).iterator();
    while (localIterator.hasNext())
    {
      PackageInfo localPackageInfo = (PackageInfo)localIterator.next();
      if (localPackageInfo.packageName.toString().toLowerCase().contains("calcul"))
      {
        HashMap localHashMap = new HashMap();
        localHashMap.put("appName", localPackageInfo.applicationInfo.loadLabel(localPackageManager));
        localHashMap.put("packageName", localPackageInfo.packageName);
        ((ArrayList)localObject).add(localHashMap);
      }
    }
    if (((ArrayList)localObject).size() >= 1)
    {
      localObject = localPackageManager.getLaunchIntentForPackage((String)((HashMap)((ArrayList)localObject).get(0)).get("packageName"));
      if (localObject != null) {
        startActivity((Intent)localObject);
      }
      return;
    }
    Toast.makeText(this, "Sorry! Calculator not found.", Toast.LENGTH_SHORT).show();
  }
  
  private void openVideoHelp()
  {

  }
  
  private void sendBackup()
  {
    Date localDate = new Date();
    if (new Date(SimpleAccountingApp.getPreference().getLong("PREF_BACKUP_MAIL", localDate.getTime())).before(localDate))
    {
      //this.path = DatabaseExportImport.exportDb();
      showPositiveAlert(null, "It seems a long time you haven't been backed up. Please backups your data.");
      long l = localDate.getTime();
      SimpleAccountingApp.getPreference().edit().putLong("PREF_BACKUP_MAIL", l + 1080000000L).commit();
    }
  }
  
  private void setTabs()
  {
    this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
    this.mViewPager = ((ViewPager)findViewById(R.id.container));
    this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
    this.tabs = ((SlidingTabLayout)findViewById(R.id.tabs));
    this.tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
    {
      public int getDividerColor(int paramAnonymousInt)
      {
        return 0;
      }
      
      public int getIndicatorColor(int paramAnonymousInt)
      {
        return MainActivity.this.getResources().getColor(R.color.tabsScrollColor);
      }
    });
    this.tabs.setViewPager(this.mViewPager);
  }
  
  private void toggleDrawer()
  {
    try
    {
      if (this.mDrawer.isDrawerOpen(5))
      {
        this.mDrawer.closeDrawer(5);
        return;
      }
      this.mDrawer.openDrawer(5);
      return;
    }
    catch (Exception localException)
    {
      if (this.mDrawer.isDrawerOpen(3))
      {
        this.mDrawer.closeDrawer(3);
        return;
      }
      this.mDrawer.openDrawer(3);
    }
  }
  
  private void toggleList()
  {
    if (this.isProfileMenu)
    {
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
  
  public void getUserDetailView()
  {
    ((TextView)findViewById(R.id.tv_name_nav)).setText(AppUtils.capitalWord(SessionManager.getName()));
    ((TextView)findViewById(R.id.tv_email_nav)).setText(SessionManager.getEmail());
    ImageView localImageView = (ImageView)findViewById(R.id.img_profile_nav);
    byte[] arrayOfByte = new FetchData().getProfileDetail().getImage();
    this.mImgDrawerArrow = ((ImageView)findViewById(R.id.img_drawer_arrow));
    AppUtils.setImage(localImageView, arrayOfByte);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if ((paramInt1 == 5) && (paramInt2 == -1))
    {
      Locale locale = new Locale(SimpleAccountingApp.getPreference().getString("PREF_LANGUAGE", "en"));
      Configuration localConfiguration = new Configuration();
      localConfiguration.locale = locale;
      onConfigurationChanged(localConfiguration);
    }
  }
  
  public void onBackPressed()
  {
    try
    {
      if (this.mDrawer.isDrawerOpen(5))
      {
        this.mDrawer.closeDrawer(5);
        return;
      }
      if (this.mViewPager.getCurrentItem() != 0)
      {
        this.mViewPager.setCurrentItem(0);
        return;
      }
    }
    catch (Exception localException)
    {
      if (this.mDrawer.isDrawerOpen(3))
      {
        this.mDrawer.closeDrawer(3);
        this.isCloseApp = true;
        showAlertExitApp(getResources().getString(R.string.txt_Exit), 64);
        return;
      }
      if (this.mViewPager.getCurrentItem() != 0)
      {
        this.mViewPager.setCurrentItem(0);
        return;
      }
      this.isCloseApp = true;
      showAlertExitApp(getResources().getString(R.string.txt_Exit), 64);
    }
  }
  
  public void onClick(View paramView)
  {


  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    getBaseContext().getResources().updateConfiguration(paramConfiguration, getBaseContext().getResources().getDisplayMetrics());
    setContentView(R.layout.activity_main);
    init();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_main);
    init();
    sendBackup();
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    Log.e("onItemClick", "position: " + paramInt);
    toggleDrawer();
    if (!this.isProfileMenu)
    {
      switch (paramInt)
      {
      default: 
        Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
      case 0: 
        return;
      case 1: 
        startActivityForResult(new Intent(getApplicationContext(), ActivityAddCredit.class), 1);
        return;
      case 2: 
        startActivityForResult(new Intent(getApplicationContext(), ActivityAddDebit.class), 1);
        return;
      case 3: 
        startActivityForResult(new Intent(getApplicationContext(), ActivityAddCreditDebit.class), 1);
        return;
      case 4: 
        startActivity(new Intent(getApplicationContext(), AccountLedgerActivity.class));
        return;
      /*case 5:
        Intent intent = new Intent(this, ChooseDateFragment.class);
        intent.putExtra("index", 7);
        intent.putExtra("EXTRA", true);
        startActivity(intent);
        return;*/
      case 6:
        Intent intent = new Intent(getApplicationContext(), ActivitySearchCategory.class);
        intent.putExtra("index", 6);
        startActivity(intent);
        return;
      case 7: 
        startActivity(new Intent(getApplicationContext(), OverallCategoryActivity.class));
        return;
      case 8: 
        intent = new Intent(getApplicationContext(), ActivitySearchCategory.class);
        intent.putExtra("index", 2);
        startActivity(intent);
        return;
      /*case 9:
        intent = new Intent(this, ChooseDateFragment.class);
        intent.putExtra("index", 3);
        startActivity(intent);
        return;
      case 10:
        intent = new Intent(this, ChooseDateFragment.class);
        intent.putExtra("index", 8);
        startActivity(intent);
        return;*/
      case 11: 
        startActivity(new Intent(this, LastTransactionActivity.class));
        return;
      case 12: 
        startActivity(new Intent(this, LastYearActivity.class));
        return;
        case 13:
          startActivity(new Intent(getApplicationContext(), ShowReminderActivity.class));
          return;
      }
    }
    switch (paramInt)
    {
    default: 
      Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
      return;
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
      startActivity(new Intent(this, ManageFilesActivity.class));
      return;
    case 5: 
      startActivityForResult(new Intent(getApplicationContext(), ActivityPreferences.class), 5);
      return;
    case 6: 
      openCalculator();
      return;
    case 7: 
      startActivityForResult(new Intent(getApplicationContext(), ActivityPreferences.class), 5);
      return;
    case 8: 
      startActivityForResult(new Intent(getApplicationContext(), ActivityUserManual.class), 1);
      return;
    case 9: 
      new ShareHelper(this, SessionManager.getName() + "Suggest you Simple Accounting App for Andoroid Mobile", "🌿🌹🌿🌹🌿🌹🌿🌹\nSimple Accounting APP\n(Android / iOS / Desktop)\nNo. 1 Personal Accounting APP\n\nSome of the features are highlighted below:\n\n* Very Simple Design & easy to understand \n* Show individual Account Balances (Ledger)\n* Multilingual Support available \n* Database is included within the application (No online storage)\n* Backup & Restore Facility (Database backup via email, dropbox and Google Drive also)\n\nADSL Infotech\nPlay Store Link\nhttps://play.google.com/store/apps/details?id=com.adslinfotech.mobileaccounting\nVisit our Website For Desktop Setup\nwww.indianandroidstore.com\n🍁🍁🍁🍁🍁🍁🍁🍁").share();
      return;
    case 10: 
      startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://bit.ly/1LGUjOE")));
      return;
    case 11: 
      startActivityForResult(new Intent(getApplicationContext(), ActivityFAQ.class), 1);
      return;
    case 12: 
      openVideoHelp();
      return;
    }
  }
  
  public class SectionsPagerAdapter extends FragmentPagerAdapter
  {
    public SectionsPagerAdapter(FragmentManager paramFragmentManager)
    {
      super(paramFragmentManager);
    }
    
    public int getCount()
    {
      return 6;
    }
    
    public Fragment getItem(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return AccountListFragment.newInstance();
      case 0: 
        return HomeFragment.newInstance();
      case 1: 
        return AccountListFragment.newInstance();
      case 2: 
        return AccountGlanceFragment.newInstance();
      case 3: 
        return ReminderListFragment.newInstance();
      case 4: 
        return NoteListFragment.newInstance();
        case 5:
          return CategoryListFragment.newInstance();
      }

    }
    
    public CharSequence getPageTitle(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 0: 
        return MainActivity.this.getResources().getString(R.string.txt_home);
      case 1: 
        return MainActivity.this.getResources().getString(R.string.txt_accounts);
      case 2: 
        return MainActivity.this.getResources().getString(R.string.txt_galance);
      case 3: 
        return MainActivity.this.getResources().getString(R.string.txt_Reminders);
      case 4: 
        return MainActivity.this.getResources().getString(R.string.txt_notes);
        case 5:
        return MainActivity.this.getResources().getString(R.string.txt_category);
      }

    }
  }
}
