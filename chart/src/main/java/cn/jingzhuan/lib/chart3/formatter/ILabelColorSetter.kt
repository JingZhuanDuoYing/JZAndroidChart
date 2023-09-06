package cn.jingzhuan.lib.chart3.formatter

import androidx.annotation.ColorInt

/**
 * @since 2023-09-06
 * 坐标轴文本颜色设置器接口
 */
interface ILabelColorSetter {

    @ColorInt
    fun getColorByIndex(position: Int): Int
}