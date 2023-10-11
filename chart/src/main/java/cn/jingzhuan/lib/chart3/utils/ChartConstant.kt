package cn.jingzhuan.lib.chart3.utils

import android.graphics.Color

/**
 * @since 2023-09-05
 */
object ChartConstant {

    const val COLOR_NONE = Color.TRANSPARENT

    var COLOR_RED = 0xFFFD263F.toInt()

    var COLOR_GREEN = 0xFF00AA3B.toInt()

    const val SHAPE_ALIGN_CENTER = 1

    const val SHAPE_ALIGN_TOP = 2

    const val SHAPE_ALIGN_BOTTOM = 3

    const val SHAPE_ALIGN_PARENT_TOP = 4

    const val SHAPE_ALIGN_PARENT_BOTTOM = 5

    const val SCATTER_TEXT_ALIGN_TOP = 1

    const val SCATTER_TEXT_ALIGN_BOTTOM = 2

    // <editor-fold desc="光标 状态">    -------------------
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

    // <editor-fold desc="光标 标签组">    -------------------

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

    // </editor-fold desc="光标 标签组">    ------------------

    const val TYPE_AXIS_LEFT = 10

    const val TYPE_AXIS_TOP = 11

    const val TYPE_AXIS_RIGHT = 12

    const val TYPE_AXIS_BOTTOM = 13

    /**
     * The scaling factor for a single zoom 'step'.
     *
     * @see .zoomIn
     * @see .zoomOut
     */
    const val ZOOM_AMOUNT = 0.15f

    // <editor-fold desc="区间统计触摸状态">    -------------------

    const val RANGE_TOUCH_NONE = 0

    const val RANGE_TOUCH_LEFT = 1

    const val RANGE_TOUCH_RIGHT = 2

    const val RANGE_TOUCH_BOTH = 3

    // </editor-fold desc="区间统计触摸状态">    ------------------

    // <editor-fold desc="画线工具拖动状态">    -------------------

    const val DRAW_LINE_NONE = 0

    const val DRAW_LINE_DRAG_LEFT = 1

    const val DRAW_LINE_DRAG_RIGHT = 2

    const val DRAW_LINE_DRAG_BOTH = 3

    // </editor-fold desc="画线工具拖动状态">    ------------------

}