package br.liveo.sliding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import br.liveo.sliding.SlidingTabLayout.TabColorizer;
import com.itextpdf.text.pdf.BaseField;

class SlidingTabStrip extends LinearLayout {
  private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = (byte) 38;
  private static final int DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2;
  private static final byte DEFAULT_DIVIDER_COLOR_ALPHA = (byte) 32;
  private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;
  private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
  private static final int DEFAULT_SELECTED_INDICATOR_COLOR = -13388315;
  private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 8;
  private final Paint mBottomBorderPaint;
  private final int mBottomBorderThickness;
  private TabColorizer mCustomTabColorizer;
  private final int mDefaultBottomBorderColor;
  private final SimpleTabColorizer mDefaultTabColorizer;
  private final float mDividerHeight;
  private final Paint mDividerPaint;
  private final Paint mSelectedIndicatorPaint;
  private final int mSelectedIndicatorThickness;
  private int mSelectedPosition;
  private float mSelectionOffset;

  private static class SimpleTabColorizer implements TabColorizer {
    private int[] mDividerColors;
    private int[] mIndicatorColors;

    private SimpleTabColorizer() {
    }

    public final int getIndicatorColor(int position) {
      return this.mIndicatorColors[position % this.mIndicatorColors.length];
    }

    public final int getDividerColor(int position) {
      return this.mDividerColors[position % this.mDividerColors.length];
    }

    void setIndicatorColors(int... colors) {
      this.mIndicatorColors = colors;
    }

    void setDividerColors(int... colors) {
      this.mDividerColors = colors;
    }
  }

  SlidingTabStrip(Context context) {
    this(context, null);
  }

  SlidingTabStrip(Context context, AttributeSet attrs) {
    super(context, attrs);
    setWillNotDraw(false);
    float density = getResources().getDisplayMetrics().density;
    TypedValue outValue = new TypedValue();
    final int themeForegroundColor =  outValue.data;
    context.getTheme().resolveAttribute(16842800, outValue, true);
    this.mDefaultBottomBorderColor = setColorAlpha(outValue.data, DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);
    this.mDefaultTabColorizer = new SimpleTabColorizer();
    this.mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);
    this.mDefaultTabColorizer.setDividerColors(setColorAlpha(themeForegroundColor, (byte) 32));
    this.mBottomBorderThickness = (int) (BaseField.BORDER_WIDTH_MEDIUM * density);
    this.mBottomBorderPaint = new Paint();
    this.mBottomBorderPaint.setColor(this.mDefaultBottomBorderColor);
    this.mSelectedIndicatorThickness = (int) (8.0f * density);
    this.mSelectedIndicatorPaint = new Paint();
    this.mDividerHeight = DEFAULT_DIVIDER_HEIGHT;
    this.mDividerPaint = new Paint();
    this.mDividerPaint.setStrokeWidth((float) ((int) (BaseField.BORDER_WIDTH_THIN * density)));
  }

  void setCustomTabColorizer(TabColorizer customTabColorizer) {
    this.mCustomTabColorizer = customTabColorizer;
    invalidate();
  }

  void setSelectedIndicatorColors(int... colors) {
    this.mCustomTabColorizer = null;
    this.mDefaultTabColorizer.setIndicatorColors(colors);
    invalidate();
  }

  void setDividerColors(int... colors) {
    this.mCustomTabColorizer = null;
    this.mDefaultTabColorizer.setDividerColors(colors);
    invalidate();
  }

  void onViewPagerPageChanged(int position, float positionOffset) {
    this.mSelectedPosition = position;
    this.mSelectionOffset = positionOffset;
    invalidate();
  }

  protected void onDraw(Canvas canvas) {
    int height = getHeight();
    int childCount = getChildCount();
    TabColorizer tabColorizer = null;
    int dividerHeightPx = (int) (Math.min(Math.max(0.0f, this.mDividerHeight), BaseField.BORDER_WIDTH_THIN) * ((float) height));
    if (this.mCustomTabColorizer != null) {
      tabColorizer = this.mCustomTabColorizer;
    } else {
      tabColorizer = this.mDefaultTabColorizer;
    }
    if (childCount > 0) {
      View selectedTitle = getChildAt(this.mSelectedPosition);
      int left = selectedTitle.getLeft();
      int right = selectedTitle.getRight();
      int color = tabColorizer.getIndicatorColor(this.mSelectedPosition);
      if (this.mSelectionOffset > 0.0f && this.mSelectedPosition < getChildCount() - 1) {
        int nextColor = tabColorizer.getIndicatorColor(this.mSelectedPosition + 1);
        if (color != nextColor) {
          color = blendColors(nextColor, color, this.mSelectionOffset);
        }
        View nextTitle = getChildAt(this.mSelectedPosition + 1);
        left = (int) ((this.mSelectionOffset * ((float) nextTitle.getLeft())) + ((BaseField.BORDER_WIDTH_THIN - this.mSelectionOffset) * ((float) left)));
        right = (int) ((this.mSelectionOffset * ((float) nextTitle.getRight())) + ((BaseField.BORDER_WIDTH_THIN - this.mSelectionOffset) * ((float) right)));
      }
      this.mSelectedIndicatorPaint.setColor(color);
      canvas.drawRect((float) left, (float) (height - this.mSelectedIndicatorThickness), (float) right, (float) height, this.mSelectedIndicatorPaint);
    }
    canvas.drawRect(0.0f, (float) (height - this.mBottomBorderThickness), (float) getWidth(), (float) height, this.mBottomBorderPaint);
    int separatorTop = (height - dividerHeightPx) / 2;
    for (int i = 0; i < childCount - 1; i++) {
      View child = getChildAt(i);
      this.mDividerPaint.setColor(tabColorizer.getDividerColor(i));
      canvas.drawLine((float) child.getRight(), (float) separatorTop, (float) child.getRight(), (float) (separatorTop + dividerHeightPx), this.mDividerPaint);
    }
  }

  private static int setColorAlpha(int color, byte alpha) {
    return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
  }

  private static int blendColors(int color1, int color2, float ratio) {
    float inverseRation = BaseField.BORDER_WIDTH_THIN - ratio;
    return Color.rgb((int) ((((float) Color.red(color1)) * ratio) + (((float) Color.red(color2)) * inverseRation)), (int) ((((float) Color.green(color1)) * ratio) + (((float) Color.green(color2)) * inverseRation)), (int) ((((float) Color.blue(color1)) * ratio) + (((float) Color.blue(color2)) * inverseRation)));
  }
}
