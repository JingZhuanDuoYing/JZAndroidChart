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

    // <editor-fold desc="光标 标签组">    ---------------------

    const val FLAG_TAG_NAME = "flag_tag_name"

    /**
     * 交易详情
     */
    const val FLAG_TRADE_DETAIL = 0

    /**
     * 交易详情(模)
     */
    const val FLAG_SIMULATE_TRADE_DETAIL = 1

    /**
     * 涨停分析
     */
    const val FLAG_LIMIT_UP = 2

    /**
     * 公告
     */
    const val FLAG_NOTICE = 3

    /**
     * 历史分时
     */
    const val FLAG_HISTORY_MINUTE = 4

    /**
     * 龙虎榜
     */
    const val FLAG_LHB = 5

    // </editor-fold desc="光标 标签组">    --------------------
}