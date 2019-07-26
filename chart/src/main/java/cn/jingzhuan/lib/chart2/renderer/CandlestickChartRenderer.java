package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.data.ChartData;
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
        mViewport.set(viewport);
        calcDataSetMinMax();
      }
    });

    final Highlight highlight = new Highlight();
    chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
      @Override
      public void touch(float x, float y) {
        if (chart.isHighlightDisable()) return;

        for (CandlestickDataSet dataSet : getDataSet()) {
          if (dataSet.isHighlightedVerticalEnable()) {
            final int valueCount = dataSet.getEntryCount();
            int index;
            float xPosition;
            float yPosition;
            highlight.setTouchX(x);
            highlight.setTouchY(y);
            if (x >= mContentRect.left) {
              index = getEntryIndexByCoordinate(x, y) - dataSet.getStartIndexOffset();
              if (index < valueCount && index >= 0) {
                final CandlestickValue candlestickValue = dataSet.getEntryForIndex(index);
                xPosition = candlestickValue.getX();
                yPosition = candlestickValue.getY();
                if (xPosition >= 0 && yPosition >= 0) {
                  highlight.setX(xPosition);
                  highlight.setY(yPosition);
                  highlight.setDataIndex(index);
                  chart.highlightValue(highlight);
                }
              }
            }
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

    final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);

    final float scale = 1 / mViewport.width();
    final float step = mContentRect.width() * scale / valueCount;
    final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;

    for (int i = 0; i < valueCount && i < candlestickDataSet.getValues().size(); i++) {

      final CandlestickValue candlestick = candlestickDataSet.getEntryForIndex(i);

      if (!visibleValues.contains(candlestick)) {
        continue;
      }

      float candleWidth = candlestickDataSet.getCandleWidth();

      if (candlestickDataSet.isAutoWidth()) {
        candleWidth = (mContentRect.width() + 0f) / Math.max(visibleValues.size(), candlestickDataSet.getMinValueCount());
      }

      float xPosition = startX + step * (i + candlestickDataSet.getStartIndexOffset());

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

      candlestick.setCoordinate(candlestickCenterX, closeY);

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

      if (i > 0) {
        final CandlestickValue previousValue = candlestickDataSet.getEntryForIndex(i - 1);
        boolean isLimitUp = Float.compare((candlestick.getClose() - previousValue.getClose()) / previousValue.getClose(), 0.095f) > 0;
        if (candlestickDataSet.getLimitUpColor() != Color.TRANSPARENT) {
          if (isLimitUp) {
            mRenderPaint.setColor(candlestickDataSet.getLimitUpColor());
          }
        }
        if (candlestickDataSet.getLimitUpPaintStyle() != null) {
          if (isLimitUp) {
            mRenderPaint.setStyle(candlestickDataSet.getLimitUpPaintStyle());
          }
        }
      }

      if (mBodyBuffers[1] == mBodyBuffers[3]) {
        canvas.drawLine(mBodyBuffers[0],
                        mBodyBuffers[1],
                        mBodyBuffers[2],
                        mBodyBuffers[3], mRenderPaint);
      } else {
        canvas.drawRect(mBodyBuffers[0],
                        mBodyBuffers[1],
                        mBodyBuffers[2],
                        mBodyBuffers[3], mRenderPaint);
      }

      canvas.drawLines(mUpperShadowBuffers, mRenderPaint);

      canvas.drawLines(mLowerShadowBuffers, mRenderPaint);

      if (candlestickDataSet instanceof CandlestickDataSetArrowDecorator) {
        ((CandlestickDataSetArrowDecorator) candlestickDataSet).draw(canvas, candlestick,
                                                                     mContentRect, candleWidth,
                                                                     xPosition,
                                                                     highY, lowY);
      }
    }

    if (candlestickDataSet instanceof CandlestickDataSetArrowDecorator) {
      ((CandlestickDataSetArrowDecorator) candlestickDataSet).reset();
    }
  }

  @Override public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

    mRenderPaint.setColor(getHighlightColor());
    mRenderPaint.setStrokeWidth(1);
    mRenderPaint.setStyle(Paint.Style.FILL);
    if (mHighlightedDashPathEffect != null) {
      mRenderPaint.setPathEffect(mHighlightedDashPathEffect);
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

  @Override public void removeDataSet(CandlestickDataSet dataSet) {
    getChartData().remove(dataSet);
    calcDataSetMinMax();
  }

  @Override public void clearDataSet() {
    getChartData().clear();
    getChartData().calcMaxMin(mViewport, mContentRect);
  }

  @Override public List<CandlestickDataSet> getDataSet() {
    return chartData.getDataSets();
  }

  @Override public ChartData<CandlestickDataSet> getChartData() {
    if (chartData == null)
      chartData = new CandlestickData();
    return chartData;
  }

}
