package cn.jingzhuan.lib.chart.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.animation.Easing;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.OnTouchHighlightChangeListener;
import cn.jingzhuan.lib.chart.event.OnTouchPointChangeListener;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart2.renderer.AbstractDataRenderer;

/**
 * @since 2023-03-24
 */
public class JZChart extends AbstractChart {

    protected AbstractDataRenderer mRenderer;

    private List<AxisRenderer> mAxisRenderers;

    private ChartAnimator mChartAnimator;

    protected Highlight[] mHighlights;

    protected int mHighlightIndex = -1;

    public JZChart(Context context) {
        super(context);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        mAxisRenderers = new ArrayList<>(4);

        mAxisRenderers.add(new AxisRenderer(this, mAxisTop));
        mAxisRenderers.add(new AxisRenderer(this, mAxisBottom));
        mAxisRenderers.add(new AxisRenderer(this, mAxisLeft));
        mAxisRenderers.add(new AxisRenderer(this, mAxisRight));

        mChartAnimator = new ChartAnimator(animation -> postInvalidate());
    }

    @Override
    public Paint getRenderPaint() {
        return mRenderer.getRenderPaint();
    }

    @Override
    public void drawAxis(Canvas canvas) {
        for (AxisRenderer axisRenderer : mAxisRenderers) {
            axisRenderer.renderer(canvas);
        }
    }

    @Override
    public void drawGridLine(Canvas canvas) {
        for (AxisRenderer axisRenderer : mAxisRenderers) {
            axisRenderer.drawGridLines(canvas);
        }
    }

    @Override
    public void drawWatermark(Canvas canvas) {

    }

    @Override
    public void drawLabels(Canvas canvas) {
        for (AxisRenderer axisRenderer : mAxisRenderers) {
            axisRenderer.drawLabels(canvas);
        }
    }

