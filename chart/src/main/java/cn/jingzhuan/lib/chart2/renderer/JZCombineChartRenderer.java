package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextValue;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart.data.TreeValue;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.base.JZChart;
import cn.jingzhuan.lib.chart2.draw.BaseDraw;
import cn.jingzhuan.lib.chart2.draw.JZBarDraw;
import cn.jingzhuan.lib.chart2.draw.JZCandlestickDraw;
import cn.jingzhuan.lib.chart2.draw.JZLineDraw;
import cn.jingzhuan.lib.chart2.draw.JZLineScatterDraw;
import cn.jingzhuan.lib.chart2.draw.JZLineScatterTextDraw;
import cn.jingzhuan.lib.chart2.draw.JZPointLineDraw;
import cn.jingzhuan.lib.chart2.draw.JZTreeDraw;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZCombineChartRenderer extends AbstractChartRenderer {

    protected BaseDraw<TreeValue, TreeDataSet> treeDraw;

    protected BaseDraw<BarValue, BarDataSet> barDraw;

    protected BaseDraw<PointValue, LineDataSet> lineDraw;

    protected BaseDraw<CandlestickValue, CandlestickDataSet> candlestickDraw;

    protected BaseDraw<ScatterValue, ScatterDataSet> scatterDraw;

    protected BaseDraw<PointValue, PointLineDataSet> pointLineDraw;

    protected BaseDraw<ScatterTextValue, ScatterTextDataSet> scatterTextDraw;

    public RangeRenderer rangeRenderer;

    private final JZChart mChart;

    public JZCombineChartRenderer(final Chart chart) {
        super(chart);
        initDraw(chart);
        rangeRenderer = initRangeChartRenderer(chart);
        this.mChart = (JZChart) chart;
        getChartData().setChart(chart);
    }

    private void initDraw(final Chart chart) {
        treeDraw = new JZTreeDraw(chart);
        lineDraw = new JZLineDraw(chart);
        barDraw = new JZBarDraw(chart);
        candlestickDraw = new JZCandlestickDraw(chart);
        scatterDraw = new JZLineScatterDraw(chart);
        pointLineDraw = new JZPointLineDraw(chart);
        scatterTextDraw = new JZLineScatterTextDraw(chart);
    }

    private RangeRenderer initRangeChartRenderer(Chart chart) {
        return new RangeRenderer(chart);
    }

    @Override
    protected void renderDataSet(Canvas canvas) {
        CombineData combineData = getChartData();
        List<AbstractDataSet<?>> sortedDataSets = combineData.getAllDataSet();
        List<AbstractDataSet<?>> candlestickDataSets = new ArrayList<>();
        // 按云引擎指定顺序绘制dataSet
        for (int i = 0; i < sortedDataSets.size(); i++) {
            AbstractDataSet<?> dataSet = sortedDataSets.get(i);
            if (dataSet instanceof TreeDataSet) {
                treeDraw.drawDataSet(canvas, combineData.getTreeChartData(), (TreeDataSet) dataSet);
            }

            if (dataSet instanceof CandlestickDataSet) {
                candlestickDraw.drawDataSet(canvas, combineData.getCandlestickChartData(), (CandlestickDataSet) dataSet);
                candlestickDataSets.add(dataSet);
            }

            if (dataSet instanceof LineDataSet) {
                lineDraw.drawDataSet(canvas, combineData.getLineChartData(), (LineDataSet) dataSet);
            }

            if (dataSet instanceof BarDataSet) {
                barDraw.drawDataSet(canvas, combineData.getBarChartData(), (BarDataSet) dataSet);
            }

            if (dataSet instanceof ScatterDataSet) {
                scatterDraw.drawDataSet(canvas, combineData.getScatterChartData(), (ScatterDataSet) dataSet);
            }

            if (dataSet instanceof PointLineDataSet) {
                pointLineDraw.drawDataSet(canvas, combineData.getPointLineChartData(), (PointLineDataSet) dataSet);
            }

            if (dataSet instanceof ScatterTextDataSet) {
                scatterTextDraw.drawDataSet(canvas, combineData.getScatterTextChartData(), (ScatterTextDataSet) dataSet);
            }
        }

        if (mChart.getRangeEnable()) {
            // k线叠加时 区间统计只画一次
            if (!candlestickDataSets.isEmpty()) {
                rangeRenderer.renderDataSet(canvas, combineData.getCandlestickChartData(), (CandlestickDataSet) candlestickDataSets.get(0));
            }
        }

    }


    @Override
    public void setHighlightColor(int highlightColor) {
        super.setHighlightColor(highlightColor);
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
        if (rangeRenderer.getDataSet() != null && !rangeRenderer.getDataSet().isEmpty()) {
            rangeRenderer.renderHighlighted(canvas, highlights);
        }
        mRenderPaint.setColor(getHighlightColor());
        mRenderPaint.setStrokeWidth(3);
        mRenderPaint.setStyle(Paint.Style.FILL);
        if (mHighlightedDashPathEffect != null) {
            mRenderPaint.setPathEffect(mHighlightedDashPathEffect);
        }

        for (Highlight highlight : highlights) {
            if (mChart.isHighlightedVerticalEnable()) {
                canvas.drawLine(highlight.getX(),
                        mContentRect.top,
                        highlight.getX(),
                        mContentRect.bottom,
                        mRenderPaint);
            }

            if (mChart.isHighlightedHorizontalEnable()) {
                canvas.drawLine(mContentRect.left,
                        highlight.getY(),
                        mContentRect.right,
                        highlight.getY(),
                        mRenderPaint);
            }
        }

        mRenderPaint.setPathEffect(null);
    }

    @Override
    public void addDataSet(AbstractDataSet dataSet) {
        if (dataSet == null) return;
        getChartData().add(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        if (defaultVisibleEntryCount <= 0) return;
        rangeRenderer.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
        super.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
    }

    @Override
    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        if (maxVisibleEntryCount <= 0) return;
        rangeRenderer.setMaxVisibleEntryCount(maxVisibleEntryCount);
        super.setMaxVisibleEntryCount(maxVisibleEntryCount);
    }

    @Override
    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        if (minVisibleEntryCount <= 0) return;
        rangeRenderer.setMinVisibleEntryCount(minVisibleEntryCount);
        super.setMinVisibleEntryCount(minVisibleEntryCount);
    }

    @Override
    public void removeDataSet(AbstractDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();

        cleanTreeDataSet();
        cleanLineDataSet();
        cleanBarDataSet();
        cleanCandlestickDataSet();
        cleanScatterDataSet();
        cleanPointLineDataSet();
        cleanScatterTextDataSet();
        cleanRangeDataSet();

        calcDataSetMinMax();
    }

    public void cleanLineDataSet() {
        getChartData().getLineChartData().clear();
    }

    public void cleanBarDataSet() {
        getChartData().getBarChartData().clear();
    }

    public void cleanCandlestickDataSet() {
        getChartData().getCandlestickChartData().clear();
    }

    public void cleanScatterDataSet() {
        getChartData().getScatterChartData().clear();
    }

    public void cleanPointLineDataSet() {
        getChartData().getPointLineChartData().clear();
    }

    public void cleanScatterTextDataSet() {
        getChartData().getScatterTextChartData().clear();
    }

    public void cleanTreeDataSet() {
        getChartData().getTreeChartData().clear();
    }

    public void cleanRangeDataSet() {
        rangeRenderer.clearDataSet();
    }

    @Override
    protected List<AbstractDataSet> getDataSet() {
        return mChart.getAdapter().getDataSets();
    }

    @Override
    public CombineData getChartData() {
        return (CombineData) mChart.getAdapter().getData();
    }

    @Override
    public void enableHighlightDashPathEffect(float[] intervals, float phase) {
        super.enableHighlightDashPathEffect(intervals, phase);
    }

    @Override
    public void setTypeface(Typeface tf) {
       super.setTypeface(tf);
    }

    @Override
    public int getEntryIndexByCoordinate(float x, float y) {
        return super.getEntryIndexByCoordinate(x, y);
    }

    @Override
    public float getEntryCoordinateByIndex(int index) {
        return super.getEntryCoordinateByIndex(index);
    }

    @Override
    public void setHighlightThickness(float highlightThickness) {
        super.setHighlightThickness(highlightThickness);
    }

    @Override
    public boolean isFullSupport() {
        JZCandlestickDraw draw = (JZCandlestickDraw) candlestickDraw;
        if (draw.getDataSets() == null || draw.getDataSets().isEmpty()) return true;
        CandlestickDataSet dataSet = draw.getDataSets().get(0);
        float candleWidth = dataSet.getCandleWidth();

        if (dataSet.isAutoWidth()) {
            candleWidth = mContentRect.width() / dataSet.getVisibleRange(mViewport);
        }
        return candleWidth * dataSet.getRealEntryCount() >= mContentRect.width();
    }
}
