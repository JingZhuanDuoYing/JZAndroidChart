package cn.jingzhuan.lib.chart.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;

public interface IChart {

    void drawAxis(Canvas canvas);

    void drawGridLine(Canvas canvas);

    void render(Canvas canvas);

    Paint getRenderPaint();

    void drawLabels(Canvas canvas);

    void initChart();

    Highlight[] getHighlights();

    void highlightValue(Highlight highlight);

    void cleanHighlight();

    void onTouchPoint(MotionEvent e);

    void onTouchHighlight(MotionEvent e);

    int getEntryIndexByCoordinate(float x, float y);

    float getEntryCoordinateByIndex(int index);

    ChartAnimator getChartAnimator();

}
