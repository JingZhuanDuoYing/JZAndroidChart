package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarData;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarChartRenderer extends AbstractDataRenderer<BarDataSet> {

    private BarData mBarDataSets;

    public BarChartRenderer(final Chart chart) {
        super(chart);

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport = viewport;
                calcDataSetMinMax();
            }
        });

        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                for (BarDataSet dataSet : getDataSet()) {
                    if (dataSet.isHighlightedVerticalEnable()) {

                        int index = getEntryIndexByCoordinate(x, y);
                        BarValue barValue = dataSet.getEntryForIndex(index);
                        chart.highlightValue(new Highlight(barValue.getX(), barValue.getY(), index));
                    }
                }
            }
        });
    }

    @Override protected void renderDataSet(Canvas canvas, ChartData<BarDataSet> chartData) {
        for (BarDataSet dataSet : chartData.getDataSets()) {
            if (dataSet.isVisible()) {
                drawBarDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
            }
        }
    }

    private void drawBarDataSet(Canvas canvas, BarDataSet barDataSet,
        float lMax, float lMin, float rMax, float rMin) {

        mRenderPaint.setStrokeWidth(barDataSet.getStrokeThickness());
        mRenderPaint.setStyle(Paint.Style.FILL);

        int valueCount = barDataSet.getEntryCount();

        float min, max;
        switch (barDataSet.getAxisDependency()) {
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

        float width = barDataSet.getBarWidth();
        int visibleCount = barDataSet.getVisibleValueCount(mViewport);
        if (barDataSet.isAutoBarWidth() && visibleCount > 0) {
            width = mContentRect.width() / visibleCount;
        }
        final float percent = 0.8f;

        for (int i = 0; i < valueCount && i < barDataSet.getValues().size(); i++) {
            BarValue barValue = barDataSet.getEntryForIndex(i);

            if (barValue.getValues().length < 1 || Float.isNaN(barValue.getValues()[0])) continue;

            if (barValue.getColor() != -2) {
                mRenderPaint.setColor(barValue.getColor());
            } else {
                mRenderPaint.setColor(barDataSet.getColor());
            }

            float x = getDrawX(i / (valueCount + 0f));

            float top;
            float bottom = calcHeight(0, max, min);

            if (barValue.getValueCount() > 0) {

                top = calcHeight(barValue.getValues()[0], max, min);
                if (barValue.getValueCount() > 1) bottom = calcHeight(barValue.getValues()[1], max, min);

                barValue.setX(x + width * 0.5f);
                barValue.setY(top);

                mRenderPaint.setStyle(barValue.getPaintStyle());

                canvas.drawRect(x + width * (1 - percent) * 0.5f,
                        top,
                        x + width * percent,
                        bottom, mRenderPaint);
            }
        }
        mRenderPaint.setStyle(Paint.Style.FILL);
    }

    private float calcHeight(float value, float max, float min) {
        return (max - value) / (max - min) * mContentRect.height();
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mRenderPaint.setColor(getHighlightColor());
        mRenderPaint.setStrokeWidth(2);
        mRenderPaint.setStyle(Paint.Style.STROKE);
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
        }

        mRenderPaint.setPathEffect(null);
    }

    @Override public void removeDataSet(BarDataSet dataSet) {
        if (dataSet == null) return;
        mBarDataSets.remove(dataSet);
        calcDataSetMinMax();

    }

    @Override public void clearDataSet() {
        mBarDataSets.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<BarDataSet> getDataSet() {
        return mBarDataSets.getDataSets();
    }

    @Override public BarData getChartData() {
        if (mBarDataSets == null) mBarDataSets = new BarData();
        return mBarDataSets;
    }

}
