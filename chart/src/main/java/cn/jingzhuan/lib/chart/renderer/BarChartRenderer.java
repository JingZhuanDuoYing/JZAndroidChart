package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.value.BarDataSet;
import cn.jingzhuan.lib.chart.value.BarValue;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarChartRenderer extends AbstractDataRenderer<BarDataSet> {

    private CopyOnWriteArrayList<BarDataSet> mBarDataSets;


    public BarChartRenderer(Chart chart) {
        super(chart);

        mBarDataSets = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void renderDataSet(Canvas canvas) {
        Log.d("drawBarDataSet", "renderDataSet");

        for (BarDataSet barDataSet : getDataSet()) {
            if (barDataSet.isVisible()) {
                drawBarDataSet(canvas, barDataSet);
            }
        }
    }

    private void drawBarDataSet(Canvas canvas, BarDataSet barDataSet) {

        mRenderPaint.setStrokeWidth(barDataSet.getBarWidth());
        mRenderPaint.setColor(barDataSet.getColor());

        int valueCount = barDataSet.getEntryCount();

        float min = barDataSet.getYMin();
        float max = barDataSet.getYMax();

        float width = barDataSet.getBarWidth();

        BarValue barValue;
        for (int i = 0; i < valueCount; i++) {
            barValue = barDataSet.getEntryForIndex(i);

            float x = getDrawX((i) / (valueCount - 0f));

            float top;
            float bottom = mContentRect.bottom;

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
    public void renderHighlighted(Highlight[] highlights) {

    }

    @Override
    public void addDataSet(BarDataSet dataSet) {

        if (dataSet == null) return;

        if (mBarDataSets == null) {
            mBarDataSets = new CopyOnWriteArrayList<>();
        }

        mBarDataSets.add(dataSet);
    }

    @Override
    public List<BarDataSet> getDataSet() {
        return mBarDataSets;
    }
}
