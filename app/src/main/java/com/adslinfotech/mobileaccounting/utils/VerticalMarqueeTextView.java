package com.adslinfotech.mobileaccounting.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class VerticalMarqueeTextView extends AppCompatTextView {
  private final Activity activity;
  private long duration;
  private boolean isNotDrawn = true;
  private boolean isPaused;
  private boolean isUserScrolling;
  private int pixelYOffSet;
  private boolean stop;

  private class AutoScrollTextView extends AsyncTask<Void, Void, Void> {
    private int pixelCount;

    private AutoScrollTextView() {
    }

    protected Void doInBackground(Void... params) {
      while (textViewNotDrawn()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      while (!VerticalMarqueeTextView.this.stop) {
        if (!(VerticalMarqueeTextView.this.isPressed() || !VerticalMarqueeTextView.this.isUserScrolling || VerticalMarqueeTextView.this.isPaused)) {
          VerticalMarqueeTextView.this.isUserScrolling = false;
        }
        while (!VerticalMarqueeTextView.this.isUserScrolling && !VerticalMarqueeTextView.this.stop && !VerticalMarqueeTextView.this.isPaused) {
          try {
            Thread.sleep(VerticalMarqueeTextView.this.duration);
          } catch (InterruptedException e2) {
            e2.printStackTrace();
          }
          VerticalMarqueeTextView.this.activity.runOnUiThread(new Runnable() {
            public void run() {
              if (VerticalMarqueeTextView.this.isPressed()) {
                VerticalMarqueeTextView.this.isUserScrolling = true;
                return;
              }
              if (VerticalMarqueeTextView.this.getScrollY() >= AutoScrollTextView.this.pixelCount) {
                VerticalMarqueeTextView.this.scrollTo(0, 0);
              } else {
                VerticalMarqueeTextView.this.scrollBy(0, VerticalMarqueeTextView.this.pixelYOffSet);
              }
              VerticalMarqueeTextView.this.invalidate();
            }
          });
        }
      }
      return null;
    }

    protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
    }

    private boolean textViewNotDrawn() {
      VerticalMarqueeTextView.this.activity.runOnUiThread(new Runnable() {
        public void run() {
          if (VerticalMarqueeTextView.this.getLineCount() > 0) {
            AutoScrollTextView.this.pixelCount = VerticalMarqueeTextView.this.getLineHeight() * VerticalMarqueeTextView.this.getLineCount();
            VerticalMarqueeTextView.this.isNotDrawn = false;
          }
        }
      });
      return VerticalMarqueeTextView.this.isNotDrawn;
    }
  }

  public VerticalMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.activity = (Activity) context;
    init();
  }

  public VerticalMarqueeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.activity = (Activity) context;
    init();
  }

  public VerticalMarqueeTextView(Context context) {
    super(context);
    this.activity = (Activity) context;
    init();
  }

  private void init() {
    setDuration(65);
    setPixelYOffSet(1);
    this.stop = false;
    this.isPaused = false;
    this.isUserScrolling = false;
    startMarquee();
  }

  public long getDuration() {
    return this.duration;
  }

  public void setDuration(long duration) {
    if (duration <= 0) {
      this.duration = 65;
    } else {
      this.duration = duration;
    }
  }

  public int getPixelYOffSet() {
    return this.pixelYOffSet;
  }

  public void setPixelYOffSet(int pixelYOffSet) {
    if (pixelYOffSet < 1) {
      this.pixelYOffSet = 1;
    } else {
      this.pixelYOffSet = pixelYOffSet;
    }
  }

  private void startMarquee() {
    new AutoScrollTextView().execute(new Void[0]);
  }

  public void stopMarquee() {
    this.stop = true;
  }

  public void pauseMarquee() {
    this.isPaused = true;
  }

  public void resumeMarquee() {
    this.isPaused = false;
  }

  public boolean isPaused() {
    if (this.isPaused || this.isUserScrolling) {
      return true;
    }
    return false;
  }
}
