package cn.jingzhuan.lib.chart3.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart3.base.AbstractChartView;

/**
 * @since 2023-09-04
 * created by lei
 */
public abstract class AbstractRenderer<V extends Value, T extends AbstractDataSet<V>> {

    protected Viewport mViewport;

    protected Rect mContentRect;

    protected Paint mRenderPaint;

    protected Paint mLabelTextPaint;

    public AbstractRenderer(AbstractChartView<V, T> chart) {
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.STROKE);

        mLabelTextPaint = new Paint();
        mLabelTextPaint.setAntiAlias(true);
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

    public void setTypeface(Typeface tf) {
        mLabelTextPaint.setTypeface(tf);
    }

    public Paint getRenderPaint() {
        return mRenderPaint;
    }

    public ChartData<T> getChartData(){
        return new ChartData<>();
    }

    public abstract void renderer(Canvas canvas);
}
