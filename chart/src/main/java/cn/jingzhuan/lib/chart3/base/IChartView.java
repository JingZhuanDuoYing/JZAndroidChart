package cn.jingzhuan.lib.chart3.base;

import android.graphics.Canvas;

public interface IChartView {


    /**
     * 画水印
     */
    void drawWaterMark(Canvas canvas);

    /**
     * 画网格线
     */
    void drawGridLine(Canvas canvas);

    /**
     * 画十字光标
     */
    void drawCrossWire();
}
