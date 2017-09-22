package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData extends ChartData<AbstractDataSet> {

    private BarData barData;
    private LineData lineData;
    private CandlestickData candlestickData;

    public CombineData() {
        barData = new BarData();
        lineData = new LineData();
        candlestickData = new CandlestickData();
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

    public void addDataSet(BarDataSet dataSet) {
        barData.add(dataSet);
//        barData.calcMinMax();
    }

    public void addDataSet(LineDataSet dataSet) {
        lineData.add(dataSet);
//        lineData.calcMinMax();
    }

    public void addDataSet(CandlestickDataSet dataSet) {
        candlestickData.add(dataSet);
//        candlestickData.calcMinMax();
    }

    public void calcDataSetMinMax(Viewport viewport, Rect content) {
        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        calcCandlestickDataSetMinMax(viewport, content);
        leftMin = candlestickData.leftMin;
        leftMax = candlestickData.leftMax;
        calcLineDataSetMinMax(viewport, content);
        leftMin = Math.min(lineData.leftMin, candlestickData.leftMin);
        leftMax = Math.max(lineData.leftMax, candlestickData.leftMax);
    }

    public void calcCandlestickDataSetMinMax(Viewport viewport, Rect content) {

        Log.d("calcCandlestick", "getCandlestickData() = " + getCandlestickData().size());
        candlestickData.leftMax = -Float.MAX_VALUE;
        candlestickData.leftMin = Float.MAX_VALUE;
        if (getCandlestickData().isEmpty()) return;
        for (CandlestickDataSet candlestickDataSet : getCandlestickData()) {
            for (CandlestickValue e : getVisiblePoints(candlestickDataSet, viewport)) {
                calcViewportMinMax(e);
            }
        }
        Log.d("calcCandlestick", candlestickData.leftMax + ", " + candlestickData.leftMin);
    }

    @Override
    public boolean add(AbstractDataSet e) {
        if (e instanceof CandlestickDataSet) {
            return getCandlestickData().add((CandlestickDataSet) e);
        }
        return super.add(e);
    }

    private void calcViewportMinMax(CandlestickValue e) {
        if (e.getLow() < candlestickData.leftMin)
            candlestickData.leftMin = e.getLow();

        if (e.getHigh() > candlestickData.leftMax)
            candlestickData.leftMax = e.getHigh();
    }

    public void calcLineDataSetMinMax(Viewport viewport, Rect content) {
        lineData.leftMax = -Float.MAX_VALUE;
        lineData.leftMin = Float.MAX_VALUE;
        for (LineDataSet lineDataSet : getLineData()) {
            lineDataSet.onViewportChange(viewport);
            lineData.leftMax = Math.max(lineData.leftMax, lineDataSet.getViewportYMax());
            lineData.leftMin = Math.min(lineData.leftMin, lineDataSet.getViewportYMin());
        }
    }

    public void calcBarDataSetMinMax(Viewport viewport, Rect content) {
        for (BarDataSet candlestickDataSet : getBarData()) {
//            candlestickDataSet.o;
        }
    }

    @Override
    public void calcMinMax() {
        if (leftAxis != null) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }

    protected List<CandlestickValue> getVisiblePoints(CandlestickDataSet candlestickDataSet, Viewport viewport) {
        int from = (int) (viewport.left * candlestickDataSet.getValues().size());
        int to   = (int) (viewport.right * candlestickDataSet.getValues().size());

        return candlestickDataSet.getValues().subList(from, to);
    }

}
