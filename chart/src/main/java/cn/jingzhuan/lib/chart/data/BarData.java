package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

/**
 * Created by Donglua on 17/8/2.
 */

public class BarData extends ChartData<BarDataSet> {

  @Override public void calcMaxMin(Viewport viewport, Rect content) {
    leftMax = -Float.MAX_VALUE;
    leftMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;

    for (BarDataSet barDataSet : getDataSets()) {
      barDataSet.calcMinMax(viewport);

      if (barDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH || barDataSet.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
        leftMax = Math.max(leftMax, barDataSet.getViewportYMax());
        leftMin = Math.min(leftMin, barDataSet.getViewportYMin());
      }
      if (barDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH || barDataSet.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
        rightMax = Math.max(rightMax, barDataSet.getViewportYMax());
        rightMin = Math.min(rightMin, barDataSet.getViewportYMin());
      }
    }
  }

}
