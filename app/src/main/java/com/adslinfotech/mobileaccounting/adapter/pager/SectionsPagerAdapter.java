package com.adslinfotech.mobileaccounting.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.home.AccountGlanceFragment;
import com.adslinfotech.mobileaccounting.activities.home.AccountListFragment;
import com.adslinfotech.mobileaccounting.activities.home.CategoryListFragment;
import com.adslinfotech.mobileaccounting.activities.home.HomeFragment;
import com.adslinfotech.mobileaccounting.activities.home.NoteListFragment;
import com.adslinfotech.mobileaccounting.activities.home.ReminderListFragment;
import com.adslinfotech.mobileaccounting.ui.SessionManager;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private AccountListFragment accountListFragment;
    private CategoryListFragment categoryListFragment;
    private AccountGlanceFragment glanceFragment;
    private HomeFragment homeFragment;
    private Context mContext;
    private NoteListFragment noteListFragment;
    private ReminderListFragment reminderListFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    public Fragment getItem(int position) {
        SessionManager.incrementInteractionCount();
        switch (position) {
            case 0:
                if (this.homeFragment == null) {
                    this.homeFragment = HomeFragment.newInstance();
                }
                return this.homeFragment;
            case 1:
                if (this.accountListFragment == null) {
                    this.accountListFragment = AccountListFragment.newInstance();
                }
                return this.accountListFragment;
            case 2:
                if (this.glanceFragment == null) {
                    this.glanceFragment = AccountGlanceFragment.newInstance();
                }
                return this.glanceFragment;
            case 3:
                if (this.reminderListFragment == null) {
                    this.reminderListFragment = ReminderListFragment.newInstance();
                }
                return this.reminderListFragment;
            case 4:
                if (this.noteListFragment == null) {
                    this.noteListFragment = NoteListFragment.newInstance();
                }
                return this.noteListFragment;
            case 5:
                if (this.categoryListFragment == null) {
                    this.categoryListFragment = CategoryListFragment.newInstance();
                }
                return this.categoryListFragment;
            default:
                return this.accountListFragment;
        }
    }

    public int getCount() {
        return 6;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return this.mContext.getString(R.string.txt_home);
            case 1:
                return this.mContext.getString(R.string.txt_accounts);
            case 2:
                return this.mContext.getString(R.string.txt_galance);
            case 3:
                return this.mContext.getString(R.string.txt_Reminders);
            case 4:
                return this.mContext.getString(R.string.txt_notes);
            case 5:
                return this.mContext.getString(R.string.txt_category);
            default:
                return null;
        }
    }
}
