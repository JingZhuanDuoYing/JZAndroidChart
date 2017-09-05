package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import android.support.annotation.NonNull;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarData;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;
import cn.jingzhuan.lib.chart.data.LineDataSet;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarChartRenderer extends AbstractDataRenderer<BarDataSet> {

    private BarData mBarDataSets;

    public BarChartRenderer(final Chart chart) {
        super(chart);

        mBarDataSets = new BarData();


        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (BarDataSet dataSet : getDataSet()) {
                    if (dataSet.isHighlightedEnable()) {

                        int index = 0;
                        if (x > mContentRect.left) {
                            index = (int) (dataSet.getEntryCount()
                                    * (x - mContentRect.left) * mViewport.width()
                                    / mContentRect.width()
                                    + mViewport.left);
                        }
                        if (index >= dataSet.getValues().size()) index = dataSet.getValues().size() - 1;
                        chart.highlightValue(new Highlight(x, y, index));
                    }
                }
            }
        });
    }

    @Override
    protected void renderDataSet(Canvas canvas) {

        for (BarDataSet barDataSet : getDataSet()) {
            if (barDataSet.isVisible()) {
                drawBarDataSet(canvas, barDataSet);
            }
        }
    }

    private void drawBarDataSet(Canvas canvas, BarDataSet barDataSet) {

        mRenderPaint.setStrokeWidth(barDataSet.getBarWidth());
        mRenderPaint.setStyle(Paint.Style.FILL);

        int valueCount = barDataSet.getEntryCount();

        float min = barDataSet.getYMin();
        float max = barDataSet.getYMax();

        float width = barDataSet.getBarWidth();
        if (barDataSet.isAutoBarWidth()) {
            width = mContentRect.width() / valueCount;
        }

        for (int i = 0; i < valueCount && i < barDataSet.getValues().size(); i++) {
            BarValue barValue = barDataSet.getEntryForIndex(i);

            if (barValue.getColor() != 0) {
                mRenderPaint.setColor(barValue.getColor());
            } else {
                mRenderPaint.setColor(barDataSet.getColor());
            }

            float x = getDrawX(i / (valueCount - 0f));

            float top;
            float bottom = calcHeight(0f, max, min);

            if (barValue.getValueCount() > 0) {

                top = calcHeight(barValue.getValues()[0], max, min);

                if (barValue.getValueCount() >= 2) bottom = calcHeight(barValue.getValues()[1], max, min);

                canvas.drawRect(x,
                        top,
                        x + width,
                        bottom, mRenderPaint);
            }
        }
    }

    private float calcHeight(float value, float max, float min) {
        return (max - value) / (max - min) * mContentRect.height();
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mRenderPaint.setColor(getHighlightColor());
        mRenderPaint.setStrokeWidth(2);

        for (Highlight highlight : highlights) {

            Canvas c = mBitmapCanvas == null ? canvas : mBitmapCanvas;
            c.drawLine(
                    highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);
        }

    }

    @Override
    public void addDataSet(BarDataSet dataSet) {
        if (dataSet == null) return;

        mBarDataSets.add(dataSet);
    }

    @Override
    public List<BarDataSet> getDataSet() {
        return mBarDataSets.getDataSets();
    }
}
