package cn.jingzhuan.lib.chart3.formatter

/**
 * @since 2023-09-05
 * Value格式化接口
 * Created by lei
 */
interface IValueFormatter {
    /**
     * 格式化value
     *
     * @param value 传入的value值
     * @return 返回字符串
     */
    fun format(value: Float, index: Int): String?
}