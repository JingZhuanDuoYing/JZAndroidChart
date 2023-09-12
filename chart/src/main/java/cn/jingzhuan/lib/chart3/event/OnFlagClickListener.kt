package cn.jingzhuan.lib.chart3.event

/**
 * @since 2023-09-12
 * created by lei
 * 标签点击回调
 */
interface OnFlagClickListener {

    /**
     * [type] - 标签类型
     * [index] - 光标下标
     */
    fun onClick(type: Int, index: Int)

}