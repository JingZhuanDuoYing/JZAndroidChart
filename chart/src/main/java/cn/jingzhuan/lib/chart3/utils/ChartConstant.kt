package cn.jingzhuan.lib.chart3.utils

import android.graphics.Color

/**
 * @since 2023-09-05
 */
object ChartConstant {

    const val COLOR_NONE = Color.TRANSPARENT

    const val SHAPE_ALIGN_CENTER = 1

    const val SHAPE_ALIGN_TOP = 2

    const val SHAPE_ALIGN_BOTTOM = 3

    const val SHAPE_ALIGN_PARENT_TOP = 4

    const val SHAPE_ALIGN_PARENT_BOTTOM = 5

    // <editor-fold desc="光标 状态">    ---------------------
    /**
     * 初始状态
     */
    const val HIGHLIGHT_STATUS_INITIAL = 0

    /**
     * 按下状态
     */
    const val HIGHLIGHT_STATUS_PRESS = 1

    /**
     * 移动状态
     */
    const val HIGHLIGHT_STATUS_MOVE = 2

    /**
     * 长存状态
     */
    const val HIGHLIGHT_STATUS_FOREVER = 3

    // </editor-fold desc="光标 状态">    --------------------
}