package cn.jingzhuan.lib.chart3.event

import cn.jingzhuan.lib.chart3.Highlight

/**
 * @since 2023-09-13
 * created by lei
 * 光标变化监听
 */
interface OnHighlightListener {

    fun onHighlightShow(highlight: Highlight?)

    fun onHighlightHide()
}