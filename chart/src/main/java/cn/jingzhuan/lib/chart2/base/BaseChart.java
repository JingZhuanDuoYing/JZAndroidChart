package cn.jingzhuan.lib.chart2.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
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
    protected List<AxisRenderer> mAxisRenderers;

    private HighlightStatusChangeListener mHighlightStatusChangeListener;
    private OnHighlightListener mHighlightListener;

    private ChartAnimator mChartAnimator;

    private final Paint waterMarkPaint = new Paint();

    protected Paint mHighlightTextPaint;

    protected Paint mHighlightBgPaint;

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
        initHighlightPaint();
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

    protected void initHighlightPaint() {

        mHighlightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightTextPaint.setStyle(Paint.Style.FILL);
        mHighlightTextPaint.setTextSize(getHighlightTextSize());
        mHighlightTextPaint.setColor(getHighlightTextColor());
        mHighlightTextPaint.setTextAlign(Paint.Align.CENTER);

        mHighlightBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightBgPaint.setStyle(Paint.Style.FILL);
        mHighlightBgPaint.setColor(getHighlightTextBgColor());
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

    @Override
    public void drawHighlightLeft(Canvas canvas) {
        if (isEnableHorizontalHighlight() && isEnableHighlightLeftText()) {
            if (getHighlights() == null) return;
            Highlight highlight = getHighlights()[0];
            int textHeight = getHighlightTextBgHeight();
            int top = (int) (highlight.getY() + textHeight * 0.5f);
            Rect contentRect = getContentRect();
            top = Math.min(Math.max(top, contentRect.top + textHeight), contentRect.bottom);
            int bottom = (int) (highlight.getY() - textHeight * 0.5f);
            bottom = Math.min(bottom, contentRect.bottom - textHeight);

            Rect bgRect = new Rect(0, top, 80, bottom);
            canvas.drawRect(bgRect, mHighlightBgPaint);

            ChartData chartData = mRenderer.getChartData();

            float price = getTouchPriceByY(highlight.getY(), chartData.getLeftMax(), chartData.getLeftMin());

            Log.d("drawHighlightLeft", "price=" + price + "leftMax: "+chartData.getLeftMax()  + "leftMin: "+chartData.getLeftMin() );

        }
    }

    private float getTouchPriceByY(float touchY, float viewportMax, float viewportMin) {
        if (viewportMax > viewportMin && viewportMax > 0) {
            Rect contentRect = getContentRect();
            var price = viewportMin + (viewportMax - viewportMin) / contentRect.height() * (contentRect.height() - touchY);
            if (price > viewportMax) price = viewportMax;
            if (price < viewportMin) price = viewportMin;
            return price;
        }
        return -1f;
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

    @Override
    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        super.setMinVisibleEntryCount(minVisibleEntryCount);
        mRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
    }

    @Override
    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        super.setMaxVisibleEntryCount(maxVisibleEntryCount);
        mRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    @Override
    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        super.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
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

