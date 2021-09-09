package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import cn.jingzhuan.lib.chart.Viewport;;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.utils.RequestDataType;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import java.util.List;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChartRenderer extends AbstractDataRenderer {

    protected BarChartRenderer barChartRenderer;
    protected LineRenderer lineRenderer;
    protected CandlestickChartRenderer candlestickChartRenderer;
    protected ScatterChartRenderer scatterChartRenderer;
    public RangeRenderer rangeRenderer;//K线区域选择的renderer
    protected PointLineRenderer pointLineRenderer;
    private Chart chart;

    private Boolean showRange = false;
    private CombineData combineData;
    private  int lastDataSize = 0;

    public CombineChartRenderer(final Chart chart) {
        super(chart);

        lineRenderer = initLineRenderer(chart);
        barChartRenderer = initBarChartRenderer(chart);
        candlestickChartRenderer = initCandlestickChartRenderer(chart);
        scatterChartRenderer = initScatterChartRenderer(chart);
        rangeRenderer = initRangeChartRenderer(chart);
        pointLineRenderer = initPointLineRenderer(chart);
        this.chart = chart;

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });
        showRange = chart.getRangeEnable();
    }

    private PointLineRenderer initPointLineRenderer(Chart chart) {
        return new PointLineRenderer(chart);
    }

    private RangeRenderer initRangeChartRenderer(Chart chart) {
        return new RangeRenderer(chart);
    }

    @NotNull
    private ScatterChartRenderer initScatterChartRenderer(Chart chart) {
        return new ScatterChartRenderer(chart);
    }

    @NotNull
    protected CandlestickChartRenderer initCandlestickChartRenderer(Chart chart) {
        return new CandlestickChartRenderer(chart);
    }

    @NotNull
    protected BarChartRenderer initBarChartRenderer(Chart chart) {
        return new BarChartRenderer(chart);
    }

    @NotNull
    protected LineRenderer initLineRenderer(Chart chart) {
        return new LineRenderer(chart);
    }

    @Override
    protected void renderDataSet(Canvas canvas) {
        barChartRenderer.renderDataSet(canvas, getChartData().getBarChartData());
        candlestickChartRenderer.renderDataSet(canvas, getChartData().getCandlestickChartData());
        lineRenderer.renderDataSet(canvas, getChartData().getLineChartData());
        scatterChartRenderer.renderDataSet(canvas, getChartData().getScatterChartData());
        if (showRange) rangeRenderer.renderDataSet(canvas, getChartData().getCandlestickChartData());
        pointLineRenderer.renderDataSet(canvas,getChartData().getPointLineChartData());

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
        rangeRenderer.setHighlightColor(highlightColor);
        pointLineRenderer.setHighlightColor(highlightColor);
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
        if (scatterChartRenderer.getDataSet() != null && !scatterChartRenderer.getDataSet().isEmpty()) {
            scatterChartRenderer.renderHighlighted(canvas, highlights);
        }
        if (rangeRenderer.getDataSet() != null && !rangeRenderer.getDataSet().isEmpty()) {
            rangeRenderer.renderHighlighted(canvas, highlights);
        }

        if (pointLineRenderer.getDataSet() != null && !pointLineRenderer.getDataSet().isEmpty()) {
            pointLineRenderer.renderHighlighted(canvas, highlights);
        }
    }

    @Override
    public void addDataSet(AbstractDataSet dataSet) {
        getChartData().add(dataSet);
        int dataSize = dataSet.getValues().size();
        if (lastDataSize== 0) lastDataSize = dataSize;
        if (RequestDataType.DATA_TYPE == RequestDataType.DATA_TYPE_RANGE){
            if (lastDataSize != dataSize){
                int from = Math.round(mViewport.left * lastDataSize ) + (dataSize - lastDataSize);
                int to = Math.round(mViewport.right * lastDataSize )+ (dataSize - lastDataSize);
                mViewport.left = from / (float)dataSize;
                mViewport.right = to / (float)dataSize;
                chart.setCurrentViewport(mViewport);
                lastDataSize = dataSize;
            }
        }



        if (dataSet instanceof LineDataSet) {
            lineRenderer.addDataSet((LineDataSet) dataSet);
        } else if (dataSet instanceof BarDataSet) {
            barChartRenderer.addDataSet((BarDataSet) dataSet);
        } else if (dataSet instanceof CandlestickDataSet) {
            candlestickChartRenderer.addDataSet((CandlestickDataSet) dataSet);
            rangeRenderer.addDataSet((CandlestickDataSet) dataSet);
        } else if (dataSet instanceof ScatterDataSet) {
            scatterChartRenderer.addDataSet((ScatterDataSet) dataSet);
        } else if (dataSet instanceof PointLineDataSet){
            pointLineRenderer.addDataSet((PointLineDataSet)dataSet);
        }

        calcDataSetMinMax();
    }

    @Override public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        if (defaultVisibleEntryCount <= 0) return;

        barChartRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        lineRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        scatterChartRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        candlestickChartRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        rangeRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        pointLineRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
    }

    @Override public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        if (maxVisibleEntryCount <= 0) return;

        barChartRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        lineRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        scatterChartRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        candlestickChartRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        rangeRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        pointLineRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    @Override public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        if (minVisibleEntryCount <= 0) return;

        barChartRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        lineRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        scatterChartRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        candlestickChartRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        rangeRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        pointLineRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
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
        cleanPointLineDataSet();

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

    public void cleanPointLineDataSet(){
        pointLineRenderer.clearDataSet();
        getChartData().getPointLineChartData().clear();
    }

    @Override
    protected List<AbstractDataSet> getDataSet() {
        return combineData.getDataSets();
    }

    @Override public CombineData getChartData() {
        if (combineData == null) combineData = new CombineData();
        return combineData;
    }

    @Override public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        super.enableHighlightDashPathEffect(intervals, phase);
        this.lineRenderer.enableHighlightDashPathEffect(intervals, phase);
        this.barChartRenderer.enableHighlightDashPathEffect(intervals, phase);
        this.candlestickChartRenderer.enableHighlightDashPathEffect(intervals, phase);
        this.scatterChartRenderer.enableHighlightDashPathEffect(intervals, phase);
        this.rangeRenderer.enableHighlightDashPathEffect(intervals, phase);
        this.pointLineRenderer.enableHighlightDashPathEffect(intervals, phase);
    }

    @Override public void setTypeface(Typeface tf) {
        this.lineRenderer.setTypeface(tf);
        this.barChartRenderer.setTypeface(tf);
        this.candlestickChartRenderer.setTypeface(tf);
        this.scatterChartRenderer.setTypeface(tf);
        this.rangeRenderer.setTypeface(tf);
        this.pointLineRenderer.setTypeface(tf);
    }

    @Override public int getEntryIndexByCoordinate(float x, float y) {
        if (!getChartData().getCandlestickData().isEmpty()) {
            return candlestickChartRenderer.getEntryIndexByCoordinate(x, y);
        }
        if (!getChartData().getLineData().isEmpty()) {
            return lineRenderer.getEntryIndexByCoordinate(x, y);
        }
        if (!getChartData().getBarData().isEmpty()) {
            return barChartRenderer.getEntryIndexByCoordinate(x, y);
        }
        if (!getChartData().getScatterData().isEmpty()) {
            return scatterChartRenderer.getEntryIndexByCoordinate(x, y);
        }
        if (!getChartData().getPointLineData().isEmpty()) {
            return pointLineRenderer.getEntryIndexByCoordinate(x, y);
        }

        return super.getEntryIndexByCoordinate(x, y);
    }

    public Boolean getShowRange() {
        return showRange;
    }

    public void setShowRange(Boolean showRange) {
        this.showRange = showRange;
    }
}
