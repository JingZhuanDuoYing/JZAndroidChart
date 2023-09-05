package cn.jingzhuan.lib.chart3.formatter

/**
 * @since 2023-09-05
 * Value格式化接口
 * Created by lei
 */
interface IValueIndexFormatter {
    /**
     * 格式化value
     *
     * @param index 传入index
     * @return 返回字符串
     */
    fun format(index: Int): String?
}