package cn.jingzhuan.lib.chart2.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import cn.jingzhuan.lib.chart.event.OnHighlightListener;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart2.renderer.AbstractDataRenderer;

import java.util.ArrayList;
import java.util.List;

import static cn.jingzhuan.lib.chart.animation.Easing.EasingFunction;

/**
 * Created by Donglua on 17/7/17.
 */

public class BaseChart extends Chart {

    protected AbstractDataRenderer mRenderer;
    private List<AxisRenderer> mAxisRenderers;

    private HighlightStatusChangeListener mHighlightStatusChangeListener;
    private OnHighlightListener mHighlightListener;

    private ChartAnimator mChartAnimator;

    public BaseChart(Context context) {
        super(context);
    }

    public BaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {

        mAxisRenderers = new ArrayList<>(4);

        mAxisRenderers.add(new AxisRenderer(this, mAxisTop));
        mAxisRenderers.add(new AxisRenderer(this, mAxisBottom));
        mAxisRenderers.add(new AxisRenderer(this, mAxisLeft));
        mAxisRenderers.add(new AxisRenderer(this, mAxisRight));

        mChartAnimator = new ChartAnimator(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
    }

    @Override
    public Paint getRenderPaint() {
        return mRenderer.getRenderPaint();
    }

    @Override
    public Canvas getBitmapCanvas() {
        return mBitmapCanvas;
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
    public void drawLabels(Canvas canvas) {
        for (AxisRenderer axisRenderer : mAxisRenderers) {
            axisRenderer.drawLabels(canvas);
        }
    }

    @Override
    public void onTouchPoint(MotionEvent e) {
        if (e.getPointerCount() == 1) {
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
    public void highlightValue(Highlight highlight) {

        if (highlight == null) return;

        final Highlight[] highlights = new Highlight[]{highlight};

        if (mHighlightStatusChangeListener != null) {
            mHighlightStatusChangeListener.onHighlightShow(highlights);
        }

        if (mHighlightListener != null) {
            mHighlightListener.highlight(highlights);
        }

        mHighlights = highlights;
        mIsHighlight = true;
        invalidate();
    }

    @Override
    public void cleanHighlight() {
        mHighlights = null;
        if (mHighlightStatusChangeListener != null)
            mHighlightStatusChangeListener.onHighlightHide();

        mFocusIndex = -1;
        mIsHighlight = false;
        invalidate();
    }

    @Override
    public boolean getIfKlineFullRect() {
        boolean isFullSupport = mRenderer.isFullSupport();
        return mRenderer.isFullSupport();
    }

    public void setRenderer(AbstractDataRenderer renderer) {
        this.mRenderer = renderer;
    }

    @Override
    public final void render(final Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
        }
        drawEdgeEffectsUnclipped(canvas);
        renderHighlighted(canvas);
    }

    public void renderHighlighted(Canvas canvas) {
        if (mRenderer != null && getHighlights() != null) {
            mRenderer.renderHighlighted(canvas, getHighlights());
        }
    }

    public void setHighlightColor(int color) {
        mRenderer.setHighlightColor(color);
    }

    public int getHighlightColor() {
        return mRenderer.getHighlightColor();
    }

    public Highlight[] getHighlights() {
        return mHighlights;
    }

    public void setHighlights(Highlight[] highlights) {

        this.mHighlights = highlights;
    }

    public void setOnHighlightStatusChangeListener(HighlightStatusChangeListener mHighlightStatusChangeListener) {
        this.mHighlightStatusChangeListener = mHighlightStatusChangeListener;
    }

    public HighlightStatusChangeListener getOnHighlightStatusChangeListener() {
        return mHighlightStatusChangeListener;
    }

    public void setOnHighlightListener(OnHighlightListener highlightListener) {
        this.mHighlightListener = highlightListener;
    }

    public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        this.mRenderer.enableHighlightDashPathEffect(intervals, phase);
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

    @Override
    public int getEntryIndexByCoordinate(float x, float y) {
        return mRenderer.getEntryIndexByCoordinate(x, y);
    }

    public void setTypeface(Typeface tf) {
        for (AxisRenderer mAxisRenderer : mAxisRenderers) {
            mAxisRenderer.setTypeface(tf);
        }
        postInvalidate();
    }

    public ChartAnimator getChartAnimator() {
        return mChartAnimator;
    }

    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX,
                          EasingFunction easingY) {
        mChartAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easing) {
        mChartAnimator.animateXY(durationMillisX, durationMillisY, easing);
    }

    public void animateX(int durationMillis, EasingFunction easing) {
        mChartAnimator.animateX(durationMillis, easing);
    }

    public void animateY(int durationMillis, EasingFunction easing) {
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
}

