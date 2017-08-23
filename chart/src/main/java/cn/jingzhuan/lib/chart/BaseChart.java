package cn.jingzhuan.lib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import cn.jingzhuan.lib.chart.event.OnHighlightRenderListener;
import cn.jingzhuan.lib.chart.renderer.AbstractDataRenderer;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart.renderer.Renderer;

/**
 * Created by Donglua on 17/7/17.
 */

public class BaseChart extends Chart {

    protected AbstractDataRenderer mRenderer;
    private List<AxisRenderer> mAxisRenderers;

    protected Highlight[] mHighlights;
    private HighlightStatusChangeListener mHighlightStatusChangeListener;
    private OnHighlightRenderListener mHighlightListener;

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
    }

    @Override
    protected void drawAxis(Canvas canvas) {
        for (Renderer axisRenderer : mAxisRenderers) {
            axisRenderer.renderer(canvas);
        }
    }

    @Override
    protected void drawLabels(Canvas canvas) {
        for (AxisRenderer axisRenderer : mAxisRenderers) {
            axisRenderer.drawLabels(canvas);
        }
    }

    @Override
    protected void onTouchPoint(float x, float y) {
        for (OnTouchPointChangeListener touchPointChangeListener : mTouchPointChangeListeners) {
            touchPointChangeListener.touch(x, y);
        }
    }

    @Override
    public void highlightValue(Highlight highlight) {

        if (highlight == null) return;

        final Highlight[] highlights = new Highlight[] { highlight };

        if (this.mHighlights != null && mHighlightStatusChangeListener != null) {
            mHighlightStatusChangeListener.onHighlightShow(highlights);
        }


        mHighlights = highlights;

        invalidate();
    }

    @Override
    public void cleanHighlight() {
        mHighlights = null;

        if (mHighlightStatusChangeListener != null)
            mHighlightStatusChangeListener.onHighlightHide();

        invalidate();
    }

    public void setRenderer(AbstractDataRenderer renderer) {
        this.mRenderer = renderer;
    }

    @Override
    protected final void render(final Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
            renderHighlighted(canvas);
        }
    }

    public void renderHighlighted(Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderHighlighted(canvas, getHighlights());
            if (getHighlights() != null && mHighlightListener != null) {
                mHighlightListener.highlight(getHighlights());
            }
        }
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

    public void setOnRenderHighlightListener(OnHighlightRenderListener mHighlightListener) {
        this.mHighlightListener = mHighlightListener;
    }
}
