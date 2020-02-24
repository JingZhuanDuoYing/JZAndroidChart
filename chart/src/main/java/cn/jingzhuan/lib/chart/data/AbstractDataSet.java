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

  /**
   * 设置实体数据集合
   * @param values
   */
  public abstract void setValues(List<T> values);

  /**
   * 得到实体的数据集
   * @return
   */
  public abstract List<T> getValues();

  /**
   * 添加一个实体
   * @param e
   * @return
   */
  public abstract boolean addEntry(T e);

  /**
   * 移除一个实体对象
   * @param e
   * @return
   */
  public abstract boolean removeEntry(T e);
  /**
   * 获取某个坐标信息在集合中的下标信息
   * @param e
   * @return
   */
  public abstract int getEntryIndex(T e);

  /**
   * 通过下标获取实体数据
   */
  public abstract T getEntryForIndex(int index);

  @AxisDependency
  public int getAxisDependency() {
    return mAxisDependency;
  }

  /**
   * 设置当前dataSet依赖于图表库最左边的垂直的坐标体系还是最右边，或者左右两边
   * todo 存在缺陷
   */
  public void setAxisDependency(@AxisDependency int mAxisDependency) {
    this.mAxisDependency = mAxisDependency;
  }

  /**
   * {@inheritDoc}
   */
  public float getViewportYMin() {
    return mViewportYMin;
  }

  /**
   * 得到数据集合中对应Y坐标最大的数值，返回的是数值不是对应的坐标
   */
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

  /**
   * 获取某个可见区域的数据集合列表
   */
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

  /**
   * 可见区域可以显示的数据集合的数量
   * @param viewport
   * @return
   */
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

  /**
   * 当前数据集是否有效
   */
  @Override
  public boolean isEnable() {
    return enable;
  }

  /**
   * 当前数据集合中的最小的数据数量
   */
  @Override
  public int getMinValueCount() {
    return minValueCount;
  }

  /**
   * 获得绘制数据列表的起始偏移坐标
   */
  public int getStartIndexOffset() {
    int startIndex = 0;
    if (minValueCount > 0 && getValues() != null && getValues().size() > 0) {
      startIndex = getEntryCount() - getValues().size();
    }
    return startIndex;
  }

  /**
   * 设置当前数据集合中的最小的数据数量
   * 如果数据流不够默认在前面进行填充
   */
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
