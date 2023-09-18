package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet


/**
 * @since 2023-09-06
 * @author lei
 */
interface IDraw<T : AbstractDataSet<*>> {

    fun drawDataSet(canvas: Canvas, chartData: ChartData<T>, dataSet: T, viewport: Viewport)

    fun drawDataSet(canvas: Canvas, chartData: ChartData<T>, viewport: Viewport){
        synchronized (chartData) {
            for (dataSet in chartData.dataSets) {
                drawDataSet(
                    canvas,
                    chartData,
                    dataSet,
                    viewport
                )
            }
        }
    }

}