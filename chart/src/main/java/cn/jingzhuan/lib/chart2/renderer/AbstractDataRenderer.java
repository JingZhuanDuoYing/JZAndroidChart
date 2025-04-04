package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.renderer.Renderer;
import cn.jingzhuan.lib.chart2.base.BaseChart;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import java.util.List;

/**
 * Created by Donglua on 17/7/19.
 */

public abstract class AbstractDataRenderer<T extends AbstractDataSet> implements Renderer {

    protected Viewport mViewport;
    protected Rect mContentRect;
    protected Paint mRenderPaint;

    protected DashPathEffect mHighlightedDashPathEffect;

    private int mHighlightColor = Color.WHITE;
    protected ChartAnimator mChartAnimator;
    private float mHighlightThickness = 3;

    protected Paint mHighlightLinePaint;

    public AbstractDataRenderer(Chart chart) {
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();

        if (chart instanceof BaseChart) {
            this.mChartAnimator = ((BaseChart) chart).getChartAnimator();
        }

        getChartData().setChart(chart);

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);

        mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightLinePaint.setStyle(Paint.Style.FILL);
        mHighlightLinePaint.setStrokeWidth(mHighlightThickness);
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

    public abstract void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights);

    /**
     * Computes the pixel offset for the given X lib value. This may be outside the view bounds.
     */
    protected float getDrawX(float x) {
        return mContentRect.left
                + mContentRect.width() * (x - mViewport.left) / mViewport.width();
    }

    /**
     * Computes the pixel offset for the given Y lib value. This may be outside the view bounds.
     */
    protected float getDrawY(float y) {
        return mContentRect.bottom
                - mContentRect.height() * (y - mViewport.top) / mViewport.height();
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

    public int getEntryIndexByCoordinate(float x, float y) {
        float valueCount = getChartData().getEntryCount();

        int index =
            (int) (((x - mContentRect.left) * mViewport.width() / mContentRect.width() + mViewport.left) * valueCount);
        if (index >= getChartData().getEntryCount()) index = getChartData().getEntryCount() - 1;
        if (index < 0) index = 0;

        return index;
    }

    public float getEntryCoordinateByIndex(int index) {
        float valueCount = getChartData().getEntryCount();

        float x = mContentRect.left + ((index / valueCount - mViewport.left) / mViewport.width()) * mContentRect.width();

        if (x > mContentRect.right) x = mContentRect.right;
        if (x < mContentRect.left)  x = mContentRect.left;

        return x;
    }

    protected void calcDataSetMinMax() {
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    public void setHighlightColor(int highlightColor) {
        this.mHighlightColor = highlightColor;
        mHighlightLinePaint.setColor(mHighlightColor);
    }

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        mHighlightedDashPathEffect = new DashPathEffect(intervals, phase);
        mHighlightLinePaint.setPathEffect(mHighlightedDashPathEffect);
    }

    public DashPathEffect getHighlightDashPathEffect() {
        return this.mHighlightedDashPathEffect;
    }

    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        if (maxVisibleEntryCount <= 0) return;
        getChartData().setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        if (minVisibleEntryCount <= 0) return;
        getChartData().setMinVisibleEntryCount(minVisibleEntryCount);
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
        mHighlightLinePaint.setStrokeWidth(mHighlightThickness);
    }

    public Paint getHighlightLinePaint() {
        return mHighlightLinePaint;
    }

    public void setTypeface(Typeface tf) {
    }
}
