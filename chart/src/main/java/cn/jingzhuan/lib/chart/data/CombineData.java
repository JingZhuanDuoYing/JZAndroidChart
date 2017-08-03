package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData {

    private BarData barData;
    private LineData lineData;

    private float mYMin = Float.MAX_VALUE;
    private float mYMax = -Float.MAX_VALUE;

    public CombineData() {
        barData = new BarData();
        lineData = new LineData();
    }

    public int getEntryCount() {
        return Math.max(barData.getEntryCount(), lineData.getEntryCount());
    }

    public void addDataSet(LineDataSet dataSet) {
        lineData.add(dataSet);
        lineData.calcMinMax();
        calcMinMax();
    }

    public void addDataSet(BarDataSet dataSet) {
        barData.add(dataSet);
        lineData.calcMinMax();
        calcMinMax();
    }

    public void calcMinMax() {
        mYMax = Math.max(barData.getMax(), lineData.getMax());
        mYMin = Math.min(lineData.getMin(), lineData.getMin());
    }

    public float getMax() {
        return mYMax;
    }

    public float getMin() {
        return mYMin;
    }
}
