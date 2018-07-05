package com.adslinfotech.mobileaccounting.fragment.chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.activities.report.LastYearActivity;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment;
import com.github.mikephil.charting.components.Legend.LegendOrientation;
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.text.pdf.BaseField;
import java.util.ArrayList;
import java.util.Iterator;
import com.adslinfotech.mobileaccounting.R;

public class LineChartFragment extends Fragment implements OnChartValueSelectedListener {
    private static String KEY_EXTRA = "KEY_EXTRA";
    private LineChart mChart;
    private ArrayList<Transaction> mList;
    private double mMaxLimit;
    private double mMinLimit;
    protected Typeface mTfLight;
    protected Typeface mTfRegular;

    public static LineChartFragment newInstance(ArrayList<Transaction> list) {
        LineChartFragment fragment = new LineChartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_EXTRA)) {
            this.mList = (ArrayList) args.getSerializable(KEY_EXTRA);
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linechart_yearly, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        this.mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        this.mChart = (LineChart) view.findViewById(R.id.chart1);
        this.mChart.setOnChartValueSelectedListener(this);
        this.mChart.getDescription().setEnabled(false);
        this.mChart.setTouchEnabled(true);
        this.mChart.setDragDecelerationFrictionCoef(0.9f);
        this.mChart.setDragEnabled(true);
        this.mChart.setScaleEnabled(true);
        this.mChart.setDrawGridBackground(false);
        this.mChart.setHighlightPerDragEnabled(true);
        this.mChart.setPinchZoom(true);
        this.mChart.setBackgroundColor(getResources().getColor(Color.blue(12)));
        setData();
        this.mChart.animateX(2500);
        Legend l = this.mChart.getLegend();
        l.setForm(LegendForm.LINE);
        l.setTypeface(this.mTfLight);
        l.setTextSize(11.0f);
        l.setTextColor(-1);
        l.setVerticalAlignment(LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(LegendHorizontalAlignment.LEFT);
        l.setOrientation(LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        XAxis xAxis = this.mChart.getXAxis();
        xAxis.setTypeface(this.mTfLight);
        xAxis.setTextSize(11.0f);
        xAxis.setTextColor(-1);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        YAxis leftAxis = this.mChart.getAxisLeft();
        leftAxis.setTypeface(this.mTfLight);
        leftAxis.setTextColor(-16776961);
        leftAxis.setAxisMaximum((float) this.mMaxLimit);
        leftAxis.setAxisMinimum((float) this.mMinLimit);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        YAxis rightAxis = this.mChart.getAxisRight();
        rightAxis.setTypeface(this.mTfLight);
        rightAxis.setTextColor(SupportMenu.CATEGORY_MASK);
        rightAxis.setAxisMaximum((float) this.mMaxLimit);
        rightAxis.setAxisMinimum((float) this.mMinLimit);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    private void setData() {
        ArrayList<Entry> yValsCr = new ArrayList();
        ArrayList<Entry> yValsDr = new ArrayList();
        int i = 0;
        Iterator it = this.mList.iterator();
        while (it.hasNext()) {
            Transaction dao = (Transaction) it.next();
            double cr = dao.getCraditAmount();
            double dr = dao.getDebitAmount();
            if (this.mMinLimit > cr) {
                this.mMinLimit = cr;
            }
            if (this.mMinLimit > dr) {
                this.mMinLimit = dr;
            }
            if (this.mMaxLimit < cr) {
                this.mMaxLimit = cr;
            }
            if (this.mMaxLimit < dr) {
                this.mMaxLimit = dr;
            }
            yValsCr.add(new Entry((float) i, (float) cr, dao));
            yValsDr.add(new Entry((float) i, (float) dr, dao));
            i++;
        }
        if (this.mChart.getData() == null || ((LineData) this.mChart.getData()).getDataSetCount() <= 0) {
            LineDataSet setCr = new LineDataSet(yValsCr, "Credit");
            setCr.setAxisDependency(AxisDependency.LEFT);
            setCr.setColor(ColorTemplate.getHoloBlue());
            setCr.setCircleColor(-1);
            setCr.setLineWidth(BaseField.BORDER_WIDTH_MEDIUM);
            setCr.setCircleRadius(BaseField.BORDER_WIDTH_THICK);
            setCr.setFillAlpha(65);
            setCr.setFillColor(ColorTemplate.getHoloBlue());
            setCr.setHighLightColor(Color.rgb(244, 117, 117));
            setCr.setDrawCircleHole(false);
            LineDataSet setDr = new LineDataSet(yValsDr, "Debit");
            setDr.setAxisDependency(AxisDependency.RIGHT);
            setDr.setColor(SupportMenu.CATEGORY_MASK);
            setDr.setCircleColor(-1);
            setDr.setLineWidth(BaseField.BORDER_WIDTH_MEDIUM);
            setDr.setCircleRadius(BaseField.BORDER_WIDTH_THICK);
            setDr.setFillAlpha(65);
            setDr.setFillColor(SupportMenu.CATEGORY_MASK);
            setDr.setDrawCircleHole(false);
            setDr.setHighLightColor(Color.rgb(244, 117, 117));
            LineData data = new LineData(setCr, setDr);
            data.setValueTextColor(ViewCompat.MEASURED_STATE_MASK);
            data.setValueTextSize(9.0f);
            this.mChart.setData(data);

            setDr = (LineDataSet) ((LineData) this.mChart.getData()).getDataSetByIndex(1);
            ((LineDataSet) ((LineData) this.mChart.getData()).getDataSetByIndex(0)).setValues(yValsCr);
            setDr.setValues(yValsDr);
            ((LineData) this.mChart.getData()).notifyDataChanged();
            this.mChart.notifyDataSetChanged();

            return;
        }

    }

    public void onValueSelected(Entry e, Highlight h) {
        String amt;
        Log.i("Entry selected", e.toString());
        this.mChart.centerViewToAnimated(e.getX(), e.getY(), ((ILineDataSet) ((LineData) this.mChart.getData()).getDataSetByIndex(h.getDataSetIndex())).getAxisDependency(), 500);
        Log.e("VAL SELECTED", e.getData() + " d- ex " + e.getX() + "- ey " + e.getY() + "- hsi " + h.getStackIndex() + "- ed " + e.getData());
        Log.e("VAL SELECTED", "Value: " + e.getY() + ", index: " + h.getX() + ", DataSet index: " + h.getDataIndex());
        Transaction dao = (Transaction) e.getData();
        if (h.getDataSetIndex() == 0) {
            amt = ((LastYearActivity) getActivity()).getFormatedBal(dao.getCraditAmount()) + " Cr";
        } else {
            amt = ((LastYearActivity) getActivity()).getFormatedBal(dao.getDebitAmount()) + " Dr";
        }
        Toast.makeText(getActivity(), dao.getDate() + " : " + amt, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
