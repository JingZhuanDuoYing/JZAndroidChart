package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.component.XYCoordinate;
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

    public LineRenderer(final Chart chart) {
        super(chart);

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
                for (LineDataSet line : getDataSet()) {
                    if (line.isHighlightedVerticalEnable() && !line.getValues().isEmpty()) {
                        int index = getEntryIndexByCoordinate(x, y);
                        if (index > 0 && index < line.getValues().size()) {
                            final PointValue pointValue = line.getEntryForIndex(index);
                            XYCoordinate coordinate = pointValue.getCoordinate();
                            if (coordinate != null) {
                                highlight.setX(coordinate.getX());
                                highlight.setY(coordinate.getY());
                                highlight.setDataIndex(index);
                                chart.highlightValue(highlight);
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
        if (mDashedHighlightPhase > 0) {
            mRenderPaint.setPathEffect(new DashPathEffect(mDashedHighlightIntervals, mDashedHighlightPhase));
        }

        for (Highlight highlight : highlights) {

            canvas.drawLine(
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
                    if (highlight.getDataIndex() < lineDataSet.getValues().size()) {
                        float value = lineDataSet.getEntryForIndex(highlight.getDataIndex()).getValue();
                        float y = (max - value) / (max - min) * mContentRect.height();
                        canvas.drawLine(0, y, mContentRect.right, y, mRenderPaint);
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

            point.setCoordinate(new XYCoordinate(xPosition, yPosition));

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
            int lastIndex = lineDataSet.getValues().size() - 1;
            if (lastIndex >= valueCount) lastIndex = valueCount - 1;

            if (lineDataSet.getValues().get(lastIndex).getCoordinate() != null
                && lineDataSet.getValues().get(0).getCoordinate() != null) {
                shaderPath.lineTo(lineDataSet.getValues().get(lastIndex).getCoordinate().getX(),
                    mContentRect.bottom);
                shaderPath.lineTo(0, mContentRect.bottom);
                shaderPath.lineTo(0, lineDataSet.getValues().get(0).getCoordinate().getY());
                shaderPath.close();
                mRenderPaint.setShader(lineDataSet.getShader());
                canvas.drawPath(shaderPath, mRenderPaint);
                mRenderPaint.setShader(null);
                mRenderPaint.setStyle(Paint.Style.STROKE);
            }
        }

        canvas.drawPath(path, mRenderPaint);
    }

}
