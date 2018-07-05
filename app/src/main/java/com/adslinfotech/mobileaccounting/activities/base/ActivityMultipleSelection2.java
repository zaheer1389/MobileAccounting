package com.adslinfotech.mobileaccounting.activities.base;

import android.graphics.Bitmap;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.ReportSearchActivity;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.ArrayList;
import java.util.List;

public abstract class ActivityMultipleSelection2 extends ReportSearchActivity {
    protected static final int MULTIPLE_DELETE = 3;
    protected boolean isAllSelected = false;
    protected boolean isMultiSelect = false;
    protected ActionMode mActionMode;
    protected Callback mActionModeCallback = new Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_multiple_delete, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean z = false;
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    if (ActivityMultipleSelection2.this.multiselect_list.size() == 0) {
                        ActivityMultipleSelection2.this.showPositiveAlert(null, ActivityMultipleSelection2.this.getResources().getString(R.string.msg_no_item_delete));
                        return true;
                    }
                    ActivityMultipleSelection2.this.showAlertExitApp(ActivityMultipleSelection2.this.getResources().getString(R.string.alert_delete_multiple_transaction), 3);
                    return true;
                case R.id.menu_select_all:
                    if (ActivityMultipleSelection2.this.isAllSelected) {
                        ActivityMultipleSelection2.this.multiselect_list.clear();
                        ActivityMultipleSelection2.this.refreshAdapterData();
                        ActivityMultipleSelection2.this.setActionTitle();
                    } else {
                        ActivityMultipleSelection2.this.onSelectAllClick();
                    }
                    ActivityMultipleSelection2 activityMultipleSelection2 = ActivityMultipleSelection2.this;
                    if (!ActivityMultipleSelection2.this.isAllSelected) {
                        z = true;
                    }
                    activityMultipleSelection2.isAllSelected = z;
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            ActivityMultipleSelection2.this.mActionMode = null;
            ActivityMultipleSelection2.this.isMultiSelect = false;
            ActivityMultipleSelection2.this.isAllSelected = false;
            ActivityMultipleSelection2.this.multiselect_list = new ArrayList();
            ActivityMultipleSelection2.this.refreshAdapterData();
        }
    };
    protected List<Transaction> multiselect_list = new ArrayList();

    protected abstract void onSelectAllClick();

    protected abstract void refreshAdapterData();

    public void multi_select(Transaction item) {
        if (this.mActionMode != null) {
            if (this.multiselect_list.contains(item)) {
                this.multiselect_list.remove(item);
            } else {
                this.multiselect_list.add(item);
            }
            setActionTitle();
            refreshAdapterData();
        }
    }

    protected void setActionTitle() {
        if (this.multiselect_list.size() > 0) {
            this.mActionMode.setTitle("" + this.multiselect_list.size());
        } else {
            this.mActionMode.setTitle("");
        }
    }

    public void onPositiveClick(int from) {
        checkPasswordRequired(from);
    }

    protected void setImage(Bitmap thumbnail) {
    }
}
