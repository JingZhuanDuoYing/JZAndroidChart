package cn.jingzhuan.lib.chart3.event

import android.graphics.PointF
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.drawline.DrawLineState

/**
 * @since 2023-10-09
 * created by lei
 * 画线监听
 */
interface OnDrawLineListener {

    fun onTouch(state: DrawLineState, point: PointF, type: Int)

    fun onComplete(point1: PointF, point2: PointF, type: Int, dataSet: DrawLineDataSet)
}