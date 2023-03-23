package cn.jingzhuan.lib.chart2.base;

import android.view.MotionEvent;

import cn.jingzhuan.lib.chart.component.Highlight;

public interface IAbstractChart {

    void initChart();

    Highlight[] getHighlights();

    void highlightValue(Highlight highlight);

    void cleanHighlight();

    void onTouchPoint(MotionEvent e);

    void onTouchHighlight(MotionEvent e);

    int getEntryIndexByCoordinate(float x, float y);

}
