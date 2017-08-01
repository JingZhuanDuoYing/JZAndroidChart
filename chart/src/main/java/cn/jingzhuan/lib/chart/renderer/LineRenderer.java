package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.value.Line;
import cn.jingzhuan.lib.chart.value.PointValue;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<Line> {

    private CopyOnWriteArrayList<Line> mLines;


    public LineRenderer(final Chart chart) {
        super(chart);

        mLines = new CopyOnWriteArrayList<>();

        chart.setOnScaleListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                for (Line line : mLines) {
                    line.setViewport(viewport);
                }
            }
        });

        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (Line line : mLines) {
                    if (line.isHighlightdEnable()) {
                        int index = (int) getDrawX(x) * line.getEntryCount();
                        chart.highlightValue(new Highlight(x, y, index));
                    }
                }
            }
        });
    }

    @Override
    public void renderHighlighted(Highlight[] highlights) {
        if (highlights == null) return;

        for (Highlight highlight : highlights) {
            mBitmapCanvas.drawLine(highlight.getX(), 0, highlight.getX(), mContentRect.bottom, mRenderPaint);
        }
    }

    @Override
    public void addDataSet(Line line) {
        this.mLines.add(line);
    }

    @Override
    public List<Line> getDataSet() {
        return mLines;
    }

    @Override
    protected void renderDataSet(Canvas canvas) {

//        int width = mContentRect.width();
//        int height = mContentRect.height();
//
//        if (mDrawBitmap == null
//                || (mDrawBitmap.get().getWidth() != width)
//                || (mDrawBitmap.get().getHeight() != height)) {
//
//            if (width > 0 && height > 0) {
//
//                mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, mBitmapConfig));
//                mBitmapCanvas = new Canvas(mDrawBitmap.get());
//            } else
//                return;
//        }
//
//        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        for (Line line : mLines) {
            if (line.isVisible()) {
                drawDataSet(canvas, line);
            }
        }

//        canvas.drawBitmap(mDrawBitmap.get(), 0, 0, mRenderPaint);

    }

    private void drawDataSet(Canvas canvas, Line line) {

        mRenderPaint.setStrokeWidth(line.getLineThickness());
        mRenderPaint.setColor(line.getLineColor());

        int valueCount = line.getEntryCount();

        Path path = new Path();
        path.reset();
        boolean isFirst = true;

        float min = line.getViewportYMin();
        float max = line.getViewportYMax();

        for (int i = 0; i < valueCount; i++) {
            PointValue point = line.getEntryForIndex(i);

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
