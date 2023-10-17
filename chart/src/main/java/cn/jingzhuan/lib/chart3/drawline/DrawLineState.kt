package cn.jingzhuan.lib.chart3.drawline


/**
 * @since 2023-10-09
 * @author lei
 * 画线状态
 */
enum class DrawLineState {
    /**
     * 预备
     */
    prepare,

    /**
     * 第一步
     */
    first,

    /**
     * 第二步
     */
    second,

    /**
     * 完成
     */
    complete
}