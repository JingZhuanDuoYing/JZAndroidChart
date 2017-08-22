package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;

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

    public CombineChartRenderer(final Chart chart) {
        super(chart);

        lineRenderer = new LineRenderer(chart);
        barChartRenderer = new BarChartRenderer(chart);

    }

    @Override
    protected void renderDataSet(Canvas canvas) {
        barChartRenderer.renderDataSet(canvas);
        lineRenderer.renderDataSet(canvas);
    }

    @Override
    public void renderHighlighted(Canvas canvas, Highlight[] highlights) {
        lineRenderer.renderHighlighted(canvas, highlights);
        barChartRenderer.renderHighlighted(canvas, highlights);
    }

    @Override
    public void addDataSet(IDataSet dataSet) {

        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
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
