package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.BaseChart;
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

    protected final float radiusIn;

    protected final float radiusOut;

    private List<CandlestickValue> candlestickValues = new ArrayList<>();

    protected float viewportMax;

    protected float viewportMin;

    private DrawLineTouchState touchState = DrawLineTouchState.none;

    protected PointF touchPointStart;

    protected PointF touchPointEnd;

    public BaseDraw(Chart chart) {
        this.chart = chart;
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();
        radiusIn = chart.getResources().getDimensionPixelSize(R.dimen.jz_draw_line_point_in);
        radiusOut = chart.getResources().getDimensionPixelSize(R.dimen.jz_draw_line_point_out);
        initPaint();
    }

    public void initPaint() {
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);

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
        this.candlestickValues = candlestickDataSet.getValues();
        this.viewportMax = lMax;
        this.viewportMin = lMin;
        setPaint(dataSet);
        drawTouchState(canvas);
    }

    @Override
    public void onTouch(DrawLineTouchState state, PointF point) {
        this.touchState = state;
        switch (state) {
            case none:
                touchPointStart = new PointF();
                touchPointEnd = new PointF();
                break;
            case first:
                touchPointStart = point;
                break;
            case second:
                touchPointEnd = point;
                break;
            case complete:
                chart.setDrawLineComplete(touchPointStart, touchPointEnd);
                break;

        }
    }

    public void drawTouchState(Canvas canvas) {
        linePaint.setStyle(Paint.Style.FILL);

        if (touchState == DrawLineTouchState.first) {
            // 第一步 画起点
            drawStartPoint(canvas);
        } else if (touchState == DrawLineTouchState.second) {
            // 第二步 画起点 、终点、 高亮背景、存入数据
            drawStartPoint(canvas);
            drawEndPoint(canvas);
            drawTypeShape(canvas);
        }
    }


    /**
     * 画起点
     */
    private void drawStartPoint(Canvas canvas) {
        int index = ((BaseChart) chart).getEntryIndexByCoordinate(touchPointStart.x, touchPointStart.y);
        float cx = candlestickValues.get(index).getX();
        touchPointStart.x = cx;
        bgPaint.setAlpha(30);
        canvas.drawCircle(cx, touchPointStart.y, radiusOut, bgPaint);
        canvas.drawCircle(cx, touchPointStart.y, radiusIn, linePaint);
    }

    /**
     * 画终点
     */
    private void drawEndPoint(Canvas canvas) {
        int index = ((BaseChart) chart).getEntryIndexByCoordinate(touchPointEnd.x, touchPointEnd.y);
        float cx = candlestickValues.get(index).getX();
        touchPointEnd.x = cx;
        bgPaint.setAlpha(30);
        canvas.drawCircle(cx, touchPointEnd.y, radiusOut, bgPaint);
        canvas.drawCircle(cx, touchPointEnd.y, radiusIn, linePaint);
    }

    protected float getScaleY(float value) {
        if (viewportMax > viewportMin && viewportMax > 0) {

            return (viewportMax - value) / (viewportMax - viewportMin) * mContentRect.height();
        }
        return -1f;
    }
}
