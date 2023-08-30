package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.PointF;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.DrawLineData;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.drawline.BaseDraw;
import cn.jingzhuan.lib.chart2.drawline.DrawLineTouchState;
import cn.jingzhuan.lib.chart2.drawline.DrawLineType;
import cn.jingzhuan.lib.chart2.drawline.OnDrawLineTouchListener;
import cn.jingzhuan.lib.chart2.drawline.SegmentDraw;
import cn.jingzhuan.lib.chart2.drawline.StraightLineDraw;

/**
 * @since 2023-08-28
 * 画线工具Renderer
 */
public class DrawLineRenderer extends AbstractDataRenderer<DrawLineDataSet> {

    private DrawLineData chartData;

    private ChartData<CandlestickDataSet> candlestickChartData;

    private final Map<Integer, BaseDraw> drawMap = new HashMap<>();


    public DrawLineRenderer(Chart chart) {
        super(chart);
        initDraw(chart);
        initListener(chart);
    }

    private void initListener(final Chart chart) {
        chart.setOnDrawLineTouchListener(new OnDrawLineTouchListener() {
            @Override
            public void onTouch(DrawLineTouchState state, PointF point, int type) {
                List<DrawLineDataSet> dataSets = getDataSet();
                for (DrawLineDataSet dataSet : dataSets) {
                    int drawLineType = dataSet.getLineType();
                    BaseDraw draw = drawMap.get(drawLineType);
                    if (drawLineType == type) {
                        if (draw != null) draw.onTouch(state, point);
                    } else {
                        if (draw != null) draw.onTouch(DrawLineTouchState.none, point);
                    }
                }
                chart.postInvalidate();
            }
        });
    }

    private void initDraw(final Chart chart) {
        drawMap.put(DrawLineType.ltStraightLine.ordinal(), new StraightLineDraw(chart));

        drawMap.put(DrawLineType.ltSegment.ordinal(), new SegmentDraw(chart));
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
            int type = dataSet.getLineType();
            BaseDraw draw = drawMap.get(type);
            if (draw != null) {
                draw.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
            }
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

