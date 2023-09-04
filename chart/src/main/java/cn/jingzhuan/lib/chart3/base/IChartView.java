package cn.jingzhuan.lib.chart3.base;

import android.graphics.Canvas;

public interface IChartView {

    /**
     * 画水印
     */
    void drawWaterMark(Canvas canvas);

    /**
     * 画坐标轴
     */
    void drawAxis(Canvas canvas);

    /**
     * 画坐标轴文本 (左、右、上) 底部单独处理
     */
    void drawAxisLabels(Canvas canvas);

    /**
     * 画底部坐标轴文本
     */
    void drawBottomLabels(Canvas canvas);

    /**
     * 画网格线
     */
    void drawGridLine(Canvas canvas);

    /**
     * 画十字光标
     */
    void drawHighlight(Canvas canvas);

    /**
     * 初始化
     */
    void initChart();

    /**
     * 画图表内容
     */
    void drawChart(Canvas canvas);
}
