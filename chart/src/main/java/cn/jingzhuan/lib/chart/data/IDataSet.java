package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

/**
 * Created by Donglua on 17/7/19.
 */

public interface IDataSet {

  void calcMinMax(Viewport viewport);

  /**
   * 得到图标库数据集合的数量，注意这个由子类自主实现
   *
   */
  int getEntryCount();

  /**
   * 得到数据集合中对应Y坐标最大的数值，返回的是数值不是对应的坐标
   * @return
   */
  float getViewportYMin();

  /**
   * 得到数据集合中对应Y坐标最大的数值，返回的是数值不是对应的坐标
   * @return
   */
  float getViewportYMax();

  void setMaxVisibleEntryCount(int maxVisibleEntryCount);

  void setMinVisibleEntryCount(int minVisibleEntryCount);

  void setDefaultVisibleEntryCount(int defaultVisibleEntryCount);

  @AxisDependency int getAxisDependency();

  boolean isEnable();

  int getMinValueCount();

  void setMinValueCount(int minValueCount);
}
