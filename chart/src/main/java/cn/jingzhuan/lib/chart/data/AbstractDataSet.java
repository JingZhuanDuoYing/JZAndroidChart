package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract DataSet
 * <p>
 * Created by Donglua on 17/7/20.
 */

public abstract class AbstractDataSet<T extends Value> extends AbstractVisible implements IDataSet {

  protected float mViewportYMin = Float.MAX_VALUE;
  protected float mViewportYMax = -Float.MAX_VALUE;
  protected float minValueOffsetPercent = 0F;
  protected float maxValueOffsetPercent = 0F;
  protected float startXOffset = 0f;
  protected float endXOffset = 0f;
  private int mAxisDependency = AxisY.DEPENDENCY_LEFT;
  private int mColor = Color.GRAY;
  private int maxVisibleEntryCount = 500;
  private int minVisibleEntryCount = 20;
  private int defaultVisibleEntryCount = -1;
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

  public abstract List<T> getValues();

  public abstract void setValues(List<T> values);

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

  public boolean isHighlightedVerticalEnable() {
    return isHighlightedVerticalEnable;
  }

  public void setHighlightedVerticalEnable(boolean highlightedVerticalEnable) {
    isHighlightedVerticalEnable = highlightedVerticalEnable;
  }

  public boolean isHighlightedHorizontalEnable() {
    return isHighlightedHorizontalEnable;
  }

  public void setHighlightedHorizontalEnable(boolean highlightedHorizontalEnable) {
    isHighlightedHorizontalEnable = highlightedHorizontalEnable;
  }

  public List<T> getVisiblePoints(Viewport viewport) {
    //生成一个拷贝,利用不可变的思想保证这里不存在并发问题
    ArrayList<T> backUpList = new ArrayList<>(getValues());
    //防止多次调用
    int listSize = backUpList.size();

    int from = Math.round(viewport.left * listSize);
    int to = Math.round(viewport.right * listSize);

    if (Float.compare(viewport.width(), 1f) == 0
        && defaultVisibleEntryCount > 0
        && defaultVisibleEntryCount < listSize) {
      from = to - defaultVisibleEntryCount;
      viewport.left = from / (float) listSize;
    } else {
      if (maxVisibleEntryCount > 0 && to - from > maxVisibleEntryCount) {
        from = to - maxVisibleEntryCount;
        viewport.left = from / (float) listSize;
      }
      if (minVisibleEntryCount > 0
          && minVisibleEntryCount < listSize
          && to - from < minVisibleEntryCount) {
        if (to >= minVisibleEntryCount) {
          from = to - minVisibleEntryCount;
          //防止越界
          if (from < 0) {
            from = 0;
          }
          viewport.left = from / (float) listSize;
        } else {
          to = from + minVisibleEntryCount;
          //防止越界
          if (to >= listSize) {
            to = listSize - 1;
          }
          viewport.right = to / (float) listSize;
        }
      }
    }

    return backUpList.subList(from, to);
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

  @Override
  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
    this.setVisible(enable);
  }

  @Override
  public int getMinValueCount() {
    return minValueCount;
  }

  @Override
  public void setMinValueCount(int minValueCount) {
    this.minValueCount = minValueCount;
  }

  public int getStartIndexOffset() {
    int startIndex = 0;
    if (minValueCount > 0 && getValues() != null && getValues().size() > 0) {
      startIndex = getEntryCount() - getValues().size();
    }
    return startIndex;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }
}
