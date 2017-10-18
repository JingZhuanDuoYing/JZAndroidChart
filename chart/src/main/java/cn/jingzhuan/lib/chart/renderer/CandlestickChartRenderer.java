package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickData;
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
  private CandlestickData chartData;

  public CandlestickChartRenderer(final Chart chart) {
    super(chart);

    chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
      @Override public void onViewportChange(Viewport viewport) {
        mViewport = viewport;
        calcDataSetMinMax();
      }
    });

    chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
      @Override
      public void touch(float x, float y) {
        for (CandlestickDataSet dataSet : getDataSet()) {
          if (dataSet.isHighlightedVerticalEnable()) {

            float valueCount = dataSet.getEntryCount();
            int index = 0;
            float xPosition = x;
            float yPosition = -1;
            if (x > mContentRect.left) {
              index =
                  (int) (((x - mContentRect.left) * mViewport.width() / mContentRect.width() + mViewport.left) * valueCount);

              if (index >= dataSet.getValues().size()) index = dataSet.getValues().size() - 1;

              final CandlestickValue candlestickValue = dataSet.getEntryForIndex(index);
              xPosition = candlestickValue.getX();
              yPosition = candlestickValue.getY();
            }
            chart.highlightValue(new Highlight(xPosition, yPosition, index));
          }
        }
      }
    });
  }



  @Override protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData) {
    for (CandlestickDataSet dataSet : chartData.getDataSets()) {
      if (dataSet.isVisible()) {
        drawDataSet(canvas, dataSet,
            chartData.getLeftMax(), chartData.getLeftMin(),
            chartData.getRightMax(), chartData.getRightMin());
      }
    }
  }

  @Override protected void renderDataSet(Canvas canvas) {
    renderDataSet(canvas, getChartData());
  }

  private void drawDataSet(Canvas canvas, CandlestickDataSet candlestickDataSet,
      float lMax, float lMin, float rMax, float rMin) {
    float min, max;
    switch (candlestickDataSet.getAxisDependency()) {
      case AxisY.DEPENDENCY_RIGHT:
        min = rMin;
        max = rMax;
        break;
      case AxisY.DEPENDENCY_BOTH:
      case AxisY.DEPENDENCY_LEFT:
      default:
        min = lMin;
        max = lMax;
        break;
    }

    mRenderPaint.setStrokeWidth(candlestickDataSet.getStrokeThickness());
    mRenderPaint.setColor(candlestickDataSet.getColor());

    int valueCount = candlestickDataSet.getEntryCount();

    Path path = new Path();
    path.reset();

    for (int i = 0; i < valueCount; i++) {
      final CandlestickValue candlestick = candlestickDataSet.getEntryForIndex(i);

      float candleWidth = candlestickDataSet.getCandleWidth();

      if (candlestickDataSet.isAutoWidth()) {
        candleWidth = mContentRect.width() / candlestickDataSet.getVisibleValueCount(mViewport);
      }

      float xPosition = getDrawX(i / (valueCount + 0f));

      float highY  = (max - candlestick.getHigh())  / (max - min) * mContentRect.height();
      float lowY   = (max - candlestick.getLow())   / (max - min) * mContentRect.height();
      float openY  = (max - candlestick.getOpen())  / (max - min) * mContentRect.height();
      float closeY = (max - candlestick.getClose()) / (max - min) * mContentRect.height();

      float widthPercent = 0.8f;

      mBodyBuffers[0] = xPosition + (1 - widthPercent) * 0.5f * candleWidth;
      mBodyBuffers[1] = closeY;
      mBodyBuffers[2] = mBodyBuffers[0] + candleWidth * widthPercent;
      mBodyBuffers[3] = openY;

      final float candlestickCenterX = xPosition + candleWidth * 0.5f;

      mUpperShadowBuffers[0] = candlestickCenterX;
      mUpperShadowBuffers[2] = candlestickCenterX;
      mLowerShadowBuffers[0] = candlestickCenterX;
      mLowerShadowBuffers[2] = candlestickCenterX;

      candlestick.setX(candlestickCenterX);
      candlestick.setY(closeY);

      if (Float.compare(candlestick.getOpen(), candlestick.getClose()) > 0) { // 阴线

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = openY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = closeY;

        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
          mRenderPaint.setColor(candlestickDataSet.getDecreasingColor());
        } else {
          mRenderPaint.setColor(candlestick.getColor());
        }

        if (candlestick.getPaintStyle() != null) {
          mRenderPaint.setStyle(candlestick.getPaintStyle());
        } else {
          mRenderPaint.setStyle(candlestickDataSet.getDecreasingPaintStyle());
        }

      } else if (Float.compare(candlestick.getOpen(), candlestick.getClose()) < 0) { // 阳线

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = closeY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = openY;

        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
          mRenderPaint.setColor(candlestickDataSet.getIncreasingColor());
        } else {
          mRenderPaint.setColor(candlestick.getColor());
        }

        if (candlestick.getPaintStyle() != null) {
          mRenderPaint.setStyle(candlestick.getPaintStyle());
        } else {
          mRenderPaint.setStyle(candlestickDataSet.getIncreasingPaintStyle());
        }
      } else {

        mUpperShadowBuffers[1] = highY;
        mUpperShadowBuffers[3] = openY;
        mLowerShadowBuffers[1] = lowY;
        mLowerShadowBuffers[3] = mUpperShadowBuffers[3];

        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
          mRenderPaint.setColor(candlestickDataSet.getNeutralColor());
        } else {
          mRenderPaint.setColor(candlestick.getColor());
        }
      }

      if (candlestickDataSet.getLimitUpColor() != Color.TRANSPARENT && i > 0) {
        final CandlestickValue previousValue = candlestickDataSet.getEntryForIndex(i - 1);
        boolean isLimitUp = Float.compare((candlestick.getClose() - previousValue.getClose()) / previousValue.getClose(), 0.095f) > 0;
        if (isLimitUp) {
          mRenderPaint.setColor(candlestickDataSet.getLimitUpColor());
        }
      }

      canvas.drawRect(mBodyBuffers[0],
                      mBodyBuffers[1],
                      mBodyBuffers[2],
                      mBodyBuffers[3], mRenderPaint);

      canvas.drawLines(mUpperShadowBuffers, mRenderPaint);

      canvas.drawLines(mLowerShadowBuffers, mRenderPaint);

    }

  }

  @Override public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

    mRenderPaint.setColor(getHighlightColor());
    mRenderPaint.setStrokeWidth(2);
    mRenderPaint.setStyle(Paint.Style.STROKE);
    if (mDashedHighlightPhase > 0) {
      mRenderPaint.setPathEffect(new DashPathEffect(mDashedHighlightIntervals, mDashedHighlightPhase));
    }

    for (Highlight highlight : highlights) {

      for (CandlestickDataSet dataSet : getDataSet()) {
        if (dataSet.isHighlightedVerticalEnable()) {
          canvas.drawLine(highlight.getX(),
                          mContentRect.top,
                          highlight.getX(),
                          mContentRect.bottom,
                          mRenderPaint);
        }
        if (dataSet.isHighlightedHorizontalEnable()) {
          canvas.drawLine(mContentRect.left,
                          highlight.getY(),
                          mContentRect.right,
                          highlight.getY(),
                          mRenderPaint);
        }
      }


    }

    mRenderPaint.setPathEffect(null);
  }

  @Override public void addDataSet(CandlestickDataSet dataSet) {
    chartData.add(dataSet);
    calcDataSetMinMax();
  }

  @Override public void removeDataSet(CandlestickDataSet dataSet) {
    chartData.remove(dataSet);
    calcDataSetMinMax();
  }

  @Override public void clearDataSet() {
    chartData.clear();
    chartData.calcMaxMin(mViewport, mContentRect);
  }

  @Override public List<CandlestickDataSet> getDataSet() {
    return chartData.getDataSets();
  }

  @Override public ChartData<CandlestickDataSet> getChartData() {
    if (chartData == null)
      chartData = new CandlestickData();
    return chartData;
  }

  @Override public void calcDataSetMinMax() {
    getChartData().calcMaxMin(mViewport, mContentRect);
  }
}
