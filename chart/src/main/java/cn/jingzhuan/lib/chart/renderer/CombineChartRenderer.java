package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;

import android.support.annotation.NonNull;
import android.util.Log;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
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
    private ScatterChartRenderer scatterChartRenderer;

    private CombineData combineData;

    public CombineChartRenderer(final Chart chart) {
        super(chart);

        lineRenderer = new LineRenderer(chart);
        barChartRenderer = new BarChartRenderer(chart);
        candlestickChartRenderer = new CandlestickChartRenderer(chart);
        scatterChartRenderer = new ScatterChartRenderer(chart);

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
        candlestickChartRenderer.renderDataSet(canvas, getChartData().getCandlestickChartData());
        lineRenderer.renderDataSet(canvas, getChartData().getLineChartData());
        scatterChartRenderer.renderDataSet(canvas, getChartData().getScatterChartData());
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
        scatterChartRenderer.setHighlightColor(highlightColor);
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        Canvas c = mBitmapCanvas == null ? canvas : mBitmapCanvas;

        if (lineRenderer.getDataSet() != null && !lineRenderer.getDataSet().isEmpty()) {
            lineRenderer.renderHighlighted(c, highlights);
        }
        if (barChartRenderer.getDataSet() != null && !barChartRenderer.getDataSet().isEmpty()) {
            barChartRenderer.renderHighlighted(c, highlights);
        }
        if (candlestickChartRenderer.getDataSet() != null && !candlestickChartRenderer.getDataSet().isEmpty()) {
            candlestickChartRenderer.renderHighlighted(c, highlights);
        }
        if (scatterChartRenderer.getDataSet() != null && !scatterChartRenderer.getDataSet().isEmpty()) {
            scatterChartRenderer.renderHighlighted(c, highlights);
        }
    }

    @Override
    public void addDataSet(AbstractDataSet dataSet) {

        getChartData().add(dataSet);

        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.addDataSet((CandlestickDataSet) dataSet);
        } else if (dataSet instanceof ScatterDataSet) {
            scatterChartRenderer.addDataSet((ScatterDataSet) dataSet);
        }

        calcDataSetMinMax();
    }

    @Override public void removeDataSet(AbstractDataSet dataSet) {

        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override public void clearDataSet() {
        getChartData().clear();

        cleanLineDataSet();
        cleanBarDataSet();
        cleanCandlestickDataSet();
        cleanScatterDataSet();

        calcDataSetMinMax();
    }

    public void cleanLineDataSet() {
        lineRenderer.clearDataSet();
        getChartData().getLineChartData().clear();
    }

    public void cleanBarDataSet() {
        barChartRenderer.clearDataSet();
        getChartData().getBarChartData().clear();
    }

    public void cleanCandlestickDataSet() {
        candlestickChartRenderer.clearDataSet();
        getChartData().getCandlestickChartData().clear();
    }
    public void cleanScatterDataSet() {
        scatterChartRenderer.clearDataSet();
        getChartData().getScatterChartData().clear();
    }

    @Override
    protected List<AbstractDataSet> getDataSet() {
        return combineData.getDataSets();
    }

    @Override public CombineData getChartData() {
        if (combineData == null) combineData = new CombineData();
        return combineData;
    }

    @Override public void enableDashPathEffect(float[] intervals, float phase) {
        super.enableDashPathEffect(intervals, phase);
        this.lineRenderer.enableDashPathEffect(intervals, phase);
        this.barChartRenderer.enableDashPathEffect(intervals, phase);
        this.candlestickChartRenderer.enableDashPathEffect(intervals, phase);
        this.scatterChartRenderer.enableDashPathEffect(intervals, phase);
    }
}
