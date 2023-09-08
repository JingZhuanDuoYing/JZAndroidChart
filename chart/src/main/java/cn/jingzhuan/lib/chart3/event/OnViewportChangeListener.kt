package cn.jingzhuan.lib.chart3.event

import cn.jingzhuan.lib.chart3.Viewport

/**
 * @since 2023-09-08
 * @author lei
 * viewport变化监听
 */
fun interface OnViewportChangeListener {

    fun onViewportChange(viewport: Viewport)

}