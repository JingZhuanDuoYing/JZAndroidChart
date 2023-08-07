package cn.jingzhuan.lib.chart2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.List;

import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart2.adapter.IAdapter;
import cn.jingzhuan.lib.chart2.adapter.JZCombineChartAdapter;
import cn.jingzhuan.lib.chart2.base.JZBaseChart;
import cn.jingzhuan.lib.chart2.renderer.CombineChartRenderer;
import cn.jingzhuan.lib.chart2.renderer.JZCombineChartRenderer;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZCombineChart extends JZBaseChart {

    public JZCombineChart(Context context) {
        super(context);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();
        if (getAdapter() == null) {
            setAdapter(new JZCombineChartAdapter());
        }
        mRenderer = new JZCombineChartRenderer(this);
    }

    public void addDataSet(AbstractDataSet abstractDataSet) {
        getRenderer().addDataSet(abstractDataSet);
    }

    public void setDataSet(AbstractDataSet dataSet) {
        cleanAllDataSet();
        addDataSet(dataSet);
    }

    public void setData(final CombineData combineData) {
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

    public void cleanAllDataSet() {
        getRenderer().clearDataSet();
    }

    public JZCombineChartRenderer getRenderer() {
        return (JZCombineChartRenderer) mRenderer;
    }

    /**
     * 把触摸事件传到RangeRenderer 用于处理拖动区间统计范围
     */
    public boolean onTouchEvent(MotionEvent event) {
        JZCombineChartRenderer renderer = (JZCombineChartRenderer) this.mRenderer;
        if (getRangeEnable() && renderer.rangeRenderer != null) {
            if (renderer.rangeRenderer.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }


}
