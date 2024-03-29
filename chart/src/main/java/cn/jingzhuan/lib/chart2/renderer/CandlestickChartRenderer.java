package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import android.util.Pair;

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
 * Created by donglua on 8/29/17.
 */

public class CandlestickChartRenderer extends AbstractDataRenderer<CandlestickDataSet> {

    private final float[] mUpperShadowBuffers = new float[4];
    private final float[] mLowerShadowBuffers = new float[4];
    private final float[] mBodyBuffers = new float[4];
    private CandlestickData chartData;

    protected Paint mHighlightRenderPaint;

    private final Chart chart;

    public CandlestickChartRenderer(final Chart chart) {
        super(chart);
        this.chart = chart;
        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });

        final Highlight highlight = new Highlight();
        chart.addOnTouchPointChangeListener((x, y) -> {
            if (chart.isHighlightDisable()) return;

            for (CandlestickDataSet dataSet : getDataSet()) {
                if (dataSet.isHighlightedVerticalEnable()) {
                    final int valueCount = dataSet.getEntryCount();
                    float xPosition;
                    float yPosition;
                    highlight.setTouchX(x);
                    highlight.setTouchY(y);
                    int index = getEntryIndexByCoordinate(x, y) - dataSet.getStartIndexOffset();
                    if (index < valueCount && index >= 0 && index < dataSet.getValues().size()) {
                        final CandlestickValue candlestickValue = dataSet.getEntryForIndex(index);
                        xPosition = Math.max(mContentRect.left, candlestickValue.getX());
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
        });

        mHighlightRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightRenderPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData) {
        for (CandlestickDataSet dataSet : chartData.getDataSets()) {
            renderDataSet(canvas, chartData, dataSet);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData, CandlestickDataSet dataSet) {
        if (dataSet.isVisible()) {
            drawDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
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
        final float visibleRange = candlestickDataSet.getVisibleRange(mViewport);

        final double scale = 1.0 / mViewport.width();
        final double step = mContentRect.width() * scale / valueCount;
        final double startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;

        double candleWidth = candlestickDataSet.getCandleWidth();

        if (candlestickDataSet.isAutoWidth()) {
            candleWidth = mContentRect.width() / Math.max(visibleRange, candlestickDataSet.getMinValueCount());
        }

        float widthPercent = candlestickDataSet.getCandleWidthPercent();

        for (int i = 0; i < valueCount && i < candlestickDataSet.getValues().size(); i++) {

            CandlestickValue candlestick = candlestickDataSet.getEntryForIndex(i);

            if (!candlestick.isVisible()) continue;
            if (!visibleValues.contains(candlestick)) {
                continue;
            }

            double xPosition = startX + step * (i + candlestickDataSet.getStartIndexOffset());
            final double candlestickCenterX = xPosition + candleWidth * 0.5;

            if (candlestickDataSet.isEnableGap()) {
                mRenderPaint.setColor(candlestickDataSet.getGapColor());
                mRenderPaint.setStyle(Paint.Style.FILL);
                if (candlestickDataSet.getLowGaps().size() > 0) {
                    // 缺口
                    final Pair<Float, Float> gap = candlestickDataSet.getLowGaps().get(i, null);
                    if (gap != null) {
                        float y1 = (max - gap.first) / (max - min) * mContentRect.height();
                        float y2 = (max - gap.second) / (max - min) * mContentRect.height();
                        canvas.drawRect((float) xPosition,
                                y1,
                                mContentRect.right,
                                y2, mRenderPaint);
                    }
                }
                if (candlestickDataSet.getHighGaps().size() > 0) {
                    // 缺口
                    final Pair<Float, Float> gap = candlestickDataSet.getHighGaps().get(i, null);
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
                boolean isLimitUp20 = (Float.compare(
                        candlestick.getClose(),
                        previousValue.getClose() * 1.2f - 0.01f) > 0) && candlestick.getClose() == candlestick.getHigh();
                boolean isLimitUp = (Float.compare(
                        candlestick.getClose(),
                        previousValue.getClose() * 1.1f - 0.01f) > 0) && candlestick.getClose() == candlestick.getHigh();
                if (candlestickDataSet.getLimitUpColor() != Color.TRANSPARENT) {
                    if (isLimitUp20) {
                        mRenderPaint.setColor(candlestickDataSet.getLimitUpColor20());
                    } else if (isLimitUp) {
                        mRenderPaint.setColor(candlestickDataSet.getLimitUpColor());
                    }
                }
                if (candlestickDataSet.getLimitUpPaintStyle() != null) {
                    if (isLimitUp || isLimitUp20) {
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
                int decimalDigitsNumber = chart.getDecimalDigitsNumber();
                CandlestickDataSetArrowDecorator arrowDecorator = (CandlestickDataSetArrowDecorator) candlestickDataSet;
                arrowDecorator.draw(canvas, candlestick, mContentRect, (float) candleWidth, (float) xPosition, highY, lowY, decimalDigitsNumber);
            }
        }

        if (candlestickDataSet instanceof CandlestickDataSetArrowDecorator) {
            ((CandlestickDataSetArrowDecorator) candlestickDataSet).reset();
        }
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mHighlightRenderPaint.setColor(getHighlightColor());
        if (mHighlightedDashPathEffect != null) {
            mHighlightRenderPaint.setPathEffect(mHighlightedDashPathEffect);
        }
        mHighlightRenderPaint.setStrokeWidth(getHighlightThickness());
        mHighlightRenderPaint.setStyle(Paint.Style.FILL);

        for (Highlight highlight : highlights) {

            for (CandlestickDataSet dataSet : getDataSet()) {
                if (dataSet.isHighlightedVerticalEnable() && !chart.isEnableVerticalHighlight()) {
                    canvas.drawLine(highlight.getX(),
                            mContentRect.top,
                            highlight.getX(),
                            mContentRect.bottom,
                            mHighlightRenderPaint);
                }
                if (dataSet.isHighlightedHorizontalEnable() && !chart.isEnableHorizontalHighlight()) {
                    float y = highlight.getY();
                    if (y < mContentRect.top + getHighlightThickness() * 0.5f) {
                        y = mContentRect.top + getHighlightThickness() * 0.5f;
                    }

                    if (y > mContentRect.bottom - getHighlightThickness() * 0.5f){
                        y = mContentRect.bottom - getHighlightThickness() * 0.5f;
                    }
                    canvas.drawLine(mContentRect.left,
                            y,
                            mContentRect.right,
                            y,
                            mHighlightRenderPaint);
                }
            }
        }

        mHighlightRenderPaint.setPathEffect(null);
    }

    @Override
    public void removeDataSet(CandlestickDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    @Override
    public List<CandlestickDataSet> getDataSet() {
        return chartData.getDataSets();
    }

    @Override
    public ChartData<CandlestickDataSet> getChartData() {
        if (chartData == null)
            chartData = new CandlestickData();
        return chartData;
    }

}
