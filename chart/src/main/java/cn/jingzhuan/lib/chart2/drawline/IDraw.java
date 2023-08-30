package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;
import android.graphics.PointF;

import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;

/**
 * @since 2023-08-29
 */
public interface IDraw {

    void onDraw(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin);

    void onTouch(DrawLineTouchState state, PointF point);

    void drawTypeShape(Canvas canvas);

}
