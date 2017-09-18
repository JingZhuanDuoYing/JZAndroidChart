package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.support.annotation.NonNull;
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

    private ChartData<LineDataSet> lineData;

    public LineRenderer(final Chart chart) {
        super(chart);

        lineData = new LineData();

        chart.setOnViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                for (LineDataSet line : getDataSet()) {
                    line.onViewportChange(viewport);
                }
            }
        });

        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (LineDataSet line : getDataSet()) {
                    if (line.isHighlightedEnable()) {
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

        for (Highlight highlight : highlights) {

            Canvas c = mBitmapCanvas == null ? canvas : mBitmapCanvas;
            c.drawLine(
                    highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);

            for (LineDataSet lineDataSet : getDataSet()) {
                if (lineDataSet.isHighlightedHorizontalEnable()) {
                    float min = lineDataSet.getViewportYMin();
                    float max = lineDataSet.getViewportYMax();
                    float value =  lineDataSet.getEntryForIndex(highlight.getDataIndex()).getValue();
                    float y = (max - value) / (max - min) * mContentRect.height();
                    canvas.drawLine(0, y, mContentRect.right, y, mRenderPaint);
                }
            }
        }

    }

    @Override
    public void addDataSet(LineDataSet dataSet) {
        lineData.add(dataSet);
    }

    @Override
    public List<LineDataSet> getDataSet() {
        return lineData.getDataSets();
    }

    @Override
    protected void renderDataSet(Canvas canvas) {

        for (LineDataSet line : getDataSet()) {
            if (line.isVisible()) {
                drawDataSet(canvas, line);
            }
        }

    }

    private void drawDataSet(Canvas canvas, LineDataSet lineDataSet) {

        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());

        int valueCount = lineDataSet.getEntryCount();

        Path path = new Path();
        path.reset();
        boolean isFirst = true;

        float min = lineDataSet.getViewportYMin();
        float max = lineDataSet.getViewportYMax();

        for (int i = 0; i < valueCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            float xV = getDrawX(i / (valueCount - 1f));
            float yV = (max - point.getValue()) / (max - min) * mContentRect.height();

            point.setX(xV);
            point.setY(yV);

            if (isFirst) {
                isFirst = false;
                path.moveTo(xV, yV);
            } else  {
                path.lineTo(xV, yV);
            }
        }
        canvas.drawPath(path, mRenderPaint);
    }

}
