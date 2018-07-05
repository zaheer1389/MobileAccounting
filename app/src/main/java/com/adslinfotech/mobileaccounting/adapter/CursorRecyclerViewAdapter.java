package com.adslinfotech.mobileaccounting.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

public abstract class CursorRecyclerViewAdapter<VH extends ViewHolder> extends Adapter<VH> {
    protected boolean isShowFirstRow;
    private Cursor mCursor;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;

    private class NotifyingDataSetObserver extends DataSetObserver {
        private NotifyingDataSetObserver() {
        }

        public void onChanged() {
            super.onChanged();
            CursorRecyclerViewAdapter.this.mDataValid = true;
            CursorRecyclerViewAdapter.this.notifyDataSetChanged();
        }

        public void onInvalidated() {
            super.onInvalidated();
            CursorRecyclerViewAdapter.this.mDataValid = false;
            CursorRecyclerViewAdapter.this.notifyDataSetChanged();
        }
    }

    public abstract void onBindViewHolder(VH vh, Cursor cursor);

    public CursorRecyclerViewAdapter(Cursor cursor) {
        this.mCursor = cursor;
        this.mDataValid = cursor != null;
        this.mRowIdColumn = this.mDataValid ? this.mCursor.getColumnIndex("_id") : -1;
        this.mDataSetObserver = new NotifyingDataSetObserver();
        if (this.mCursor != null) {
            this.mCursor.registerDataSetObserver(this.mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return this.mCursor;
    }

    public int getItemCount() {
        if (!this.mDataValid || this.mCursor == null) {
            return 0;
        }
        return this.mCursor.getCount();
    }

    public long getItemId(int position) {
        if (this.mDataValid && this.mCursor != null && this.mCursor.moveToPosition(position)) {
            return this.mCursor.getLong(this.mRowIdColumn);
        }
        return 0;
    }

    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public void onBindViewHolder(VH viewHolder, int position) {
        if (this.mDataValid) {
            if (this.isShowFirstRow) {
                if (position == 0) {
                    onBindViewHolder( viewHolder, this.mCursor);
                    return;
                }
                position--;
            }
            if (this.mCursor.moveToPosition(position)) {
                onBindViewHolder( viewHolder, this.mCursor);
                return;
            }
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        throw new IllegalStateException("this should only be called when the cursor is valid");
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == this.mCursor) {
            return null;
        }
        Cursor oldCursor = this.mCursor;
        if (!(oldCursor == null || this.mDataSetObserver == null)) {
            oldCursor.unregisterDataSetObserver(this.mDataSetObserver);
        }
        this.mCursor = newCursor;
        if (this.mCursor != null) {
            if (this.mDataSetObserver != null) {
                this.mCursor.registerDataSetObserver(this.mDataSetObserver);
            }
            this.mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            this.mDataValid = true;
            notifyDataSetChanged();
            return oldCursor;
        }
        this.mRowIdColumn = -1;
        this.mDataValid = false;
        notifyDataSetChanged();
        return oldCursor;
    }
}
