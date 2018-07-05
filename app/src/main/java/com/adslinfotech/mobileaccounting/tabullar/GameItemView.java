package com.adslinfotech.mobileaccounting.tabullar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class GameItemView extends GridLayout {
  public GameItemView(Context context) {
    super(context);
  }

  public GameItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GameItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  protected void onFinishInflate() {
    super.onFinishInflate();
  }

  public String toString() {
    return ": " + getLeft() + "," + getTop() + ": " + getMeasuredWidth() + "x" + getMeasuredHeight();
  }
}
