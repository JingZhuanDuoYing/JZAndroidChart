package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;

import android.util.Log;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData extends ChartData<AbstractDataSet> {

    private BarData barData;
    private LineData lineData;
    private CandlestickData candlestickData;
    private ChartData<ScatterDataSet> scatterChartData;

    public CombineData() {
        barData = new BarData();
        lineData = new LineData();
        candlestickData = new CandlestickData();
        scatterChartData = new ScatterData();
    }

    public List<BarDataSet> getBarData() {
        return barData.getDataSets();
    }

    public List<LineDataSet> getLineData() {
        return lineData.getDataSets();
    }

    public List<CandlestickDataSet> getCandlestickData() {
        return candlestickData.getDataSets();
    }


    public BarData getBarChartData() {
        return barData;
    }

    public LineData getLineChartData() {
        return lineData;
    }

    public CandlestickData getCandlestickChartData() {
        return candlestickData;
    }

    public ChartData<ScatterDataSet> getScatterChartData() {
        return scatterChartData;
    }

    public boolean addDataSet(BarDataSet dataSet) {
        return barData.add(dataSet);
    }

    public boolean addDataSet(LineDataSet dataSet) {
        return lineData.add(dataSet);
    }

    public boolean addDataSet(CandlestickDataSet dataSet) {
        return candlestickData.add(dataSet);
    }

    public boolean addDataSet(ScatterDataSet dataSet) {
        return scatterChartData.add(dataSet);
    }

    @Override public void calcMaxMin(Viewport viewport, Rect content) {
        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        rightMin = Float.MAX_VALUE;
        rightMax = -Float.MAX_VALUE;

        candlestickData.calcMaxMin(viewport, content);
        leftMin = Math.min(candlestickData.leftMin, leftMin);
        leftMax = Math.max(candlestickData.leftMax, leftMax);
        rightMin = Math.min(candlestickData.rightMin, rightMin);
        rightMax = Math.max(candlestickData.rightMax, rightMax);

        lineData.calcMaxMin(viewport, content);
        leftMin = Math.min(lineData.leftMin, leftMin);
        leftMax = Math.max(lineData.leftMax, leftMax);
        rightMin = Math.min(lineData.rightMin, rightMin);
        rightMax = Math.max(lineData.rightMax, rightMax);

        barData.calcMaxMin(viewport, content);
        leftMin = Math.min(barData.leftMin, leftMin);
        leftMax = Math.max(barData.leftMax, leftMax);
        rightMin = Math.min(barData.rightMin, rightMin);
        rightMax = Math.max(barData.rightMax, rightMax);

        scatterChartData.calcMaxMin(viewport, content);
        leftMin = Math.min(scatterChartData.leftMin, leftMin);
        leftMax = Math.max(scatterChartData.leftMax, leftMax);
        rightMin = Math.min(scatterChartData.rightMin, rightMin);
        rightMax = Math.max(scatterChartData.rightMax, rightMax);

        barData.setLeftMax(leftMax);
        lineData.setLeftMax(leftMax);
        candlestickData.setLeftMax(leftMax);
        scatterChartData.setLeftMax(leftMax);

        barData.setLeftMin(leftMin);
        lineData.setLeftMin(leftMin);
        candlestickData.setLeftMin(leftMin);
        scatterChartData.setLeftMin(leftMin);

        barData.setRightMax(rightMax);
        lineData.setRightMax(rightMax);
        candlestickData.setRightMax(rightMax);
        scatterChartData.setRightMax(rightMax);

        barData.setRightMin(rightMin);
        lineData.setRightMin(rightMin);
        candlestickData.setRightMin(rightMin);
        scatterChartData.setRightMin(rightMin);

        setMinMax();
    }

    @Override
    public boolean add(AbstractDataSet e) {
        if (e instanceof CandlestickDataSet) {
            return addDataSet((CandlestickDataSet) e);
        }
        if (e instanceof LineDataSet) {
            return addDataSet((LineDataSet) e);
        }
        if (e instanceof BarDataSet) {
            return addDataSet((BarDataSet) e);
        }
        if (e instanceof ScatterDataSet) {
            return addDataSet((ScatterDataSet) e);
        }
        return super.add(e);
    }

    @Override
    public void setMinMax() {
        if (leftAxis != null) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }

}
