package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;

import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @since 2023-08-29
 * 画直线
 */
public class StraightLineDraw extends BaseDraw {

    public StraightLineDraw(final Chart chart) {
        super(chart);
    }

    @Override
    public void onDraw(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin) {
        super.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
    }
}
