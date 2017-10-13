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
    //
    ///**
    // * maximum y-value in the value array across all axes
    // */
    //protected float mYMax = -Float.MAX_VALUE;
    //
    ///**
    // * the minimum y-value in the value array across all axes
    // */
    //protected float mYMin = Float.MAX_VALUE;

    protected float mViewportYMin = Float.MAX_VALUE;
    protected float mViewportYMax = -Float.MAX_VALUE;

    private int mAxisDependency = AxisY.DEPENDENCY_LEFT;

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

    public float getViewportYMin() {
        return mViewportYMin;
    }

    public float getViewportYMax() {
        return mViewportYMax;
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

}
