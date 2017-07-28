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
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.value.Line;
import cn.jingzhuan.lib.chart.value.PointValue;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineRenderer extends AbstractDataRenderer<Line> {

    private CopyOnWriteArrayList<Line> mLines;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;
    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;
    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    public LineRenderer(Chart chart) {
        super(chart);

        mLines = new CopyOnWriteArrayList<>();

        chart.setOnScaleListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                Log.d("Renderer", "viewport = " + viewport);
                for (Line line : mLines) {
                    line.setViewport(viewport);
                }
            }
        });
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
    public void renderer(Canvas canvas) {

        int width = mContentRect.width();
        int height = mContentRect.height();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        for (Line line : mLines) {
            if (line.isVisible()) {
                drawDataSet(canvas, line);
            }
        }
        canvas.drawBitmap(mDrawBitmap.get(), 0, 0, mRenderPaint);
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
