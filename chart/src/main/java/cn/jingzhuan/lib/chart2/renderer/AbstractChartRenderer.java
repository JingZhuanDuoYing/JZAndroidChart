package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import java.util.List;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.renderer.Renderer;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.base.JZBaseChart;

/**
 * @author YL
 * @since 2023-08-04
 */
public abstract class AbstractChartRenderer<T extends AbstractDataSet> implements Renderer {

    protected Viewport mViewport;

    protected Rect mContentRect;

    protected Paint mRenderPaint;

    protected DashPathEffect mHighlightedDashPathEffect;

    private int mHighlightColor = Color.WHITE;

    protected ChartAnimator mChartAnimator;

    private float mHighlightThickness = 3;

    public AbstractChartRenderer(Chart chart) {
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();

        if (chart instanceof JZBaseChart) {
            this.mChartAnimator = ((JZBaseChart) chart).getChartAnimator();
            chart.setInternalViewportChangeListener(viewport -> {
                mViewport.set(viewport);
                calcDataSetMinMax();
            });
        }

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public final void renderer(Canvas canvas) {
        renderDataSet(canvas);
    }

    protected abstract void renderDataSet(Canvas canvas);

    public abstract void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights);

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
    }

    public void setTypeface(Typeface tf) {
    }

    public boolean isFullSupport() {
        return false;
    }
}
