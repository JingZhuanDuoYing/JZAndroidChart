package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

/**
 * Created by Donglua on 17/7/19.
 */

public interface IDataSet {

  void calcMinMax(Viewport viewport);

  int getEntryCount();

  float getYMin();

  float getYMax();

  float getViewportYMin();

  float getViewportYMax();

  @AxisDependency int getAxisDependency();
}
