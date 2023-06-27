package cn.jingzhuan.lib.chart2.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.component.Highlight;

public interface IChart {

    /**
     * 初始化 Renderer、Animator
     */
    void initChart();

    /**
     * 画坐标轴
     */
    void drawAxis(Canvas canvas);

    /**
     * 画坐标轴文本
     */
    void drawLabels(Canvas canvas);

    /**
     * 画网格线
     */
    void drawGridLine(Canvas canvas);

    /**
     * 画水印
     */
    void drawWaterMark(Canvas canvas);

    /**
     * 渲染图表
     */
    void render(Canvas canvas);

    /**
     * 获取当前Renderer的Painter
     */
    Paint getRenderPaint();

    /**
     * 获取当前图表设置的背景颜色
     */
    int getBackgroundColor();

    /**
     * 十字光标选中 信息更新
     */
    void highlightValue(Highlight highlight);

    /**
     * 清除十字光标
     */
    void cleanHighlight();

    /**
     * 当前K线是否占满绘制区域
     */
    boolean getIfKlineFullRect();

    /**
     * 根据当前点获取下标
     */
    int getEntryIndexByCoordinate(float x, float y);

    void onTouchPoint(MotionEvent e);

    void onTouchHighlight(MotionEvent e);

    boolean getDrawLabelsInBottom();
}
