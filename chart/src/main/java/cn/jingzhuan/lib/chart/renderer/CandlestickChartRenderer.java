package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.PointValue;
import java.util.List;

/**
 *
 * Created by donglua on 8/29/17.
 */

public class CandlestickChartRenderer extends AbstractDataRenderer<CandlestickDataSet> {

  private float[] mUpperShadowBuffers = new float[4];
  private float[] mLowerShadowBuffers = new float[4];
  private float[] mBodyBuffers = new float[4];

  public CandlestickChartRenderer(Chart chart) {
    super(chart);
  }

  @Override protected void renderDataSet(Canvas canvas) {
    for (CandlestickDataSet candlestickDataSet : getDataSet()) {
      if (candlestickDataSet.isVisible()) {
        drawDataSet(canvas, candlestickDataSet);
      }
    }
  }

  private void drawDataSet(Canvas canvas, CandlestickDataSet candlestickDataSet) {

    float min = candlestickDataSet.getViewportYMin();
    float max = candlestickDataSet.getViewportYMax();

    mRenderPaint.setStrokeWidth(5);
    mRenderPaint.setColor(candlestickDataSet.getColor());

    int valueCount = candlestickDataSet.getEntryCount();

    Path path = new Path();
    path.reset();

    for (int i = 0; i < valueCount; i++) {
      CandlestickValue candlestickValue = candlestickDataSet.getEntryForIndex(i);

      float xPostion = getDrawX(i / (valueCount - 1f));
      float highY  = (max - candlestickValue.getHigh())  / (max - min) * mContentRect.height();
      float lowY   = (max - candlestickValue.getLow())   / (max - min) * mContentRect.height();
      float openY  = (max - candlestickValue.getOpen())  / (max - min) * mContentRect.height();
      float closeY = (max - candlestickValue.getClose()) / (max - min) * mContentRect.height();


      mBodyBuffers[0] = xPostion;
      mBodyBuffers[1] = closeY;
      mBodyBuffers[2] = xPostion + 5;
      mBodyBuffers[3] = openY;
      canvas.drawLines(mBodyBuffers, mRenderPaint);
    }

  }

  @Override public void renderHighlighted(Canvas canvas, Highlight[] highlights) {

  }

  @Override public void addDataSet(CandlestickDataSet dataSet) {

  }

  @Override public List<CandlestickDataSet> getDataSet() {
    return null;
  }
}
