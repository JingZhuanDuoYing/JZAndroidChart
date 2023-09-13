package cn.jingzhuan.lib.chart2.demo.chart3.chart

import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.utils.NumberUtils

class SubKlineLeftValueFormatter(private val decimalDigitsNumber: Int) : IValueFormatter {

    override fun format(value: Float, index: Int): String {
        return if (index == 1 || value.isNaN() || value > Int.MAX_VALUE || value < Int.MIN_VALUE) ""
        else NumberUtils.keepPrecision("$value", decimalDigitsNumber)
    }
}