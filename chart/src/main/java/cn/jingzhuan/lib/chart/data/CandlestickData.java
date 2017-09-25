package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import android.util.Log;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import java.util.List;

/**
 * Created by donglua on 8/29/17.
 */

public class CandlestickData extends ChartData<CandlestickDataSet> {

  @Override
  public void calcMaxMin(Viewport viewport, Rect content) {

    leftMax = -Float.MAX_VALUE;
    leftMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;

    if (getDataSets().isEmpty()) return;

    for (CandlestickDataSet candlestickDataSet : getDataSets()) {

      //for (CandlestickValue e : getVisiblePoints(candlestickDataSet, viewport)) {
        candlestickDataSet.calcMinMax(viewport);
        if (candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH
            || candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
          leftMin = Math.min(leftMin, candlestickDataSet.getYMin());
          leftMax = Math.max(leftMax, candlestickDataSet.getYMax());
        }
        if (candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH
            || candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
          rightMin = Math.min(rightMin, candlestickDataSet.getYMin());
          rightMax = Math.max(rightMax, candlestickDataSet.getYMax());
        }
      //}
    }
  }

  protected List<CandlestickValue> getVisiblePoints(CandlestickDataSet candlestickDataSet, Viewport viewport) {
    int from = (int) (viewport.left * candlestickDataSet.getValues().size());
    int to   = (int) (viewport.right * candlestickDataSet.getValues().size());

    return candlestickDataSet.getValues().subList(from, to);
  }


}
