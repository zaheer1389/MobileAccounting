package com.adslinfotech.mobileaccounting.fragment.chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.report.LastYearActivity;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.github.mikephil.charting.animation.Easing.EasingOption;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieDataSet.ValuePosition;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.text.pdf.BaseField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PieChartFragment extends Fragment {
    private static String KEY_EXTRA = "KEY_EXTRA";
    private PieChart mChartCr;
    private PieChart mChartDr;
    private ArrayList<Transaction> mList;

    public static PieChartFragment newInstance(ArrayList<Transaction> list) {
        PieChartFragment fragment = new PieChartFragment();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pie_chart_yearly, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mChartCr = (PieChart) view.findViewById(R.id.chart1);
        this.mChartDr = (PieChart) view.findViewById(R.id.chart2);
        initCreditChat();
        initDebitChat();
        setData();
    }

    private void initDebitChat() {
        this.mChartDr.setUsePercentValues(true);
        this.mChartDr.setContentDescription("Debit Chart");
        this.mChartDr.setExtraOffsets(5.0f, 10.0f, 5.0f, 5.0f);
        this.mChartDr.setDragDecelerationFrictionCoef(0.95f);
        this.mChartDr.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
        this.mChartDr.setCenterText(generateCenterSpannableText());
        this.mChartDr.setExtraOffsets(20.0f, 0.0f, 20.0f, 0.0f);
        this.mChartDr.setDrawHoleEnabled(true);
        this.mChartDr.setHoleColor(-1);
        this.mChartDr.setTransparentCircleColor(-1);
        this.mChartDr.setTransparentCircleAlpha(110);
        this.mChartDr.setHoleRadius(58.0f);
        this.mChartDr.setTransparentCircleRadius(61.0f);
        this.mChartDr.setDrawCenterText(true);
        this.mChartDr.setRotationAngle(0.0f);
        this.mChartDr.setRotationEnabled(true);
        this.mChartDr.setHighlightPerTapEnabled(true);
        this.mChartDr.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            public void onValueSelected(Entry e, Highlight h) {
                if (e != null) {
                    Transaction dao = (Transaction) e.getData();
                    Toast.makeText(PieChartFragment.this.getActivity(), dao.getDate() + " : " + (((LastYearActivity) PieChartFragment.this.getActivity()).getFormatedBal(dao.getDebitAmount()) + " Dr"), Toast.LENGTH_LONG).show();
                }
            }

            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");
            }
        });
        this.mChartDr.animateY(1400, EasingOption.EaseInOutQuad);
        Legend l = this.mChartDr.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
    }

    private void initCreditChat() {
        this.mChartCr.setUsePercentValues(true);
        this.mChartCr.setContentDescription("Credit Chart");
        this.mChartCr.setExtraOffsets(5.0f, 10.0f, 5.0f, 5.0f);
        this.mChartCr.setDragDecelerationFrictionCoef(0.95f);
        this.mChartCr.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
        this.mChartCr.setCenterText(generateCenterSpannableText());
        this.mChartCr.setExtraOffsets(20.0f, 0.0f, 20.0f, 0.0f);
        this.mChartCr.setDrawHoleEnabled(true);
        this.mChartCr.setHoleColor(-1);
        this.mChartCr.setTransparentCircleColor(-1);
        this.mChartCr.setTransparentCircleAlpha(110);
        this.mChartCr.setHoleRadius(58.0f);
        this.mChartCr.setTransparentCircleRadius(61.0f);
        this.mChartCr.setDrawCenterText(true);
        this.mChartCr.setRotationAngle(0.0f);
        this.mChartCr.setRotationEnabled(true);
        this.mChartCr.setHighlightPerTapEnabled(true);
        this.mChartCr.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            public void onValueSelected(Entry e, Highlight h) {
                Transaction dao = (Transaction) e.getData();
                Toast.makeText(PieChartFragment.this.getActivity(), dao.getDate() + " : " + (((LastYearActivity) PieChartFragment.this.getActivity()).getFormatedBal(dao.getCraditAmount()) + " Cr"), Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");
            }
        });
        this.mChartCr.animateY(1400, EasingOption.EaseInOutQuad);
        Legend l = this.mChartCr.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
    }

    private void setData() {
        List<PieEntry> crEntries = new ArrayList();
        List<PieEntry> drEntries = new ArrayList();
        Iterator it = this.mList.iterator();
        while (it.hasNext()) {
            Transaction dao = (Transaction) it.next();
            if (dao.getCraditAmount() != 0.0d) {
                crEntries.add(new PieEntry((float) dao.getCraditAmount(), dao.getDate(), dao));
            }
            if (dao.getDebitAmount() != 0.0d) {
                drEntries.add(new PieEntry((float) dao.getDebitAmount(), dao.getDate(), dao));
            }
        }
        PieDataSet dataSetCr = new PieDataSet(crEntries, "Credit Report");
        dataSetCr.setSliceSpace(BaseField.BORDER_WIDTH_THICK);
        dataSetCr.setSelectionShift(5.0f);
        PieDataSet dataSetDr = new PieDataSet(drEntries, "Debit Report");
        dataSetDr.setSliceSpace(BaseField.BORDER_WIDTH_THICK);
        dataSetDr.setSelectionShift(5.0f);
        dataSetCr.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSetDr.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSetCr.setValueLinePart1OffsetPercentage(80.0f);
        dataSetCr.setValueLinePart1Length(0.2f);
        dataSetCr.setValueLinePart2Length(0.4f);
        dataSetCr.setYValuePosition(ValuePosition.OUTSIDE_SLICE);
        dataSetDr.setValueLinePart1OffsetPercentage(80.0f);
        dataSetDr.setValueLinePart1Length(0.2f);
        dataSetDr.setValueLinePart2Length(0.4f);
        dataSetDr.setYValuePosition(ValuePosition.OUTSIDE_SLICE);
        PieData dataCr = new PieData(dataSetCr);
        dataCr.setValueFormatter(new PercentFormatter());
        dataCr.setValueTextSize(11.0f);
        dataCr.setValueTextColor(ViewCompat.MEASURED_STATE_MASK);
        dataCr.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
        this.mChartCr.setData(dataCr);
        this.mChartCr.highlightValues(null);
        this.mChartCr.invalidate();
        PieData dataDr = new PieData(dataSetDr);
        dataDr.setValueFormatter(new PercentFormatter());
        dataDr.setValueTextSize(11.0f);
        dataDr.setValueTextColor(ViewCompat.MEASURED_STATE_MASK);
        dataDr.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
        this.mChartDr.setData(dataDr);
        this.mChartDr.highlightValues(null);
        this.mChartDr.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name) + "\ndeveloped by ADSL Infosoft");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(0), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(0.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(2), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
}
