package cn.jingzhuan.lib.chart.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import java.util.List;

import cn.jingzhuan.lib.chart.BaseChart;
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

    public void addDataSet(BarDataSet barDataSet) {
        barDataSet.setChart(this);
        getRenderer().addDataSet(barDataSet);
    }

    public void addDataSet(LineDataSet lineDataSet) {
        lineDataSet.setChart(this);
        getRenderer().addDataSet(lineDataSet);
    }

    public void addDataSet(CandlestickDataSet candlestickDataSet) {
        candlestickDataSet.setChart(this);
        getRenderer().addDataSet(candlestickDataSet);
    }

    public void setDataSet(BarDataSet barDataSet) {
        cleanAllDataSet();

        addDataSet(barDataSet);
    }

    public void setDataSet(LineDataSet lineDataSet) {
        cleanAllDataSet();

        addDataSet(lineDataSet);
    }

    public void setDataSet(CandlestickDataSet candlestickDataSet) {
        cleanAllDataSet();

        addDataSet(candlestickDataSet);
    }

    public void setLineData(List<LineDataSet> data) {
        cleanAllDataSet();

        for (LineDataSet datum : data) {
            addDataSet(datum);
        }
    }

    public void setCombineData(CombineData combineData) {
        cleanAllDataSet();

        List<LineDataSet> lineDataSets = combineData.getLineData();
        if (lineDataSets != null) {
            for (LineDataSet lineDataSet : lineDataSets) {
                addDataSet(lineDataSet);
            }
        }

        List<BarDataSet> barDataSets = combineData.getBarData();
        if (barDataSets != null) {
            for (BarDataSet barDataSet : barDataSets) {
                addDataSet(barDataSet);
            }
        }

        List<CandlestickDataSet> candlestickDataSets = combineData.getCandlestickData();
        if (candlestickDataSets != null) {
            for (CandlestickDataSet candlestickDataSet : candlestickDataSets) {
                addDataSet(candlestickDataSet);
            }
        }
    }

    public List<LineDataSet> getLineDataSet() {
        return getRenderer().getDataSet();
    }

    public List<BarDataSet> getBarDataSet() {
        return getRenderer().getBarDataSet();
    }
    public List<CandlestickDataSet> getCandlestickDataSet() {
        return getRenderer().getCandlestickDataSet();
    }

    public CombineChartRenderer getRenderer() {
        return (CombineChartRenderer) mRenderer;
    }

    public void cleanLineDataSet() {
        getLineDataSet().clear();
    }

    public void cleanBarDataSet() {
        getBarDataSet().clear();
    }

    public void cleanCandlestickDataSet() {
        getCandlestickDataSet().clear();
    }

    public void cleanAllDataSet() {
        cleanLineDataSet();
        cleanBarDataSet();
        cleanCandlestickDataSet();
    }
}
