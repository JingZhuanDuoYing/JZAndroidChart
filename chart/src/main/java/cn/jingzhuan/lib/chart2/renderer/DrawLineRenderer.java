package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.DrawLineData;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.drawline.DrawLine;

/**
 * @since 2023-08-28
 * 画线工具Renderer
 */
public class DrawLineRenderer extends AbstractDataRenderer<DrawLineDataSet> {

    private DrawLineData chartData;

    /**
     * 当前chart
     */
    private final Chart chart;

    private DrawLine drawLine;

    private ChartData<CandlestickDataSet> candlestickChartData;

    public DrawLineRenderer(Chart chart) {
        super(chart);
        this.chart = chart;
        drawLine = new DrawLine(chart);
    }


    public void setCandlestickChartData(ChartData<CandlestickDataSet> candlestickChartData) {
        this.candlestickChartData = candlestickChartData;
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<DrawLineDataSet> chartData) {
        for (DrawLineDataSet dataSet : chartData.getDataSets()) {
            renderDataSet(canvas, chartData, dataSet);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<DrawLineDataSet> chartData, DrawLineDataSet dataSet) {
        if (dataSet.isVisible()) {
            drawDataSet(canvas, dataSet);
        }
    }

    private void drawDataSet(Canvas canvas, DrawLineDataSet dataSet) {
        if (!candlestickChartData.getDataSets().isEmpty()) {
            float lMax = candlestickChartData.getLeftMax();
            float lMin = candlestickChartData.getLeftMin();
            CandlestickDataSet candlestickDataSet = candlestickChartData.getDataSets().get(0);
            drawLine.drawDataSet(canvas, dataSet, candlestickDataSet, lMax, lMin);
        }
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull @NotNull Highlight[] highlights) {
    }

    @Override
    public void removeDataSet(DrawLineDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    @Override
    protected List<DrawLineDataSet> getDataSet() {
        return chartData.getDataSets();
    }

    @Override
    public ChartData<DrawLineDataSet> getChartData() {
        if (chartData == null)
            chartData = new DrawLineData();
        return chartData;
    }

}

