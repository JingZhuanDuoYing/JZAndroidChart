package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.PointF
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-09
 * created by lei
 */
interface IDrawLine {

    fun onDraw(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float)

//    fun onTouch(state: DrawLineState, point: PointF, dataSet: DrawLineDataSet)

    fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet)
}