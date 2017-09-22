package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;

import android.support.annotation.NonNull;
import android.util.Log;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import java.util.Collections;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.LineDataSet;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChartRenderer extends AbstractDataRenderer {

    private BarChartRenderer barChartRenderer;
    private LineRenderer lineRenderer;
    private CandlestickChartRenderer candlestickChartRenderer;

    private CombineData combineData;

    public CombineChartRenderer(final Chart chart) {
        super(chart);

        combineData = new CombineData();

        lineRenderer = new LineRenderer(chart);
        barChartRenderer = new BarChartRenderer(chart);
        candlestickChartRenderer = new CandlestickChartRenderer(chart);

        chart.setOnViewportChangeListener(new OnViewportChangeListener() {
            @Override public void onViewportChange(Viewport viewport) {
                //resetMaxMin();

                //lineRenderer.calcMaxMin(viewport);
                //barChartRenderer.calcMaxMin();
                //candlestickChartRenderer.calcMaxMin();
                //
//                for (LineDataSet lineDataSet : combineData.getLineData()) {
//                    lineDataSet.onViewportChange(viewport);
//                }
                //for (BarDataSet barDataSet : combineData.getBarData()) {
                //    barDataSet.calcMinMax();
                //}
//                for (CandlestickDataSet candlestickDataSet : combineData.getCandlestickData()) {
//                    candlestickDataSet.onViewportChange(viewport, mContentRect);
//                }
                //if (!lineRenderer.getDataSet().isEmpty()) {
                //    setMax(Math.max(lineRenderer.getMax(), getMax()));
                //    setMin(Math.min(lineRenderer.getMin(), getMin()));
                //}
                //if (!barChartRenderer.getDataSet().isEmpty()) {
                //    setMax(Math.max(barChartRenderer.getMax(), getMax()));
                //    setMin(Math.min(barChartRenderer.getMin(), getMin()));
                //}
                //if (!candlestickChartRenderer.getDataSet().isEmpty()) {
                //    setMax(Math.max(candlestickChartRenderer.getMax(), getMax()));
                //    setMin(Math.min(candlestickChartRenderer.getMin(), getMin()));
                //}

                combineData.calcDataSetMinMax(viewport, mContentRect);
                combineData.calcMinMax();
                //chart.getAxisLeft().setYMax(getMax());
                //chart.getAxisLeft().setYMin(getMin());

                Log.d("ChartRenderer",  "calcMinMax, " + combineData.getLeftMin() + ", " + combineData.getLeftMax());
                Log.d("ChartRenderer",  "viewport = " + viewport);

            }
        });
    }

    @Override
    protected void renderDataSet(Canvas canvas) {

        barChartRenderer.renderDataSet(canvas);
        lineRenderer.renderDataSet(canvas);
        candlestickChartRenderer.renderDataSet(canvas);
    }

    @Override
    public void setHighlightColor(int highlightColor) {
        super.setHighlightColor(highlightColor);
        lineRenderer.setHighlightColor(highlightColor);
        barChartRenderer.setHighlightColor(highlightColor);
        candlestickChartRenderer.setHighlightColor(highlightColor);
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
        if (lineRenderer.getDataSet() != null && !lineRenderer.getDataSet().isEmpty()) {
            lineRenderer.renderHighlighted(canvas, highlights);
        }
        if (barChartRenderer.getDataSet() != null && !barChartRenderer.getDataSet().isEmpty()) {
            barChartRenderer.renderHighlighted(canvas, highlights);
        }
        if (candlestickChartRenderer.getDataSet() != null && !candlestickChartRenderer.getDataSet().isEmpty()) {
            candlestickChartRenderer.renderHighlighted(canvas, highlights);
        }
    }

    @Override
    public void addDataSet(AbstractDataSet dataSet) {

//        combineData.calcMinMax();
        combineData.add(dataSet);

        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.addDataSet((CandlestickDataSet) dataSet);
            Log.d("calcCandlestick", "instanceof CandlestickDataSet");
        }

    }

    @Override public void removeDataSet(AbstractDataSet dataSet) {

        combineData.remove(dataSet);

        if (dataSet instanceof LineDataSet) {
            lineRenderer.removeDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.removeDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.removeDataSet((CandlestickDataSet) dataSet);
        }
    }

    @Override public void clearDataSet() {
        combineData.clear();

        lineRenderer.clearDataSet();
        barChartRenderer.clearDataSet();
        candlestickChartRenderer.clearDataSet();
    }

    @Override
    protected List<AbstractDataSet> getDataSet() {
        return combineData.getDataSets();
    }

    @Override public CombineData getChartData() {
        return combineData;
    }

//    public List<BarDataSet> getBarDataSet() {
//        return barChartRenderer.getDataSet();
//    }

//    public List<CandlestickDataSet> getCandlestickDataSet() {
//        return candlestickChartRenderer.getDataSet();
//    }

}
