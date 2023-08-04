package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import java.util.List;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZCandlestickDraw extends BaseDraw<CandlestickValue, CandlestickDataSet> {

    private final float[] mUpperShadowBuffers = new float[4];

    private final float[] mLowerShadowBuffers = new float[4];

    private final float[] mBodyBuffers = new float[4];

    public JZCandlestickDraw(Chart chart) {
        super(chart);


        addOnTouchPointChangeListener();
    }

    @Override
    public void drawDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData, CandlestickDataSet dataSet) {
        if (dataSet.isVisible()) {
            dataSetList = chartData.getDataSets();
            drawCandlestickDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawCandlestickDataSet(Canvas canvas, final CandlestickDataSet dataSet, float lMax, float lMin, float rMax, float rMin) {

        float min, max;
        switch (dataSet.getAxisDependency()) {
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

        mRenderPaint.setStrokeWidth(dataSet.getStrokeThickness());
        mRenderPaint.setColor(dataSet.getColor());

        int valueCount = dataSet.getEntryCount();

        final List<CandlestickValue> visibleValues = dataSet.getVisiblePoints(mViewport);
        final float visibleRange = dataSet.getVisibleRange(mViewport);

        final double scale = 1.0 / mViewport.width();
        final double step = mContentRect.width() * scale / valueCount;
        final double startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;

        double candleWidth = dataSet.getCandleWidth();

        if (dataSet.isAutoWidth()) {
            candleWidth = mContentRect.width() / Math.max(visibleRange, dataSet.getMinValueCount());
        }

        float widthPercent = dataSet.getCandleWidthPercent();

        for (int i = 0; i < valueCount && i < dataSet.getValues().size(); i++) {

            CandlestickValue candlestick = dataSet.getEntryForIndex(i);

            if (!candlestick.isVisible()) continue;
            if (!visibleValues.contains(candlestick)) {
                continue;
            }

            double xPosition = startX + step * (i + dataSet.getStartIndexOffset());
            final double candlestickCenterX = xPosition + candleWidth * 0.5;

            if (dataSet.isEnableGap()) {
                mRenderPaint.setColor(dataSet.getGapColor());
                mRenderPaint.setStyle(Paint.Style.FILL);
                if (dataSet.getLowGaps().size() > 0) {
                    // 缺口
                    final Pair<Float, Float> gap = dataSet.getLowGaps().get(i, null);
                    if (gap != null) {
                        float y1 = (max - gap.first) / (max - min) * mContentRect.height();
                        float y2 = (max - gap.second) / (max - min) * mContentRect.height();
                        canvas.drawRect((float) xPosition,
                                y1,
                                mContentRect.right,
                                y2, mRenderPaint);
                    }
                }
                if (dataSet.getHighGaps().size() > 0) {
                    // 缺口
                    final Pair<Float, Float> gap = dataSet.getHighGaps().get(i, null);
                    if (gap != null) {
                        float y1 = (max - gap.first) / (max - min) * mContentRect.height();
                        float y2 = (max - gap.second) / (max - min) * mContentRect.height();
                        canvas.drawRect((float) candlestickCenterX,
                                y1,
                                mContentRect.right,
                                y2, mRenderPaint);
                    }
                }
            }

            if (candlestick.getFillBackgroundColor() != CandlestickValue.COLOR_NONE) { // 画背景
                mRenderPaint.setColor(candlestick.getFillBackgroundColor());
                mRenderPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect((float) xPosition, 0,
                        (float) (xPosition + candleWidth), canvas.getHeight(), mRenderPaint);
            }

            float highY = (max - candlestick.getHigh()) / (max - min) * mContentRect.height();
            float lowY = (max - candlestick.getLow()) / (max - min) * mContentRect.height();
            float openY = (max - candlestick.getOpen()) / (max - min) * mContentRect.height();
            float closeY = (max - candlestick.getClose()) / (max - min) * mContentRect.height();


            mBodyBuffers[0] = (float) (xPosition + (1 - widthPercent) * 0.5 * candleWidth);
            mBodyBuffers[1] = closeY;
            mBodyBuffers[2] = (float) (mBodyBuffers[0] + candleWidth * widthPercent);
            mBodyBuffers[3] = openY;

            mUpperShadowBuffers[0] = (float) candlestickCenterX;
            mUpperShadowBuffers[2] = (float) candlestickCenterX;
            mLowerShadowBuffers[0] = (float) candlestickCenterX;
            mLowerShadowBuffers[2] = (float) candlestickCenterX;

            candlestick.setCoordinate((float) candlestickCenterX, closeY);

            if (Float.compare(candlestick.getOpen(), candlestick.getClose()) > 0) { // 阴线

                mUpperShadowBuffers[1] = highY;
                mUpperShadowBuffers[3] = openY;
                mLowerShadowBuffers[1] = lowY;
                mLowerShadowBuffers[3] = closeY;

                if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                } else {
                    mRenderPaint.setColor(candlestick.getColor());
                }

                if (candlestick.getPaintStyle() != null) {
                    mRenderPaint.setStyle(candlestick.getPaintStyle());
                } else {
                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                }

            } else if (Float.compare(candlestick.getOpen(), candlestick.getClose()) < 0) { // 阳线

                mUpperShadowBuffers[1] = highY;
                mUpperShadowBuffers[3] = closeY;
                mLowerShadowBuffers[1] = lowY;
                mLowerShadowBuffers[3] = openY;

                if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getIncreasingColor());
                } else {
                    mRenderPaint.setColor(candlestick.getColor());
                }

                if (candlestick.getPaintStyle() != null) {
                    mRenderPaint.setStyle(candlestick.getPaintStyle());
                } else {
                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                }
            } else {

                mUpperShadowBuffers[1] = highY;
                mUpperShadowBuffers[3] = openY;
                mLowerShadowBuffers[1] = lowY;
                mLowerShadowBuffers[3] = mUpperShadowBuffers[3];

                if (candlestick.getColor() == CandlestickValue.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getNeutralColor());
                } else {
                    mRenderPaint.setColor(candlestick.getColor());
                }
            }

            if (i > 0) {
                final CandlestickValue previousValue = dataSet.getEntryForIndex(i - 1);
                boolean isLimitUp20 = (Float.compare(
                        candlestick.getClose(),
                        previousValue.getClose() * 1.2f - 0.01f) > 0) && candlestick.getClose() == candlestick.getHigh();
                boolean isLimitUp = (Float.compare(
                        candlestick.getClose(),
                        previousValue.getClose() * 1.1f - 0.01f) > 0) && candlestick.getClose() == candlestick.getHigh();
                if (dataSet.getLimitUpColor() != Color.TRANSPARENT) {
                    if (isLimitUp20) {
                        mRenderPaint.setColor(dataSet.getLimitUpColor20());
                    } else if (isLimitUp) {
                        mRenderPaint.setColor(dataSet.getLimitUpColor());
                    }
                }
                if (dataSet.getLimitUpPaintStyle() != null) {
                    if (isLimitUp || isLimitUp20) {
                        mRenderPaint.setStyle(dataSet.getLimitUpPaintStyle());
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

            if (dataSet instanceof CandlestickDataSetArrowDecorator) {
                int decimalDigitsNumber = mChart.getDecimalDigitsNumber();
                CandlestickDataSetArrowDecorator arrowDecorator = (CandlestickDataSetArrowDecorator) dataSet;
                arrowDecorator.draw(canvas, candlestick, mContentRect, (float) candleWidth, (float) xPosition, highY, lowY, decimalDigitsNumber);
            }
        }

        if (dataSet instanceof CandlestickDataSetArrowDecorator) {
            ((CandlestickDataSetArrowDecorator) dataSet).reset();
        }
    }

    @Override
    public List<CandlestickDataSet> getDataSets() {
        return super.getDataSets();
    }
}
