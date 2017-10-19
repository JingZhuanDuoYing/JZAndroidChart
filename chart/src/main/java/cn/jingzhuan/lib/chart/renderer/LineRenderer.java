package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import android.support.annotation.NonNull;
import android.util.Log;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.ChartData;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.LineData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<LineDataSet> {

    private LineData lineData;

    public LineRenderer(final Chart chart) {
        super(chart);

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport = viewport;
                calcDataSetMinMax();
            }
        });

        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (LineDataSet line : getDataSet()) {
                    if (line.isHighlightedVerticalEnable()) {
                        float xPositionMax = line.getEntryForIndex(line.getValues().size() - 1).getX();

                        int index = 0;
                        if (x > mContentRect.left) {
                            index = (int) (line.getEntryCount()
                                    * (x - mContentRect.left) * mViewport.width()
                                    / mContentRect.width()
                                    + mViewport.left);
                        }
                        if (index >= line.getValues().size()) index = line.getValues().size() - 1;
                        if (x > xPositionMax) x = xPositionMax;
                        chart.highlightValue(new Highlight(x, y, index));
                    }
                }
            }
        });
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setColor(getHighlightColor());
        if (mDashedHighlightPhase > 0) {
            mRenderPaint.setPathEffect(new DashPathEffect(mDashedHighlightIntervals, mDashedHighlightPhase));
        }

        for (Highlight highlight : highlights) {
            Canvas c = mBitmapCanvas == null ? canvas : mBitmapCanvas;

            c.drawLine(
                    highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);

            // Horizontal
            for (LineDataSet lineDataSet : getDataSet()) {
                if (lineDataSet.isHighlightedHorizontalEnable()) {
                    float min = lineDataSet.getViewportYMin();
                    float max = lineDataSet.getViewportYMax();
                    float value =  lineDataSet.getEntryForIndex(highlight.getDataIndex()).getValue();
                    float y = (max - value) / (max - min) * mContentRect.height();
                    c.drawLine(0, y, mContentRect.right, y, mRenderPaint);
                }
            }
        }
        mRenderPaint.setPathEffect(null);
    }

    @Override
    public void addDataSet(LineDataSet dataSet) {
        lineData.add(dataSet);
        calcDataSetMinMax();
    }

    @Override public void removeDataSet(LineDataSet dataSet) {
        lineData.remove(dataSet);
        calcDataSetMinMax();
    }

    @Override public void clearDataSet() {
        lineData.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<LineDataSet> getDataSet() {
        return lineData.getDataSets();
    }

    @Override public ChartData<LineDataSet> getChartData() {
        if (lineData == null) lineData = new LineData();
        return lineData;
    }

    @Override public void calcDataSetMinMax() {
        lineData.calcMaxMin(mViewport, mContentRect);
    }

    @Override
    protected void renderDataSet(Canvas canvas) {
        renderDataSet(canvas, getChartData());
    }

    @Override protected void renderDataSet(Canvas canvas, ChartData<LineDataSet> chartData) {
        for (LineDataSet dataSet : getDataSet()) {
            if (dataSet.isVisible()) {
                drawDataSet(canvas, dataSet,
                        chartData.getLeftMax(), chartData.getLeftMin(),
                        chartData.getRightMax(), chartData.getRightMin());
            }
        }
    }

    private void drawDataSet(Canvas canvas, final LineDataSet lineDataSet,
        float lMax, float lMin, float rMax, float rMin) {

        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());

        int valueCount = lineDataSet.getEntryCount();

        Path path = new Path();
        path.reset();
        boolean isFirst = true;

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

        final float width = mContentRect.width() / lineDataSet.getVisibleValueCount(mViewport);

        for (int i = 0; i < valueCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            float xPosition = width * 0.5f + getDrawX(i / ((float) valueCount));
            float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();

            point.setX(xPosition);
            point.setY(yPosition);

            if (isFirst) {
                isFirst = false;
                path.moveTo(xPosition, yPosition);
            } else  {
                path.lineTo(xPosition, yPosition);
            }
        }

        // draw shader area
        if (lineDataSet.getShader() != null && lineDataSet.getValues().size() > 0) {
            mRenderPaint.setStyle(Paint.Style.FILL);
            Path shaderPath = new Path(path);
            shaderPath.lineTo(lineDataSet.getValues().get(lineDataSet.getValues().size() - 1).getX(), mContentRect.bottom);
            shaderPath.lineTo(0, mContentRect.bottom);
            shaderPath.lineTo(0, lineDataSet.getValues().get(0).getY());
            shaderPath.close();
            mRenderPaint.setShader(lineDataSet.getShader());
            canvas.drawPath(shaderPath, mRenderPaint);
            mRenderPaint.setShader(null);
            mRenderPaint.setStyle(Paint.Style.STROKE);
        }

        canvas.drawPath(path, mRenderPaint);
    }

}
