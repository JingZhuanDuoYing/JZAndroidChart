package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarData;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarChartRenderer extends AbstractDataRenderer<BarDataSet, BarData> {

    private BarData mBarDataSets;

    public BarChartRenderer(Chart chart) {
        super(chart);

        mBarDataSets = new BarData();

        mRenderPaint.setStyle(Paint.Style.FILL);
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
    public void renderHighlighted(Canvas canvas, Highlight[] highlights) {

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
