package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import java.lang.ref.WeakReference;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;

/**
 * Created by Donglua on 17/7/19.
 */

public abstract class AbstractDataRenderer<T extends AbstractDataSet> implements Renderer {

    protected Viewport mViewport;
    protected Rect mContentRect;
    protected Paint mRenderPaint;

    protected float mDashedHighlightIntervals[] = null;
    protected float mDashedHighlightPhase = -1;

    protected int maxVisibleEntryCount = 500;
    protected int minVisibleEntryCount = 20;
    protected int defaultVisibleEntryCount = -1;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;
    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    private int mHighlightColor = Color.WHITE;

    public AbstractDataRenderer(Chart chart) {
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();

        getChartData().setChart(chart);

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public final void renderer(Canvas canvas) {
        int width = mContentRect.width() + mContentRect.left;
        int height = mContentRect.height();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        renderDataSet(mBitmapCanvas);

        canvas.drawBitmap(mDrawBitmap.get(), 0, 0, mRenderPaint);
    }


    protected void renderDataSet(Canvas canvas) {
        renderDataSet(canvas, getChartData());
    }

    protected abstract void renderDataSet(Canvas canvas, ChartData<T> chartData);

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
        dataSet.setMinVisibleEntryCount(minVisibleEntryCount);
        dataSet.setMaxVisibleEntryCount(maxVisibleEntryCount);
        dataSet.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        getChartData().add(dataSet);
        calcDataSetMinMax();
    }

    public abstract void removeDataSet(T dataSet);
    public abstract void clearDataSet();

    protected abstract List<T> getDataSet();
    public abstract ChartData<T> getChartData();

    protected void calcDataSetMinMax() {
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    public void setHighlightColor(int highlightColor) {
        this.mHighlightColor = highlightColor;
    }

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public void enableDashPathEffect(float intervals[], float phase) {
        this.mDashedHighlightIntervals = intervals;
        this.mDashedHighlightPhase = phase;
    }

    public Canvas getCacheCanvas() {
        return mBitmapCanvas;
    }

    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        this.maxVisibleEntryCount = maxVisibleEntryCount;
        if (maxVisibleEntryCount <= 0) return;

        synchronized (getDataSet()) {
            for (T t : getDataSet()) {
                t.setMaxVisibleEntryCount(maxVisibleEntryCount);
            }
        }
    }

    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        this.minVisibleEntryCount = minVisibleEntryCount;
        if (minVisibleEntryCount <= 0) return;

        synchronized (getDataSet()) {
            for (T t : getDataSet()) {
                t.setMinVisibleEntryCount(minVisibleEntryCount);
            }
        }
    }

    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        this.defaultVisibleEntryCount = defaultVisibleEntryCount;
        if (defaultVisibleEntryCount <= 0) return;

        synchronized (getDataSet()) {
            for (T t : getDataSet()) {
                t.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
            }
        }
    }

    public int getMinVisibleEntryCount() {
        return minVisibleEntryCount;
    }

    public int getMaxVisibleEntryCount() {
        return maxVisibleEntryCount;
    }

    public int getDefaultVisibleEntryCount() {
        return defaultVisibleEntryCount;
    }
}
