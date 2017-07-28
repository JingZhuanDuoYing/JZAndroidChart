package cn.jingzhuan.lib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.renderer.AbstractDataRenderer;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart.renderer.Renderer;
import cn.jingzhuan.lib.chart.value.Line;

/**
 * Created by Donglua on 17/7/17.
 */

public class BaseChart<T extends Line> extends Chart {

    protected AbstractDataRenderer<T> mRenderer;
    private List<Renderer> mAxisRenderers;

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

    public void setRenderer(AbstractDataRenderer<T> renderer) {
        this.mRenderer = renderer;
    }

    @Override
    protected final void render(final Canvas canvas) {
        if (mRenderer != null) {
            mRenderer.renderer(canvas);
        }
    }


}
