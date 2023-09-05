package cn.jingzhuan.lib.chart3.state

/**
 * @since 2023-09-05
 * created by lei
 * 十字光标状态
 */
enum class HighlightState {
    /**
     * 初始状态
     */
    Initial,

    /**
     * 按下
     */
    Press,

    /**
     * 移动
     */
    Move,

    /**
     * 长存
     */
    Forever
}