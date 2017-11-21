package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;
import java.util.List;

/**
 * Abstract DataSet
 *
 * Created by Donglua on 17/7/20.
 */

public abstract class AbstractDataSet<T extends Value> extends AbstractVisible implements IDataSet {

    protected float mViewportYMin = Float.MAX_VALUE;
    protected float mViewportYMax = -Float.MAX_VALUE;

    private int mAxisDependency = AxisY.DEPENDENCY_LEFT;

    private int mColor = Color.GRAY;

    private int maxVisibleEntryCount = 500;
    private int minVisibleEntryCount = 20;
    private int defaultVisibleEntryCount = -1;

    private boolean isHighlightedVerticalEnable = false;
    private boolean isHighlightedHorizontalEnable = false;

    private boolean enable = true;

    public AbstractDataSet() {
    }

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
        int to = (int) (viewport.right * getValues().size());

        if (Float.compare(viewport.width(), 1f) == 0
            && defaultVisibleEntryCount > 0
            && defaultVisibleEntryCount < getValues().size()) {
            from = to - defaultVisibleEntryCount;
            viewport.left = from / (float) getValues().size();
        } else {
            if (maxVisibleEntryCount > 0 && to - from > maxVisibleEntryCount) {
                from = to - maxVisibleEntryCount;
                viewport.left = from / (float) getValues().size();
            }
            if (minVisibleEntryCount > 0
                && minVisibleEntryCount < getValues().size()
                && to - from < minVisibleEntryCount) {
                if (to >= minVisibleEntryCount) {
                    from = to - minVisibleEntryCount;
                    viewport.left = from / (float) getValues().size();
                } else {
                    to = from + minVisibleEntryCount;
                    viewport.right = to / (float) getValues().size();
                }
            }
        }
        return getValues().subList(from, to);
    }

    public int getVisibleValueCount(Viewport viewport) {
        return (int) ((viewport.right - viewport.left) * getEntryCount());
    }

    @Override
    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        this.maxVisibleEntryCount = maxVisibleEntryCount;
    }

    @Override
    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        this.minVisibleEntryCount = minVisibleEntryCount;
    }

    @Override
    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        this.defaultVisibleEntryCount = defaultVisibleEntryCount;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        this.setVisible(enable);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }
}
