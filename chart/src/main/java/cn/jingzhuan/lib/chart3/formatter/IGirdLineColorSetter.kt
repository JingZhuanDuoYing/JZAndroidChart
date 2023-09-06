package cn.jingzhuan.lib.chart3.formatter

import androidx.annotation.ColorInt

/**
 * @since 2023-09-06
 * 网格线颜色 设置器接口
 */
interface IGirdLineColorSetter {

    @ColorInt
    fun getColorByIndex(color: Int, position: Int): Int
}