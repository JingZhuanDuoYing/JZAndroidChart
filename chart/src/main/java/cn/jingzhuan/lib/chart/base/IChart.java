package cn.jingzhuan.lib.chart.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;

public interface IChart {

    /**
     * 画坐标轴
     */
    void drawAxis(Canvas canvas);

    /**
     * 画坐标轴刻度文本
     */
    void drawLabels(Canvas canvas);

    /**
     * 画网格线
     */
    void drawGridLine(Canvas canvas);

    /**
     * 画水印
     */
    void drawWatermark(Canvas canvas);

    void initChart();

    void render(Canvas canvas);

    Paint getRenderPaint();

    ChartAnimator getChartAnimator();

    Highlight[] getHighlights();

    void highlightValue(Highlight highlight);

    void cleanHighlight();

    void onTouchPoint(MotionEvent e);

    void onTouchHighlight(MotionEvent e);

    /**
     * 通过坐标获取下标
     */
    int getEntryIndexByCoordinate(float x, float y);

    /**
     * 通过下标获取X坐标
     */
    float getEntryCoordinateByIndex(int index);

    int getVisibleCount(Viewport viewport);

    int getMaxVisibleEntryCount();

    int getMinVisibleEntryCount();

}
