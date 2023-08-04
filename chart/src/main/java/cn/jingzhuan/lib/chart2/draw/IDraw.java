package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Canvas;

import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Value;

public interface IDraw<E extends Value, T extends AbstractDataSet<E>> {

    void drawDataSet(Canvas canvas, ChartData<T> chartData, T dataSet);

}
