package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.value.IDataSet;

/**
 * Created by Donglua on 17/7/19.
 */

public abstract class AbstractDataRenderer<T extends IDataSet> implements Renderer {

    protected Viewport mViewport;
    protected Rect mContentRect;
    protected Paint mRenderPaint;

    public AbstractDataRenderer(Chart chart) {
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);
    }

    public abstract void renderHighlighted(Canvas canvas, Highlight[] highlights);

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

    public abstract void addDataSet(T dataSet);

    public abstract List<T> getDataSet();

}
