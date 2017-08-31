package cn.jingzhuan.lib.chart.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Donglua on 17/8/2.
 */

public class ChartData<T extends IDataSet> {

    private List<T> chartData;

    private float min = Float.MAX_VALUE;
    private float max = -Float.MAX_VALUE;
    private int entryCount = 0;

    public ChartData() {
        this.chartData = new CopyOnWriteArrayList<>();
    }

    public List<T> getDataSets() {
        if (chartData == null) {
            chartData = new CopyOnWriteArrayList<>();
        }
        return chartData;
    }

    public boolean add(T e) {
        if (e == null) return false;

        return getDataSets().add(e);
    }

    public boolean remove(T e) {
        if (e == null) return false;

        return getDataSets().remove(e);
    }

    public void calcMinMax() {
        for (T t : getDataSets()) {
            if (t.getYMax() > max) {
                max = t.getYMax();
            }
            if (t.getYMin() < min) {
                min = t.getYMin();
            }
            if (t.getEntryCount() > entryCount) {
                entryCount = t.getEntryCount();
            }
        }
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public int getEntryCount() {
        return entryCount;
    }
}
