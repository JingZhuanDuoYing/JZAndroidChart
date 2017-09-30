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

        lineRenderer = new LineRenderer(chart);
        barChartRenderer = new BarChartRenderer(chart);
        candlestickChartRenderer = new CandlestickChartRenderer(chart);

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override public void onViewportChange(Viewport viewport) {
                mViewport = viewport;
                calcDataSetMinMax();
            }
        });
    }

    @Override
    protected void renderDataSet(Canvas canvas) {

        barChartRenderer.renderDataSet(canvas, getChartData().getBarChartData());
        lineRenderer.renderDataSet(canvas, getChartData().getLineChartData());
        candlestickChartRenderer.renderDataSet(canvas, getChartData().getCandlestickChartData());
    }

    @Override protected void renderDataSet(Canvas canvas, ChartData chartData) {
        // ignore
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

        combineData.add(dataSet);

        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.addDataSet((CandlestickDataSet) dataSet);
        }

        calcDataSetMinMax();
    }

    @Override public void removeDataSet(AbstractDataSet dataSet) {

        combineData.remove(dataSet);
        combineData.calcDataSetMinMax(mViewport, mContentRect);
    }

    @Override
    public void calcDataSetMinMax() {
        combineData.calcDataSetMinMax(mViewport, mContentRect);
    }

    @Override public void clearDataSet() {
        combineData.clear();

        lineRenderer.clearDataSet();
        barChartRenderer.clearDataSet();
        candlestickChartRenderer.clearDataSet();

        combineData.getBarChartData().clear();
        combineData.getLineChartData().clear();
        combineData.getCandlestickChartData().clear();

        calcDataSetMinMax();
    }

    public void cleanLineDataSet() {
        lineRenderer.clearDataSet();
        combineData.getLineChartData().clear();
    }

    public void cleanBarDataSet() {
        lineRenderer.clearDataSet();
        combineData.getBarChartData().clear();
    }

    public void cleanCandlestickDataSet() {
        candlestickChartRenderer.clearDataSet();
        combineData.getCandlestickChartData().clear();
    }

    @Override
    protected List<AbstractDataSet> getDataSet() {
        return combineData.getDataSets();
    }

    @Override public CombineData getChartData() {
        if (combineData == null) combineData = new CombineData();
        return combineData;
    }

}
