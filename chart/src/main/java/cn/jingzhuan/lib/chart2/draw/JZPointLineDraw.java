package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZPointLineDraw extends BaseDraw<PointValue, PointLineDataSet> {

    private Path mPath;

    private final Paint mPointPaint;

    public JZPointLineDraw(Chart chart) {
        super(chart);
        mPath = new Path();
        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);

        addOnTouchPointChangeListener();
    }

    @Override
    public void drawDataSet(Canvas canvas, ChartData<PointLineDataSet> chartData, PointLineDataSet dataSet) {
        if (dataSet.isVisible()) {
            dataSetList = chartData.getDataSets();
            drawPointLineDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawPointLineDataSet(Canvas canvas, final PointLineDataSet lineDataSet, float lMax, float lMin, float rMax, float rMin) {
        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());

        float interval = lineDataSet.getInterval();
        if (interval != 0f)
            mRenderPaint.setPathEffect(new DashPathEffect(new float[]{interval, interval}, lineDataSet.getPhase()));
        mRenderPaint.setAntiAlias(true);

        mPointPaint.setColor(lineDataSet.getColor());
        mPointPaint.setAntiAlias(true);

        int valueCount = lineDataSet.getEntryCount();

        float min, max;
        switch (lineDataSet.getAxisDependency()) {
            case AxisY.DEPENDENCY_RIGHT:
                min = rMin;
                max = rMax;
                break;
            case AxisY.DEPENDENCY_BOTH:
            case AxisY.DEPENDENCY_LEFT:
            default:
                min = lMin;
                max = lMax;
                break;
        }

        final int offset = lineDataSet.getStartIndexOffset();
        final float scale = 1 / mViewport.width();
        final float step = (mContentRect.width() * scale / valueCount);
        final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;

        int valuePhaseCount = (int) Math.floor(valueCount * mChartAnimator.getPhaseX());
        mPath.reset();
        mPath = new Path();
        boolean isFirst = true;

        int i = 0;
        final float visibleRange = lineDataSet.getVisibleRange(mViewport);
        double candleWidth = mContentRect.width() / Math.max(visibleRange, lineDataSet.getMinValueCount());
        for (; i < valuePhaseCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            if (point.isValueNaN()) {
                continue;
            }

            float xPosition = startX + step * (i + offset);
            float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();
            final float candlestickCenterX = (float) (xPosition + candleWidth * 0.5);
            point.setCoordinate(candlestickCenterX, yPosition);


            if ((yPosition + lineDataSet.getRadius()) > mContentRect.bottom) {
                yPosition -= lineDataSet.getRadius();
            }
            if ((yPosition - lineDataSet.getRadius()) < mContentRect.top)
                yPosition += lineDataSet.getRadius();

            if (isFirst) {
                isFirst = false;
                mPath.moveTo(candlestickCenterX, yPosition);
            } else {
                mPath.lineTo(candlestickCenterX, yPosition);
            }

            if (point.isDrawCircle()) {
                canvas.drawCircle(candlestickCenterX, yPosition, lineDataSet.getRadius(), mPointPaint);
            }
        }

        if (lineDataSet.isLineVisible()) {
            canvas.drawPath(mPath, mRenderPaint);
        }
    }

}
