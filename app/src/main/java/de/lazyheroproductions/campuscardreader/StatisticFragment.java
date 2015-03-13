package de.lazyheroproductions.campuscardreader;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

public class StatisticFragment extends Fragment {

    private View rootView;
    private LineChartView transactionLineChart;
    private LineChartView creditLineChart;
    private BarChartView averageBarChart;
    private CreditData cData; //TODO maybe initialize a dummy?
    private CreditDatabase cDb;
    private boolean isAttached = false;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_statistic, container, false);
        // bad practice - find a better way but for now let it be
        cDb = new CreditDatabase(getActivity().getApplicationContext());
        cData = cDb.getData();
        // bad practice end :)
        transactionLineChart = (LineChartView) rootView.findViewById(R.id.transaction_linechart);
        creditLineChart =  (LineChartView) rootView.findViewById(R.id.credit_linechart);
        averageBarChart = (BarChartView) rootView.findViewById(R.id.average_barchart);
        setUpTransactionLineChart();
        setUpCreditLineChart();
        setUpAverageBarChart();
        setUpAllTimeSpendings();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        isAttached = true;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        isAttached = false;
    }

    public boolean isAttached(){
        return isAttached;
    }

    public void updateData(CreditData cData){
        this.cData = cData;
        // update all the charts!
        setUpAllTimeSpendings();
        averageBarChart.reset();
        setUpAverageBarChart();
        creditLineChart.reset();
        setUpCreditLineChart();
        transactionLineChart.reset();
        setUpTransactionLineChart();
    }

    private void setUpAllTimeSpendings(){
        TextView spendings = (TextView) rootView.findViewById(R.id.all_time_spendings_textview);
        float f = cData.getSumTransactions();
        String s = DataAnalysisTools.format(f);
        spendings.setText(s);
    }

    private void setUpTransactionLineChart(){
        String lineLabels[] = cData.getDatesHumanReadable();
        float lineValues[] = cData.getTransactions();
        int minMaxStep[] = DataAnalysisTools.calculateAxisBorderValuesLineCharts(lineValues);
        LineSet dataSet = new LineSet();
        if (lineLabels.length != 0 || lineValues.length != 0) {
            dataSet.addPoints(lineLabels, lineValues);
        } else {
            dataSet.addPoint(getResources().getString(R.string.no_data_to_show), 0f);
        }
        dataSet.setDots(true)
                .setSmooth(true)
                .setDotsColor(this.getResources().getColor(R.color.purple_primary)) //line_bg
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.line)) //line
                .setLineColor(this.getResources().getColor(R.color.line)) //line
                .setLineThickness(Tools.fromDpToPx(3));
        transactionLineChart.addData(dataSet);

        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.purple_primary_dark)); //line_grid
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        transactionLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(ChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(minMaxStep[0],minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setLabelsMetric("€")
                .show();
    }

    private void setUpCreditLineChart(){
        String lineLabels[] = cData.getDatesHumanReadable();
        float lineValues[] = cData.getCredits();
        int minMaxStep[] = DataAnalysisTools.calculateAxisBorderValuesLineCharts(lineValues);
        LineSet dataSet = new LineSet();
        if( lineLabels.length != 0 || lineValues.length != 0) {
            dataSet.addPoints(lineLabels, lineValues);
        }else{
            dataSet.addPoint(getResources().getString(R.string.no_data_to_show),0f);
        }
        dataSet.setDots(true)
                .setSmooth(true)
                .setDotsColor(this.getResources().getColor(R.color.indigo_primary)) //line_bg
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.line)) //line
                .setLineColor(this.getResources().getColor(R.color.line)) //line
                .setLineThickness(Tools.fromDpToPx(3));
        creditLineChart.addData(dataSet);

        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.indigo_primary_dark)); //line_grid
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        creditLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(ChartView.GridType.HORIZONTAL,mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(minMaxStep[0],minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setLabelsMetric("€")
                .show();
    }

    private void setUpAverageBarChart(){
        Paint mBarGridPaint = new Paint();
        mBarGridPaint.setColor(this.getResources().getColor(R.color.deep_orange_primary_dark));//bar grid
        mBarGridPaint.setStyle(Paint.Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        BarSet barSet = new BarSet();
        Bar bar;
        bar = new Bar(this.getResources().getString(R.string.average_transactions), cData.getTransactionAverage());
        bar.setColor(this.getResources().getColor(R.color.deep_orange_300));
        barSet.addBar(bar);

        bar = new Bar(this.getResources().getString(R.string.average_credit), cData.getCreditAverage());
        bar.setColor(this.getResources().getColor(R.color.deep_orange_300));
        barSet.addBar(bar);

        averageBarChart.addData(barSet);

        averageBarChart.setSetSpacing(Tools.fromDpToPx(40));//20?
        averageBarChart.setBarSpacing(Tools.fromDpToPx(40));

        int[] minMaxStep = DataAnalysisTools.calculateAxisBorderValuesBarCharts(cData.getCreditAverage(),cData.getTransactionAverage());
        averageBarChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setAxisBorderValues(minMaxStep[0], minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setGrid(ChartView.GridType.FULL, mBarGridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setLabelsMetric("€")
                .setBorderSpacing(Tools.fromDpToPx(40))
                .show();
    }
}