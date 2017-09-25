package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

/**
 * Created by Donglua on 17/8/2.
 */

public class LineData extends ChartData<LineDataSet> {

  @Override public void calcMaxMin(Viewport viewport, Rect content) {
    leftMax = -Float.MAX_VALUE;
    leftMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;
    for (LineDataSet lineDataSet : getDataSets()) {
      lineDataSet.calcMinMax(viewport);
      if (lineDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH || lineDataSet.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
        leftMax = Math.max(leftMax, lineDataSet.getViewportYMax());
        leftMin = Math.min(leftMin, lineDataSet.getViewportYMin());
      }
      if (lineDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH || lineDataSet.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
        rightMax = Math.max(rightMax, lineDataSet.getViewportYMax());
        rightMin = Math.min(rightMin, lineDataSet.getViewportYMin());
      }
    }
  }
}
