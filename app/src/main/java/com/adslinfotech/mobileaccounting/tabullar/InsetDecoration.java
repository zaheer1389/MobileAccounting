package com.adslinfotech.mobileaccounting.tabullar;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import com.adslinfotech.mobileaccounting.R;

public class InsetDecoration extends ItemDecoration {
  private int mInsets;

  public InsetDecoration(Context context) {
    this.mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
  }

  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
    outRect.set(this.mInsets, this.mInsets, this.mInsets, this.mInsets);
  }
}
