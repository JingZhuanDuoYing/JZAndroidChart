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
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.renderer.AbstractDataRenderer;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart.renderer.Renderer;
import cn.jingzhuan.lib.chart.data.IDataSet;

/**
 * Created by Donglua on 17/7/17.
 */

public class BaseChart extends Chart {

    protected AbstractDataRenderer mRenderer;
    private List<AxisRenderer> mAxisRenderers;

    protected Highlight[] mHighlights;

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

        mHighlights = new Highlight[] { highlight };

        invalidate();
    }

    @Override
    public void cleanHighlight() {
        mHighlights = null;

        invalidate();
    }

    public void setRenderer(AbstractDataRenderer renderer) {
        this.mRenderer = renderer;
    }

    @Override
    protected final void render(final Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
            mRenderer.renderHighlighted(getHighlights());
        }
    }

    public Highlight[] getHighlights() {
        return mHighlights;
    }

    public void setHighlights(Highlight[] highlights) {
        this.mHighlights = highlights;
    }
}
