package com.adslinfotech.mobileaccounting.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class BarChartActivity
{
  public static Intent openBarChart(Context paramContext, ArrayList<Transaction> paramArrayList)
  {
    int j = paramArrayList.size();
    Object localObject1 = new ArrayList();
    Object localObject2 = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    //Iterator<Transaction> paramArrayList = paramArrayList.iterator();
    for (Transaction transaction : paramArrayList)
    {
      //Transaction localObject3 = (Transaction)paramArrayList.next();
      double d1 = ((Transaction)transaction).getCraditAmount();
      double d2 = ((Transaction)transaction).getDebitAmount();
      String remark = ((Transaction)transaction).getRemark();
      ((ArrayList)localObject2).add(Double.valueOf(d1));
      ((ArrayList)localObject1).add(Double.valueOf(d2));
      localArrayList.add(transaction);
    }
    Object localObject3 = new XYSeries("Debit");
    XYSeries localXYSeries = new XYSeries("Credit");
    int i = 0;
    while (i < j)
    {
      ((XYSeries)localObject3).add(i, ((Double)((ArrayList)localObject1).get(i)).doubleValue());
      localXYSeries.add(i, ((Double)((ArrayList)localObject2).get(i)).doubleValue());
      i += 1;
    }
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    dataset.addSeries((XYSeries)localObject3);
    dataset.addSeries(localXYSeries);
    localObject1 = new XYSeriesRenderer();
    ((XYSeriesRenderer)localObject1).setColor(Color.rgb(130, 130, 230));
    ((XYSeriesRenderer)localObject1).setFillPoints(true);
    ((XYSeriesRenderer)localObject1).setLineWidth(2.0F);
    ((XYSeriesRenderer)localObject1).setDisplayChartValues(true);
    localObject2 = new XYSeriesRenderer();
    ((XYSeriesRenderer)localObject2).setColor(Color.rgb(220, 80, 80));
    ((XYSeriesRenderer)localObject2).setFillPoints(true);
    ((XYSeriesRenderer)localObject2).setLineWidth(2.0F);
    ((XYSeriesRenderer)localObject2).setDisplayChartValues(true);
    localObject3 = new XYMultipleSeriesRenderer();
    ((XYMultipleSeriesRenderer)localObject3).setXLabels(0);
    ((XYMultipleSeriesRenderer)localObject3).setChartTitle("Simple Accounting Chart");
    ((XYMultipleSeriesRenderer)localObject3).setXTitle("Year " + (new Date().getYear() + 1900));
    ((XYMultipleSeriesRenderer)localObject3).setYTitle("Amount in Rupees");
    ((XYMultipleSeriesRenderer)localObject3).setZoomButtonsVisible(true);
    i = 0;
    while (i < j)
    {
      ((XYMultipleSeriesRenderer)localObject3).addXTextLabel(i, (String)localArrayList.get(i));
      i += 1;
    }
    ((XYMultipleSeriesRenderer)localObject3).addSeriesRenderer((SimpleSeriesRenderer)localObject1);
    ((XYMultipleSeriesRenderer)localObject3).addSeriesRenderer((SimpleSeriesRenderer)localObject2);
    return ChartFactory.getBarChartIntent(paramContext, dataset, (XYMultipleSeriesRenderer)localObject3, BarChart.Type.DEFAULT);
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/chart/BarChartActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */