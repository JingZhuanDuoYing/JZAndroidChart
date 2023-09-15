package cn.jingzhuan.lib.chart3.event

/**
 * @since 2023-09-15
 * @author lei
 * 区间统计 起始位置变化
 */
interface OnRangeChangeListener {
    /**
     * 区间统计坐标
     * @param startX 开始的X坐标
     * @param endX   结束的X坐标
     * @param touchType 触摸类型
     */
    fun onRange(startX: Float, endX: Float, touchType: Int)
}