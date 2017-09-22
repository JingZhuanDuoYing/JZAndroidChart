package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.widget.CombineChart;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Donglua on 17/8/2.
 */

public class ChartData<T extends IDataSet> {

    private List<T> chartData;

    private float leftMin = Float.MAX_VALUE;
    private float leftMax = -Float.MAX_VALUE;
    private float rightMin = Float.MAX_VALUE;
    private float rightMax = -Float.MAX_VALUE;

    private int entryCount = 0;

    private AxisY leftAxis;
    private AxisY rightAxis;

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
        return e != null && getDataSets().remove(e);
    }

    public void clear() {
        getDataSets().clear();
    }

    public void calcMinMax() {
        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        rightMin = Float.MAX_VALUE;
        rightMax = -Float.MAX_VALUE;

        for (T t : getDataSets()) {

            if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
                if (t.getViewportYMax() > leftMax) {
                    leftMax = t.getViewportYMax();
                }
                if (t.getViewportYMin() < leftMin) {
                    leftMin = t.getViewportYMin();
                }
            }
            if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
                if (t.getViewportYMax() > rightMax) {
                    rightMax = t.getViewportYMax();
                }
                if (t.getViewportYMin() < rightMin) {
                    rightMin = t.getViewportYMin();
                }
            }

            if (t.getEntryCount() > entryCount) {
                entryCount = t.getEntryCount();
            }
        }
        if (leftAxis != null) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }

    public float getLeftMin() {
        return leftMin;
    }

    public float getLeftMax() {
        return leftMax;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public void setChart(Chart chart) {
        this.leftAxis = chart.getAxisLeft();
        this.rightAxis = chart.getAxisRight();
    }
}
