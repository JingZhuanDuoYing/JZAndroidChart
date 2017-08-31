package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import java.util.List;

/**
 *
 * Created by donglua on 8/29/17.
 */

public class CandlestickChartRenderer extends AbstractDataRenderer<CandlestickDataSet> {

  private float[] mUpperShadowBuffers = new float[4];
  private float[] mLowerShadowBuffers = new float[4];
  private float[] mBodyBuffers = new float[4];
  private ChartData<CandlestickDataSet> chartData;

  public CandlestickChartRenderer(Chart chart) {
    super(chart);

    chartData = new ChartData<>();

    chart.setOnViewportChangeListener(new OnViewportChangeListener() {
      @Override public void onViewportChange(Viewport viewport) {
        for (CandlestickDataSet dataSet : getDataSet()) {
          dataSet.onViewportChange(mContentRect, viewport);
        }
      }
    });
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

      mRenderPaint.setStyle(Paint.Style.FILL);

      float candleWidth = candlestickDataSet.getCandleWidth();

      float xPosition = mContentRect.left + candleWidth * 0.5f
          + (mContentRect.width() - candleWidth) * (i / (valueCount - 1f) - mViewport.left) / mViewport.width();

      float highY  = (max - candlestickValue.getHigh())  / (max - min) * mContentRect.height();
      float lowY   = (max - candlestickValue.getLow())   / (max - min) * mContentRect.height();
      float openY  = (max - candlestickValue.getOpen())  / (max - min) * mContentRect.height();
      float closeY = (max - candlestickValue.getClose()) / (max - min) * mContentRect.height();

      mBodyBuffers[0] = xPosition - candleWidth * 0.4f;
      mBodyBuffers[1] = closeY;
      mBodyBuffers[2] = xPosition + candleWidth * 0.4f;
      mBodyBuffers[3] = openY;

      mUpperShadowBuffers[0] = xPosition;
      mUpperShadowBuffers[2] = xPosition;
      mLowerShadowBuffers[0] = xPosition;
      mLowerShadowBuffers[2] = xPosition;

      if (Float.compare(candlestickValue.getOpen(), candlestickValue.getClose()) > 0) {

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = openY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = closeY;

      } else if (Float.compare(candlestickValue.getOpen(), candlestickValue.getClose()) < 0) {

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = closeY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = openY;

      } else {

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = openY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = mUpperShadowBuffers[3];

      }

      canvas.drawRect(mBodyBuffers[0],
                      mBodyBuffers[1],
                      mBodyBuffers[2],
                      mBodyBuffers[3], mRenderPaint);

      canvas.drawLines(mUpperShadowBuffers, mRenderPaint);

      canvas.drawLines(mLowerShadowBuffers, mRenderPaint);

    }

  }

  @Override public void renderHighlighted(Canvas canvas, Highlight[] highlights) {

  }

  @Override public void addDataSet(CandlestickDataSet dataSet) {
    chartData.add(dataSet);
  }

  @Override public List<CandlestickDataSet> getDataSet() {
    return chartData.getDataSets();
  }
}
