package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData extends ChartData<AbstractDataSet> {

    private BarData barData;
    private LineData lineData;
    private CandlestickData candlestickData;
    private ScatterData scatterData;

    public CombineData() {
        barData = new BarData();
        lineData = new LineData();
        candlestickData = new CandlestickData();
        scatterData = new ScatterData();
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

    public List<ScatterDataSet> getScatterData() {
        return scatterData.getDataSets();
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

    public ScatterData getScatterChartData() {
        return scatterData;
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
        return scatterData.add(dataSet);
    }

    public void setCombineData(CombineData combineData) {
        this.leftMin = combineData.leftMin;
        this.rightMin = combineData.rightMin;
        this.leftMax = combineData.leftMax;
        this.rightMax = combineData.rightMax;

        barData.setLeftMax(leftMax);
        lineData.setLeftMax(leftMax);
        candlestickData.setLeftMax(leftMax);
        scatterData.setLeftMax(leftMax);

        barData.setLeftMin(leftMin);
        lineData.setLeftMin(leftMin);
        candlestickData.setLeftMin(leftMin);
        scatterData.setLeftMin(leftMin);

        barData.setRightMax(rightMax);
        lineData.setRightMax(rightMax);
        candlestickData.setRightMax(rightMax);
        scatterData.setRightMax(rightMax);

        barData.setRightMin(rightMin);
        lineData.setRightMin(rightMin);
        candlestickData.setRightMin(rightMin);
        scatterData.setRightMin(rightMin);

        barData.getDataSets().addAll(combineData.getBarData());
        lineData.getDataSets().addAll(combineData.getLineData());
        candlestickData.getDataSets().addAll(combineData.getCandlestickData());
        scatterData.getDataSets().addAll(combineData.getScatterData());
    }

    @Override public void calcMaxMin(Viewport viewport, Rect content) {
        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        rightMin = Float.MAX_VALUE;
        rightMax = -Float.MAX_VALUE;

        if (!candlestickData.getDataSets().isEmpty()) {
            candlestickData.calcMaxMin(viewport, content);
            leftMin = Math.min(candlestickData.leftMin, leftMin);
            leftMax = Math.max(candlestickData.leftMax, leftMax);
            rightMin = Math.min(candlestickData.rightMin, rightMin);
            rightMax = Math.max(candlestickData.rightMax, rightMax);
        }

        if (!lineData.getDataSets().isEmpty()) {
            lineData.calcMaxMin(viewport, content);
            leftMin = Math.min(lineData.leftMin, leftMin);
            leftMax = Math.max(lineData.leftMax, leftMax);
            rightMin = Math.min(lineData.rightMin, rightMin);
            rightMax = Math.max(lineData.rightMax, rightMax);
        }

        if (!barData.getDataSets().isEmpty()) {
            barData.calcMaxMin(viewport, content);
            leftMin = Math.min(barData.leftMin, leftMin);
            leftMax = Math.max(barData.leftMax, leftMax);
            rightMin = Math.min(barData.rightMin, rightMin);
            rightMax = Math.max(barData.rightMax, rightMax);
        }

        if (!scatterData.getDataSets().isEmpty()) {
            scatterData.calcMaxMin(viewport, content);
            leftMin = Math.min(scatterData.leftMin, leftMin);
            leftMax = Math.max(scatterData.leftMax, leftMax);
            rightMin = Math.min(scatterData.rightMin, rightMin);
            rightMax = Math.max(scatterData.rightMax, rightMax);
        }

        barData.setLeftMax(leftMax);
        lineData.setLeftMax(leftMax);
        candlestickData.setLeftMax(leftMax);
        scatterData.setLeftMax(leftMax);

        barData.setLeftMin(leftMin);
        lineData.setLeftMin(leftMin);
        candlestickData.setLeftMin(leftMin);
        scatterData.setLeftMin(leftMin);

        barData.setRightMax(rightMax);
        lineData.setRightMax(rightMax);
        candlestickData.setRightMax(rightMax);
        scatterData.setRightMax(rightMax);

        barData.setRightMin(rightMin);
        lineData.setRightMin(rightMin);
        candlestickData.setRightMin(rightMin);
        scatterData.setRightMin(rightMin);

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


    public <T extends AbstractDataSet> void addAll(List<T> dataSets) {
        for (AbstractDataSet dataSet : dataSets) {
            add(dataSet);
        }
    }

}
