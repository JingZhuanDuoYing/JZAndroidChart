package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;

import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;

public class DrawLine {

    private BaseDraw straightLineDraw;

    private BaseDraw segmentDraw;

    public DrawLine(final Chart chart) {
        initDraw(chart);
    }

    private void initDraw(final Chart chart) {
        if (straightLineDraw == null) straightLineDraw = new StraightLineDraw(chart);

        if (segmentDraw == null) segmentDraw = new SegmentDraw(chart);

    }

    public void drawDataSet(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin) {
        int type = dataSet.getLineType();
        if (type == DrawLineType.ltStraightLine.ordinal()) {
            straightLineDraw.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
        } else if (type == DrawLineType.ltSegment.ordinal()) {
            segmentDraw.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
        }
    }
}
