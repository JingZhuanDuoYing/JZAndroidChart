package cn.jingzhuan.lib.chart2.base;

import static cn.jingzhuan.lib.chart.animation.Easing.EasingFunction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import cn.jingzhuan.lib.chart.event.OnHighlightListener;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart2.renderer.AbstractChartRenderer;

/**
 * @author YL
 * @since 2023-08-04
 */

public class JZBaseChart extends JZChart {

    protected AbstractChartRenderer mRenderer;

    protected List<AxisRenderer> mAxisRenderers;

    private HighlightStatusChangeListener mHighlightStatusChangeListener;

    private OnHighlightListener mHighlightListener;

    private ChartAnimator mChartAnimator;

    private final Paint waterMarkPaint = new Paint();

    public JZBaseChart(Context context) {
        super(context);
    }

    public JZBaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JZBaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZBaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setRenderer(AbstractChartRenderer renderer) {
        this.mRenderer = renderer;
    }

    @Override
    public void initChart() {

        mAxisRenderers = new ArrayList<>(4);

        mAxisRenderers.add(new AxisRenderer(this, mAxisTop));
        mAxisRenderers.add(new AxisRenderer(this, mAxisBottom));
        mAxisRenderers.add(new AxisRenderer(this, mAxisLeft));
        mAxisRenderers.add(new AxisRenderer(this, mAxisRight));

        mChartAnimator = new ChartAnimator(animation -> postInvalidate());
        super.initChart();
    }

    @Override
    public final void render(final Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
            drawEdgeEffectsUnclipped(canvas);
            renderHighlighted(canvas);
        }
    }

    public void renderHighlighted(Canvas canvas) {
        if (getHighlights() != null) {
            mRenderer.renderHighlighted(canvas, getHighlights());
        }
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
    public void drawWaterMark(Canvas canvas) {
        if (isShowWaterMark()) {
            int padding = getResources().getDimensionPixelSize(R.dimen.jz_chart_water_mark_padding);
            Bitmap waterMarkBitmap = BitmapFactory.decodeResource(
                    this.getResources(), isNightMode() ? R.drawable.ico_water_mark_night : R.drawable.ico_water_mark);
            int left = getWidth() - padding - waterMarkBitmap.getWidth() - getPaddingRight();
            canvas.drawBitmap(waterMarkBitmap, (float) left, (float) padding, waterMarkPaint);
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

    public void setTypeface(Typeface tf) {
        for (AxisRenderer mAxisRenderer : mAxisRenderers) {
            mAxisRenderer.setTypeface(tf);
        }
        postInvalidate();
    }

    public ChartAnimator getChartAnimator() {
        return mChartAnimator;
    }

    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX, EasingFunction easingY) {
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

    @Override
    public Paint getRenderPaint() {
        return mRenderer.getRenderPaint();
    }

    @Override
    public int getEntryIndexByCoordinate(float x, float y) {
        return mRenderer.getEntryIndexByCoordinate(x, y);
    }

    @Override
    public Canvas getBitmapCanvas() {
        return mBitmapCanvas;
    }
}

