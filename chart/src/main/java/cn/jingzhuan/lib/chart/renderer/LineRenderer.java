package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.IDataSet;
import cn.jingzhuan.lib.chart.data.LineData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<LineDataSet, LineData> {

    private LineData lineData;

    public LineRenderer(final Chart chart) {
        super(chart);

        lineData = new LineData();

        chart.setOnScaleListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                for (LineDataSet line : getDataSet()) {
                    line.setViewport(viewport);
                }
            }
        });

        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (LineDataSet line : getDataSet()) {
                    if (line.isHighlightedEnable()) {
                        int index = (int) getDrawX(x) * line.getEntryCount();
                        chart.highlightValue(new Highlight(x, y, index));
                    }
                }
            }
        });
    }

    @Override
    public void renderHighlighted(Canvas canvas, Highlight[] highlights) {

        Log.d("LineRenderer", "highlights = " + highlights + " bitmap = " + mDrawBitmap);

        if (highlights == null) return;

        for (Highlight highlight : highlights) {
            (mBitmapCanvas == null ? canvas : mBitmapCanvas).drawLine(
                    highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);
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
