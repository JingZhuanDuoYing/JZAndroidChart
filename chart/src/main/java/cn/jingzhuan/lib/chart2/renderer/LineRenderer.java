package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.LineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.widget.LineChart;

/**
 * Line Renderer
 *
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<LineDataSet> {

    private LineData lineData;
    private List<Path> shaderPaths;
    private List<Shader> shaderPathColors;
    private List<Path> linePaths;
    private Path shaderPath;

    private boolean onlyLines = false;

    public LineRenderer(final Chart chart) {
        super(chart);

        linePaths = new ArrayList<>();
        shaderPath = new Path();
        shaderPaths = new ArrayList<>();
        shaderPathColors = new ArrayList<>();

        if (chart instanceof LineChart) {
            onlyLines = true;
        }

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });

        final Highlight highlight = new Highlight();
        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                if (chart.isHighlightDisable()) return;

                synchronized (chart) {
                    for (LineDataSet line : getDataSet()) {
                        if (line.isHighlightedVerticalEnable() && !line.getValues().isEmpty()) {
                            highlight.setTouchX(x);
                            highlight.setTouchY(y);
                            int offset = line.getStartIndexOffset();
                            int index = getEntryIndexByCoordinate(x, y) - offset;
                            if (index >= 0 && index < line.getValues().size()) {
                                final PointValue pointValue = line.getEntryForIndex(index);
                                float xPosition = pointValue.getX();
                                float yPosition = pointValue.getY();

                                if (xPosition >= 0 && yPosition >= 0) {
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

        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setStrokeWidth(1f);
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

        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());

        int valueCount = lineDataSet.getEntryCount();

        shaderPath.reset();
        shaderPaths.clear();
        shaderPathColors.clear();

        linePaths.clear();

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

        final float count = lineDataSet.getVisibleRange(mViewport);
        final float width = count > 0 ? (mContentRect.width() / count): 0;

        final int offset = lineDataSet.getStartIndexOffset();

        final float scale = 1 / mViewport.width();
        final float step = (valueCount > 1 && onlyLines) ?
            (mContentRect.width() * scale / (valueCount - 1)) : (mContentRect.width() * scale / valueCount);
        final float startX = mContentRect.left + (onlyLines ? 0f : step * 0.5f) - mViewport.left * mContentRect.width() * scale;

        PointValue prevValue = null;

        boolean shaderSplit = !Float.isNaN(lineDataSet.getShaderBaseValue()) &&
                              lineDataSet.getShaderBaseValue() < max &&
                              lineDataSet.getShaderBaseValue() > min;

        int lastIndex = 0;
        if (mChartAnimator.getPhaseX() > 0) {
          lastIndex = (int) (Math.floor(lineDataSet.getValues().size() * mChartAnimator.getPhaseX()) - 1);
        }
        
        if (lastIndex >= valueCount) lastIndex = valueCount - 1;

        PointValue startPoint = null;

        int valuePhaseCount = (int) Math.floor(valueCount * mChartAnimator.getPhaseX());

        Path linePath = new Path();

        float splitStartBaseX = 0;
        int i = 0;
        float preBaseX = Float.NaN;
        for (; i < valuePhaseCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            if (point.isValueNaN()) {
                continue;
            }

            float xPosition = startX + step * (i + offset);
            float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();

            point.setCoordinate(xPosition, yPosition);

            if (isFirst) {
                if (!point.isPathEnd()) {
                    isFirst = false;
                    linePath.moveTo(xPosition, yPosition);
                }
            } else {
                linePath.lineTo(xPosition, yPosition);
                if (point.isPathEnd()) {
                    linePaths.add(linePath);
                    linePath = new Path();
                    isFirst = true;
                }
            }

            if (shaderSplit) {
                float baseValue = lineDataSet.getShaderBaseValue();
                float baseValueY = mContentRect.height() / (max - min) * (max - baseValue);

                if (prevValue == null) {
                    preBaseX = point.getX();
                    shaderPath.moveTo(preBaseX, yPosition);
                } else if (prevValue.getValue() > lineDataSet.getShaderBaseValue()) {
                    if (point.getValue() <= lineDataSet.getShaderBaseValue()) { // 跨越颜色区域

                        float nextBaseX = getBaseX(prevValue, point, baseValueY);
                        shaderPath.lineTo(nextBaseX, baseValueY);
                        shaderPath.lineTo(preBaseX, baseValueY);
                        shaderPath.close();

                        shaderPaths.add(new Path(shaderPath));
                        shaderPathColors.add(lineDataSet.getShaderTop());

                        shaderPath.reset();
                        shaderPath.moveTo(nextBaseX, baseValueY);
                        shaderPath.lineTo(xPosition, yPosition);

                        preBaseX = nextBaseX;
                    } else {
                        shaderPath.lineTo(xPosition, yPosition); // 当前值坐标
                    }
                } else if (point.getValue() > lineDataSet.getShaderBaseValue()) {

                    float nextBaseX = getBaseX(prevValue, point, baseValueY);
                    shaderPath.lineTo(nextBaseX, baseValueY);
                    shaderPath.lineTo(preBaseX, baseValueY);
                    shaderPath.close();

                    shaderPaths.add(new Path(shaderPath));
                    shaderPathColors.add(lineDataSet.getShaderBottom());

                    shaderPath.reset();
                    shaderPath.moveTo(nextBaseX, baseValueY);
                    shaderPath.lineTo(xPosition, yPosition);

                    preBaseX = nextBaseX;

                } else {
                    shaderPath.lineTo(xPosition, yPosition); // 当前值坐标
                }

                prevValue = point;

                if (lastIndex == i) {
                    shaderPath.lineTo(xPosition, baseValueY);
                    shaderPath.lineTo(preBaseX, baseValueY);
                    shaderPath.close();
                    shaderPaths.add(new Path(shaderPath));
                    if (prevValue.getValue() > baseValue) {
                        shaderPathColors.add(lineDataSet.getShaderTop());
                    } else {
                        shaderPathColors.add(lineDataSet.getShaderBottom());
                    }
                    shaderPath.reset();
                }
            }
        } // end for.

        if (!isFirst) {
            linePaths.add(linePath);
        }

        if (!shaderSplit) { // 不区分颜色分段

            // draw shader area
            if (i > 0 && lineDataSet.getShader() != null && lineDataSet.getValues().size() > 0) {
                mRenderPaint.setStyle(Paint.Style.FILL);

                if (shaderPath == null) {
                    shaderPath = new Path(linePath);
                } else {
                    shaderPath.set(linePath);
                }

                PointValue pointValue = lineDataSet.getEntryForIndex(i - 1);

                if (pointValue != null) {
                    shaderPath.lineTo(startX + pointValue.getX(), mContentRect.bottom);
                    shaderPath.lineTo(startX + offset * width, mContentRect.bottom);
                    shaderPath.lineTo(startX + offset * width, lineDataSet.getValues().get(0).getY());
                    shaderPath.close();
                    mRenderPaint.setShader(lineDataSet.getShader());
                    canvas.drawPath(shaderPath, mRenderPaint);
                    mRenderPaint.setShader(null);
                    mRenderPaint.setStyle(Paint.Style.STROKE);
                }
            }
        } else {
            mRenderPaint.setStyle(Paint.Style.FILL);

            for (i = 0; i < shaderPaths.size(); i++) {
                Path path = shaderPaths.get(i);

                Shader shader = shaderPathColors.get(i);
                mRenderPaint.setShader(shader);
                canvas.drawPath(path, mRenderPaint);
                mRenderPaint.setShader(null);
            }
            mRenderPaint.setStyle(Paint.Style.STROKE);
        }

        if (lineDataSet.isLineVisible()) {
            for (Path path : linePaths) {
                canvas.drawPath(path, mRenderPaint);
            }
        }
    }

    private float getBaseX(PointValue p1, PointValue p2, float baseY) {
        float x1 = p1.getX();
        float x2 = p2.getX();
        float y1 = Math.abs(p1.getY() - baseY);
        float y2 = Math.abs(p2.getY() - baseY);
        return (y1 * x2 + x1 * y2) / (y2 + y1);
    }
}
