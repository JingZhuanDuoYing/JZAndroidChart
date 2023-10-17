package cn.jingzhuan.lib.chart3.event

import android.view.MotionEvent

/**
 * @since 2023-10-17
 * created by lei
 * 画线交互监听
 */
interface OnDrawLineTouchListener {

    fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float)
}