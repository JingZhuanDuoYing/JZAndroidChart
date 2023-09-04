package cn.jingzhuan.lib.chart3.data;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.base.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.IDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart3.base.AbstractChartView;

/**
 * @since 2023-09-04
 * created by lei
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
        this.chartData = Collections.synchronizedList(new ArrayList<>());
    }

    public List<T> getDataSets() {
        if (chartData == null) {
            chartData = Collections.synchronizedList(new ArrayList<>());
        }
        return chartData;
    }

    public boolean add(T e) {
        if (e == null) return false;
        synchronized (this) {
            return getDataSets().add(e);
        }
    }

    public boolean remove(T e) {
        synchronized (this) {
            return e != null && getDataSets().remove(e);
        }
    }

    public void clear() {
        synchronized (this) {
            getDataSets().clear();
        }

        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        rightMin = Float.MAX_VALUE;
        rightMax = -Float.MAX_VALUE;
    }

    public void setMinMax() {
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
        calcMaxMin(viewport, content, -Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public void calcMaxMin(Viewport viewport, Rect content, float lMax, float lMin, float rMax, float rMin) {
        leftMin = lMin;
        leftMax = lMax;
        rightMin = rMin;
        rightMax = rMax;

        entryCount = 0;

        if (!getDataSets().isEmpty()) {
            synchronized (this) {
                for (T t : getDataSets()) {

                    if (!t.isEnable() || t instanceof ScatterDataSet) continue;

                    t.calcMinMax(viewport, content, -Float.MAX_VALUE, Float.MAX_VALUE);
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
//        Log.d("ChartData", "calcMaxMin_0 leftMax:" + leftMax
//                + ", leftMin:" + leftMin + ", rightMax:" + rightMax + ", rightMin:" + rightMin);
                for (T t : getDataSets()) {

                    if (!t.isEnable() || !(t instanceof ScatterDataSet)) continue;

                    if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
                        t.calcMinMax(viewport, content, leftMax, leftMin);
                    }
                    if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
                        t.calcMinMax(viewport, content, rightMax, rightMin);
                    }
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
            }
//      Log.d("ChartData", "calcMaxMin_1 leftMax:" + leftMax
//              + ", leftMin:" + leftMin + ", rightMax:" + rightMax + ", rightMin:" + rightMin);
            setMinMax();
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

    public void setLeftMin(float leftMin) {
        this.leftMin = leftMin;
    }

    public void setLeftMax(float leftMax) {
        this.leftMax = leftMax;
    }

    public void setRightMin(float rightMin) {
        this.rightMin = rightMin;
    }

    public void setRightMax(float rightMax) {
        this.rightMax = rightMax;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public void setChart(Chart chart) {
        this.leftAxis = chart.getAxisLeft();
        this.rightAxis = chart.getAxisRight();
    }

    public void setChart(AbstractChartView chart) {
        this.leftAxis = (AxisY) chart.getAxisLeftRenderer().getAxis();
        this.rightAxis = (AxisY) chart.getAxisRightRenderer().getAxis();
    }

}
