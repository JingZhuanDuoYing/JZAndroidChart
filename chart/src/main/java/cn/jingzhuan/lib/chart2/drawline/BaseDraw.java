package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;


/**
 * @since 2023-08-29
 */
public abstract class BaseDraw implements IDraw {

    protected Viewport mViewport;

    protected Rect mContentRect;
    protected final Paint linePaint = new Paint();

    protected final Paint bgPaint = new Paint();

    protected final Paint textPaint = new Paint();

    protected final Chart chart;

    protected float radiusIn = 3f;

    protected float radiusOut = 8f;

    public BaseDraw(Chart chart) {
        this.chart = chart;
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();
        initPaint();
    }

    public void initPaint() {
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setAlpha(20);

        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
    }

    private void setPaint(DrawLineDataSet dataSet) {
        linePaint.setColor(dataSet.getLineColor());
        linePaint.setStrokeWidth(dataSet.getLineSize());

        bgPaint.setColor(dataSet.getLineColor());

        textPaint.setTextSize(dataSet.getFontSize());
        textPaint.setColor(dataSet.getLineColor());
    }

    @Override
    public void onDraw(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin) {
        setPaint(dataSet);
    }

    protected float getScaleY(float value, float viewportMax, float viewportMin) {
        if (viewportMax > viewportMin && viewportMax > 0) {

            return (viewportMax - value) / (viewportMax - viewportMin) * mContentRect.height();
        }
        return -1f;
    }
}
