package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.base.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.ScatterData;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import java.util.List;

/**
 * Scatter Chart Renderer
 *
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

    final float width = (mContentRect.width() - dataSet.getStartXOffset() - dataSet.getEndXOffset())
        / dataSet.getVisibleRange(mViewport) + 1;

    float shapeWidth = dataSet.getShape().getIntrinsicWidth();
    float shapeHeight = dataSet.getShape().getIntrinsicHeight();
    if (dataSet.isAutoWidth()) {
      shapeWidth = width * 0.8f;
      shapeHeight = shapeWidth * shapeHeight / ((float) dataSet.getShape().getIntrinsicWidth());
    }

    for (int i = 0; i < valueCount && i < dataSet.getValues().size() && dataSet.getShape() != null; i++) {
      ScatterValue point = dataSet.getEntryForIndex(i);

      if (!point.isVisible()) continue;

      float xPosition = dataSet.getStartXOffset() + width * 0.5f
          + getDrawX((i + dataSet.getStartIndexOffset()) / ((float) valueCount)) - shapeWidth * 0.5f;
      float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height() - shapeHeight * 0.5f;

      point.setCoordinate(xPosition, yPosition);

      int x = (int) (xPosition + dataSet.getDrawOffsetX());
      int y = (int) (yPosition + dataSet.getDrawOffsetY());
      if (point.getColor() != Color.TRANSPARENT) {
        dataSet.getShape().setColorFilter(point.getColor(), PorterDuff.Mode.SRC_OVER);
      }
      dataSet.getShape().setBounds(x,
                                   y,
                                  (int) (x + shapeWidth),
                                  (int) (y + shapeHeight));
      int saveId = canvas.save();
      dataSet.getShape().draw(canvas);
      canvas.restoreToCount(saveId);

      if (dataSet.getTextValueRenderers() != null) {
        for (TextValueRenderer textValueRenderer : dataSet.getTextValueRenderers()) {
          textValueRenderer.render(canvas, i,
              x + shapeWidth * 0.5f, y + shapeHeight * 0.5f);
        }
      }
    }
  }

  @Override public int getEntryIndexByCoordinate(float x, float y) {
    int index = -1;
    if (scatterData.getDataSets().size() > 0) {
      ScatterDataSet dataSet = scatterData.getDataSets().get(0);
      RectF rect = new RectF();
      float shapeWidth = dataSet.getShape().getIntrinsicWidth();
      float shapeHeight = dataSet.getShape().getIntrinsicHeight();
      for (int i = 0; i < dataSet.getValues().size(); i++) {
        final ScatterValue value = dataSet.getEntryForIndex(i);
        float pX = value.getX();
        float pY = value.getY();
        rect.set(pX, pY, pX + shapeWidth, pY + shapeHeight);
        if (rect.contains(x, y)) {
          index = i;
          break;
        }
      }
      return index;
    }
    return super.getEntryIndexByCoordinate(x, y);
  }

  @Override public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
  }

  @Override public void removeDataSet(ScatterDataSet dataSet) {
    getChartData().remove(dataSet);
    calcDataSetMinMax();
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
