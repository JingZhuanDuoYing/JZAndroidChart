package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;

import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import java.util.Collections;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.IDataSet;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.widget.BarChart;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChartRenderer extends AbstractDataRenderer {

    private BarChartRenderer barChartRenderer;
    private LineRenderer lineRenderer;
    private CandlestickChartRenderer candlestickChartRenderer;

    public CombineChartRenderer(final Chart chart) {
        super(chart);

        lineRenderer = new LineRenderer(chart);
        barChartRenderer = new BarChartRenderer(chart);
        candlestickChartRenderer = new CandlestickChartRenderer(chart);
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
    public void addDataSet(IDataSet dataSet) {

        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.addDataSet((CandlestickDataSet) dataSet);
        }

    }

    @Override
    public List<LineDataSet> getDataSet() {
        return lineRenderer.getDataSet();
    }

    public List<BarDataSet> getBarDataSet() {
        return barChartRenderer.getDataSet();
    }

}
