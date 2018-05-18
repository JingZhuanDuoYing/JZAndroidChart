package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;

import cn.jingzhuan.lib.chart.base.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.LineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import java.util.List;

/**
 * Line Renderer
 *
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<LineDataSet> {

    private LineData lineData;
    private Path linePath;
    private Path shaderPath;

    public LineRenderer(final Chart chart) {
        super(chart);

        linePath = new Path();

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport = viewport;
                calcDataSetMinMax();
            }
        });

        final Highlight highlight = new Highlight();
        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                //noinspection SynchronizeOnNonFinalField
                synchronized (lineData) {
                    for (LineDataSet line : getDataSet()) {
                        if (line.isHighlightedVerticalEnable() && !line.getValues().isEmpty()) {
                            int index = getEntryIndexByCoordinate(x, y);
                            if (index > 0 && index < line.getValues().size()) {
                                final PointValue pointValue = line.getEntryForIndex(index);
                                float xPosition = pointValue.getX();
                                float yPosition = pointValue.getY();

                                if (xPosition > 0 && yPosition > 0) {
                                    highlight.setX(xPosition);
                                    highlight.setY(yPosition);
                                    highlight.setDataIndex(index);
                                    chart.highlightValue(highlight);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setColor(getHighlightColor());
        if (mHighlightedDashPathEffect != null) {
            mRenderPaint.setPathEffect(mHighlightedDashPathEffect);
        }

        for (Highlight highlight : highlights) {

            if (highlight != null) {
                canvas.drawLine(highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);

                // Horizontal
                for (LineDataSet lineDataSet : getDataSet()) {
                    if (lineDataSet.isHighlightedHorizontalEnable()) {
                        canvas.drawLine(0,
                            highlight.getY(),
                            mContentRect.right,
                            highlight.getY(),
                            mRenderPaint);
                    }
                }
            }
        }
        mRenderPaint.setPathEffect(null);
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

        linePath.reset();
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

        final int count = lineDataSet.getVisibleValueCount(mViewport);
        float width = 0;
        if (count > 0) {
            width = mContentRect.width() / ((float) count);
        }

        for (int i = 0; i < valueCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            if (Float.isNaN(point.getValue())) {
                continue;
            }

            float xPosition = width * 0.5f + getDrawX(i / ((float) valueCount));
            float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();

            point.setCoordinate(xPosition, yPosition);

            if (isFirst) {
                isFirst = false;
                linePath.moveTo(xPosition, yPosition);
            } else  {
                linePath.lineTo(xPosition, yPosition);
            }
        }

        // draw shader area
        if (lineDataSet.getShader() != null && lineDataSet.getValues().size() > 0) {
            mRenderPaint.setStyle(Paint.Style.FILL);

            if (shaderPath == null) {
                shaderPath = new Path(linePath);
            } else {
                shaderPath.set(linePath);
            }

            int lastIndex = lineDataSet.getValues().size() - 1;
            if (lastIndex >= valueCount) lastIndex = valueCount - 1;

            PointValue pointValue = lineDataSet.getEntryForIndex(lastIndex);

            if (pointValue != null) {

                shaderPath.lineTo(lineDataSet.getValues().get(lastIndex).getX(), mContentRect.bottom);
                shaderPath.lineTo(0, mContentRect.bottom);
                shaderPath.lineTo(0, lineDataSet.getValues().get(0).getY());
                shaderPath.close();
                mRenderPaint.setShader(lineDataSet.getShader());
                canvas.drawPath(shaderPath, mRenderPaint);
                mRenderPaint.setShader(null);
                mRenderPaint.setStyle(Paint.Style.STROKE);

            }
        }

        canvas.drawPath(linePath, mRenderPaint);
    }

}
