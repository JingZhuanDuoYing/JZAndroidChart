//package cn.jingzhuan.lib.chart2.renderer;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//
//import java.util.List;
//
//import cn.jingzhuan.lib.chart.R;
//import cn.jingzhuan.lib.chart.Viewport;
//import cn.jingzhuan.lib.chart.component.AxisY;
//import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
//import cn.jingzhuan.lib.chart.data.CandlestickValue;
//import cn.jingzhuan.lib.chart.data.ChartData;
//import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator;
//import cn.jingzhuan.lib.chart2.base.Chart;
//
//public class AIKLineCandlestickChartRenderer extends CandlestickChartRenderer {
//
//  private final AIKLineDrawSelectRangeHelper aikLineDrawSelectRangeHelper;
//  private float[] mUpperShadowBuffers = new float[4];
//  private float[] mLowerShadowBuffers = new float[4];
//  private float[] mBodyBuffers = new float[4];
//
//  public AIKLineCandlestickChartRenderer(final Chart chart) {
//    super(chart);
//    aikLineDrawSelectRangeHelper = new AIKLineDrawSelectRangeHelper(this, chart);
//  }
//
//  @Override protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData) {
//    super.renderDataSet(canvas,chartData);
//    aikLineDrawSelectRangeHelper.drawCanvas(canvas);
//  }
//
//  @Override
//  protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData, CandlestickDataSet dataSet) {
//    super.renderDataSet(canvas, chartData, dataSet);
//    aikLineDrawSelectRangeHelper.drawCanvas(canvas);
//  }
//
//  Viewport getViewPort() {
//    return mViewport;
//  }
//
//  int getItemWidth() {
//    if (getDataSet() == null || getDataSet().size() == 0) {
//      return 0;
//    }
//    CandlestickDataSet candlestickDataSet = getDataSet().get(0);
//    float candleWidth = candlestickDataSet.getCandleWidth();
//    final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);
//    if (candlestickDataSet.isAutoWidth()) {
//      candleWidth = (mContentRect.width() + 0f) / Math.max(visibleValues.size(),
//          candlestickDataSet.getMinValueCount());
//    }
//
//   return (int) candleWidth;
//  }
//
//  private void drawDataSet(Canvas canvas, CandlestickDataSet candlestickDataSet,
//      float lMax, float lMin, float rMax, float rMin) {
//    float min, max;
//    switch (candlestickDataSet.getAxisDependency()) {
//      case AxisY.DEPENDENCY_RIGHT:
//        min = rMin;
//        max = rMax;
//        break;
//      case AxisY.DEPENDENCY_BOTH:
//      case AxisY.DEPENDENCY_LEFT:
//      default:
//        min = lMin;
//        max = lMax;
//        break;
//    }
//    mRenderPaint.setStrokeWidth(candlestickDataSet.getStrokeThickness());
//    mRenderPaint.setColor(candlestickDataSet.getColor());
//
//    int valueCount = candlestickDataSet.getEntryCount();
//
//    final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);
//
//    final float scale = 1 / mViewport.width();
//    final float step = mContentRect.width() * scale / valueCount;
//    final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;
//
//    for (int i = 0; i < valueCount && i < candlestickDataSet.getValues().size(); i++) {
//
//      final CandlestickValue candlestick = candlestickDataSet.getEntryForIndex(i);
//
//      if (!visibleValues.contains(candlestick)) {
//        continue;
//      }
//
//      float candleWidth = candlestickDataSet.getCandleWidth();
//
//      if (candlestickDataSet.isAutoWidth()) {
//        candleWidth = (mContentRect.width() + 0f) / Math.max(visibleValues.size(),
//            candlestickDataSet.getMinValueCount());
//      }
//
//      float xPosition = startX + step * (i + candlestickDataSet.getStartIndexOffset());
//
//      float highY = (max - candlestick.getHigh()) / (max - min) * mContentRect.height();
//      float lowY = (max - candlestick.getLow()) / (max - min) * mContentRect.height();
//      float openY = (max - candlestick.getOpen()) / (max - min) * mContentRect.height();
//      float closeY = (max - candlestick.getClose()) / (max - min) * mContentRect.height();
//
//      float widthPercent = 0.8f;
//
//      mBodyBuffers[0] = xPosition + (1 - widthPercent) * 0.5f * candleWidth;
//      mBodyBuffers[1] = closeY;
//      mBodyBuffers[2] = mBodyBuffers[0] + candleWidth * widthPercent;
//      mBodyBuffers[3] = openY;
//
//      final float candlestickCenterX = xPosition + candleWidth * 0.5f;
//
//      mUpperShadowBuffers[0] = candlestickCenterX;
//      mUpperShadowBuffers[2] = candlestickCenterX;
//      mLowerShadowBuffers[0] = candlestickCenterX;
//      mLowerShadowBuffers[2] = candlestickCenterX;
//
//      candlestick.setCoordinate(candlestickCenterX, closeY);
//
//      if (Float.compare(candlestick.getOpen(), candlestick.getClose()) > 0) { // 阴线
//
//        mUpperShadowBuffers[1] = highY;
//        mUpperShadowBuffers[3] = openY;
//        mLowerShadowBuffers[1] = lowY;
//        mLowerShadowBuffers[3] = closeY;
//
//        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
//          mRenderPaint.setColor(candlestickDataSet.getDecreasingColor());
//        } else {
//          mRenderPaint.setColor(candlestick.getColor());
//        }
//
//        if (candlestick.getPaintStyle() != null) {
//          mRenderPaint.setStyle(candlestick.getPaintStyle());
//        } else {
//          mRenderPaint.setStyle(candlestickDataSet.getDecreasingPaintStyle());
//        }
//      } else if (Float.compare(candlestick.getOpen(), candlestick.getClose()) < 0) { // 阳线
//
//        mUpperShadowBuffers[1] = highY;
//        mUpperShadowBuffers[3] = closeY;
//        mLowerShadowBuffers[1] = lowY;
//        mLowerShadowBuffers[3] = openY;
//
//        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
//          mRenderPaint.setColor(candlestickDataSet.getIncreasingColor());
//        } else {
//          mRenderPaint.setColor(candlestick.getColor());
//        }
//
//        if (candlestick.getPaintStyle() != null) {
//          mRenderPaint.setStyle(candlestick.getPaintStyle());
//        } else {
//          mRenderPaint.setStyle(candlestickDataSet.getIncreasingPaintStyle());
//        }
//      } else {
//
//        mUpperShadowBuffers[1] = highY;
//        mUpperShadowBuffers[3] = openY;
//        mLowerShadowBuffers[1] = lowY;
//        mLowerShadowBuffers[3] = mUpperShadowBuffers[3];
//
//        if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
//          mRenderPaint.setColor(candlestickDataSet.getNeutralColor());
//        } else {
//          mRenderPaint.setColor(candlestick.getColor());
//        }
//      }
//
//      if (i > 0) {
//        final CandlestickValue previousValue = candlestickDataSet.getEntryForIndex(i - 1);
//        boolean isLimitUp = (Float.compare(
//                candlestick.getClose(),
//                previousValue.getClose() * 1.1f - 0.01f) > 0) && candlestick.getClose() == candlestick.getHigh();
//        if (candlestickDataSet.getLimitUpColor() != Color.TRANSPARENT) {
//          if (isLimitUp) {
//            mRenderPaint.setColor(candlestickDataSet.getLimitUpColor());
//          }
//        }
//        if (candlestickDataSet.getLimitUpPaintStyle() != null) {
//          if (isLimitUp) {
//            mRenderPaint.setStyle(candlestickDataSet.getLimitUpPaintStyle());
//          }
//        }
//      }
//      mRenderPaint.setColor(Color.parseColor("#ffffff"));
//      if (mBodyBuffers[1] == mBodyBuffers[3]) {
//        canvas.drawLine(mBodyBuffers[0],
//            mBodyBuffers[1],
//            mBodyBuffers[2],
//            mBodyBuffers[3], mRenderPaint);
//      } else {
//        canvas.drawRect(mBodyBuffers[0],
//            mBodyBuffers[1],
//            mBodyBuffers[2],
//            mBodyBuffers[3], mRenderPaint);
//      }
//      canvas.drawLines(mUpperShadowBuffers, mRenderPaint);
//
//      canvas.drawLines(mLowerShadowBuffers, mRenderPaint);
//
//      if (candlestickDataSet instanceof CandlestickDataSetArrowDecorator) {
//        ((CandlestickDataSetArrowDecorator) candlestickDataSet).draw(canvas, candlestick,
//            mContentRect, candleWidth,
//            xPosition,
//            highY, lowY);
//      }
//    }
//
//    if (candlestickDataSet instanceof CandlestickDataSetArrowDecorator) {
//      ((CandlestickDataSetArrowDecorator) candlestickDataSet).reset();
//    }
//  }
//
//  public AIKLineDrawSelectRangeHelper getDrawSelectRangeHelper() {
//    return aikLineDrawSelectRangeHelper;
//  }
//  /**
//   * 更新左右两侧选中想要的K线
//   */
//  public void updateAIKLineSelectRange(long startTime, long endTime) {
//    aikLineDrawSelectRangeHelper.updateAIKLineSelectRange(startTime,endTime);
//  }
//}
