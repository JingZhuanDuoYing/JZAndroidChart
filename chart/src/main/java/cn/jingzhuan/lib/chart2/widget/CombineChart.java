package cn.jingzhuan.lib.chart2.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.base.JZChart;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart2.renderer.CombineChartRenderer;

import java.util.List;

/**
 * Created by Donglua on 17/8/2.
 * 综合Chart
 */

public class CombineChart extends JZChart {

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

    public void addDataSet(AbstractDataSet<?> abstractDataSet) {
        getRenderer().addDataSet(abstractDataSet);
    }

    public void setDataSet(AbstractDataSet<?> dataSet) {
        cleanAllDataSet();

        addDataSet(dataSet);
    }

    public <T extends AbstractDataSet<?>> void addAll(List<T> dataSets) {
        for (AbstractDataSet<?> dataSet : dataSets) {
            addDataSet(dataSet);
        }
    }

    public void setCombineData(final CombineData combineData) {
        cleanAllDataSet();
        for (TreeDataSet treeDataSet : combineData.getTreeData()) {
            addDataSet(treeDataSet);
        }
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
        for (PointLineDataSet pointLineDataSet : combineData.getPointLineData()) {
            addDataSet(pointLineDataSet);
        }
        for (ScatterTextDataSet pointLineDataSet : combineData.getScatterTextData()) {
            addDataSet(pointLineDataSet);
        }
    }

    public <T extends AbstractDataSet<?>> void setData(List<T> data) {
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

    public List<PointLineDataSet> getPointLineDataSet() {
        return getRenderer().getChartData().getPointLineData();
    }

    public List<ScatterDataSet> getScatterDataSet() {
        return getRenderer().getChartData().getScatterData();
    }

    public CombineChartRenderer getRenderer() {
        return (CombineChartRenderer) mRenderer;
    }

    @Override
    public void setTypeface(Typeface tf) {
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

    public void cleanRangeData() {
        getRenderer().rangeRenderer.resetData();
    }

    public void addLine(LineDataSet lineDataSet) {
        mRenderer.addDataSet(lineDataSet);
    }

    public void setLine(LineDataSet lineDataSet) {
        mRenderer.clearDataSet();
        addLine(lineDataSet);
    }

    /**
     * 把触摸事件传到RangeRenderer 用于处理拖动区间统计范围
     */
    public boolean onTouchEvent(MotionEvent event) {
        CombineChartRenderer renderer = (CombineChartRenderer) this.mRenderer;
        if (getRangeEnable() && renderer.rangeRenderer != null) {
            if (renderer.rangeRenderer.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }


}
