package com.adslinfotech.mobileaccounting.tabullar;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class FixedGridLayoutManager extends LayoutManager {
  private static final int DEFAULT_COUNT = 1;
  private static final int DIRECTION_DOWN = 3;
  private static final int DIRECTION_END = 1;
  private static final int DIRECTION_NONE = -1;
  private static final int DIRECTION_START = 0;
  private static final int DIRECTION_UP = 2;
  private static final int REMOVE_INVISIBLE = 1;
  private static final int REMOVE_VISIBLE = 0;
  private static final String TAG = FixedGridLayoutManager.class.getSimpleName();
  private int mChangedPositionCount;
  private int mDecoratedChildHeight;
  private int mDecoratedChildWidth;
  private int mFirstChangedPosition;
  private int mFirstVisiblePosition;
  private int mTotalColumnCount = 1;
  private int mVisibleColumnCount;
  private int mVisibleRowCount;

  public static class LayoutParams extends android.support.v7.widget.RecyclerView.LayoutParams {
    public int column;
    public int row;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(android.view.ViewGroup.LayoutParams source) {
      super(source);
    }

    public LayoutParams(android.support.v7.widget.RecyclerView.LayoutParams source) {
      super(source);
    }
  }

  public static FixedGridLayoutManager newInstance() {
    FixedGridLayoutManager manager = new FixedGridLayoutManager();
    manager.setTotalColumnCount(1);
    return manager;
  }

  public void setTotalColumnCount(int count) {
    this.mTotalColumnCount = count;
    requestLayout();
  }

  public boolean supportsPredictiveItemAnimations() {
    return true;
  }

  public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
    this.mFirstChangedPosition = positionStart;
    this.mChangedPositionCount = itemCount;
  }

  public void onLayoutChildren(Recycler recycler, State state) {
    if (getItemCount() == 0) {
      detachAndScrapAttachedViews(recycler);
    } else if (getChildCount() != 0 || !state.isPreLayout()) {
      int childLeft;
      int childTop;
      if (!state.isPreLayout()) {
        this.mChangedPositionCount = 0;
        this.mFirstChangedPosition = 0;
      }
      if (getChildCount() == 0) {
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);
        this.mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        this.mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
        detachAndScrapView(scrap, recycler);
      }
      updateWindowSizing();
      SparseIntArray removedCache = null;
      if (state.isPreLayout()) {
        int i;
        removedCache = new SparseIntArray(getChildCount());
        for (i = 0; i < getChildCount(); i++) {
          LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
          if (lp.isItemRemoved()) {
            removedCache.put(lp.getViewLayoutPosition(), 0);
          }
        }
        if (removedCache.size() == 0 && this.mChangedPositionCount > 0) {
          for (i = this.mFirstChangedPosition; i < this.mFirstChangedPosition + this.mChangedPositionCount; i++) {
            removedCache.put(i, 1);
          }
        }
      }
      if (getChildCount() == 0) {
        this.mFirstVisiblePosition = 0;
        childLeft = getPaddingLeft();
        childTop = getPaddingTop();
      } else if (state.isPreLayout() || getVisibleChildCount() < state.getItemCount()) {
        View topChild = getChildAt(0);
        childLeft = getDecoratedLeft(topChild);
        childTop = getDecoratedTop(topChild);
        if (!state.isPreLayout() && getVerticalSpace() > getTotalRowCount() * this.mDecoratedChildHeight) {
          this.mFirstVisiblePosition %= getTotalColumnCount();
          childTop = getPaddingTop();
          if (this.mFirstVisiblePosition + this.mVisibleColumnCount > state.getItemCount()) {
            this.mFirstVisiblePosition = Math.max(state.getItemCount() - this.mVisibleColumnCount, 0);
            childLeft = getPaddingLeft();
          }
        }
        int maxFirstRow = getTotalRowCount() - (this.mVisibleRowCount - 1);
        int maxFirstCol = getTotalColumnCount() - (this.mVisibleColumnCount - 1);
        boolean isOutOfRowBounds = getFirstVisibleRow() > maxFirstRow;
        boolean isOutOfColBounds = getFirstVisibleColumn() > maxFirstCol;
        if (isOutOfRowBounds || isOutOfColBounds) {
          int firstRow;
          int firstCol;
          if (isOutOfRowBounds) {
            firstRow = maxFirstRow;
          } else {
            firstRow = getFirstVisibleRow();
          }
          if (isOutOfColBounds) {
            firstCol = maxFirstCol;
          } else {
            firstCol = getFirstVisibleColumn();
          }
          this.mFirstVisiblePosition = (getTotalColumnCount() * firstRow) + firstCol;
          childLeft = getHorizontalSpace() - (this.mDecoratedChildWidth * this.mVisibleColumnCount);
          childTop = getVerticalSpace() - (this.mDecoratedChildHeight * this.mVisibleRowCount);
          if (getFirstVisibleRow() == 0) {
            childTop = Math.min(childTop, getPaddingTop());
          }
          if (getFirstVisibleColumn() == 0) {
            childLeft = Math.min(childLeft, getPaddingLeft());
          }
        }
      } else {
        this.mFirstVisiblePosition = 0;
        childLeft = getPaddingLeft();
        childTop = getPaddingTop();
      }
      detachAndScrapAttachedViews(recycler);
      fillGrid(-1, childLeft, childTop, recycler, state, removedCache);
      if (!state.isPreLayout() && !recycler.getScrapList().isEmpty()) {
        List<ViewHolder> scrapList = recycler.getScrapList();
        HashSet<View> disappearingViews = new HashSet(scrapList.size());
        for (ViewHolder holder : scrapList) {
          View child = holder.itemView;
          if (!((LayoutParams) child.getLayoutParams()).isItemRemoved()) {
            disappearingViews.add(child);
          }
        }
        Iterator it = disappearingViews.iterator();
        while (it.hasNext()) {
          layoutDisappearingView((View) it.next());
        }
      }
    }
  }

  public void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter) {
    removeAllViews();
  }

  private void updateWindowSizing() {
    this.mVisibleColumnCount = (getHorizontalSpace() / this.mDecoratedChildWidth) + 1;
    if (getHorizontalSpace() % this.mDecoratedChildWidth > 0) {
      this.mVisibleColumnCount++;
    }
    if (this.mVisibleColumnCount > getTotalColumnCount()) {
      this.mVisibleColumnCount = getTotalColumnCount();
    }
    this.mVisibleRowCount = (getVerticalSpace() / this.mDecoratedChildHeight) + 1;
    if (getVerticalSpace() % this.mDecoratedChildHeight > 0) {
      this.mVisibleRowCount++;
    }
    if (this.mVisibleRowCount > getTotalRowCount()) {
      this.mVisibleRowCount = getTotalRowCount();
    }
  }

  private void fillGrid(int direction, Recycler recycler, State state) {
    fillGrid(direction, 0, 0, recycler, state, null);
  }

  private void fillGrid(int direction, int emptyLeft, int emptyTop, Recycler recycler, State state, SparseIntArray removedPositions) {
    int i;
    if (this.mFirstVisiblePosition < 0) {
      this.mFirstVisiblePosition = 0;
    }
    if (this.mFirstVisiblePosition >= getItemCount()) {
      this.mFirstVisiblePosition = getItemCount() - 1;
    }
    SparseArray<View> sparseArray = new SparseArray(getChildCount());
    int startLeftOffset = emptyLeft;
    int startTopOffset = emptyTop;
    if (getChildCount() != 0) {
      View topView = getChildAt(0);
      startLeftOffset = getDecoratedLeft(topView);
      startTopOffset = getDecoratedTop(topView);
      switch (direction) {
        case 0:
          startLeftOffset -= this.mDecoratedChildWidth;
          break;
        case 1:
          startLeftOffset += this.mDecoratedChildWidth;
          break;
        case 2:
          startTopOffset -= this.mDecoratedChildHeight;
          break;
        case 3:
          startTopOffset += this.mDecoratedChildHeight;
          break;
      }
      for (i = 0; i < getChildCount(); i++) {
        sparseArray.put(positionOfIndex(i), getChildAt(i));
      }
      for (i = 0; i < sparseArray.size(); i++) {
        detachView((View) sparseArray.valueAt(i));
      }
    }
    switch (direction) {
      case 0:
        this.mFirstVisiblePosition--;
        break;
      case 1:
        this.mFirstVisiblePosition++;
        break;
      case 2:
        this.mFirstVisiblePosition -= getTotalColumnCount();
        break;
      case 3:
        this.mFirstVisiblePosition += getTotalColumnCount();
        break;
    }
    int leftOffset = startLeftOffset;
    int topOffset = startTopOffset;
    for (i = 0; i < getVisibleChildCount(); i++) {
      int nextPosition = positionOfIndex(i);
      int offsetPositionDelta = 0;
      if (state.isPreLayout()) {
        int offsetPosition = nextPosition;
        int offset = 0;
        while (offset < removedPositions.size()) {
          if (removedPositions.valueAt(offset) == 1 && removedPositions.keyAt(offset) < nextPosition) {
            offsetPosition--;
          }
          offset++;
        }
        offsetPositionDelta = nextPosition - offsetPosition;
        nextPosition = offsetPosition;
      }
      if (nextPosition >= 0 && nextPosition < state.getItemCount()) {
        View view = (View) sparseArray.get(nextPosition);
        if (view == null) {
          view = recycler.getViewForPosition(nextPosition);
          addView(view);
          if (!state.isPreLayout()) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.row = getGlobalRowOfPosition(nextPosition);
            lp.column = getGlobalColumnOfPosition(nextPosition);
          }
          measureChildWithMargins(view, 0, 0);
          layoutDecorated(view, leftOffset, topOffset, leftOffset + this.mDecoratedChildWidth, topOffset + this.mDecoratedChildHeight);
        } else {
          attachView(view);
          sparseArray.remove(nextPosition);
        }
        if (i % this.mVisibleColumnCount == this.mVisibleColumnCount - 1) {
          leftOffset = startLeftOffset;
          topOffset += this.mDecoratedChildHeight;
          if (state.isPreLayout()) {
            layoutAppearingViews(recycler, view, nextPosition, removedPositions.size(), offsetPositionDelta);
          }
        } else {
          leftOffset += this.mDecoratedChildWidth;
        }
      }
    }
    for (i = 0; i < sparseArray.size(); i++) {
      recycler.recycleView((View) sparseArray.valueAt(i));
    }
  }

  public void scrollToPosition(int position) {
    if (position >= getItemCount()) {
      Log.e(TAG, "Cannot scroll to " + position + ", item count is " + getItemCount());
      return;
    }
    this.mFirstVisiblePosition = position;
    removeAllViews();
    requestLayout();
  }

  public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
    if (position >= getItemCount()) {
      Log.e(TAG, "Cannot scroll to " + position + ", item count is " + getItemCount());
      return;
    }
    LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
      public PointF computeScrollVectorForPosition(int targetPosition) {
        return new PointF((float) (FixedGridLayoutManager.this.mDecoratedChildWidth * (FixedGridLayoutManager.this.getGlobalColumnOfPosition(targetPosition) - FixedGridLayoutManager.this.getGlobalColumnOfPosition(FixedGridLayoutManager.this.mFirstVisiblePosition))), (float) (FixedGridLayoutManager.this.mDecoratedChildHeight * (FixedGridLayoutManager.this.getGlobalRowOfPosition(targetPosition) - FixedGridLayoutManager.this.getGlobalRowOfPosition(FixedGridLayoutManager.this.mFirstVisiblePosition))));
      }
    };
    scroller.setTargetPosition(position);
    startSmoothScroll(scroller);
  }

  public boolean canScrollHorizontally() {
    return true;
  }

  public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
    if (getChildCount() == 0) {
      return 0;
    }
    View topView = getChildAt(0);
    View bottomView = getChildAt(this.mVisibleColumnCount - 1);
    if (getDecoratedRight(bottomView) - getDecoratedLeft(topView) < getHorizontalSpace()) {
      return 0;
    }
    int delta;
    boolean leftBoundReached = getFirstVisibleColumn() == 0;
    boolean rightBoundReached = getLastVisibleColumn() >= getTotalColumnCount();
    if (dx > 0) {
      if (rightBoundReached) {
        delta = Math.max(-dx, (getHorizontalSpace() - getDecoratedRight(bottomView)) + getPaddingRight());
      } else {
        delta = -dx;
      }
    } else if (leftBoundReached) {
      delta = Math.min(-dx, (-getDecoratedLeft(topView)) + getPaddingLeft());
    } else {
      delta = -dx;
    }
    offsetChildrenHorizontal(delta);
    if (dx > 0) {
      if (getDecoratedRight(topView) < 0 && !rightBoundReached) {
        fillGrid(1, recycler, state);
      } else if (!rightBoundReached) {
        fillGrid(-1, recycler, state);
      }
    } else if (getDecoratedLeft(topView) > 0 && !leftBoundReached) {
      fillGrid(0, recycler, state);
    } else if (!leftBoundReached) {
      fillGrid(-1, recycler, state);
    }
    return -delta;
  }

  public boolean canScrollVertically() {
    return true;
  }

  public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
    if (getChildCount() == 0) {
      return 0;
    }
    View topView = getChildAt(0);
    View bottomView = getChildAt(getChildCount() - 1);
    if (getDecoratedBottom(bottomView) - getDecoratedTop(topView) < getVerticalSpace()) {
      return 0;
    }
    int delta;
    int maxRowCount = getTotalRowCount();
    boolean topBoundReached = getFirstVisibleRow() == 0;
    boolean bottomBoundReached = getLastVisibleRow() >= maxRowCount;
    if (dy > 0) {
      if (bottomBoundReached) {
        int bottomOffset;
        if (rowOfIndex(getChildCount() - 1) >= maxRowCount - 1) {
          bottomOffset = (getVerticalSpace() - getDecoratedBottom(bottomView)) + getPaddingBottom();
        } else {
          bottomOffset = (getVerticalSpace() - (getDecoratedBottom(bottomView) + this.mDecoratedChildHeight)) + getPaddingBottom();
        }
        delta = Math.max(-dy, bottomOffset);
      } else {
        delta = -dy;
      }
    } else if (topBoundReached) {
      delta = Math.min(-dy, (-getDecoratedTop(topView)) + getPaddingTop());
    } else {
      delta = -dy;
    }
    offsetChildrenVertical(delta);
    if (dy > 0) {
      if (getDecoratedBottom(topView) < 0 && !bottomBoundReached) {
        fillGrid(3, recycler, state);
      } else if (!bottomBoundReached) {
        fillGrid(-1, recycler, state);
      }
    } else if (getDecoratedTop(topView) > 0 && !topBoundReached) {
      fillGrid(2, recycler, state);
    } else if (!topBoundReached) {
      fillGrid(-1, recycler, state);
    }
    return -delta;
  }

  public View findViewByPosition(int position) {
    for (int i = 0; i < getChildCount(); i++) {
      if (positionOfIndex(i) == position) {
        return getChildAt(i);
      }
    }
    return null;
  }

  public android.support.v7.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(-2, -2);
  }

  public android.support.v7.widget.RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
    return new LayoutParams(c, attrs);
  }

  public android.support.v7.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams lp) {
    if (lp instanceof MarginLayoutParams) {
      return new LayoutParams((MarginLayoutParams) lp);
    }
    return new LayoutParams(lp);
  }

  public boolean checkLayoutParams(android.support.v7.widget.RecyclerView.LayoutParams lp) {
    return lp instanceof LayoutParams;
  }

  private void layoutAppearingViews(Recycler recycler, View referenceView, int referencePosition, int extraCount, int offset) {
    if (extraCount >= 1) {
      for (int extra = 1; extra <= extraCount; extra++) {
        int extraPosition = referencePosition + extra;
        if (extraPosition >= 0 && extraPosition < getItemCount()) {
          View appearing = recycler.getViewForPosition(extraPosition);
          addView(appearing);
          layoutTempChildView(appearing, getGlobalRowOfPosition(extraPosition + offset) - getGlobalRowOfPosition(referencePosition + offset), getGlobalColumnOfPosition(extraPosition + offset) - getGlobalColumnOfPosition(referencePosition + offset), referenceView);
        }
      }
    }
  }

  private void layoutDisappearingView(View disappearingChild) {
    addDisappearingView(disappearingChild);
    LayoutParams lp = (LayoutParams) disappearingChild.getLayoutParams();
    layoutTempChildView(disappearingChild, getGlobalRowOfPosition(lp.getViewAdapterPosition()) - lp.row, getGlobalColumnOfPosition(lp.getViewAdapterPosition()) - lp.column, disappearingChild);
  }

  private void layoutTempChildView(View child, int rowDelta, int colDelta, View referenceView) {
    int layoutTop = getDecoratedTop(referenceView) + (this.mDecoratedChildHeight * rowDelta);
    int layoutLeft = getDecoratedLeft(referenceView) + (this.mDecoratedChildWidth * colDelta);
    measureChildWithMargins(child, 0, 0);
    layoutDecorated(child, layoutLeft, layoutTop, layoutLeft + this.mDecoratedChildWidth, layoutTop + this.mDecoratedChildHeight);
  }

  private int getGlobalColumnOfPosition(int position) {
    return position % this.mTotalColumnCount;
  }

  private int getGlobalRowOfPosition(int position) {
    return position / this.mTotalColumnCount;
  }

  private int positionOfIndex(int childIndex) {
    return (this.mFirstVisiblePosition + (getTotalColumnCount() * (childIndex / this.mVisibleColumnCount))) + (childIndex % this.mVisibleColumnCount);
  }

  private int rowOfIndex(int childIndex) {
    return positionOfIndex(childIndex) / getTotalColumnCount();
  }

  private int getFirstVisibleColumn() {
    return this.mFirstVisiblePosition % getTotalColumnCount();
  }

  private int getLastVisibleColumn() {
    return getFirstVisibleColumn() + this.mVisibleColumnCount;
  }

  private int getFirstVisibleRow() {
    return this.mFirstVisiblePosition / getTotalColumnCount();
  }

  private int getLastVisibleRow() {
    return getFirstVisibleRow() + this.mVisibleRowCount;
  }

  private int getVisibleChildCount() {
    return this.mVisibleColumnCount * this.mVisibleRowCount;
  }

  private int getTotalColumnCount() {
    if (getItemCount() < this.mTotalColumnCount) {
      return getItemCount();
    }
    return this.mTotalColumnCount;
  }

  private int getTotalRowCount() {
    if (getItemCount() == 0 || this.mTotalColumnCount == 0) {
      return 0;
    }
    int maxRow = getItemCount() / this.mTotalColumnCount;
    if (getItemCount() % this.mTotalColumnCount != 0) {
      return maxRow + 1;
    }
    return maxRow;
  }

  private int getHorizontalSpace() {
    return (getWidth() - getPaddingRight()) - getPaddingLeft();
  }

  private int getVerticalSpace() {
    return (getHeight() - getPaddingBottom()) - getPaddingTop();
  }
}
