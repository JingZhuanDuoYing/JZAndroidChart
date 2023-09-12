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

}