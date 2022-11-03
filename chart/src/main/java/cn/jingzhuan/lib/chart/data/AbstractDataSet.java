package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Rect;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;
import cn.jingzhuan.lib.chart.utils.RequestDataType;

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
  private int minVisibleEntryCount = 15;
  private int defaultVisibleEntryCount = -1;
  private boolean isHighlightedVerticalEnable = false;
  private boolean isHighlightedHorizontalEnable = false;

  private boolean enable = true;

  private int minValueCount = -1;

  private int drawIndex = -1;

  private String tag;

  private DataFormatter formatter; // 数据格式化器

  public AbstractDataSet() {
  }

  public AbstractDataSet(String tag) {
    this.tag = tag;
  }

  @Override
  public void calcMinMax(Viewport viewport) {}
  @Override
  public void calcMinMax(Viewport viewport, Rect content, float max, float mix) {
    calcMinMax(viewport);
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
    ArrayList<T> allValue = new ArrayList<>(getValues());
    int listSize = allValue.size();

    int from = Math.round(viewport.left * listSize);
    int to = Math.round(viewport.right * listSize);

    if (listSize <= minVisibleEntryCount) {
      return allValue;
    }

    return safeSubList(allValue, from, to);
  }

  private List<T> safeSubList(List<T> list, int from, int to) {
    if (list == null || list.isEmpty()) return list;
    int size = list.size();
    int safeFrom =  (from >= size) ? 0 : from;
    int safeTo = Math.min(to, size);
    return list.subList(safeFrom, safeTo);
  }

  public float getVisibleRange(Viewport viewport) {
    return (viewport.right - viewport.left) * getEntryCount();
  }

  @Override
  public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
    this.maxVisibleEntryCount = maxVisibleEntryCount;
  }

  public int getMaxVisibleEntryCount() {
    return maxVisibleEntryCount;
  }

  @Override
  public void setMinVisibleEntryCount(int minVisibleEntryCount) {
    this.minVisibleEntryCount = minVisibleEntryCount;
  }

  public int getMinVisibleEntryCount() {
    return minVisibleEntryCount;
  }

  @Override
  public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
    this.defaultVisibleEntryCount = defaultVisibleEntryCount;
  }

  public int getDefaultVisibleEntryCount() {
    return defaultVisibleEntryCount;
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
//    if (minValueCount > 0 && getValues() != null && getValues().size() > 0) {
//      startIndex = getEntryCount() - getValues().size();
//    }
    return startIndex;
  }

  public int getDrawIndex() {
    return drawIndex;
  }

  public void setDrawIndex(int drawIndex) {
    this.drawIndex = drawIndex;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public DataFormatter getFormatter() {
    return formatter;
  }

  public void setFormatter(DataFormatter formatter) {
    this.formatter = formatter;
  }
}
