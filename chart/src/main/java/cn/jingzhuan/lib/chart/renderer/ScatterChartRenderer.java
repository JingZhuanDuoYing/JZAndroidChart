package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.ScatterData;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterChartRenderer extends AbstractDataRenderer<ScatterDataSet> {
  private ScatterData scatterData;

  public ScatterChartRenderer(Chart chart) {
    super(chart);
  }

  @Override protected void renderDataSet(Canvas canvas, ChartData<ScatterDataSet> chartData) {
    for (ScatterDataSet dataSet : getDataSet()) {
      if (dataSet.isVisible()) {
        drawDataSet(canvas, dataSet,
            chartData.getLeftMax(), chartData.getLeftMin(),
            chartData.getRightMax(), chartData.getRightMin());
      }
    }
  }

  private void drawDataSet(Canvas canvas, final ScatterDataSet dataSet,
      float leftMax, float leftMin, float rightMax, float rightMin) {

    mRenderPaint.setStrokeWidth(2);
    mRenderPaint.setColor(dataSet.getColor());

    int valueCount = dataSet.getEntryCount();

    float min, max;
    switch (dataSet.getAxisDependency()) {
      case AxisY.DEPENDENCY_RIGHT:
        min = rightMin;
        max = rightMax;
        break;
      case AxisY.DEPENDENCY_BOTH:
      case AxisY.DEPENDENCY_LEFT:
      default:
        min = leftMin;
        max = leftMax;
        break;
    }

    final float width = mContentRect.width() / dataSet.getVisibleValueCount(mViewport);
    Log.d("drawDataSet", "valueCount  = " + valueCount);

    for (int i = 0; i < valueCount && i < dataSet.getValues().size(); i++) {
      ScatterValue point = dataSet.getEntryForIndex(i);

      float xPosition = width * 0.5f + getDrawX(i / ((float) valueCount));
      float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();

      point.setX(xPosition);
      point.setY(yPosition);

      Log.d("drawDataSet", "drawDataSet x = " + xPosition + ", y = " + yPosition);

      //if (dataSet.getShape() != null) {
      //  int saveId = canvas.save();
      //  dataSet.getShape().setBounds((int) xPosition, (int) yPosition, dataSet.getShape().getIntrinsicWidth(), dataSet.getShape().getIntrinsicHeight());
      //  canvas.translate(xPosition + dataSet.getDrawOffsetX(), yPosition + dataSet.getDrawOffsetY());
      //  dataSet.getShape().draw(canvas);
      //  canvas.restoreToCount(saveId);
      //}

      mRenderPaint.setColor(Color.BLACK);
      mRenderPaint.setStrokeWidth(20);
      canvas.drawPoint(xPosition, yPosition, mRenderPaint);
    }

  }

  @Override public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
  }


  @Override public void addDataSet(ScatterDataSet dataSet) {
    getChartData().add(dataSet);
  }

  @Override public void removeDataSet(ScatterDataSet dataSet) {
    getChartData().remove(dataSet);
  }

  @Override public void clearDataSet() {
    getChartData().clear();
  }

  @Override protected List<ScatterDataSet> getDataSet() {
    return getChartData().getDataSets();
  }

  @Override public ChartData<ScatterDataSet> getChartData() {
    if (scatterData == null) {
      scatterData = new ScatterData();
    }
    return scatterData;
  }

}
