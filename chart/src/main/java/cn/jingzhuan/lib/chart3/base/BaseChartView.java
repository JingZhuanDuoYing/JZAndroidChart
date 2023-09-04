package cn.jingzhuan.lib.chart3.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart3.renderer.AbstractRenderer;
import cn.jingzhuan.lib.chart3.state.HighlightState;

public class BaseChartView<V extends Value, T extends AbstractDataSet<V>> extends AbstractChartView<V, T> {

    private AbstractRenderer<V, T> chartRenderer;

    private final Paint waterMarkPaint = new Paint();

    public BaseChartView(Context context) {
        super(context);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void initChart() {

    }

    public void setChartRenderer(AbstractRenderer<V, T> chartRenderer) {
        this.chartRenderer = chartRenderer;
    }

    /**
     * 画内容
     */
    @Override
    public void drawChart(Canvas canvas) {
        chartRenderer.renderer(canvas);
    }

    /**
     * 画水印
     */
    @Override
    public void drawWaterMark(Canvas canvas) {
        int padding = getResources().getDimensionPixelSize(R.dimen.jz_chart_water_mark_padding);
        Bitmap waterMarkBitmap = BitmapFactory.decodeResource(
                this.getResources(), isNightMode() ? R.drawable.ico_water_mark_night : R.drawable.ico_water_mark);
        int left = getWidth() - padding - waterMarkBitmap.getWidth() - getPaddingRight();
        canvas.drawBitmap(waterMarkBitmap, (float) left, (float) padding, waterMarkPaint);
    }

    /**
     * 画坐标轴
     */
    @Override
    public void drawAxis(Canvas canvas) {
        mAxisLeftRenderer.renderer(canvas);
        mAxisRightRenderer.renderer(canvas);
        mAxisTopRenderer.renderer(canvas);
        mAxisBottomRenderer.renderer(canvas);
    }

    /**
     * 画坐标轴文本 (左、右、上 如果配置了)
     */
    @Override
    public void drawAxisLabels(Canvas canvas) {
        mAxisLeftRenderer.drawAxisLabels(canvas);
        mAxisRightRenderer.drawAxisLabels(canvas);
        mAxisTopRenderer.drawAxisLabels(canvas);
    }

    /**
     * 画坐标轴底部文本
     */
    @Override
    public void drawBottomLabels(Canvas canvas) {
        mAxisBottomRenderer.drawAxisLabels(canvas);
    }

    /**
     * 画网格线
     */
    @Override
    public void drawGridLine(Canvas canvas) {
        // 左右 只画一次
        Axis axisLeft = mAxisLeftRenderer.getAxis();
        boolean drawLeft = axisLeft.isEnable() && axisLeft.isGridLineEnable() && axisLeft.getGridCount() > 0;

        Axis axisRight = mAxisRightRenderer.getAxis();
        boolean drawRight = axisRight.isEnable() && axisRight.isGridLineEnable() && axisRight.getGridCount() > 0;

        if (drawLeft && drawRight) {
            mAxisLeftRenderer.drawGridLines(canvas);
        } else if (drawLeft) {
            mAxisLeftRenderer.drawGridLines(canvas);
        } else if (drawRight) {
            mAxisRightRenderer.drawGridLines(canvas);
        }

        // 上下 只画一次
        Axis axisTop = mAxisTopRenderer.getAxis();
        boolean drawTop = axisTop.isEnable() && axisTop.isGridLineEnable() && axisTop.getGridCount() > 0;

        Axis axisBottom = mAxisBottomRenderer.getAxis();
        boolean drawBottom = axisBottom.isEnable() && axisBottom.isGridLineEnable() && axisBottom.getGridCount() > 0;

        if (drawTop && drawBottom) {
            mAxisTopRenderer.drawGridLines(canvas);
        } else if (drawTop) {
            mAxisTopRenderer.drawGridLines(canvas);
        } else if (drawBottom) {
            mAxisBottomRenderer.drawGridLines(canvas);
        }
    }

    /**
     * 画十字交叉线
     */
    @Override
    public void drawHighlight(Canvas canvas) {
        mHighlightRenderer.renderer(canvas);
    }

    @Override
    void highlightValue(Highlight highlight) {
        mHighlightRenderer.highlightValue(highlight);
        invalidate();
    }

    @Override
    void cleanHighlight() {
        setHighlightState(HighlightState.initial);
        mHighlightRenderer.cleanHighlight();
        invalidate();
    }

    @Override
    void onTouchPoint(MotionEvent event) {

    }

    @Nullable
    @Override
    Paint getRenderPaint() {
        if (chartRenderer == null) return null;
        return chartRenderer.getRenderPaint();
    }

    @Override
    public ChartData<T> getChartData() {
        return chartRenderer.getChartData();
    }
}
