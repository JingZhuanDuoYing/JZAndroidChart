package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

/**
 * Created by Donglua on 17/7/19.
 */

public interface IDataSet {

  void calcMinMax(Viewport viewport);

  int getEntryCount();

  float getViewportYMin();

  float getViewportYMax();

  void setMaxVisibleEntryCount(int maxVisibleEntryCount);

  void setMinVisibleEntryCount(int minVisibleEntryCount);

  void setDefaultVisibleEntryCount(int defaultVisibleEntryCount);

  @AxisDependency int getAxisDependency();

  boolean isEnable();

  int getMinValueCount();

  void setMinValueCount(int minValueCount);
}
