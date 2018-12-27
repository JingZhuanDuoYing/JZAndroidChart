package cn.jingzhuan.lib.chart.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import java.util.List;

import cn.jingzhuan.lib.chart.base.BaseChart;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.renderer.CombineChartRenderer;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChart extends BaseChart {

    public CombineChart(Context context) {
        super(context);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();

        mRenderer = new CombineChartRenderer(this);
    }

    public void addDataSet(AbstractDataSet abstractDataSet) {
        getRenderer().addDataSet(abstractDataSet);
    }

    public void setDataSet(AbstractDataSet dataSet) {
        cleanAllDataSet();

        addDataSet(dataSet);
    }

    public void setCombineData(final CombineData combineData) {
        cleanAllDataSet();

        for (LineDataSet lineDataSet : combineData.getLineData()) {
            addDataSet(lineDataSet);
        }
        for (BarDataSet barDataSet : combineData.getBarData()) {
            addDataSet(barDataSet);
        }
        for (CandlestickDataSet candlestickDataSet : combineData.getCandlestickData()) {
            addDataSet(candlestickDataSet);
        }
        for (ScatterDataSet scatterDataSet : combineData.getScatterData()) {
            addDataSet(scatterDataSet);
        }
    }

    public <T extends AbstractDataSet> void setData(List<T> data) {
        cleanAllDataSet();

        for (T datum : data) {
            addDataSet(datum);
        }
    }

    public List<LineDataSet> getLineDataSet() {
        return getRenderer().getChartData().getLineData();
    }

    public List<BarDataSet> getBarDataSet() {
        return getRenderer().getChartData().getBarData();
    }

    public List<CandlestickDataSet> getCandlestickDataSet() {
        return getRenderer().getChartData().getCandlestickData();
    }

    public List<ScatterDataSet> getScatterDataSet() {
        return getRenderer().getChartData().getScatterData();
    }

    public CombineChartRenderer getRenderer() {
        return (CombineChartRenderer) mRenderer;
    }

    @Override public void setTypeface(Typeface tf) {
        mRenderer.setTypeface(tf);
        super.setTypeface(tf);
    }

    public void cleanLineDataSet() {
        getRenderer().cleanLineDataSet();
    }

    public void cleanBarDataSet() {
        getRenderer().cleanBarDataSet();
    }

    public void cleanCandlestickDataSet() {
        getRenderer().cleanCandlestickDataSet();
    }
    public void cleanScatterDataSet() {
        getRenderer().cleanScatterDataSet();
    }

    public void cleanAllDataSet() {
        getRenderer().clearDataSet();
    }

}
