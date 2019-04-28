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

    protected float minValueOffsetPercent = 0F;
    protected float maxValueOffsetPercent = 0F;

    protected float startXOffset = 0f;
    protected float endXOffset = 0f;

    private boolean isHighlightedVerticalEnable = false;
    private boolean isHighlightedHorizontalEnable = false;

    private boolean enable = true;

    private int minValueCount = -1;

    private String tag;

    public AbstractDataSet() {
    }

    public AbstractDataSet(String tag) {
        this.tag = tag;
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
        int from = Math.round(viewport.left * getValues().size());
        int to = Math.round(viewport.right * getValues().size());

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

    public float getVisibleRange(Viewport viewport) {
        return (viewport.right - viewport.left) * getEntryCount();
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

    @Override
    public int getMinValueCount() {
        return minValueCount;
    }

    public int getStartIndexOffset() {
        int startIndex = 0;
        if (minValueCount > 0 && getValues() != null && getValues().size() > 0) {
            startIndex = getEntryCount() - getValues().size();
        }
        return startIndex;
    }

    @Override
    public void setMinValueCount(int minValueCount) {
        this.minValueCount = minValueCount;
    }

    public String getTag() {
    return tag;
  }

    public void setTag(String tag) {
    this.tag = tag;
  }
}
