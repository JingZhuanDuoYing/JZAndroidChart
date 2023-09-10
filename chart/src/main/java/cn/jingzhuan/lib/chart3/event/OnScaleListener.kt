package cn.jingzhuan.lib.chart3.event

import cn.jingzhuan.lib.chart3.Viewport


/**
 * @since 2023-09-10
 * created by lei
 * 缩放监听
 */
interface OnScaleListener {
    fun onScaleStart(viewport: Viewport)
    fun onScale(viewport: Viewport)
    fun onScaleEnd(viewport: Viewport)
}