package cn.jingzhuan.lib.chart2.renderer;

import static cn.jingzhuan.lib.chart.config.JZChartConfig.HIGHLIGHT_THICKNESS;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.base.AbstractChart;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.renderer.Renderer;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import java.util.List;

/**
 * Created by Donglua on 17/7/19.
 */

public abstract class AbstractDataRenderer<T extends AbstractDataSet<?>> implements Renderer {

    protected Viewport mViewport;

    protected Rect mContentRect;

    protected Paint mRenderPaint;

    protected DashPathEffect mHighlightedDashPathEffect;

    private int mHighlightColor = Color.WHITE;

    protected ChartAnimator mChartAnimator;

    private float mHighlightThickness = HIGHLIGHT_THICKNESS;

    public AbstractDataRenderer(AbstractChart chart) {
        this.mViewport = chart.getCurrentViewport();

        this.mContentRect = chart.getContentRect();

        this.mChartAnimator = chart.getChartAnimator();

        getChartData().setChart(chart);

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public final void renderer(Canvas canvas) {
        renderDataSet(canvas);
    }

    protected void renderDataSet(Canvas canvas) {
        renderDataSet(canvas, getChartData());
    }

    protected abstract void renderDataSet(Canvas canvas, ChartData<T> chartData);

    protected abstract void renderDataSet(Canvas canvas, ChartData<T> chartData, T dataSet);

    /**
     * 绘制十字光标
     */
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights){
        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setStrokeWidth(getHighlightThickness());
        mRenderPaint.setColor(getHighlightColor());

        if (mHighlightedDashPathEffect != null) {
            mRenderPaint.setPathEffect(mHighlightedDashPathEffect);
        }

        for (Highlight highlight : highlights) {

            for (T dataSet : getDataSet()) {
                if (dataSet.isHighlightedVerticalEnable()) {
                    float x = dataSet.getEntryForIndex(highlight.getDataIndex()).getX();
//                    if(dataSet instanceof CandlestickDataSet) {
//                        CandlestickDataSet candlestickDataSet = ((CandlestickDataSet) dataSet);
//                        float candleWidth = ((CandlestickDataSet) dataSet).getCandleWidth();
//                        if (candlestickDataSet.isAutoWidth()) {
//                            candleWidth = mContentRect.width() / Math.max(candlestickDataSet.getVisibleRange(mViewport), candlestickDataSet.getMinValueCount());
//                        }
//                        float leftSide = candleWidth * 0.5f;
//                        float rightSide = mContentRect.width() - leftSide;
//                        if(x <= leftSide) {
//                            x = leftSide;
//                        }
//                        if(x >= rightSide ) {
//                            x = rightSide;
//                        }
//                    }
                    canvas.drawLine(x,
                            mContentRect.top,
                            x,
                            mContentRect.bottom,
                            mRenderPaint);
                }
                if (dataSet.isHighlightedHorizontalEnable() && !Float.isNaN(highlight.getY())) {
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

    /**
     * Computes the pixel offset for the given X lib value. This may be outside the view bounds.
     */
    protected float getDrawX(float x) {
        return mContentRect.left + mContentRect.width() * (x - mViewport.left) / mViewport.width();
    }

    /**
     * Computes the pixel offset for the given Y lib value. This may be outside the view bounds.
     */
    protected float getDrawY(float y) {
        return mContentRect.bottom - mContentRect.height() * (y - mViewport.top) / mViewport.height();
    }

    public void addDataSet(T dataSet) {
        if (dataSet == null) return;
        getChartData().add(dataSet);
        calcDataSetMinMax();
    }

    public abstract void removeDataSet(T dataSet);
    public abstract void clearDataSet();

    protected abstract List<T> getDataSet();
    public abstract ChartData<T> getChartData();

    public int getEntryCounts() {
        return getChartData().getEntryCount();
    }

    public int getEntryIndexByCoordinate(float x, float y) {
        int counts = getEntryCounts();

        int index = (int) (((x - mContentRect.left) * mViewport.width() / mContentRect.width() + mViewport.left) * counts);
        if (index >= counts) index = counts - 1;
        if (index < 0) index = 0;

        return index;
    }

    public float getEntryCoordinateByIndex(int index) {
        int counts = getEntryCounts();

        float x = mContentRect.left + ((index / (float)counts - mViewport.left) / mViewport.width()) * mContentRect.width();

        if (x > mContentRect.right) x = mContentRect.right;
        if (x < mContentRect.left)  x = mContentRect.left;

        return x;
    }

    protected void calcDataSetMinMax() {
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    public void setHighlightColor(int highlightColor) {
        this.mHighlightColor = highlightColor;
    }

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        mHighlightedDashPathEffect = new DashPathEffect(intervals, phase);
    }

    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        if (maxVisibleEntryCount <= 0) return;
        getChartData().setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    public int getMaxVisibleEntryCount() {
        return getChartData().getMaxVisibleEntryCount();
    }

    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        if (minVisibleEntryCount <= 0) return;
        getChartData().setMinVisibleEntryCount(minVisibleEntryCount);
    }

    public int getMinVisibleEntryCount() {
        return getChartData().getMinVisibleEntryCount();
    }

    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        if (defaultVisibleEntryCount <= 0) return;
        getChartData().setDefaultVisibleEntryCount(defaultVisibleEntryCount);
    }

    public Paint getRenderPaint() {
        return mRenderPaint;
    }

    public ChartAnimator getChartAnimator() {
        return mChartAnimator;
    }

    public float getHighlightThickness() {
        return mHighlightThickness;
    }

    public void setHighlightThickness(float highlightThickness) {
        this.mHighlightThickness = highlightThickness;
    }

    public void setTypeface(Typeface tf) {
    }

    public int getVisibleCount(Viewport viewport) {
        if(getChartData().getDataSets() == null || getChartData().getDataSets().isEmpty()) return 0;
        T dataSet = getChartData().getDataSets().get(0);
        return dataSet.getVisiblePoints(viewport).size();
    }
}
