package com.adslinfotech.mobileaccounting.activities.base;

import android.graphics.Bitmap;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEdit;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.ArrayList;
import java.util.List;

public abstract class ActivityMultipleSelection extends ActivityEdit {
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
                    if (ActivityMultipleSelection.this.multiselect_list.size() == 0) {
                        ActivityMultipleSelection.this.showPositiveAlert(null, ActivityMultipleSelection.this.getResources().getString(R.string.msg_no_item_delete));
                        return true;
                    }
                    ActivityMultipleSelection.this.showAlertExitApp(ActivityMultipleSelection.this.getResources().getString(R.string.alert_delete_multiple_transaction), 3);
                    return true;
                case R.id.menu_select_all:
                    if (ActivityMultipleSelection.this.isAllSelected) {
                        ActivityMultipleSelection.this.multiselect_list.clear();
                        ActivityMultipleSelection.this.refreshAdapterData();
                        ActivityMultipleSelection.this.setActionTitle();
                    } else {
                        ActivityMultipleSelection.this.onSelectAllClick();
                    }
                    ActivityMultipleSelection activityMultipleSelection = ActivityMultipleSelection.this;
                    if (!ActivityMultipleSelection.this.isAllSelected) {
                        z = true;
                    }
                    activityMultipleSelection.isAllSelected = z;
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            ActivityMultipleSelection.this.mActionMode = null;
            ActivityMultipleSelection.this.isMultiSelect = false;
            ActivityMultipleSelection.this.isAllSelected = false;
            ActivityMultipleSelection.this.multiselect_list = new ArrayList();
            ActivityMultipleSelection.this.refreshAdapterData();
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
