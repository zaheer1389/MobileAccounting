package com.adslinfotech.mobileaccounting.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class PieChartActivity
  extends SimpleAccountingActivity
{
  private static int[] COLORS = { -16711936, -3355444, -16776961, -65281, -16711681, -65536, 65280, -16777216, -12303292, -7829368, 2131492936, 2131492873 };
  private GraphicalView mChartView;
  private GraphicalView mChartViewDr;
  private String mDateFormat;
  private DefaultRenderer mRenderer = new DefaultRenderer();
  private DefaultRenderer mRendererDr = new DefaultRenderer();
  private CategorySeries mSeries = new CategorySeries("Credit");
  private CategorySeries mSeriesDr = new CategorySeries("Debit");
  private ArrayList<Transaction> mTransactions;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.xy_chart);
    this.mRenderer.setApplyBackgroundColor(true);
    this.mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
    this.mRenderer.setChartTitleTextSize(20.0F);
    this.mRenderer.setLabelsTextSize(15.0F);
    this.mRenderer.setLegendTextSize(15.0F);
    this.mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
    this.mRenderer.setZoomButtonsVisible(true);
    //this.mRenderer.setStartAngle(90.0F);
    this.mRenderer.setChartTitle("Credit");
    this.mRendererDr.setChartTitle("Debit");
    this.mRendererDr.setApplyBackgroundColor(true);
    this.mRendererDr.setBackgroundColor(Color.argb(100, 50, 50, 50));
    this.mRendererDr.setChartTitleTextSize(20.0F);
    this.mRendererDr.setLabelsTextSize(15.0F);
    this.mRendererDr.setLegendTextSize(15.0F);
    this.mRendererDr.setMargins(new int[] { 20, 30, 15, 0 });
    this.mRendererDr.setZoomButtonsVisible(true);
    this.mTransactions = ((ArrayList)getIntent().getSerializableExtra("data"));
    //paramBundle = this.mTransactions.iterator();
    for (Transaction transaction : mTransactions)
    {
      //Object localObject = (Transaction)paramBundle.next();
      double d1 = transaction.getCraditAmount();
      double d2 = transaction.getDebitAmount();
      String remark = transaction.getRemark();
      if (d1 != 0.0D)
      {
        this.mSeries.add(transaction.toString(), d1);
        SimpleSeriesRenderer localSimpleSeriesRenderer = new SimpleSeriesRenderer();
        localSimpleSeriesRenderer.setColor(COLORS[((this.mSeries.getItemCount() - 1) % COLORS.length)]);
        this.mRenderer.addSeriesRenderer(localSimpleSeriesRenderer);
      }
      if (d2 != 0.0D)
      {
        this.mSeriesDr.add(transaction.toString(), d2);
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(COLORS[((this.mSeriesDr.getItemCount() - 1) % COLORS.length)]);
        this.mRendererDr.addSeriesRenderer(renderer);
      }
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  protected void onPause()
  {
    super.onPause();
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.mSeries = ((CategorySeries)paramBundle.getSerializable("current_series"));
    this.mSeriesDr = ((CategorySeries)paramBundle.getSerializable("current_series_dr"));
    this.mRenderer = ((DefaultRenderer)paramBundle.getSerializable("current_renderer"));
    this.mDateFormat = paramBundle.getString("date_format");
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.mChartView == null)
    {
      LinearLayout localLinearLayout1 = (LinearLayout)findViewById(R.id.cr_chart);
      LinearLayout localLinearLayout2 = (LinearLayout)findViewById(R.id.dr_chart);
      this.mChartView = ChartFactory.getPieChartView(this, this.mSeries, this.mRenderer);
      this.mChartViewDr = ChartFactory.getPieChartView(this, this.mSeriesDr, this.mRendererDr);
      this.mRenderer.setClickEnabled(true);
      this.mRenderer.setSelectableBuffer(10);
      this.mRendererDr.setClickEnabled(true);
      this.mRendererDr.setSelectableBuffer(10);
      this.mChartView.setOnClickListener(new OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SeriesSelection seriesSelection = PieChartActivity.this.mChartView.getCurrentSeriesAndPoint();
          if (paramAnonymousView != null) {
            Toast.makeText(PieChartActivity.this, "Credit of " + PieChartActivity.this.mSeries.getCategory(seriesSelection.getPointIndex()) + " is: " + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
          }
        }
      });
      this.mChartViewDr.setOnClickListener(new OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SeriesSelection seriesSelection = PieChartActivity.this.mChartViewDr.getCurrentSeriesAndPoint();
          if (paramAnonymousView != null) {
            Toast.makeText(PieChartActivity.this, "Debit of " + PieChartActivity.this.mSeriesDr.getCategory(seriesSelection.getPointIndex()) + " is: " + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
          }
        }
      });
      localLinearLayout1.addView(this.mChartView, new ViewGroup.LayoutParams(-1, -1));
      localLinearLayout2.addView(this.mChartViewDr, new ViewGroup.LayoutParams(-1, -1));
      return;
    }
    this.mChartView.repaint();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putSerializable("current_series", this.mSeries);
    paramBundle.putSerializable("current_series_dr", this.mSeriesDr);
    paramBundle.putSerializable("current_renderer", this.mRenderer);
    paramBundle.putString("date_format", this.mDateFormat);
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/chart/PieChartActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */