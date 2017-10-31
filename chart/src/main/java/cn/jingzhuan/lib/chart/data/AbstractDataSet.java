package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;

import cn.jingzhuan.lib.chart.Viewport;
import java.util.List;

import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

/**
 * Created by Donglua on 17/7/20.
 */

public abstract class AbstractDataSet<T extends Value> extends AbstractVisible implements IDataSet {

    protected float mViewportYMin = Float.MAX_VALUE;
    protected float mViewportYMax = -Float.MAX_VALUE;

    private int mAxisDependency = AxisY.DEPENDENCY_LEFT;

    private int mColor = Color.GRAY;

    private int maxVisibleEntryCount = 500;
    private int minVisibleEntryCount = 20;

    private boolean isHighlightedVerticalEnable = false;
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

    public void setHighlightedVerticalEnable(boolean highlightedVerticalEnable) {
        isHighlightedVerticalEnable = highlightedVerticalEnable;
    }

    public boolean isHighlightedVerticalEnable() {
        return isHighlightedVerticalEnable;
    }

    public void setHighlightedHorizontalEnable(boolean highlightedHorizontalEnable) {
        isHighlightedHorizontalEnable = highlightedHorizontalEnable;
    }

    public boolean isHighlightedHorizontalEnable() {
        return isHighlightedHorizontalEnable;
    }

    public List<T> getVisiblePoints(Viewport viewport) {
        int from = (int) (viewport.left * getValues().size());
        int to  = (int) (viewport.right * getValues().size());

        if (getMaxVisibleEntryCount() > 0 && to - from > getMaxVisibleEntryCount()) {
            from = to - getMaxVisibleEntryCount();
            viewport.left = from / (float) getValues().size();
        }
        if (getMinVisibleEntryCount() > 0
            && getMinVisibleEntryCount() < getValues().size()
            && to - from < getMinVisibleEntryCount()) {
            if (to >= getMinVisibleEntryCount()) {
                from = to - getMinVisibleEntryCount();
                viewport.left = from / (float) getValues().size();
            } else {
                to = from + getMinVisibleEntryCount();
                viewport.right = to / (float) getValues().size();
            }
        }
        return getValues().subList(from, to);
    }

    public int getVisibleValueCount(Viewport viewport) {
        return (int) ((viewport.right - viewport.left) * getEntryCount());
    }

    public int getMaxVisibleEntryCount() {
        return maxVisibleEntryCount;
    }

    public int getMinVisibleEntryCount() {
        return minVisibleEntryCount;
    }

    @Override
    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        this.maxVisibleEntryCount = maxVisibleEntryCount;
    }

    @Override
    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        this.minVisibleEntryCount = minVisibleEntryCount;
    }
}
