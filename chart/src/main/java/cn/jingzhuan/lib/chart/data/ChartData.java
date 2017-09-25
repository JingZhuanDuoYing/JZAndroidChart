package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Donglua on 17/8/2.
 */

public class ChartData<T extends IDataSet> {

    private List<T> chartData;

    protected float leftMin = Float.MAX_VALUE;
    protected float leftMax = -Float.MAX_VALUE;
    protected float rightMin = Float.MAX_VALUE;
    protected float rightMax = -Float.MAX_VALUE;

    private int entryCount = 0;

    protected AxisY leftAxis;
    protected AxisY rightAxis;

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

    public void setMinMax() {
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
        if (leftAxis != null && leftMin != Float.MAX_VALUE) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null && rightMin != Float.MAX_VALUE) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }

    public void calcMaxMin(Viewport viewport, Rect content) {
        for (T t : chartData) {
            t.calcMinMax(viewport);
            if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
                leftMax = Math.max(leftMax, t.getViewportYMax());
                leftMin = Math.min(leftMin, t.getViewportYMin());
            }
            if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
                rightMax = Math.max(rightMax, t.getViewportYMax());
                rightMin = Math.min(rightMin, t.getViewportYMin());
            }
            if (t.getEntryCount() > entryCount) {
                entryCount = t.getEntryCount();
            }
        }
        if (leftAxis != null && leftMin != Float.MAX_VALUE) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null && rightMin != Float.MAX_VALUE) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }

    public float getLeftMin() {
        return leftMin;
    }

    public float getRightMax() {
        return rightMax;
    }

    public float getRightMin() {
        return rightMin;
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
