package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.NonNull;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.PointLineData;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 *  虚线加点
 */
public class PointLineRenderer extends AbstractDataRenderer<PointLineDataSet> {

    private PointLineData pointLineData;
    private Path mPath;
    private final Paint mPointPaint; //圆点画笔
    public PointLineRenderer(Chart chart) {
        super(chart);

        mPath = new Path();
        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);
        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });
    }

    private void drawDataSet(Canvas canvas, final PointLineDataSet lineDataSet,
                             float lMax, float lMin, float rMax, float rMin){
        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());
        float interval = lineDataSet.getInterval();
        mRenderPaint.setPathEffect(new DashPathEffect(new float[]{interval,interval},lineDataSet.getPhase()));
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
        final float step =  (mContentRect.width() * scale / valueCount);
        final float startX = mContentRect.left  - mViewport.left * mContentRect.width() * scale;

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


            if ((yPosition + lineDataSet.getRadius() )> mContentRect.bottom){
                yPosition -= lineDataSet.getRadius();
            }
            if ((yPosition - lineDataSet.getRadius()) < mContentRect.top)
                yPosition += lineDataSet.getRadius();

            if (isFirst){
                isFirst = false;
                mPath.moveTo(candlestickCenterX, yPosition);
            }else {
                mPath.lineTo(candlestickCenterX,yPosition);
            }

            canvas.drawCircle(candlestickCenterX,yPosition,lineDataSet.getRadius(),mPointPaint);
        }//for
        if (lineDataSet.isLineVisible()) {
                canvas.drawPath(mPath, mRenderPaint);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<PointLineDataSet> chartData) {
        for (PointLineDataSet dataSet : getDataSet()) {
            renderDataSet(canvas, chartData, dataSet);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<PointLineDataSet> chartData, PointLineDataSet dataSet) {
        if (dataSet.isVisible()) {
            drawDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

    }

    @Override
    public void removeDataSet(PointLineDataSet dataSet) {
        pointLineData.remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        pointLineData.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<PointLineDataSet> getDataSet() {
        return pointLineData.getDataSets();
    }

    @Override
    public ChartData<PointLineDataSet> getChartData() {
        if (pointLineData == null) pointLineData = new PointLineData();
        return pointLineData;
    }
}
