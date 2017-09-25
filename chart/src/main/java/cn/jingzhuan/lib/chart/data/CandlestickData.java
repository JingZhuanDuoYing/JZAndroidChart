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

  private float mViewportWidth = -1;

  @Override
  public void calcMaxMin(Viewport viewport, Rect content) {
    if (getDataSets().isEmpty()) return;

    leftMax = -Float.MAX_VALUE;
    leftMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;

    int i = 0;

    for (CandlestickDataSet candlestickDataSet : getDataSets()) {

        boolean needCalcCandleWidth = Float.compare(viewport.width(), mViewportWidth) != 0;

        candlestickDataSet.calcMinMax(viewport);

        if (needCalcCandleWidth) {
          candlestickDataSet.setCandleWidth(content.width() / (candlestickDataSet.getVisibleCount(viewport) + 1));
        }
        mViewportWidth = viewport.width();

        if (candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH
            || candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
          leftMin = Math.min(leftMin, candlestickDataSet.getViewportYMin());
          leftMax = Math.max(leftMax, candlestickDataSet.getViewportYMax());
        }
        if (candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_BOTH
            || candlestickDataSet.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
          rightMin = Math.min(rightMin, candlestickDataSet.getViewportYMin());
          rightMax = Math.max(rightMax, candlestickDataSet.getViewportYMax());
        }
        Log.d("drawDataSet", "-- CandlestickData leftMin = " + leftMin + ", leftMax = " + leftMax);
        i ++;
    }
    Log.d("drawDataSet", i + "-->>>>>>>>>>>>>>>>>> CandlestickData leftMin = " + leftMin + ", leftMax = " + leftMax);

  }

}
