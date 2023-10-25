package cn.jingzhuan.lib.chart3.event

import android.graphics.PointF
import cn.jingzhuan.lib.chart3.drawline.DrawLineState

/**
 * @since 2023-10-09
 * created by lei
 * 画线监听
 */
interface OnDrawLineListener {

    fun onTouch(state: DrawLineState, lineKey: String, type: Int, reselected: Boolean = false)

    fun onDrag(bitmapPoint: PointF, translatePoint: PointF, state: Int)
}