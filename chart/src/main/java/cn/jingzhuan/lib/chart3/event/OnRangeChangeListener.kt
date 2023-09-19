package cn.jingzhuan.lib.chart3.event

/**
 * @since 2023-09-15
 * @author lei
 * 区间统计 起始位置变化
 */
interface OnRangeChangeListener {
    /**
     * 区间统计坐标
     * @param startIndex 开始下标
     * @param endIndex   结束下坐标
     * @param touchType 触摸类型
     */
    fun onRange(startIndex: Int, endIndex: Int, touchType: Int)

    /**
     * 关闭区间统计
     */
    fun onClose()
}