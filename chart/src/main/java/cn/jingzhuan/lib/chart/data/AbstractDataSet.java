package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;

import cn.jingzhuan.lib.chart.Chart;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AbstractComponent;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

import static cn.jingzhuan.lib.chart.component.AxisY.DEPENDENCY_BOTH;

/**
 * Created by Donglua on 17/7/20.
 */

public abstract class AbstractDataSet<T extends Value> extends AbstractVisible implements IDataSet {

    /**
     * maximum y-value in the value array across all axes
     */
    protected float mYMax = -Float.MAX_VALUE;

    /**
     * the minimum y-value in the value array across all axes
     */
    protected float mYMin = Float.MAX_VALUE;

    /**
     * maximum x-value in the value array
     */
    protected float mXMax = -Float.MAX_VALUE;

    /**
     * minimum x-value in the value array
     */
    protected float mXMin = Float.MAX_VALUE;

    protected float mViewportYMin = Float.MAX_VALUE;
    protected float mViewportYMax = -Float.MAX_VALUE;

    private int mAxisDependency = AxisY.DEPENDENCY_LEFT;
    public Viewport mViewport;


    @AxisDependency protected int mDepsAxis = DEPENDENCY_BOTH;
    protected AxisY mAxisLeft;
    protected AxisY mAxisRight;

    private int mColor = Color.GRAY;


    private boolean isHighlightedEnable = false;
    private boolean isHighlightedHorizontalEnable = false;


    public abstract void setValues(List<T> values);

    public abstract List<T> getValues();

    public abstract boolean addEntry(T e);

    public abstract boolean removeEntry(T e);

    public abstract int getEntryIndex(T e);

    public abstract T getEntryForIndex(int index);

    @AxisDependency
    public int getAxisDependency() {
        return mAxisDependency;
    }

    public void setAxisDependency(@AxisDependency int mAxisDependency) {
        this.mAxisDependency = mAxisDependency;
    }

    public float getYMax() {
        return mYMax;
    }

    public float getYMin() {
        return mYMin;
    }

    public float getViewportYMin() {
        return mViewportYMin;
    }

    public float getViewportYMax() {
        return mViewportYMax;
    }

    public AxisY getAxisLeft() {
        return mAxisLeft;
    }

    public AxisY getAxisRight() {
        return mAxisRight;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int barColor) {
        this.mColor = barColor;
    }


    public void setHighlightedEnable(boolean highlightedEnable) {
        isHighlightedEnable = highlightedEnable;
    }

    public boolean isHighlightedEnable() {
        return isHighlightedEnable;
    }

    public void setHighlightedHorizontalEnable(boolean highlightedHorizontalEnable) {
        isHighlightedHorizontalEnable = highlightedHorizontalEnable;
    }

    public boolean isHighlightedHorizontalEnable() {
        return isHighlightedHorizontalEnable;
    }

    protected static void setAxisViewportY(AxisY axis, float min, float max) {

        if (axis != null) {

            axis.setYMin(min);
            axis.setYMax(max);
        }
    }

    public void setChart(Chart chart) {
        switch (mDepsAxis) {
            case AxisY.DEPENDENCY_LEFT:
                this.mAxisLeft = chart.getAxisLeft();
                this.mAxisRight = null;
                break;
            case AxisY.DEPENDENCY_RIGHT:
                this.mAxisLeft = null;
                this.mAxisRight = chart.getAxisRight();
                break;
            case AxisY.DEPENDENCY_BOTH:
                this.mAxisLeft = chart.getAxisLeft();
                this.mAxisRight = chart.getAxisRight();
                break;
        }

        this.mViewport = chart.getCurrentViewport();

        calcMinMax();
    }
}