    @Override
    public void render(Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
        }
        drawEdgeEffectsUnClipped(canvas);
        renderHighlighted(canvas);
    }

    public void renderHighlighted(Canvas canvas) {
        if (mRenderer != null && getHighlights() != null) {
            mRenderer.renderHighlighted(canvas, getHighlights());
        }
    }

    @Override
    public Highlight[] getHighlights() {
        return mHighlights;
    }

    public void setHighlights(Highlight[] highlights) {
        this.mHighlights = highlights;
    }

    @Override
    public void highlightValue(Highlight highlight) {
        if (highlight == null) return;

        boolean highlightChange = mHighlightIndex != highlight.getDataIndex() || mHighlightY != highlight.getY();

        if (highlightChange) {
            Log.w("highlightChange", mHighlightIndex + "==" + highlight.getDataIndex() + "---" + mHighlightX + "==" + highlight.getX()+ "---" + mHighlightY + "==" + highlight.getY());
            mHighlightIndex = highlight.getDataIndex();
            mHighlightX = highlight.getX();
            mHighlightY = highlight.getY();
        }

        final Highlight[] highlights = new Highlight[] { highlight };

        if (mHighlightStatusChangeListener != null && highlightChange) {
            mHighlightStatusChangeListener.onHighlightShow(highlights);
        }

        if (mHighlightListener != null && highlightChange) {
            mHighlightListener.highlight(highlights);
        }

        mHighlights = highlights;
        mIsHighlight = true;
        invalidate();

    }

    @Override
    public void onAlwaysHighlight() {
        if (mHighlights == null) return;
        Highlight highlight = mHighlights[0];
        if(highlight == null) return;

        int dataIndex = highlight.getDataIndex();

        float highlightX = getEntryCoordinateByIndex(dataIndex);

        float width = getContentRect().width();

        int visibleSize = getVisibleCount(mCurrentViewport);
        float pointWidth = (float) mContentRect.width() / (float) visibleSize;
        float halfPointWidth = pointWidth * 0.5f;

        if(highlightX <= 0) {
            dataIndex = getEntryIndexByCoordinate(halfPointWidth, 0f);
        }

        if(highlightX >= width - halfPointWidth) {
            dataIndex = getEntryIndexByCoordinate(width - halfPointWidth, 0f);
        }

        float x = Math.max(halfPointWidth, Math.min(width - halfPointWidth, highlightX + halfPointWidth));

        highlight.setX(x);
        highlight.setY(mHighlightY);
        highlight.setDataIndex(dataIndex);
        Log.w("onAlwaysHighlight", x + "-" + mHighlightY + "-" + dataIndex);
        highlightValue(highlight);
    }

    /**
     * 清掉十字光标
     */
    @Override
    public void cleanHighlight() {
        mHighlights = null;
        if (mHighlightStatusChangeListener != null)
            mHighlightStatusChangeListener.onHighlightHide();

        mFocusIndex = -1;
        mHighlightIndex = -1;
        mHighlightX = Float.NaN;
        mHighlightY = Float.NaN;
        mIsHighlight = false;
        invalidate();
    }

    @Override
    public void onTouchPoint(MotionEvent e) {
        if (e.getPointerCount() == 1) {
            Log.w("onTouchPoint", e.getX()+ "-" + e.getY());
            for (OnTouchPointChangeListener touchPointChangeListener : mTouchPointChangeListeners) {
                touchPointChangeListener.touch(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void onTouchHighlight(MotionEvent e) {
        if (e.getPointerCount() == 1) {
            for (OnTouchHighlightChangeListener touchHighlightChangeListener : mTouchHighlightChangeListeners) {
                touchHighlightChangeListener.highlight(e.getX(), e.getY());
            }
        }
    }

    @Override
    public int getEntryIndexByCoordinate(float x, float y) {
        return mRenderer.getEntryIndexByCoordinate(x, y);
    }

    @Override
    public float getEntryCoordinateByIndex(int index) {
        return mRenderer.getEntryCoordinateByIndex(index);
    }

    @Override
    public ChartAnimator getChartAnimator() {
        return mChartAnimator;
    }

    public void setRenderer(AbstractDataRenderer renderer) {
        this.mRenderer = renderer;
    }

    public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        this.mRenderer.enableHighlightDashPathEffect(intervals, phase);
    }

    public void setHighlightColor(int color) {
        mRenderer.setHighlightColor(color);
    }

    public int getHighlightColor() {
        return mRenderer.getHighlightColor();
    }

    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        mRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
    }

    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        mRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        mRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
    }

    public void setTypeface(Typeface tf) {
        for (AxisRenderer mAxisRenderer : mAxisRenderers) {
            mAxisRenderer.setTypeface(tf);
        }
        postInvalidate();
    }

    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingFunction easingX,
                          Easing.EasingFunction easingY) {
        mChartAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingFunction easing) {
        mChartAnimator.animateXY(durationMillisX, durationMillisY, easing);
    }

    public void animateX(int durationMillis, Easing.EasingFunction easing) {
        mChartAnimator.animateX(durationMillis, easing);
    }

    public void animateY(int durationMillis, Easing.EasingFunction easing) {
        mChartAnimator.animateY(durationMillis, easing);
    }

    public void animateX(int durationMillis) {
        mChartAnimator.animateX(durationMillis);
    }

    public void animateY(int durationMillis) {
        mChartAnimator.animateY(durationMillis);
    }

    public void animateXY(int durationMillisX, int durationMillisY) {
        mChartAnimator.animateXY(durationMillisX, durationMillisY);
    }

    public int getFocusIndex() {
        return mFocusIndex;
    }

    public void setFocusIndex(int focusIndex) {
        this.mFocusIndex = focusIndex;
    }


    @Override
    public int getVisibleCount(Viewport viewport) {
        return mRenderer.getVisibleCount(viewport);
    }

    @Override
    public int getMaxVisibleEntryCount() {
        return mRenderer.getMaxVisibleEntryCount();
    }

    @Override
    public int getMinVisibleEntryCount() {
        return mRenderer.getMinVisibleEntryCount();
    }
}
